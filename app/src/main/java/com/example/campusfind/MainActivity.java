package com.example.campusfind;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campusfind.adapter.ItemAdapter;
import com.example.campusfind.database.DatabaseHelper;
import com.example.campusfind.models.Item;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private ItemAdapter adapter;
    private RecyclerView rvItems;
    private TextView tvEmpty;
    private EditText etSearch;
    private Button btnLost, btnFound, btnReportItem;

    // Track kung anong tab yung active: "Lost" o "Found"
    private String currentFilter = "Lost";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        // Kunin lahat ng views mula sa layout
        rvItems = findViewById(R.id.rvItems);
        tvEmpty = findViewById(R.id.tvEmpty);
        etSearch = findViewById(R.id.etSearch);
        btnLost = findViewById(R.id.btnLost);
        btnFound = findViewById(R.id.btnFound);
        btnReportItem = findViewById(R.id.btnReportItem);

        // I-set up ang RecyclerView: kailangan niya ng LayoutManager
        // (nagsasabi kung paano i-aayos ang mga items - dito, straight down)
        rvItems.setLayoutManager(new LinearLayoutManager(this));

        // Simulan sa blangkong list muna; papalitan sa loadItems()
        adapter = new ItemAdapter(new java.util.ArrayList<>(), item -> {
            // Tinatawag ito kapag tinap ang isang item sa listahan - buksan
            // ang AddEditItemActivity sa EDIT MODE (ipasa ang item ID)
            Intent intent = new Intent(MainActivity.this, AddEditItemActivity.class);
            intent.putExtra(AddEditItemActivity.EXTRA_ITEM_ID, item.getItemId());
            startActivity(intent);
        });
        rvItems.setAdapter(adapter);

        // Pag-tap ng "Lost" tab
        btnLost.setOnClickListener(v -> {
            currentFilter = "Lost";
            loadItems();
        });

        // Pag-tap ng "Found" tab
        btnFound.setOnClickListener(v -> {
            currentFilter = "Found";
            loadItems();
        });

        Button btnUsers = findViewById(R.id.btnUsers);
        btnUsers.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, UserListActivity.class)));

        Button btnClaims = findViewById(R.id.btnClaims);
        btnClaims.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, ClaimListActivity.class)));

        // Pag-tap ng "+ REPORT ITEM" - buksan ang AddEditItemActivity sa
        // CREATE MODE (walang ipapasang item ID)
        btnReportItem.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditItemActivity.class);
            startActivity(intent);
        });


        // Tuwing may tinatype sa search bar, i-refresh agad ang listahan
        etSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadItems();
            }

            @Override
            public void afterTextChanged(android.text.Editable s) { }
        });



        loadItems();
    }




    // Kinukuha ang items mula sa database base sa currentFilter (Lost/Found),
    // tapos ino-update ang adapter at RecyclerView
    private void loadItems() {
        // Kunin ang keyword sa search bar (kung wala, blangko lang)
        String keyword = etSearch.getText().toString().trim();

        List<Item> items;
        if (keyword.isEmpty()) {
            // Walang tinype: ipakita lahat ng items sa active tab (Lost/Found)
            items = dbHelper.getItemsByStatus(currentFilter);
        } else {
            // May tinype: hanapin lang yung may match sa active tab
            items = dbHelper.searchItems(currentFilter, keyword);
        }
        adapter.updateList(items);

        // Ipakita yung "empty state" text kung walang laman ang listahan
        if (items.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvItems.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvItems.setVisibility(View.VISIBLE);
        }
    }

    // Kapag bumalik dito galing sa ibang screen (hal. pagkatapos mag-add ng
    // bagong item), i-refresh ulit ang listahan para makita agad ang bago
    @Override
    protected void onResume() {
        super.onResume();
        loadItems();
    }
}