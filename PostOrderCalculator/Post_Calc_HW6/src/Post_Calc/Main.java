package Post_Calc;

import java.util.Scanner;

public class Main {
    public static void main(String[] args)

    {
        Scanner input = new Scanner(System.in);
        PostFix_Calculator mainCalculator = new PostFix_Calculator();
        while (input.hasNext()) {
            String myInput = input.nextLine();
            if(!myInput.isBlank()) {
                //if(myInput.)
                //System.out.println(myInput);
                try {
                    int result = mainCalculator.calculateExpression(myInput);
                    System.out.println(result);
                } catch (Exception ex) {
                    if (ex.getClass() == NumberFormatException.class)
                        System.out.println("Malformed expression");
                    else {
                        if (ex.getMessage() == "Zero division")
                            System.out.println(ex.getMessage());
                        else
                            System.out.println("Malformed expression");
                    }
                }
            }
        }
    }
}