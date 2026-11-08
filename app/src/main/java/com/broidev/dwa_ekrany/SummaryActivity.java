package com.broidev.dwa_ekrany;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class SummaryActivity extends AppCompatActivity {

    public static final String EXTRA_AMOUNT = "com.broidev.dwa_ekrany.AMOUNT";
    public static final String EXTRA_CURRENCY = "com.broidev.dwa_ekrany.CURRENCY";
    public static final String EXTRA_MARGIN = "com.broidev.dwa_ekrany.MARGIN";
    public static final String EXTRA_VIP = "com.broidev.dwa_ekrany.VIP";

    public static final String EXTRA_TRANSACTION_ID = "com.broidev.dwa_ekrany.TRANSACTION_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        TextView tvSummary = findViewById(R.id.tvSummary);
        tvSummary.setText(buildSummary());

        findViewById(R.id.btnConfirm).setOnClickListener(v -> confirm());
        findViewById(R.id.btnEdit).setOnClickListener(v -> finish());
        findViewById(R.id.btnShare).setOnClickListener(v -> share(tvSummary.getText().toString()));
    }

    private String buildSummary() {
        Intent intent = getIntent();

        double amountPln = intent.getDoubleExtra(EXTRA_AMOUNT, 0.0);
        String currency = intent.getStringExtra(EXTRA_CURRENCY);
        int margin = intent.getIntExtra(EXTRA_MARGIN, 0);
        boolean isVip = intent.getBooleanExtra(EXTRA_VIP, false);

        if (currency == null) {
            currency = "EUR";
        }

        // Proste przeliczniki (sztywny kurs)
        double rate = 1.0;
        switch (currency) {
            case "EUR": rate = 4.30; break;
            case "USD": rate = 4.00; break;
            case "GBP": rate = 5.10; break;
            case "CHF": rate = 4.50; break;
        }

        // Vip nie płaci marży
        int effectiveMargin = isVip ? 0 : margin;

        // Kwota docelowa: (PLN / rate) potrącone o marżę
        double resultAmount = (amountPln / rate) * (1.0 - (effectiveMargin / 100.0));

        String vipText = isVip ? getString(R.string.yes_short) : getString(R.string.no_short);

        return getString(R.string.summary_format, amountPln, currency, margin, vipText, resultAmount, currency);
    }

    private void confirm() {
        String txId = "TX-" + (10000 + new Random().nextInt(90000));

        Intent result = new Intent();
        result.putExtra(EXTRA_TRANSACTION_ID, txId);
        setResult(RESULT_OK, result);
        finish();
    }

    private void share(String textToShare) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, textToShare);

        try {
            startActivity(Intent.createChooser(intent, getString(R.string.share_via)));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.error_no_app, Toast.LENGTH_SHORT).show();
        }
    }
}
