package com.farminfinity.farminfinity.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
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
import com.farminfinity.farminfinity.Adapters.EmiRecyclerAdapter;
import com.farminfinity.farminfinity.Adapters.EmptyRecyclerViewAdapter;
import com.farminfinity.farminfinity.Adapters.LoansRecyclerAdapter;
import com.farminfinity.farminfinity.App.AppController;
import com.farminfinity.farminfinity.Helper.EndPoints;
import com.farminfinity.farminfinity.Helper.SessionManager;
import com.farminfinity.farminfinity.Model.AmortizationModel;
import com.farminfinity.farminfinity.Model.EmiModel;
import com.farminfinity.farminfinity.Model.LoanModel;
import com.farminfinity.farminfinity.Model.RepaymentModel;
import com.farminfinity.farminfinity.R;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RecyclerViewEmiFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerViewEmi;
    private LinearLayoutManager layoutManager;
    private CoordinatorLayout coordinatorLayout;

    private SessionManager mSessionManager;
    private EmiRecyclerAdapter adapter;

    private EmptyRecyclerViewAdapter emptyRecyclerViewAdapter;
    private final ArrayList<EmiModel> processedData = new ArrayList<>();
    private String userType = null;
    private String token = null;
    private int int_user_type;
    private String loan_id;

    private static int B2B_PG_REQUEST_CODE = 777;

    public RecyclerViewEmiFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        loan_id = getArguments().getString("LOAN_ID");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recycler_view_emi, container, false);
        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.cl_fragment_emi_recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.srl_fragment_emi_recycler_view);
        recyclerViewEmi = (RecyclerView) view.findViewById(R.id.rv_fragment_emi_recycler_view);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewEmi.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerViewEmi.getContext(),
                layoutManager.getOrientation());
        recyclerViewEmi.addItemDecoration(dividerItemDecoration);

        // Initialize Objects
        swipeRefreshLayout.setOnRefreshListener(this);
        mSessionManager = new SessionManager(getActivity());

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
            int_user_type = allClaims.get("int_user_type").asInt();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //GetAllEmi();
        return view;
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    //Api call
                    swipeRefreshLayout.setRefreshing(false);
                } catch (Exception e) {
                    e.printStackTrace();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        }, 3000);
    }

    private void GetAllEmi() {
        String url = EndPoints.URL_FARMER_GET_EMIS + loan_id;

        // Tag used to cancel the request
        String tag_json_obj = "json_obj_req";

        final Snackbar snackbar = Snackbar
                .make(coordinatorLayout, "Please wait..", Snackbar.LENGTH_INDEFINITE);
        snackbar.show();

        processedData.clear();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            snackbar.dismiss();
                            // Parsing json object response
                            JSONArray array2 = response.getJSONArray("schedule");
                            if (array2.length() != 0) {
                                for (int i = 1; i < array2.length(); i++) {
                                    JSONObject object = array2.getJSONObject(i);
                                    EmiModel emiModel = new EmiModel();
                                    emiModel.setLoan_id(loan_id);
                                    emiModel.setEmi_number(object.getInt("emi_number"));
                                    emiModel.setAmount(object.getInt("emi_amount"));
                                    emiModel.setDue_date(object.getString("emi_due_date"));
                                    emiModel.setStatus(false);
                                    processedData.add(emiModel);
                                }
                                JSONArray array1 = response.getJSONArray("repayments");
                                for (int i = 0; i < array1.length(); i++) {
                                    JSONObject object = array1.getJSONObject(i);
                                    if (object.getInt("emiNumber") == processedData.get(i).getEmi_number()) {
                                        processedData.get(i).setStatus(true);
                                    }
                                }
                                adapter = new EmiRecyclerAdapter(getActivity(), recyclerViewEmi, processedData, coordinatorLayout, token);
                                recyclerViewEmi.setAdapter(adapter);
                            } else {
                                emptyRecyclerViewAdapter = new EmptyRecyclerViewAdapter("Empty");
                                recyclerViewEmi.setAdapter(emptyRecyclerViewAdapter);
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
                        emptyRecyclerViewAdapter = new EmptyRecyclerViewAdapter("No emi to show");
                        recyclerViewEmi.setAdapter(emptyRecyclerViewAdapter);
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();
        GetAllEmi();
    }
}
