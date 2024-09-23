package com.farminfinity.farminfinity.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.farminfinity.farminfinity.Helper.SessionManager;
import com.farminfinity.farminfinity.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ChooseLanguageActivity extends AppCompatActivity {
    private Spinner spinnerLanguage;
    private CardView cvBtnSave;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_language);
        sessionManager = new SessionManager(ChooseLanguageActivity.this);

        isPreferredLangSelected();

        spinnerLanguage = (Spinner) findViewById(R.id.spinner_language_activity_choose_language);
        cvBtnSave = (CardView) findViewById(R.id.cv_btn_save_activity_choose_language);

        // Language Spinner Drop down elements
        List<String> lstLanguage = new ArrayList<>();
        lstLanguage.add(getResources().getString(R.string.ddl_english));
        lstLanguage.add(getResources().getString(R.string.ddl_hindi));
        lstLanguage.add(getResources().getString(R.string.ddl_assamese));

        ArrayAdapter<String> dataAdapterLanguage = new ArrayAdapter<String>(ChooseLanguageActivity.this, android.R.layout.simple_spinner_item, lstLanguage);
        dataAdapterLanguage.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLanguage.setAdapter(dataAdapterLanguage);

        cvBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = "";
                // Set App locale
                //Toast.makeText(ChooseLanguageActivity.this, "Outside if cond.", Toast.LENGTH_SHORT).show();
                //Toast.makeText(ChooseLanguageActivity.this, sessionManager.getPreferredLanguage()+"hk", Toast.LENGTH_SHORT).show();
                if (sessionManager.getPreferredLanguage() == null || sessionManager.getPreferredLanguage().isEmpty()){
                    //Toast.makeText(ChooseLanguageActivity.this, "inside if condition", Toast.LENGTH_SHORT).show();
                    switch (spinnerLanguage.getSelectedItem().toString()){
                        case "English":
                            setAppLocale("en");
                            code = "en";
                            //Toast.makeText(ChooseLanguageActivity.this, "Inside case english", Toast.LENGTH_SHORT).show();
                            break;

                        case "Hindi-हिंदी":
                            setAppLocale("hi");
                            code = "hi";
                            //Toast.makeText(ChooseLanguageActivity.this, "Inside case hindi", Toast.LENGTH_SHORT).show();
                            break;

                        case "Assamese-অসমীয়া":
                            setAppLocale("as");
                            code = "as";
                            //Toast.makeText(ChooseLanguageActivity.this, "Inside case hindi", Toast.LENGTH_SHORT).show();
                            break;

                        default:
                            setAppLocale("en");
                            code = "en";
                            //Toast.makeText(ChooseLanguageActivity.this, "Inside case default", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    sessionManager.createPreferredLanguage(code);
                    Intent intent = new  Intent(ChooseLanguageActivity.this,LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void isPreferredLangSelected() {
        if (sessionManager.getPreferredLanguage()!=null){
            setAppLocale(sessionManager.getPreferredLanguage());
            Intent intent = new  Intent(ChooseLanguageActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();
        }
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