import java.util.Scanner;

public class Main {
    public static String ERROR = "ERROR";

    public static void main(String[] args) {
        Scanner myScanner = new Scanner(System.in);
        String expression;
        while (myScanner.hasNextLine()) {
            expression = myScanner.nextLine();
            try {
                if (!expression.equals("")) {
                    System.out.printf("%.5f%n", ExpressionEvaluator.calculateExpression(expression));
                }
            } catch (Exception ex) {
                System.out.println(ERROR);
                ExpressionEvaluator.last = (double) 0;
            }
        }
        myScanner.close();
    }
}