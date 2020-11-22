package com.financialcalculator.utility;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.provider.Settings;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;

import com.financialcalculator.R;
import com.financialcalculator.SplashActivity;
import com.financialcalculator.banking.fd.FDCalculatorActivity;
import com.financialcalculator.banking.ppf.PPFCalculatotActivity;
import com.financialcalculator.banking.rd.RDCalculatorActivity;
import com.financialcalculator.emi.emicalculator.EmiCalculatorActivity;
import com.financialcalculator.emi.emicompare.EmiCompareActivity;
import com.financialcalculator.emi.emifixedvsreducing.FixedVsReducingActivity;
import com.financialcalculator.firebase.FirebaseHelper;
import com.financialcalculator.generic.GenericCalculatorActivity;
import com.financialcalculator.generic.WebViewActivity;
import com.financialcalculator.gst.GstCalculatorActivity;
import com.financialcalculator.gst.VatCalculatorActivity;
import com.financialcalculator.home.MainActivity;
import com.financialcalculator.loanprofile.CreateLoanProfileActivity;
import com.financialcalculator.loanprofile.HomeLoanEligibility;
import com.financialcalculator.loanprofile.ViewLoanProfile;
import com.financialcalculator.model.CalculatorEntity;
import com.financialcalculator.model.HomePageModel;
import com.financialcalculator.searchhistory.SerachHistoryACtivity;
import com.financialcalculator.sip.LumpSumpSipActivity;
import com.financialcalculator.sip.SIPCalculatorActivity;
import com.financialcalculator.sip.SIPGoalCalculatorActivity;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Stack;
import java.util.UUID;

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
                return new BigDecimal(Math.pow(a.doubleValue(), b.doubleValue())).setScale(PRECISION, RoundingMode.HALF_UP);
//            return a.pow(b).setScale(PRECISION, RoundingMode.HALF_UP);
        }
        return new BigDecimal(0);
    }

    public static void inAppRedirection(Context context, String redUrl, String title) {
        if (!TextUtils.isEmpty(redUrl)) {
            if (redUrl.startsWith("http")) {
                // open web view
                Intent intent = new Intent(context, WebViewActivity.class);
                intent.putExtra("URL", redUrl);
                intent.putExtra("TITLE", title);
                context.startActivity(intent);
            } else {
                Class<?> className = getClassName(redUrl);
                if (context instanceof MainActivity && redUrl.equalsIgnoreCase("MainActivity")) {
                    return;
                } else if (context instanceof WebViewActivity && redUrl.equalsIgnoreCase("WebViewActivity")) {
                    return;
                } else if (redUrl.equalsIgnoreCase("WebViewActivity")) {
                    Intent intent = new Intent(context, WebViewActivity.class);
                    intent.putExtra("URL", redUrl);
                    intent.putExtra("TITLE", title);
                    context.startActivity(intent);
                } else {
                    Intent intent = new Intent(context, className);
                    context.startActivity(intent);
                }
            }

        }

    }

    private static Class<?> getClassName(String redUrl) {
        switch (redUrl) {
            case "WebViewActivity":
                return WebViewActivity.class;
            case "GenericCalculatorActivity":
                return GenericCalculatorActivity.class;
            case "SplashActivity":
                return SplashActivity.class;
            case "MainActivity":
                return MainActivity.class;
            case "EmiCalculatorActivity":
                return EmiCalculatorActivity.class;
            case "EmiCompareActivity":
                return EmiCompareActivity.class;
            case "FixedVsReducingActivity":
                return FixedVsReducingActivity.class;
            case "GstCalculatorActivity":
                return GstCalculatorActivity.class;
            case "VatCalculatorActivity":
                return VatCalculatorActivity.class;
            case "FDCalculatorActivity":
                return FDCalculatorActivity.class;
            case "RDCalculatorActivity":
                return RDCalculatorActivity.class;
            case "PPFCalculatotActivity":
                return PPFCalculatotActivity.class;
            case "SIPCalculatorActivity":
                return SIPCalculatorActivity.class;
            case "SIPGoalCalculatorActivity":
                return SIPGoalCalculatorActivity.class;
            case "LumpSumpSipActivity":
                return LumpSumpSipActivity.class;
            case "SerachHistoryACtivity":
                return SerachHistoryACtivity.class;
            case "CreateLoanProfileActivity":
                return CreateLoanProfileActivity.class;
            case "ViewLoanProfile":
                return ViewLoanProfile.class;
            case "HomeLoanEligibility":
                return HomeLoanEligibility.class;

            default:
                return MainActivity.class;
        }
    }

    public static String getFormattedDouble(double d) {
        return new DecimalFormat("#").format(d);
    }

    public static String getCommaSeparated(String number) {
        String result = "";
        try {
            if (number.contains(".")) {
                result = number.length() > 12 ? bigNumberWithDecimal(new BigDecimal(number))
                        : numberWithDecimal(Double.parseDouble(number));
            } else {
                result = number.length() > 12 ? bigNumWithoutDecimal(new BigDecimal(number))
                        : numWithoutDecimal(Double.parseDouble(number));
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static String numberWithDecimal(Double price) {
        DecimalFormat formatter = new DecimalFormat("##,##,###.00");
        return formatter.format(price);
    }

    public static String numWithoutDecimal(Double price) {
        DecimalFormat formatter = new DecimalFormat("##,##,###.##");
        return formatter.format(price);
    }

    public static String bigNumberWithDecimal(BigDecimal price) {
        DecimalFormat formatter = new DecimalFormat("##,##,###.00");
        return formatter.format(price);
    }

    public static String bigNumWithoutDecimal(BigDecimal price) {
        DecimalFormat formatter = new DecimalFormat("##,##,###.##");
        return formatter.format(price);
    }

    public static String removeComma(String number) {
        return number != null ? number.replace(",", "") : "";
    }

    public static boolean isValidROI(String number) {
        try {
            //number !=null ? (Double.parseDouble(number) <= 100 && Double.parseDouble(number) > 0): false;
            return number != null && (Double.parseDouble(number) <= 100 && Double.parseDouble(number) > 0);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static String removeWord(String string, String word) {

        // Check if the word is present in string
        // If found, remove it using removeAll()
        if (string.contains(word)) {

            // To cover the case
            // if the word is at the
            // beginning of the string
            // or anywhere in the middle
            String tempWord = word + " ";
            string = string.replaceAll(tempWord, "");

            // To cover the edge case
            // if the word is at the
            // end of the string
            tempWord = " " + word;
            string = string.replaceAll(tempWord, "");
        }

        // Return the resultant string
        return string;
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    public static int getCalculatorIcon(int calculatorId) {
        switch (calculatorId) {
            case Constants.EMI_CALCULATOR:
                return R.drawable.emi_cal;
            case Constants.COMPARE_LOAN:
                return R.drawable.compare_loan_new;
            case Constants.FLAT_VS_REDUCING:
                return R.drawable.compare_icon;

            case Constants.HOME_LOAN_CALCULATOR:
                return R.drawable.emi_cal;
            case Constants.PERSONAL_LOAN_CALCULATOR:
                return R.drawable.emi_cal;
            case Constants.LAON_AGAINST_PROPERTY:
                return R.drawable.emi_cal;
            case Constants.GOLD_LOAN_CALCULATOR:
                return R.drawable.emi_cal;

            case Constants.FD_CALCULATOR:
                return R.drawable.fd;
            case Constants.RD_CALCULATOR:
                return R.drawable.emi_cal;
            case Constants.PPF_CALCULATOR:
                return R.drawable.ppf;

            case Constants.SIP_CALCULATOR:
                return R.drawable.sip_icons;
            case Constants.ADVANCE_SIP_CALCULATOR:
                return R.drawable.goal;
            case Constants.LUMPSUMP_CALCULATOR:
                return R.drawable.lumpsum;

            case Constants.GST_CALCULATOR:
                return R.drawable.gst;
            case Constants.VAT_CALCULATOR:
                return R.drawable.vat;

            case Constants.LOAN_PROFILE:
                return R.drawable.create_loan_;
            case Constants.LOAN_PROFILE_VIEW:
                return R.drawable.view_loan_;
            case Constants.HOME_LOAN_ELIGIBLE:
                return R.drawable.home_loan;

            default:
                return 0;
        }
    }

    public static String loadJSONFromAsset(Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open("dashboard.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    @SuppressLint("HardwareIds")
    public static String getUniqueDeviceId(Context context) {
        String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID((long) androidId.hashCode(), (long) androidId.hashCode());
        return deviceUuid.toString();
    }

    public static String getVersionName(Context context) {
        String versionName = "";
        PackageInfo pinfo;
        try {
            pinfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pinfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    public static int getVersionCode(Context context) {
        int versionCode = 0;
        PackageInfo pinfo;
        try {
            pinfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pinfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    public static void get() {

    }

    public static boolean isRunning(Context ctx) {
        ActivityManager activityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (ActivityManager.RunningTaskInfo task : tasks) {
            if (ctx.getPackageName().equalsIgnoreCase(task.baseActivity.getPackageName()))
                return true;
        }

        return false;
    }

    public static List<HomePageModel> parseDashboardListFirebase() {
        List<HomePageModel> homePageModels = new ArrayList<>();
        Gson gson = new Gson();
        try {
            JSONObject jsonObject = new JSONObject(FirebaseHelper.getDashboardList());
            Iterator<String> keys = jsonObject.keys();
            HomePageModel homePageModel;
            while (keys.hasNext()) {
                String key = keys.next();
                String values = jsonObject.get(key).toString();
                homePageModel = gson.fromJson(values, HomePageModel.class);
                if (homePageModel != null) {
                    homePageModels.add(homePageModel);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return homePageModels;
    }

    public static SpannableStringBuilder evaluateString(String inputString, CalculatorEntity calculatorEntity) {
        HashMap<Character, BigDecimal> bigDecimals = calculatorEntity.getInputHashmap();
        HashMap<Character, String> spinnerHashmap = calculatorEntity.getSpinnerHashMap();
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        String temp = "";
        for (int i = 0; i < inputString.length(); i++) {
            switch (inputString.charAt(i)) {
                case '$':
                    i = i + 1;
                    if (Character.isUpperCase(inputString.charAt(i))) {
                        temp = spinnerHashmap.get(inputString.charAt(i));
                    } else {
                        temp = bigDecimals.get(inputString.charAt(i)).setScale(0, 0).toPlainString();
                    }
                    //temp = bigDecimals.get(inputString.charAt(i)).setScale(0, 0).toPlainString();
                    ssb.append(temp);
                    //ssb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), ssb.length() - temp.length(), ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    //ssb.setSpan(new ForegroundColorSpan(Color.parseColor("#ff000000")), ssb.length() - temp.length(), ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                case '@':
                    i = i + 1;
                    String formulae = "";
                    while (inputString.charAt(i) != '@') {
                        formulae = formulae + inputString.charAt(i);
                        i++;
                    }
                    temp = evaluate(formulae, bigDecimals).setScale(0, 0).toPlainString();
                    ssb.append(temp);
                    //ssb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), ssb.length() - temp.length(), ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    //ssb.setSpan(new ForegroundColorSpan(Color.parseColor("#ff000000")), ssb.length() - temp.length(), ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    //result = result + evaluate(formulae, bigDecimals).setScale(0);
                    //ssb.setSpan(new ForegroundColorSpan(Color.parseColor("#3F51B5")), i, bigDecimals.get(inputString.charAt(i)).setScale(0).toPlainString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                default:
                    ssb.append(inputString.charAt(i));
                    //result = result + inputString.charAt(i);
                    break;
            }
        }
        return ssb;
    }
}
