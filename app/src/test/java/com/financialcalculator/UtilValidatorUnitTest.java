package com.financialcalculator;

import com.financialcalculator.utility.Util;
import com.financialcalculator.utility.ValidationUtil;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class UtilValidatorUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void formatting_large_number() throws Exception {
        assertEquals("12,347,347,365,463,636,347,823,646", Util.getCommaSeparated("12347347365463636347823646"));
    }
    @Test
    public void formatting_decimal() throws Exception {
        assertEquals("3,457,634,751,234.89", Util.getCommaSeparated("3457634751234.89"));
    }
    @Test
    public void formatting_wrong_number() throws Exception {
        assertEquals("", Util.getCommaSeparated("3457634er5"));
    }
    @Test
    public void formatting_empty_number() throws Exception {
        assertEquals("", Util.getCommaSeparated(""));
    }
    @Test
    public void formatting_zero_after_dot_number() throws Exception {
        assertEquals("3,457,634,751,234.00", Util.getCommaSeparated("3457634751234.00000"));
    }

    @Test
    public void remove_coma_from_number_with_coma() throws Exception {
        assertEquals("3457634751234.00", Util.removeComma("3,457,634,751,234.00"));
    }

    @Test
    public void remove_coma_from_number_without_coma() throws Exception {
        assertEquals("3457634751234.00", Util.removeComma("3457634751234.00"));
    }
    @Test
    public void remove_coma_from_null_number() throws Exception {
        assertEquals("", Util.removeComma(null));
    }

    @Test
    public void check_roi_lessthan_100() throws Exception {
        assertEquals(true, ValidationUtil.isValidROI("99.999"));
    }

    @Test
    public void check_roi_greaterthan_100() throws Exception {
        assertEquals(false, ValidationUtil.isValidROI("100.8"));
    }
    @Test
    public void check_roi_equal_0() throws Exception {
        assertEquals(false, ValidationUtil.isValidROI("0.00"));
    }
    @Test
    public void check_roi_equal_100() throws Exception {
        assertEquals(true, ValidationUtil.isValidROI("100.00"));
    }
    @Test
    public void check_roi_equal_null() throws Exception {
        assertEquals(false, ValidationUtil.isValidROI(null));
    }
}