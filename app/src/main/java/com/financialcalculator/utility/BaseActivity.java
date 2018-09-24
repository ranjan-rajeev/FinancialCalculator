package com.financialcalculator.utility;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.financialcalculator.R;

import java.text.DecimalFormat;

/**
 * Created by Rohit on 12/12/15.
 */
public class BaseActivity extends AppCompatActivity {

    ProgressDialog dialog;

    public void dialogExit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit");
        builder.setMessage("Do you want to exit the application?");
        builder.setCancelable(false);

        builder.setPositiveButton(
                "YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        finish();
                    }
                });

        builder.setNegativeButton(
                "NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog exitdialog = builder.create();
        exitdialog.show();

        Button negative = exitdialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        Button positive = exitdialog.getButton(DialogInterface.BUTTON_POSITIVE);
        negative.setTextColor(getResources().getColor(R.color.header_light_text));
        positive.setTextColor(getResources().getColor(R.color.black));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public static String getFormattedDouble(double d) {
        return new DecimalFormat("#").format(d);
    }
    public static String getFormattedDoubleUpToDecimal(double d) {
        return new DecimalFormat("#.##").format(d);
    }
    //region all neccessary functions
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    protected void cancelDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    protected void showDialog() {
//        dialog = ProgressDialog.show(BaseActivity.this, "", "Loading...", true);
        showDialog("Loading...");
    }

    protected void showDialog(String msg) {
        if (dialog == null)
            dialog = ProgressDialog.show(BaseActivity.this, "", msg, true);
        else {
            if (!dialog.isShowing())
                dialog = ProgressDialog.show(BaseActivity.this, "", msg, true);
        }
    }

    public void sendSms(String mobNumber) {
        try {
            mobNumber = mobNumber.replaceAll("\\s", "");
            mobNumber = mobNumber.replaceAll("\\+", "");
            mobNumber = mobNumber.replaceAll("-", "");
            mobNumber = mobNumber.replaceAll(",", "");
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", mobNumber, null)));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Invalid Number", Toast.LENGTH_SHORT).show();
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

    public static boolean isValidPan(EditText editText) {
        String panNo = "[A-Z]{5}[0-9]{4}[A-Z]{1}";
        String panNoAlter = editText.getText().toString().toUpperCase();
        return !(panNoAlter.isEmpty() || !panNoAlter.matches(panNo));
    }

    public static boolean isValidAadhar(EditText editText) {
        String aadharPattern = "[0-9]{12}";
        String aadharNo = editText.getText().toString().toUpperCase();
        return !(aadharNo.isEmpty() || !aadharNo.matches(aadharPattern));
    }

    public static void hideKeyBoard(View view, Context context) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void sharePdfTowhatsApp(String pdfFileName) {
        /*try {
            File outputFile = new File(Environment.getExternalStorageDirectory(), "/FINMART/QUOTES/" + pdfFileName + ".pdf");

            Uri uri = FileProvider.getUriForFile(BaseActivity.this,
                    getString(R.string.file_provider_authority),
                    outputFile);
            //Uri uri = Uri.fromFile(outputFile);

            Intent share = new Intent();
            share.setAction(Intent.ACTION_SEND);
            share.setType("application/pdf");
            share.putExtra(Intent.EXTRA_STREAM, uri);
            share.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            //share.setPackage("com.whatsapp");
            Intent intent = Intent.createChooser(share, "Share Quote");
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }


    public void showAlert(String strBody) {
        try {
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(BaseActivity.this);
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
            Toast.makeText(this, "Please try again..", Toast.LENGTH_SHORT).show();
        }
    }

    //endregion
}

