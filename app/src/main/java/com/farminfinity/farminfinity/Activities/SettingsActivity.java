package com.farminfinity.farminfinity.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
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
import com.farminfinity.farminfinity.App.AppController;
import com.farminfinity.farminfinity.BuildConfig;
import com.farminfinity.farminfinity.Helper.AlertDialogManager;
import com.farminfinity.farminfinity.Helper.EndPoints;
import com.farminfinity.farminfinity.Helper.InputValidation;
import com.farminfinity.farminfinity.Helper.SessionManager;
import com.farminfinity.farminfinity.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {
    private Context mContext;
    private AlertDialog mAlertDialog;
    private RelativeLayout rlChangeLanguage;
    private RelativeLayout rlAbout;
    private RelativeLayout rlChangePassword;
    private RelativeLayout rlClearCache;
    private RelativeLayout rlShareApp;
    private RelativeLayout rlPrivacyPolicy;

    private RelativeLayout rlTnc;

    private SessionManager sessionManager;
    private InputValidation mInputValidation;
    private AlertDialogManager mAlert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mContext = getApplicationContext();
        setupToolbar();
        initViews();
        initObject();
        initListeners();
    }

    private void initObject() {
        sessionManager = new SessionManager(mContext);
        mInputValidation = new InputValidation(mContext);
        mAlert = new AlertDialogManager();
    }

    private void initViews() {
        rlChangeLanguage = (RelativeLayout) findViewById(R.id.rl_changeLanguage_content_activity_settings);
        rlAbout = (RelativeLayout) findViewById(R.id.rl_version_content_activity_settings);
        rlChangePassword = (RelativeLayout) findViewById(R.id.rl_changePassword_content_activity_settings);
        rlClearCache = (RelativeLayout) findViewById(R.id.rl_clearCache_content_activity_settings);
        rlShareApp = (RelativeLayout) findViewById(R.id.rl_shareApp_content_activity_settings);
        rlPrivacyPolicy = (RelativeLayout) findViewById(R.id.rl_privacy_policy_content_activity_settings);
        rlTnc = (RelativeLayout) findViewById(R.id.rl_tnc_content_activity_settings);
    }

    private void initListeners() {
        rlChangeLanguage.setOnClickListener(this);
        rlAbout.setOnClickListener(this);
        rlChangePassword.setOnClickListener(this);
        rlClearCache.setOnClickListener(this);
        rlShareApp.setOnClickListener(this);
        rlPrivacyPolicy.setOnClickListener(this);
        rlTnc.setOnClickListener(this);
    }

    private void setupToolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_activity_settings);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Settings");
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_changeLanguage_content_activity_settings:
                Intent intentChLang = new Intent(SettingsActivity.this, ChangeLanguageActivity.class);
                startActivity(intentChLang);
                break;
            case R.id.rl_version_content_activity_settings:
                AlertDialog.Builder alertVersion = new AlertDialog.Builder(this);
                alertVersion.setTitle("Farm Infinity");
                alertVersion.setMessage(getResources().getString(R.string.lbl_about_text) + "\n\n" + getResources().getString(R.string.lbl_version))
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });

                alertVersion.show();
                break;
            case R.id.rl_changePassword_content_activity_settings:
                Intent intentChPswd = new Intent(SettingsActivity.this, ChangePasswordActivity.class);
                startActivity(intentChPswd);
                break;
            case R.id.rl_shareApp_content_activity_settings:
                String shareMsg = getResources().getString(R.string.lbl_share_msg) + "\n";
                shareMsg = shareMsg + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID.trim() + "\n";
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, shareMsg);
                startActivity(Intent.createChooser(intent, "Share"));
                Toast.makeText(SettingsActivity.this, getResources().getString(R.string.lbl_thank_msg), Toast.LENGTH_SHORT).show();
                break;
            case R.id.rl_clearCache_content_activity_settings:
                deleteCache(SettingsActivity.this);
                Toast.makeText(SettingsActivity.this, "Cache cleared successful.", Toast.LENGTH_SHORT).show();
                break;

            case R.id.rl_privacy_policy_content_activity_settings:
                Intent webIntent = new Intent(Intent.ACTION_VIEW);
                webIntent.setData(Uri.parse(getString(R.string.privacy_policy_url)));
                startActivity(webIntent);
                break;

            case R.id.rl_tnc_content_activity_settings:
                Intent webIntent_tnc = new Intent(Intent.ACTION_VIEW);
                webIntent_tnc.setData(Uri.parse(getString(R.string.terms_url)));
                startActivity(webIntent_tnc);
                break;

            default:
                break;
        }
    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }
}