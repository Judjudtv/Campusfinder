package com.example.campusfind;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campusfind.adapter.ClaimAdapter;
import com.example.campusfind.database.DatabaseHelper;
import com.example.campusfind.models.ClaimInfo;
import com.example.campusfind.models.Item;
import com.example.campusfind.models.User;

import java.util.ArrayList;
import java.util.List;

public class ClaimListActivity extends AppCompatActivity {

    private static final String[] STATUSES = {"Pending", "Approved", "Rejected"};

    private DatabaseHelper dbHelper;
    private ClaimAdapter adapter;
    private RecyclerView rvClaims;
    private TextView tvEmptyClaims;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_claim_list);

        dbHelper = new DatabaseHelper(this);
        rvClaims = findViewById(R.id.rvClaims);
        tvEmptyClaims = findViewById(R.id.tvEmptyClaims);
        Button btnAddClaim = findViewById(R.id.btnAddClaim);

        rvClaims.setLayoutManager(new LinearLayoutManager(this));

        // Pag-tap ng claim: buksan ang popup sa EDIT mode
        adapter = new ClaimAdapter(new ArrayList<>(), claim -> showClaimDialog(claim));
        rvClaims.setAdapter(adapter);

        // Pag-tap ng "+ ADD CLAIM": buksan ang popup sa CREATE mode
        btnAddClaim.setOnClickListener(v -> showClaimDialog(null));

        loadClaims();
    }

    // READ: kunin lahat ng claims (kasama ang pangalan ng item at user)
    private void loadClaims() {
        List<ClaimInfo> claims = dbHelper.getAllClaimInfo();
        adapter.updateList(claims);

        if (claims.isEmpty()) {
            tvEmptyClaims.setVisibility(View.VISIBLE);
            rvClaims.setVisibility(View.GONE);
        } else {
            tvEmptyClaims.setVisibility(View.GONE);
            rvClaims.setVisibility(View.VISIBLE);
        }
    }

    // Create (existing == null) o Update/Delete (existing != null)
    private void showClaimDialog(ClaimInfo existing) {
        boolean isEdit = existing != null;

        List<Item> items = dbHelper.getAllItems();
        List<User> users = dbHelper.getAllUsers();

        // Sa Create, kailangan may kahit isang item at isang user na
        if (!isEdit && (items.isEmpty() || users.isEmpty())) {
            Toast.makeText(this, "Add at least one item and one user first",
                    Toast.LENGTH_LONG).show();
            return;
        }

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_claim, null);
        Spinner spItem = view.findViewById(R.id.spClaimItem);
        Spinner spUser = view.findViewById(R.id.spClaimUser);
        Spinner spStatus = view.findViewById(R.id.spClaimStatus);
        LinearLayout layoutStatus = view.findViewById(R.id.layoutClaimStatus);

        // Punan ang dropdown ng items
        List<String> itemNames = new ArrayList<>();
        for (Item i : items) {
            itemNames.add(i.getItemName() + " (" + i.getStatus() + ")");
        }
        spItem.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, itemNames));

        // Punan ang dropdown ng users
        List<String> userNames = new ArrayList<>();
        for (User u : users) {
            userNames.add(u.getName() + " (" + u.getStudentId() + ")");
        }
        spUser.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, userNames));

        // Punan ang dropdown ng status
        spStatus.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, STATUSES));

        if (isEdit) {
            // Sa Edit, status lang ang mababago; naka-lock ang item at user
            layoutStatus.setVisibility(View.VISIBLE);

            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).getItemId() == existing.getItemId()) {
                    spItem.setSelection(i);
                    break;
                }
            }
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).getUserId() == existing.getUserId()) {
                    spUser.setSelection(i);
                    break;
                }
            }
            for (int i = 0; i < STATUSES.length; i++) {
                if (STATUSES[i].equalsIgnoreCase(existing.getClaimStatus())) {
                    spStatus.setSelection(i);
                    break;
                }
            }
            spItem.setEnabled(false);
            spUser.setEnabled(false);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(isEdit ? "Edit Claim" : "Add Claim")
                .setView(view)
                .setPositiveButton("Save", null)
                .setNegativeButton("Cancel", null);
        if (isEdit) {
            builder.setNeutralButton("Delete", null);
        }

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            if (!isEdit) {
                // CREATE: laging "Pending" ang simula ng bagong claim
                int itemId = items.get(spItem.getSelectedItemPosition()).getItemId();
                int userId = users.get(spUser.getSelectedItemPosition()).getUserId();
                dbHelper.addClaim(itemId, userId, "Pending");
                Toast.makeText(this, "Claim submitted", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                loadClaims();
            } else {
                // UPDATE: may confirmation muna
                String newStatus = STATUSES[spStatus.getSelectedItemPosition()];
                new AlertDialog.Builder(this)
                        .setTitle("Confirm Update")
                        .setMessage("Change claim status to " + newStatus + "?")
                        .setPositiveButton("Yes", (d, w) -> {
                            dbHelper.updateClaimStatus(existing.getClaimId(), newStatus);
                            Toast.makeText(this, "Claim updated", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            loadClaims();
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

        if (isEdit) {
            // DELETE: may confirmation din
            dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(v -> {
                new AlertDialog.Builder(this)
                        .setTitle("Delete Claim")
                        .setMessage("Are you sure you want to delete this claim?")
                        .setPositiveButton("Delete", (d, w) -> {
                            dbHelper.deleteClaim(existing.getClaimId());
                            Toast.makeText(this, "Claim deleted", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            loadClaims();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });
        }
    }
}