import java.util.Scanner;

public class ring_A2 {

    public static void main (String[] args){
        ring_A2_Q1 Q1;
        ring_A2_Q2 Q2;
        Scanner in = new Scanner(System.in);

        System.out.println("Select the question to run: ");
        System.out.println("1. Question 1");
        System.out.println("2. Question 2");
        int answer = in.nextInt();
        if(answer==1) {
           Q1 = new ring_A2_Q1();
        }else{
            Q2 = new ring_A2_Q2();
        }


    }
}
