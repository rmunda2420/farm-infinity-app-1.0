// Not in use
package com.farminfinity.farminfinity.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
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
import com.farminfinity.farminfinity.Helper.SessionManager;
import com.farminfinity.farminfinity.R;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FinalSubmitFragment extends BaseFragment {
    private String userType;
    private String token;
    private int intUserType;

    private String fId = null;

    private CheckBox cbFarmerDeclaration;
    private CheckBox cbOfficerDeclaration;

    private CardView cvBtnSubmit;

    private EditText etFarmerDeclarationTxt;
    private EditText etOfficerDeclarationTxt;

    private TextView tvCvBtnSubmitText;

    private CoordinatorLayout coordinatorLayout;
    private LinearLayout llOfficerDeclaration;

    private AlertDialogManager mAlert;
    private SessionManager mSessionManager;
    private InputValidation mInputValidation;

    public FinalSubmitFragment() {
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
        View view = inflater.inflate(R.layout.fragment_final_submit, container, false);

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

        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.cl_fragment_final_submit);
        llOfficerDeclaration = (LinearLayout) view.findViewById(R.id.ll_officer_decl_fragment_final_submit);

        etFarmerDeclarationTxt = (EditText) view.findViewById(R.id.et_farmer_decl_fragment_final_submit);
        etOfficerDeclarationTxt = (EditText) view.findViewById(R.id.et_officer_decl_fragment_final_submit);

        cbFarmerDeclaration = (CheckBox) view.findViewById(R.id.cb_farmer_declaration_fragment_final_submit);
        cbOfficerDeclaration = (CheckBox) view.findViewById(R.id.cb_officer_declaration_fragment_final_submit);

        cvBtnSubmit = (CardView) view.findViewById(R.id.cv_btn_save_fragment_final_submit);

        tvCvBtnSubmitText = (TextView) view.findViewById(R.id.tv_btn_submit_fragment_final_submit);

        if (userType.equals("officer"))
            llOfficerDeclaration.setVisibility(View.VISIBLE);

        cvBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateFields();
            }
        });

        return view;
    }

    private void InitDeclarationForm() {

    }

    private void ValidateFields() {
        if (userType.equals("farmer")) {
            if (!cbFarmerDeclaration.isChecked()) {
                mAlert.showAlertDialog(getActivity(), "Validation error!", "Please agree to the declaration", false);
                return;
            }
        } else if (userType.equals("officer")) {
            if (!cbFarmerDeclaration.isChecked()) {
                mAlert.showAlertDialog(getActivity(), "Validation error!", "Please agree to the declaration", false);
                return;
            }
            if (!cbOfficerDeclaration.isChecked()) {
                mAlert.showAlertDialog(getActivity(), "Validation error!", "Please agree to the declaration", false);
                return;
            }
        } else {
            return;
        }
        AddData();
    }

    private void AddData() {
        String url = "";
        if (userType.equals("farmer"))
            url = EndPoints.URL_FARMER_ADD_FORM_END;
        else if (userType.equals("officer")) {
            switch (intUserType) {
                case 1:// FIE
                    url = EndPoints.URL_EMP_ADD_FORM_END;
                    break;
                case 2:
                    url = EndPoints.URL_AGENT_ADD_FORM_END;
                    break;
                case 3:
                    url = EndPoints.URL_FPO_ADD_FORM_END;
                    break;
                case 4:
                    url = EndPoints.URL_BANK_REP_ADD_FORM_END;
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
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("user_id", fId);
            if (userType.equals("farmer")) {
                jsonBody.put("farmer_declaration", cbFarmerDeclaration.isChecked());
                jsonBody.put("farmer_declaration_text", etFarmerDeclarationTxt.getText().toString().trim());
            } else if (userType.equals("officer")) {
                jsonBody.put("user_id", fId);
                jsonBody.put("farmer_declaration", cbFarmerDeclaration.isChecked());
                jsonBody.put("officer_declaration", cbOfficerDeclaration.isChecked());
                jsonBody.put("farmer_declaration_text", etFarmerDeclarationTxt.getText().toString().trim());
                jsonBody.put("officer_declaration_text", etOfficerDeclarationTxt.getText().toString().trim());
            }
            jsonBody.put("done_level", true);
        } catch (Exception e) {
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
                                //ScoreCardSubscriptionFragment nextFrag = new ScoreCardSubscriptionFragment();
                                FarmAuthentication nextFrag = new FarmAuthentication();
                                // Pass mode
                                Bundle args = new Bundle();
                                //args.putString("MODE", "add");
                                args.putString("FID", fId);
                                nextFrag.setArguments(args);
                                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.fragment_container, nextFrag, "finalSubmitFragment");
                                transaction.addToBackStack(null);
                                transaction.commit();
                                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

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

    @Override
    public void onBackPressed() {
        getActivity().getSupportFragmentManager().popBackStack();
    }
}
