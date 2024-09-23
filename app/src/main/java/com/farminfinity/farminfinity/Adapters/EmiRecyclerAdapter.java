package com.farminfinity.farminfinity.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
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
import com.farminfinity.farminfinity.Activities.CheckoutActivity;
import com.farminfinity.farminfinity.App.AppController;
import com.farminfinity.farminfinity.Fragments.PaymentAcknowledgeFragment;
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
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EmiRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements CFCheckoutResponseCallback {
    private Context context;
    private ArrayList<EmiModel> processedData;
    private RecyclerView mRecyclerView;

    private boolean isLoading = false;
    private int visibleThreshold = 1;

    private final int VIEW_TYPE_ITEM = 0, VIEW_TYPE_LOADING = 1;

    private String currentLoanId = null;
    private Double currentAmount;
    //private String orderId = null;
    //private String orderToken = null;

    private String token = null;

    private static int B2B_PG_REQUEST_CODE = 777;

    private CoordinatorLayout coordinatorLayout;

    CFSession.Environment cfEnvironment = CFSession.Environment.PRODUCTION;

    public EmiRecyclerAdapter(Context context, RecyclerView recyclerView, final ArrayList<EmiModel> processedData, CoordinatorLayout coordinatorLayout, String token) {
        this.context = context;
        this.mRecyclerView = recyclerView;
        this.processedData = processedData;
        this.coordinatorLayout = coordinatorLayout;
        this.token = token;
        try {
            CFPaymentGatewayService.getInstance().setCheckoutCallback(this);
        } catch (CFException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(context).inflate(R.layout.list_item_emi, parent, false);
            return new EmiRecyclerAdapter.ItemViewHolder(view);

        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.list_item_loading, parent, false);
            return new EmiRecyclerAdapter.LoadingViewHolder(view);
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
        if (holder instanceof EmiRecyclerAdapter.ItemViewHolder) {
            EmiModel emiModel = (EmiModel) processedData.get(position);
            final EmiRecyclerAdapter.ItemViewHolder viewHolder = (EmiRecyclerAdapter.ItemViewHolder) holder;
            viewHolder.emiNumber.setText("Service Installment: " + String.valueOf(emiModel.getEmi_number()));
            viewHolder.emiAmount.setText("Amount(Rs): " + String.valueOf(emiModel.getAmount()));
            viewHolder.emiDueDate.setText("Due Date: " + emiModel.getDue_date());
            if (emiModel.getStatus()) {
                viewHolder.emiStatus.setEnabled(false);
                viewHolder.emiStatus.setText("Paid");
            } else {
                viewHolder.emiStatus.setEnabled(true);
                viewHolder.emiStatus.setText("Pay Now");
            }
        } else {
            EmiRecyclerAdapter.LoadingViewHolder loadingViewHolder = (EmiRecyclerAdapter.LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return processedData.size();
    }

    @Override
    public void onPaymentVerify(String s) {
        addRepayment(s);
    }

    @Override
    public void onPaymentFailure(CFErrorResponse cfErrorResponse, String s) {
        PaymentAcknowledgeFragment nextFrag = new PaymentAcknowledgeFragment();
        Bundle args = new Bundle();
        args.putString("MSG", "You payment was unsuccessful");
        args.putString("COLOR", "#ff0000");
        nextFrag.setArguments(args);
        ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, nextFrag, "Adapter")
                .addToBackStack(null)
                .commit();
    }

    private void getPgOrderEmi() {
        final String[] url = {EndPoints.URL_PG_PP_ORDER + currentLoanId + "/" + currentAmount};
        // Tag used to cancel the request
        String tag_json_obj = "json_obj_req";

        final Snackbar snackbar = Snackbar
                .make(coordinatorLayout, "Please wait..", Snackbar.LENGTH_INDEFINITE);

        snackbar.show();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url[0], null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        snackbar.dismiss();
                        try {
                            //orderId = response.getString("order_id");
                            //orderToken = response.getString("order_token");
                            String url = response.getString("url");
                            Intent intent = new Intent(context.getApplicationContext(), CheckoutActivity.class);
                            intent.putExtra("URL", url);
                            context.startActivity(intent);
                            //doDropCheckoutPaymentEmi();
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
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
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

    /*private void doDropCheckoutPaymentEmi() {
        try {
            CFSession cfSession = new CFSession.CFSessionBuilder()
                    .setEnvironment(cfEnvironment)
                    .setOrderToken(orderToken)
                    .setOrderId(orderId)
                    .build();
            CFPaymentComponent cfPaymentComponent = new CFPaymentComponent.CFPaymentComponentBuilder()
                    // Shows only Card and UPI modes
                    //.add(CFPaymentComponent.CFPaymentModes.CARD)
                    .add(CFPaymentComponent.CFPaymentModes.UPI)
                    //.add(CFPaymentComponent.CFPaymentModes.WALLET)
                    .build();
            // Replace with your application's theme colors
            CFTheme cfTheme = new CFTheme.CFThemeBuilder()
                    .setNavigationBarBackgroundColor("#447DF0")
                    .setNavigationBarTextColor("#ffffff")
                    .setButtonBackgroundColor("#ff6d00")
                    .setButtonTextColor("#ffffff")
                    .setPrimaryTextColor("#000000")
                    .setSecondaryTextColor("#000000")
                    .build();
            CFDropCheckoutPayment cfDropCheckoutPayment = new CFDropCheckoutPayment.CFDropCheckoutPaymentBuilder()
                    .setSession(cfSession)
                    .setCFUIPaymentModes(cfPaymentComponent)
                    .setCFNativeCheckoutUITheme(cfTheme)
                    .build();
            CFPaymentGatewayService gatewayService = CFPaymentGatewayService.getInstance();
            gatewayService.doPayment(context, cfDropCheckoutPayment);
        } catch (CFException exception) {
            exception.printStackTrace();
        }
    }*/

    private void addRepayment(String s) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date now = new Date();

        String url = EndPoints.URL_FARMER_PAY_EMI;
        // Tag used to cancel the request
        String tag_json_obj = "json_obj_req";

        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("loan_id", currentLoanId);
            jsonBody.put("emi_amount_received", currentAmount);
            jsonBody.put("emi_status", true);
            jsonBody.put("emi_received_date", dateFormat.format(now));
            jsonBody.put("emi_credit_date", dateFormat.format(now));
            jsonBody.put("payment_mode", "Online");
            jsonBody.put("ref_id", s);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        final Snackbar snackbar = Snackbar
                .make(coordinatorLayout, "Please wait..", Snackbar.LENGTH_INDEFINITE);

        snackbar.show();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Refresh
                        snackbar.dismiss();
                        PaymentAcknowledgeFragment nextFrag = new PaymentAcknowledgeFragment();
                        Bundle args = new Bundle();
                        args.putString("MSG", "You payment was successful");
                        args.putString("COLOR", "#008000");
                        args.putString("LOAN_ID", currentLoanId);
                        nextFrag.setArguments(args);
                        ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, nextFrag, "Adapter")
                                .addToBackStack(null)
                                .commit();

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
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String AppAccessKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJGSUFQUDEwMSIsImlhdCI6MTUxNjIzOTAyMn0._jKrqO1xcUoKoguQgzhhbB3yML0GfsNTo7Mm14Bw0Bo"; // Production
                //String AppAccessKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJGSUFQUDEwMSIsImlhdCI6MTUxNjIzOTAyMn0.mbwX1OXEXaY3BQoa5QPHRlBYKNgFQUpaf4wxqciEMuU";
                headers.put("app-access-token", AppAccessKey);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView emiNumber;
        TextView emiAmount;
        TextView emiDueDate;
        Button emiStatus;

        private ItemViewHolder(View itemView) {
            super(itemView);

            emiNumber = (TextView) itemView.findViewById(R.id.tv_emi_no_list_item_emi);
            emiAmount = (TextView) itemView.findViewById(R.id.tv_emi_amt_list_item_emi);
            emiDueDate = (TextView) itemView.findViewById(R.id.tv_emi_due_date_list_item_emi);
            emiStatus = (Button) itemView.findViewById(R.id.btn_emi_status_list_item_emi);

            emiStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EmiModel emiModel = (EmiModel) processedData.get(getAdapterPosition());
                    currentLoanId = emiModel.getLoan_id();
                    currentAmount = emiModel.getAmount();

                    //Intent intent = new Intent(context, CheckoutActivity.class);
                    //intent.putExtra("loaid", currentLoanId);
                    //intent.putExtra("amount", currentAmount.toString());
                    //((Activity) context).startActivity(intent);
                    getPgOrderEmi();
                }
            });
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

    /*private void phonePay(String checksum, String payload) {
        try {
            //PhonePe.setFlowId("Unique Id of the user") // Recommended, not mandatory , An alphanumeric string without any special character
            List<UPIApplicationInfo> upiApps = PhonePe.getUpiApps();
            B2BPGRequest b2BPGRequest = new B2BPGRequestBuilder()
                    .setData(payload)
                    .setChecksum(checksum)
                    .setUrl("/pg/v1/pay")
                    .build();
        } catch (PhonePeInitException exception) {
            exception.printStackTrace();
        }
    }*/

}
