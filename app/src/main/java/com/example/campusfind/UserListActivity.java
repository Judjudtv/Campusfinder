package com.example.campusfind;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campusfind.adapter.UserAdapter;
import com.example.campusfind.database.DatabaseHelper;
import com.example.campusfind.models.User;

import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private UserAdapter adapter;
    private RecyclerView rvUsers;
    private TextView tvEmptyUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        dbHelper = new DatabaseHelper(this);
        rvUsers = findViewById(R.id.rvUsers);
        tvEmptyUsers = findViewById(R.id.tvEmptyUsers);
        Button btnAddUser = findViewById(R.id.btnAddUser);

        rvUsers.setLayoutManager(new LinearLayoutManager(this));

        // Pag-tap ng user sa listahan: buksan ang popup sa EDIT mode
        adapter = new UserAdapter(new ArrayList<>(), user -> showUserDialog(user));
        rvUsers.setAdapter(adapter);

        // Pag-tap ng "+ ADD USER": buksan ang popup sa CREATE mode (walang user)
        btnAddUser.setOnClickListener(v -> showUserDialog(null));

        loadUsers();
    }

    // READ: kunin lahat ng users mula sa database at ipakita
    private void loadUsers() {
        List<User> users = dbHelper.getAllUsers();
        adapter.updateList(users);

        if (users.isEmpty()) {
            tvEmptyUsers.setVisibility(View.VISIBLE);
            rvUsers.setVisibility(View.GONE);
        } else {
            tvEmptyUsers.setVisibility(View.GONE);
            rvUsers.setVisibility(View.VISIBLE);
        }
    }

    // Popup form para sa Create (existing == null) at Update/Delete (existing != null)
    private void showUserDialog(User existing) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_user, null);
        EditText etName = view.findViewById(R.id.etUserName);
        EditText etStudentId = view.findViewById(R.id.etUserStudentId);
        EditText etContact = view.findViewById(R.id.etUserContact);

        boolean isEdit = existing != null;
        if (isEdit) {
            etName.setText(existing.getName());
            etStudentId.setText(existing.getStudentId());
            etContact.setText(existing.getContact());
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(isEdit ? "Edit User" : "Add User")
                .setView(view)
                .setPositiveButton("Save", null)   // null muna, para hindi auto-close
                .setNegativeButton("Cancel", null);
        if (isEdit) {
            builder.setNeutralButton("Delete", null);
        }

        AlertDialog dialog = builder.create();
        dialog.show();

        // Sarili nating click listener para makontrol kung kailan magsasara
        // ang popup (hal. hindi magsasara kapag may kulang na field)
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String studentId = etStudentId.getText().toString().trim();
            String contact = etContact.getText().toString().trim();

            if (name.isEmpty() || studentId.isEmpty()) {
                Toast.makeText(this, "Name and Student ID are required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isEdit) {
                // CREATE
                dbHelper.addUser(name, studentId, contact);
                Toast.makeText(this, "User added", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                loadUsers();
            } else {
                // UPDATE: may confirmation muna (requirement ng activity)
                new AlertDialog.Builder(this)
                        .setTitle("Confirm Update")
                        .setMessage("Save changes to this user?")
                        .setPositiveButton("Yes", (d, w) -> {
                            dbHelper.updateUser(existing.getUserId(), name, studentId, contact);
                            Toast.makeText(this, "User updated", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            loadUsers();
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

        if (isEdit) {
            // DELETE: may confirmation din
            dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(v -> {
                new AlertDialog.Builder(this)
                        .setTitle("Delete User")
                        .setMessage("Are you sure you want to delete this user?")
                        .setPositiveButton("Delete", (d, w) -> {
                            dbHelper.deleteUser(existing.getUserId());
                            Toast.makeText(this, "User deleted", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            loadUsers();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });
        }
    }
}