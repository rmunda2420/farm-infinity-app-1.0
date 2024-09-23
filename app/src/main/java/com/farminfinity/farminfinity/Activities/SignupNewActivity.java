package com.farminfinity.farminfinity.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.farminfinity.farminfinity.Helper.InputValidation;
import com.farminfinity.farminfinity.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SignupNewActivity extends AppCompatActivity {
    private CoordinatorLayout coordinatorLayout;
    private ImageView ivProfilePic;
    private TextInputLayout tilFirstName;
    private TextInputLayout tilMiddleName;
    private TextInputLayout tilLastName;
    private TextInputLayout tilPhoneNumber;
    private TextInputEditText tietFirstName;
    private TextInputEditText tietMiddleName;
    private TextInputEditText tietLastName;
    private TextInputEditText tietPhoneNumber;
    private Spinner spinnerGender;
    private CheckBox cbTc;
    private Button btnUpload;
    private CardView cvBtnSubmit;
    private TextView tvlblTermCon;
    private TextView tvlblPrivacyPolicy;

    private InputValidation mInputValidation;
    private AlertDialogManager mAlert;

    private Bitmap bitmapProfilePhoto;

    private Uri uri = null;

    private static final int CROP_PROFILE_PHOTO_IMAGE_REQUEST = 428;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_new);
        setupToolbar();

        //final String textTC = getResources().getString(R.string.lbl_term_con);
        final String textPP = getResources().getString(R.string.lbl_privacy_policy);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.cl_activity_signup_new);
        //tvlblTermCon = (TextView) findViewById(R.id.tv_lbl_tc_activity_signup_new);
        tvlblPrivacyPolicy = (TextView) findViewById(R.id.tv_lbl_pp_activity_signup_new);
        tilFirstName = (TextInputLayout) findViewById(R.id.til_f_name_activity_signup_new);
        tilMiddleName = (TextInputLayout) findViewById(R.id.til_m_name_activity_signup_new);
        tilLastName = (TextInputLayout) findViewById(R.id.til_l_name_activity_signup_new);
        tilPhoneNumber = (TextInputLayout) findViewById(R.id.til_phone_activity_signup_new);
        tietFirstName = (TextInputEditText) findViewById(R.id.tiet_f_name_activity_signup_new);
        tietMiddleName = (TextInputEditText) findViewById(R.id.tiet_m_name_activity_signup_new);
        tietLastName = (TextInputEditText) findViewById(R.id.tiet_l_name_activity_signup_new);
        spinnerGender = (Spinner) findViewById(R.id.spinner_gender_activity_signup_new);
        ivProfilePic = (ImageView) findViewById(R.id.iv_profile_pic_activity_signup_new);
        tietPhoneNumber = (TextInputEditText) findViewById(R.id.tiet_phone_activity_signup_new);
        cbTc = (CheckBox) findViewById(R.id.cb_tc_activity_signup_new);
        btnUpload = (Button) findViewById(R.id.btn_upload_pic_activity_signup_new);
        cvBtnSubmit = (CardView) findViewById(R.id.cv_btn_submit_activity_signup_new);

        mInputValidation = new InputValidation(SignupNewActivity.this);
        mAlert = new AlertDialogManager();

        // Gender Spinner Drop down elements
        List<String> lstGender = new ArrayList<>();
        lstGender.add("--Select--");
        lstGender.add(getResources().getString(R.string.ddl_male));
        lstGender.add(getResources().getString(R.string.ddl_female));

        ArrayAdapter<String> dataAdapterGender = new ArrayAdapter<String>(SignupNewActivity.this, android.R.layout.simple_spinner_item, lstGender);

        dataAdapterGender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerGender.setAdapter(dataAdapterGender);

        /*ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent browserIntent1 = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.terms_url)));
                startActivity(browserIntent1);
            }
        };*/
        ClickableSpan clickableSpan2 = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent browserIntent2 = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.privacy_policy_url)));
                startActivity(browserIntent2);
            }
        };

        //SpannableString spannableStringFpo = new SpannableString(textTC);
        //spannableStringFpo.setSpan(clickableSpan1, 0, textTC.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //tvlblTermCon.setText(spannableStringFpo);
        //tvlblTermCon.setMovementMethod(LinkMovementMethod.getInstance());

        SpannableString spannableStringAgent = new SpannableString(textPP);
        spannableStringAgent.setSpan(clickableSpan2, 0, textPP.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvlblPrivacyPolicy.setText(spannableStringAgent);
        tvlblPrivacyPolicy.setMovementMethod(LinkMovementMethod.getInstance());

        cvBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cbTc.isChecked()) {
                    if (!mInputValidation.isInputEditTextFilled(tietFirstName, tilFirstName, getString(R.string.error_message_blank_field))) {
                        return;
                    }
                    if (!mInputValidation.isInputEditTextFilled(tietLastName, tilLastName, getString(R.string.error_message_blank_field))) {
                        return;
                    }
                    if (spinnerGender.getSelectedItemPosition()==0) {
                        mAlert.showAlertDialog(SignupNewActivity.this, "Validation Error", getResources().getString(R.string.err_msg_gender), false);
                        return;
                    }
                    if (!mInputValidation.isInputEditTextFilled(tietPhoneNumber, tilPhoneNumber, getString(R.string.error_message_blank_field))) {
                        return;
                    }
                    if (!mInputValidation.isInputEditTextPhone(tietPhoneNumber, tilPhoneNumber, getString(R.string.error_message_invalid_phone_no))) {
                        return;
                    }
                    SendOtp();
                } else {
                    Toast.makeText(SignupNewActivity.this, "To continue, please agree to the terms and condition!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    showImagePickerOptions(view);
                    Toast.makeText(SignupNewActivity.this, "Crop and upload a profile photo!", Toast.LENGTH_SHORT).show();
                } catch (ActivityNotFoundException anfe) {
                    // display an error message
                    String errorMessage = "Whoops - your device doesn't support the crop action!";
                    Toast toast = Toast.makeText(SignupNewActivity.this, errorMessage, Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }

    private void setupToolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_activity_signup_new);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Register");
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void SendOtp() {
        String url = EndPoints.URL_FARMER_SIGNUP_SEND_OTP;
        // Tag used to cancel the request
        String tag_json_obj = "json_obj_req";

        final Snackbar snackbar = Snackbar
                .make(coordinatorLayout, "Please wait..", Snackbar.LENGTH_INDEFINITE);

        snackbar.show();

        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("ph_no", tietPhoneNumber.getText().toString().trim());
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
                            String message = response.getString("msg");
                            if (isSuccess) {
                                snackbar.dismiss();
                                Toast.makeText(SignupNewActivity.this, message, Toast.LENGTH_SHORT).show();
                                Toast.makeText(SignupNewActivity.this, "OTP Sent", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SignupNewActivity.this, OtpVerificationActivity.class);
                                if (uri != null)
                                    intent.putExtra("PROFILE_PIC_URI", uri.toString());
                                else
                                    intent.putExtra("PROFILE_PIC_URI", "");
                                intent.putExtra("FIRST_NAME", tietFirstName.getText().toString().trim());
                                intent.putExtra("MIDDLE_NAME", tietMiddleName.getText().toString().trim());
                                intent.putExtra("LAST_NAME", tietLastName.getText().toString().trim());
                                intent.putExtra("SEX", spinnerGender.getSelectedItem().toString());
                                intent.putExtra("PHONE", tietPhoneNumber.getText().toString().trim());
                                intent.putExtra("AGREE_TC", String.valueOf(cbTc.isChecked()));
                                startActivity(intent);
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
                Toast.makeText(SignupNewActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
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
                final String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJGSUFQUDEwMSIsImlhdCI6MTUxNjIzOTAyMn0._jKrqO1xcUoKoguQgzhhbB3yML0GfsNTo7Mm14Bw0Bo";//Prod
                //final String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJGSUFQUDEwMSIsImlhdCI6MTUxNjIzOTAyMn0.mbwX1OXEXaY3BQoa5QPHRlBYKNgFQUpaf4wxqciEMuU";
                Map<String, String> headers = new HashMap<>();
                headers.put("app-access-token", token);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(40 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    private void showImagePickerOptions(View view) {
        ImagePickerActivity.showImagePickerOptions(SignupNewActivity.this, new ImagePickerActivity.PickerOptionListener() {
            @Override
            public void onTakeCameraSelected() {
                launchCameraIntent(view);
            }

            @Override
            public void onChooseGallerySelected() {
                launchGalleryIntent(view);
            }
        });
    }

    private void launchCameraIntent(View view) {
        Intent intent = new Intent(SignupNewActivity.this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_IMAGE_CAPTURE);

        // setting aspect ratio
        //intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        //intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        //intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);

        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 600);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 600);

        switch (view.getId()) {
            case R.id.btn_upload_pic_activity_signup_new:
                startActivityForResult(intent, CROP_PROFILE_PHOTO_IMAGE_REQUEST);
                break;
            default:
                break;
        }
    }

    private void launchGalleryIntent(View view) {
        Intent intent = new Intent(SignupNewActivity.this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);

        // setting aspect ratio
        //intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        //intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        //intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);

        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 600);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 600);
        // For multiple upload button not needed here
        switch (view.getId()) {
            case R.id.btn_upload_pic_activity_signup_new:
                startActivityForResult(intent, CROP_PROFILE_PHOTO_IMAGE_REQUEST);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            uri = data.getParcelableExtra("path");
            try {
                //Getting image from gallery
                bitmapProfilePhoto = MediaStore.Images.Media.getBitmap(SignupNewActivity.this.getContentResolver(), uri);
                ivProfilePic.setImageBitmap(bitmapProfilePhoto);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}