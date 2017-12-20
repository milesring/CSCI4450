import java.util.Scanner;

public class csci4450assign3 {
    /*
    Question 1: by hand
    Question 2: k-means
    Question 3: statespace = 9 states
    pg 690 (a) grid
            array
                4 actions
    Action model slide 3 of Lec 6 "Stochastic Action model of agent"
    all actions should be .80 in4 the direction and .10 in others
    transition model (currentstate, direction, nextstate) returns probability
    reward model, hardcoded to values on pg 690 grid (a)

    value iteration equation. U[s'] utility value is hardcoded in array
    instance on slide 30 of lec 6
    epsilon = .001 gamma = .99

    policy evaluation
    k ~ 50 policy evaluation pg 657 above pseudocode

    */
    public static void main (String[] args){

            System.out.println("*************************");
            System.out.println("* CSCI 4450 Homework 3  *");
            System.out.println("* Miles Ring            *");
            System.out.println("*                       *");
            System.out.println("* 1. Question 2         *");
            System.out.println("* 2. Question 3         *");
            System.out.println("* 3. Exit               *");
            System.out.println("*************************");

            Scanner in = new Scanner(System.in);

            int choice;
            while(true) {
                choice = in.nextInt();
               if(choice == 1) {
                   csci4450assign3Q2 Q2 = new csci4450assign3Q2();
                   break;
               }
               else if(choice == 2) {
                   csci4450assign3Q3 Q3 = new csci4450assign3Q3();
                   break;
               }
               else if(choice == 3){
                   System.exit(0);
               }
            }


    }
}
