package com.acxiom.librarymgmt.maintenance;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.acxiom.librarymgmt.R;
import com.acxiom.librarymgmt.activities.AdminHomeActivity;
import com.acxiom.librarymgmt.activities.LoginActivity;
import com.acxiom.librarymgmt.activities.UserHomeActivity;
import com.acxiom.librarymgmt.utils.SessionManager;

public class MaintenanceActivity extends AppCompatActivity {

    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance);

        session = new SessionManager(this);
        if (!session.isAdmin()) {
            Toast.makeText(this, R.string.err_access_denied, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(R.string.title_maintenance);

        findViewById(R.id.nav_add_membership).setOnClickListener(v -> startActivity(new Intent(this, AddMembershipActivity.class)));
        findViewById(R.id.nav_update_membership).setOnClickListener(v -> startActivity(new Intent(this, UpdateMembershipActivity.class)));
        findViewById(R.id.nav_add_book).setOnClickListener(v -> startActivity(new Intent(this, AddBookActivity.class)));
        findViewById(R.id.nav_update_book).setOnClickListener(v -> startActivity(new Intent(this, UpdateBookActivity.class)));
        findViewById(R.id.nav_user_mgmt).setOnClickListener(v -> startActivity(new Intent(this, UserManagementActivity.class)));

        Button btnHome = findViewById(R.id.btn_home);
        Button btnLogout = findViewById(R.id.btn_logout);

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
}
