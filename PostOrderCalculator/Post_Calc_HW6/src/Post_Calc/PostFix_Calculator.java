package Post_Calc;

import java.util.*;

public class PostFix_Calculator {
     Stack<Integer> myCalculator;
     private static final String ADD = "+";
     private static final String SUB = "-";
     private static final String MUL = "*";
     private static final String DIV = "/";

     PostFix_Calculator()  {
          myCalculator = new Stack<Integer>();
     }

     int calculateExpression(String expression) throws Exception {
          String[] parameters;// = new String[expression.length()/2 + 1];
          parameters = expression.split("[ \t]");
          int operand1, operand2;

          for (String parameter : parameters) {
               if(!Objects.equals(parameter, ""))
                    if (parameter.equals(ADD) || parameter.equals(SUB) || parameter.equals(MUL) || parameter.equals(DIV)) {
                         operand2 = myCalculator.pop();
                         operand1 = myCalculator.pop();
                         switch (parameter) {
                              case ADD -> {
                                   int local = operand1 + operand2;
                                   myCalculator.push(local);
                                   break;
                              }
                              case SUB -> {
                                   int local = operand1 - operand2;
                                   myCalculator.push(local);
                                   break;
                              }
                              case MUL -> {
                                   int local = operand1 * operand2;
                                   myCalculator.push(local);
                                   break;
                              }
                              case DIV -> {
                                   if(operand2 == 0)
                                        throw new Exception("Zero division");
                                   int local = operand1 / operand2;
                                   myCalculator.push(local);
                                   break;
                              }
                         }
                    } else {
                         myCalculator.push(Integer.parseInt(parameter));
                    }
               }
          if(myCalculator.size() > 1)
               throw new Exception("Malformed expression");
          return myCalculator.pop();
     }

}
