package com.farminfinity.farminfinity.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.farminfinity.farminfinity.App.AppController;
import com.farminfinity.farminfinity.Helper.AlertDialogManager;
import com.farminfinity.farminfinity.Helper.EndPoints;
import com.farminfinity.farminfinity.Helper.SessionManager;
import com.farminfinity.farminfinity.Helper.VolleyMultipartRequest;
import com.farminfinity.farminfinity.Model.DataPart;
import com.farminfinity.farminfinity.R;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class OtpVerificationActivity extends AppCompatActivity {
    private CoordinatorLayout coordinatorLayout;
    private EditText etOtp;
    private TextView tvResendOtp;
    private Button btnSubmit;

    private String firstName;
    private String middleName;
    private String lastName;
    private String gender;
    private String phone;
    private String profilePicPath;
    private String tc;

    private Bitmap bitmapProfilePhoto = null;
    private AlertDialogManager mAlert;
    private SessionManager mSessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);
        final String text3 = getResources().getString(R.string.resend_otp);

        firstName = getIntent().getStringExtra("FIRST_NAME");
        middleName = getIntent().getStringExtra("MIDDLE_NAME");
        lastName = getIntent().getStringExtra("LAST_NAME");
        gender = getIntent().getStringExtra("SEX");
        profilePicPath = getIntent().getStringExtra("PROFILE_PIC_URI");
        phone = getIntent().getStringExtra("PHONE");
        tc = getIntent().getStringExtra("AGREE_TC");
        // Get photo from intent
        if (!profilePicPath.isEmpty())
            getFile(profilePicPath);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.cl_activity_otp_verification);
        etOtp = (EditText) findViewById(R.id.et_otp_activity_otp_verification);
        tvResendOtp = (TextView) findViewById(R.id.tv_resent_otp_activity_otp_verification);
        btnSubmit = (Button) findViewById(R.id.btn_submit_activity_otp_verification);

        mAlert = new AlertDialogManager();
        mSessionManager = new SessionManager(OtpVerificationActivity.this);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etOtp.getText().toString().trim().length() == 6) {
                    VerifyOtp();
                } else {
                    Toast.makeText(OtpVerificationActivity.this, "Please enter a valid OTP!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ClickableSpan clickableSpan4 = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                SendOtp(phone);
            }
        };

        SpannableString spannableString4 = new SpannableString(text3);
        spannableString4.setSpan(clickableSpan4, 0,text3.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvResendOtp.setText(spannableString4);
        tvResendOtp.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void getFile(String profilePicPath) {
        try {
            Uri uri = Uri.parse(profilePicPath);
            FileInputStream is = (FileInputStream) getContentResolver().openInputStream(uri);
            bitmapProfilePhoto = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Farmer
    private void SendOtp(final String phoneNumber) {
        if (phoneNumber.length() == 10) {
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
                                    Toast.makeText(OtpVerificationActivity.this,  message, Toast.LENGTH_SHORT).show();
                                    Toast.makeText(OtpVerificationActivity.this,  "Enter OTP Once You Receive It", Toast.LENGTH_SHORT).show();
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
                        }else if(error.getClass().equals(NetworkError.class)){
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
                    Toast.makeText(OtpVerificationActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    //tvCvBtnText.setText("Resend");
                }
            }){
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
        }else {
            mAlert.showAlertDialog(OtpVerificationActivity.this, "Login failed..", getResources().getString(R.string.error_message_invalid_phone_no), false);
        }
    }
    // Farmer
    private void VerifyOtp() {
        String url = EndPoints.URL_FARMER_SIGNUP_VERIFY_OTP;
        // Tag used to cancel the request
        String tag_json_obj = "json_obj_req";

        final Snackbar snackbar = Snackbar
                .make(coordinatorLayout, "Please wait..", Snackbar.LENGTH_INDEFINITE);

        snackbar.show();

        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("ph_no", phone);
            jsonBody.put("otp", etOtp.getText().toString().trim());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Parsing json object response
                            boolean isSuccess = response.getBoolean("success");
                            if (isSuccess) {
                                snackbar.dismiss();
                                Signup();
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
                Toast.makeText(OtpVerificationActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        }) {
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

    private void Signup() {
        String url = EndPoints.URL_FARMER_SIGNUP;
        // Tag used to cancel the request
        String tag_custom_multi_part_obj = "custom_multi_part_obj_req";

        final Snackbar snackbar = Snackbar
                .make(coordinatorLayout, "Please wait..", Snackbar.LENGTH_INDEFINITE);

        snackbar.show();

        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, url,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            // Parsing json object response
                            JSONObject obj = new JSONObject(new String(response.data));
                            boolean isSuccess = obj.getBoolean("success");
                            String message = obj.getString("msg");
                            final String jwt = obj.getString("jwt");

                            if (isSuccess) {
                                snackbar.dismiss();
                                Toast.makeText(OtpVerificationActivity.this, message, Toast.LENGTH_SHORT).show();
                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.ENGLISH);
                                Date currentDate = new Date();
                                mSessionManager.createLoginSession(jwt, "farmer", dateFormat.format(currentDate));
                                Intent i = new Intent(OtpVerificationActivity.this, MainActivity.class);
                                i.putExtra("USER_TYPE", "farmer");
                                startActivity(i);
                                finish();
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
                Toast.makeText(OtpVerificationActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJGSUFQUDEwMSIsImlhdCI6MTUxNjIzOTAyMn0._jKrqO1xcUoKoguQgzhhbB3yML0GfsNTo7Mm14Bw0Bo";//Prod
                //final String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJGSUFQUDEwMSIsImlhdCI6MTUxNjIzOTAyMn0.mbwX1OXEXaY3BQoa5QPHRlBYKNgFQUpaf4wxqciEMuU";
                Map<String, String> headers = new HashMap<>();
                headers.put("app-access-token", token);
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                // Add Parameters
                final boolean lockEdit = false;
                final boolean active = true;

                params.put("first_name", firstName);
                params.put("middle_name", middleName);
                params.put("last_name", lastName);
                params.put("sex", gender);
                params.put("ph_no", phone);
                params.put("emp_ref_id", null);
                params.put("agree_tc", tc);
                params.put("app_status", "1");
                params.put("lock_edit", String.valueOf(lockEdit));
                params.put("active", String.valueOf(active));
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() throws AuthFailureError {
                Map<String, DataPart> params = new HashMap<>();
                long imgProfilePhotoName = System.currentTimeMillis();
                if (bitmapProfilePhoto != null)
                    params.put("img_profile_pic", new DataPart(imgProfilePhotoName + ".jpg", getFileDataFromDrawable(bitmapProfilePhoto)));

                return params;
            }
        };

        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(volleyMultipartRequest, tag_custom_multi_part_obj);
    }

    private byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}