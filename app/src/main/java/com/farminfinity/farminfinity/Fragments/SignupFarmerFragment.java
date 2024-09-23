package com.farminfinity.farminfinity.Fragments;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.farminfinity.farminfinity.Activities.ImagePickerActivity;
import com.farminfinity.farminfinity.Activities.OtpVerificationActivity;
import com.farminfinity.farminfinity.Activities.SignupNewActivity;
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

public class SignupFarmerFragment extends BaseFragment {
    private static final String TAG = "SignupFarmerFragment";
    private CoordinatorLayout coordinatorLayout;
    private ImageView ivProfilePic;
    private TextInputLayout tilFirstName;
    private TextInputLayout tilMiddleName;
    private TextInputLayout tilLastName;
    private TextInputEditText tietFirstName;
    private TextInputEditText tietMiddleName;
    private TextInputEditText tietLastName;
    private TextInputLayout tilPhoneNumber;
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

    private static final int CROP_PROFILE_PHOTO_IMAGE_REQUEST = 427;

    public SignupFarmerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_signup_farmer, container, false);

        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.cl_fragment_signup_farmer);
        //tvlblTermCon = (TextView) view.findViewById(R.id.tv_lbl_tc_fragment_signup_farmer);
        tvlblPrivacyPolicy = (TextView) view.findViewById(R.id.tv_lbl_pp_fragment_signup_farmer);
        tilFirstName = (TextInputLayout) view.findViewById(R.id.til_f_name_fragment_signup_farmer);
        tilMiddleName = (TextInputLayout) view.findViewById(R.id.til_m_name_fragment_signup_farmer);
        tilLastName = (TextInputLayout) view.findViewById(R.id.til_l_name_fragment_signup_farmer);
        tilPhoneNumber = (TextInputLayout) view.findViewById(R.id.til_phone_fragment_signup_farmer);
        tietFirstName = (TextInputEditText) view.findViewById(R.id.tiet_f_name_fragment_signup_farmer);
        tietMiddleName = (TextInputEditText) view.findViewById(R.id.tiet_m_name_fragment_signup_farmer);
        tietLastName = (TextInputEditText) view.findViewById(R.id.tiet_l_name_fragment_signup_farmer);
        spinnerGender = (Spinner) view.findViewById(R.id.spinner_gender_fragment_signup_farmer);
        ivProfilePic = (ImageView) view.findViewById(R.id.iv_profile_pic_fragment_signup_farmer);
        tietPhoneNumber = (TextInputEditText) view.findViewById(R.id.tiet_phone_fragment_signup_farmer);
        cbTc = (CheckBox) view.findViewById(R.id.cb_tc_fragment_signup_farmer);
        btnUpload = (Button) view.findViewById(R.id.btn_upload_pic_fragment_signup_farmer);
        cvBtnSubmit = (CardView) view.findViewById(R.id.cv_btn_submit_fragment_signup_farmer);

        mInputValidation = new InputValidation(getActivity());
        mAlert = new AlertDialogManager();

        //final String textTC = getResources().getString(R.string.lbl_term_con);
        final String textPP = getResources().getString(R.string.lbl_privacy_policy);

        // Gender Spinner Drop down elements
        List<String> lstGender = new ArrayList<>();
        lstGender.add("--Select--");
        lstGender.add(getResources().getString(R.string.ddl_male));
        lstGender.add(getResources().getString(R.string.ddl_female));

        ArrayAdapter<String> dataAdapterGender = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, lstGender);

        dataAdapterGender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerGender.setAdapter(dataAdapterGender);

        ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent browserIntent1 = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.privacy_policy_url)));
                startActivity(browserIntent1);
            }
        };
        /*ClickableSpan clickableSpan2 = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent browserIntent2 = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.terms_url)));
                startActivity(browserIntent2);
            }
        };*/

        //SpannableString spannableStringFpo = new SpannableString(textPP);
        //spannableStringFpo.setSpan(clickableSpan1, 0, textPP.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //tvlblTermCon.setText(spannableStringFpo);
        //tvlblTermCon.setMovementMethod(LinkMovementMethod.getInstance());

        SpannableString spannableStringAgent = new SpannableString(textPP);
        spannableStringAgent.setSpan(clickableSpan1, 0, textPP.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
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
                    if (spinnerGender.getSelectedItemPosition() == 0) {
                        mAlert.showAlertDialog(getActivity(), "Validation Error", getResources().getString(R.string.err_msg_gender), false);
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
                    Toast.makeText(getActivity(), "To continue, please agree to the terms and condition!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    showImagePickerOptions(view);
                    Toast.makeText(getActivity(), "Crop and upload a profile photo!", Toast.LENGTH_SHORT).show();
                } catch (ActivityNotFoundException anfe) {
                    // display an error message
                    String errorMessage = "Whoops - your device doesn't support the crop action!";
                    Toast toast = Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        return view;
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
                                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                                Toast.makeText(getActivity(), "OTP Sent", Toast.LENGTH_SHORT).show();

                                OtpVerifyFragment otpVerifyFragment = new OtpVerifyFragment();

                                Bundle args = new Bundle();
                                if (uri != null)
                                    args.putString("PROFILE_PIC_URI", uri.toString());
                                else
                                    args.putString("PROFILE_PIC_URI", "");
                                args.putString("FIRST_NAME", tietFirstName.getText().toString().trim());
                                args.putString("MIDDLE_NAME", tietMiddleName.getText().toString().trim());
                                args.putString("LAST_NAME", tietLastName.getText().toString().trim());
                                args.putString("SEX", spinnerGender.getSelectedItem().toString());
                                args.putString("PHONE", tietPhoneNumber.getText().toString().trim());
                                args.putString("AGREE_TC", String.valueOf(cbTc.isChecked()));
                                otpVerifyFragment.setArguments(args);

                                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.fragment_container, otpVerifyFragment, "SignupFarmerFragment");
                                transaction.addToBackStack(null);
                                transaction.commit();
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
                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
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
                //final String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJGSUFQUDEwMSIsImlhdCI6MTUxNjIzOTAyMn0.mbwX1OXEXaY3BQoa5QPHRlBYKNgFQUpaf4wxqciEMuU";
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

    private void showImagePickerOptions(View view) {
        ImagePickerActivity.showImagePickerOptions(getActivity(), new ImagePickerActivity.PickerOptionListener() {
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
        Intent intent = new Intent(getActivity(), ImagePickerActivity.class);
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
            case R.id.btn_upload_pic_fragment_signup_farmer:
                startActivityForResult(intent, CROP_PROFILE_PHOTO_IMAGE_REQUEST);
                break;
            default:
                break;
        }
    }

    private void launchGalleryIntent(View view) {
        Intent intent = new Intent(getActivity(), ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);

        // setting aspect ratio
        //intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        //intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        //intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);

        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 600);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 600);

        switch (view.getId()) {
            case R.id.btn_upload_pic_fragment_signup_farmer:
                startActivityForResult(intent, CROP_PROFILE_PHOTO_IMAGE_REQUEST);
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            uri = data.getParcelableExtra("path");
            try {
                //Getting image from gallery
                bitmapProfilePhoto = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                ivProfilePic.setImageBitmap(bitmapProfilePhoto);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}