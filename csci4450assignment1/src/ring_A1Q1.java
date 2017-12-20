import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

/*

Written by Miles Ring on 9/6/2017 for CSCI4450 Intro to AI

!NOTE to Dr. Dasgupta!: I treated the "blank" space or the '0' in the puzzle as the moving tile.
                        Compared to your example given, my answer will be opposite of yours.
                        Example:    4 3 1   -> "UP"(Down for your example) -> 4 0 1
                                    5 0 2                                     5 3 2

Problem:
A 5 puzzle is a 3 X 2 grid with 5 tiles numbered 1...5 and an empty tile as shown in the figures below.

    4 3 1           1 2 3
    5 0 2           4 5 0
    Random State    Goal State

A tile next to the empty space can be moved (left, right, up or down but not diagonally) into the empty space.
The objective is to have the arrangement of tiles shown in the goal state.

For simplifying the problem the empty space is considered as tile 0.
A state can be represented by the traversing the tiles row-wise (or row-major) starting from the top-left corner.
So the state of the left hand figure is represented as 431502 while the goal state is 123450.
An action at each state would be one of L(left), R(right), U(up) or D(down).
For example, the action left on the random state given above would move tile ‘2’ to its left into the empty space.
Therefore, L(431502) would give the state 431520. Similarly, D(431502) (move tile 3 down) would give the state 401532.
Notice that each state will have two possible actions if the empty space (tile 0) is at one of the corner squares and three possible actions if the empty space (tile 0) is in one of the middle squares.
The cost of each action is the same.
Write a program to find a solution to the 5-puzzle starting from a given state (that you will input) using iterative deepening search(IDS).
While inputting the start state you should keep in mind that some states are never possible or invalid because the goal state is unreachable from those states.
For e.g. 132450 is an invalid state for the above start/goal state.
For testing the correctness of your program you can use the start state given in the example above.
The action sequence D-L-U-R-R-D-L-L-U leads to the goal state.

 */

public class ring_A1Q1 {

    ArrayList<String> explored;
    LinkedList<Character> answer;
    final String solution = "123450";
    Scanner in;
    char[] dirs = {'L','R','U','D'};

    public ring_A1Q1(){
        in = new Scanner(System.in);
        explored = new ArrayList<>();
        answer = new LinkedList<>();

        System.out.println("Input starting state(XXXXXX, where\nX corresponds to a digit between 0 and 5): ");
        String puzzle = in.next();
        System.out.println("The Goal State is "+solution);
        solve(puzzle);

    }

    public void solve(String puzzle){
        //BEGIN IDS ALGORITHM TO SOLVE
        IDS(puzzle);

        for(int i=0;i<answer.size();++i){
            System.out.print(answer.get(i));
            if(i!=answer.size()-1){
                System.out.print("-");
            }
        }
        System.out.println();
    }


    public String IDS(String puzzle){
        String result;

        for(int i=0;;++i){
            result = DLS(puzzle, i);
            if(!result.equals("cutoff")){
                return result;
            }
        }

        //return "failure";
    }

    public String DLS(String puzzle, int limit){

        return RDLS(puzzle, limit);

    }

    public String RDLS(String state, int limit){

        //System.out.println("Exploring state: "+state);

        if(goalTest(state)){
            //System.out.println("Goal state found");
            return state;
        }
        else if (limit == 0){
            return "cutoff";
        }
        else{
            boolean cutoff_occurred = false;
            for(char dir:dirs) {
              String child =  makeNode(state, dir);
              if(explored.contains(state)){
                  continue;
              }
              String result = RDLS(child, limit-1);
              if(result.equals("cutoff")){
                  cutoff_occurred = true;
              }
              else if( !result.equals("failure")){
                  explored.add(state);
                  answer.addFirst(dir);
                  return result;
              }
            }
            return cutoff_occurred ? "cutoff" : "failure";
        }





    }

    public String makeNode(String state, char dir){
        /*State represented by a String in a 3 x 2 table
        i.e. Goal state
               1 2 3
               4 5 0
        */

        //converts to char array for manipulation
        char[] stateChar = state.toCharArray();

        //checks where the "blank" spot in the puzzle is
        int i = 0;
        for(;i<state.length();++i){
            if(state.charAt(i) == '0') {
                break;
            }
        }

        //creates new state based on the puzzle option given
        switch(dir) {
            case 'L':
                if(i == 0 || i == 3){
                    return state;
                }
                else{
                    char tmp = stateChar[i - 1];
                    stateChar[i - 1] = stateChar[i];
                    stateChar[i] = tmp;
                }
                break;
            case 'R':
                if(i == 2 || i == 5){
                    return state;
                }
                else{
                    char tmp = stateChar[i + 1];
                    stateChar[i + 1] = stateChar[i];
                    stateChar[i] = tmp;
                }
                break;
            case 'U':
                if(i >= 0 && i<= 2){
                    return state;
                }
                else{
                    char tmp = stateChar[i - 3];
                    stateChar[i - 3] = stateChar[i];
                    stateChar[i] = tmp;
                }
                break;
            case 'D':
                if(i >= 3 && i <= 5){
                    return state;
                }
                else{
                    char tmp = stateChar[i + 3];
                    stateChar[i + 3] = stateChar[i];
                    stateChar[i] = tmp;
                }
                break;
            default:
                break;
        }

        return new String(stateChar);
    }
    public boolean goalTest(String state){
        return solution.equals(state);

    }
}
