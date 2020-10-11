package com.financialcalculator;

import com.financialcalculator.utility.Util;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class CalculatorUnitTest {

    @Test
    public void evaluate_atal_pension_calculator() {


        HashMap<Character, BigDecimal> decimalHashMap = new HashMap<>();
        //decimalHashMap.put('r', new BigDecimal(0.02833).setScale(Util.PRECISION, BigDecimal.ROUND_HALF_UP));
        decimalHashMap.put('m', new BigDecimal(850000).setScale(Util.PRECISION, BigDecimal.ROUND_HALF_UP));
        decimalHashMap.put('n', new BigDecimal(40).setScale(Util.PRECISION, BigDecimal.ROUND_HALF_UP));
        //decimalHashMap.put('c', new BigDecimal(1).setScale(Util.PRECISION, BigDecimal.ROUND_HALF_UP));
        decimalHashMap.put('f', new BigDecimal(12).setScale(Util.PRECISION, BigDecimal.ROUND_HALF_UP));

        String formulae = "  ( m * ( 7.89  / ( f * 100 ) ) )  / ( ( 1 + (7.89 / (f * 100 ) ) ) * ( ( ( 1  + ( 7.89 / (f * 100 ) ) ) ^ ( (60 -n) * f ) -1 ) ) ) ";
        String part1 = " ( m * ( 7.89  / ( f * 100 ) ) )";
        String part2 = " ( 1 + (7.89 / (f * 100 ) ) )";
        String part3 = "( ( ( 1  + ( 7.89 / (f * 100 ) ) ) ^ ( 60 -n ) -1 ) )";


        //assertEquals(new BigDecimal(1117.75).setScale(Util.PRECISION,BigDecimal.ROUND_HALF_UP), Util.evaluate(part1, decimalHashMap));
        //assertEquals(new BigDecimal(1.006575).setScale(Util.PRECISION,BigDecimal.ROUND_HALF_UP), Util.evaluate(part2, decimalHashMap));
        //assertEquals(new BigDecimal(0.316854734973).setScale(Util.PRECISION,BigDecimal.ROUND_HALF_UP), Util.evaluate(part3, decimalHashMap));

        assertEquals(new BigDecimal(1117.75).setScale(Util.PRECISION, BigDecimal.ROUND_HALF_UP), Util.evaluate(formulae, decimalHashMap));

        /* //String formulae = " ( m * r ) / ( c + r ) * ( ( ( c + r ) ^ n ) -  c)";
        //String formulae = " ( m * r ) / ( ( c + r ) * ( ( ( c + r ) ^ n ) -  c ) )";


        String formulae1 = "(100+9/3)";
        String formulae2 = " ( ( 1 + 10 ) * ( ( ( 1 + 10 ) ^ 1 ) -  1 ) )";
        String formulae3 = "(100*10)/((1+10)*(((1+10)^1)-1))";*/


        //assertEquals(new BigDecimal(14000).setScale(Util.PRECISION, BigDecimal.ROUND_HALF_UP), Util.evaluate(formulae1, decimalHashMap));
        //assertEquals(new BigDecimal(532.58).setScale(Util.PRECISION, BigDecimal.ROUND_HALF_UP), Util.evaluate(formulae, decimalHashMap));
        //assertEquals(new BigDecimal(532.58).setScale(Util.PRECISION, BigDecimal.ROUND_HALF_UP), Util.evaluate(formulae, decimalHashMap));
    }

    @Test
    public void evaluate_CAGR_calculator() {
        // p = Beginning Value Of Investment
        //m  = Ending Value Of Investment
        //n =  Years Of Investment

        HashMap<Character, BigDecimal> decimalHashMap = new HashMap<>();
        decimalHashMap.put('p', new BigDecimal(123456789).setScale(Util.PRECISION, BigDecimal.ROUND_HALF_UP));
        decimalHashMap.put('m', new BigDecimal(1234567890).setScale(Util.PRECISION, BigDecimal.ROUND_HALF_UP));
        decimalHashMap.put('n', new BigDecimal(5).setScale(Util.PRECISION, BigDecimal.ROUND_HALF_UP));
        //  CAGR = [(Ending value/Beginning Value)^(1/N)]-1
        // res = ( (  (   ( m /p ) ^  ( 1 / n)    ) - 1  ) *  100 )

        String formulae = "(((( m / p ) ^ ( 1 / n) ) - 1 ) * 100 )";
        assertEquals(new BigDecimal(58.489319246100).setScale(Util.PRECISION, BigDecimal.ROUND_HALF_UP), Util.evaluate(formulae, decimalHashMap));
    }

    @Test
    public void evaluate_NPS_calculator() {
        //

        HashMap<Character, BigDecimal> decimalHashMap = new HashMap<>();
        decimalHashMap.put('p', new BigDecimal(123456789).setScale(Util.PRECISION, BigDecimal.ROUND_HALF_UP));
        decimalHashMap.put('m', new BigDecimal(1234567890).setScale(Util.PRECISION, BigDecimal.ROUND_HALF_UP));
        decimalHashMap.put('n', new BigDecimal(5).setScale(Util.PRECISION, BigDecimal.ROUND_HALF_UP));
        //  CAGR = [(Ending value/Beginning Value)^(1/N)]-1
        // res = ( (  (   ( m /p ) ^  ( 1 / n)    ) - 1  ) *  100 )

        String formulae = "(((( m / p ) ^ ( 1 / n) ) - 1 ) * 100 )";
        assertEquals(new BigDecimal(58.489319246100).setScale(Util.PRECISION, BigDecimal.ROUND_HALF_UP), Util.evaluate(formulae, decimalHashMap));
    }

    @Test
    public void evaluate_GRATUITY_calculator() {
        HashMap<Character, BigDecimal> decimalHashMap = new HashMap<>();
        decimalHashMap.put('p', new BigDecimal(123456789).setScale(Util.PRECISION, BigDecimal.ROUND_HALF_UP));
        decimalHashMap.put('m', new BigDecimal(1234567890).setScale(Util.PRECISION, BigDecimal.ROUND_HALF_UP));
        decimalHashMap.put('n', new BigDecimal(5).setScale(Util.PRECISION, BigDecimal.ROUND_HALF_UP));
        //  CAGR = [(Ending value/Beginning Value)^(1/N)]-1
        // res = ( (  (   ( m /p ) ^  ( 1 / n)    ) - 1  ) *  100 )

        String formulae = "(((( m / p ) ^ ( 1 / n) ) - 1 ) * 100 )";
        assertEquals(new BigDecimal(58.489319246100).setScale(Util.PRECISION, BigDecimal.ROUND_HALF_UP), Util.evaluate(formulae, decimalHashMap));
    }

    @Test
    public void evaluate_expression_calculator() {
        HashMap<Character, BigDecimal> decimalHashMap = new HashMap<>();
        decimalHashMap.put('p', new BigDecimal(111111).setScale(Util.PRECISION, BigDecimal.ROUND_HALF_UP));
        decimalHashMap.put('m', new BigDecimal(222222).setScale(Util.PRECISION, BigDecimal.ROUND_HALF_UP));
        decimalHashMap.put('n', new BigDecimal(5).setScale(Util.PRECISION, BigDecimal.ROUND_HALF_UP));

        String test = "Sum of $p and $m is : @p + m + 100 -100 @";
        assertEquals("Sum of 111111 and 222222 is : 333333", Util.evaluateString(test, decimalHashMap));
    }


}