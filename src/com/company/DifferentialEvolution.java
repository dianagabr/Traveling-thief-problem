package com.company;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/***
 * Implementation of Differential Evolution algorithm
 */
public class DifferentialEvolution {

    static List<Chromosome> population = new ArrayList<>() ;
    static int POPULATION_SIZE = 20;
    static double CROSSOVER_PROBABILITY = 0.4;
    static double MUTATION_FACTOR = 1.2d;
    static int ITERATION_NUMBER = 10;
    static int[] tour;

    public DifferentialEvolution(int[] tour){
        this.tour = tour.clone();
    }


    Random random = new Random();

    List<double[]> getDimensions()
    /***
     *  get lower and upper bound for each parameter
     */
    {
        double[] alpha_bounds = new double[2];
        alpha_bounds[0] = Kp.c -  Kp.delta;
        alpha_bounds[1] = Kp.c + Kp.delta;
        double[] beta_bounds = new double[2];
        beta_bounds[0] = Kp.c -  Kp.delta;
        beta_bounds[1] = Kp.c + Kp.delta;
        List<double[]> dimensions = new ArrayList<>();
        dimensions.add(alpha_bounds);
        dimensions.add(beta_bounds);
        return dimensions;
    }

     void init_population(List<double[]> dimensions )
     /***
      * initialize population
       */
     {

        for (int i=0; i< POPULATION_SIZE;i++){
            Chromosome chromosome = new Chromosome(dimensions);
            population.add(chromosome);
        }
    }

    List<Chromosome> generate_agents(int index)

    /***
     * get three random agents from population to use them in mutation
     */
    {

         List<Chromosome> agents = new ArrayList<>();
         int index_a, index_b, index_c = -1;
         do{
             index_a = random.nextInt(population.size());
         }while (index == index_a);
         do{
             index_b = random.nextInt(population.size());
         }while(index_b == index || index_b == index_a);
         do{
             index_c = random.nextInt(population.size());
         }while(index_c == index || index_c == index_b || index_c == index_a);

         agents.add(population.get(index_a));
         agents.add(population.get(index_b));
         agents.add(population.get(index_c));
         return agents;
    }

    public double[] run()
    /***
     * implementation of differential evolution
      */
    {
        int size = population.size();
        List<double[]> dimensions = getDimensions();
        //initialize population
        init_population(dimensions);
        for(int i = 0; i < ITERATION_NUMBER ; i++){
            int chromosome_index = 0;
            while (chromosome_index < size) {
                Chromosome originalChromosome = null;
                Chromosome candidateChromosome = null;
                boolean respectBounds;
                do {
                    int index = chromosome_index;
                    respectBounds = true;
                    List<Chromosome> agents = generate_agents(index);
                    Chromosome agent1 = agents.get(0);
                    Chromosome agent2 = agents.get(1);
                    Chromosome agent3 = agents.get(2);

                    Chromosome donorCandidate = new Chromosome(dimensions);

                    //mutation process
                    for (int l = 0; l < 2; l++) {
                        donorCandidate.values[l] = (agent1.values[l] + MUTATION_FACTOR * (agent2.values[l] - agent3.values[l]));
                    }

                    //create trial candidate
                    originalChromosome = population.get(index);
                    candidateChromosome = new Chromosome(dimensions);
                    for (int l = 0; l < 2; l++) {
                        candidateChromosome.values[l] = originalChromosome.values[l];
                    }

                    //crossover process
                    int I = random.nextInt(2);

                    for (int l = 0; l < 2; l++) {

                        double crossoverProb = random.nextDouble();
                        if (crossoverProb < CROSSOVER_PROBABILITY || l == I) {
                            candidateChromosome.values[l] = donorCandidate.values[l];
                        }
                    }

                    //check if trial candidate is valid
                    for (int l = 0; l < 2; l++) {
                        if (candidateChromosome.values[l] < dimensions.get(l)[0] || candidateChromosome.values[l] > dimensions.get(l)[1]) {
                            respectBounds = false;
                        }
                    }

                } while (respectBounds == false);

                int[] picking_plan_org = Kp.pack(tour, originalChromosome.values[0], originalChromosome.values[1]);
                int[] picking_plan_cand = Kp.pack(tour, candidateChromosome.values[0], candidateChromosome.values[1]);
                double obj_value_org = Ttp.calculateObjectiveValue(tour, picking_plan_org);
                double obj_value_candidate = Ttp.calculateObjectiveValue(tour, picking_plan_cand);
                if (obj_value_org < obj_value_candidate) {
                    population.remove(originalChromosome);
                    population.add(candidateChromosome);
                }
                chromosome_index++;
            }
    }

        Chromosome bestFitness = new Chromosome(dimensions);
        for(int i = 0; i < population.size(); i++){
            Chromosome chromosome = population.get(i);
            int[] picking_plan_ch = Kp.pack(tour, chromosome.values[0], chromosome.values[1]);
            int[] picking_plan_best = Kp.pack(tour, bestFitness.values[0], bestFitness.values[1]);
            if ( Ttp.calculateObjectiveValue(tour, picking_plan_best) < Ttp.calculateObjectiveValue(tour, picking_plan_ch )){
                try {
                    bestFitness = (Chromosome) chromosome.clone();
                } catch (CloneNotSupportedException ex) {

                }
            }

        }

        double[] values = new double[2];
        values[0] = bestFitness.values[0];
        values[1] = bestFitness.values[1];
        return values;
    }

    public class Chromosome implements Cloneable{

        double[] values;

        public Chromosome( List<double[]> dimensions) {
            values = new double[dimensions.size()];
            for(int i= 0; i< dimensions.size(); i++){
                 double lowerBound = dimensions.get(i)[0];
                 double upperBound = dimensions.get(i)[1];

                 values[i] = lowerBound + (upperBound - lowerBound) * random.nextDouble();
             }
        }



        @Override
        protected Object clone() throws CloneNotSupportedException {
            return super.clone();
        }
    }


}

