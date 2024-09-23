package com.farminfinity.farminfinity.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

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
import com.cashfree.pg.api.CFPaymentGatewayService;
import com.cashfree.pg.core.api.CFSession;
import com.cashfree.pg.core.api.CFTheme;
import com.cashfree.pg.core.api.callback.CFCheckoutResponseCallback;
import com.cashfree.pg.core.api.exception.CFException;
import com.cashfree.pg.core.api.utils.CFErrorResponse;
import com.cashfree.pg.ui.api.CFDropCheckoutPayment;
import com.cashfree.pg.ui.api.CFPaymentComponent;
import com.farminfinity.farminfinity.App.AppController;
import com.farminfinity.farminfinity.Fragments.HomeFarmerFragment;
import com.farminfinity.farminfinity.Fragments.HomeFragment;
import com.farminfinity.farminfinity.Fragments.ScoreFragment;
import com.farminfinity.farminfinity.Helper.EndPoints;
import com.farminfinity.farminfinity.Model.EmiModel;
import com.farminfinity.farminfinity.R;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EmiIntRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<EmiModel> processedData;
    private RecyclerView mRecyclerView;

    private boolean isLoading = false;
    private int visibleThreshold = 1;

    private final int VIEW_TYPE_ITEM = 0, VIEW_TYPE_LOADING = 1;

    private String currentLoanId = null;
    private Double currentAmount;
    private String orderId = null;
    private String orderToken = null;

    private String token = null;

    private CoordinatorLayout coordinatorLayout;

    CFSession.Environment cfEnvironment = CFSession.Environment.SANDBOX;

    public EmiIntRecyclerAdapter(Context context, RecyclerView recyclerView, final ArrayList<EmiModel> processedData, CoordinatorLayout coordinatorLayout, String token) {
        this.context = context;
        this.mRecyclerView = recyclerView;
        this.processedData = processedData;
        this.coordinatorLayout = coordinatorLayout;
        this.token = token;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(context).inflate(R.layout.list_item_emi_int, parent, false);
            return new EmiIntRecyclerAdapter.ItemViewHolder(view);

        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.list_item_loading, parent, false);
            return new EmiIntRecyclerAdapter.LoadingViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (processedData.get(position) == null) {
            return VIEW_TYPE_LOADING;
        } else {
            return VIEW_TYPE_ITEM;
        }
    }

    public void setLoaded() {
        isLoading = false;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof EmiIntRecyclerAdapter.ItemViewHolder) {
            EmiModel emiModel = (EmiModel) processedData.get(position);
            final EmiIntRecyclerAdapter.ItemViewHolder viewHolder = (EmiIntRecyclerAdapter.ItemViewHolder) holder;
            viewHolder.emiNumber.setText("Service Installment: " + String.valueOf(emiModel.getEmi_number()));
            viewHolder.emiAmount.setText("Amount(Rs): " + String.valueOf(emiModel.getAmount()));
            viewHolder.emiDueDate.setText("Due Date: " + emiModel.getDue_date());
            if (emiModel.getStatus()) {
                viewHolder.btnQr.setEnabled(false);
                viewHolder.btnPaymentLink.setEnabled(false);
                viewHolder.emiStatus.setText("Status: Paid");
            } else {
                viewHolder.btnQr.setEnabled(true);
                viewHolder.btnPaymentLink.setEnabled(true);
                viewHolder.emiStatus.setText("Status: Due");
            }
        } else {
            EmiIntRecyclerAdapter.LoadingViewHolder loadingViewHolder = (EmiIntRecyclerAdapter.LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return processedData.size();
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView emiNumber;
        TextView emiAmount;
        TextView emiDueDate;
        TextView emiStatus;

        Button btnQr;

        Button btnPaymentLink;

        private ItemViewHolder(View itemView) {
            super(itemView);

            emiNumber = (TextView) itemView.findViewById(R.id.tv_emi_no_list_item_emi_int);
            emiAmount = (TextView) itemView.findViewById(R.id.tv_emi_amt_list_item_emi_int);
            emiDueDate = (TextView) itemView.findViewById(R.id.tv_emi_due_date_list_item_emi_int);
            emiStatus = (TextView) itemView.findViewById(R.id.tv_emi_status_date_list_item_emi_int);
            btnQr = (Button) itemView.findViewById(R.id.btn_emi_pay_qr_list_item_emi_int);
            btnPaymentLink = (Button) itemView.findViewById(R.id.btn_emi_pay_link_list_item_emi_int);

            btnQr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Feature not available", Toast.LENGTH_SHORT).show();
                }
            });
            btnPaymentLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EmiModel emiModel = (EmiModel) processedData.get(getAdapterPosition());
                    currentLoanId = emiModel.getLoan_id();
                    currentAmount = emiModel.getAmount();
                    getPgEmiLink();
                }
            });
        }
        private void getPgEmiLink() {
            String url = EndPoints.URL_PG_EMI_LINK + currentLoanId + "/" + currentAmount;
            // Tag used to cancel the request
            String tag_json_obj = "json_obj_req";

            final Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, "Please wait..", Snackbar.LENGTH_INDEFINITE);

            snackbar.show();

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            snackbar.dismiss();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    snackbar.dismiss();

                    NetworkResponse networkResponse = error.networkResponse;
                    String errorMessage = "Error!!";
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

    private static class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        private LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

                Drawable drawableProgress = DrawableCompat.wrap(progressBar.getIndeterminateDrawable());
                DrawableCompat.setTint(drawableProgress, ContextCompat.getColor(itemView.getContext(), android.R.color.darker_gray));
                progressBar.setIndeterminateDrawable(DrawableCompat.unwrap(drawableProgress));

            } else {
                progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(itemView.getContext(), android.R.color.darker_gray), PorterDuff.Mode.SRC_IN);
            }
        }
    }
}
