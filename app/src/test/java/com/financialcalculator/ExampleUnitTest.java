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
        //decimalHashMap.put('r', new BigDecimal(0.02833).setScale(Util.PRECISION, BigDecimal.ROUND_HALF_UP));
        decimalHashMap.put('m', new BigDecimal(850000).setScale(Util.PRECISION, BigDecimal.ROUND_HALF_UP));
        decimalHashMap.put('n', new BigDecimal(40).setScale(Util.PRECISION, BigDecimal.ROUND_HALF_UP));
        //decimalHashMap.put('c', new BigDecimal(1).setScale(Util.PRECISION, BigDecimal.ROUND_HALF_UP));
        decimalHashMap.put('f', new BigDecimal(12).setScale(Util.PRECISION, BigDecimal.ROUND_HALF_UP));

        String formulae  =  "  ( m * ( 7.89  / ( f * 100 ) ) )  / ( ( 1 + (7.89 / (f * 100 ) ) ) * ( ( ( 1  + ( 7.89 / (f * 100 ) ) ) ^ ( (60 -n) * f ) -1 ) ) ) ";
        String part1  = " ( m * ( 7.89  / ( f * 100 ) ) )";
        String part2  = " ( 1 + (7.89 / (f * 100 ) ) )";
        String part3  =  "( ( ( 1  + ( 7.89 / (f * 100 ) ) ) ^ ( 60 -n ) -1 ) )";


        //assertEquals(new BigDecimal(1117.75).setScale(Util.PRECISION,BigDecimal.ROUND_HALF_UP), Util.evaluate(part1, decimalHashMap));
        //assertEquals(new BigDecimal(1.006575).setScale(Util.PRECISION,BigDecimal.ROUND_HALF_UP), Util.evaluate(part2, decimalHashMap));
        //assertEquals(new BigDecimal(0.316854734973).setScale(Util.PRECISION,BigDecimal.ROUND_HALF_UP), Util.evaluate(part3, decimalHashMap));

        assertEquals(new BigDecimal(1117.75).setScale(Util.PRECISION,BigDecimal.ROUND_HALF_UP), Util.evaluate(formulae, decimalHashMap));

        /* //String formulae = " ( m * r ) / ( c + r ) * ( ( ( c + r ) ^ n ) -  c)";
        //String formulae = " ( m * r ) / ( ( c + r ) * ( ( ( c + r ) ^ n ) -  c ) )";


        String formulae1 = "(100+9/3)";
        String formulae2 = " ( ( 1 + 10 ) * ( ( ( 1 + 10 ) ^ 1 ) -  1 ) )";
        String formulae3 = "(100*10)/((1+10)*(((1+10)^1)-1))";*/


        //assertEquals(new BigDecimal(14000).setScale(Util.PRECISION, BigDecimal.ROUND_HALF_UP), Util.evaluate(formulae1, decimalHashMap));
        //assertEquals(new BigDecimal(532.58).setScale(Util.PRECISION, BigDecimal.ROUND_HALF_UP), Util.evaluate(formulae, decimalHashMap));
        //assertEquals(new BigDecimal(532.58).setScale(Util.PRECISION, BigDecimal.ROUND_HALF_UP), Util.evaluate(formulae, decimalHashMap));
    }
}