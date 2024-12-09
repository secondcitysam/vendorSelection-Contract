package com.example.vendorselection.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.vendorselection.R;
import com.example.vendorselection.Room.DAO.AppDao;
import com.example.vendorselection.Room.Database.AppDatabase;
import com.example.vendorselection.Room.Entity.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SignUp extends AppCompatActivity {
    private EditText usernameField, passwordField, roleField;
    private AppCompatButton signUpButton;
    private TextView loginLink;

    private AppDao appDao;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize database and DAO
        AppDatabase database = Room.databaseBuilder(
                        getApplicationContext(),
                        AppDatabase.class,
                        "contract_vendor_database"
                )
                .addCallback(new RoomDatabase.Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        db.execSQL("PRAGMA foreign_keys=ON;");
                    }
                })
                .build();

        appDao = database.appDao();
        executorService = Executors.newSingleThreadExecutor();

        // Initialize views
        initializeViews();

        // Set button click listeners
        signUpButton.setOnClickListener(v -> signUp());
        loginLink.setOnClickListener(v -> startActivity(new Intent(SignUp.this, Login.class)));
    }

    private void signUp() {
        if (validateInput()) {
            String username = usernameField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();
            String role = roleField.getText().toString().trim();

            executorService.execute(() -> {
                // Check if the user already exists
                User existingUser = appDao.getUserByUsername(username);

                if (existingUser == null) {
                    // Create a new user
                    User newUser = new User();
                    newUser.username = username;
                    newUser.password = password;
                    newUser.email = username + "@gmail.com";
                    newUser.role = role;

                    // Insert user into the database
                    appDao.insertUser(newUser);

                    runOnUiThread(() -> {
                        showDialog("Sign Up Successful", "Welcome, " + newUser.username + "!");

                        // Redirect based on role
                        Intent intent;
                        if ("user".equalsIgnoreCase(newUser.role)) {
                            intent = new Intent(SignUp.this, Home.class);
                        } else if ("admin".equalsIgnoreCase(newUser.role)) {
                            intent = new Intent(SignUp.this, com.example.vendorselection.Activities.View.class);
                        } else {
                            showDialog("Error", "Invalid role. Please try again.");
                            return;
                        }

                        intent.putExtra("name", newUser.username);
                        intent.putExtra("id", newUser.getUserId());
                        intent.putExtra("role", newUser.role);
                        startActivity(intent);
                    });

                } else {
                    runOnUiThread(() -> showDialog("Sign Up Failed", "Username is already taken."));
                }
            });
        }
    }

    private void initializeViews() {
        usernameField = findViewById(R.id.u2);
        passwordField = findViewById(R.id.p2);
        roleField = findViewById(R.id.r2);
        signUpButton = findViewById(R.id.b2);
        loginLink = findViewById(R.id.t2);
    }

    private boolean validateInput() {
        String username = usernameField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();
        String role = roleField.getText().toString().trim();

        if (username.isEmpty()) {
            usernameField.setError("Username is required");
            return false;
        }
        if (password.isEmpty()) {
            passwordField.setError("Password is required");
            return false;
        }
        if (password.length() < 6) {
            passwordField.setError("Password must be at least 6 characters long");
            return false;
        }
        if (role.isEmpty()) {
            roleField.setError("Role is required");
            return false;
        }
        if (!role.equalsIgnoreCase("user") && !role.equalsIgnoreCase("admin")) {
            roleField.setError("Role must be 'user' or 'admin'");
            return false;
        }
        return true;
    }

    private void showDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog_background);
        dialog.show();
    }
}
