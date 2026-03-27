package com.acxiom.librarymgmt.transactions;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.acxiom.librarymgmt.R;
import com.acxiom.librarymgmt.activities.AdminHomeActivity;
import com.acxiom.librarymgmt.activities.LoginActivity;
import com.acxiom.librarymgmt.activities.UserHomeActivity;
import com.acxiom.librarymgmt.models.Book;
import com.acxiom.librarymgmt.utils.Constants;
import com.acxiom.librarymgmt.utils.FirebaseHelper;
import com.acxiom.librarymgmt.utils.SessionManager;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BookAvailableActivity extends AppCompatActivity {

    private AutoCompleteTextView actvBookName, actvAuthor;
    private Button btnSearch, btnHome, btnLogout;
    private TextView tvError;
    private ProgressBar progressBar;
    private SessionManager session;
    private FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_available);
// Force Light Mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        session = new SessionManager(this);
        firebaseHelper = FirebaseHelper.getInstance();

        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(R.string.title_book_availability);

        actvBookName = findViewById(R.id.actv_book_name);
        actvAuthor = findViewById(R.id.actv_author);
        btnSearch = findViewById(R.id.btn_search);
        btnHome = findViewById(R.id.btn_home);
        btnLogout = findViewById(R.id.btn_logout);
        tvError = findViewById(R.id.tv_error);
        progressBar = findViewById(R.id.progress_bar);

        loadDropdownData();

        btnSearch.setOnClickListener(v -> {
            String bookName = actvBookName.getText().toString().trim();
            String author = actvAuthor.getText().toString().trim();

            if (bookName.isEmpty() && author.isEmpty()) {
                tvError.setVisibility(View.VISIBLE);
            } else {
                tvError.setVisibility(View.GONE);
                Intent intent = new Intent(this, SearchResultsActivity.class);
                intent.putExtra("bookName", bookName);
                intent.putExtra("author", author);
                startActivity(intent);
            }
        });

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

    private void loadDropdownData() {
        progressBar.setVisibility(View.VISIBLE);
        firebaseHelper.getDb().collection(Constants.BOOKS).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    progressBar.setVisibility(View.GONE);
                    Set<String> bookNames = new HashSet<>();
                    Set<String> authors = new HashSet<>();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Book book = doc.toObject(Book.class);
                        if (book.getName() != null) bookNames.add(book.getName());
                        if (book.getAuthorName() != null) authors.add(book.getAuthorName());
                    }

                    ArrayAdapter<String> bookAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<>(bookNames));
                    actvBookName.setAdapter(bookAdapter);

                    ArrayAdapter<String> authorAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<>(authors));
                    actvAuthor.setAdapter(authorAdapter);
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                });
    }
}
