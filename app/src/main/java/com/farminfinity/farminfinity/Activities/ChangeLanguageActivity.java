package com.farminfinity.farminfinity.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.farminfinity.farminfinity.Helper.SessionManager;
import com.farminfinity.farminfinity.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ChangeLanguageActivity extends AppCompatActivity {
    private Spinner spinnerLanguage;
    private CardView cvBtnSave;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_language);
        setupToolbar();
        sessionManager = new SessionManager(ChangeLanguageActivity.this);
        spinnerLanguage = (Spinner) findViewById(R.id.spinner_language_activity_change_language);
        cvBtnSave = (CardView) findViewById(R.id.cv_btn_save_activity_change_language);

        // Language Spinner Drop down elements
        List<String> lstLanguage = new ArrayList<>();
        lstLanguage.add(getResources().getString(R.string.ddl_english));
        lstLanguage.add(getResources().getString(R.string.ddl_hindi));
        lstLanguage.add(getResources().getString(R.string.ddl_assamese));

        ArrayAdapter<String> dataAdapterLanguage = new ArrayAdapter<String>(ChangeLanguageActivity.this, android.R.layout.simple_spinner_item, lstLanguage);
        dataAdapterLanguage.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLanguage.setAdapter(dataAdapterLanguage);

        cvBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = "";
                // Set App locale
                if (sessionManager.getPreferredLanguage() != null){
                    switch (spinnerLanguage.getSelectedItem().toString()){
                        case "English":
                            setAppLocale("en");
                            code = "en";
                            break;

                        case "Hindi-हिंदी":
                            setAppLocale("hi");
                            code = "hi";
                            break;

                        case "Assamese-অসমীয়া":
                            setAppLocale("as");
                            code = "as";
                            //Toast.makeText(ChooseLanguageActivity.this, "Inside case hindi", Toast.LENGTH_SHORT).show();
                            break;

                        default:
                            setAppLocale("en");
                            code = "en";
                            break;
                    }
                    sessionManager.createPreferredLanguage(code);
                    // Go to home
                    Intent mStartActivity = new Intent(ChangeLanguageActivity.this, MainActivity.class);
                    //mStartActivity.putExtra("USER_TYPE", "officer");
                    mStartActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mStartActivity);
                    finish();
                }
            }
        });

    }

    private void setupToolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_activity_change_language);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Preferences");
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void setAppLocale(String localeCode){
        Resources resources = getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(new Locale(localeCode.toLowerCase()));
        resources.updateConfiguration(configuration, displayMetrics);
        configuration.locale = new Locale(localeCode.toLowerCase());
        resources.updateConfiguration(configuration, displayMetrics);
    }
}