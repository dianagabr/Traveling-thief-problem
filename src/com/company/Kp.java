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
    static final int c = 5; //constant used for packIterative
    static final double delta = 2.5;
    static final int q = 10;



    /**
     *
     * @return the total profit of the picking plan
     */
    public double getTotalProfit(){
        double profit = 0;

        for(int i = 0; i< m ; i++){
            profit += pickingPlan[i] * profits[i];
        }

        return profit;
    }

    /**
     *
     * @param tour
     * @param city
     * @return the distance from city where an item k was selected to the end of the given tour
     */
    public static int calculate_distance(int[] tour, int city){

        int distance = 0;
        int index = Tsp.get_index_city(tour, city);

        for(int i = index; i < tour.length -1 ; i++){
            distance += Tsp.instance.distance[tour[i]][tour[i+1]];
        }
        return distance;
    }

    /**
     *
     * @param tour
     * @param alpha
     * @return the score for each object according to the formula using in PACKITERATIVE
     */
    public static List<ScoreItem> compute_score(int[] tour, double alpha){

        List<ScoreItem> scoreList = new ArrayList<>();
        int i;

        for( i = 0 ; i< m; i++){
            if(availability[i] != -1){
                int distance = calculate_distance(tour,availability[i]);
                double score  =(double) (Math.pow(profits[i], alpha))/(Math.pow(weights[i], alpha) * distance );
                scoreList.add(new ScoreItem(availability[i],i,score));
            }

        }


        return scoreList;

    }

    public static int getCurrentWeightInPickingPlan(int[] pickingPlan, int k , List<ScoreItem> score){

        int currentWeight = 0 ;
        for(int i = 0; i < k; i++){
            int item = score.get(i).getItem();
            currentWeight += pickingPlan[item] * weights[item];
        }

        return currentWeight;
    }


    public static double getTotalProfit(int[] pickingPlan, int[] profits, int m)
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

    public static int getTotalWeight(int[] pickingPlan)
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


    static boolean check_if_empty(int[] pickingPlan){

        for(int i = 0 ; i< pickingPlan.length; i++){
            if(pickingPlan[i] != 0){
                return false;
            }
        }
        return true;
    }

    public static int[] pack(int[] tour, double alpha) {
        List<ScoreItem> score = compute_score(tour, alpha); //score list for each item
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
                        currentWeight = getCurrentWeightInPickingPlan(packingPlan, k, score);
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

    public static int[] packIterative(int[] tour, int c, int q, double delta){

        double epsilon = 0.1;
        int[] pickingPlanLeft = pack(tour, c-delta);
        double objValueLeft = Ttp.calculateObjectiveValue(tour, pickingPlanLeft);


        int[] pickingPlanMid = pack(tour, c);
        double objValueMid = Ttp.calculateObjectiveValue(tour, pickingPlanMid);


        int[] pickingPlanRight = pack(tour, c + delta);
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

              pickingPlanLeft = pack(tour, c- delta);
              objValueLeft = Ttp.calculateObjectiveValue(tour, pickingPlanLeft);

              pickingPlanRight = pack(tour, c + delta);
              objValueRight = Ttp.calculateObjectiveValue(tour, pickingPlanRight);
              i++;

              if(objValueLeft - objValueMid < epsilon && objValueRight - objValueMid < epsilon){
                  break;
              }

        }
        return bestPickingPlan;
    }
}

