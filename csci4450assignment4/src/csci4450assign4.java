import java.util.Scanner;

public class csci4450assign4 {

    public static void main (String[] args){

            System.out.println("*************************");
            System.out.println("* CSCI 4450 Homework 4  *");
            System.out.println("* Miles Ring            *");
            System.out.println("*                       *");
            System.out.println("* 1. Question 1         *");
            System.out.println("* 2. Question 2         *");
            System.out.println("* 3. Exit               *");
            System.out.println("*************************");

            Scanner in = new Scanner(System.in);

            int choice;
            while(true) {
                choice = in.nextInt();
               if(choice == 1) {
                   csci4450assign4Q1 Q1 = new csci4450assign4Q1();
                   break;
               }
               else if(choice == 2) {
                   //csci4450assign4Q2 Q2 = new csci4450assign4Q2();
                   break;
               }
               else if(choice == 3){
                   System.exit(0);
               }
            }


    }
}
