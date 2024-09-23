package com.farminfinity.farminfinity.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import com.farminfinity.farminfinity.Activities.LoginActivity;
import com.farminfinity.farminfinity.Activities.MainActivity;
import com.farminfinity.farminfinity.Activities.SettingsActivity;
import com.farminfinity.farminfinity.App.AppController;
import com.farminfinity.farminfinity.Helper.AlertDialogManager;
import com.farminfinity.farminfinity.Helper.EndPoints;
import com.farminfinity.farminfinity.Helper.InputValidation;
import com.farminfinity.farminfinity.Helper.SessionManager;
import com.farminfinity.farminfinity.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends BaseFragment {
    private static final String TAG = "HomeFragment";
    private String userType = null;
    private String token = null;
    private int intUserType;
    private boolean admin;
    private boolean changedFirstPswd;

    private CardView cvBtnAddClient;
    private CardView cvBtnEditClient;
    private CardView cvBtnViewClient;
    private CardView cvBtnSettings;
    private CardView cvBtnReports;

    private TextInputLayout tilOldPassword;
    private TextInputLayout tilNewPassword;
    private TextInputLayout tilConfirmNewPassword;

    private TextInputEditText tietOldPassword;
    private TextInputEditText tietNewPassword;
    private TextInputEditText tietConfirmNewPassword;

    private CoordinatorLayout coordinatorLayout;

    private AlertDialog alertDialog;

    private SessionManager mSessionManager;
    private InputValidation mInputValidation;
    private AlertDialogManager mAlert;

    private LinearLayout llReports;

    public HomeFragment() {
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        llReports = (LinearLayout) view.findViewById(R.id.ll_report_fragment_home);

        // Initialize objects
        mSessionManager = new SessionManager(getActivity());
        mInputValidation = new InputValidation(getActivity());
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
            changedFirstPswd = allClaims.get("changed_password").asBoolean();
        } catch (Exception e) {
            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
        }

        cvBtnAddClient = view.findViewById(R.id.cv_btn_add_client_home_fragment);
        cvBtnEditClient = view.findViewById(R.id.cv_btn_edit_client_home_fragment);
        cvBtnViewClient = view.findViewById(R.id.cv_btn_view_client_home_fragment);
        cvBtnSettings = view.findViewById(R.id.cv_btn_settings_home_fragment);
        cvBtnReports = view.findViewById(R.id.cv_btn_report_fragment_home);

        //Update view based on role
        UpdateView();

        cvBtnAddClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignupFarmerFragment signupFarmerFragment = new SignupFarmerFragment();

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, signupFarmerFragment, "HomeFragment")
                        .addToBackStack(null)
                        .commit();
            }
        });

        cvBtnEditClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder customAlertDialog = new AlertDialog.Builder(getActivity());
                customAlertDialog.setCancelable(true);
                View getFarmerView = LayoutInflater.from(getActivity()).inflate(R.layout.custom_alert_dialog_get_farmer, null, false);

                final TextInputLayout tilFarmerId = (TextInputLayout) getFarmerView.findViewById(R.id.til_farmerId_custom_alert_dialog_get_farmer);
                final TextInputEditText tietFarmerId = (TextInputEditText) getFarmerView.findViewById(R.id.tiet_farmerId_custom_alert_dialog_get_farmer);
                final Spinner spinnerSearchType = (Spinner) getFarmerView.findViewById(R.id.spinner_search_type_custom_alert_dialog_get_farmer);
                CardView cvOk = (CardView) getFarmerView.findViewById(R.id.cv_btn_ok_custom_alert_dialog_get_farmer);

                // Gender Spinner Drop down elements
                List<String> lstSearchType = new ArrayList<>();
                lstSearchType.add("Search By Name");
                lstSearchType.add("Search By Phone");
                // Creating adapter for spinner
                ArrayAdapter<String> dataAdapterSearchType = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, lstSearchType);
                // Drop down layout style - list view spinner
                dataAdapterSearchType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // attaching data adapter to spinner
                spinnerSearchType.setAdapter(dataAdapterSearchType);

                customAlertDialog.setView(getFarmerView);
                final AlertDialog alertDialog = customAlertDialog.create();
                alertDialog.show();

                cvOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!mInputValidation.isInputEditTextFilled(tietFarmerId, tilFarmerId, getString(R.string.error_message_blank_field))) {
                            return;
                        }

                        // Next form
                        if (spinnerSearchType.getSelectedItem().toString().toLowerCase().equals("search by phone")) {
                            RecyclerViewSearchByNameFragment nextFrag = new RecyclerViewSearchByNameFragment();
                            Bundle args = new Bundle();
                            args.putString("PARAM", tietFarmerId.getText().toString().trim());
                            args.putString("TYPE", "phone");
                            nextFrag.setArguments(args);
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.fragment_container, nextFrag, "HomeFragment")
                                    .addToBackStack(null)
                                    .commit();
                        } else if (spinnerSearchType.getSelectedItem().toString().toLowerCase().equals("search by name")) {
                            RecyclerViewSearchByNameFragment nextFrag = new RecyclerViewSearchByNameFragment();
                            Bundle args = new Bundle();
                            args.putString("PARAM", tietFarmerId.getText().toString().trim());
                            args.putString("TYPE", "name");
                            nextFrag.setArguments(args);
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.fragment_container, nextFrag, "HomeFragment")
                                    .addToBackStack(null)
                                    .commit();
                        }

                        alertDialog.dismiss();

                        /*if (mSessionManager.isLoggedIn()) {
                            HashMap<String, String> userInfo = mSessionManager.getUserDetails();
                            final String token = userInfo.get("Token");
                            final String farmerPhoneOrName = tietFarmerId.getText().toString().trim();
                            // Check farmer form api request
                            // Tag used to cancel the request
                            String tag_json_obj = "json_obj_req";
                            String url = "";
                            if (spinnerSearchType.getSelectedItem().toString().toLowerCase().equals("search by phone")) {
                                url = EndPoints.URL_SEARCH_PHONE + farmerPhoneOrName;
                            } else if (spinnerSearchType.getSelectedItem().toString().toLowerCase().equals("search by name")) {
                                url = EndPoints.URL_SEARCH_NAME + farmerPhoneOrName;
                            }

                            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url, null,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            try {
                                                // Parsing json object response
                                                boolean isSuccess = response.getBoolean("success");
                                                if ((isSuccess)) {
                                                    // Next form
                                                    if (spinnerSearchType.getSelectedItem().toString().toLowerCase().equals("search by phone")) {
                                                        EditLevelOneFragment nextFrag = new EditLevelOneFragment();
                                                        Bundle args = new Bundle();
                                                        args.putString("PHONE", farmerPhoneOrName);
                                                        //args.putString("MODE", "edit");
                                                        nextFrag.setArguments(args);
                                                        getActivity().getSupportFragmentManager().beginTransaction()
                                                                .replace(R.id.fragment_container, nextFrag, "HomeFragment")
                                                                .addToBackStack(null)
                                                                .commit();
                                                    } else if (spinnerSearchType.getSelectedItem().toString().toLowerCase().equals("search by name")) {
                                                        RecyclerViewSearchByNameFragment nextFrag = new RecyclerViewSearchByNameFragment();
                                                        Bundle args = new Bundle();
                                                        args.putString("NAME", farmerPhoneOrName);
                                                        nextFrag.setArguments(args);
                                                        getActivity().getSupportFragmentManager().beginTransaction()
                                                                .replace(R.id.fragment_container, nextFrag, "HomeFragment")
                                                                .addToBackStack(null)
                                                                .commit();
                                                    }
                                                }
                                                alertDialog.dismiss();

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                                alertDialog.dismiss();
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
                                                errorMessage = message;
                                            } else if (networkResponse.statusCode == 401) {
                                                errorMessage = message + " Please login again";
                                            } else if (networkResponse.statusCode == 400) {
                                                errorMessage = message + " Check your inputs";
                                            } else if (networkResponse.statusCode == 500) {
                                                errorMessage = message + " Something is getting wrong";
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            alertDialog.dismiss();
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

                        } else {
                            alertDialog.dismiss();
                            Toast.makeText(getActivity(), getResources().getString(R.string.error_msg_logged_out), Toast.LENGTH_SHORT).show();
                        }*/
                    }
                });
            }
        });

        cvBtnViewClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecyclerViewFragment nextFrag = new RecyclerViewFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, nextFrag, "RecyclerViewFragment")
                        .addToBackStack(null)
                        .commit();
            }
        });

        cvBtnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private void UpdateView() {
        switch (intUserType) {
            case 1:
                cvBtnReports.setEnabled(true);
                llReports.setVisibility(View.VISIBLE);
                break;
            case 2:
                // Hide some functionality
                cvBtnReports.setEnabled(false);
                llReports.setVisibility(View.GONE);
                break;
            default:
                break;
        }

        if (!changedFirstPswd) {
            final AlertDialog.Builder customAlertDialog = new AlertDialog.Builder(getActivity());
            customAlertDialog.setCancelable(false);
            View customView = LayoutInflater.from(getActivity()).inflate(R.layout.custom_alert_dialog_reset_password, null, false);

            tilOldPassword = (TextInputLayout) customView.findViewById(R.id.til_old_password_custom_alert_dialog_reset_password);
            tilNewPassword = (TextInputLayout) customView.findViewById(R.id.til_new_password_custom_alert_dialog_reset_password);
            tilConfirmNewPassword = (TextInputLayout) customView.findViewById(R.id.til_confirm_new_password_custom_alert_dialog_reset_password);

            tietOldPassword = (TextInputEditText) customView.findViewById(R.id.tiet_old_password_custom_alert_dialog_reset_password);
            tietNewPassword = (TextInputEditText) customView.findViewById(R.id.tiet_new_password_custom_alert_dialog_rest_password);
            tietConfirmNewPassword = (TextInputEditText) customView.findViewById(R.id.tiet_confirm_new_password_custom_alert_dialog_reset_password);

            CardView cvOk = (CardView) customView.findViewById(R.id.cv_btn_ok_custom_alert_dialog_reset_password);

            customAlertDialog.setView(customView);
            alertDialog = customAlertDialog.create();
            alertDialog.show();

            cvOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ValidateFields();
                }
            });
        }
    }

    private void ValidateFields() {
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
            mAlert.showAlertDialog(getActivity(), "Validation error", getResources().getString(R.string.error_msg_pswd_len), false);
            return;
        }
        if (!tietNewPassword.getText().toString().trim().equals(tietConfirmNewPassword.getText().toString().trim())) {
            mAlert.showAlertDialog(getActivity(), "Validation error", getResources().getString(R.string.error_msg_pswd_match), false);
            return;
        }

        ChangePassword();
    }

    private void ChangePassword() {
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
                    Toast.makeText(getActivity(), "Invalid userid!", Toast.LENGTH_SHORT).show();
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
                                alertDialog.dismiss();
                                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                                removeAllFragments(getActivity().getSupportFragmentManager());
                                //Redirect to login page
                                mSessionManager.logoutUser();
                                //finalize();
                            }
                        } catch (JSONException e) {
                            //alertDialog.dismiss();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //alertDialog.dismiss();
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

    private void removeAllFragments(FragmentManager fragmentManager) {
        while (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }
}
