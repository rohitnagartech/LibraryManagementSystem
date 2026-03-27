package com.acxiom.librarymgmt.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.acxiom.librarymgmt.R;
import com.acxiom.librarymgmt.utils.SessionManager;

public class ConfirmationActivity extends AppCompatActivity {

    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);

        session = new SessionManager(this);

        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Transaction Successful");

        Button btnHome = findViewById(R.id.btn_home);
        Button btnLogout = findViewById(R.id.btn_logout);

        btnHome.setOnClickListener(v -> {
            Intent intent;
            if (session.isAdmin()) {
                intent = new Intent(this, AdminHomeActivity.class);
            } else {
                intent = new Intent(this, UserHomeActivity.class);
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        btnLogout.setOnClickListener(v -> {
            session.clearSession();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
