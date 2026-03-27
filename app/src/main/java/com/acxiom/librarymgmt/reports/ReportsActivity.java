package com.acxiom.librarymgmt.reports;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.acxiom.librarymgmt.R;
import com.acxiom.librarymgmt.activities.AdminHomeActivity;
import com.acxiom.librarymgmt.activities.LoginActivity;
import com.acxiom.librarymgmt.activities.UserHomeActivity;
import com.acxiom.librarymgmt.utils.SessionManager;

public class ReportsActivity extends AppCompatActivity {

    private ListView lvReports;
    private SessionManager session;
    private String[] reports = {
            "Master List of Books",
            "Master List of Movies",
            "Master List of Memberships",
            "Active Issues",
            "Overdue Returns",
            "Issue Requests"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        session = new SessionManager(this);

        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(R.string.title_reports);

        lvReports = findViewById(R.id.lv_reports);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, reports);
        lvReports.setAdapter(adapter);

        lvReports.setOnItemClickListener((parent, view, position, id) -> {
            switch (position) {
                case 0: startActivity(new Intent(this, MasterBooksActivity.class)); break;
                case 1: startActivity(new Intent(this, MasterMoviesActivity.class)); break;
                case 2: startActivity(new Intent(this, MasterMembershipsActivity.class)); break;
                case 3: startActivity(new Intent(this, ActiveIssuesActivity.class)); break;
                case 4: startActivity(new Intent(this, OverdueReturnsActivity.class)); break;
                case 5: startActivity(new Intent(this, IssueRequestsActivity.class)); break;
            }
        });

        Button btnHome = findViewById(R.id.btn_home);
        Button btnLogout = findViewById(R.id.btn_logout);

        btnHome.setOnClickListener(v -> {
            if (session.isAdmin()) startActivity(new Intent(this, AdminHomeActivity.class));
            else startActivity(new Intent(this, UserHomeActivity.class));
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
