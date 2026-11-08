package com.broidev.dwa_ekrany;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final ActivityResultLauncher<Intent> drawLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String selected = result.getData().getStringExtra(SummaryActivity.EXTRA_SELECTED_ITEM);
                    if (selected != null) {
                        Snackbar.make(findViewById(R.id.main),
                                getString(R.string.selected_item_msg, selected),
                                Snackbar.LENGTH_LONG).show();
                    }
                }
            });

    private EditText etItem;
    private TextView tvList;
    private final ArrayList<String> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etItem = findViewById(R.id.etItem);
        tvList = findViewById(R.id.tvList);

        findViewById(R.id.btnAdd).setOnClickListener(v -> addItem());
        findViewById(R.id.btnDraw).setOnClickListener(v -> openDrawScreen());
    }

    private void addItem() {
        String text = etItem.getText().toString().trim();
        if (!text.isEmpty()) {
            items.add(text);
            updateList();
            etItem.setText("");
        }
    }

    private void updateList() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            sb.append(i + 1).append(". ").append(items.get(i)).append("\n");
        }
        tvList.setText(sb.toString());
    }

    private void openDrawScreen() {
        if (items.isEmpty()) {
            Toast.makeText(this, R.string.empty_list_error, Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, SummaryActivity.class);
        // Przekazanie wpisów jako tablicy (wymaganie z zadania)
        intent.putExtra(SummaryActivity.EXTRA_ITEMS_ARRAY, items.toArray(new String[0]));
        drawLauncher.launch(intent);
    }
}
