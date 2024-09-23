package com.farminfinity.farminfinity;

import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.farminfinity.farminfinity.Activities.ChooseLanguageActivity;
import com.farminfinity.farminfinity.Activities.GetStartedActivity;
import com.farminfinity.farminfinity.Activities.LoginActivity;
import com.google.android.material.snackbar.Snackbar;
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

import gr.net.maroulis.library.EasySplashScreen;

import static com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE;

public class SplashScreen extends AppCompatActivity {
    private static final int REQ_CODE_VERSION_UPDATE = 330;
    private AppUpdateManager appUpdateManager;
    private InstallStateUpdatedListener installStateUpdatedListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EasySplashScreen config = new EasySplashScreen(SplashScreen.this)
                .withFullScreen()
                .withTargetActivity(GetStartedActivity.class)
                .withSplashTimeOut(2000)
                .withLogo(R.mipmap.ic_launcher_foreground)
                .withBackgroundColor(Color.parseColor("#FFFFFF"))
                .withFooterText(getString(R.string.splash_footer));

        View view = config.create();
        setContentView(view);

        checkForAppUpdate();
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
                    Log.d("fail","Update flow failed! Result code: " + resultCode);
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
        appUpdateManager = AppUpdateManagerFactory.create(getBaseContext());

        // Returns an intent object that you use to check for an update.
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        // Create a listener to track request state updates.
        installStateUpdatedListener = new InstallStateUpdatedListener() {
            @Override
            public void onStateUpdate(InstallState installState) {
                // Show module progress, log state, or install the update.
                if (installState.installStatus() == InstallStatus.DOWNLOADED)
                    Toast.makeText(SplashScreen.this, "Please restart the application", Toast.LENGTH_SHORT).show();
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
                    if (result.isUpdateTypeAllowed(IMMEDIATE)) {
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
                    IMMEDIATE,
                    // The current activity making the update request.
                    SplashScreen.this,
                    // Include a request code to later monitor this update request.
                    SplashScreen.REQ_CODE_VERSION_UPDATE);
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
                                else if (result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                                        && result.isUpdateTypeAllowed(IMMEDIATE)) {
                                    startAppUpdateImmediate(result);
                                }
                            }
                        });
    }
}
