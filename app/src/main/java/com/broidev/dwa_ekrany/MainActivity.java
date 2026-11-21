package com.broidev.dwa_ekrany;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_share) {
            share();
            return true;
        } else if (id == R.id.action_clear) {
            clearForm();
            return true;
        } else if (id == R.id.action_about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void clearForm() {
        items.clear();
        updateList();
        Toast.makeText(this, R.string.cleared, Toast.LENGTH_SHORT).show();
    }

    private void share() {
        if (items.isEmpty()) {
            Toast.makeText(this, R.string.nothing_to_share, Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder sb = new StringBuilder("Moja lista elementów:\n");
        for (String item : items) {
            sb.append("- ").append(item).append("\n");
        }

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, sb.toString());

        try {
            startActivity(Intent.createChooser(intent, getString(R.string.share_via)));
        } catch (android.content.ActivityNotFoundException e) {
            Toast.makeText(this, R.string.error_no_app, Toast.LENGTH_SHORT).show();
        }
    }
}
