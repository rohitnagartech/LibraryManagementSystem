package com.acxiom.librarymgmt.maintenance;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.acxiom.librarymgmt.R;
import com.acxiom.librarymgmt.activities.AdminHomeActivity;
import com.acxiom.librarymgmt.activities.ConfirmationActivity;
import com.acxiom.librarymgmt.activities.LoginActivity;
import com.acxiom.librarymgmt.models.User;
import com.acxiom.librarymgmt.utils.Constants;
import com.acxiom.librarymgmt.utils.FirebaseHelper;
import com.acxiom.librarymgmt.utils.SessionManager;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserManagementActivity extends AppCompatActivity {

    private RadioGroup rgUserType;
    private TextInputEditText etName;
    private MaterialSwitch cbActive, cbAdmin;
    private Button btnConfirm, btnCancel, btnHome, btnLogout;
    private TextView tvPageError;
    private ProgressBar progressBar;
    private SessionManager session;
    private FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);
        // Force Light Mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        session = new SessionManager(this);
        if (!session.isAdmin()) {
            Toast.makeText(this, R.string.err_access_denied, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        firebaseHelper = FirebaseHelper.getInstance();

        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText("User Management");

        rgUserType = findViewById(R.id.rg_user_type);
        etName = findViewById(R.id.et_name);
        cbActive = findViewById(R.id.cb_active);
        cbAdmin = findViewById(R.id.cb_admin);
        btnConfirm = findViewById(R.id.btn_confirm);
        btnCancel = findViewById(R.id.btn_cancel);
        btnHome = findViewById(R.id.btn_home);
        btnLogout = findViewById(R.id.btn_logout);
        tvPageError = findViewById(R.id.tv_page_error);
        progressBar = findViewById(R.id.progress_bar);

        btnConfirm.setOnClickListener(v -> saveUser());
        btnCancel.setOnClickListener(v -> finish());
        
        btnHome.setOnClickListener(v -> {
            startActivity(new Intent(this, AdminHomeActivity.class));
            finish();
        });
        
        btnLogout.setOnClickListener(v -> {
            session.clearSession();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.btn_toolbar_home).setOnClickListener(v -> btnHome.performClick());
    }

    private void saveUser() {
        String name = etName.getText().toString().trim();
        if (name.isEmpty()) {
            tvPageError.setText("Name is mandatory");
            tvPageError.setVisibility(View.VISIBLE);
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        boolean isNew = rgUserType.getCheckedRadioButtonId() == R.id.rb_new_user;

        if (isNew) {
            String userId = UUID.randomUUID().toString();
            // Using name as username for simplicity in this assessment seed
            String username = name.toLowerCase().replace(" ", "");
            User newUser = new User(userId, name, username, "password123", cbAdmin.isChecked(), cbActive.isChecked());
            
            firebaseHelper.getDb().collection(Constants.USERS).document(username).set(newUser)
                    .addOnSuccessListener(aVoid -> {
                        progressBar.setVisibility(View.GONE);
                        startActivity(new Intent(UserManagementActivity.this, ConfirmationActivity.class));
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        progressBar.setVisibility(View.GONE);
                        tvPageError.setText("Error: " + e.getMessage());
                        tvPageError.setVisibility(View.VISIBLE);
                    });
        } else {
            // Update existing by searching name
            firebaseHelper.getDb().collection(Constants.USERS)
                    .whereEqualTo("name", name)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (queryDocumentSnapshots.isEmpty()) {
                            progressBar.setVisibility(View.GONE);
                            tvPageError.setText("User not found");
                            tvPageError.setVisibility(View.VISIBLE);
                            return;
                        }

                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            Map<String, Object> updates = new HashMap<>();
                            updates.put("active", cbActive.isChecked());
                            updates.put("admin", cbAdmin.isChecked());
                            
                            doc.getReference().update(updates);
                        }
                        progressBar.setVisibility(View.GONE);
                        startActivity(new Intent(UserManagementActivity.this, ConfirmationActivity.class));
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        progressBar.setVisibility(View.GONE);
                        tvPageError.setText("Error: " + e.getMessage());
                        tvPageError.setVisibility(View.VISIBLE);
                    });
        }
    }
}
