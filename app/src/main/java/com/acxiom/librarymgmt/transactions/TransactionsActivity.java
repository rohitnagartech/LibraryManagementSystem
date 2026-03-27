package com.acxiom.librarymgmt.transactions;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.acxiom.librarymgmt.R;
import com.acxiom.librarymgmt.activities.AdminHomeActivity;
import com.acxiom.librarymgmt.activities.LoginActivity;
import com.acxiom.librarymgmt.activities.UserHomeActivity;
import com.acxiom.librarymgmt.utils.SessionManager;
import com.google.android.material.card.MaterialCardView;

public class TransactionsActivity extends AppCompatActivity {

    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        session = new SessionManager(this);

        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(R.string.title_transactions);

        MaterialCardView cardBookAvailable = findViewById(R.id.card_book_available);
        MaterialCardView cardIssueBook = findViewById(R.id.card_issue_book);
        MaterialCardView cardReturnBook = findViewById(R.id.card_return_book);
        MaterialCardView cardPayFine = findViewById(R.id.card_pay_fine);
        Button btnHome = findViewById(R.id.btn_home);
        Button btnLogout = findViewById(R.id.btn_logout);

        cardBookAvailable.setOnClickListener(v -> startActivity(new Intent(this, BookAvailableActivity.class)));
        cardIssueBook.setOnClickListener(v -> startActivity(new Intent(this, BookAvailableActivity.class))); // Same flow as per prompt
        cardReturnBook.setOnClickListener(v -> startActivity(new Intent(this, ReturnBookActivity.class)));
        cardPayFine.setOnClickListener(v -> startActivity(new Intent(this, PayFineActivity.class)));

        btnHome.setOnClickListener(v -> {
            if (session.isAdmin()) {
                startActivity(new Intent(this, AdminHomeActivity.class));
            } else {
                startActivity(new Intent(this, UserHomeActivity.class));
            }
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
}
