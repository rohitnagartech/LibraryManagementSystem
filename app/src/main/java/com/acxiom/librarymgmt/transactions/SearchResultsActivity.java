package com.acxiom.librarymgmt.transactions;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.acxiom.librarymgmt.R;
import com.acxiom.librarymgmt.activities.AdminHomeActivity;
import com.acxiom.librarymgmt.activities.LoginActivity;
import com.acxiom.librarymgmt.activities.UserHomeActivity;
import com.acxiom.librarymgmt.adapters.BookListAdapter;
import com.acxiom.librarymgmt.models.Book;
import com.acxiom.librarymgmt.utils.Constants;
import com.acxiom.librarymgmt.utils.FirebaseHelper;
import com.acxiom.librarymgmt.utils.SessionManager;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsActivity extends AppCompatActivity {

    private RecyclerView rvSearchResults;
    private BookListAdapter adapter;
    private List<Book> bookList;
    private Button btnIssueBook, btnHome, btnLogout;
    private TextView tvError;
    private View tvNoRecords;
    private ProgressBar progressBar;
    private SessionManager session;
    private FirebaseHelper firebaseHelper;
    private String selectedSerialNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        // Force Light Mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        session = new SessionManager(this);
        firebaseHelper = FirebaseHelper.getInstance();

        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(R.string.title_book_availability);

        rvSearchResults = findViewById(R.id.rv_search_results);
        btnIssueBook = findViewById(R.id.btn_issue_book);
        btnHome = findViewById(R.id.btn_home);
        btnLogout = findViewById(R.id.btn_logout);
        tvError = findViewById(R.id.tv_error);
        tvNoRecords = findViewById(R.id.tv_no_records);
        progressBar = findViewById(R.id.progress_bar);

        bookList = new ArrayList<>();
        rvSearchResults.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BookListAdapter(bookList, serialNo -> {
            selectedSerialNo = serialNo;
            tvError.setVisibility(View.GONE);
        });
        rvSearchResults.setAdapter(adapter);

        String bookName = getIntent().getStringExtra("bookName");
        String author = getIntent().getStringExtra("author");

        performSearch(bookName, author);

        btnIssueBook.setOnClickListener(v -> {
            if (selectedSerialNo == null) {
                tvError.setVisibility(View.VISIBLE);
            } else {
                Intent intent = new Intent(this, BookIssueActivity.class);
                intent.putExtra("serialNo", selectedSerialNo);
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

    private void performSearch(String bookName, String author) {
        progressBar.setVisibility(View.VISIBLE);
        Query query = firebaseHelper.getDb().collection(Constants.BOOKS);

        if (bookName != null && !bookName.isEmpty()) {
            query = query.whereEqualTo("name", bookName);
        }
        if (author != null && !author.isEmpty()) {
            query = query.whereEqualTo("authorName", author);
        }

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            progressBar.setVisibility(View.GONE);
            bookList.clear();
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                bookList.add(doc.toObject(Book.class));
            }
            adapter.notifyDataSetChanged();
            if (bookList.isEmpty()) {
                tvNoRecords.setVisibility(View.VISIBLE);
            } else {
                tvNoRecords.setVisibility(View.GONE);
            }
        }).addOnFailureListener(e -> {
            progressBar.setVisibility(View.GONE);
        });
    }
}
