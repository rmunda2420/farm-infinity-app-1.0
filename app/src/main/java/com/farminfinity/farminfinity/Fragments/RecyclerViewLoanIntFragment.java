package com.farminfinity.farminfinity.Fragments;

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
import com.farminfinity.farminfinity.Adapters.ClientRecyclerAdapter;
import com.farminfinity.farminfinity.Adapters.EmptyRecyclerViewAdapter;
import com.farminfinity.farminfinity.Adapters.LoansIntRecyclerAdapter;
import com.farminfinity.farminfinity.Adapters.LoansRecyclerAdapter;
import com.farminfinity.farminfinity.App.AppController;
import com.farminfinity.farminfinity.Helper.EndPoints;
import com.farminfinity.farminfinity.Helper.SessionManager;
import com.farminfinity.farminfinity.Interface.ILoadMore;
import com.farminfinity.farminfinity.Model.FarmerModel;
import com.farminfinity.farminfinity.Model.LoanModel;
import com.farminfinity.farminfinity.R;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RecyclerViewLoanIntFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerViewLoan;
    private LinearLayoutManager layoutManager;
    private CoordinatorLayout coordinatorLayout;

    private SessionManager mSessionManager;
    private LoansIntRecyclerAdapter adapter;
    private EmptyRecyclerViewAdapter emptyRecyclerViewAdapter;
    private final ArrayList<LoanModel> processedData = new ArrayList<>();

    private String userType = null;
    private String token = null;
    private int int_user_type;

    private String fId = null;

    public RecyclerViewLoanIntFragment() {
        // Required empty public constructor
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recycler_view_loan_int, container, false);
        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.cl_fragment_loan_int_recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.srl_fragment_loan_int_recycler_view);
        recyclerViewLoan = (RecyclerView) view.findViewById(R.id.rv_fragment_loan_int_recycler_view);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewLoan.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerViewLoan.getContext(),
                layoutManager.getOrientation());
        recyclerViewLoan.addItemDecoration(dividerItemDecoration);

        // Get farmer id from previous fragment
        fId = getArguments().getString("FID");

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
        GetAllLoans();
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

    private void GetAllLoans() {
        String url = "";
        switch (int_user_type){
            case 1:
                url = EndPoints.URL_EMP_GET_BORROWER_LOAN + fId;
                break;
            case 2:
                url = EndPoints.URL_AGENT_GET_BORROWER_LOAN + fId;
                break;
        }
        if (url.isEmpty()) {
            return;
        }
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
                            JSONArray array = response.getJSONArray("data");
                            if (array.length() != 0) {
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject object = array.getJSONObject(i);
                                    LoanModel loanModel = new LoanModel();
                                    loanModel.setLoan_id(object.getString("loan_id"));
                                    processedData.add(loanModel);
                                }
                                adapter = new LoansIntRecyclerAdapter(getActivity(), recyclerViewLoan, processedData);
                                recyclerViewLoan.setAdapter(adapter);
                            } else {
                                emptyRecyclerViewAdapter = new EmptyRecyclerViewAdapter("No loans to show");
                                recyclerViewLoan.setAdapter(emptyRecyclerViewAdapter);
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
                        emptyRecyclerViewAdapter = new EmptyRecyclerViewAdapter("Empty");
                        recyclerViewLoan.setAdapter(emptyRecyclerViewAdapter);
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