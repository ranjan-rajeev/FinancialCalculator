package com.financialcalculator;

import com.financialcalculator.utility.Util;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void evaluate_correct_expression() throws Exception {
        assertEquals(1024.976, Util.evaluate("( 1 ^ 10 )"));
    }

    @Test
    public void evaluate_withvalues_correct_expression() {
        HashMap<Character, BigDecimal> decimalHashMap = new HashMap<>();
        decimalHashMap.put('r', new BigDecimal(0.0066667).setScale(Util.PRECISION, BigDecimal.ROUND_HALF_UP));
        decimalHashMap.put('m', new BigDecimal(850000).setScale(Util.PRECISION, BigDecimal.ROUND_HALF_UP));
        decimalHashMap.put('t', new BigDecimal(420).setScale(Util.PRECISION, BigDecimal.ROUND_HALF_UP));
        decimalHashMap.put('c', new BigDecimal(1).setScale(Util.PRECISION, BigDecimal.ROUND_HALF_UP));


        String formulae = "(m*r) / ( (c+r) * (((c+r)^t)-c) )";
        String formulae1 = "(m*r)";
        String formulae2 = "(((c+r)^t)-c)";
        String formulae3 = "( (c+r) * (((c+r)^n)-c) )";
        String formulae4 = "(m*7.89/1200) / ( (1+7.89/1200) * (( (1+7.89/1200)^t)-1) )";


       /* //String formulae = " ( m * r ) / ( c + r ) * ( ( ( c + r ) ^ n ) -  c)";
        //String formulae = " ( m * r ) / ( ( c + r ) * ( ( ( c + r ) ^ n ) -  c ) )";


        String formulae1 = "(100+9/3)";
        String formulae2 = " ( ( 1 + 10 ) * ( ( ( 1 + 10 ) ^ 1 ) -  1 ) )";
        String formulae3 = "(100*10)/((1+10)*(((1+10)^1)-1))";


        String formulae5 = "((1+10)*(((1+10)^1)-1))";
        // assertEquals(1024.976, Util.evaluate(formulae, decimalHashMap));
        //assertEquals(new BigDecimal(1000).setScale(2, BigDecimal.ROUND_HALF_UP), Util.evaluate(formulae1));
        //assertEquals(new BigDecimal(110).setScale(2,BigDecimal.ROUND_HALF_UP), Util.evaluate(formulae2));
        //assertEquals(new BigDecimal(110).setScale(2,BigDecimal.ROUND_HALF_UP), Util.evaluate(formulae3));
        assertEquals(new BigDecimal(110).setScale(2, BigDecimal.ROUND_HALF_UP), Util.evaluate(formulae5));
        assertEquals(new BigDecimal(110).setScale(2, BigDecimal.ROUND_HALF_UP), Util.evaluate(formulae3));*/

        //assertEquals(new BigDecimal(14000).setScale(Util.PRECISION, BigDecimal.ROUND_HALF_UP), Util.evaluate(formulae1, decimalHashMap));
        //assertEquals(new BigDecimal(532.58).setScale(Util.PRECISION, BigDecimal.ROUND_HALF_UP), Util.evaluate(formulae, decimalHashMap));
        assertEquals(new BigDecimal(532.58).setScale(Util.PRECISION, BigDecimal.ROUND_HALF_UP), Util.evaluate(formulae4, decimalHashMap));
    }
}