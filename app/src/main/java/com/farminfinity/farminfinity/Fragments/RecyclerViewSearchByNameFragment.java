package com.farminfinity.farminfinity.Fragments;

import android.os.Bundle;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.farminfinity.farminfinity.Adapters.SearchByNameRecyclerAdapter;
import com.farminfinity.farminfinity.App.AppController;
import com.farminfinity.farminfinity.Helper.EndPoints;
import com.farminfinity.farminfinity.Helper.SessionManager;
import com.farminfinity.farminfinity.Model.FarmerModel;
import com.farminfinity.farminfinity.R;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RecyclerViewSearchByNameFragment extends BaseFragment {
    private RecyclerView recyclerView;
    private CoordinatorLayout coordinatorLayout;

    private SessionManager mSessionManager;
    private SearchByNameRecyclerAdapter adapter;

    private final ArrayList<FarmerModel> processedData = new ArrayList<>();

    public RecyclerViewSearchByNameFragment() {
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
        View view = inflater.inflate(R.layout.fragment_recycler_view_search_by_name, container, false);

        String parameter = getArguments().getString("PARAM");
        String type = getArguments().getString("TYPE");

        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.cl_fragment_recycler_view_search_by_name);
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_fragment_recycler_view_search_by_name);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Initialize Objects
        mSessionManager = new SessionManager(getActivity());

        if (mSessionManager.isLoggedIn()) {
            if (type.equals("phone")) {
                GetFarmersByPhone(parameter);
            } else if (type.equals("name")) {
                GetFarmersByName(parameter);
            }
        } else
            Toast.makeText(getActivity(), "You have logged out", Toast.LENGTH_SHORT).show();

        return view;
    }

    private void GetFarmersByName(String name) {
        // Tag used to cancel the request
        String tag_json_obj = "json_obj_req";
        String url = EndPoints.URL_SEARCH_NAME + name;

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
                            JSONArray array = response.getJSONArray("data");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                FarmerModel farmerModel = new FarmerModel();
                                farmerModel.setId(object.getString("id"));
                                farmerModel.setApp_id(object.getString("app_id"));
                                farmerModel.setF_name(object.getString("first_name"));
                                farmerModel.setM_name(object.getString("middle_name"));
                                farmerModel.setL_name(object.getString("last_name"));
                                farmerModel.setPh_no(object.getString("ph_no"));
                                farmerModel.setVillage(object.getString("village"));
                                farmerModel.setCity_tow(object.getString("city_town"));
                                farmerModel.setLoan_amt_sought(object.getString("loan_amt_sought"));
                                farmerModel.setStatus(object.getInt("app_status"));
                                farmerModel.setCreated_at(object.getString("created_at"));
                                farmerModel.setModified_at(object.getString("modified_at"));
                                processedData.add(farmerModel);
                            }
                            adapter = new SearchByNameRecyclerAdapter(getActivity(), recyclerView, processedData);
                            recyclerView.setAdapter(adapter);
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
                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJGSUFQUDEwMiIsImlhdCI6MTUxNjIzOTAyMn0.YxElUmAy5J9_rLdQ5P7i-vbH4Tz2X3tRwhE1_WujrLM";
                headers.put("app-access-token", token);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    private void GetFarmersByPhone(String phone) {
        // Tag used to cancel the request
        String tag_json_obj = "json_obj_req";
        String url = EndPoints.URL_SEARCH_PHONE + phone;

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
                            JSONArray array = response.getJSONArray("data");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                FarmerModel farmerModel = new FarmerModel();
                                farmerModel.setId(object.getString("id"));
                                farmerModel.setApp_id(object.getString("app_id"));
                                farmerModel.setF_name(object.getString("first_name"));
                                farmerModel.setM_name(object.getString("middle_name"));
                                farmerModel.setL_name(object.getString("last_name"));
                                farmerModel.setPh_no(object.getString("ph_no"));
                                farmerModel.setVillage(object.getString("village"));
                                farmerModel.setCity_tow(object.getString("city_town"));
                                farmerModel.setLoan_amt_sought(object.getString("loan_amt_sought"));
                                farmerModel.setStatus(object.getInt("app_status"));
                                farmerModel.setCreated_at(object.getString("created_at"));
                                farmerModel.setModified_at(object.getString("modified_at"));
                                processedData.add(farmerModel);
                            }
                            adapter = new SearchByNameRecyclerAdapter(getActivity(), recyclerView, processedData);
                            recyclerView.setAdapter(adapter);
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
                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJGSUFQUDEwMiIsImlhdCI6MTUxNjIzOTAyMn0.YxElUmAy5J9_rLdQ5P7i-vbH4Tz2X3tRwhE1_WujrLM";
                headers.put("app-access-token", token);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }
}
