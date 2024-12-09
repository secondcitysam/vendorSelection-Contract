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

public class Login extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private AppCompatButton loginButton;
    private TextView signUpTextView;

    private AppDao appDao;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        AppDatabase database = Room.databaseBuilder(
                getApplicationContext(),
                AppDatabase.class,
                "contract_vendor_database"
        ).addCallback(new RoomDatabase.Callback() {
            @Override
            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                super.onCreate(db);
                db.execSQL("PRAGMA foreign_keys=ON;");
            }
        }).build();

        appDao = database.appDao();
        executorService = Executors.newSingleThreadExecutor();

        initializeViews();

        loginButton.setOnClickListener(v -> login());

        signUpTextView.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), SignUp.class))
        );
    }

    private void initializeViews() {
        usernameEditText = findViewById(R.id.u1);
        passwordEditText = findViewById(R.id.p1);
        loginButton = findViewById(R.id.b1);
        signUpTextView = findViewById(R.id.t1);
    }

    private void login() {
        if (validateInput(usernameEditText, passwordEditText)) {
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            executorService.execute(() -> {
                User user = appDao.loginUser(username, password);

                runOnUiThread(() -> {
                    if (user != null) {
                        showDialog("Login Successful", "Welcome, " + user.getUsername());
                        Intent intent;
                        if (user.getRole().equalsIgnoreCase("user")) {
                            intent = new Intent(getApplicationContext(), Home.class);
                        } else {
                            intent = new Intent(getApplicationContext(), com.example.vendorselection.Activities.View.class);
                        }
                        intent.putExtra("name", user.getUsername());
                        intent.putExtra("id", user.getUserId());
                        intent.putExtra("role", user.getRole());
                        startActivity(intent);
                    } else {
                        showDialog("Login Failed", "Invalid username or password");
                    }
                });
            });
        }
    }

    private boolean validateInput(EditText usernameEditText, EditText passwordEditText) {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (username.isEmpty()) {
            usernameEditText.setError("Required");
            return false;
        }
        if (password.isEmpty()) {
            passwordEditText.setError("Required");
            return false;
        }
        if (password.length() < 6) {
            passwordEditText.setError("Minimum 6 characters required");
            return false;
        }
        return true;
    }

    private void showDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog_background);
        dialog.show();
    }
}
