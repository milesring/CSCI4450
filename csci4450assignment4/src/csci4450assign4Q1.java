import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/*
Problem 1. Maze Navigation Reinforcement Learning					(40 points)

In class, we discussed three different approaches for reinforcement learning (RL):
    direct utility estimates, adaptive dynamic programming and temporal difference learning.
In this question, we will explore these RL algorithms for maze navigation
For the maze, assume bottom left cell is at (1, 1) and the format for the coordinates is (<col>, <row>)

        a. 4 X 3 world with one obstacle in (2, 2), reward +1 at (4, 3) and -1 at (4, 2)
       (same environment as in textbook Figure 17.1 (a))
        b. 10 X 10 world with no obstacles and reward +1 at (5, 5)
        c. 10 X 10 world with four obstacles at (4, 4) (6, 4) (4, 6) (6, 6) and reward +1 at (5, 5),
        d. 10 X 10 world with four obstacles at (4, 4) (6, 4) (4, 6) (6, 6) and reward +1 at (5, 5),
        reward -1 at (5, 7) and (4, 5)

Write a program that prompts the user via a start menu to first select the world (a) – (d) above,
and then to select the RL algorithm (1) DUE, (2) ADP, (3) TD.
Your program should select an appropriate number of trials or epochs to learn the utilities and/or model.
When the algorithm finishes, your program should again prompt the user to input a start state
(two integer coordinates separated by a space, with check for input being a valid state – inside environment, not obstacle).
From this start state, your agent should navigate until it reaches a terminal state (correct operation should reach the +1 terminal state).
Your program should then printout the coordinates of the states the agent navigated through until it reached the terminal state.
Remember to return the program to the start menu after each run, and add an exit option to the start menu,
so that the program can be tested multiple times.

*/

public class csci4450assign4Q1 {

    final int epochs = 3000;
    String world;
    int rlAlg;
    Scanner in = new Scanner(System.in);

    List<StateActionPairs> stateActionPairsList = new ArrayList<>();
    List<StateActionTriple> stateActionTripleList = new ArrayList<>();
    List<StateActionPairs> transitionProbability = new ArrayList<>();

    int[] prevState;
    int prevAction;
    double[][] ADPUtility;
    double[][] ADPReward;
    double gamma = 0.99;


    public csci4450assign4Q1(){
        DisplayMenu();
    }

    private void DisplayMenu(){
        Long start;
        Long end;
        while(true) {
            world = "";
            rlAlg = 0;
            while (true) {
                System.out.println("Select World (a-d)");
                System.out.println("");
                System.out.println("a. 4 X 3 world with one obstacle in (2, 2), reward +1 at (4, 3) and -1 at (4, 2) ");
                System.out.println("b. 10 X 10 world with no obstacles and reward +1 at (5, 5)");
                System.out.println("c. 10 X 10 world with four obstacles at (4, 4) (6, 4) (4, 6) (6, 6) and reward +1 at (5, 5),");
                System.out.println("d. 10 X 10 world with four obstacles at (4, 4) (6, 4) (4, 6) (6, 6) and reward +1 at (5, 5),\n" +
                        "        reward -1 at (5, 7) and (4, 5)");
                System.out.println("z. exit");
                System.out.println("");

                world = in.next();
                world = world.toLowerCase();
                if(world.equals("z")){
                    System.exit(0);
                }
                if (!world.equals("a") && !world.equals("b") && !world.equals("c") &&
                        !world.equals("d")) {
                    continue;
                }
                break;
            }


            double[][] worldGrid = BuildWorld(world);
            int[][] policy = BuildPolicy(world);

            while (true) {
                System.out.println("Select RL algorithm");
                System.out.println("");
                System.out.println("1. DUE");
                System.out.println("2. ADP");
                System.out.println("3. TD");
                System.out.println("");

                rlAlg = in.nextInt();
                if (rlAlg < 1 && rlAlg > 3) {
                    continue;
                }
                break;
            }
            start = System.currentTimeMillis();
            switch (rlAlg) {
                case 1:
                    DUE(policy, worldGrid);
                    break;
                case 2:
                    ADP(policy, worldGrid);
                    break;
                case 3:
                    TD(policy, worldGrid);
                    break;
                default:
                    break;
            }
            end = System.currentTimeMillis();
            Long timeTaken = end - start;
            System.out.println("Alg took: "+timeTaken+ " ms");
        }



    }


    private void DUE(int[][] policy, double[][] worldGrid) {
       double[][] utility = new double[worldGrid.length][worldGrid[0].length];
       int[][] visited = new int[worldGrid.length][worldGrid[0].length];
       boolean atGoal;

       //running for epochs
       for(int e=0;e<epochs;++e){
           List<Double> traveled = new ArrayList<>();
           atGoal = false;
           int i,j,di,dj;

           //randomize start location
           while(true) {
               i = ThreadLocalRandom.current().nextInt(0, worldGrid.length);
               j = ThreadLocalRandom.current().nextInt(0, worldGrid[0].length);
               if (policy[i][j] == 4) {
                   continue;
               }
               break;
           }

           //changing coordinates
           di = i;
           dj = j;
           //run until goal is found
           while(!atGoal){
               //add to traveled list
               traveled.add(worldGrid[di][dj]);
               //check the policy instruction
               switch (policy[di][dj]){
                   //up
                    case 0:
                        di--;
                        break;
                    //down
                    case 1:
                        di++;
                        break;
                    //left
                    case 2:
                        dj--;
                        break;
                    //right
                    case 3:
                        dj++;
                        break;
                    //obstacle
                    case 4:
                        System.out.println("Error: over obstacle");
                        break;
                    //goal
                    case 5:
                        atGoal = true;
                        break;
                }
           }

           //sum all
           double sum = 0;
           for(int x = 0; x < traveled.size() ; ++x){
               sum+=traveled.get(x);
           }
           visited[i][j]++;
           //calc new average
           utility[i][j] = utility[i][j]+((sum - utility[i][j])/visited[i][j]);
       }

        SolveMaze(utility, policy);
    }

    private void SolveMaze(double[][] utility, int[][] policy){
        Scanner in = new Scanner(System.in);
        int x;
        int y;
        int[] coords;
        while(true) {
            System.out.println("Enter a start state(two integer coordinates separated by a space:");

            //column first
            y = in.nextInt();
            x = in.nextInt();



            if(x < 1 || x > utility.length){
                System.out.println("Error: row out of bounds. Enter new state");
                continue;
            }

            if(y < 1 || y > utility[0].length){
                System.out.println("Error: col out of bounds. Enter new state");
                continue;
            }

            coords = UnConvertCoords(x,y,utility.length, utility[0].length);
            if(policy[coords[0]][coords[1]] == 4){
                System.out.println("Error: starting state is an obstacle. Enter new state");
                continue;
            }
            break;
        }

        List<int[]> traveled = new ArrayList<>();
        boolean atGoal = false;
        //traverse based on utility
        int dx = coords[0];
        int dy = coords[1];
        while(!atGoal){
            double[] directions = new double[4];

            if(policy[dx][dy]==5){
                atGoal = true;
            }

            //up
            if(dx > 0){
                directions[0] = utility[dx-1][dy];
            }
            else{
                directions[0] = utility[dx][dy];
            }

            //down
            if(dx < utility.length-1){
                directions[1] = utility[dx+1][dy];
            }
            else{
                directions[1] = utility[dx][dy];
            }

            //left
            if(dy > 0){
                directions[2] = utility[dx][dy-1];
            } else {
                directions[2] = utility[dx][dy];
            }

            //right
            if(dy < utility[0].length-1){
                directions[3] = utility[dx][dy+1];
            } else {
                directions[3] = utility[dx][dy];
            }

            //find direction to go based on max utility
            int dir = 0;
            double max = directions[0];
            for(int i=0;i<directions.length;++i){
                if(directions[i]>max && directions[i] != 0){
                    max = directions[i];
                    dir = i;
                }
            }

            traveled.add(new int[]{dx,dy});
            switch (dir){
                case 0:
                    dx--;
                    break;
                case 1:
                    dx++;
                    break;
                case 2:
                    dy--;
                    break;
                case 3:
                    dy++;
                    break;
            }



        }
        PrintTraversed(traveled, utility);
    }

    private void PrintTraversed(List<int[]> traveled, double[][] utility){
        System.out.println("States navigated:");
        for(int i=0;i<traveled.size();++i){
            int[] coords = ConvertCoords(traveled.get(i)[0], traveled.get(i)[1],utility.length, utility[0].length);
            System.out.print("(<"+coords[1]+">, <"+coords[0]+">)");

            if(i==0){
                System.out.println(": Start");
            }else if(i==traveled.size()-1){
                System.out.println(": Goal");
            }else{
                System.out.println();
            }
        }

    }

    private void ADP(int[][] policy, double[][] worldGrid){
        //utility
         ADPUtility = new double[policy.length][policy[0].length];
         //reward
         ADPReward = new double[policy.length][policy[0].length];
        //whether a state is new or not
        int[][] stateStatus = new int[policy.length][policy[0].length];

        prevAction = -1;
        prevState = null;

        boolean atGoal;
        int i, j, di, dj;

        //data structure does not lead to very efficient means w/ 3000 epochs
        //for(int e=0;e<epochs;++e){
        for(int e=0;e<350;++e){
            atGoal = false;

            //randomize start location
            while(true) {
                i = ThreadLocalRandom.current().nextInt(0, worldGrid.length);
                j = ThreadLocalRandom.current().nextInt(0, worldGrid[0].length);
                //i = 0;
                //j = 3;
                if (policy[i][j] == 4) {
                    continue;
                }
                break;
            }

            //changing coordinates
            di = i;
            dj = j;
            //run until goal is found

            while(!atGoal){
                i = di;
                j = dj;
                //check the policy instruction
                if(prevAction != -1) {
                    switch (prevAction) {
                        //up
                        case 0:
                            di--;
                            break;
                        //down
                        case 1:
                            di++;
                            break;
                        //left
                        case 2:
                            dj--;
                            break;
                        //right
                        case 3:
                            dj++;
                            break;
                        //obstacle
                        case 4:
                            System.out.println("Error: over obstacle");
                            break;
                        //goal
                        case 5:
                            atGoal = true;
                            break;
                    }
                }
                AdpAgent(new int[]{di,dj}, stateStatus, worldGrid, policy);
                if(prevAction == -1){
                    atGoal = true;
                }
            }


        }
    SolveMaze(ADPUtility, policy);

    }

    private int AdpAgent(int[] state, int[][] stateStatus, double[][] worldGrid, int[][] policy){
        int x = state[0];
        int y = state[1];

        // if s' is new then U[s'] <- r'; R[s'] <- r'
        if(stateStatus[x][y] == 0){
            ADPUtility[x][y] = worldGrid[x][y];
            ADPReward[x][y] = worldGrid[x][y];
            stateStatus[x][y] = 1;
        }

        // if s is not null then
        if(prevState != null){

            //increment Nsa[s,a]
            StateActionPairs sa = new StateActionPairs();
            sa.action = prevAction;
            sa.state = prevState;

            boolean in = false;
            for(int i=0;i<stateActionPairsList.size();++i){
                if(sa.action == stateActionPairsList.get(i).action &&
                        sa.state == stateActionPairsList.get(i).state ){
                    stateActionPairsList.get(i).freq++;
                    in = true;
                }
            }
            if(!in){
                sa.freq++;
                stateActionPairsList.add(sa);
            }

            in = false;
            //and Nsas’[s,a,s’]
            StateActionTriple sat = new StateActionTriple();
            sat.action = prevAction;
            sat.state = prevState;
            sat.statePrime = state;
            for(int i=0;i<stateActionTripleList.size();++i){
                if(sat.action == stateActionTripleList.get(i).action &&
                        sat.state == stateActionTripleList.get(i).state &&
                        sat.statePrime == stateActionTripleList.get(i).statePrime){
                    stateActionTripleList.get(i).freq++;
                    in = true;
                }
            }
            if(!in){
                sat.freq++;
                stateActionTripleList.add(sat);
            }


            //for each t such that Nsas [sat] is a nonzero
            for(int i=0;i<stateActionTripleList.size();++i){
                if(stateActionTripleList.get(i).freq != 0){
                    boolean contains = false;
                    for(int j=0;j<transitionProbability.size();++j){
                        if(transitionProbability.get(j).state == stateActionTripleList.get(i).state &&
                                transitionProbability.get(j).action == stateActionTripleList.get(i).action ){
                            contains = true;

                            for(int k=0;k<stateActionPairsList.size();++k){
                                if(stateActionTripleList.get(i).state == stateActionPairsList.get(k).state &&
                                        stateActionTripleList.get(i).action == stateActionPairsList.get(k).action){
                                    //DO T[sat] <- Nsas[sast]/Nsa[sa]
                                    transitionProbability.get(j).freq = stateActionTripleList.get(i).freq /stateActionPairsList.get(j).freq;
                                }
                            }
                        }
                    }
                    if(!contains){
                        StateActionPairs t = new StateActionPairs();
                        t.action = stateActionTripleList.get(i).action;
                        t.state = stateActionTripleList.get(i).state;
                        t.freq = 1;
                        transitionProbability.add(t);
                    }
                }
            }
        }

        ADPUtility = PolicyEvaluation(ADPUtility, ADPReward, policy);
        if(policy[state[0]][state[1]]==5){
            prevState = null;
            prevAction = -1;
        }
        else{
            prevState = state;
            prevAction = policy[state[0]][state[1]];
        }
        return prevAction;
    }

    private double GetUp(int x, int y, double[][] a){
        double up = 0.0;

        //up
        if (x > 0) {
            up += 0.8 * a[x - 1][y];
        }
        //terminal
        else {
            up += 0.8 * a[x][y];
        }

        //left
        if(y != 0){
            up += 0.1 * a[x][y-1];
        }
        else {
            up += 0.1 * a[x][y];
        }

        //right
        if (y != a[x].length-1) {
            up += 0.1 * a[x][y + 1];
        }
        //terminal
        else {
            up += 0.1 * a[x][y];
        }

        return up;
    }

    private double GetLeft(int x, int y, double[][] a){
        double left = 0.0;
        if (y != 0) {
            left += 0.8 * a[x][y-1];
        }
        //terminal
        else {
            left += 0.8 * a[x][y];
        }

        //up
        if (x > 0) {
            left += 0.1 * a[x - 1][y];
        }
        //terminal
        else {
            left += 0.1 * a[x][y];
        }

        //down
        if (x != a.length - 1) {
            left += 0.1 * a[x + 1][y];
        }
        //terminal
        else {
            left += 0.1 * a[x][y];
        }


        return left;
    }

    private double GetRight(int x, int y, double[][] a){
        double right = 0.0;
        //right
        if (y != a[x].length-1) {
            right += 0.8 * a[x][y + 1];
        }
        //terminal
        else {
            right += 0.8 * a[x][y];
        }

        //up
        if (x > 0) {
            right += 0.1 * a[x - 1][y];
        }
        //terminal
        else {
            right += 0.1 * a[x][y];
        }

        //down
        if (x != a.length - 1) {
            right += 0.1 * a[x + 1][y];
        }
        //terminal
        else {
            right += 0.1 * a[x][y];
        }

        return right;
    }

    private double GetDown(int x, int y, double[][] a){
        double down = 0.0;

        //down
        if (x != a.length - 1) {
            down += 0.8 * a[x + 1][y];
        }
        //terminal
        else {
            down += 0.8 * a[x][y];
        }

        //left
        if(y != 0){
            down += 0.1 * a[x][y-1];
        }
        else {
            down += 0.1 * a[x][y];
        }

        //right
        if (y != a[x].length-1) {
            down += 0.1 * a[x][y + 1];
        }
        //terminal
        else {
            down += 0.1 * a[x][y];
        }
        return down;
    }

    private double PolicyTransition(int x, int y, double[][] util, int[][] policy){
        switch (policy[x][y]){
            case 0:
                return GetUp(x,y,util);
            case 1:
                return GetDown(x,y,util);
            case 2:
                return GetLeft(x,y,util);
            case 3:
                return GetRight(x,y,util);
            default:
                return 0;
        }
    }

    private double[][] PolicyEvaluation(double[][] utility, double[][] reward, int[][] policy){
        int k = 50;
        double gamma = 0.99;
        //repeat k times
        for(;k>=0;k--) {
            for (int i = 0; i < utility.length; ++i) {
                for (int j = 0; j < utility[i].length; ++j) {
                    if(i > 0 || j < 2) {
                        utility[i][j] = reward[i][j] + gamma * PolicyTransition(i, j, utility, policy);
                    }
                }
            }
        }
        return utility;
    }

    private void TD(int[][] policy, double[][] worldGrid){
        double[][] utility = new double[worldGrid.length][worldGrid[0].length];
        int[][] visited = new int[worldGrid.length][worldGrid[0].length];
        boolean atGoal;
        int i, j, di, dj;

        for(int e=0;e<epochs;++e){
            atGoal = false;

            //randomize start location
            while(true) {
                i = ThreadLocalRandom.current().nextInt(0, worldGrid.length);
                j = ThreadLocalRandom.current().nextInt(0, worldGrid[0].length);
                if (policy[i][j] == 4) {
                    continue;
                }
                break;
            }

            //changing coordinates
            di = i;
            dj = j;
            //run until goal is found

            while(!atGoal){
                i = di;
                j = dj;
                //check the policy instruction
                switch (policy[di][dj]){
                    //up
                    case 0:
                        di--;
                        break;
                    //down
                    case 1:
                        di++;
                        break;
                    //left
                    case 2:
                        dj--;
                        break;
                    //right
                    case 3:
                        dj++;
                        break;
                    //obstacle
                    case 4:
                        System.out.println("Error: over obstacle");
                        break;
                    //goal
                    case 5:
                        atGoal = true;
                        break;
                }
                visited[i][j]++;
                TDUpdate(i,j,di,dj,policy,worldGrid, utility,visited[i][j]);
            }


        }
        SolveMaze(utility,policy);
    }

    private void TDUpdate(int i, int j, int di, int dj, int[][] policy, double[][] worldGrid, double[][] utility, int n){
        utility[i][j] = utility[i][j] + (1/(double)n)*(worldGrid[i][j]+gamma*(utility[di][dj]-utility[i][j]));
    }

    private double[][] BuildWorld(String world){
        double[][] worldGrid;
        switch (world){
            case "a":
                worldGrid = new double[3][4];
                //worldGrid[1][1] = -10000000;
                worldGrid[0][3] = 1;
                worldGrid[1][3] = -1;
                break;
            case "b":
                worldGrid = new double[10][10];
                worldGrid[5][4] = 1;
                break;
            case "c":
                worldGrid = new double[10][10];
                worldGrid[6][3] = -10000000;
                worldGrid[6][5] = -10000000;
                worldGrid[4][3] = -10000000;
                worldGrid[4][5] = -10000000;
                worldGrid[5][4] = 1;
                break;
            case "d":
                worldGrid = new double[10][10];
                worldGrid[6][3] = -10000000;
                worldGrid[6][5] = -10000000;
                worldGrid[4][3] = -10000000;
                worldGrid[4][5] = -10000000;
                worldGrid[5][4] = 1;
                worldGrid[3][4] = -1;
                worldGrid[5][3] = -1;
                break;
            default:
                worldGrid = null;
                break;
        }

        for(int i=0;i<worldGrid.length;++i){
            for(int j=0;j<worldGrid[i].length;++j){
                if(worldGrid[i][j] == 0.0){
                    worldGrid[i][j] = -0.04;
                }
            }
        }
        return worldGrid;
    }

    private int[][] BuildPolicy(String world) {
        int[][] policy;
        switch (world){
            case "a":
                policy = BuildPolicyA();
                break;
            case "b":
                policy = BuildPolicyB();
                break;
            case "c":
                policy = BuildPolicyC();
                break;
            case "d":
                policy = BuildPolicyD();
                break;
            default:
                policy = null;
                break;
        }
        return  policy;
    }

    private void OutputTable(int[][] policy) {
        System.out.println("Policy table calculated:");
        for (int i = 0; i < policy.length; ++i) {
            for (int j = 0; j < policy[i].length; ++j) {
                int[] coords = ConvertCoords(i, j, policy.length, policy[i].length);
                System.out.print("(" + coords[1] + ", " + coords[0] + "): ");

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
                        case 4:
                            System.out.println("Obstacle");
                            break;
                        case 5:
                            System.out.println("Goal");
                            break;
                    }
            }
        }
    }

    private int[][] BuildPolicyA(){
        int[][] policy = new int[3][4];

        //first column
        policy[0][0] = 3;
        policy[1][0] = 0;
        policy[2][0] = 0;

        //second column
        policy[0][1] = 3;
        policy[1][1] = 4;
        policy[2][1] = 2;

        //third column
        policy[0][2] = 3;
        policy[1][2] = 0;
        policy[2][2] = 2;

        //fourth column
        policy[0][3] = 5;
        policy[1][3] = 5;
        policy[2][3] = 2;

        return policy;
    }

    private int[][] BuildPolicyB(){
        int[][] policy = new int[10][10];
        //first column
        policy[0][0] = 3;
        policy[1][0] = 3;
        policy[2][0] = 3;
        policy[3][0] = 3;
        policy[4][0] = 3;
        policy[5][0] = 3;
        policy[6][0] = 3;
        policy[7][0] = 3;
        policy[8][0] = 3;
        policy[9][0] = 3;

        //second column
        policy[0][1] = 1;
        policy[1][1] = 3;
        policy[2][1] = 3;
        policy[3][1] = 3;
        policy[4][1] = 3;
        policy[5][1] = 3;
        policy[6][1] = 3;
        policy[7][1] = 3;
        policy[8][1] = 3;
        policy[9][1] = 0;

        //third column
        policy[0][2] = 1;
        policy[1][2] = 1;
        policy[2][2] = 3;
        policy[3][2] = 3;
        policy[4][2] = 3;
        policy[5][2] = 3;
        policy[6][2] = 3;
        policy[7][2] = 3;
        policy[8][2] = 0;
        policy[9][2] = 0;

        //fourth column
        policy[0][3] = 1;
        policy[1][3] = 1;
        policy[2][3] = 1;
        policy[3][3] = 1;
        policy[4][3] = 1;
        policy[5][3] = 3;
        policy[6][3] = 0;
        policy[7][3] = 0;
        policy[8][3] = 0;
        policy[9][3] = 0;

        //fifth column
        policy[0][4] = 1;
        policy[1][4] = 1;
        policy[2][4] = 1;
        policy[3][4] = 1;
        policy[4][4] = 1;
        //goal
        policy[5][4] = 5;
        policy[6][4] = 0;
        policy[7][4] = 0;
        policy[8][4] = 0;
        policy[9][4] = 0;

        //sixth column
        policy[0][5] = 1;
        policy[1][5] = 1;
        policy[2][5] = 1;
        policy[3][5] = 1;
        policy[4][5] = 1;
        policy[5][5] = 2;
        policy[6][5] = 0;
        policy[7][5] = 0;
        policy[8][5] = 0;
        policy[9][5] = 0;

        //seventh column
        policy[0][6] = 1;
        policy[1][6] = 1;
        policy[2][6] = 1;
        policy[3][6] = 2;
        policy[4][6] = 2;
        policy[5][6] = 2;
        policy[6][6] = 2;
        policy[7][6] = 2;
        policy[8][6] = 0;
        policy[9][6] = 0;

        //eighth column
        policy[0][7] = 1;
        policy[1][7] = 1;
        policy[2][7] = 1;
        policy[3][7] = 2;
        policy[4][7] = 2;
        policy[5][7] = 2;
        policy[6][7] = 2;
        policy[7][7] = 2;
        policy[8][7] = 0;
        policy[9][7] = 0;

        //ninth column
        policy[0][8] = 1;
        policy[1][8] = 2;
        policy[2][8] = 2;
        policy[3][8] = 2;
        policy[4][8] = 2;
        policy[5][8] = 2;
        policy[6][8] = 2;
        policy[7][8] = 2;
        policy[8][8] = 2;
        policy[9][8] = 0;

        //tenth column
        policy[0][9] = 2;
        policy[1][9] = 2;
        policy[2][9] = 2;
        policy[3][9] = 2;
        policy[4][9] = 2;
        policy[5][9] = 2;
        policy[6][9] = 2;
        policy[7][9] = 2;
        policy[8][9] = 2;
        policy[9][9] = 2;

        return policy;

    }

    private int[][] BuildPolicyC(){
        int[][] policy = new int[10][10];
        //first column
        policy[0][0] = 3;
        policy[1][0] = 3;
        policy[2][0] = 3;
        policy[3][0] = 3;
        policy[4][0] = 3;
        policy[5][0] = 3;
        policy[6][0] = 3;
        policy[7][0] = 3;
        policy[8][0] = 3;
        policy[9][0] = 3;

        //second column
        policy[0][1] = 1;
        policy[1][1] = 3;
        policy[2][1] = 3;
        policy[3][1] = 3;
        policy[4][1] = 3;
        policy[5][1] = 3;
        policy[6][1] = 3;
        policy[7][1] = 3;
        policy[8][1] = 3;
        policy[9][1] = 0;

        //third column
        policy[0][2] = 1;
        policy[1][2] = 1;
        policy[2][2] = 3;
        policy[3][2] = 3;
        policy[4][2] = 1;
        policy[5][2] = 3;
        policy[6][2] = 0;
        policy[7][2] = 3;
        policy[8][2] = 0;
        policy[9][2] = 0;

        //fourth column
        policy[0][3] = 1;
        policy[1][3] = 1;
        policy[2][3] = 1;
        policy[3][3] = 3;
        policy[4][3] = 4;
        policy[5][3] = 3;
        policy[6][3] = 4;
        policy[7][3] = 3;
        policy[8][3] = 0;
        policy[9][3] = 0;

        //fifth column
        policy[0][4] = 1;
        policy[1][4] = 1;
        policy[2][4] = 1;
        policy[3][4] = 1;
        policy[4][4] = 1;
        //goal
        policy[5][4] = 5;
        policy[6][4] = 0;
        policy[7][4] = 0;
        policy[8][4] = 0;
        policy[9][4] = 0;

        //sixth column
        policy[0][5] = 1;
        policy[1][5] = 1;
        policy[2][5] = 1;
        policy[3][5] = 2;
        policy[4][5] = 4;
        policy[5][5] = 2;
        policy[6][5] = 4;
        policy[7][5] = 2;
        policy[8][5] = 0;
        policy[9][5] = 0;

        //seventh column
        policy[0][6] = 1;
        policy[1][6] = 1;
        policy[2][6] = 1;
        policy[3][6] = 1;
        policy[4][6] = 1;
        policy[5][6] = 2;
        policy[6][6] = 0;
        policy[7][6] = 0;
        policy[8][6] = 0;
        policy[9][6] = 0;

        //eighth column
        policy[0][7] = 1;
        policy[1][7] = 1;
        policy[2][7] = 1;
        policy[3][7] = 2;
        policy[4][7] = 2;
        policy[5][7] = 2;
        policy[6][7] = 2;
        policy[7][7] = 2;
        policy[8][7] = 0;
        policy[9][7] = 0;

        //ninth column
        policy[0][8] = 1;
        policy[1][8] = 2;
        policy[2][8] = 2;
        policy[3][8] = 2;
        policy[4][8] = 2;
        policy[5][8] = 2;
        policy[6][8] = 2;
        policy[7][8] = 2;
        policy[8][8] = 2;
        policy[9][8] = 0;

        //tenth column
        policy[0][9] = 2;
        policy[1][9] = 2;
        policy[2][9] = 2;
        policy[3][9] = 2;
        policy[4][9] = 2;
        policy[5][9] = 2;
        policy[6][9] = 2;
        policy[7][9] = 2;
        policy[8][9] = 2;
        policy[9][9] = 2;

        return policy;

    }

    private int[][] BuildPolicyD(){
        int[][] policy = new int[10][10];
        //first column
        policy[0][0] = 3;
        policy[1][0] = 3;
        policy[2][0] = 3;
        policy[3][0] = 3;
        policy[4][0] = 3;
        policy[5][0] = 3;
        policy[6][0] = 3;
        policy[7][0] = 3;
        policy[8][0] = 3;
        policy[9][0] = 3;

        //second column
        policy[0][1] = 1;
        policy[1][1] = 3;
        policy[2][1] = 3;
        policy[3][1] = 3;
        policy[4][1] = 3;
        policy[5][1] = 3;
        policy[6][1] = 3;
        policy[7][1] = 3;
        policy[8][1] = 3;
        policy[9][1] = 0;

        //third column
        policy[0][2] = 1;
        policy[1][2] = 1;
        policy[2][2] = 3;
        policy[3][2] = 3;
        policy[4][2] = 0;
        policy[5][2] = 0;
        policy[6][2] = 1;
        policy[7][2] = 3;
        policy[8][2] = 0;
        policy[9][2] = 0;

        //fourth column
        policy[0][3] = 1;
        policy[1][3] = 1;
        policy[2][3] = 3;
        policy[3][3] = 0;
        policy[4][3] = 4;
        policy[5][3] = 5;
        policy[6][3] = 4;
        policy[7][3] = 3;
        policy[8][3] = 0;
        policy[9][3] = 0;

        //fifth column
        policy[0][4] = 1;
        policy[1][4] = 1;
        policy[2][4] = 3;
        policy[3][4] = 5;
        policy[4][4] = 1;
        //goal
        policy[5][4] = 5;
        policy[6][4] = 0;
        policy[7][4] = 0;
        policy[8][4] = 0;
        policy[9][4] = 0;

        //sixth column
        policy[0][5] = 1;
        policy[1][5] = 1;
        policy[2][5] = 1;
        policy[3][5] = 3;
        policy[4][5] = 4;
        policy[5][5] = 2;
        policy[6][5] = 4;
        policy[7][5] = 2;
        policy[8][5] = 0;
        policy[9][5] = 0;

        //seventh column
        policy[0][6] = 1;
        policy[1][6] = 1;
        policy[2][6] = 1;
        policy[3][6] = 1;
        policy[4][6] = 1;
        policy[5][6] = 2;
        policy[6][6] = 0;
        policy[7][6] = 0;
        policy[8][6] = 0;
        policy[9][6] = 0;

        //eighth column
        policy[0][7] = 1;
        policy[1][7] = 1;
        policy[2][7] = 1;
        policy[3][7] = 2;
        policy[4][7] = 2;
        policy[5][7] = 2;
        policy[6][7] = 2;
        policy[7][7] = 2;
        policy[8][7] = 0;
        policy[9][7] = 0;

        //ninth column
        policy[0][8] = 1;
        policy[1][8] = 2;
        policy[2][8] = 2;
        policy[3][8] = 2;
        policy[4][8] = 2;
        policy[5][8] = 2;
        policy[6][8] = 2;
        policy[7][8] = 2;
        policy[8][8] = 2;
        policy[9][8] = 0;

        //tenth column
        policy[0][9] = 2;
        policy[1][9] = 2;
        policy[2][9] = 2;
        policy[3][9] = 2;
        policy[4][9] = 2;
        policy[5][9] = 2;
        policy[6][9] = 2;
        policy[7][9] = 2;
        policy[8][9] = 2;
        policy[9][9] = 2;

        return policy;

    }


    //converts java coordinates to strange reversed upside down <col, row> coords
    private int[] ConvertCoords(int i, int j, int x, int y){
        int[] coords = new int[2];

        coords[0] = x-i;
        coords[1] = j+1;

        return  coords;
    }


    //changes the <col, row> coordinates to usable java coordinates for arrays
    private int[] UnConvertCoords(int i, int j, int x, int y){
        int[] coords = new int[2];
        coords[0] = Math.abs(i-(x));
        coords[1] = j-1;
        return coords;
    }


}

class StateActionPairs{
    public int[] state;
    public int action;
    public int freq;
}

class StateActionTriple{
    public int[] state;
    public int action;
    public int[] statePrime;
    public int freq;
}