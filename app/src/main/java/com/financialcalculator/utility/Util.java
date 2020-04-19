package com.financialcalculator.utility;

import com.financialcalculator.R;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Stack;

/**
 * Created by Rajeev Ranjan -  ABPB on 05-04-2019.
 */
public class Util {

    public static final int PRECISION = 12;

    public static long getLongDate(String dateString) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar calendar = Calendar.getInstance();
        Date date = null;
        try {
            date = simpleDateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.setTime(date);
        return calendar.getTimeInMillis();
    }

    public static String getDatefromLong(long timeStamp) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);
        final Date date = calendar.getTime();
        return simpleDateFormat.format(date);
    }

    public static int getYearMonthDate(long timeStamp, int type) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);
        return calendar.get(type);
    }

    public static int getYearMonthDate(String dateString, int type) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar calendar = Calendar.getInstance();
        try {
            Date date = simpleDateFormat.parse(dateString);
            calendar.setTime(date);
            return getYearMonthDate(calendar.getTimeInMillis(), type);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return getYearMonthDate(calendar.getTimeInMillis(), type);
    }

    public static String getNumberFormatted(String number) {

        String s = "";
        int j = 1;
        for (int i = number.length() - 1; i >= 0; i--) {

            char c = number.charAt(i);
            s = c + s;
            if (j % 3 == 0 && i != 0) {
                s = "," + s;
            }
            j++;
        }
        return s;
    }

    public static int getRandomBackground() {
        Random rand = new Random();
        int n = rand.nextInt(2);
        switch (n) {
            case 0:
                return R.drawable.circle_green;
            case 1:
                return R.drawable.circle_blue;
            case 2:
                return R.drawable.circle_red;
            default:
                return R.drawable.circle_green;
        }
    }

    public static int getFixedBackground(int position) {
        Random rand = new Random();
        int n = rand.nextInt(2);
        switch (position % 3) {
            case 0:
                return R.drawable.circle_green;
            case 1:
                return R.drawable.circle_blue;
            case 2:
                return R.drawable.circle_red;
            default:
                return R.drawable.circle_green;
        }
    }

    public static BigDecimal evaluate(String expression, HashMap<Character, BigDecimal> bigDecimals) {
        char[] tokens = expression.toCharArray();

        // Stack for numbers: 'values'
        Stack<BigDecimal> values = new Stack<BigDecimal>();

        // Stack for Operators: 'ops'
        Stack<Character> ops = new Stack<Character>();

        for (int i = 0; i < tokens.length; i++) {
            // Current token is a whitespace, skip it
            if (tokens[i] == ' ')
                continue;

            // current token is a variable ,fetch from hashmap , push it to stack for numbers
            if (tokens[i] >= 'a' && tokens[i] <= 'z') {
                values.push(bigDecimals.get(tokens[i]));
            }
            // Current token is a number, push it to stack for numbers
            else if ((tokens[i] == '.') || ((tokens[i] >= '0' && tokens[i] <= '9'))) {
                StringBuffer sbuf = new StringBuffer();
                // There may be more than one digits in number
                while (i < tokens.length && (tokens[i] == '.' || (tokens[i] >= '0' && tokens[i] <= '9'))) {
                    sbuf.append(tokens[i++]);
                }
                i--; // to handle space as it moved one char ahead to check for int character
                values.push(new BigDecimal(sbuf.toString()));
            }

            // Current token is an opening brace, push it to 'ops'
            else if (tokens[i] == '(')
                ops.push(tokens[i]);

                // Closing brace encountered, solve entire brace
            else if (tokens[i] == ')') {
                while (ops.peek() != '(')
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()));
                ops.pop();
            }

            // Current token is an operator.
            else if (tokens[i] == '+' || tokens[i] == '-' ||
                    tokens[i] == '*' || tokens[i] == '/' || tokens[i] == '^') {
                // While top of 'ops' has same or greater precedence to current
                // token, which is an operator. Apply operator on top of 'ops'
                // to top two elements in values stack
                while (!ops.empty() && hasPrecedence(tokens[i], ops.peek()))
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()));

                // Push current token to 'ops'.
                ops.push(tokens[i]);
            }
        }

        // Entire expression has been parsed at this point, apply remaining
        // ops to remaining values
        while (!ops.empty())
            values.push(applyOp(ops.pop(), values.pop(), values.pop()));

        // Top of 'values' contains result, return it

        return PRECISION == 0 ? values.pop() : values.pop().setScale(PRECISION, BigDecimal.ROUND_HALF_UP);
    }

    public static BigDecimal evaluate(String expression) {
        char[] tokens = expression.toCharArray();

        // Stack for numbers: 'values'
        Stack<BigDecimal> values = new Stack<BigDecimal>();

        // Stack for Operators: 'ops'
        Stack<Character> ops = new Stack<Character>();

        for (int i = 0; i < tokens.length; i++) {
            // Current token is a whitespace, skip it
            if (tokens[i] == ' ')
                continue;

            // Current token is a number, push it to stack for numbers
            if (tokens[i] == '.' || (tokens[i] >= '0' && tokens[i] <= '9')) {
                StringBuffer sbuf = new StringBuffer();
                // There may be more than one digits in number
                while (i < tokens.length && (tokens[i] == '.' || (tokens[i] >= '0' && tokens[i] <= '9'))) {
                    sbuf.append(tokens[i++]);
                }
                i--; // to handle space as it moved one char ahead to check for int character
                values.push(new BigDecimal(sbuf.toString()));
            }

            // Current token is an opening brace, push it to 'ops'
            else if (tokens[i] == '(')
                ops.push(tokens[i]);

                // Closing brace encountered, solve entire brace
            else if (tokens[i] == ')') {
                while (ops.peek() != '(')
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()));
                ops.pop();
            }

            // Current token is an operator.
            else if (tokens[i] == '+' || tokens[i] == '-' ||
                    tokens[i] == '*' || tokens[i] == '/' || tokens[i] == '^') {
                // While top of 'ops' has same or greater precedence to current
                // token, which is an operator. Apply operator on top of 'ops'
                // to top two elements in values stack
                while (!ops.empty() && hasPrecedence(tokens[i], ops.peek()))
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()));

                // Push current token to 'ops'.
                ops.push(tokens[i]);
            }
        }

        // Entire expression has been parsed at this point, apply remaining
        // ops to remaining values
        while (!ops.empty())
            values.push(applyOp(ops.pop(), values.pop(), values.pop()));

        // Top of 'values' contains result, return it

        return PRECISION == 0 ? values.pop() : values.pop().setScale(PRECISION, BigDecimal.ROUND_HALF_UP);
    }

    // Returns true if 'op2' has higher or same precedence as 'op1',
    // otherwise returns false.
    public static boolean hasPrecedence(char op1, char op2) {
        if (op2 == '(' || op2 == ')')
            return false;
        if ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-'))
            return false;
        if ((op1 == '^') && (op2 == '*' || op2 == '/' || op2 == '+' || op2 == '-'))
            return false;
        else
            return true;
    }

    // A utility method to apply an operator 'op' on operands 'a'
    // and 'b'. Return the result.
    public static BigDecimal applyOp(char op, BigDecimal b, BigDecimal a) {
        switch (op) {
            case '+':
                return a.add(b);
            case '-':
                return a.subtract(b);
            case '*':
                return a.multiply(b);
            case '/':
                return a.divide(b, PRECISION, RoundingMode.HALF_UP);
            case '^':
                return a.pow(b.intValue()).setScale(PRECISION, RoundingMode.HALF_UP);
        }
        return new BigDecimal(0);
    }
}
