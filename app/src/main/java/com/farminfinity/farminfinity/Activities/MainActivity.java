package com.farminfinity.farminfinity.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.farminfinity.farminfinity.Fragments.BaseFragment;
import com.farminfinity.farminfinity.Fragments.HomeFarmerFragment;
import com.farminfinity.farminfinity.Fragments.HomeFragment;
import com.farminfinity.farminfinity.Helper.AlertDialogManager;
import com.farminfinity.farminfinity.Helper.EndPoints;
import com.farminfinity.farminfinity.Helper.InputValidation;
import com.farminfinity.farminfinity.Helper.SessionManager;
import com.farminfinity.farminfinity.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.android.play.core.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener, EasyPermissions.PermissionCallbacks {
    private static final String TAG = "MainActivity";
    private String userType = null;
    private String token = null;
    private int intUserType;
    private boolean admin;

    private static final int CHECK_PERMISSION_CODE = 100;

    private SessionManager mSessionManager;
    private InputValidation mInputValidation;
    private AlertDialogManager mAlert;

    private String currentUserName = null;

    private static final int REQ_CODE_VERSION_UPDATE = 330;
    private AppUpdateManager appUpdateManager;
    private InstallStateUpdatedListener installStateUpdatedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkForAppUpdate();
        checkAppPermissions();

        Toolbar mToolbar = (Toolbar) findViewById(R.id.nav_action);
        mToolbar.setTitle("Home");
        setSupportActionBar(mToolbar);

        // Init object
        mSessionManager = new SessionManager(MainActivity.this);
        mInputValidation = new InputValidation(MainActivity.this);
        mAlert = new AlertDialogManager();

        // Checking if expired token time if yes then redirect to login page
        if (mSessionManager.isLoggedIn()) {
            HashMap<String, String> userInfo = mSessionManager.getUserDetails();
            String sessionStart = userInfo.get("Session");
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.ENGLISH);
            Date currentDate = new Date();
            try {
                Date loginTime = dateFormat.parse(sessionStart);
                long diff = currentDate.getTime() - loginTime.getTime();
                long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(diff);
                if (diffInMinutes > 45) {
                    Toast.makeText(this, getResources().getString(R.string.error_msg_session_expired), Toast.LENGTH_SHORT).show();
                    mSessionManager.logoutUser();
                    finish();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (NullPointerException n) {
                mSessionManager.logoutUser();
            }
            userType = userInfo.get("UserType");
            token = userInfo.get("Token");
        }

        // Get claims
        try {
            JWT jwt = new JWT(token);
            Map<String, Claim> allClaims = jwt.getClaims();
            if (userType.equals("farmer"))
                currentUserName = allClaims.get("name").asString();
            else if (userType.equals("officer")) {
                currentUserName = allClaims.get("name").asString();
                intUserType = allClaims.get("int_user_type").asInt();
                if(intUserType==1)
                    admin = allClaims.get("admin").asBoolean();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        switch (userType) {
            case "farmer":
                HomeFarmerFragment homeFarmerFragment = new HomeFarmerFragment();     // Setting the mode in Home Fragment

                FragmentTransaction transactionF = getSupportFragmentManager().beginTransaction();
                transactionF.replace(R.id.fragment_container, homeFarmerFragment, "HomeFarmerFragment");
                transactionF.commit();
                break;
            case "officer":
                HomeFragment homeFragment = new HomeFragment();     // Setting the mode in Home Fragment

                FragmentTransaction transactionO = getSupportFragmentManager().beginTransaction();
                transactionO.replace(R.id.fragment_container, homeFragment, "HomeFragment");
                transactionO.commit();
                break;
        }

        //Listen for changes in the back stack
        getSupportFragmentManager().addOnBackStackChangedListener(this);

        try {
            if (!currentUserName.isEmpty())
                Toast.makeText(MainActivity.this, "Welcome " + currentUserName, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.d("fiUser", e.toString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_home_toolbar_action_setting:
                View menuItemView = findViewById(R.id.menu_home_toolbar_action_setting);
                PopupMenu popupMenu = new PopupMenu(this, menuItemView);
                popupMenu.inflate(R.menu.settings_popup_menu);
                if (userType.equals("farmer")) {
                    popupMenu.getMenu().findItem(R.id.main_menu_popup_menu_action_reset_password).setVisible(false);
                    popupMenu.getMenu().findItem(R.id.main_menu_popup_menu_action_settings).setVisible(true);
                }
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem items) {
                        switch (items.getItemId()) {
                            case R.id.main_menu_popup_menu_action_reset_password:
                                if (userType.equals("officer")) {
                                    final AlertDialog.Builder customAlertDialog = new AlertDialog.Builder(MainActivity.this);
                                    customAlertDialog.setCancelable(true);
                                    View customView = LayoutInflater.from(MainActivity.this).inflate(R.layout.custom_alert_dialog_reset_password, null, false);

                                    final TextInputLayout tilOldPassword = (TextInputLayout) customView.findViewById(R.id.til_old_password_custom_alert_dialog_reset_password);
                                    final TextInputLayout tilNewPassword = (TextInputLayout) customView.findViewById(R.id.til_new_password_custom_alert_dialog_reset_password);
                                    final TextInputLayout tilConfirmNewPassword = (TextInputLayout) customView.findViewById(R.id.til_confirm_new_password_custom_alert_dialog_reset_password);

                                    final TextInputEditText tietOldPassword = (TextInputEditText) customView.findViewById(R.id.tiet_old_password_custom_alert_dialog_reset_password);
                                    final TextInputEditText tietNewPassword = (TextInputEditText) customView.findViewById(R.id.tiet_new_password_custom_alert_dialog_rest_password);
                                    final TextInputEditText tietConfirmNewPassword = (TextInputEditText) customView.findViewById(R.id.tiet_confirm_new_password_custom_alert_dialog_reset_password);

                                    CardView cvOk = (CardView) customView.findViewById(R.id.cv_btn_ok_custom_alert_dialog_reset_password);

                                    customAlertDialog.setView(customView);
                                    final AlertDialog alertDialog = customAlertDialog.create();
                                    alertDialog.show();

                                    cvOk.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (!mInputValidation.isInputEditTextFilled(tietOldPassword, tilOldPassword, getString(R.string.error_message_blank_field))) {
                                                return;
                                            }
                                            if (!mInputValidation.isInputEditTextFilled(tietNewPassword, tilNewPassword, getString(R.string.error_message_blank_field))) {
                                                return;
                                            }
                                            if (!mInputValidation.isInputEditTextFilled(tietConfirmNewPassword, tilConfirmNewPassword, getString(R.string.error_message_blank_field))) {
                                                return;
                                            }
                                            if (!(tietNewPassword.getText().toString().trim().length() > 7 && tietNewPassword.getText().toString().trim().length() < 17)) {
                                                mAlert.showAlertDialog(MainActivity.this, "Validation error", getResources().getString(R.string.error_msg_pswd_len), false);
                                                return;
                                            }
                                            if (!tietNewPassword.getText().toString().trim().equals(tietConfirmNewPassword.getText().toString().trim())) {
                                                mAlert.showAlertDialog(MainActivity.this, "Validation error", getResources().getString(R.string.error_msg_pswd_match), false);
                                                return;
                                            }
                                            if (mSessionManager.isLoggedIn()) {
                                                String url = "";
                                                if (userType.equals("officer")) {
                                                    switch (intUserType) {
                                                        case 1:// FIE
                                                            url = EndPoints.URL_EMP_CHANGE_PASSWORD;
                                                            break;
                                                        case 2:
                                                            url = EndPoints.URL_AGENT_CHANGE_PASSWORD;
                                                            break;
                                                        case 3:
                                                            url = EndPoints.URL_FPO_CHANGE_PASSWORD;
                                                            break;
                                                        case 4:
                                                            url = EndPoints.URL_BANK_REP_CHANGE_PASSWORD;
                                                            break;
                                                        default:
                                                            Toast.makeText(MainActivity.this, "Invalid userid!", Toast.LENGTH_SHORT).show();
                                                            break;
                                                    }
                                                } else {
                                                    return;
                                                }
                                                if (url.isEmpty()) {
                                                    return;
                                                }

                                                final String oldPassword = tietOldPassword.getText().toString().trim();
                                                final String newPassword = tietNewPassword.getText().toString().trim();

                                                // Tag used to cancel the request
                                                String tag_json_obj = "json_obj_req";

                                                JSONObject jsonBody = new JSONObject();
                                                try {
                                                    jsonBody.put("old_password", oldPassword);
                                                    jsonBody.put("new_password", newPassword);
                                                    jsonBody.put("changed_password", true);//Check for future
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }

                                                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT, url, jsonBody,
                                                        new Response.Listener<JSONObject>() {
                                                            @Override
                                                            public void onResponse(JSONObject response) {
                                                                try {
                                                                    // Parsing json object response
                                                                    boolean isSuccess = response.getBoolean("success");
                                                                    String message = response.getString("msg");
                                                                    if (isSuccess) {
                                                                        alertDialog.dismiss();
                                                                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                                                                        removeAllFragments(getSupportFragmentManager());
                                                                        //Redirect to login page
                                                                        mSessionManager.logoutUser();
                                                                        finish();
                                                                    }
                                                                } catch (JSONException e) {
                                                                    alertDialog.dismiss();
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                        }, new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
                                                        alertDialog.dismiss();

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
                                                                    errorMessage = message + ".";
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
                                                        Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
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

                                            } else {
                                                alertDialog.dismiss();
                                                Toast.makeText(MainActivity.this, getResources().getString(R.string.error_msg_logged_out), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(MainActivity.this, getResources().getString(R.string.error_msg_feature_not_available), Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case R.id.main_menu_popup_menu_action_settings:
                                // Open settings activity
                                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                                startActivity(intent);
                                break;
                            case R.id.main_menu_popup_menu_action_logout:
                                mSessionManager.logoutUser();
                                finish();
                                break;
                            case R.id.main_menu_popup_menu_action_user_profile:
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.getMenu().getItem(0).setTitle("Hi " + currentUserName);
                popupMenu.show();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackStackChanged() {
        shouldDisplayHomeUp();
    }

    public void shouldDisplayHomeUp() {
        //Enable Up button only  if there are entries in the back stack
        boolean canGoBack = getSupportFragmentManager().getBackStackEntryCount() > 0;
        getSupportActionBar().setDisplayHomeAsUpEnabled(canGoBack);
        if (canGoBack)
            getSupportActionBar().setTitle(null);
        else
            getSupportActionBar().setTitle("Home");
    }
    @Override
    public boolean onSupportNavigateUp() {
        //This method is called when the up button is pressed. Just the pop back stack.
        getSupportFragmentManager().popBackStack();
        return true;
    }


    private void checkAppPermissions() {
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CALL_PHONE
                , Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
                , Manifest.permission.ACCESS_COARSE_LOCATION};
        if (!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(this, getResources().getString(R.string.error_msg_app_update),
                    CHECK_PERMISSION_CODE, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        // Do nothing
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        // Do nothing
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkNewAppVersionState();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case REQ_CODE_VERSION_UPDATE:
                if (resultCode != RESULT_OK) { //RESULT_OK / RESULT_CANCELED / RESULT_IN_APP_UPDATE_FAILED
                    //Log.d("Update flow failed! Result code: " + resultCode);
                    // If the update is cancelled or fails,
                    // you can request to start the update again.
                    checkNewAppVersionState();
                }

                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void checkForAppUpdate() {
        // Creates instance of the manager.
        appUpdateManager = AppUpdateManagerFactory.create(MainActivity.this);

        // Returns an intent object that you use to check for an update.
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        // Create a listener to track request state updates.
        installStateUpdatedListener = new InstallStateUpdatedListener() {
            @Override
            public void onStateUpdate(InstallState installState) {
                // Show module progress, log state, or install the update.
                if (installState.installStatus() == InstallStatus.DOWNLOADED)
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.error_msg_app_restart), Toast.LENGTH_SHORT).show();
                // After the update is downloaded, show a notification
                // and request user confirmation to restart the app.
            }
        };

        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo result) {
                if (result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                    // Request the update.
                    if (result.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                        // Start an update.
                        startAppUpdateImmediate(result);
                    }
                }
            }
        });
    }

    private void startAppUpdateImmediate(AppUpdateInfo appUpdateInfo) {
        try {
            appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.IMMEDIATE,
                    // The current activity making the update request.
                    this,
                    // Include a request code to later monitor this update request.
                    MainActivity.REQ_CODE_VERSION_UPDATE);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks that the update is not stalled during 'onResume()'.
     * However, you should execute this check at all app entry points.
     */
    private void checkNewAppVersionState() {
        appUpdateManager
                .getAppUpdateInfo()
                .addOnSuccessListener(
                        new OnSuccessListener<AppUpdateInfo>() {
                            @Override
                            public void onSuccess(AppUpdateInfo result) {
                                //IMMEDIATE:
                                if (result.updateAvailability()
                                        == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                                    // If an in-app update is already running, resume the update.
                                    startAppUpdateImmediate(result);
                                }
                            }
                        });
    }

    private void removeAllFragments(FragmentManager fragmentManager) {
        while (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    @Override
    public void onBackPressed() {
        tellFragments();
        super.onBackPressed();
    }

    private void tellFragments() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for (Fragment f : fragments) {
            if (f != null && f instanceof BaseFragment)
                ((BaseFragment) f).onBackPressed();
        }
    }
}
