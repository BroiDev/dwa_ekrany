package com.broidev.dwa_ekrany;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SummaryActivity extends AppCompatActivity {

    public static final String EXTRA_NAME = "com.broidev.dwa_ekrany.NAME";
    public static final String EXTRA_EMAIL = "com.broidev.dwa_ekrany.EMAIL";
    public static final String EXTRA_COURSE = "com.broidev.dwa_ekrany.COURSE";
    public static final String EXTRA_LEVEL = "com.broidev.dwa_ekrany.LEVEL";
    public static final String EXTRA_NEWSLETTER = "com.broidev.dwa_ekrany.NEWSLETTER";

    private TextView tvSummary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        tvSummary = findViewById(R.id.tvSummary);
        tvSummary.setText(buildSummary());
    }

    private String buildSummary() {
        Intent intent = getIntent();

        String name = intent.getStringExtra(EXTRA_NAME);
        String email = intent.getStringExtra(EXTRA_EMAIL);
        String course = intent.getStringExtra(EXTRA_COURSE);
        int level = intent.getIntExtra(EXTRA_LEVEL, 0);
        boolean newsletter = intent.getBooleanExtra(EXTRA_NEWSLETTER, false);

        String newsletterText = newsletter
                ? getString(R.string.yes_short)
                : getString(R.string.no_short);

        return getString(R.string.summary_format, name, email, course, level, newsletterText);
    }
}
