package com.company;

import com.company.Kp;
import com.company.Tsp;

import java.util.HashMap;
import java.util.Map;

/**
 * TTP instance which contains a TSP instance and KP instance
 * Also contains the availability of each item
 */
public class Ttp {


    public static  String problemName;
    public static String knapsackDataType ;

    public static double RATE;
    public static double MINIMUM_SPEED;
    public static double MAXIMUM_SPEED;
    public static double SPEED;
    public static boolean BOOST;
    public static Map<int[], Double> tourObjValue = new HashMap<>();
    public Ttp(){}



    public static int get_weight_in_city(int[] picking_plan, int city){

        int index;

        int currentWeight = 0;


        for(int i = 0 ; i < picking_plan.length; i++){
            if(picking_plan[i] == 1 && Kp.availability[i] == city){
                currentWeight += Kp.weights[i];
            }
        }
        return currentWeight;
    }

    public static double calculateObjectiveValue(int[] tour, int[] pickingPlan)
    /**
     * calculate objective value
     */
    {
        double objectiveValue = Kp.getTotalProfit(pickingPlan, Kp.profits, Kp.m );
        int currentWeight = 0;
        int first_city_weight = 0;
        double sum = 0;
        int i ;
        for(i= 0; i< Tsp.n ; i++){
            int weight = get_weight_in_city(pickingPlan, tour[i]) ;
            if (i == 0 && weight != 0) {
                first_city_weight = weight;
                weight = 0;
            }
            currentWeight += weight;
            sum += Tsp.instance.distance[tour[i]][tour[i+1] ]/ (Ttp.MAXIMUM_SPEED - Ttp.SPEED * currentWeight);
        }
        sum += first_city_weight;
        objectiveValue = objectiveValue - Ttp.RATE* sum;
        return objectiveValue;
    }

    public void compute_speed(){

        SPEED = (MAXIMUM_SPEED-MINIMUM_SPEED)/Kp.W;

    }


    static void compute_score(int[] tour, int[] pickingPlan){

        tourObjValue.putIfAbsent(tour, calculateObjectiveValue(tour,pickingPlan));
    }




}
