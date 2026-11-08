package com.broidev.dwa_ekrany;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class SummaryActivity extends AppCompatActivity {

    public static final String EXTRA_ITEMS_ARRAY = "com.broidev.dwa_ekrany.ITEMS_ARRAY";
    public static final String EXTRA_SELECTED_ITEM = "com.broidev.dwa_ekrany.SELECTED_ITEM";

    private String[] items;
    private String currentSelection;
    private TextView tvResult;
    private final Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        tvResult = findViewById(R.id.tvResult);
        TextView tvCount = findViewById(R.id.tvCount);

        Intent intent = getIntent();
        items = intent.getStringArrayExtra(EXTRA_ITEMS_ARRAY);

        if (items == null || items.length == 0) {
            finish(); // Zabezpieczenie na wypadek niespodziewanego braku danych
            return;
        }

        tvCount.setText(getString(R.string.total_items, items.length));

        drawItem();

        findViewById(R.id.btnRedraw).setOnClickListener(v -> drawItem());
        findViewById(R.id.btnSelectThis).setOnClickListener(v -> confirmSelection());
    }

    private void drawItem() {
        int index = random.nextInt(items.length);
        currentSelection = items[index];
        tvResult.setText(currentSelection);
    }

    private void confirmSelection() {
        Intent result = new Intent();
        result.putExtra(EXTRA_SELECTED_ITEM, currentSelection);
        setResult(RESULT_OK, result);
        finish();
    }
}
