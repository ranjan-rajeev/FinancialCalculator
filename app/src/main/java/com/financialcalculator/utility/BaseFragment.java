package com.financialcalculator.utility;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.widget.EditText;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Rohit on 17/01/16.
 */
public class BaseFragment extends Fragment {
    ProgressDialog dialog;

    public BaseFragment() {

    }

    protected void cancelDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    public static String getFormattedDouble(double d) {
        return new DecimalFormat("#").format(d);
    }

    protected void showDialog() {
        showDialog("Loading...");
    }

    protected void showDialog(String msg) {
        if (dialog == null)
            dialog = ProgressDialog.show(getActivity(), "", msg, true);
        else {
            if (!dialog.isShowing())
                dialog = ProgressDialog.show(getActivity(), "", msg, true);
        }

    }


    public static boolean isValidePhoneNumber(EditText editText) {
        String phoneNumberPattern = "^(?:(?:\\+|0{0,2})91(\\s*[\\-]\\s*)?|[0]?)?[789]\\d{9}$";
        String phoneNumberEntered = editText.getText().toString().trim();
        return !(phoneNumberEntered.isEmpty() || !phoneNumberEntered.matches(phoneNumberPattern));
    }

    public static boolean isValideEmailID(EditText editText) {
        String emailEntered = editText.getText().toString().trim();
        return !(emailEntered.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(emailEntered).matches());
    }

    public static boolean isEmpty(EditText editText) {
        String text = editText.getText().toString().trim();
        return !(text.isEmpty());
    }

    public static boolean validatePhoneNumber(EditText editText) {
        String phoneNumberPattern = "^(?:(?:\\+|0{0,2})91(\\s*[\\-]\\s*)?|[0]?)?[789]\\d{9}$";
        String phoneNumberEntered = editText.getText().toString().trim();
        if (phoneNumberEntered.isEmpty() || !phoneNumberEntered.matches(phoneNumberPattern)) {
            return false;
        }
        return true;
    }

    public static boolean isValidPan(String Pan) {
//        String rx = "/[A-Z]{5}[0-9]{4}[A-Z]{1}$/";
        Pattern pattern = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]{1}");
        Matcher matcher = pattern.matcher(Pan);
        if (matcher.matches()) {
            return true;

        } else {
            return false;
        }
    }


    public void showAlert(String strBody) {
        try {
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
            builder.setTitle("Finmart");

            builder.setMessage(strBody);
            String positiveText = "Ok";
            builder.setPositiveButton(positiveText,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    });
            final android.support.v7.app.AlertDialog dialog = builder.create();
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        } catch (Exception ex) {
            Toast.makeText(getActivity(), "Please try again..", Toast.LENGTH_SHORT).show();
        }
    }
}
