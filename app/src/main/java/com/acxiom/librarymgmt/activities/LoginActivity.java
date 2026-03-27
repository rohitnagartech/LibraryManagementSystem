package com.acxiom.librarymgmt.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.acxiom.librarymgmt.R;
import com.acxiom.librarymgmt.models.User;
import com.acxiom.librarymgmt.utils.Constants;
import com.acxiom.librarymgmt.utils.FirebaseHelper;
import com.acxiom.librarymgmt.utils.SessionManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etUserId, etPassword;
    private Button btnLogin;
    private ProgressBar progressBar;
    private SessionManager session;
    private FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        session = new SessionManager(this);
        firebaseHelper = FirebaseHelper.getInstance();

        etUserId = findViewById(R.id.et_user_id);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progress_bar);

        btnLogin.setOnClickListener(v -> loginUser());
    }

    private void loginUser() {
        String userId = etUserId.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (userId.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, getString(R.string.err_empty_fields), Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        firebaseHelper.getDb().collection(Constants.USERS)
                .whereEqualTo("username", userId)
                .whereEqualTo("active", true)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    progressBar.setVisibility(View.GONE);
                    if (queryDocumentSnapshots.isEmpty()) {
                        Toast.makeText(LoginActivity.this, getString(R.string.err_user_not_found), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    boolean found = false;
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        User user = document.toObject(User.class);
                        if (user.getPassword().equals(password)) {
                            found = true;
                            session.createSession(user.getUserId(), user.isAdmin(), user.getName());
                            
                            Intent intent;
                            if (user.isAdmin()) {
                                intent = new Intent(LoginActivity.this, AdminHomeActivity.class);
                            } else {
                                intent = new Intent(LoginActivity.this, UserHomeActivity.class);
                            }
                            startActivity(intent);
                            finish();
                            break;
                        }
                    }

                    if (!found) {
                        Toast.makeText(LoginActivity.this, getString(R.string.err_login_failed), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
