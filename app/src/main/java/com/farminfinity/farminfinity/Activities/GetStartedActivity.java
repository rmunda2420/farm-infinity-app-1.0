package com.farminfinity.farminfinity.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.farminfinity.farminfinity.R;

public class GetStartedActivity extends AppCompatActivity {
    private TextView tvPp;
    private CardView btnGetStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final String textPp = getResources().getString(R.string.lbl_pp_link);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);
        btnGetStarted = findViewById(R.id.btn_activity_get_started);
        tvPp = findViewById(R.id.tv_pp_activity_get_started);

        ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent browserIntent3 = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.privacy_policy_url)));
                startActivity(browserIntent3);
            }
        };

        SpannableString spannableStringTermsPolicy = new SpannableString(textPp);
        spannableStringTermsPolicy.setSpan(clickableSpan1, 0, textPp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvPp.setText(spannableStringTermsPolicy);
        tvPp.setMovementMethod(LinkMovementMethod.getInstance());

        btnGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mStartActivity = new Intent(GetStartedActivity.this, ChooseLanguageActivity.class);
                mStartActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mStartActivity);
                finish();
            }
        });
    }
}