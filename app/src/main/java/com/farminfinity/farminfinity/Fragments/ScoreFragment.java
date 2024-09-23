package com.farminfinity.farminfinity.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RadioButton;
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
import com.android.volley.toolbox.StringRequest;
import com.farminfinity.farminfinity.App.AppController;
import com.farminfinity.farminfinity.Helper.AlertDialogManager;
import com.farminfinity.farminfinity.Helper.EndPoints;
import com.farminfinity.farminfinity.Helper.InputValidation;
import com.farminfinity.farminfinity.Helper.SessionManager;
import com.farminfinity.farminfinity.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ScoreFragment extends Fragment {
    private String userType;
    private String token;

    private WebView mWebView;
    private FloatingActionButton fab;
    private ProgressBar mProgress;
    private CoordinatorLayout coordinatorLayout;

    private SessionManager mSessionManager;
    private InputValidation mInputValidation;
    private AlertDialogManager mAlert;

    private String fId = null;

    public ScoreFragment() {
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
        View view = inflater.inflate(R.layout.fragment_score, container, false);
        fId = getArguments().getString("FID");
        //Log.d("score", "http://api.farmeasytechnologies.com/static/pdf/umreport/2f83e2e6-8777-11ec-b851-bb9b98f4e69e.pdf");
        mWebView = (WebView) view.findViewById(R.id.webview_fragment_score);
        fab = (FloatingActionButton) view.findViewById(R.id.fab_fragment_score);
        mProgress = (ProgressBar) view.findViewById(R.id.progressBar_fragment_score);
        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.cl_fragment_score);

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
        //mWebView.loadUrl("https://docs.google.com/gview?embedded=true&url="
        //+ "http://api.farmeasytechnologies.com/static/pdf/umreport/" + fId + ".pdf");
        GetScorecardA();

        return view;
    }

    //create a function to create the print job
    private void createWebPrintJob(WebView webView) {

        //create object of print manager in your device
        PrintManager printManager = (PrintManager) getContext().getSystemService(Context.PRINT_SERVICE);

        //create object of print adapter
        PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter();

        //provide name to your newly generated pdf file
        String jobName = getString(R.string.app_name) + fId;

        //open print dialog
        printManager.print(jobName, printAdapter, new PrintAttributes.Builder().build());
    }

    private void GetScorecard() {
        String url = EndPoints.URL_FARMER_GET_REPORT + fId;
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
                            String url_score = response.getString("url");
                            mWebView.setWebChromeClient(new WebChromeClient() {
                                @Override
                                public void onProgressChanged(WebView view, int progress) {
                                    super.onProgressChanged(view, progress);
                                    if (progress < 100 && mProgress.getVisibility() == ProgressBar.GONE) {
                                        mProgress.setVisibility(ProgressBar.VISIBLE);
                                    }

                                    mProgress.setProgress(progress);
                                    if (progress == 100) {
                                        mProgress.setVisibility(ProgressBar.GONE);
                                        //fab.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                            mWebView.getSettings().setSupportZoom(true);
                            mWebView.getSettings().setJavaScriptEnabled(true);
                            fab.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    createWebPrintJob(mWebView);
                                }
                            });
                            //mWebView.loadUrl("https://docs.google.com/gview?embedded=true&url="
                            //+ url_score);
                            final String mimeType = "text/html";
                            final String encoding = "UTF-8";
                            String html = "<br /><br />Read the handouts please for tomorrow.<br /><br /><!--homework help homework" +
                                    "help help with homework homework assignments elementary school high school middle school" +
                                    "// --><font color='#60c000' size='4'><strong>Please!</strong></font>" +
                                    "<img src='http://www.homeworknow.com/hwnow/upload/images/tn_star300.gif'  />";
                            mWebView.loadDataWithBaseURL("", html, mimeType, encoding, "");
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
                String errorMessage = "Error!";
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

    private void GetScorecardA() {
        String url = EndPoints.URL_FARMER_GET_REPORT + fId;
        String tag_str = "json_str_req";
        final Snackbar snackbar = Snackbar
                .make(coordinatorLayout, "Please wait..", Snackbar.LENGTH_INDEFINITE);

        snackbar.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                snackbar.dismiss();

                mWebView.setWebChromeClient(new WebChromeClient() {
                    @Override
                    public void onProgressChanged(WebView view, int progress) {
                        super.onProgressChanged(view, progress);
                        if (progress < 100 && mProgress.getVisibility() == ProgressBar.GONE) {
                            mProgress.setVisibility(ProgressBar.VISIBLE);
                        }

                        mProgress.setProgress(progress);
                        if (progress == 100) {
                            mProgress.setVisibility(ProgressBar.GONE);
                            fab.setVisibility(View.VISIBLE);
                        }
                    }
                });
                mWebView.getSettings().setSupportZoom(true);
                mWebView.getSettings().setJavaScriptEnabled(true);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        createWebPrintJob(mWebView);
                    }
                });
                //mWebView.loadUrl("https://docs.google.com/gview?embedded=true&url="
                //+ url_score);
                final String mimeType = "text/html";
                final String encoding = "UTF-8";
                mWebView.loadDataWithBaseURL("", response, mimeType, encoding, "");

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                snackbar.dismiss();

                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Error!";
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

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(stringRequest, tag_str);
    }
}