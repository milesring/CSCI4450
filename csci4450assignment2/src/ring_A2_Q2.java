import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class ring_A2_Q2 {

/*Written by Miles Ring on 9/25/2017 for CSCI4450 Intro to AI

Problem:
A very popular application of GAs is in function optimization for multi-variable, complex functions encountered in engineering problems.
In this question, we will use a simpler, linear function and optimize it using a GA.
Consider the function: f = |a + 2b + 3c + 4d – 50|.
Your objective is to find the combination of a, b, c and d that will minimize f.
For this, a chromosome could be the combination of values for a-b-c-d.
For example, a=20, b = 12, c=15, d = 8 is encoded as 20-12-15-8.
The fitness function Is the value of f for the combination of a-b-c-d values in the chromosome.

Write a program to implement a GA that minimizes a linear function.
Your program should prompt the user for GA input parameters like in Question 1 above.
After that, it should prompt the user to enter the function to be optimized in the form below, or exit the program.

The function input format is:
    <max OR min> |n_1a + n_2b + n_3c + ….| - K where n_1, n_2, n_3…and K are integer constants.

Each of the constants could be positive or negative.
Note that the number of function variables, a, b, c, and so on have to be determined from the input.
The optimization will be either max or min.
After running the GA, the program should print the same output as in Question 1, that is given again below:
    -Values of input parameters used (on a single line, comma-separated, format:- <parameter name: value>)
    -Number of iterations or generations of the GA executed
    -Final state reached
    -Final state’s fitness function value.
After printing the output, the program should go back to the menu for function input and exit.
*/


    Scanner in = new Scanner(System.in);
    Random rand = new Random();
    double bestF = 200.0;
    String bestS = "";

    public ring_A2_Q2() {
        int numChromosomes;
        double mutationRate;
        double fitLimit;
        int numIterations;
        int iterationsRan;
        String maxOrMin;
        String function;
        int populationSize;

        ArrayList<String> population = new ArrayList<>();

        //System.out.println(RandomSelection(generatePopulation(parseFunction("-|3a+2b+4c|+5"), 4)));
        //Mutate("3(3)+2(5)+5(2)");

        while(true) {

            System.out.println("CSCI 4450 Assignment 2 Question 2");
            System.out.println("*********************************");
            System.out.println("Enter the following on a single line, separated by a single space:");
            System.out.println("Initial population size, # of chromosomes to select after each iteration or generation of the GA,");
            System.out.println("mutation rate, # of iterations or generations to run the GA for: -1 to run the GA until goal state is found.");
            populationSize = in.nextInt();
            numChromosomes = in.nextInt();
            mutationRate = in.nextDouble();
            numIterations = in.nextInt();
            fitLimit = 0.0;
            iterationsRan = 0;


            if (numIterations == -1) {
                numIterations = Integer.MAX_VALUE;
            }

            System.out.println("***********************************************");
            System.out.println("* Enter a function to be optimized:           *");
            System.out.println("* (<max OR min> |n_1a + n_2b + n_3c+ ...| - K *");
            System.out.println("*     where n_1, n_2, n_3 ... and K are       *");
            System.out.println("*     integer constants                       *");
            System.out.println("*                                             *");
            System.out.println("*** NOTICE: ONLY MIN IS IMPLEMENTED           *");
            System.out.println("*            MAX IS NOT FINISHED              *");
            System.out.println("*                                             *");
            System.out.println("* 5. Exit                                     *");
            System.out.println("***********************************************");

            maxOrMin = in.next();
            function = in.next();

            if (function.equals("5")) {
                System.exit(0);
            }

            population = generatePopulation(parseFunction(function), populationSize);

            for (int i = 0; i < numIterations; ++i) {
                population = GeneticAlgorithm(population, numChromosomes, mutationRate);
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
            System.out.printf("Input values: <numChromosomes: %d> <mutationRate: %f> <numIterations: %d> <fLimit: %f>\n",
                   numChromosomes, mutationRate, numIterations, fitLimit);
            System.out.println("Number of iterations ran: " + iterationsRan);
            System.out.println("Final state: " + bestS);
            System.out.println("Final state fValue: " + bestF);
            System.out.println("\n");
        }

    }

    //finds coeff of variables
    public String parseFunction(String function) {
        ArrayList<String> ops = new ArrayList<>();
        for (int i = 0; i < function.length(); ++i) {
            if (function.charAt(i) == '+' || function.charAt(i) == '-') {
                if (i < function.length() - 1 && function.charAt(i + 1) != '|' && Character.getNumericValue(function.charAt(i + 1)) != -1)
                    ops.add(Character.toString(function.charAt(i)));
            }
            if (function.charAt(i) == '|' || function.charAt(i) == '-') {
                if (i < function.length() - 1 && function.charAt(i) == '-' && Character.getNumericValue(function.charAt(i + 1)) != -1) {
                    continue;
                }
                String s1 = function.substring(0, i);
                String s2 = function.substring(i + 1, function.length());
                function = s1 + s2;
                --i;
            }

        }


        String[] terms = function.split("\\+|-");
        String[] vars = new String[terms.length];
        String[] coeff = new String[terms.length];
        for (int i = 0; i < terms.length; ++i) {
            coeff[i] = Character.toString(terms[i].charAt(0));
            if (terms[i].length() > 1) {
                vars[i] = Character.toString(terms[i].charAt(1));
            }
        }

        if (vars[vars.length - 1] == null) {
            String[] temp = new String[coeff.length - 1];
            for (int i = 0; i < temp.length; ++i) {
                temp[i] = coeff[i];
            }
            coeff = temp;
            ops.remove(ops.size() - 1);
        }

        String newFunc = "";
        for (int i = 0; i < coeff.length; i++) {
            newFunc += coeff[i];
            if (i < ops.size()) {
                newFunc += ops.get(i);
            }
        }

        return newFunc;

    }

    //populates initial population with random variables
    public ArrayList<String> generatePopulation(String func, int populationSize) {
        ArrayList<String> pop = new ArrayList<>();

        for (int i = 0; i < populationSize; ++i) {
            String temp = "";
            for (int j = 0; j < func.length(); ++j) {
                if(func.charAt(j) == '+' || func.charAt(j) == '-'){
                    temp += "(" + Integer.toString(rand.nextInt(10)) + ")";
                }
                temp += Character.toString(func.charAt(j));
                if (j == func.length()-1) {
                    temp += "(" + Integer.toString(rand.nextInt(10)) + ")";
                }

            }
            pop.add(temp);
        }
        return pop;
    }

    //finds best fvalue
    public void findBest(ArrayList<String> pop) {
        double best = 10000.0f;
        int index = 0;
        int[] rawValues = new int[pop.size()];
        double[] fValues = new double[pop.size()];
        int totalVal = 0;

        //calculate unattacking queen pairs
        for (int i = 0; i < pop.size(); ++i) {
            rawValues[i] = FitnessFn(pop.get(i));
            totalVal += rawValues[i];

        }

        //calc fitness values
        for (int i = 0; i < pop.size(); ++i) {
            fValues[i] = (double) rawValues[i] / (double) totalVal;
        }

        for (int i = 0; i < pop.size(); ++i) {
            if (fValues[i] < best) {
                best = fValues[i];
                index = i;
            }

        }
        if (best < bestF) {
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
    public ArrayList GeneticAlgorithm(ArrayList pop, int numChromosomes, double mutationRate) {
        ArrayList<String> newPop = new ArrayList<>();

        for (int j = 1; j <= pop.size(); ++j) {
            for (int i = 0; i < numChromosomes / 2; i++) {
                String x = RandomSelection(pop);
                String y = RandomSelection(pop);
                while (true) {
                    if (x == y) {
                        y = RandomSelection(pop);
                    } else {
                        break;
                    }
                }
                String child = Reproduce(x, y);

                if (rand.nextDouble() < mutationRate) {
                    child = Mutate(child);
                }
                newPop.add(child);
            }
        }
        return newPop;
    }

    public String RandomSelection(ArrayList<String> pop) {
        String select = pop.get(pop.size() - 1);
        int[] rawValues = new int[pop.size()];
        double[] fValues = new double[pop.size()];
        int totalVal = 0;

        //calculate function values
        //System.out.println("Calculating function values...");
        for (int i = 0; i < pop.size(); ++i) {
            rawValues[i] = FitnessFn(pop.get(i));
            //System.out.print(rawValues[i]+" ");
            totalVal += rawValues[i];
        }
        //System.out.println();
        double totalFit = 0.0;
        //calc fitness values
        //System.out.println("Calculating fitness values...");
        for (int i = 0; i < pop.size(); ++i) {
            fValues[i] = (double) rawValues[i] / (double) totalVal;
            totalFit += fValues[i];
            //System.out.printf(fValues[i]+" ");
        }
        //System.out.println();
        double prob = rand.nextDouble() * totalFit;
        for (int i = 0; i < pop.size(); ++i) {
            prob -= fValues[i];
            if (prob <= 0) {
                select = pop.get(i);
                //System.out.println("Selected: "+select);
                break;
            }
        }
        return select;
    }

    public String Reproduce(String x, String y) {

        return singlePoint(x, y);
    }

    public String singlePoint(String x, String y) {
        String child;
        int randomSplit = rand.nextInt(7 + 1);
        child = x.substring(0, randomSplit) + y.substring(randomSplit, y.length());
        //System.out.println("x: "+x+ " and "+y+" have produced...");
        //System.out.println(child);
        return child;
    }

    public String Mutate(String child) {
        String newChild;

        //count ops to determine index range
        ArrayList<String> ops = new ArrayList<>();
        //split operations
        for (int i = 0; i < child.length(); ++i) {
            if (child.charAt(i) == '+' || child.charAt(i) == '-') {
                ops.add(Character.toString(child.charAt(i)));
            }
        }
        char[] childArr = child.toCharArray();
        int newVal = rand.nextInt(10);
        int index = rand.nextInt(ops.size()+1);
        int count = 0;

        for(int i=0;i<childArr.length;++i) {
            if(childArr[i] == '('){
                if(count == index) {
                    childArr[i+1] = Integer.toString(newVal).charAt(0);
                    break;
                }
                count++;
              }
        }
        newChild = new String(childArr);
        //System.out.println(child +" mutated into "+newChild);

        return newChild;
    }

    public int FitnessFn(String s) {
        int fitness = 0;
        ArrayList<String> ops = new ArrayList<>();
        ArrayList<String> vals = new ArrayList<>();
        ArrayList<String> coeff = new ArrayList<>();

        //split operations
        for (int i = 0; i < s.length(); ++i) {
            if (s.charAt(i) == '+' || s.charAt(i) == '-') {
                ops.add(Character.toString(s.charAt(i)));
            }
        }

        String[] terms = s.split("\\+|-");

        //split function variables and coeff
        for(int i=0;i<terms.length;++i){
            String[] temp = terms[i].split("\\(");
            coeff.add(temp[0]);
            String tempString = temp[1].substring(0,temp[1].length()-1);
            vals.add(tempString);
        }

        //calc value
        fitness += Integer.parseInt(coeff.get(0)) * Integer.parseInt(vals.get(0));
        for(int i=0;i<ops.size();++i){
            if(ops.get(i).equals("+")) {
                fitness += Integer.parseInt(coeff.get(i+1)) * Integer.parseInt(vals.get(i+1));
            }
            else{
                fitness -= Integer.parseInt(coeff.get(i+1)) * Integer.parseInt(vals.get(i+1));
            }
        }
        return fitness;
    }
}