import java.util.ArrayList;
import java.util.Scanner;


public class ring_A1Q2 {
    /* TO DO

        implement loop portion of A*


     */


    /* Written by Miles Ring on *INSERT DATE* for CSCI4450 Intro to AI

    Problem:
    The 8-puzzle is comprises of tiles numbered 0..8 in a 3 X 3 grid, as shown alongside.
    Formulate the 8 puzzle problem in a manner similar to the 5-puzzle problem given in question 1,
    that is, define a representation of a state, the possible actions from each state,
    the goal test and the step cost.

    0 1 2
    3 4 5
    6 7 8
    Goal State

    a) Write a program to search for the goal state shown in Figure 3.4 (right),
    starting from a random state in the 8 puzzle using A* search with the heuristic function
    h(n) = number of tiles that are not in the correct place in state ‘n’.

    b) Define the Manhattan distance of a tile
    M(tile) =  Number of moves needed to get the tile from its position in the current state
    to its position in the goal state. Write a program to search for a goal state starting
    from a random state in the 8 puzzle using A* search using
    the heuristic function h(n) = Sum of Manhattan distances of each tile in state ‘n’.

    For parts a) and b) assume that g(n) is the cost from the start state to state ‘n’.
    Use the same format of input and output given in Question 1.
     */


    final String solution = "012345678";
    Scanner in;
    char[] dirs = {'L','R','U','D'};
    ArrayList<ring_A1Q2_Node> successors;

    public ring_A1Q2(){
        in = new Scanner(System.in);
        successors = new ArrayList<>();
        solve();
    }

    public void solve(){
        System.out.println("Input starting state(XXXXXXXX, where\nX corresponds to a digit between 0 and 8): ");
        String puzzle = in.next();
        System.out.println("The Goal State is "+solution);

        Recursive_BFS(buildNode(new ring_A1Q2_Node(puzzle)));

    }



    public ring_A1Q2_Node Recursive_BFS(ring_A1Q2_Node node){
        System.out.println(node.h+" tiles out of place");

        return RBFS(node, 100000);
    }

    public ring_A1Q2_Node RBFS(ring_A1Q2_Node node, int f_limit){
        if(goalTest(node.state)) {
            System.out.println("goal state found");
            return node;
        }
        successors.clear();
        for (char dir:dirs) {
            boolean contains = false;
           ring_A1Q2_Node temp = makeNode(node.state, dir);
           temp = buildNode(temp);

           //searching for duplicate successors
           for(int i=0;i<successors.size();++i){
               if(successors.get(i).state.equals(temp.state)){
                   contains = true;
               }
           }

           if(!contains) {
               successors.add(temp);
           }
        }

        System.out.println("printing succ");
        for(int i=0;i<successors.size();++i){
            System.out.println(successors.get(i).state);
        }

        if(successors.isEmpty()){
            return new ring_A1Q2_Node("failure");
        }

        for (ring_A1Q2_Node s:successors){
            s.f = Math.max(s.h, node.f);
        }

        /*do{
        //best <- lowest f-val node in succ
        //if best.f > f_limit then return falure, best.f
        //alternative <- the second-lowest f-value among succ
        //result, best.f <- RBFS(best,min(f_limit,alternative))
        //if result!=failure then return result

        }while(true);
        */
        return node;

    }
    public ring_A1Q2_Node makeNode(String state, char dir){
        /*State represented by a String in a 3 x 3 table
        i.e. Goal state
               0 1 2
               3 4 5
               6 7 8
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
                if(i == 0 || i == 3 || i == 6){
                    return new ring_A1Q2_Node(state);
                }
                else{
                    char tmp = stateChar[i - 1];
                    stateChar[i - 1] = stateChar[i];
                    stateChar[i] = tmp;
                }
                break;
            case 'R':
                if(i == 2 || i == 5 || i == 8){
                    return new ring_A1Q2_Node(state);
                }
                else{
                    char tmp = stateChar[i + 1];
                    stateChar[i + 1] = stateChar[i];
                    stateChar[i] = tmp;
                }
                break;
            case 'U':
                if(i >= 0 && i<= 2){
                    return new ring_A1Q2_Node(state);
                }
                else{
                    char tmp = stateChar[i - 3];
                    stateChar[i - 3] = stateChar[i];
                    stateChar[i] = tmp;
                }
                break;
            case 'D':
                if(i >= 6 && i <= 8){
                    return new ring_A1Q2_Node(state);
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

        return new ring_A1Q2_Node(new String(stateChar));
    }
    public boolean goalTest(String state){
        return solution.equals(state);

    }

    public ring_A1Q2_Node buildNode(ring_A1Q2_Node node){
        node.h = 0;

        //converts to char array for comparing
        char[] stateChar = node.state.toCharArray();

        for(int i=0;i<node.state.length();++i){
            if(Integer.toString(i).toCharArray()[0] != stateChar[i]){
                node.h++;
            }
        }

        return node;
    }


}