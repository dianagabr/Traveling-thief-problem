package com.company;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Knapsack instance for Traveling Thief Problem
 */

public class Kp {
    static int m; //number of items
    static int[]  weights; //weight for each item
    static int[] profits; //profit for each item
    static int W; // knapsack total weight
    static int[] pickingPlan; //contains the solution for the knapsack sub-problem
    static int[] availability ; // availability of each item]
    static final int c = 5; //constant used for pack_iterative
    static final double delta = 2.5; //constant used for pack_iterative
    static final int q = 10; // constant used for pack_iterative




    public double get_total_profit()
    /**
     * return the total profit of the picking plan
     */
    {
        double profit = 0;

        for(int i = 0; i< m ; i++){
            profit += pickingPlan[i] * profits[i];
        }

        return profit;
    }


    public static int calculate_distance(int[] tour, int city)
    /**
     * return the distance from city where an item k was selected to the end of the given tour
     */
    {

        int distance = 0;
        int index = Tsp.get_index_city(tour, city);

        for(int i = index; i < tour.length -1 ; i++){
            distance += Tsp.instance.distance[tour[i]][tour[i+1]];
        }
        return distance;
    }

    /**
     * return the score for each object according to the formula using in PACKITERATIVE
     */
    public static List<ScoreItem> compute_score(int[] tour, double alpha, double beta){

        List<ScoreItem> scoreList = new ArrayList<>();
        int i;

        for( i = 0 ; i< m; i++){
            if(availability[i] != -1){
                int distance = calculate_distance(tour,availability[i]);
                double score  =(double) (Math.pow(profits[i], alpha))/(Math.pow(weights[i], beta) * distance );
                scoreList.add(new ScoreItem(availability[i],i,score));
            }

        }


        return scoreList;

    }

    public static int get_current_weight_in_picking_plan(int[] pickingPlan, int k , List<ScoreItem> score)
    /***
     * obtain the total weight of the constructed picking plan
      */
    {

        int currentWeight = 0 ;
        for(int i = 0; i < k; i++){
            int item = score.get(i).getItem();
            currentWeight += pickingPlan[item] * weights[item];
        }

        return currentWeight;
    }


    public static double get_total_profit(int[] pickingPlan, int[] profits, int m)
    /**
     * compute total profit of a given picking plan
     */
    {
        double profit = 0;

        for(int i = 0; i< m ; i++){
            profit += pickingPlan[i] * profits[i];
        }

        return profit;
    }

    public static int get_total_weight(int[] pickingPlan)
    /**
     * compute total weight of a given picking plan
     */
    {

        int weight = 0;
        for(int i = 0; i< m; i++){
            weight += pickingPlan[i] * weights[i];
        }

        return weight;
    }


    static boolean check_if_empty(int[] pickingPlan)
    /***
     * verify if a picking plan is empty
     */
    {

        for(int i = 0 ; i< pickingPlan.length; i++){
            if(pickingPlan[i] != 0){
                return false;
            }
        }
        return true;
    }

    public static int[] pack(int[] tour, double alpha, double beta)
    /***
     *  algorithm used for constructing packing plan
     */
    {
        List<ScoreItem> score = compute_score(tour, alpha, beta); //score list for each item
        score.sort(Comparator.comparing(ScoreItem::getScore).reversed()); //sort the items in non-decreasing order of their score
        int frequency = 3; //(int) Math.floor(m/alpha); //  controls the frequency of the objective value recomputation
        int[] packingPlan  = new int[m];

        double currentWeight = 0;
        int[] bestPackingPlan  = new int[m];
        double bestObjectiveValue = Integer.MIN_VALUE;
        double objectiveValue=  0;
        int k = 0;
        int k_best = 0;
        while(currentWeight < W && frequency > 1 && k < score.size()) {
            if(currentWeight + weights[score.get(k).getItem()]<= W){
                packingPlan[score.get(k).getItem()] = 1;
                currentWeight += weights[score.get(k).getItem()];
                if(k % frequency == 0){
                    objectiveValue = Ttp.calculateObjectiveValue(tour,packingPlan);
                    if(objectiveValue < bestObjectiveValue ){
                        packingPlan = bestPackingPlan.clone();
                        k = k_best;
                        frequency = frequency /2 ;
                        currentWeight = get_current_weight_in_picking_plan(packingPlan, k, score);
                    }
                    else{
                        bestPackingPlan = packingPlan.clone();
                        k_best = k;
                        bestObjectiveValue = objectiveValue;
                    }
                }
            }

            k++;
        }
        score.clear();
        return packingPlan;
    }

    public static int[] pack_evolutiv(int[] tour)
    /***
     * method proposed for solving the packing routine
     * this method uses Differential Evolution to calculate the exponents
     * alpha and beta for PACK
     * return the packing plan for the given tour
     */
    {
        DifferentialEvolution differentialEvolution = new DifferentialEvolution(tour);
        double[] values = differentialEvolution.run();
        double alpha = values[0];
        double beta = values[1];
        int[] picking_plan = pack(tour, alpha, beta);
        return picking_plan;
    }

    public static int[] pack_iterative(int[] tour, int c, int q, double delta)
    /***
     * method proposed in article for solving the picking plan
      */
    {

        double epsilon = 0.1;
        int[] pickingPlanLeft = pack(tour, c-delta,c + delta );
        double objValueLeft = Ttp.calculateObjectiveValue(tour, pickingPlanLeft);


        int[] pickingPlanMid = pack(tour, c, c);
        double objValueMid = Ttp.calculateObjectiveValue(tour, pickingPlanMid);


        int[] pickingPlanRight = pack(tour, c + delta,c + delta);
        double objValueRight = Ttp.calculateObjectiveValue(tour, pickingPlanRight);



        int[] bestPickingPlan = pickingPlanMid.clone();
        int i = 0;
        while( i <= q) {
              if(objValueLeft > objValueMid && objValueRight > objValueMid){
                  if(objValueLeft > objValueRight){
                      objValueMid = objValueLeft;
                      c = (int) (c- delta);
                      bestPickingPlan = pickingPlanLeft.clone();
                  }else{
                      objValueMid = objValueRight;
                      c = (int) (c + delta);
                      bestPickingPlan = pickingPlanRight.clone();
                  }
            } else if(objValueLeft > objValueMid ){
                  objValueMid = objValueLeft;
                  c = (int) (c- delta);
                  bestPickingPlan = pickingPlanLeft.clone();
              }
              else if(objValueRight > objValueMid){
                  objValueMid = objValueRight;
                  c = (int) (c + delta);
                  bestPickingPlan = pickingPlanRight.clone();
              }
              delta = delta/2;

              pickingPlanLeft = pack(tour, c- delta, c-delta);
              objValueLeft = Ttp.calculateObjectiveValue(tour, pickingPlanLeft);

              pickingPlanRight = pack(tour, c + delta,c + delta);
              objValueRight = Ttp.calculateObjectiveValue(tour, pickingPlanRight);
              i++;

              if(objValueLeft - objValueMid < epsilon && objValueRight - objValueMid < epsilon){
                  break;
              }

        }
          return bestPickingPlan;
    }
}

