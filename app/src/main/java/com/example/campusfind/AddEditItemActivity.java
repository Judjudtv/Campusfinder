package com.example.campusfind;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import com.example.campusfind.models.User;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

import com.example.campusfind.database.DatabaseHelper;
import com.example.campusfind.models.Item;

public class AddEditItemActivity extends AppCompatActivity {

    // Key na gagamitin natin para magpasa ng item ID papunta dito galing
    // MainActivity (kapag nag-e-edit, hindi kapag nagda-Create ng bago)
    public static final String EXTRA_ITEM_ID = "extra_item_id";

    private DatabaseHelper dbHelper;
    private EditText etItemName, etDescription, etLocation, etDate;
    private Button btnMarkLost, btnMarkFound, btnSave, btnDelete;
    private TextView tvFormTitle;

    private Spinner spUser;
    private List<User> userList = new ArrayList<>();

    private String selectedStatus = "Lost"; // default
    private int editingItemId = -1; // -1 ibig sabihin bagong item (Create mode)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_item);

        dbHelper = new DatabaseHelper(this);

        tvFormTitle = findViewById(R.id.tvFormTitle);
        etItemName = findViewById(R.id.etItemName);
        etDescription = findViewById(R.id.etDescription);
        etLocation = findViewById(R.id.etLocation);
        etDate = findViewById(R.id.etDate);
        btnMarkLost = findViewById(R.id.btnMarkLost);
        btnMarkFound = findViewById(R.id.btnMarkFound);
        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);
        spUser = findViewById(R.id.spUser);

        btnMarkLost.setOnClickListener(v -> {
            selectedStatus = "Lost";
            updateStatusButtons();
        });
        btnMarkFound.setOnClickListener(v -> {
            selectedStatus = "Found";
            updateStatusButtons();
        });

        loadUsers();   // dapat BAGO ang check ng editingItemId sa ibaba

        // Kunin yung item ID na ipinasa galing MainActivity (kung meron)
        editingItemId = getIntent().getIntExtra(EXTRA_ITEM_ID, -1);

        if (editingItemId != -1) {
            // EDIT MODE: kunin yung existing data at i-display sa form
            loadItemForEditing();
        }

        btnSave.setOnClickListener(v -> saveItem());
        btnDelete.setOnClickListener(v -> confirmDelete());

        updateStatusButtons();



    }
    // Pinupuno ang dropdown ng lahat ng users mula sa database
    private void loadUsers() {
        userList = dbHelper.getAllUsers();

        List<String> names = new ArrayList<>();
        for (User u : userList) {
            names.add(u.getName() + " (" + u.getStudentId() + ")");
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, names);
        spUser.setAdapter(spinnerAdapter);
    }


    // Kinukuha yung existing item mula database at pinupuno ang form dito
    // (Update operation)
    private void loadItemForEditing() {
        Item item = dbHelper.getItemById(editingItemId);
        if (item == null) return;

        tvFormTitle.setText("Edit Item");
        etItemName.setText(item.getItemName());
        etDescription.setText(item.getDescription());
        etLocation.setText(item.getLocation());
        etDate.setText(item.getDate());
        selectedStatus = item.getStatus();
        updateStatusButtons();

        // Piliin sa dropdown yung user na nag-report ng item na 'to,
        // at i-lock, kasi hindi na dapat magpalit ng nag-report
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getUserId() == item.getUserId()) {
                spUser.setSelection(i);
                break;
            }
        }
        spUser.setEnabled(false);

        btnDelete.setVisibility(android.view.View.VISIBLE);
    }
    // Kinukulayan yung napiling button (pula = Lost, berde = Found),
// at gray naman yung hindi napili
    // Kinukulayan yung napiling button (pula = Lost, berde = Found),
    // at gray naman yung hindi napili
    private void updateStatusButtons() {
        int gray = android.graphics.Color.parseColor("#BDBDBD");
        if (selectedStatus.equals("Lost")) {
            btnMarkLost.setBackgroundColor(android.graphics.Color.parseColor("#E53935"));
            btnMarkFound.setBackgroundColor(gray);
        } else {
            btnMarkFound.setBackgroundColor(android.graphics.Color.parseColor("#43A047"));
            btnMarkLost.setBackgroundColor(gray);
        }
    }


    // Simpleng validation + pag-save (Create kung bago, Update kung existing)
    private void saveItem() {
        String name = etItemName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String date = etDate.getText().toString().trim();

        if (name.isEmpty() || location.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Item name, location, and date are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (editingItemId == -1) {
            // CREATE: kailangan may user na mapipili sa dropdown
            if (userList.isEmpty()) {
                Toast.makeText(this, "Add a user first (Users button on the home screen)",
                        Toast.LENGTH_LONG).show();
                return;
            }
            int userId = userList.get(spUser.getSelectedItemPosition()).getUserId();
            dbHelper.addItem(name, description, location, date, selectedStatus, userId);
            Toast.makeText(this, "Item reported successfully", Toast.LENGTH_SHORT).show();
        } else {
            // UPDATE: may confirmation muna bago i-save
            new AlertDialog.Builder(this)
                    .setTitle("Confirm Update")
                    .setMessage("Save changes to this item?")
                    .setPositiveButton("Yes", (d, w) -> {
                        dbHelper.updateItem(editingItemId, name, description, location, date, selectedStatus);
                        Toast.makeText(this, "Item updated", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .setNegativeButton("No", null)
                    .show();
            return;   // hintayin muna ang sagot sa dialog bago mag-finish()
        }
        finish(); // babalik sa MainActivity, awtomatikong ma-re-refresh dahil sa onResume()
    }

    // May confirmation dialog muna bago talaga mag-delete (requirement ng activity)
    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Item")
                .setMessage("Are you sure you want to delete this item? This cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    dbHelper.deleteItem(editingItemId);
                    Toast.makeText(this, "Item deleted", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}