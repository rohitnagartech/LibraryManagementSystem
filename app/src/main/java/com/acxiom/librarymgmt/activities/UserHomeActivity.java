package com.acxiom.librarymgmt.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.acxiom.librarymgmt.R;
import com.acxiom.librarymgmt.reports.ReportsActivity;
import com.acxiom.librarymgmt.transactions.TransactionsActivity;
import com.acxiom.librarymgmt.utils.SessionManager;
import com.google.android.material.card.MaterialCardView;

public class UserHomeActivity extends AppCompatActivity {

    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        session = new SessionManager(this);

        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(R.string.title_user_home);

        findViewById(R.id.btn_toolbar_home).setOnClickListener(v -> {
            // Already on home
        });

        MaterialCardView cardReports = findViewById(R.id.card_reports);
        MaterialCardView cardTransactions = findViewById(R.id.card_transactions);
        Button btnLogout = findViewById(R.id.btn_logout);

        cardReports.setOnClickListener(v -> startActivity(new Intent(this, ReportsActivity.class)));
        cardTransactions.setOnClickListener(v -> startActivity(new Intent(this, TransactionsActivity.class)));

        btnLogout.setOnClickListener(v -> {
            session.clearSession();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
