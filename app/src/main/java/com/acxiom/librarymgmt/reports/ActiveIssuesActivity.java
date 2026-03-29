package com.acxiom.librarymgmt.reports;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
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
import com.acxiom.librarymgmt.adapters.IssueAdapter;
import com.acxiom.librarymgmt.models.Issue;
import com.acxiom.librarymgmt.utils.Constants;
import com.acxiom.librarymgmt.utils.FirebaseHelper;
import com.acxiom.librarymgmt.utils.SessionManager;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ActiveIssuesActivity extends AppCompatActivity {

    private RecyclerView rvReport;
    private IssueAdapter adapter;
    private List<Issue> issueList = new ArrayList<>();
    private ProgressBar progressBar;
    private View tvNoRecords;
    private SessionManager session;
    private FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_list);
        // Force Light Mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        session = new SessionManager(this);
        firebaseHelper = FirebaseHelper.getInstance();

        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Active Issues");

        progressBar = findViewById(R.id.progress_bar);
        tvNoRecords = findViewById(R.id.tv_no_records);
        rvReport = findViewById(R.id.rv_report);
        rvReport.setLayoutManager(new LinearLayoutManager(this));
        adapter = new IssueAdapter(issueList);
        rvReport.setAdapter(adapter);

        setupHeader();
        setupSideNav();
        loadData();

        findViewById(R.id.btn_home).setOnClickListener(v -> {
            if (session.isAdmin()) startActivity(new Intent(this, AdminHomeActivity.class));
            else startActivity(new Intent(this, UserHomeActivity.class));
            finish();
        });

        findViewById(R.id.btn_logout).setOnClickListener(v -> {
            session.clearSession();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
        
        findViewById(R.id.btn_toolbar_home).setOnClickListener(v -> findViewById(R.id.btn_home).performClick());
    }

    private void setupHeader() {
        LinearLayout header = findViewById(R.id.layout_table_header);
        header.removeAllViews();
        addHeaderText(header, "Serial No Book/Movie", 100);
        addHeaderText(header, "Name of Book/Movie", 150);
        addHeaderText(header, "Membership Id", 100);
        addHeaderText(header, "Date of Issue", 100);
        addHeaderText(header, "Date of Return", 100);
        addHeaderText(header, "Fine", 80);
    }

    private void addHeaderText(LinearLayout parent, String text, int widthDp) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextColor(getResources().getColor(android.R.color.black));
        tv.setTypeface(null, android.graphics.Typeface.BOLD);
        tv.setPadding(8, 8, 8, 8);
        float scale = getResources().getDisplayMetrics().density;
        tv.setLayoutParams(new LinearLayout.LayoutParams((int) (widthDp * scale), LinearLayout.LayoutParams.WRAP_CONTENT));
        parent.addView(tv);
    }

    private void setupSideNav() {
        findViewById(R.id.nav_books).setOnClickListener(v -> { startActivity(new Intent(this, MasterBooksActivity.class)); finish(); });
        findViewById(R.id.nav_movies).setOnClickListener(v -> { startActivity(new Intent(this, MasterMoviesActivity.class)); finish(); });
        findViewById(R.id.nav_memberships).setOnClickListener(v -> { startActivity(new Intent(this, MasterMembershipsActivity.class)); finish(); });
        findViewById(R.id.nav_active_issues).setOnClickListener(v -> {});
        findViewById(R.id.nav_overdue).setOnClickListener(v -> { startActivity(new Intent(this, OverdueReturnsActivity.class)); finish(); });
        findViewById(R.id.nav_requests).setOnClickListener(v -> { startActivity(new Intent(this, IssueRequestsActivity.class)); finish(); });
    }

    private void loadData() {
        progressBar.setVisibility(View.VISIBLE);
        firebaseHelper.getDb().collection(Constants.ISSUES)
                .whereEqualTo("status", "active")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    progressBar.setVisibility(View.GONE);
                    issueList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        issueList.add(doc.toObject(Issue.class));
                    }
                    adapter.notifyDataSetChanged();
                    tvNoRecords.setVisibility(issueList.isEmpty() ? View.VISIBLE : View.GONE);
                })
                .addOnFailureListener(e -> progressBar.setVisibility(View.GONE));
    }
}
