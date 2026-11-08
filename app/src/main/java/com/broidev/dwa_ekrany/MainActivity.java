package com.broidev.dwa_ekrany;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    private final ActivityResultLauncher<Intent> summaryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String txId = result.getData().getStringExtra(SummaryActivity.EXTRA_TRANSACTION_ID);
                    showRegistered(txId);
                }
            });

    private EditText etAmount;
    private Spinner spCurrency;
    private SeekBar sbMargin;
    private TextView tvMarginValue;
    private CheckBox cbVip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etAmount = findViewById(R.id.etAmount);
        spCurrency = findViewById(R.id.spCurrency);
        sbMargin = findViewById(R.id.sbMargin);
        tvMarginValue = findViewById(R.id.tvMarginValue);
        cbVip = findViewById(R.id.cbVip);

        setUpCurrencySpinner();
        setUpMarginSeekBar();

        findViewById(R.id.btnNext).setOnClickListener(v -> openSummary());
    }

    private void setUpCurrencySpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.currencies, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCurrency.setAdapter(adapter);
    }

    private void setUpMarginSeekBar() {
        tvMarginValue.setText(String.valueOf(sbMargin.getProgress()));
        sbMargin.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvMarginValue.setText(String.valueOf(progress));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
    }

    private void openSummary() {
        String amountText = etAmount.getText().toString().trim();
        if (amountText.isEmpty()) {
            etAmount.setError(getString(R.string.error_empty_amount));
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            etAmount.setError(getString(R.string.error_empty_amount));
            return;
        }

        if (amount <= 0) {
            etAmount.setError(getString(R.string.error_empty_amount));
            return;
        }

        Intent intent = new Intent(this, SummaryActivity.class);
        intent.putExtra(SummaryActivity.EXTRA_AMOUNT, amount);
        intent.putExtra(SummaryActivity.EXTRA_CURRENCY, spCurrency.getSelectedItem().toString());
        intent.putExtra(SummaryActivity.EXTRA_MARGIN, sbMargin.getProgress());
        intent.putExtra(SummaryActivity.EXTRA_VIP, cbVip.isChecked());

        summaryLauncher.launch(intent);
    }

    private void showRegistered(String txId) {
        Snackbar.make(findViewById(R.id.main),
                getString(R.string.registered, txId),
                Snackbar.LENGTH_LONG).show();
    }
}
