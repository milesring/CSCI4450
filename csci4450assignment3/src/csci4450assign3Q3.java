import java.util.Random;
import java.util.Scanner;


/*
Miles Ring
CSCI4450
Dasgupta Fall 2017

Problem 3. Markov Decision Process (MDP)
(Adapted from Russell-Norvig Problem 17.8)			(30 points = 15 points each part)

In class, we studied that one way to solve the Bellman update equation in MDPs is using the Value iteration algorithm. (Figure 17.4 of textbook).

(a) Implement the value iteration algorithm to calculate the policy for navigating a robot (agent) with uncertain motion in a rectangular grid, similar to the situation discussed in class, from Section 17.1 of the textbook.
(b) Calculate the same robot’s policy in the same environment, this time using the policy iteration algorithm.
You can combine these two parts into the same class or program and have the user input select the appropriate algorithm.

Your program should create the 3 x 3 grid world given in Figure 17.14 (a) of the textbook along with the corresponding rewards at each state (cell). The transition model for your agent is the same as that given in Section 17.1 (discussed in class) – 80% of the time it goes in the intended direction, 20% of the time it goes at right angles to its intended direction. You should accept the following values of r as input: 100, -3, 0 and +3. The input format is below:

Enter r: <value of r>
Enter 1 for Value Iteration, 2 for Policy Iteration, 3 to Exit: <1 or 2 or 3>

The output of your program should give the policy for each cell in the grid world calculated by your program(s). For value iteration, the policy at each state (cell) is calculated using the policy equation (Equation 17.4 of textbook). For policy iteration, the algorithm’s output is the policy for each state.

Output format:
Policy table calculated:
(0, 0): <calculated policy>
(0, 1): <calculated policy>

 */


public class csci4450assign3Q3 {
    double[][] states;
    double[][] utility;
    double[][] utilityPrime;
    int[][] policy;
    int r;
    int choice;
    Scanner in = new Scanner(System.in);

    public csci4450assign3Q3(){
        states = new double[3][3];
        policy = new int[3][3];
        //hardcoding the utilities
        for(int i=0;i<states.length;++i){
            for(int j=0;j<states[i].length;++j) {
                if (i == 0 && j == 0) {
                   states[i][j] = r;
                } else if (i == 0 && j == 2) {
                    states[i][j] = 10;
                } else {
                    states[i][j] = -1;
                }
            }
        }
        DisplayMenu();
    }

    private void DisplayMenu(){
        while(true){
            System.out.println("Enter r:");
            r = in.nextInt();
            if( r== 100 || r == -3 || r == 0 || r == 3){
                break;
            }
        }
        while(true){
            System.out.println("Enter 1 for Value Iteration, 2 for Policy Iteration, 3 to Exit:");
            choice = in.nextInt();
            if(choice == 1 || choice == 2) {
                break;
            }
            else if(choice == 3){
                System.exit(0);
            }
        }

        if(choice == 1){
            ValueIteration();
        }
        else{
            PolicyIteration();
        }

        OutputTable();
    }

    /*
    function VALUE-ITERATION(mdp, epsilon) returns a utility function
        inputs: mdp, an MDP with states S, actions A(s), transition model P (s' | s, a),
                    rewards R(s), discount γ
               epsilon, the maximum error allowed in the utility of any state
    local variables: U , U', vectors of utilities for states in S , initially zero
                        δ, the maximum change in the utility of any state in an iteration
    repeat
        U ← U'; δ ← 0
        for each state s in S do
            U'[s] ← R(s) + γ max Sum(s') P (s' | s, a) U [s']
                            a ∈ A(s)
            if |U'[s] − U [s]| > δ then δ ← |U'[s] − U [s]|
    until δ < epsilon(1 − γ)/γ
    return U
    */
    private void ValueIteration(){
        double gamma = 0.99;
        double delta;
        double epsilon = 0.01;
        double min = epsilon*(1-gamma)/gamma;
        utility = new double[3][3];
        utilityPrime = new double[3][3];


        //repeat
        do{
            // U <- U'
            for(int i=0;i<states.length;++i) {
                for (int j = 0; j < states[i].length; ++j) {
                    utility[i][j] = utilityPrime[i][j];
                }
            }

            //delta <- 0
            delta = 0;

            //for each state s in S do
            for(int i=0;i<states.length;++i){
                for(int j=0;j<states[i].length;++j) {
                    //U'[s] <- R(s) + gamma Max(Sum)P(s'|s,a)U[s']
                    utilityPrime[i][j] = states[i][j] + gamma * TransitionModel(i,j, utilityPrime);

                    //if |U'[s] − U [s]| > δ then δ ← |U'[s] − U [s]|
                    if( Math.abs(utilityPrime[i][j]-utility[i][j]) > delta ){
                        delta = Math.abs(utilityPrime[i][j]-utility[i][j]);
                    }
                }
            }
        // until δ < epsilon(1 − γ)/γ
        }while(delta >= min);


    }

    private double TransitionModel(int x,int y, double[][] a){
        double max = 0;
        double up, down, left, right;
        for(int i=0;i<4;++i){
            up = GetUp(x, y, i, a);
            left = GetLeft(x, y, i, a);
            right = GetRight(x, y, i, a);
            down = GetDown(x, y, i, a);
            switch(i) {
                //up
                case 0:
                    down = 0;
                    break;
                //down
                case 1:
                    up = 0;
                    break;
                //left
                case 2:
                    right = 0;
                    break;
                //right
                case 3:
                    left = 0;
                    break;
                default:
            }
            double val = up + left + right + down;
            if(val > max){
               max = val;
               policy[x][y] = i;
            }
        }
        return max;
    }


    private double GetUp(int x, int y, int d, double[][] a){
        //get up
        double up;
        if(d == 0) {
            if (x > 0) {
                up = 0.8 * a[x - 1][y];
            }
            //terminal
            else {
                up = 0.8 * a[x][y];
            }
        } else {
            if (x > 0) {
                up = 0.1 * a[x - 1][y];
            }
            //terminal
            else {
                up = 0.1 * a[x][y];
            }
        }
        return up;
    }

    private double GetLeft(int x, int y, int d, double[][] a){
        double left;
        //get left
        if(d == 2){
            if (y != 0) {
                left = 0.8 * a[x][y-1];
            }
            //terminal
            else {
                left = 0.8 * a[x][y];
            }

        } else {
            if (y != 0) {
                left = 0.1 * a[x][y-1];
            }
            //terminal
            else {
                left = 0.1 * a[x][y];
            }
        }

        return left;
    }

    private double GetRight(int x, int y, int d, double[][] a){
        double right;
        //get right
        if(d == 3){
            if (y != 2) {
                right = 0.8 * a[x][y + 1];
            }
            //terminal
            else {
                right = 0.8 * a[x][y];
            }

        } else {
            if (y != 2) {
                right = 0.1 * a[x][y + 1];
            }
            //terminal
            else {
                right = 0.1 * a[x][y];
            }
        }

        return right;
    }

    private double GetDown(int x, int y, int d, double[][] a){
        double down;
        if(d == 1){
            if (x < a.length - 1) {
                down = 0.8 * a[x + 1][y];
            }
            //terminal
            else {
                down = 0.8 * a[x][y];
            }
        } else {
            if (x < a.length - 1) {
                down = 0.1 * a[x + 1][y];
            }
            //terminal
            else {
                down = 0.1 * a[x][y];
            }
        }
        return down;
    }

    /*function POLICY-ITERATION(mdp) returns a policy
        inputs: mdp, an MDP with states S , actions A(s), transition model P (s' | s, a)
        local variables:    U , a vector of utilities for states in S , initially zero
                            π, a policy vector indexed by state, initially random

        repeat
            U ← POLICY-EVALUATION (π, U, mdp)
            unchanged ? ← true
            for each state s in S do
                if max  Sum(s') P (s' | s, a) U [s'] > Sum(s') P (s' | s, π[s]) U [s'] then do
                    a ∈ A(s)

                    π[s] ← argmax Sum(s') P (s' | s, a) U [s']
                        a ∈ A(s)

                    unchanged ? ← false
    until unchanged?
    return π
    */
    private void PolicyIteration(){
        Random rand = new Random();
            utility = new double[3][3];
            boolean unchanged;

            //randomize policy
            for(int i=0;i<policy.length;++i){
                for(int j=0;j<policy[i].length;++j){
                   policy[i][j] = rand.nextInt(4);
                }
            }

            policy[0][2] = 5;

            do{
                //U ← POLICY-EVALUATION (π, U, mdp)
                utility = PolicyEvaluation();
                // unchanged ? ← true
                unchanged = true;

                //for each state s in S do
                for(int i=0;i<states.length;++i){
                    for(int j=0;j<states[i].length;++j){

                        // if max  Sum(s') P (s' | s, a) U [s'] > Sum(s') P (s' | s, π[s]) U [s'] then do
                        //          a ∈ A(s)
                        if( > PolicyTransition(i,j, utility)){
                            unchanged = false;
                        }
                    }
                }
            }while(!unchanged);
    }

    private double PolicyTransition(int x, int y, double[][] util){
        double up = GetUp(x, y, 1, util);
        double left= GetLeft(x, y, 1, util);
        double right = GetRight(x, y, 1, util);
        double down = GetDown(x, y, 0, util);
        switch(policy[x][y]) {
            //up
            case 0:
                up = GetUp(x,y,0, util);
                down = 0;
                break;
            //down
            case 1:
                up = 0;
                down = GetDown(x,y,1,util);
                break;
            //left
            case 2:
                right = 0;
                left = GetLeft(x,y,2,util);
                break;
            //right
            case 3:
                left = 0;
                right = GetRight(x,y,3,util);
                break;
            default:
        }
        double val = up + left + right + down;

        return val;
    }

    private double[][] PolicyEvaluation(){
        int k = 50;
        double gamma = 0.99;
        double[][] newUtil = new double[3][3];
        //repeat k times
        for(;k<=0;k--) {
            for (int i = 0; i < states.length; ++i) {
                for (int j = 0; j < states[i].length; ++i) {
                    newUtil[i][j] = states[i][j] + gamma * PolicyTransition(i, j, newUtil);

                }
            }
        }
        return newUtil;
    }

    private void OutputTable(){
        System.out.println("Policy table calculated:");
        for(int i=0;i<policy.length;++i){
          for(int j=0;j<policy[i].length;++j){
              int[] coords = ConvertCoords(i,j);
              System.out.print("("+coords[1]+", "+coords[0]+"): ");
              if(i == 0 && j == 2 ){
                  System.out.println("Goal");
              } else {
                  switch (policy[i][j]) {
                      case 0:
                          System.out.println("Up");
                          break;
                      case 1:
                          System.out.println("Down");
                          break;
                      case 2:
                          System.out.println("Left");
                          break;
                      case 3:
                          System.out.println("Right");
                          break;
                  }
              }
          }
        }
    }

    public int[] ConvertCoords(int i, int j){
        int[] coords = new int[2];

        switch (i){
            case 0:
                coords[0] = 3;
                break;
            case 1:
                coords[0] = 2;
                break;
            case 2:
                coords[0] = 1;
                break;
            default:
                break;
        }

        switch (j){
            case 0:
                coords[1] = 1;
                break;
            case 1:
                coords[1] = 2;
                break;
            case 2:
                coords[1] = 3;
                break;
            case 3:
                coords[1] = 4;
                break;
            default:
                break;
        }

        return  coords;
    }
}
