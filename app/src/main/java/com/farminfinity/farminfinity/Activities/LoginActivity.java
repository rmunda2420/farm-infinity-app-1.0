package com.farminfinity.farminfinity.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;
import com.farminfinity.farminfinity.App.AppController;
import com.farminfinity.farminfinity.Helper.AlertDialogManager;
import com.farminfinity.farminfinity.Helper.EndPoints;
import com.farminfinity.farminfinity.Helper.InputValidation;
import com.farminfinity.farminfinity.Helper.MyLocation;
import com.farminfinity.farminfinity.Helper.SessionManager;
import com.farminfinity.farminfinity.R;
import com.farminfinity.farminfinity.SplashScreen;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.android.play.core.tasks.Task;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private final AppCompatActivity activity = LoginActivity.this;
    private String userType = "farmer";
    private CoordinatorLayout coordinatorLayout;

    private EditText etUsername;
    private EditText etPassword;

    private CardView cvBtnSignUp;
    private TextView tvCvBtnText;
    private TextView tvlblFpoReg;
    private TextView tvlblAgentReg;
    private TextView tvlblForgotPassword;
    private TextView tvlblResendOtp;
    private TextView tvlblPrivacyPolicyLink;

    private ImageView ivCam;

    private RadioGroup radGroupRole;

    private CardView cvBtnLogin;

    private InputValidation mInputValidation;
    private AlertDialogManager mAlert;
    private SessionManager mSessionManager;

    private static final int REQ_CODE_VERSION_UPDATE = 330;
    private AppUpdateManager appUpdateManager;
    private InstallStateUpdatedListener installStateUpdatedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final String textFpo = getResources().getString(R.string.lbl_fpo);
        final String textAgent = getResources().getString(R.string.lbl_agent);
        final String text2 = getResources().getString(R.string.forgot_pswd);
        final String text3 = getResources().getString(R.string.resend_otp);
        final String textPPlink = getResources().getString(R.string.lbl_privacy_policy_link);

        initObjects();
        checkForAppUpdate();
        // if already logged in go directly to home
        if (mSessionManager.isLoggedIn()) {
            HashMap<String, String> userInfo = mSessionManager.getUserDetails();
            String userType = userInfo.get("UserType");

            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            i.putExtra("USER_TYPE", userType);
            startActivity(i);
            finish();
        }

        initViews();
        initListeners();

        // Update UI
        radGroupRole.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radBtn_client) {
                    userType = "farmer"; // important
                    cvBtnSignUp.setVisibility(View.VISIBLE);
                    tvlblForgotPassword.setVisibility(View.GONE);
                    etPassword.setVisibility(View.GONE);
                    etUsername.setHint(getResources().getString(R.string.hnt_et_username));
                    etPassword.setHint(getResources().getString(R.string.hnt_et_otp));
                    etPassword.setEnabled(false);
                    etUsername.setInputType(InputType.TYPE_CLASS_PHONE);
                    tvCvBtnText.setText("Send OTP");
                    etUsername.setText(null);
                    etPassword.setText(null);
                } else {
                    userType = "officer"; //important
                    cvBtnSignUp.setVisibility(View.GONE);
                    etPassword.setVisibility(View.VISIBLE);
                    tvlblForgotPassword.setVisibility(View.VISIBLE);
                    etUsername.setHint(getResources().getString(R.string.hnt_username));
                    etPassword.setHint(getResources().getString(R.string.hnt_password));
                    etPassword.setEnabled(true);
                    etUsername.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                    etUsername.setText(null);
                    etPassword.setText(null);
                    tvCvBtnText.setText("Login");
                    tvlblResendOtp.setVisibility(View.GONE);
                }
            }
        });

        ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent browserIntent1 = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.app.farminfinity.com/fpo-fpc"));
                startActivity(browserIntent1);
            }
        };
        ClickableSpan clickableSpan2 = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent browserIntent2 = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.app.farminfinity.com/field-agent"));
                startActivity(browserIntent2);
            }
        };

        ClickableSpan clickableSpan5 = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent browserIntent3 = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.privacy_policy_url)));
                startActivity(browserIntent3);
            }
        };
        ClickableSpan clickableSpan6 = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent browserIntent4 = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.terms_url)));
                startActivity(browserIntent4);
            }
        };
        // Forgot password code
        ClickableSpan clickableSpan3 = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                final AlertDialog.Builder customAlertDialog1 = new AlertDialog.Builder(LoginActivity.this);
                customAlertDialog1.setCancelable(true);
                View customView1 = LayoutInflater.from(LoginActivity.this).inflate(R.layout.custom_alert_dialog_forgot_password_one, null, false);

                final TextInputLayout tilUserId = (TextInputLayout) customView1.findViewById(R.id.til_userid_custom_alert_dialog_forgot_password_one);
                final TextInputLayout tilRegPhone = (TextInputLayout) customView1.findViewById(R.id.til_reg_phone_custom_alert_dialog_forgot_password_one);

                final TextInputEditText tietUserId = (TextInputEditText) customView1.findViewById(R.id.tiet_userid_custom_alert_dialog_reset_password_one);
                final TextInputEditText tietRegPhone = (TextInputEditText) customView1.findViewById(R.id.tiet_reg_phone_custom_alert_dialog_forgot_password_one);

                CardView cvSendOtp = (CardView) customView1.findViewById(R.id.cv_btn_send_otp_custom_alert_dialog_forgot_password_one);

                customAlertDialog1.setView(customView1);
                final AlertDialog alertDialog1 = customAlertDialog1.create();
                alertDialog1.show();

                cvSendOtp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!mInputValidation.isInputEditTextFilled(tietUserId, tilUserId, getString(R.string.error_message_blank_field))) {
                            return;
                        }
                        if (!mInputValidation.isInputEditTextFilled(tietRegPhone, tilRegPhone, getString(R.string.error_message_blank_field))) {
                            return;
                        }
                        if (tietRegPhone.getText().toString().trim().length() != 10) {
                            mAlert.showAlertDialog(LoginActivity.this, "Validation error", getResources().getString(R.string.error_message_invalid_phone_no), false);
                            return;
                        }
                        final String login_id = tietUserId.getText().toString().trim();
                        final String phoneNumber = tietRegPhone.getText().toString().trim();

                        if (phoneNumber.length() == 10) {
                            // Split user id
                            Matcher m = Pattern.compile("[^0-9]*([0-9]+).*").matcher(login_id);
                            String url1 = "";
                            String userIdInitial = "";
                            if (m.matches()) {
                                userIdInitial = login_id.substring(0, login_id.indexOf(m.group(1)));
                            }
                            switch (userIdInitial) {
                                case "FIE":
                                    url1 = EndPoints.URL_EMP_FORGOT_PASSWORD_SEND_OTP;
                                    break;
                                case "FIFA":
                                    url1 = EndPoints.URL_AGENT_FORGOT_PASSWORD_SEND_OTP;
                                    break;
                                case "FPO":
                                    url1 = EndPoints.URL_FPO_FORGOT_PASSWORD_SEND_OTP;
                                    break;
                                case "FIBR":
                                    url1 = EndPoints.URL_BANK_REP_FORGOT_PASSWORD_SEND_OTP;
                                    break;
                                default:
                                    Toast.makeText(LoginActivity.this, "Invalid userid!", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                            if (url1.isEmpty())
                                return;

                            // Tag used to cancel the request
                            String tag_json_obj = "json_obj_req";

                            final Snackbar snackbar = Snackbar
                                    .make(coordinatorLayout, "Please wait..", Snackbar.LENGTH_INDEFINITE);

                            snackbar.show();

                            JSONObject jsonBody = new JSONObject();
                            try {
                                jsonBody.put("reg_ph_no", phoneNumber);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            // Send request for otp
                            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, url1, jsonBody,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            try {
                                                // Parsing json object response
                                                boolean isSuccess = response.getBoolean("success");
                                                String message = response.getString("msg");
                                                if (isSuccess) {
                                                    snackbar.dismiss();
                                                    alertDialog1.dismiss();
                                                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                                                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.msg_enter_otp), Toast.LENGTH_SHORT).show();
                                                    // Move to otp verification dialog
                                                    final AlertDialog.Builder customAlertDialog2 = new AlertDialog.Builder(LoginActivity.this);
                                                    customAlertDialog2.setCancelable(false);
                                                    View customView2 = LayoutInflater.from(LoginActivity.this).inflate(R.layout.custom_alert_dialog_forgot_password_two, null, false);

                                                    final TextView tvResendOtp = (TextView) customView2.findViewById(R.id.tv_lbl_resend_otp_custom_alert_dialog_forgot_password_two);

                                                    final TextInputLayout tilOtp = (TextInputLayout) customView2.findViewById(R.id.til_otp_custom_alert_dialog_forgot_password_two);

                                                    final TextInputEditText tietOtp = (TextInputEditText) customView2.findViewById(R.id.tiet_otp_custom_alert_dialog_forgot_password_two);

                                                    CardView cvVerifyOtp = (CardView) customView2.findViewById(R.id.cv_btn_verify_otp_custom_alert_dialog_forgot_password_two);

                                                    customAlertDialog2.setView(customView2);
                                                    final AlertDialog alertDialog2 = customAlertDialog2.create();
                                                    alertDialog2.show();

                                                    ClickableSpan clickableSpanResendOtp = new ClickableSpan() {
                                                        @Override
                                                        public void onClick(View widget) {
                                                            // Split user id
                                                            Matcher m = Pattern.compile("[^0-9]*([0-9]+).*").matcher(login_id);
                                                            String url2 = "";
                                                            String userIdInitial = "";
                                                            if (m.matches()) {
                                                                userIdInitial = login_id.substring(0, login_id.indexOf(m.group(1)));
                                                            }
                                                            switch (userIdInitial) {
                                                                case "FIE":
                                                                    url2 = EndPoints.URL_EMP_FORGOT_PASSWORD_SEND_OTP;
                                                                    break;
                                                                case "FIFA":
                                                                    url2 = EndPoints.URL_AGENT_FORGOT_PASSWORD_SEND_OTP;
                                                                    break;
                                                                case "FPO":
                                                                    url2 = EndPoints.URL_FPO_FORGOT_PASSWORD_SEND_OTP;
                                                                    break;
                                                                case "FIBR":
                                                                    url2 = EndPoints.URL_BANK_REP_FORGOT_PASSWORD_SEND_OTP;
                                                                    break;
                                                                default:
                                                                    Toast.makeText(LoginActivity.this, "Invalid userid!", Toast.LENGTH_SHORT).show();
                                                                    break;
                                                            }
                                                            if (url2.isEmpty())
                                                                return;
                                                            // Tag used to cancel the request
                                                            String tag_json_obj = "json_obj_req";

                                                            final Snackbar snackbar = Snackbar
                                                                    .make(coordinatorLayout, "Please wait..", Snackbar.LENGTH_INDEFINITE);

                                                            snackbar.show();

                                                            JSONObject jsonBody = new JSONObject();
                                                            try {
                                                                jsonBody.put("reg_ph_no", phoneNumber);
                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                            }
                                                            // Send request to resend otp
                                                            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, url2, jsonBody,
                                                                    new Response.Listener<JSONObject>() {
                                                                        @Override
                                                                        public void onResponse(JSONObject response) {
                                                                            try {
                                                                                // Parsing json object response
                                                                                boolean isSuccess = response.getBoolean("success");
                                                                                String message = response.getString("msg");
                                                                                if (isSuccess) {
                                                                                    snackbar.dismiss();
                                                                                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                                                                                    //Toast.makeText(LoginActivity.this,  getResources().getString(R.string.msg_enter_otp), Toast.LENGTH_SHORT).show();
                                                                                    //alertDialog2.dismiss();
                                                                                }

                                                                            } catch (JSONException e) {
                                                                                snackbar.dismiss();
                                                                                e.printStackTrace();
                                                                            }
                                                                        }
                                                                    }, new Response.ErrorListener() {
                                                                @Override
                                                                public void onErrorResponse(VolleyError error) {
                                                                    snackbar.dismiss();

                                                                    NetworkResponse networkResponse = error.networkResponse;
                                                                    String errorMessage = "Unknown error";
                                                                    if (networkResponse == null) {
                                                                        if (error.getClass().equals(TimeoutError.class)) {
                                                                            errorMessage = "Request timeout";
                                                                        } else if (error.getClass().equals(NoConnectionError.class)) {
                                                                            errorMessage = "Failed to connect server";
                                                                        } else if (error.getClass().equals(NetworkError.class)) {
                                                                            errorMessage = "Check your internet connection";
                                                                        }
                                                                    } else {
                                                                        String result = new String(networkResponse.data);
                                                                        try {
                                                                            JSONObject response = new JSONObject(result);
                                                                            String message = response.getString("msg");

                                                                            Log.e("Error Message", message);

                                                                            if (networkResponse.statusCode == 404) {
                                                                                errorMessage = "Resource not found";
                                                                            } else if (networkResponse.statusCode == 401) {
                                                                                errorMessage = message + "";
                                                                            } else if (networkResponse.statusCode == 400) {
                                                                                errorMessage = message + "";
                                                                            } else if (networkResponse.statusCode == 500) {
                                                                                errorMessage = message + " Something is getting wrong";
                                                                            }
                                                                        } catch (JSONException e) {
                                                                            e.printStackTrace();
                                                                        }
                                                                    }
                                                                    Log.i("Error", errorMessage);
                                                                    error.printStackTrace();
                                                                    Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show();
                                                                    //tvCvBtnText.setText("Resend");
                                                                }
                                                            }) {
                                                                @Override
                                                                protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                                                                    // since we don't know which of the two underlying network vehicles
                                                                    // will Volley use, we have to handle and store session cookies manually
                                                                    AppController.getInstance().checkSessionCookie(response.headers);

                                                                    return super.parseNetworkResponse(response);
                                                                }

                                                                @Override
                                                                public Map<String, String> getHeaders() throws AuthFailureError {
                                                                    Map<String, String> headers = new HashMap<>();
                                                                    //String credentials = phoneNumber + ":" + otp;
                                                                    //String auth = "Basic "
                                                                    //+ Base64.encodeToString(credentials.getBytes(),
                                                                    //Base64.NO_WRAP);
                                                                    // Adding cookie
                                                                    AppController.getInstance().addSessionCookie(headers);
                                                                    String AppAccessKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJGSUFQUDEwMSIsImlhdCI6MTUxNjIzOTAyMn0._jKrqO1xcUoKoguQgzhhbB3yML0GfsNTo7Mm14Bw0Bo"; // Production
                                                                    //String AppAccessKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJGSUFQUDEwMSIsImlhdCI6MTUxNjIzOTAyMn0.mbwX1OXEXaY3BQoa5QPHRlBYKNgFQUpaf4wxqciEMuU";
                                                                    headers.put("app-access-token", AppAccessKey);
                                                                    //headers.put("Authorization", auth);

                                                                    return headers;
                                                                }
                                                            };

                                                            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(40 * 1000, 0,
                                                                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                                                            // Adding request to request queue
                                                            AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
                                                        }
                                                    };

                                                    SpannableString spannableString4 = new SpannableString(text3);
                                                    spannableString4.setSpan(clickableSpanResendOtp, 0, 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                    tvResendOtp.setText(spannableString4);
                                                    tvResendOtp.setMovementMethod(LinkMovementMethod.getInstance());

                                                    cvVerifyOtp.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            if (!mInputValidation.isInputEditTextFilled(tietOtp, tilOtp, getString(R.string.error_message_blank_field))) {
                                                                return;
                                                            }
                                                            // Tag used to cancel the request
                                                            final String otp = tietOtp.getText().toString().trim();
                                                            // Split user id
                                                            Matcher m = Pattern.compile("[^0-9]*([0-9]+).*").matcher(login_id);
                                                            String url3 = "";
                                                            String userIdInitial = "";
                                                            if (m.matches()) {
                                                                userIdInitial = login_id.substring(0, login_id.indexOf(m.group(1)));
                                                            }
                                                            switch (userIdInitial) {
                                                                case "FIE":
                                                                    url3 = EndPoints.URL_EMP_FORGOT_PASSWORD_VERIFY_OTP;
                                                                    break;
                                                                case "FIFA":
                                                                    url3 = EndPoints.URL_AGENT_FORGOT_PASSWORD_VERIFY_OTP;
                                                                    break;
                                                                case "FPO":
                                                                    url3 = EndPoints.URL_FPO_FORGOT_PASSWORD_VERIFY_OTP;
                                                                    break;
                                                                case "FIBR":
                                                                    url3 = EndPoints.URL_BANK_REP_FORGOT_PASSWORD_VERIFY_OTP;
                                                                    break;
                                                                default:
                                                                    Toast.makeText(LoginActivity.this, "Invalid userid!", Toast.LENGTH_SHORT).show();
                                                                    break;
                                                            }
                                                            if (url3.isEmpty())
                                                                return;

                                                            String tag_json_obj = "json_obj_req";

                                                            final Snackbar snackbar = Snackbar
                                                                    .make(coordinatorLayout, "Connecting..", Snackbar.LENGTH_INDEFINITE);

                                                            snackbar.show();

                                                            JSONObject jsonBody = new JSONObject();
                                                            try {
                                                                jsonBody.put("reg_ph_no", phoneNumber);
                                                                jsonBody.put("otp", Integer.parseInt(otp));
                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                            }
                                                            //Request to verify otp
                                                            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                                                                    url3, jsonBody, new Response.Listener<JSONObject>() {

                                                                @Override
                                                                public void onResponse(JSONObject response) {
                                                                    try {
                                                                        // Parsing json object response
                                                                        boolean isSuccess = response.getBoolean("success");
                                                                        final String accessToken1 = response.getString("jwt");
                                                                        String message = response.getString("msg");

                                                                        if (isSuccess) {
                                                                            snackbar.dismiss();
                                                                            alertDialog2.dismiss();
                                                                            // Open the set password alert dialogue box
                                                                            AlertDialog.Builder customAlertDialog3 = new AlertDialog.Builder(LoginActivity.this);
                                                                            customAlertDialog3.setCancelable(false);
                                                                            View customView3 = LayoutInflater.from(LoginActivity.this).inflate(R.layout.custom_alert_dialog_forgot_password_three, null, false);

                                                                            final TextInputLayout tilNewPassword = (TextInputLayout) customView3.findViewById(R.id.til_new_password_custom_alert_dialog_reset_password_three);
                                                                            final TextInputLayout tilConfirmNewPassword = (TextInputLayout) customView3.findViewById(R.id.til_confirm_new_password_custom_alert_dialog_reset_password_three);

                                                                            final TextInputEditText tietNewPassword = (TextInputEditText) customView3.findViewById(R.id.tiet_new_password_custom_alert_dialog_rest_password_three);
                                                                            final TextInputEditText tietConfirmNewPassword = (TextInputEditText) customView3.findViewById(R.id.tiet_confirm_new_password_custom_alert_dialog_reset_password_three);

                                                                            CardView cvSave = (CardView) customView3.findViewById(R.id.cv_btn_save_custom_alert_dialog_forgot_password_three);

                                                                            customAlertDialog3.setView(customView3);
                                                                            final AlertDialog alertDialog3 = customAlertDialog3.create();
                                                                            alertDialog3.show();

                                                                            cvSave.setOnClickListener(new View.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(View view) {
                                                                                    // Update password
                                                                                    if (!(tietNewPassword.getText().toString().trim().length() > 7 && tietNewPassword.getText().toString().trim().length() < 17)) {
                                                                                        mAlert.showAlertDialog(LoginActivity.this, "Validation error", getResources().getString(R.string.error_msg_pswd_len), false);
                                                                                        return;
                                                                                    }
                                                                                    if (!mInputValidation.isInputEditTextFilled(tietNewPassword, tilNewPassword, getString(R.string.error_message_blank_field))) {
                                                                                        return;
                                                                                    }
                                                                                    if (!mInputValidation.isInputEditTextFilled(tietConfirmNewPassword, tilConfirmNewPassword, getString(R.string.error_message_blank_field))) {
                                                                                        return;
                                                                                    }
                                                                                    if (!tietNewPassword.getText().toString().trim().equals(tietConfirmNewPassword.getText().toString().trim())) {
                                                                                        mAlert.showAlertDialog(LoginActivity.this, "Validation error", getResources().getString(R.string.error_msg_pswd_match), false);
                                                                                        return;
                                                                                    }
                                                                                    final String newPassword = tietNewPassword.getText().toString().trim();

                                                                                    // Split user id
                                                                                    Matcher m = Pattern.compile("[^0-9]*([0-9]+).*").matcher(login_id);
                                                                                    String url4 = "";
                                                                                    String userIdInitial = "";
                                                                                    if (m.matches()) {
                                                                                        userIdInitial = login_id.substring(0, login_id.indexOf(m.group(1)));
                                                                                    }
                                                                                    switch (userIdInitial) {
                                                                                        case "FIE":
                                                                                            url4 = EndPoints.URL_EMP_FORGOT_PASSWORD;
                                                                                            break;
                                                                                        case "FIFA":
                                                                                            url4 = EndPoints.URL_AGENT_FORGOT_PASSWORD;
                                                                                            break;
                                                                                        case "FPO":
                                                                                            url4 = EndPoints.URL_FPO_FORGOT_PASSWORD;
                                                                                            break;
                                                                                        case "FIBR":
                                                                                            url4 = EndPoints.URL_BANK_REP_FORGOT_PASSWORD;
                                                                                            break;
                                                                                        default:
                                                                                            Toast.makeText(LoginActivity.this, "Invalid userid!", Toast.LENGTH_SHORT).show();
                                                                                            break;
                                                                                    }
                                                                                    if (url4.isEmpty())
                                                                                        return;

                                                                                    // Tag used to cancel the request
                                                                                    String tag_json_obj = "json_obj_req";

                                                                                    JSONObject jsonBody = new JSONObject();
                                                                                    try {
                                                                                        jsonBody.put("new_password", newPassword);
                                                                                        jsonBody.put("changed_password", true);
                                                                                    } catch (JSONException e) {
                                                                                        e.printStackTrace();
                                                                                    }
                                                                                    // Request to update password
                                                                                    JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT, url4, jsonBody,
                                                                                            new Response.Listener<JSONObject>() {
                                                                                                @Override
                                                                                                public void onResponse(JSONObject response) {
                                                                                                    try {
                                                                                                        // Parsing json object response
                                                                                                        boolean isSuccess = response.getBoolean("success");
                                                                                                        String message = response.getString("msg");
                                                                                                        if (isSuccess) {
                                                                                                            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                                                                                                            alertDialog3.dismiss();
                                                                                                        }
                                                                                                    } catch (JSONException e) {
                                                                                                        e.printStackTrace();
                                                                                                    }
                                                                                                }
                                                                                            }, new Response.ErrorListener() {
                                                                                        @Override
                                                                                        public void onErrorResponse(VolleyError error) {
                                                                                            NetworkResponse networkResponse = error.networkResponse;
                                                                                            String errorMessage = "Unknown error";
                                                                                            if (networkResponse == null) {
                                                                                                if (error.getClass().equals(TimeoutError.class)) {
                                                                                                    errorMessage = "Request timeout";
                                                                                                } else if (error.getClass().equals(NoConnectionError.class)) {
                                                                                                    errorMessage = "Failed to connect server";
                                                                                                } else if (error.getClass().equals(NetworkError.class)) {
                                                                                                    errorMessage = "Check your internet connection";
                                                                                                }
                                                                                            } else {
                                                                                                String result = new String(networkResponse.data);
                                                                                                try {
                                                                                                    JSONObject response = new JSONObject(result);
                                                                                                    String message = response.getString("msg");

                                                                                                    Log.e("Error Message", message);

                                                                                                    if (networkResponse.statusCode == 404) {
                                                                                                        errorMessage = "Resource not found";
                                                                                                    } else if (networkResponse.statusCode == 401) {
                                                                                                        errorMessage = message + " Please login again";
                                                                                                    } else if (networkResponse.statusCode == 400) {
                                                                                                        errorMessage = message + " Check your inputs";
                                                                                                    } else if (networkResponse.statusCode == 500) {
                                                                                                        errorMessage = message + " Something is getting wrong";
                                                                                                    }
                                                                                                } catch (JSONException e) {
                                                                                                    e.printStackTrace();
                                                                                                }
                                                                                            }
                                                                                            Log.i("Error", errorMessage);
                                                                                            error.printStackTrace();
                                                                                            Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                                                                        }
                                                                                    }) {
                                                                                        @Override
                                                                                        public Map<String, String> getHeaders() throws AuthFailureError {
                                                                                            Map<String, String> headers = new HashMap<>();
                                                                                            headers.put("x-access-token", accessToken1);
                                                                                            headers.put("Content-Type", "application/json");
                                                                                            return headers;
                                                                                        }
                                                                                    };

                                                                                    jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                                                                                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                                                                                    // Adding request to request queue
                                                                                    AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
                                                                                }
                                                                            });
                                                                        }

                                                                    } catch (JSONException e) {
                                                                        e.printStackTrace();
                                                                        snackbar.dismiss();
                                                                    }
                                                                }
                                                            }, new Response.ErrorListener() {

                                                                @Override
                                                                public void onErrorResponse(VolleyError error) {
                                                                    snackbar.dismiss();

                                                                    NetworkResponse networkResponse = error.networkResponse;
                                                                    String errorMessage = "Unknown error";
                                                                    if (networkResponse == null) {
                                                                        if (error.getClass().equals(TimeoutError.class)) {
                                                                            errorMessage = "Request timeout";
                                                                        } else if (error.getClass().equals(NoConnectionError.class)) {
                                                                            errorMessage = "Failed to connect server";
                                                                        } else if (error.getClass().equals(NetworkError.class)) {
                                                                            errorMessage = "Check your internet connection";
                                                                        }
                                                                    } else {
                                                                        String result = new String(networkResponse.data);
                                                                        try {
                                                                            JSONObject response = new JSONObject(result);
                                                                            String message = response.getString("msg");

                                                                            Log.e("Error Message", message);

                                                                            if (networkResponse.statusCode == 404) {
                                                                                errorMessage = "Resource not found";
                                                                            } else if (networkResponse.statusCode == 401) {
                                                                                errorMessage = message + "";
                                                                            } else if (networkResponse.statusCode == 400) {
                                                                                errorMessage = message + "";
                                                                            } else if (networkResponse.statusCode == 500) {
                                                                                errorMessage = message + " Something is getting wrong";
                                                                            }
                                                                        } catch (JSONException e) {
                                                                            e.printStackTrace();
                                                                        }
                                                                    }
                                                                    Log.i("Error", errorMessage);
                                                                    error.printStackTrace();
                                                                    Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show();
                                                                    //tvCvBtnText.setText("Resend");
                                                                }
                                                            }) {
                                                                @Override
                                                                protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                                                                    // since we don't know which of the two underlying network vehicles
                                                                    // will Volley use, we have to handle and store session cookies manually
                                                                    AppController.getInstance().checkSessionCookie(response.headers);

                                                                    return super.parseNetworkResponse(response);
                                                                }

                                                                @Override
                                                                public Map<String, String> getHeaders() throws AuthFailureError {
                                                                    Map<String, String> headers = new HashMap<>();
                                                                    //String credentials = phoneNumber + ":" + otp;
                                                                    //String auth = "Basic "
                                                                    //+ Base64.encodeToString(credentials.getBytes(),
                                                                    //Base64.NO_WRAP);
                                                                    // Adding cookie
                                                                    AppController.getInstance().addSessionCookie(headers);
                                                                    String AppAccessKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJGSUFQUDEwMSIsImlhdCI6MTUxNjIzOTAyMn0._jKrqO1xcUoKoguQgzhhbB3yML0GfsNTo7Mm14Bw0Bo";// Production
                                                                    //String AppAccessKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJGSUFQUDEwMSIsImlhdCI6MTUxNjIzOTAyMn0.mbwX1OXEXaY3BQoa5QPHRlBYKNgFQUpaf4wxqciEMuU";
                                                                    headers.put("app-access-token", AppAccessKey);
                                                                    //headers.put("Authorization", auth);

                                                                    return headers;
                                                                }
                                                            };

                                                            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(40 * 1000, 0,
                                                                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                                                            // Adding request to request queue
                                                            AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
                                                        }
                                                    });
                                                }

                                            } catch (JSONException e) {
                                                snackbar.dismiss();
                                                e.printStackTrace();
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    snackbar.dismiss();

                                    NetworkResponse networkResponse = error.networkResponse;
                                    String errorMessage = "Unknown error";
                                    if (networkResponse == null) {
                                        if (error.getClass().equals(TimeoutError.class)) {
                                            errorMessage = "Request timeout";
                                        } else if (error.getClass().equals(NoConnectionError.class)) {
                                            errorMessage = "Failed to connect server";
                                        } else if (error.getClass().equals(NetworkError.class)) {
                                            errorMessage = "Check your internet connection";
                                        }
                                    } else {
                                        String result = new String(networkResponse.data);
                                        try {
                                            JSONObject response = new JSONObject(result);
                                            String message = response.getString("msg");

                                            Log.e("Error Message", message);

                                            if (networkResponse.statusCode == 404) {
                                                errorMessage = "Resource not found";
                                            } else if (networkResponse.statusCode == 401) {
                                                errorMessage = message + "";
                                            } else if (networkResponse.statusCode == 400) {
                                                errorMessage = message + "";
                                            } else if (networkResponse.statusCode == 500) {
                                                errorMessage = message + " Something is getting wrong";
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    Log.i("Error", errorMessage);
                                    error.printStackTrace();
                                    Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show();
                                    //tvCvBtnText.setText("Resend");
                                }
                            }) {
                                @Override
                                protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                                    // since we don't know which of the two underlying network vehicles
                                    // will Volley use, we have to handle and store session cookies manually
                                    AppController.getInstance().checkSessionCookie(response.headers);

                                    return super.parseNetworkResponse(response);
                                }

                                @Override
                                public Map<String, String> getHeaders() throws AuthFailureError {
                                    Map<String, String> headers = new HashMap<>();
                                    // Adding cookie
                                    AppController.getInstance().addSessionCookie(headers);
                                    String AppAccessKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJGSUFQUDEwMSIsImlhdCI6MTUxNjIzOTAyMn0._jKrqO1xcUoKoguQgzhhbB3yML0GfsNTo7Mm14Bw0Bo"; // Production
                                    //String AppAccessKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJGSUFQUDEwMSIsImlhdCI6MTUxNjIzOTAyMn0.mbwX1OXEXaY3BQoa5QPHRlBYKNgFQUpaf4wxqciEMuU";
                                    headers.put("app-access-token", AppAccessKey);

                                    return headers;
                                }
                            };

                            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(40 * 1000, 0,
                                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                            // Adding request to request queue
                            AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
                        } else {
                            mAlert.showAlertDialog(LoginActivity.this, "Login failed..", getResources().getString(R.string.error_message_invalid_phone_no), false);
                        }
                    }
                });
            }
        };

        ClickableSpan clickableSpan4 = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                SendOtp(etUsername.getText().toString().trim());
            }
        };

        SpannableString spannableStringFpo = new SpannableString(textFpo);
        spannableStringFpo.setSpan(clickableSpan1, 0, textFpo.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvlblFpoReg.setText(spannableStringFpo);
        tvlblFpoReg.setMovementMethod(LinkMovementMethod.getInstance());

        SpannableString spannableStringAgent = new SpannableString(textAgent);
        spannableStringAgent.setSpan(clickableSpan2, 0, textAgent.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvlblAgentReg.setText(spannableStringAgent);
        tvlblAgentReg.setMovementMethod(LinkMovementMethod.getInstance());

        SpannableString spannableStringTermsPolicy = new SpannableString(textPPlink);
        spannableStringTermsPolicy.setSpan(clickableSpan5, 32, 46, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringTermsPolicy.setSpan(clickableSpan6, 51, 72, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvlblPrivacyPolicyLink.setText(spannableStringTermsPolicy);
        tvlblPrivacyPolicyLink.setMovementMethod(LinkMovementMethod.getInstance());

        SpannableString spannableString3 = new SpannableString(text2);
        spannableString3.setSpan(clickableSpan3, 0, text2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvlblForgotPassword.setText(spannableString3);
        tvlblForgotPassword.setMovementMethod(LinkMovementMethod.getInstance());

        SpannableString spannableString4 = new SpannableString(text3);
        spannableString4.setSpan(clickableSpan4, 0, text3.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvlblResendOtp.setText(spannableString4);
        tvlblResendOtp.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void initViews() {
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.cl_activity_login);

        etUsername = (EditText) findViewById(R.id.et_username_activity_login);
        etPassword = (EditText) findViewById(R.id.et_password_activity_login);
        ivCam = (ImageView) findViewById(R.id.iv_cam_activity_login);

        cvBtnSignUp = (CardView) findViewById(R.id.cv_btn_signup_activity_login);
        tvCvBtnText = (TextView) findViewById(R.id.tv_cv_btn_txt_activity_login);
        tvlblFpoReg = (TextView) findViewById(R.id.tv_lbl_fpo_reg_activity_login);
        tvlblAgentReg = (TextView) findViewById(R.id.tv_lbl_agent_reg_activity_login);
        tvlblPrivacyPolicyLink = (TextView) findViewById(R.id.tv_lbl_privacy_activity_login);
        tvlblForgotPassword = (TextView) findViewById(R.id.tv_lbl_forgot_pswd_activity_login);
        tvlblResendOtp = (TextView) findViewById(R.id.tv_lbl_resend_otp_activity_login);

        radGroupRole = (RadioGroup) findViewById(R.id.radioGroup_role_login_activity);

        cvBtnLogin = (CardView) findViewById(R.id.cv_btn_login_activity_login);
    }

    private void initObjects() {
        mAlert = new AlertDialogManager();
        mInputValidation = new InputValidation(LoginActivity.this);
        mSessionManager = new SessionManager(activity);
    }

    private void initListeners() {
        cvBtnLogin.setOnClickListener(this);
        cvBtnSignUp.setOnClickListener(this);
        ivCam.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cv_btn_login_activity_login:
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                //int selectedId = radGroupRole.getCheckedRadioButtonId();
                //RadioButton radioRoleButton = (RadioButton) findViewById(selectedId);
                //String userType = radioRoleButton.getText().toString().toLowerCase().trim();

                if (tvCvBtnText.getText().equals("Login")) {
                    if (username.trim().length() > 0 && password.trim().length() > 0) {
                        if (userType.equals("officer")) {
                            verifyEmployee(username, password, userType);
                        }
                    } else {
                        mAlert.showAlertDialog(LoginActivity.this, "Login failed..", getResources().getString(R.string.err_msg_login_officer), false);
                    }

                } else if (tvCvBtnText.getText().equals("Send OTP")) {
                    //Get OTP
                    String phoneNumber = etUsername.getText().toString().trim();
                    SendOtp(phoneNumber);
                } else if (tvCvBtnText.getText().equals("Verify")) {
                    String phoneNumber = etUsername.getText().toString().trim();
                    String otp = etPassword.getText().toString().trim();
                    VerifyOtp(phoneNumber, otp, userType);
                } else if (tvCvBtnText.getText().equals("Resend")) {
                    String phoneNumber = etUsername.getText().toString().trim();
                    SendOtp(phoneNumber);
                }
                break;
            case R.id.cv_btn_signup_activity_login:
                Intent i = new Intent(LoginActivity.this, SignupNewActivity.class);
                startActivity(i);
                break;
            case R.id.iv_cam_activity_login:
                //mAlert.showAlertDialog(LoginActivity.this, "Coming soon..","Capture photo with geo tag",false);
                //Intent intentCaptureActivity = new Intent(LoginActivity.this, GeoTagCapture.class);
                //startActivity(intentCaptureActivity);
                /*String packageName = "com.derekr.NoteCam";
                Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
                if (intent != null) {
                    startActivity(intent);
                } else {
                    try {
                        // if play store installed, open play store, else open browser
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName));
                    } catch (ActivityNotFoundException anfe) {
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName));
                    }
                    startActivity(intent);
                }*/
                Intent intent = new Intent(LoginActivity.this, CameraActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    // Farmer
    private void SendOtp(final String phoneNumber) {
        // Tag used to cancel the request
        String tag_json_obj = "json_obj_req";

        final Snackbar snackbar = Snackbar
                .make(coordinatorLayout, "Please wait..", Snackbar.LENGTH_INDEFINITE);

        snackbar.show();

        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("ph_no", phoneNumber);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, EndPoints.URL_FARMER_LOGIN_SEND_OTP, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Parsing json object response
                            boolean isSuccess = response.getBoolean("success");
                            String message = response.getString("msg");
                            if (isSuccess) {
                                snackbar.dismiss();
                                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                                Toast.makeText(LoginActivity.this, "Enter OTP Once You Receive It", Toast.LENGTH_SHORT).show();
                                tvCvBtnText.setText("Verify");
                                etPassword.setVisibility(View.VISIBLE);
                                etPassword.setEnabled(true);
                                tvlblResendOtp.setVisibility(View.VISIBLE);
                                cvBtnSignUp.setVisibility(View.GONE);
                                etPassword.setInputType(InputType.TYPE_CLASS_NUMBER);
                            }

                        } catch (JSONException e) {
                            snackbar.dismiss();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                snackbar.dismiss();

                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    } else if (error.getClass().equals(NetworkError.class)) {
                        errorMessage = "Check your internet connection";
                    }
                } else {
                    String result = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(result);
                        String message = response.getString("msg");

                        Log.e("Error Message", message);

                        if (networkResponse.statusCode == 404) {
                            errorMessage = "Resource not found";
                        } else if (networkResponse.statusCode == 401) {
                            errorMessage = message + "";
                        } else if (networkResponse.statusCode == 400) {
                            errorMessage = message + "";
                        } else if (networkResponse.statusCode == 500) {
                            errorMessage = message + " Something is getting wrong";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("Error", errorMessage);
                error.printStackTrace();
                Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show();
                //tvCvBtnText.setText("Resend");
            }
        }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                // since we don't know which of the two underlying network vehicles
                // will Volley use, we have to handle and store session cookies manually
                AppController.getInstance().checkSessionCookie(response.headers);

                return super.parseNetworkResponse(response);
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final String token1 = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJGSUFQUDEwMSIsImlhdCI6MTUxNjIzOTAyMn0._jKrqO1xcUoKoguQgzhhbB3yML0GfsNTo7Mm14Bw0Bo";//Prod
                //final String token1 = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJGSUFQUDEwMSIsImlhdCI6MTUxNjIzOTAyMn0.mbwX1OXEXaY3BQoa5QPHRlBYKNgFQUpaf4wxqciEMuU";
                Map<String, String> headers = new HashMap<>();
                headers.put("app-access-token", token1);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(40 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

    }

    // Farmer
    private void VerifyOtp(final String phoneNumber, final String otp, final String userType) {
        // Tag used to cancel the request
        String tag_json_obj = "json_obj_req";

        final Snackbar snackbar = Snackbar
                .make(coordinatorLayout, "Connecting..", Snackbar.LENGTH_INDEFINITE);

        snackbar.show();

        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("ph_no", phoneNumber);
            jsonBody.put("otp", otp);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                EndPoints.URL_FARMER_LOGIN_VERIFY_OTP, jsonBody, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    // Parsing json object response
                    boolean isSuccess = response.getBoolean("success");
                    String accessToken = response.getString("jwt");
                    String message = response.getString("msg");

                    if (isSuccess) {
                        snackbar.dismiss();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.ENGLISH);
                        Date currentDate = new Date();
                        mSessionManager.createLoginSession(accessToken, userType, dateFormat.format(currentDate));
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        i.putExtra("USER_TYPE", userType);
                        startActivity(i);
                        finish();
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    snackbar.dismiss();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                snackbar.dismiss();

                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    } else if (error.getClass().equals(NetworkError.class)) {
                        errorMessage = "Check your internet connection";
                    }
                } else {
                    String result = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(result);
                        String message = response.getString("msg");

                        Log.e("Error Message", message);

                        if (networkResponse.statusCode == 404) {
                            errorMessage = "Resource not found";
                        } else if (networkResponse.statusCode == 401) {
                            errorMessage = message + "";
                        } else if (networkResponse.statusCode == 400) {
                            errorMessage = message + "";
                        } else if (networkResponse.statusCode == 500) {
                            errorMessage = message + " Something is getting wrong";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("Error", errorMessage);
                error.printStackTrace();
                Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show();
                //tvCvBtnText.setText("Resend");
            }
        }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                // since we don't know which of the two underlying network vehicles
                // will Volley use, we have to handle and store session cookies manually
                AppController.getInstance().checkSessionCookie(response.headers);

                return super.parseNetworkResponse(response);
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final String token2 = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJGSUFQUDEwMSIsImlhdCI6MTUxNjIzOTAyMn0._jKrqO1xcUoKoguQgzhhbB3yML0GfsNTo7Mm14Bw0Bo";//Prod
                //final String token2 = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJGSUFQUDEwMSIsImlhdCI6MTUxNjIzOTAyMn0.mbwX1OXEXaY3BQoa5QPHRlBYKNgFQUpaf4wxqciEMuU";
                Map<String, String> headers = new HashMap<>();
                headers.put("app-access-token", token2);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(40 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    private void verifyEmployee(final String userId, final String password, final String userType) {
        // Split user id
        Matcher m = Pattern.compile("[^0-9]*([0-9]+).*").matcher(userId);
        String url = "";
        String userIdInitial = "";
        if (m.matches()) {
            userIdInitial = userId.substring(0, userId.indexOf(m.group(1)));
        }
        switch (userIdInitial) {
            case "FIE":
                url = EndPoints.URL_EMP_LOGIN;
                break;
            case "FIFA":
                url = EndPoints.URL_AGENT_LOGIN;
                break;
            case "FPO":
                url = EndPoints.URL_FPO_LOGIN;
                break;
            case "FIBR":
                url = EndPoints.URL_BANK_REP_LOGIN;
                break;
            default:
                Toast.makeText(LoginActivity.this, "Invalid userid!", Toast.LENGTH_SHORT).show();
                break;
        }

        if (url.isEmpty())
            return;

        // Tag used to cancel the request
        String tag_json_obj = "json_obj_req";

        final Snackbar snackbar = Snackbar
                .make(coordinatorLayout, "Connecting..", Snackbar.LENGTH_INDEFINITE);
        snackbar.show();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    snackbar.dismiss();
                    // Parsing json object response
                    boolean isSuccess = response.getBoolean("success");
                    String accessToken = response.getString("jwt");
                    String message = response.getString("msg");

                    if (isSuccess) {
                        snackbar.dismiss();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.ENGLISH);
                        Date currentDate = new Date();

                        mSessionManager.createLoginSession(accessToken, userType, dateFormat.format(currentDate));
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        i.putExtra("USER_TYPE", userType);
                        startActivity(i);
                        finish();
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    snackbar.dismiss();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                snackbar.dismiss();

                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    } else if (error.getClass().equals(NetworkError.class)) {
                        errorMessage = "Check your internet connection";
                    } else if (error.getClass().equals(AuthFailureError.class)) {
                        errorMessage = "Invalid credentials";
                    }
                } else {
                    String result = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(result);
                        String message = response.getString("msg");

                        Log.e("Error Message", message);

                        if (networkResponse.statusCode == 404) {
                            errorMessage = "Resource not found";
                        } else if (networkResponse.statusCode == 401) {
                            errorMessage = message + "";
                        } else if (networkResponse.statusCode == 400) {
                            errorMessage = message + "";
                        } else if (networkResponse.statusCode == 500) {
                            errorMessage = message + " Something is getting wrong";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("Error", errorMessage);
                error.printStackTrace();
                Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                String credentials = userId + ":" + password;
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(),
                        Base64.NO_WRAP);
                headers.put("Authorization", auth);
                final String token3 = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJGSUFQUDEwMSIsImlhdCI6MTUxNjIzOTAyMn0._jKrqO1xcUoKoguQgzhhbB3yML0GfsNTo7Mm14Bw0Bo";//Prod
                //final String token3 = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJGSUFQUDEwMSIsImlhdCI6MTUxNjIzOTAyMn0.mbwX1OXEXaY3BQoa5QPHRlBYKNgFQUpaf4wxqciEMuU";
                headers.put("app-access-token", token3);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkNewAppVersionState();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_VERSION_UPDATE:
                if (resultCode != RESULT_OK) { //RESULT_OK / RESULT_CANCELED / RESULT_IN_APP_UPDATE_FAILED
                    //Log.d("Update flow failed! Result code: " + resultCode);
                    // If the update is cancelled or fails,
                    // you can request to start the update again.
                    checkNewAppVersionState();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void checkForAppUpdate() {
        // Creates instance of the manager.
        appUpdateManager = AppUpdateManagerFactory.create(LoginActivity.this);

        // Returns an intent object that you use to check for an update.
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo result) {
                if (result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                    // Request the update.
                    if (result.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                        // Start an update.
                        startAppUpdateImmediate(result);
                    }
                }
            }
        });

        // Create a listener to track request state updates.
        installStateUpdatedListener = new InstallStateUpdatedListener() {
            @Override
            public void onStateUpdate(InstallState installState) {
                // Show module progress, log state, or install the update.
                if (installState.installStatus() == InstallStatus.DOWNLOADED)
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.error_msg_app_restart), Toast.LENGTH_SHORT).show();
                // After the update is downloaded, show a notification
                // and request user confirmation to restart the app.
            }
        };
    }

    private void startAppUpdateImmediate(AppUpdateInfo appUpdateInfo) {
        try {
            appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.IMMEDIATE,
                    // The current activity making the update request.
                    LoginActivity.this,
                    // Include a request code to later monitor this update request.
                    LoginActivity.REQ_CODE_VERSION_UPDATE);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks that the update is not stalled during 'onResume()'.
     * However, you should execute this check at all app entry points.
     */
    private void checkNewAppVersionState() {
        appUpdateManager
                .getAppUpdateInfo()
                .addOnSuccessListener(
                        new OnSuccessListener<AppUpdateInfo>() {
                            @Override
                            public void onSuccess(AppUpdateInfo result) {
                                //IMMEDIATE:
                                if (result.updateAvailability()
                                        == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                                    // If an in-app update is already running, resume the update.
                                    startAppUpdateImmediate(result);
                                }
                            }
                        });
    }
}

