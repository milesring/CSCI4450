import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class ring_A2_Q1 {

/*

Written by Miles Ring on 9/25/2017 for CSCI4450 Intro to AI

Problem:
In class, we discussed about the 8 queens problem, where the objective is to place 8 queens on a chess board so that no two queens can attack each other.
A solution corresponding to a goal state is shown in the picture below.

In this question, your objective is to compare the performance of different crossover operators.
The cross over operators you need to program are the following: single point crossover, two point crossover, cut and splice, uniform crossover.
More information about these crossover operators are given on the slides.
Feel free to search on the Internet for references and implementation ideas for these operators

You should use the same representation of a chromosome, fitness function, etc. from the class slides/textbook.
Your program should first prompt the user for the following input (just input values on a single line, separated by blank space):
    -Initial population size
    -Number of chromosomes to select at each iteration or generation of the GA
    -Mutation rate
    -Number of iterations or generations to run the GA for; -1 to run the GA until a goal state is found.

Other parameters can be hardcoded.
Then, it should prompt the user to choose one of the crossover operators from a menu.
Add an option to exit the program from this menu.
After a crossover operator is selected, the program runs.
At the end of the run, your program should print (each output on a separate line):
    -Values of input and hardcoded parameters used (on a single line, comma-separated, format:- <parameter name: value>)
    -Number of iterations or generations of the GA executed
    -Final state reached
    -Final state’s fitness function value.
After printing the output, the program should go back to the first menu prompt for another the crossover operator or exit.
*/
Scanner in = new Scanner(System.in);
Random rand = new Random();
double bestF = 0.0;
String bestS = "";

    public ring_A2_Q1(){
        int populationSize;
        int numChromosomes;
        double mutationRate;
        double fitLimit;
        int numIterations;
        int iterationsRan;
        int crossOver;

        ArrayList<String> population = new ArrayList<>();

        while(true) {
            System.out.println("CSCI 4450 Assignment 2 Question 1");
            System.out.println("*********************************");
            System.out.println("Enter the following on a single line, separated by a single space:");
            System.out.println("Initial population size, # of chromosomes to select after each iteration or generation of the GA,");
            System.out.println("mutation rate, # of iterations or generations to run the GA for: -1 to run the GA until goal state is found.");
            populationSize = in.nextInt();
            numChromosomes = in.nextInt();
            mutationRate = in.nextDouble();
            numIterations = in.nextInt();
            fitLimit = 0.35;
            iterationsRan = 0;

            population = generatePopulation(population, populationSize);

            //population.add("24748552");
            //population.add("32752411");
            //population.add("24415124");
            //population.add("32543213");

            if (numIterations == -1) {
                numIterations = Integer.MAX_VALUE;
            }

            System.out.println("********************************");
            System.out.println("* Select a crossover operator: *");
            System.out.println("*                              *");
            System.out.println("* 1. Single Point              *");
            System.out.println("* 2. Two Point                 *");
            System.out.println("* 3. Cut and Splice            *");
            System.out.println("* 4. Uniform                   *");
            System.out.println("*                              *");
            System.out.println("* 5. Exit                      *");
            System.out.println("********************************");

            crossOver = in.nextInt();

            if (crossOver == 5) {
                System.exit(0);
            }


            for (int i = 0; i < numIterations; ++i) {
                population = GeneticAlgorithm(population, numChromosomes, mutationRate, crossOver);
                findBest(population);
                if (numIterations == Integer.MAX_VALUE) {
                    if (bestF > fitLimit) {
                        iterationsRan = i;
                        break;
                    }
                }
                iterationsRan++;
            }
            if (numIterations == Integer.MAX_VALUE) {
                numIterations = -1;
            }
            System.out.printf("Input values: <initPopSize: %d> <numChromosomes: %d> <mutationRate: %f> <numIterations: %d> <fLimit: %f>\n",
                    populationSize, numChromosomes, mutationRate, numIterations, fitLimit);
            System.out.println("Number of iterations ran: " + iterationsRan);
            System.out.println("Final state: " + bestS);
            System.out.println("Final state fValue: " + bestF);
            System.out.println("\n");
        }
    }

    //populates initial population with random strings
    public ArrayList generatePopulation(ArrayList pop, int populationSize){
        for(int i=0;i<populationSize;++i){
            String temp = "";
            for(int j=0;j<8;++j){
                temp += Integer.toString(rand.nextInt(8-1+1)+1);
            }
            pop.add(temp);
        }
        return pop;
    }

    //finds best fvalue
    public void findBest(ArrayList<String> pop){
        double best = 0.0f;
        int index = 0;
        int[] rawValues = new int[pop.size()];
        double[] fValues = new double[pop.size()];
        int totalVal = 0;

        //calculate unattacking queen pairs
        for(int i=0;i<pop.size();++i){
            rawValues[i] = FitnessFn(pop.get(i));
            totalVal+=rawValues[i];

        }

        //calc fitness values
        for(int i=0;i<pop.size();++i){
            fValues[i] = (double)rawValues[i]/(double)totalVal;
        }

        for(int i=0;i<pop.size();++i){
            if(fValues[i] > best){
                best = fValues[i];
                index = i;
            }

        }
        if(best > bestF) {
            bestF = best;
            bestS = pop.get(index);
        }


    }
/*
function GENETIC-ALGORITHM(population, FITNESS-FN) returns an individual
    inputs: population, a set of individuals
            FITNESS-FN, a function that measures the fitness of an individual

    repeat
      new_population <- empty set
      for i = 1 to SIZE(population) do
        x <- RANDOM-SELECTION(population, FITNESS-FN)
        y <- RANDOM-SELECTION(population, FITNESS-FN)
        child <- REPRODUCE(x, y)
        if (small random probability) then child <- MUTATE(child)
        add child to new_population
      population <- new_population
    until some individual is fit enough, or enough time has elapsed
    return the best individual in population, according to FITNESS-FN
*/
    public ArrayList GeneticAlgorithm(ArrayList pop, int numChromosomes, double mutationRate, int crossOver){
        ArrayList<String> newPop = new ArrayList<>();

            for(int j=1;j<=pop.size();++j){
                for(int i=0;i<numChromosomes/2;i++) {
                    String x = RandomSelection(pop);
                    String y = RandomSelection(pop);
                    while(true){
                        if(x == y){
                            y = RandomSelection(pop);
                        }
                        else{
                            break;
                        }
                    }
                    String child = Reproduce(x, y, crossOver);

                    if (rand.nextDouble() < mutationRate) {
                        child = Mutate(child);
                    }
                    newPop.add(child);
                }
            }
        return newPop;
    }

    public String RandomSelection(ArrayList<String> pop){
            String select = pop.get(pop.size()-1);
            int[] rawValues = new int[pop.size()];
            double[] fValues = new double[pop.size()];
            int totalVal = 0;

            //calculate unattacking queen pairs
            //System.out.println("Calculating unattacking queens...");
            for(int i=0;i<pop.size();++i){
                rawValues[i] = FitnessFn(pop.get(i));
                //System.out.print(rawValues[i]+" ");
                totalVal+=rawValues[i];
            }
            //System.out.println();
            double totalFit = 0.0;
            //calc fitness values
            //System.out.println("Calculating fitness values...");
            for(int i=0;i<pop.size();++i){
                fValues[i] = (double)rawValues[i]/(double)totalVal;
                totalFit += fValues[i];
                //System.out.printf(fValues[i]+" ");
            }
            //System.out.println();
            double prob = rand.nextDouble()*totalFit;
            for(int i=0;i<pop.size();++i){
                prob -= fValues[i];
                if(prob<=0){
                    select = pop.get(i);
                    //System.out.println("Selected: "+select);
                    break;
                }
            }
        return select;
    }

    public String Reproduce(String x, String y, int crossOver){
        String child;

        switch(crossOver){
            case 1:
                child = singlePoint(x, y);
                break;
            case 2:
                child = twoPoint(x, y);
                break;
            case 3:
                child = cutAndSplice(x, y);
                break;
            case 4:
                child = uniformCrossover(x, y);
                break;
            default:
                child = "";
                break;
        }
        return child;
    }

    public String singlePoint(String x, String y){
        String child;
        int randomSplit = rand.nextInt(7+1);
        child = x.substring(0,randomSplit)+y.substring(randomSplit, y.length());
        //System.out.println("x: "+x+ " and "+y+" have produced...");
        //System.out.println(child);
        return child;
    }

    public String twoPoint(String x, String y){
        String child;
        int randomSplit1 = rand.nextInt(7+1);
        int randomSplit2 = rand.nextInt(7+1);

        while(true){
            if(randomSplit1 == randomSplit2){
                randomSplit2 = rand.nextInt(7+1);
                break;
            }else{
                break;
            }
        }

        if(randomSplit1 > randomSplit2){
            int temp = randomSplit1;
            randomSplit1 = randomSplit2;
            randomSplit2 = temp;
        }

        child = x.substring(0, randomSplit1)+y.substring(randomSplit1, randomSplit2)+x.substring(randomSplit2, x.length());

        return child;
    }

    public String cutAndSplice(String x, String y){
        String child;
        int randomSplit1 = rand.nextInt(7+1);
        int randomSplit2 = rand.nextInt(7+1);

        child = x.substring(0, randomSplit1)+y.substring(randomSplit2, y.length());
        if(child.length()>8){
            child.substring(0, 8);
        }else if(child.length()<8){
            for(int i=child.length();i<8;++i){
                child+=rand.nextInt(7+1)+1;
            }
        }

        return child;
    }

    public String uniformCrossover(String x, String y){
        String child="";

        double prob = rand.nextDouble();
        for(int i=0;i<x.length();++i){
            if(rand.nextDouble()<prob){
                child+=x.charAt(i);
            }else{
                child+=y.charAt(i);
            }

        }

        return child;
    }

    public String Mutate(String child){
        String newChild;
        char[] childArr = child.toCharArray();
        int newVal = rand.nextInt(7+1)+1;
        int index = rand.nextInt(7+1);
        while(true) {
            if (Character.getNumericValue(childArr[index]) == newVal) {
                newVal = rand.nextInt(7 + 1)+1;
            } else {
                childArr[index] = Integer.toString(newVal).charAt(0);
                break;
            }
        }
        newChild = new String(childArr);
        //System.out.println(child +" mutated into "+newChild);

        return newChild;
    }

    /*
    Board layout: # represents Queen pos in column
                * * * * * * * *
                * * 7 * * * * *
                * * * * * * * *
                * * * 5 * * * *
                * * * * * 4 * *
                3 * * * * * * *
                * 2 * * 2 * * *
                * * * * * * 1 1

              String would be: 32752411


     */
    public int FitnessFn(String s){
        char[] population = s.toCharArray();

        int numNonHits = 0;

        int temp;
        for(int i=0;i<population.length;++i){
            temp = Character.getNumericValue(population[i]);

            for(int j=i+1;j<population.length;++j){
                int charVal = Character.getNumericValue(population[j]);
                int upDiag = temp+j-i;
                int downDiag = temp-j+i;

                if(charVal==upDiag){
                    //System.out.println("Up diag: "+temp+" hit "+Character.getNumericValue(population[j])+" in pos "+j);
                    //System.out.println();
                }
                else if(charVal==downDiag){
                    //System.out.println("Down diag: "+temp+" hit "+Character.getNumericValue(population[j])+" in pos "+j);
                    //System.out.println();
                }
                else if(temp == charVal){
                    //System.out.println("Right:" + temp + " hit " + charVal+" in pos "+j);
                    //System.out.println();
                }
                else{
                    numNonHits++;
                }
            }
        }
        return numNonHits;
    }
}
