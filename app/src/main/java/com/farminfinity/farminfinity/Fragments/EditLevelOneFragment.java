package com.farminfinity.farminfinity.Fragments;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.farminfinity.farminfinity.App.AppController;
import com.farminfinity.farminfinity.Helper.AlertDialogManager;
import com.farminfinity.farminfinity.Helper.EndPoints;
import com.farminfinity.farminfinity.Helper.InputValidation;
import com.farminfinity.farminfinity.Helper.MyLocation;
import com.farminfinity.farminfinity.Helper.SessionManager;
import com.farminfinity.farminfinity.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class EditLevelOneFragment extends BaseFragment {
    private static final String TAG = "LevelOneFragment";

    private String userType;
    private String token;
    private int intUserType;
    private String loginId = null;

    private String fId = null;
    private boolean isLevelDone = false;

    private CoordinatorLayout coordinatorLayout;
    private LinearLayout llSpouseName;

    private TextInputLayout tilFirstName;
    private TextInputLayout tilLastName;
    private TextInputLayout tilDob;
    private TextInputLayout tilAge;
    private TextInputLayout tilFatherName;
    private TextInputLayout tilSpouseName;
    private TextInputLayout tilPhone;
    private TextInputLayout tilAltPhone;
    private TextInputLayout tilEmail;
    private TextInputLayout tilAddressLine1;
    private TextInputLayout tilAddressLine2;
    private TextInputLayout tilCity;
    private TextInputLayout tilDistrict;
    private TextInputLayout tilPin;

    private EditText etMiddleName;
    private EditText etVillage;
    private EditText etLoanRemarks;
    private EditText etGpsLocation;

    private TextInputEditText tietFirstName;
    private TextInputEditText tietLastName;
    private TextInputEditText tietDob;
    private TextInputEditText tietAge;
    private TextInputEditText tietFatherName;
    private TextInputEditText tietSpouseName;
    private TextInputEditText tietPhone;
    private TextInputEditText tietAltPhone;
    private TextInputEditText tietEmail;
    private TextInputEditText tietAddressLine1;
    private TextInputEditText tietAddressLine2;
    private TextInputEditText tietCity;
    private TextInputEditText tietDistrict;
    private TextInputEditText tietPin;

    private ImageView ivDatePicker, ivGps;

    private TextView tvCvBtnSubmitText;

    private Spinner spinnerGender;
    private Spinner spinnerState;
    private Spinner spinnerLoanAmt;
    private Spinner spinnerLoanPurpose;

    private RadioGroup radioGroupMaritalStatus;

    private CardView cvBtnSave;

    private HorizontalStepView horizontalStepView;

    private DatePickerDialog.OnDateSetListener mDateSetListener;

    private SessionManager mSessionManager;
    private InputValidation mInputValidation;
    private AlertDialogManager mAlert;

    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

    private static String latitude = "";
    private static String longitude = "";

    private ArrayList<String> arr_pre_fill_data = new ArrayList<String>();

    private boolean qMarried = false;

    public EditLevelOneFragment() {
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
        View view = inflater.inflate(R.layout.fragment_edit_level_one, container, false);

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

        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.cl_fragment_edit_level_one);
        llSpouseName = (LinearLayout) view.findViewById(R.id.ll_spouseName_fragment_edit_level_one);

        tilFirstName = (TextInputLayout) view.findViewById(R.id.til_firstName_fragment_edit_level_one);
        tilLastName = (TextInputLayout) view.findViewById(R.id.til_lastName_fragment_edit_level_one);
        tilDob = (TextInputLayout) view.findViewById(R.id.til_dob_fragment_edit_level_one);
        tilAge = (TextInputLayout) view.findViewById(R.id.til_age_fragment_edit_level_one);
        tilFatherName = (TextInputLayout) view.findViewById(R.id.til_fatherName_fragment_edit_level_one);
        tilSpouseName = (TextInputLayout) view.findViewById(R.id.til_spouse_fragment_edit_level_one);
        tilPhone = (TextInputLayout) view.findViewById(R.id.til_phone_fragment_edit_level_one);
        tilAltPhone = (TextInputLayout) view.findViewById(R.id.til_AltPhone_fragment_edit_level_one);
        tilEmail = (TextInputLayout) view.findViewById(R.id.til_email_fragment_edit_level_one);
        tilAddressLine1 = (TextInputLayout) view.findViewById(R.id.til_AddressLineOne_fragment_edit_level_one);
        tilAddressLine2 = (TextInputLayout) view.findViewById(R.id.til_AddressLineTwo_fragment_edit_level_one);
        tilCity = (TextInputLayout) view.findViewById(R.id.til_city_fragment_edit_level_one);
        tilDistrict = (TextInputLayout) view.findViewById(R.id.til_district_fragment_edit_level_one);
        tilPin = (TextInputLayout) view.findViewById(R.id.til_pin_fragment_edit_level_one);

        tietFirstName = (TextInputEditText) view.findViewById(R.id.tiet_firstName_fragment_edit_level_one);
        tietLastName = (TextInputEditText) view.findViewById(R.id.tiet_lastName_fragment_edit_level_one);
        tietDob = (TextInputEditText) view.findViewById(R.id.tiet_dob_fragment_edit_level_one);
        tietAge = (TextInputEditText) view.findViewById(R.id.tiet_age_fragment_edit_level_one);
        tietFatherName = (TextInputEditText) view.findViewById(R.id.tiet_fatherName_fragment_edit_level_one);
        tietSpouseName = (TextInputEditText) view.findViewById(R.id.tiet_spouse_fragment_edit_level_one);
        tietPhone = (TextInputEditText) view.findViewById(R.id.tiet_phone_fragment_edit_level_one);
        tietAltPhone = (TextInputEditText) view.findViewById(R.id.tiet_AltPhone_fragment_edit_level_one);
        tietEmail = (TextInputEditText) view.findViewById(R.id.tiet_email_fragment_edit_level_one);
        tietAddressLine1 = (TextInputEditText) view.findViewById(R.id.tiet_AddressLineOne_fragment_edit_level_one);
        tietAddressLine2 = (TextInputEditText) view.findViewById(R.id.tiet_AddressLineTwo_fragment_edit_level_one);
        tietCity = (TextInputEditText) view.findViewById(R.id.tiet_city_fragment_edit_level_one);
        tietDistrict = (TextInputEditText) view.findViewById(R.id.tiet_district_fragment_edit_level_one);
        tietPin = (TextInputEditText) view.findViewById(R.id.tiet_pin_fragment_edit_level_one);

        etMiddleName = (EditText) view.findViewById(R.id.et_middleName_fragment_edit_level_one);
        etVillage = (EditText) view.findViewById(R.id.et_village_fragment_edit_level_one);
        etLoanRemarks = (EditText) view.findViewById(R.id.et_loan_remarks_fragment_edit_level_one);
        etGpsLocation = (EditText) view.findViewById(R.id.et_gpsLocation_fragment_edit_level_one);

        spinnerGender = (Spinner) view.findViewById(R.id.spinner_gender_fragment_edit_level_one);
        spinnerState = (Spinner) view.findViewById(R.id.spinner_state_fragment_edit_level_one);
        spinnerLoanAmt = (Spinner) view.findViewById(R.id.spinnerLoanAmt_fragment_edit_level_one);
        spinnerLoanPurpose = (Spinner) view.findViewById(R.id.spinnerLoanPurpose_fragment_edit_level_one);

        radioGroupMaritalStatus = (RadioGroup) view.findViewById(R.id.radioGroup_maritalStatus_fragment_edit_level_one);

        ivDatePicker = (ImageView) view.findViewById(R.id.iv_dp_fragment_edit_level_one);
        ivGps = (ImageView) view.findViewById(R.id.iv_gps_fragment_edit_level_one);

        tvCvBtnSubmitText = (TextView) view.findViewById(R.id.tv_btn_submit_fragment_edit_level_one);

        cvBtnSave = (CardView) view.findViewById(R.id.cv_btn_save_fragment_edit_level_one);

        cvBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidateFields();
            }
        });

        horizontalStepView = view.findViewById(R.id.step_view_fragment_edit_level_one);

        List<StepBean> stepsBeanList = new ArrayList<>();
        StepBean stepBean0 = new StepBean("1", 0);
        StepBean stepBean1 = new StepBean("2", -1);
        StepBean stepBean2 = new StepBean("3", -1);
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
                .setStepsViewIndicatorCompleteIcon(ContextCompat.getDrawable(getActivity(), R.drawable.complted))
                .setStepsViewIndicatorDefaultIcon(ContextCompat.getDrawable(getActivity(), R.drawable.default_icon))
                .setStepsViewIndicatorAttentionIcon(ContextCompat.getDrawable(getActivity(), R.drawable.attention));

        // Gender Spinner Drop down elements
        List<String> lstGender = new ArrayList<>();
        lstGender.add("--Select--");
        lstGender.add(getResources().getString(R.string.ddl_male));
        lstGender.add(getResources().getString(R.string.ddl_female));

        // State code Spinner Drop down elements
        List<String> lstState = new ArrayList<>();
        lstState.add("--Select--");
        /*lstState.add("Andaman and Nicobar Islands");
        lstState.add("Andhra Pradesh");
        lstState.add("Arunachal Pradesh");*/
        lstState.add("Assam");
        /*lstState.add("Bihar");
        lstState.add("Chandigarh");
        lstState.add("Chhattisgarh");
        lstState.add("Dadra and Nagar Haveli and Daman and Diu");
        lstState.add("Delhi");
        lstState.add("Goa");
        lstState.add("Gujarat");
        lstState.add("Haryana");
        lstState.add("Himachal Pradesh");
        lstState.add("Jammu and Kashmir");
        lstState.add("Jharkhand");
        lstState.add("Karnataka");
        lstState.add("Kerala");
        lstState.add("Ladakh");
        lstState.add("Lakshadweep");*/
        lstState.add("Madhya Pradesh");
        lstState.add("Maharashtra");
        /*lstState.add("Manipur");
        lstState.add("Meghalaya");
        lstState.add("Mizoram");
        lstState.add("Nagaland");*/
        lstState.add("Odisha");
        /*lstState.add("Puducherry");
        lstState.add("Punjab");
        lstState.add("Rajasthan");
        lstState.add("Sikkim");
        lstState.add("Tamil Nadu");
        lstState.add("Telangana");
        lstState.add("Uttar Pradesh");
        lstState.add("Uttarakhand");
        lstState.add("West Bengal");*/
        // Loan Amount Spinner Drop down elements
        List<String> lstLoanAmt = new ArrayList<>();
        lstLoanAmt.add("--Select--");
        lstLoanAmt.add("Rs.5,000-Rs.10,000");
        lstLoanAmt.add("Rs.10,000-Rs.20,000");
        lstLoanAmt.add("Rs.20,000-Rs.50,000");
        lstLoanAmt.add("Rs.50,000-Rs.1 Lakh");
        lstLoanAmt.add("Rs.1 Lakh-5 Lakh");
        lstLoanAmt.add("Rs.5 Lakh/Above");
        // Loan Purpose Spinner Drop down elements
        List<String> lstLoanPurpose = new ArrayList<>();
        lstLoanPurpose.add("--Select--");
        lstLoanPurpose.add("Crop Input Purchase");
        lstLoanPurpose.add("Livestock Purchase");
        lstLoanPurpose.add("Animal Feed Purchase");
        lstLoanPurpose.add("Farm Equipment Purchase");
        lstLoanPurpose.add("Working Capital");
        lstLoanPurpose.add("Infrastructure Improvement");
        lstLoanPurpose.add("Land Purchase");
        lstLoanPurpose.add("Other");
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapterGender = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, lstGender);
        ArrayAdapter<String> dataAdapterState = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, lstState);
        ArrayAdapter<String> dataAdapterLoanAmt = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, lstLoanAmt);
        ArrayAdapter<String> dataAdapterLoanPurpose = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, lstLoanPurpose);

        // Drop down layout style - list view spinner
        dataAdapterGender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataAdapterState.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataAdapterLoanAmt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataAdapterLoanPurpose.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinnerGender.setAdapter(dataAdapterGender);
        spinnerState.setAdapter(dataAdapterState);
        spinnerLoanAmt.setAdapter(dataAdapterLoanAmt);
        spinnerLoanPurpose.setAdapter(dataAdapterLoanPurpose);

        // Initialize objects
        mAlert = new AlertDialogManager();
        mSessionManager = new SessionManager(getActivity());
        mInputValidation = new InputValidation(getActivity());

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                ++month;
                String sDay = "";
                String sMonth = "";
                if (dayOfMonth < 10) {
                    sDay = "0" + dayOfMonth;
                } else {
                    sDay = String.valueOf(dayOfMonth);
                }
                if (month < 10) {
                    sMonth = "0" + month;
                } else {
                    sMonth = String.valueOf(month);
                }
                String date = sDay + "-" + sMonth + "-" + year;
                tietDob.setText(date);
                try {
                    Date dob = sdf.parse(date);
                    int ageInYears = CalculateAge(dob);
                    tietAge.setText(String.valueOf(ageInYears));
                } catch (ParseException e) {
                    e.printStackTrace();
                    tietAge.setText(null);
                }
            }
        };

        tietPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        ivGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Update Location");
                builder.setCancelable(true);
                builder.setPositiveButton(
                        "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                MyLocation myLocation = new MyLocation();
                                myLocation.getLocation(getActivity(), locationResult);
                            }
                        });

                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        ivDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        v.getContext()
                        , android.R.style.Theme_Holo_Light_Dialog_MinWidth
                        , mDateSetListener
                        , year, month, day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });

        radioGroupMaritalStatus.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radBtn_qMarriedNo_fragment_edit_level_one:
                        llSpouseName.setVisibility(View.GONE);
                        qMarried = false;
                        tietSpouseName.setText(null);
                        break;
                    case R.id.radBtn_qMarriedYes_fragment_edit_level_one:
                        llSpouseName.setVisibility(View.VISIBLE);
                        qMarried = true;
                        break;
                    default:
                        break;
                }
            }
        });
        InitData();
        //if (!isLevelDone) // Changed
        //InitPreData();

        return view;
    }

    private void ValidateFields() {
        if (!mInputValidation.isInputEditTextFilled(tietFirstName, tilFirstName, getString(R.string.error_message_blank_field))) {
            return;
        }
        if (!mInputValidation.isInputEditTextFilled(tietDob, tilDob, getString(R.string.error_message_blank_field))) {
            return;
        }
        if (!mInputValidation.isInputEditTextFilled(tietAge, tilAge, getString(R.string.error_message_blank_field))) {
            return;
        }
        if (!mInputValidation.isInputEditTextFilled(tietFatherName, tilFatherName, getString(R.string.error_message_blank_field))) {
            return;
        }
        if (qMarried) {
            if (!mInputValidation.isInputEditTextFilled(tietSpouseName, tilSpouseName, getString(R.string.error_message_blank_field))) {
                return;
            }
        }
        if (!mInputValidation.isInputEditTextFilled(tietPhone, tilPhone, getString(R.string.error_message_blank_field))) {
            return;
        }
        if (!mInputValidation.isInputEditTextFilled(tietAddressLine1, tilAddressLine1, getString(R.string.error_message_blank_field))) {
            return;
        }
        if (!mInputValidation.isInputEditTextFilled(tietCity, tilCity, getString(R.string.error_message_blank_field))) {
            return;
        }
        if (!mInputValidation.isInputEditTextFilled(tietDistrict, tilDistrict, getString(R.string.error_message_blank_field))) {
            return;
        }
        if (!mInputValidation.isInputEditTextFilled(tietPin, tilPin, getString(R.string.error_message_blank_field))) {
            return;
        }
        if (!mInputValidation.isInputEditTextPhone(tietPhone, tilPhone, getString(R.string.error_message_invalid_phone_no))) {
            return;
        }
        if (!mInputValidation.isInputEditTextEmail(tietEmail, tilEmail, getString(R.string.error_message_invalid_email))) {
            return;
        }
        if (spinnerGender.getSelectedItem().toString().equals("--Select--")) {
            mAlert.showAlertDialog(getActivity(), "Validation Error", getResources().getString(R.string.err_msg_gender), false);
            return;
        }
        if (spinnerState.getSelectedItem().toString().equals("--Select--")) {
            mAlert.showAlertDialog(getActivity(), "Validation Error", getResources().getString(R.string.err_msg_state), false);
            return;
        }
        if (spinnerLoanAmt.getSelectedItem().toString().equals("--Select--")) {
            mAlert.showAlertDialog(getActivity(), "Validation Error", getResources().getString(R.string.err_msg_amount), false);
            return;
        }

        if (spinnerLoanPurpose.getSelectedItem().toString().equals("--Select--")) {
            mAlert.showAlertDialog(getActivity(), "Validation Error", getResources().getString(R.string.err_msg_loan_purpose), false);
            return;
        }

        if (!(Integer.valueOf(tietAge.getText().toString()) > 17 && Integer.valueOf(tietAge.getText().toString()) < 71)) {
            mAlert.showAlertDialog(getActivity(), "Validation Error", getResources().getString(R.string.err_msg_age), false);
            return;
        }
        if (tietPin.getText().toString().length() != 6) {
            mAlert.showAlertDialog(getActivity(), "Validation Error", getResources().getString(R.string.err_msg_pin), false);
            return;
        }
        // If level is not completed the insert else update
        if (isLevelDone)
            EditData();
        else
            AddData();
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
                            tietFirstName.setText(obj.getString("first_name"));
                            if (obj.getString("middle_name").equals("null"))
                                etMiddleName.setText("");
                            else
                                etMiddleName.setText(obj.getString("middle_name"));
                            tietLastName.setText(obj.getString("last_name"));
                            spinnerGender.setSelection(getIndex(spinnerGender, obj.getString("sex")));
                            tietPhone.setText(obj.getString("ph_no"));
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
            url = EndPoints.URL_FARMER_GET_LEVEL_ONE;
        else if (userType.equals("officer")) {
            switch (intUserType) {
                case 1:// FIE
                    url = EndPoints.URL_EMP_GET_LEVEL_ONE + fId;
                    break;
                case 2:
                    url = EndPoints.URL_AGENT_GET_LEVEL_ONE + fId;
                    break;
                case 3:
                    url = EndPoints.URL_FPO_GET_LEVEL_ONE + fId;
                    break;
                case 4:
                    url = EndPoints.URL_BANK_REP_GET_LEVEL_ONE + fId;
                    break;
                default:
                    Toast.makeText(getActivity(), "Invalid userid!", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
        if (url.isEmpty()) {
            return;
        }
        String tag_json_obj = "json_obj_req";

        final Snackbar snackbar = Snackbar
                .make(coordinatorLayout, "Please wait..", Snackbar.LENGTH_INDEFINITE);

        snackbar.show();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            snackbar.dismiss();
                            // Parsing json object response
                            JSONObject data = response.getJSONObject("data");
                            isLevelDone = data.getBoolean("done_level");
                            if (isLevelDone) {
                                tietFirstName.setText(data.getString("f_name"));
                                etMiddleName.setText(data.getString("m_name"));
                                tietLastName.setText(data.getString("l_name"));
                                tietDob.setText(data.getString("dob"));
                                try {
                                    Date dob = sdf.parse(data.getString("dob"));
                                    int ageInYears = CalculateAge(dob);
                                    tietAge.setText(String.valueOf(ageInYears));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                    tietAge.setText(null);
                                }
                                spinnerGender.setSelection(getIndex(spinnerGender, data.getString("gender")));
                                tietFatherName.setText(data.getString("father_name"));
                                String isMarried = data.getString("married_yn");
                                if (isMarried.equals("true")) {
                                    RadioButton child = (RadioButton) radioGroupMaritalStatus.getChildAt(0);
                                    child.setChecked(true);
                                    qMarried = true;
                                    llSpouseName.setVisibility(View.VISIBLE);
                                    tietSpouseName.setText(data.getString("spouse_name"));
                                }
                                tietPhone.setText(data.getString("reg_ph_no"));
                                tietAltPhone.setText(data.getString("alt_ph_no"));
                                tietEmail.setText(data.getString("email"));
                                String address = data.getString("address");
                                String[] componentsAddress = address.split(",");
                                tietAddressLine1.setText(componentsAddress[0].replace("-", ""));
                                tietAddressLine2.setText(componentsAddress[1].replace("-", ""));
                                etVillage.setText(data.getString("village"));
                                tietCity.setText(data.getString("city_town"));
                                tietDistrict.setText(data.getString("district"));
                                tietPin.setText(data.getString("pin_code"));
                                spinnerState.setSelection(getIndex(spinnerState, data.getString("state")));
                                spinnerLoanAmt.setSelection(getIndex(spinnerLoanAmt, data.getString("loan_amt_sought")));
                                spinnerLoanPurpose.setSelection(getIndex(spinnerLoanPurpose, data.getString("loan_purpose")));
                                etLoanRemarks.setText(data.getString("loan_remarks"));
                                etGpsLocation.setText(data.getString("gps_location"));
                            } else {
                                InitPreData();
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
                            errorMessage = "";
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
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    private void AddData() {
        String url = "";
        if (userType.equals("farmer"))
            url = EndPoints.URL_FARMER_ADD_FORM_ONE;
        else if (userType.equals("officer")) {
            switch (intUserType) {
                case 1:// FIE
                    url = EndPoints.URL_EMP_ADD_FORM_ONE;
                    break;
                case 2:
                    url = EndPoints.URL_AGENT_ADD_FORM_ONE;
                    break;
                case 3:
                    url = EndPoints.URL_FPO_ADD_FORM_ONE;
                    break;
                case 4:
                    url = EndPoints.URL_BANK_REP_ADD_FORM_ONE;
                    break;
                default:
                    Toast.makeText(getActivity(), "Invalid userid!", Toast.LENGTH_SHORT).show();
                    break;
            }
        } else {
            return;
        }
        if (url.isEmpty()) {
            return;
        }
        // Tag used to cancel the request
        String tag_json_obj = "json_obj_req";

        final String firstName = tietFirstName.getText().toString().trim();
        final String middleName = etMiddleName.getText().toString().trim();
        final String lastName = tietLastName.getText().toString().trim();
        final String dob = tietDob.getText().toString().trim();
        String age = tietAge.getText().toString().trim();
        String gender = spinnerGender.getSelectedItem().toString().trim();
        String fatherName = tietFatherName.getText().toString().trim();
        String spouseName = tietSpouseName.getText().toString().trim();
        final String phone = tietPhone.getText().toString().trim();
        String altPhone = tietAltPhone.getText().toString().trim();
        String email = tietEmail.getText().toString().trim();
        String addressLine1 = tietAddressLine1.getText().toString().trim();
        String addressLine2 = tietAddressLine2.getText().toString().trim();
        if (addressLine2.isEmpty())
            addressLine2 = addressLine2 + ("-");
        String village = etVillage.getText().toString().trim();
        String city = tietCity.getText().toString().trim();
        String district = tietDistrict.getText().toString().trim();
        String pin = tietPin.getText().toString().trim();
        String state = spinnerState.getSelectedItem().toString().trim();
        String loanAmt = spinnerLoanAmt.getSelectedItem().toString().trim();
        String loanPurpose = spinnerLoanPurpose.getSelectedItem().toString().trim();
        String loanRemarks = etLoanRemarks.getText().toString().trim();
        String gpsLocation = etGpsLocation.getText().toString().trim();

        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("user_id", fId);
            jsonBody.put("f_name", firstName);
            jsonBody.put("m_name", middleName);
            jsonBody.put("l_name", lastName);
            jsonBody.put("dob", dob);
            jsonBody.put("age", age);
            jsonBody.put("gender", gender);
            jsonBody.put("father_name", fatherName);
            jsonBody.put("married_yn", qMarried);
            jsonBody.put("spouse_name", spouseName);
            jsonBody.put("reg_ph_no", phone);
            jsonBody.put("alt_ph_no", altPhone);
            jsonBody.put("email", email);
            jsonBody.put("address", addressLine1 + "," + addressLine2);
            jsonBody.put("village", village);
            jsonBody.put("pin_code", pin);
            jsonBody.put("city_town", city);
            jsonBody.put("district", district);
            jsonBody.put("state", state);
            jsonBody.put("loan_amt_sought", loanAmt);
            jsonBody.put("loan_purpose", loanPurpose);
            jsonBody.put("loan_remarks", loanRemarks);
            jsonBody.put("gps_location", gpsLocation);
            jsonBody.put("done_level", true);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        final Snackbar snackbar = Snackbar
                .make(coordinatorLayout, "Saving..", Snackbar.LENGTH_INDEFINITE);

        snackbar.show();

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
                                // Move to next form
                                EditLevelTwoFragment editLevelTwoFragment = new EditLevelTwoFragment();
                                // Pass mode
                                Bundle args = new Bundle();
                                //args.putString("MODE", "add");
                                args.putString("FID", fId);
                                args.putString("F_NAME", firstName);
                                args.putString("M_NAME", middleName);
                                args.putString("L_NAME", lastName);
                                args.putString("DOB", dob);
                                editLevelTwoFragment.setArguments(args);
                                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.fragment_container, editLevelTwoFragment, "AddEditFragment");
                                transaction.addToBackStack(null);
                                transaction.commit();
                                //onBackPressed();
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

    private void EditData() {
        // Tag used to cancel the request
        String tag_json_obj = "json_obj_req";
        String url = "";
        if (userType.equals("farmer"))
            url = EndPoints.URL_FARMER_UPDATE_FORM_ONE;
        else if (userType.equals("officer")) {
            switch (intUserType) {
                case 1:// FIE
                    url = EndPoints.URL_EMP_UPDATE_FORM_ONE + fId;
                    break;
                case 2:
                    url = EndPoints.URL_AGENT_UPDATE_FORM_ONE + fId;
                    break;
                case 3:
                    url = EndPoints.URL_FPO_UPDATE_FORM_ONE + fId;
                    break;
                case 4:
                    url = EndPoints.URL_BANK_REP_UPDATE_FORM_ONE + fId;
                    break;
                default:
                    Toast.makeText(getActivity(), "Invalid userid!", Toast.LENGTH_SHORT).show();
                    break;
            }
        } else {
            return;
        }
        if (url.isEmpty())
            return;

        final String firstName = tietFirstName.getText().toString().trim();
        final String middleName = etMiddleName.getText().toString().trim();
        final String lastName = tietLastName.getText().toString().trim();
        final String dob = tietDob.getText().toString().trim();
        String age = tietAge.getText().toString().trim();
        String gender = spinnerGender.getSelectedItem().toString().trim();
        String fatherName = tietFatherName.getText().toString().trim();
        String spouseName = tietSpouseName.getText().toString().trim();
        final String phone = tietPhone.getText().toString().trim();
        String altPhone = tietAltPhone.getText().toString().trim();
        String email = tietEmail.getText().toString().trim();
        String addressLine1 = tietAddressLine1.getText().toString().trim();
        String addressLine2 = tietAddressLine2.getText().toString().trim();
        if (addressLine2.isEmpty())
            addressLine2 = addressLine2 + ("-");
        String village = etVillage.getText().toString().trim();
        String city = tietCity.getText().toString().trim();
        String district = tietDistrict.getText().toString().trim();
        String pin = tietPin.getText().toString().trim();
        String state = spinnerState.getSelectedItem().toString().trim();
        String loanAmt = spinnerLoanAmt.getSelectedItem().toString().trim();
        String loanPurpose = spinnerLoanPurpose.getSelectedItem().toString().trim();
        String loanRemarks = etLoanRemarks.getText().toString().trim();
        String gpsLocation = etGpsLocation.getText().toString().trim();

        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("f_name", firstName);
            jsonBody.put("m_name", middleName);
            jsonBody.put("l_name", lastName);
            jsonBody.put("dob", dob);
            jsonBody.put("age", age);
            jsonBody.put("gender", gender);
            jsonBody.put("father_name", fatherName);
            jsonBody.put("married_yn", qMarried);
            jsonBody.put("spouse_name", spouseName);
            jsonBody.put("reg_ph_no", phone);
            jsonBody.put("alt_ph_no", altPhone);
            jsonBody.put("email", email);
            jsonBody.put("address", addressLine1 + "," + addressLine2);
            jsonBody.put("village", village);
            jsonBody.put("pin_code", pin);
            jsonBody.put("city_town", city);
            jsonBody.put("district", district);
            jsonBody.put("state", state);
            jsonBody.put("loan_amt_sought", loanAmt);
            jsonBody.put("loan_purpose", loanPurpose);
            jsonBody.put("loan_remarks", loanRemarks);
            jsonBody.put("gps_location", gpsLocation);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        final Snackbar snackbar = Snackbar
                .make(coordinatorLayout, "Saving..", Snackbar.LENGTH_INDEFINITE);

        snackbar.show();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT, url, jsonBody,
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
                                EditLevelTwoFragment editLevelTwoFragment = new EditLevelTwoFragment();
                                // Pass mode
                                Bundle args = new Bundle();
                                //args.putString("MODE", "add");
                                args.putString("FID", fId);
                                args.putString("F_NAME", firstName);
                                args.putString("M_NAME", middleName);
                                args.putString("L_NAME", lastName);
                                args.putString("DOB", dob);
                                editLevelTwoFragment.setArguments(args);
                                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.fragment_container, editLevelTwoFragment, "AddEditFragment");
                                transaction.addToBackStack(null);
                                transaction.commit();
                                //onBackPressed();
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

    private int CalculateAge(Date birthDate) {
        int years = 0;
        int months = 0;
        int days = 0;

        //create calendar object for birth day
        Calendar birthDay = Calendar.getInstance();
        birthDay.setTimeInMillis(birthDate.getTime());

        //create calendar object for current day
        long currentTime = System.currentTimeMillis();
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(currentTime);

        //Get difference between years
        years = now.get(Calendar.YEAR) - birthDay.get(Calendar.YEAR);
        int currMonth = now.get(Calendar.MONTH) + 1;
        int birthMonth = birthDay.get(Calendar.MONTH) + 1;

        //Get difference between months
        months = currMonth - birthMonth;

        //if month difference is in negative then reduce years by one
        //and calculate the number of months.
        if (months < 0) {
            years--;
            months = 12 - birthMonth + currMonth;
            if (now.get(Calendar.DATE) < birthDay.get(Calendar.DATE))
                months--;
        } else if (months == 0 && now.get(Calendar.DATE) < birthDay.get(Calendar.DATE)) {
            years--;
            months = 11;
        }

        //Calculate the days
        if (now.get(Calendar.DATE) > birthDay.get(Calendar.DATE))
            days = now.get(Calendar.DATE) - birthDay.get(Calendar.DATE);
        else if (now.get(Calendar.DATE) < birthDay.get(Calendar.DATE)) {
            int today = now.get(Calendar.DAY_OF_MONTH);
            now.add(Calendar.MONTH, -1);
            days = now.getActualMaximum(Calendar.DAY_OF_MONTH) - birthDay.get(Calendar.DAY_OF_MONTH) + today;
        } else {
            days = 0;
            if (months == 12) {
                years++;
                months = 0;
            }
        }
        return years;
    }


    private MyLocation.LocationResult locationResult = new MyLocation.LocationResult() {
        @Override
        public void gotLocation(final Location location) {
            // Do something
            try {
                latitude = String.valueOf(location.getLatitude());
                longitude = String.valueOf(location.getLongitude());
                String finalLoc = latitude + "," + longitude;
                etGpsLocation.setText(finalLoc);
            } catch (Exception e) {
                Toast.makeText(getActivity(), "Could not get location. Try again", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    };

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

}
