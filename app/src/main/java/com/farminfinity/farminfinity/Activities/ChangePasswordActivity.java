package com.farminfinity.farminfinity.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
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
import com.farminfinity.farminfinity.Helper.SessionManager;
import com.farminfinity.farminfinity.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChangePasswordActivity extends AppCompatActivity {
    private String userType = null;
    private String token = null;
    private int intUserType;

    private TextInputLayout tilOldPassword;
    private TextInputLayout tilNewPassword;
    private TextInputLayout tilConfirmNewPassword;
    private TextInputEditText tietOldPassword;
    private TextInputEditText tietNewPassword;
    private TextInputEditText tietConfirmNewPassword;
    private CardView cvOk;
    private CoordinatorLayout coordinatorLayout;

    private SessionManager mSessionManager;
    private InputValidation mInputValidation;
    private AlertDialogManager mAlert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        setupToolbar();

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.cl_activity_change_password);
        tilOldPassword = (TextInputLayout) findViewById(R.id.til_old_password_activity_change_password);
        tilNewPassword = (TextInputLayout) findViewById(R.id.til_new_password_activity_change_password);
        tilConfirmNewPassword = (TextInputLayout) findViewById(R.id.til_confirm_new_password_activity_change_password);

        tietOldPassword = (TextInputEditText) findViewById(R.id.tiet_old_password_activity_change_password);
        tietNewPassword = (TextInputEditText) findViewById(R.id.tiet_new_password_activity_change_password);
        tietConfirmNewPassword = (TextInputEditText) findViewById(R.id.tiet_confirm_new_password_activity_change_password);

        cvOk = (CardView) findViewById(R.id.cv_btn_ok_activity_change_password);

        // Initialize objects
        mSessionManager = new SessionManager(ChangePasswordActivity.this);
        mInputValidation = new InputValidation(ChangePasswordActivity.this);
        mAlert = new AlertDialogManager();

        // Get shared preference value
        if (mSessionManager.isLoggedIn()) {
            HashMap<String, String> userInfo = mSessionManager.getUserDetails();
            userType = userInfo.get("UserType");
            token = userInfo.get("Token");
        }

        // Get claims
        try {
            JWT jwt = new JWT(token);
            Map<String, Claim> allClaims = jwt.getClaims();
            intUserType = allClaims.get("int_user_type").asInt();
        } catch (Exception e) {
            Toast.makeText(ChangePasswordActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
        cvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidateFields();
            }
        });
    }

    private void setupToolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_activity_change_password);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Reset Password");
    }

    private void ValidateFields(){
        final String oldPassword = tietOldPassword.getText().toString().trim();
        final String newPassword = tietNewPassword.getText().toString().trim();

        if (!mInputValidation.isInputEditTextFilled(tietOldPassword, tilOldPassword, getString(R.string.error_message_blank_field))) {
            return;
        }
        if (!mInputValidation.isInputEditTextFilled(tietNewPassword, tilNewPassword, getString(R.string.error_message_blank_field))) {
            return;
        }
        if (!mInputValidation.isInputEditTextFilled(tietConfirmNewPassword, tilConfirmNewPassword, getString(R.string.error_message_blank_field))) {
            return;
        }
        if (!(tietNewPassword.getText().toString().trim().length() > 7 && tietNewPassword.getText().toString().trim().length() < 17)) {
            mAlert.showAlertDialog(ChangePasswordActivity.this, "Validation error", getResources().getString(R.string.error_msg_pswd_len), false);
            return;
        }
        if (!tietNewPassword.getText().toString().trim().equals(tietConfirmNewPassword.getText().toString().trim())) {
            mAlert.showAlertDialog(ChangePasswordActivity.this, "Validation error", getResources().getString(R.string.error_msg_pswd_match), false);
            return;
        }

        ChangePassword();
    }

    private void ChangePassword(){
        String url = "";
        if (userType.equals("officer")){
            switch (intUserType) {
                case 1:// FIE
                    url = EndPoints.URL_EMP_CHANGE_PASSWORD;
                    break;
                case 2:
                    url = EndPoints.URL_AGENT_CHANGE_PASSWORD;
                    break;
                case 3:
                    url = EndPoints.URL_FPO_CHANGE_PASSWORD;
                    break;
                case 4:
                    url = EndPoints.URL_BANK_REP_CHANGE_PASSWORD;
                    break;
                default:
                    Toast.makeText(ChangePasswordActivity.this, "Invalid userid!", Toast.LENGTH_SHORT).show();
                    break;
            }
        }else {return;}
        if (url.isEmpty()){return;}
        // Tag used to cancel the request
        String tag_json_obj = "json_obj_req";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("old_password", tietOldPassword.getText().toString().trim());
            jsonBody.put("new_password", tietNewPassword.getText().toString().trim());
            jsonBody.put("changed_password", true);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT, url, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Parsing json object response
                            boolean isSuccess = response.getBoolean("success");
                            String message = response.getString("msg");
                            if (isSuccess) {
                                Toast.makeText(ChangePasswordActivity.this, message, Toast.LENGTH_SHORT).show();
                                //Redirect to login page
                                mSessionManager.logoutUser();
                                //finalize();
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
                            errorMessage = message + ".";
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
                Toast.makeText(ChangePasswordActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("x-access-token", token);
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
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}