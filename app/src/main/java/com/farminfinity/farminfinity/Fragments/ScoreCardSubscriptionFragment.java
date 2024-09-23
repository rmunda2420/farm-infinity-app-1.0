package com.farminfinity.farminfinity.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
import com.cashfree.pg.api.CFPaymentGatewayService;
import com.cashfree.pg.core.api.CFSession;
import com.cashfree.pg.core.api.CFTheme;
import com.cashfree.pg.core.api.callback.CFCheckoutResponseCallback;
import com.cashfree.pg.core.api.exception.CFException;
import com.cashfree.pg.core.api.utils.CFErrorResponse;
import com.cashfree.pg.ui.api.CFDropCheckoutPayment;
import com.cashfree.pg.ui.api.CFPaymentComponent;
import com.farminfinity.farminfinity.Activities.DropCheckoutActivity;
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

public class ScoreCardSubscriptionFragment extends BaseFragment implements CFCheckoutResponseCallback {
    private String userType;
    private String token;
    private int intUserType;

    private String fId = null;

    private CardView cvBtnProceed;
    private CardView cvBtnLater;
    private CardView cvBtnSkip;

    private TextView tvInstruction;

    private CoordinatorLayout coordinatorLayout;

    private AlertDialogManager mAlert;
    private SessionManager mSessionManager;
    private InputValidation mInputValidation;

    String orderId = null;
    String paymentSessionId = null;
    CFSession.Environment cfEnvironment = CFSession.Environment.PRODUCTION;

    public ScoreCardSubscriptionFragment() {
        // Required empty public constructor
        // Added imp
        try {
            CFPaymentGatewayService.getInstance().setCheckoutCallback(this);
        } catch (CFException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_score_card_subscription, container, false);

        // Get farmer id from previous fragment
        fId = getArguments().getString("FID");

        // Initialize objects
        mAlert = new AlertDialogManager();
        mSessionManager = new SessionManager(getActivity());
        mInputValidation = new InputValidation(getActivity());

        tvInstruction = (TextView) view.findViewById(R.id.tv_instruc_fragment_score_card_subscription);
        cvBtnProceed = (CardView) view.findViewById(R.id.cv_btn_proceed_fragment_score_card_subscription);
        cvBtnLater = (CardView) view.findViewById(R.id.cv_btn_later_fragment_score_card_subscription);
        //cvBtnSkip = (CardView) view.findViewById(R.id.cv_btn_skip_fragment_score_card_subscription);
        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.cl_fragment_score_card_subscription);

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
        if (userType.equals("farmer"))
            tvInstruction.setText(getResources().getString(R.string.string_sub_inst_farmer));

        cvBtnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPgOrder();
            }
        });

        cvBtnLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPgLink();
            }
        });

        /*cvBtnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Back to home
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
        });*/

        return view;
    }

    private void removeAllFragments(FragmentManager fragmentManager) {
        while (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    @Override
    public void onPaymentVerify(String s) {
        Log.e("onPaymentVerify", "verifyPayment triggered");
        // Start verifying your payment
        addSubscription();
    }

    @Override
    public void onPaymentFailure(CFErrorResponse cfErrorResponse, String s) {
        Log.e("onPaymentFailure " + orderId, cfErrorResponse.getMessage());
        Toast.makeText(getActivity(), "Payment failed", Toast.LENGTH_SHORT).show();
    }

    private void getPgOrder() {
        String url = EndPoints.URL_PG_ORDER + fId;
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
                        try {
                            orderId = response.getString("order_id");
                            paymentSessionId = response.getString("payment_session_id");
                            doDropCheckoutPayment();
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
                        } else if (networkResponse.statusCode == 410) {
                            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                            alert.setMessage(response.getString("msg"))
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            gotoDashboard();
                                        }
                                    });
                            alert.show();
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

    private void getPgLink() {
        String url = EndPoints.URL_PG_LINK + fId;
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
                        // Back to home
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
                        } else if (networkResponse.statusCode == 410) {
                            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                            alert.setMessage(response.getString("msg"))
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            gotoDashboard();
                                        }
                                    });
                            alert.show();
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

    private void doDropCheckoutPayment() {
        try {
            CFSession cfSession = new CFSession.CFSessionBuilder()
                    .setEnvironment(cfEnvironment)
                    .setPaymentSessionID(paymentSessionId)
                    .setOrderId(orderId)
                    .build();
            CFPaymentComponent cfPaymentComponent = new CFPaymentComponent.CFPaymentComponentBuilder()
                    // Shows only Card and UPI modes
                    .add(CFPaymentComponent.CFPaymentModes.CARD)
                    .add(CFPaymentComponent.CFPaymentModes.UPI)
                    .add(CFPaymentComponent.CFPaymentModes.WALLET)
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
            gatewayService.doPayment(getContext(), cfDropCheckoutPayment);
        } catch (CFException exception) {
            exception.printStackTrace();
        }
    }

    private void addSubscription() {
        String url = EndPoints.URL_FARMER_ADD_SUBSCRIPTION;
        // Tag used to cancel the request
        String tag_json_obj = "json_obj_req";

        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("userId", fId);
            jsonBody.put("isScoreCardSubscribed", true);
            jsonBody.put("isScoreCardSubscriptionActive", true);

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
                        try {
                            // Parsing json object response
                            boolean isSuccess = response.getBoolean("success");
                            String message = response.getString("msg");
                            if (isSuccess) {
                                snackbar.dismiss();
                                //Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                                // Back to home
                                AlertDialog.Builder alertBuilderSubscribe = new AlertDialog.Builder(getActivity());
                                alertBuilderSubscribe.setMessage("Thank you for subscribing to Farm Infinity credit scorecard. You can download your scorecard from the link sent via sms or login into the app with your registered mobile number to view and download.")
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                AlertDialog.Builder alertBuilderSubscribe = new AlertDialog.Builder(getActivity());
                                                alertBuilderSubscribe.setMessage("You will be redirected to dashboard. The scorecard will be visible next time when you login.")
                                                        .setCancelable(false)
                                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int id) {
                                                                gotoDashboard();
                                                            }
                                                        });
                                                alertBuilderSubscribe.show();
                                            }
                                        });
                                alertBuilderSubscribe.show();
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

    /* private void sendReport() {
        String url =  EndPoints.URL_FARMER_SEND_REPORT + fId;
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
                        // Back to home
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
    }*/

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
}