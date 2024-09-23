package com.farminfinity.farminfinity.Fragments;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
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
import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;
import com.baoyachi.stepview.HorizontalStepView;
import com.baoyachi.stepview.bean.StepBean;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.ceylonlabs.imageviewpopup.ImagePopup;
import com.farminfinity.farminfinity.Activities.CameraActivity;
import com.farminfinity.farminfinity.Activities.CameraActivity2;
import com.farminfinity.farminfinity.Activities.ImagePickerActivity;
import com.farminfinity.farminfinity.App.AppController;
import com.farminfinity.farminfinity.Helper.AlertDialogManager;
import com.farminfinity.farminfinity.Helper.EndPoints;
import com.farminfinity.farminfinity.Helper.InputValidation;
import com.farminfinity.farminfinity.Helper.SessionManager;
import com.farminfinity.farminfinity.Helper.VolleyMultipartRequest;
import com.farminfinity.farminfinity.Model.DataPart;
import com.farminfinity.farminfinity.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FarmAuthentication extends BaseFragment {
    private String userType = null;
    private String token = null;
    private int intUserType;

    private String fId = null;
    private boolean isLevelDone = false;

    private SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    private static final int CROP_FARM_PHOTO_IMAGE_REQUEST = 101;// default camera app
    private static final int FARM_PHOTO_IMAGE_REQUEST = 102;// my camera app
    private static final int CROP_LAND_DOC_IMAGE_REQUEST = 103;
    private String urlImageFarmPhoto = "";
    private String urlImageLandDoc = "";
    private Uri filePath1;
    private Uri filePath2;
    private CoordinatorLayout coordinatorLayout;
    private TextInputLayout tilGpsCoordinate;
    private TextInputEditText tietGpsCoordinate;
    private TextInputLayout tilCaptureDate;
    private TextInputEditText tietCaptureDate;
    private CardView cvBtnAttachFarmPhoto;
    private CardView cvBtnAttachLandDoc;
    private TextView tvBtnAttachFarmPhoto, tvLblUploadLandDoc;
    private ImageView ivViewFarmPhoto, ivViewLandDoc;
    private CardView cvBtnSaveAndContinue;
    private HorizontalStepView horizontalStepView;
    private boolean isAttachedFarmPhoto;
    private Bitmap bitmapFarmPhoto, bitmapPassportSizePhoto = null;
    private ImagePopup imagePopupFarmPhoto, imagePopupLandDoc;

    private TextInputLayout tilLandDocNo;
    private TextInputEditText tietLandDocNo;
    private Spinner spinnerLandDoc;
    private boolean islandDocValid = false;
    private String landDocVerifyId = "";

    private boolean isAttachedLandDoc = false;
    private Bitmap bitmapLandDocument = null;

    private SessionManager mSessionManager;
    private InputValidation mInputValidation;
    private AlertDialogManager mAlert;

    public FarmAuthentication() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_farm_authentication, container, false);
        // Get farmer id from previous fragment
        fId = getArguments().getString("FID");

        // Initialize objects
        mAlert = new AlertDialogManager();
        mSessionManager = new SessionManager(getActivity());
        mInputValidation = new InputValidation(getActivity());

        // Get shared preference value
        if (mSessionManager.isLoggedIn()) {
            HashMap<String, String> userInfo = mSessionManager.getUserDetails();
            userType = userInfo.get("UserType");
            token = userInfo.get("Token");
        }

        // Get claims
        try {
            if (userType.equals("officer")) {
                JWT jwt = new JWT(token);
                Map<String, Claim> allClaims = jwt.getClaims();
                intUserType = allClaims.get("int_user_type").asInt();
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
        }

        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.cl_fragment_farm_authentication);
        tilGpsCoordinate = (TextInputLayout) view.findViewById(R.id.til_gps_co_fragment_farm_authentication);
        tilLandDocNo = (TextInputLayout) view.findViewById(R.id.til_landDocNo_fragment_farm_authentication);
        tietGpsCoordinate = (TextInputEditText) view.findViewById(R.id.tiet_gps_co_fragment_farm_authentication);
        tilCaptureDate = (TextInputLayout) view.findViewById(R.id.til_gps_cap_dt_fragment_farm_authentication);
        tietCaptureDate = (TextInputEditText) view.findViewById(R.id.tiet_gps_cap_dt_fragment_farm_authentication);
        tietLandDocNo = (TextInputEditText) view.findViewById(R.id.tiet_landDocNo_fragment_farm_authentication);
        cvBtnAttachFarmPhoto = (CardView) view.findViewById(R.id.cv_btn_upload_farm_photo_fragment_farm_authentication);
        tvBtnAttachFarmPhoto = (TextView) view.findViewById(R.id.tv_btn_upload_farm_photo_fragment_farm_authentication);
        tvLblUploadLandDoc = (TextView) view.findViewById(R.id.tv_cv_btn_upload_land_doc_fragment_farm_authentication);
        ivViewFarmPhoto = (ImageView) view.findViewById(R.id.iv_viewFarmPhoto_fragment_farm_authentication);
        cvBtnAttachLandDoc = (CardView) view.findViewById(R.id.cv_btn_upload_land_doc_fragment_farm_authentication);
        cvBtnSaveAndContinue = (CardView) view.findViewById(R.id.cv_btn_save_fragment_farm_authentication);
        horizontalStepView = (HorizontalStepView) view.findViewById(R.id.step_view_fragment_farm_authentication);
        cvBtnSaveAndContinue = (CardView) view.findViewById(R.id.cv_btn_save_fragment_farm_authentication);
        ivViewLandDoc = (ImageView) view.findViewById(R.id.iv_viewLandDoc_fragment_farm_authentication);
        spinnerLandDoc = (Spinner) view.findViewById(R.id.spinner_landDoc_fragment_farm_authentication);

        imagePopupFarmPhoto = new ImagePopup(getActivity());
        imagePopupFarmPhoto.setWindowHeight(800); // Optional
        imagePopupFarmPhoto.setWindowWidth(800); // Optional
        imagePopupFarmPhoto.setBackgroundColor(Color.BLACK);  // Optional
        imagePopupFarmPhoto.setFullScreen(true); // Optional
        imagePopupFarmPhoto.setHideCloseIcon(true);  // Optional
        imagePopupFarmPhoto.setImageOnClickClose(true);

        imagePopupLandDoc = new ImagePopup(getActivity());
        imagePopupLandDoc.setWindowHeight(800); // Optional
        imagePopupLandDoc.setWindowWidth(800); // Optional
        imagePopupLandDoc.setBackgroundColor(Color.BLACK);  // Optional
        imagePopupLandDoc.setFullScreen(true); // Optional
        imagePopupLandDoc.setHideCloseIcon(true);  // Optional
        imagePopupLandDoc.setImageOnClickClose(true);  // Optional

        List<StepBean> stepsBeanList = new ArrayList<>();
        StepBean stepBean0 = new StepBean("1", 1);
        StepBean stepBean1 = new StepBean("2", 1);
        StepBean stepBean2 = new StepBean("3", 0);
        StepBean stepBean3 = new StepBean("4", -1);
        stepsBeanList.add(stepBean0);
        stepsBeanList.add(stepBean1);
        stepsBeanList.add(stepBean2);
        stepsBeanList.add(stepBean3);

        horizontalStepView.setStepViewTexts(stepsBeanList)
                .setTextSize(12)
                .setStepsViewIndicatorCompletedLineColor(ContextCompat.getColor(getActivity(), android.R.color.darker_gray))
                .setStepsViewIndicatorUnCompletedLineColor(ContextCompat.getColor(getActivity(), android.R.color.darker_gray))
                .setStepViewComplectedTextColor(ContextCompat.getColor(getActivity(), android.R.color.darker_gray))
                .setStepViewUnComplectedTextColor(ContextCompat.getColor(getActivity(), android.R.color.darker_gray))
                .setStepsViewIndicatorCompleteIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_success))
                .setStepsViewIndicatorDefaultIcon(ContextCompat.getDrawable(getActivity(), R.drawable.default_icon))
                .setStepsViewIndicatorAttentionIcon(ContextCompat.getDrawable(getActivity(), R.drawable.attention));

        // Data for Land Doc
        List<String> lstLandDoc = new ArrayList<>();
        lstLandDoc.add("--Select--");
        lstLandDoc.add("Farming Certificate");
        lstLandDoc.add("General Power of Attorney");
        lstLandDoc.add("Khata certificate or extract");
        lstLandDoc.add("Sale Agreement");
        lstLandDoc.add("Sale Deed/Title Deed/Mother Deed/Conveyance Deed");


        ArrayAdapter<String> dataAdapterLandDoc = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, lstLandDoc);
        dataAdapterLandDoc.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLandDoc.setAdapter(dataAdapterLandDoc);

        tietLandDocNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable et) {
                String s = et.toString();
                if (!s.equals(s.toUpperCase())) {
                    s = s.toUpperCase();
                    tietLandDocNo.setText(s);
                    tietLandDocNo.setSelection(tietLandDocNo.length()); //Set cursor to end
                }
            }
        });

        cvBtnAttachFarmPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImagePickerOptions(view);
            }
        });

        cvBtnAttachLandDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImagePickerOptions(view);
            }
        });

        cvBtnSaveAndContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Validate();
            }
        });

        ivViewFarmPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isAttachedFarmPhoto) {
                    imagePopupFarmPhoto.initiatePopupWithPicasso(filePath1);
                    imagePopupFarmPhoto.viewPopup();
                } else {
                    imagePopupFarmPhoto.initiatePopupWithPicasso(urlImageFarmPhoto);
                    imagePopupFarmPhoto.viewPopup();
                }
            }
        });

        ivViewLandDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isAttachedLandDoc) {
                    imagePopupLandDoc.initiatePopupWithPicasso(filePath2);
                    imagePopupLandDoc.viewPopup();
                } else {
                    imagePopupLandDoc.initiatePopupWithPicasso(urlImageLandDoc);
                    imagePopupLandDoc.viewPopup();
                }
            }
        });
        InitPreData();
        InitData();

        return view;
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
        //mAlert.showAlertDialog(getActivity(), "Camera", "Feature not available.", false);
        switch (view.getId()) {
            case R.id.cv_btn_upload_farm_photo_fragment_farm_authentication:
                Intent intent_a = new Intent(getActivity(), CameraActivity2.class);// my camera app
                startActivityForResult(intent_a, FARM_PHOTO_IMAGE_REQUEST);
                break;
            case R.id.cv_btn_upload_land_doc_fragment_farm_authentication:
                Intent intent_b = new Intent(getActivity(), ImagePickerActivity.class);
                intent_b.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_IMAGE_CAPTURE);

                // setting aspect ratio
                //intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
                //intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
                //intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);

                // setting maximum bitmap width and height
                intent_b.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
                intent_b.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 600);
                intent_b.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 600);
                startActivityForResult(intent_b, CROP_LAND_DOC_IMAGE_REQUEST);
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
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 3000);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 2000);

        switch (view.getId()) {
            case R.id.cv_btn_upload_farm_photo_fragment_farm_authentication:
                startActivityForResult(intent, CROP_FARM_PHOTO_IMAGE_REQUEST);
                break;
            case R.id.cv_btn_upload_land_doc_fragment_farm_authentication:
                startActivityForResult(intent, CROP_LAND_DOC_IMAGE_REQUEST);
                break;
            default:
                break;
        }
    }

    private void Validate() {
        /*if (!mInputValidation.isInputEditTextFilled(tietGpsCoordinate, tilGpsCoordinate, getString(R.string.error_message_blank_field))) {
            return;
        }

        if (spinnerLandDoc.getSelectedItem().toString().equals("--Select--")) {
            mAlert.showAlertDialog(getActivity(), "Validation Error", getResources().getString(R.string.err_msg_select_doc_name), false);
            return;
        }
        if (!mInputValidation.isInputEditTextFilled(tietLandDocNo, tilLandDocNo, getString(R.string.error_message_blank_field))) {
            return;
        }*/

        if (isLevelDone) {
            EditDataWithFile();
        } else {
            // Check image attachments
            /*if (!isAttachedFarmPhoto) {
                mAlert.showAlertDialog(getActivity(), "Validation Error", "Attach a selfie with farm land", false);
                return;
            }
            if (!isAttachedLandDoc) {
                mAlert.showAlertDialog(getActivity(), "Validation Error", "Attach land document photo", false);
                return;
            }*/
            AddDataWithFile();
        }
    }

    private void InitPreData() {
        String url = EndPoints.URL_FARMER_GET_SHORT_INFO + fId;

        // Tag used to cancel the request
        String tag_json_arr = "json_arr_req";

        final Snackbar snackbar = Snackbar
                .make(coordinatorLayout, "Please wait..", Snackbar.LENGTH_INDEFINITE);

        snackbar.show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            snackbar.dismiss();
                            JSONObject obj = response.getJSONObject("data");
                            String urlImagePassportSizePhoto = obj.getString("profile_pic");
                            // get bitmap
                            Glide.with(getActivity())
                                    .asBitmap()
                                    .load(urlImagePassportSizePhoto)
                                    .into(new CustomTarget<Bitmap>() {
                                        @Override
                                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                            bitmapPassportSizePhoto = resource;
                                        }

                                        @Override
                                        public void onLoadCleared(@Nullable Drawable placeholder) {
                                        }
                                    });
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
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                final String token1 = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJGSUFQUDEwMSIsImlhdCI6MTUxNjIzOTAyMn0._jKrqO1xcUoKoguQgzhhbB3yML0GfsNTo7Mm14Bw0Bo";//Prod
                //final String token1 = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJGSUFQUDEwMSIsImlhdCI6MTUxNjIzOTAyMn0.mbwX1OXEXaY3BQoa5QPHRlBYKNgFQUpaf4wxqciEMuU";
                headers.put("app-access-token", token1);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjectRequest, tag_json_arr);
    }

    private void InitData() {
        String url = "";
        if (userType.equals("farmer"))
            url = EndPoints.URL_FARMER_GET_FARM_AUTH;
        else if (userType.equals("officer")) {
            switch (intUserType) {
                case 1:// FIE
                    url = EndPoints.URL_EMP_GET_FARM_AUTH + fId;
                    break;
                case 2:
                    url = EndPoints.URL_AGENT_GET_FARM_AUTH + fId;
                    break;
                case 3:
                    url = EndPoints.URL_FPO_GET_FARM_AUTH + fId;
                    break;
                case 4:
                    url = EndPoints.URL_BANK_REP_GET_FARM_AUTH + fId;
                    break;
                default:
                    Toast.makeText(getActivity(), "Invalid userid!", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
        if (url.isEmpty())
            return;

        // Tag used to cancel the request
        String tag_json_obj = "json_obj_req";

        final Snackbar snackbar = Snackbar
                .make(coordinatorLayout, "Please wait..", Snackbar.LENGTH_INDEFINITE);

        snackbar.show();
        cvBtnSaveAndContinue.setEnabled(false);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            snackbar.dismiss();
                            cvBtnSaveAndContinue.setEnabled(true);
                            // Parsing json object response
                            JSONObject data = response.getJSONObject("data");
                            isLevelDone = data.getBoolean("done_level");
                            if (isLevelDone) {
                                tietGpsCoordinate.setText(data.getString("farm_coordinates"));
                                tietCaptureDate.setText(data.getString("capture_date"));
                                urlImageFarmPhoto = data.getString("farm_photo");
                                if (!urlImageFarmPhoto.isEmpty()) {
                                    tvBtnAttachFarmPhoto.setText("Uploaded");
                                    cvBtnAttachFarmPhoto.setBackgroundColor(Color.parseColor("#FD7D00"));
                                    ivViewFarmPhoto.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_view_doc_black));
                                }
                                String landDocName = data.getString("farmer_land_doc_name");
                                if (landDocName.isEmpty())
                                    spinnerLandDoc.setSelection(0);
                                else
                                    spinnerLandDoc.setSelection(getIndex(spinnerLandDoc, data.getString("farmer_land_doc_name")));
                                tietLandDocNo.setText(data.getString("farmer_land_doc_number"));

                                urlImageLandDoc = data.getString("farmer_land_doc_img");
                                if (!urlImageLandDoc.isEmpty()) {
                                    tvLblUploadLandDoc.setText("Uploaded");
                                    cvBtnAttachLandDoc.setBackgroundColor(Color.parseColor("#FD7D00"));
                                    ivViewLandDoc.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_view_doc_black));
                                }
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
                cvBtnSaveAndContinue.setEnabled(true);
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

    private void OcrFarmPhoto() {
        String tag_custom_multi_part_obj = "custom_multi_part_obj_req";

        final Snackbar snackbar = Snackbar
                .make(coordinatorLayout, "Getting data. please wait..", Snackbar.LENGTH_INDEFINITE);
        snackbar.show();
        // Custom Volley Request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, EndPoints.URL_OCR_FARM_PHOTO,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        snackbar.dismiss();
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));
                            JSONObject data = obj.getJSONObject("result");
                            String latitude = data.isNull("latitude") ? "" : data.getString("latitude");
                            String longitude = data.isNull("longitude")? "" : data.getString("longitude");
                            String time = data.isNull("time")? "" : data.getString("time");
                            tietGpsCoordinate.setText(latitude + "," + longitude);
                            tietCaptureDate.setText(time);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        snackbar.dismiss();
                        bitmapFarmPhoto = null;
                        cvBtnAttachFarmPhoto.setBackgroundColor(Color.parseColor("#447DF0"));

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
                                String message = "";

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
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("app-access-token", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJGSUFQUDEwMiIsImlhdCI6MTUxNjIzOTAyMn0.YxElUmAy5J9_rLdQ5P7i-vbH4Tz2X3tRwhE1_WujrLM");
                return headers;
            }

            /*
             * Here we are passing image by renaming it with a unique name
             * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imgFarmPhoto = System.currentTimeMillis();
                if (isAttachedFarmPhoto) {
                    params.put("img_farm_photo", new DataPart(imgFarmPhoto + ".jpg", getFileDataFromDrawable(bitmapFarmPhoto)));
                }
                return params;
            }
        };

        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(volleyMultipartRequest, tag_custom_multi_part_obj);
    }

    private void CompareFaceSelfie(Bitmap image_s, Bitmap image_t) {
        String url = EndPoints.URL_FACE_RECOGNITION;
        // Use this to cancel request
        final String tag_custom_multi_part_obj = "custom_multi_part_obj_req";

        final Snackbar snackbar = Snackbar
                .make(coordinatorLayout, "Please wait..", Snackbar.LENGTH_INDEFINITE);

        snackbar.show();

        // Custom Volley Request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, url,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        snackbar.dismiss();
                        try {
                            JSONObject obj_A = new JSONObject(new String(response.data));
                            JSONArray arr = obj_A.getJSONArray("FaceMatches");
                            if (arr.length() > 0) {
                                JSONObject obj_B = arr.getJSONObject(0);
                                double matchPercentage = obj_B.getDouble("Similarity");
                                if (matchPercentage < 80) {
                                    Toast.makeText(getActivity(), "Face identification failed", Toast.LENGTH_SHORT).show();
                                    isAttachedFarmPhoto = true;
                                    cvBtnAttachFarmPhoto.setBackgroundColor(Color.parseColor("#FD7D00"));
                                } else {
                                    isAttachedFarmPhoto = true;
                                }
                            } else {
                                Toast.makeText(getActivity(), "Face identification failed", Toast.LENGTH_SHORT).show();
                                isAttachedFarmPhoto = true;
                            }
                        } catch (JSONException e) {
                            snackbar.dismiss();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
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
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                final String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJGSUFQUDEwMSIsImlhdCI6MTUxNjIzOTAyMn0._jKrqO1xcUoKoguQgzhhbB3yML0GfsNTo7Mm14Bw0Bo";//Prod
                headers.put("app-access-token", token);
                return headers;
            }

            /*
             * Here we are passing image by renaming it with a unique name
             * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imgNameS = System.currentTimeMillis() + 1;
                long imgNameT = System.currentTimeMillis() + 2;
                params.put("source_img", new DataPart(imgNameS + ".jpg", getFileDataFromDrawable(image_s)));
                params.put("target_img", new DataPart(imgNameT + ".jpg", getFileDataFromDrawable(image_t)));
                return params;
            }
        };

        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(volleyMultipartRequest, tag_custom_multi_part_obj);
    }

    private void AddDataWithFile() {
        String url = "";
        if (userType.equals("farmer"))
            url = EndPoints.URL_FARMER_ADD_FORM_FARM_AUTH;
        else if (userType.equals("officer")) {
            switch (intUserType) {
                case 1:// FIE
                    url = EndPoints.URL_EMP_ADD_FORM_FARM_AUTH;
                    break;
                case 2:
                    url = EndPoints.URL_AGENT_ADD_FORM_FARM_AUTH;
                    break;
                case 3:
                    url = EndPoints.URL_FPO_ADD_FORM_FARM_AUTH;
                    break;
                case 4:
                    url = EndPoints.URL_BANK_REP_ADD_FORM_FARM_AUTH;
                    break;
                default:
                    Toast.makeText(getActivity(), "Invalid userid!", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
        if (url.isEmpty())
            return;

        final String tag_custom_multi_part_obj = "custom_multi_part_obj_req";

        final Snackbar snackbar = Snackbar
                .make(coordinatorLayout, "Saving..", Snackbar.LENGTH_INDEFINITE);

        snackbar.show();

        // Custom Volley Request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, url,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));
                            boolean isSuccess = obj.getBoolean("success");
                            String message = obj.getString("msg");
                            if (isSuccess) {
                                snackbar.dismiss();
                                //gotoDashboard();
                                /*EditLevelThreeFragment editLevelThreeFragment = new EditLevelThreeFragment();
                                // Pass mode
                                Bundle args = new Bundle();
                                //args.putString("MODE", "add");
                                args.putString("FID", fId);
                                editLevelThreeFragment.setArguments(args);
                                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.fragment_container, editLevelThreeFragment, "EditFinalSubmitFragment");
                                transaction.addToBackStack(null);
                                transaction.commit();
                                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();*/
                                //Edited
                                EditFinalSubmitFragment nextFrag = new EditFinalSubmitFragment();
                                // Pass mode
                                Bundle args = new Bundle();
                                //args.putString("MODE", "add");
                                args.putString("FID", fId);
                                nextFrag.setArguments(args);
                                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.fragment_container, nextFrag, "EditFinalSubmitFragment");
                                transaction.addToBackStack(null);
                                transaction.commit();
                                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            snackbar.dismiss();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
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
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("x-access-token", token);
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                String gpsCoordinates = tietGpsCoordinate.getText().toString().trim();
                String captureDate = tietCaptureDate.getText().toString().trim();
                String landDocNumber = tietLandDocNo.getText().toString().trim();
                String landDocName = spinnerLandDoc.getSelectedItem().toString();
                boolean doneLevel = true;
                // Add Parameters
                params.put("user_id", fId);
                params.put("farm_coordinates", gpsCoordinates);
                params.put("capture_date", captureDate);
                params.put("farmer_land_doc_name", landDocName);
                params.put("farmer_land_doc_number", landDocNumber);
                params.put("farmer_land_doc_valid", String.valueOf(islandDocValid));
                params.put("farmer_land_doc_verification_id", landDocVerifyId);
                params.put("done_level", String.valueOf(doneLevel));

                return params;
            }

            /*
             * Here we are passing image by renaming it with a unique name
             * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imgFarmPhoto = System.currentTimeMillis();
                long imgNameLandDoc = System.currentTimeMillis() + 1;
                if (isAttachedFarmPhoto) {
                    params.put("img_farm_photo", new DataPart(imgFarmPhoto + ".jpg", getFileDataFromDrawable(bitmapFarmPhoto)));
                }
                if (isAttachedLandDoc) {
                    params.put("img_land_doc", new DataPart(imgNameLandDoc + ".jpg", getFileDataFromDrawable(bitmapLandDocument)));
                }

                return params;
            }
        };

        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(volleyMultipartRequest, tag_custom_multi_part_obj);
    }

    private void EditDataWithFile() {
        String url = "";
        if (userType.equals("farmer"))
            url = EndPoints.URL_FARMER_UPDATE_FORM_FARM_AUTH;
        else if (userType.equals("officer")) {
            switch (intUserType) {
                case 1:// FIE
                    url = EndPoints.URL_EMP_UPDATE_FORM_FARM_AUTH + fId;
                    break;
                case 2:
                    url = EndPoints.URL_AGENT_UPDATE_FORM_FARM_AUTH + fId;
                    break;
                case 3:
                    url = EndPoints.URL_FPO_UPDATE_FORM_FARM_AUTH + fId;
                    break;
                case 4:
                    url = EndPoints.URL_BANK_REP_UPDATE_FORM_FARM_AUTH + fId;
                    break;
                default:
                    Toast.makeText(getActivity(), "Invalid userid!", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
        if (url.isEmpty())
            return;

        final String tag_custom_multi_part_obj = "custom_multi_part_obj_req";

        final Snackbar snackbar = Snackbar
                .make(coordinatorLayout, "Saving..", Snackbar.LENGTH_INDEFINITE);

        snackbar.show();

        // Custom Volley Request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.PUT, url,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));
                            boolean isSuccess = obj.getBoolean("success");
                            String message = obj.getString("msg");
                            if (isSuccess) {
                                snackbar.dismiss();
                                gotoDashboard();
                                /*Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                                EditLevelThreeFragment editLevelThreeFragment = new EditLevelThreeFragment();
                                // Pass mode
                                Bundle args = new Bundle();
                                //args.putString("MODE", "add");
                                args.putString("FID", fId);
                                editLevelThreeFragment.setArguments(args);
                                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.fragment_container, editLevelThreeFragment, "EditFinalSubmitFragment");
                                transaction.addToBackStack(null);
                                transaction.commit();*/
                                //Edited
                                EditFinalSubmitFragment nextFrag = new EditFinalSubmitFragment();
                                // Pass mode
                                Bundle args = new Bundle();
                                //args.putString("MODE", "add");
                                args.putString("FID", fId);
                                nextFrag.setArguments(args);
                                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.fragment_container, nextFrag, "EditFinalSubmitFragment");
                                transaction.addToBackStack(null);
                                transaction.commit();
                                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                                //onBackPressed();
                            }
                        } catch (JSONException e) {
                            snackbar.dismiss();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
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
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("x-access-token", token);
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                String gpsCoordinates = tietGpsCoordinate.getText().toString().trim();
                String captureDate = tietCaptureDate.getText().toString().trim();
                String landDocNumber = tietLandDocNo.getText().toString().trim();
                String landDocName = spinnerLandDoc.getSelectedItem().toString();
                // Add Parameters
                params.put("farm_coordinates", gpsCoordinates);
                params.put("capture_date", captureDate);
                params.put("farmer_land_doc_name", landDocName);
                params.put("farmer_land_doc_number", landDocNumber);
                params.put("farmer_land_doc_valid", String.valueOf(islandDocValid));
                params.put("farmer_land_doc_verification_id", landDocVerifyId);
                return params;
            }

            /*
             * Here we are passing image by renaming it with a unique name
             * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imgNameFarmPhoto = System.currentTimeMillis();
                long imgNameLandDoc = System.currentTimeMillis() + 1;
                if (isAttachedFarmPhoto) {
                    params.put("img_farm_photo", new DataPart(imgNameFarmPhoto + ".jpg", getFileDataFromDrawable(bitmapFarmPhoto)));
                }
                if (isAttachedLandDoc) {
                    params.put("img_land_doc", new DataPart(imgNameLandDoc + ".jpg", getFileDataFromDrawable(bitmapLandDocument)));
                }
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
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    //Set spinner text
    private int getIndex(Spinner spinner, String myString) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                return i;
            }
        }
        return 0;
    }

    @Override
    public void onBackPressed() {
        getActivity().getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CROP_FARM_PHOTO_IMAGE_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri1 = data.getParcelableExtra("path");
                filePath1 = uri1;
                try {
                    //Getting image from gallery
                    bitmapFarmPhoto = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri1);
                    isAttachedFarmPhoto = true;
                    ivViewFarmPhoto.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_view_doc_black));
                    cvBtnAttachFarmPhoto.setBackgroundColor(Color.parseColor("#FD7D00"));
                    // Api call
                    //CompareFaceSelfie(bitmapPassportSizePhoto, bitmapFarmPhoto);
                    OcrFarmPhoto();
                } catch (Exception e) {
                    isAttachedFarmPhoto = false;
                    e.printStackTrace();
                }
            }
        } else if (requestCode == FARM_PHOTO_IMAGE_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri2 = data.getParcelableExtra("path");
                filePath1 = uri2;
                try {
                    //Getting image from gallery
                    bitmapFarmPhoto = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri2);
                    isAttachedFarmPhoto = true;
                    ivViewFarmPhoto.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_view_doc_black));
                    cvBtnAttachFarmPhoto.setBackgroundColor(Color.parseColor("#FD7D00"));
                    // Api call
                    OcrFarmPhoto();
                } catch (Exception e) {
                    isAttachedFarmPhoto = false;
                    e.printStackTrace();
                }
            }
        } else if (requestCode == CROP_LAND_DOC_IMAGE_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri3 = data.getParcelableExtra("path");
                filePath2 = uri3;
                try {
                    //Getting image from gallery
                    bitmapLandDocument = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri3);
                    isAttachedLandDoc = true;
                    ivViewLandDoc.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_view_doc_black));
                    cvBtnAttachLandDoc.setBackgroundColor(Color.parseColor("#FD7D00"));
                } catch (Exception e) {
                    isAttachedLandDoc = false;
                    e.printStackTrace();
                }
            }
        }
    }

    private void gotoDashboard() {
        if (userType.equals("farmer")) {
            HomeFarmerFragment homeFarmerFragment = new HomeFarmerFragment();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, homeFarmerFragment, "ScoreCardSubscriptionFragment")
                    .commit();
            removeAllFragments(getActivity().getSupportFragmentManager());
        } else if (userType.equals("officer")) {
            HomeFragment homeFragment = new HomeFragment();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, homeFragment, "ScoreCardSubscriptionFragment")
                    .commit();
            removeAllFragments(getActivity().getSupportFragmentManager());
        }
    }

    private void removeAllFragments(FragmentManager fragmentManager) {
        while (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }
}