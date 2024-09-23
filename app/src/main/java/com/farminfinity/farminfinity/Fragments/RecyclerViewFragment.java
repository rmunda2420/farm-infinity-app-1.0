package com.farminfinity.farminfinity.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
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
import com.farminfinity.farminfinity.Adapters.ClientRecyclerAdapter;
import com.farminfinity.farminfinity.App.AppController;
import com.farminfinity.farminfinity.Helper.EndPoints;
import com.farminfinity.farminfinity.Helper.SessionManager;
import com.farminfinity.farminfinity.Interface.ILoadMore;
import com.farminfinity.farminfinity.Model.FarmerModel;
import com.farminfinity.farminfinity.R;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RecyclerViewFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private CoordinatorLayout coordinatorLayout;

    private SessionManager mSessionManager;
    private ClientRecyclerAdapter adapter;
    private final ArrayList<FarmerModel> processedData = new ArrayList<>();
    private static int offset = 0;
    private static final int LIMIT_VAL = 200;
    private static final int OFFSET_VAL = 200;

    private String userType = null;
    private String token = null;
    private int int_user_type;

    public RecyclerViewFragment() {
        // Required empty public constructor
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recycler_view, container, false);

        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.cl_fragment_recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.srl_fragment_recycler_view);
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_fragment_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

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
        GetAllOnboardedFarmer();

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

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.recycler_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    private void GetAllOnboardedFarmer() {
        String url = "";
        switch (int_user_type){
            case 1:
                url = EndPoints.URL_EMP_GET_ALL_ONBOARDED_FARMER.concat("0").concat("/").concat(String.valueOf(LIMIT_VAL));
                break;
            case 2:
                url = EndPoints.URL_AGENT_GET_ALL_ONBOARDED_FARMER.concat("0").concat("/").concat(String.valueOf(LIMIT_VAL));
                break;
            case 4:
                url = EndPoints.URL_GET_ALL_ONBOARDED_FARMER_ASSIGNED_TO_BANK_REP.concat("0").concat("/").concat(String.valueOf(LIMIT_VAL));
                break;
            default:
                break;
        }
        if (url.isEmpty()){return;}

        // Tag used to cancel the request
        String tag_json_obj = "json_obj_req";

        final Snackbar snackbar = Snackbar
                .make(coordinatorLayout, "Please wait..", Snackbar.LENGTH_INDEFINITE);
        snackbar.show();

        processedData.clear();
        offset = OFFSET_VAL;

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
                            adapter = new ClientRecyclerAdapter(getActivity(), recyclerView, processedData);
                            recyclerView.setAdapter(adapter);
                            adapter.setLoadMore(new ILoadMore() {
                                @Override
                                public void onLoadMore() {
                                    processedData.add(null);
                                    adapter.notifyItemInserted(processedData.size() - 1);
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            processedData.remove(processedData.size() - 1);
                                            adapter.notifyItemRemoved(processedData.size());
                                            LoadMoreFarmers(offset);
                                            offset = offset + LIMIT_VAL;
                                        }
                                    }, 2000);
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

    private void LoadMoreFarmers(int offset) {
        String url = "";
        switch (int_user_type){
            case 1:
                url = EndPoints.URL_EMP_GET_ALL_ONBOARDED_FARMER.concat(String.valueOf(offset)).concat("/").concat(String.valueOf(LIMIT_VAL));
                break;
            case 2:
                url = EndPoints.URL_AGENT_GET_ALL_ONBOARDED_FARMER.concat(String.valueOf(offset)).concat("/").concat(String.valueOf(LIMIT_VAL));
                break;
            case 4:
                url = EndPoints.URL_GET_ALL_ONBOARDED_FARMER_ASSIGNED_TO_BANK_REP.concat(String.valueOf(offset)).concat("/").concat(String.valueOf(LIMIT_VAL));
                break;
            default:
                break;
        }
        if (url.isEmpty()){return;}
        // Tag used to cancel the request
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
                                farmerModel.setLoan_amt_sought(object.getString("loan_amt_sought"));
                                processedData.add(farmerModel);
                            }
                            adapter.notifyDataSetChanged();
                            adapter.setLoaded();
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
                        String status = response.getString("status");
                        String message = response.getString("msg");

                        Log.e("Error Status", status);
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
}
