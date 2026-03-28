package com.acxiom.librarymgmt.transactions;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.acxiom.librarymgmt.activities.CancelActivity;
import com.acxiom.librarymgmt.activities.LoginActivity;
import com.acxiom.librarymgmt.activities.UserHomeActivity;
import com.acxiom.librarymgmt.models.Issue;
import com.acxiom.librarymgmt.utils.Constants;
import com.acxiom.librarymgmt.utils.FirebaseHelper;
import com.acxiom.librarymgmt.utils.SessionManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ReturnBookActivity extends AppCompatActivity {

    private AutoCompleteTextView actvBookName, actvSerialNo;
    private TextInputEditText etAuthor, etIssueDate, etRemarks, etReturnDate;
    private Button btnConfirm, btnCancel, btnHome, btnLogout;
    private TextView tvPageError;
    private ProgressBar progressBar;
    private SessionManager session;
    private FirebaseHelper firebaseHelper;
    private List<Issue> activeIssues = new ArrayList<>();
    private Issue selectedIssue;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private Calendar actualReturnCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_book);
        // Force Light Mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        session = new SessionManager(this);
        firebaseHelper = FirebaseHelper.getInstance();

        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(R.string.title_return_book);

        actvBookName = findViewById(R.id.actv_book_name);
        etAuthor = findViewById(R.id.et_author);
        actvSerialNo = findViewById(R.id.actv_serial_no);
        etIssueDate = findViewById(R.id.et_issue_date);
        etReturnDate = findViewById(R.id.et_return_date);
        etRemarks = findViewById(R.id.et_remarks);
        btnConfirm = findViewById(R.id.btn_confirm);
        btnCancel = findViewById(R.id.btn_cancel);
        btnHome = findViewById(R.id.btn_home);
        btnLogout = findViewById(R.id.btn_logout);
        tvPageError = findViewById(R.id.tv_page_error);
        progressBar = findViewById(R.id.progress_bar);

        // Pre-fill today's date for return
        etReturnDate.setText(sdf.format(actualReturnCalendar.getTime()));

        loadActiveIssues();

        actvBookName.setOnItemClickListener((parent, view, position, id) -> {
            String selection = (String) parent.getItemAtPosition(position);
            updateSerialNumbers(selection);
        });

        actvSerialNo.setOnItemClickListener((parent, view, position, id) -> {
            String serial = (String) parent.getItemAtPosition(position);
            updateIssueDetails(serial);
        });

        // Add text watchers to clear details if user types instead of selecting
        actvBookName.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                selectedIssue = null;
                etAuthor.setText("");
                actvSerialNo.setText("");
                etIssueDate.setText("");
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        actvSerialNo.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                selectedIssue = null;
                etIssueDate.setText("");
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        etReturnDate.setOnClickListener(v -> {
            DatePickerDialog dpd = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                actualReturnCalendar.set(year, month, dayOfMonth);
                etReturnDate.setText(sdf.format(actualReturnCalendar.getTime()));
            }, actualReturnCalendar.get(Calendar.YEAR), actualReturnCalendar.get(Calendar.MONTH), actualReturnCalendar.get(Calendar.DAY_OF_MONTH));
            dpd.show();
        });

        btnConfirm.setOnClickListener(v -> {
            String bookNameText = actvBookName.getText().toString().trim();
            String serialNoText = actvSerialNo.getText().toString().trim();

            if (bookNameText.isEmpty() || serialNoText.isEmpty()) {
                tvPageError.setText("Please select Book Name and Serial No");
                tvPageError.setVisibility(View.VISIBLE);
                return;
            }

            // Robust validation: search through all active issues
            if (selectedIssue == null) {
                for (Issue i : activeIssues) {
                    boolean matchesName = i.getBookName().equalsIgnoreCase(bookNameText) || 
                                       i.getAuthorName().equalsIgnoreCase(bookNameText);
                    if (matchesName && i.getSerialNo().equalsIgnoreCase(serialNoText)) {
                        selectedIssue = i;
                        break;
                    }
                }
            }

            if (selectedIssue == null) {
                tvPageError.setText("Invalid selection. Please re-select book and serial number.");
                tvPageError.setVisibility(View.VISIBLE);
                return;
            }
            
            Intent intent = new Intent(this, PayFineActivity.class);
            intent.putExtra("issueId", selectedIssue.getIssueId());
            intent.putExtra("actualReturnDate", etReturnDate.getText().toString());
            intent.putExtra("remarks", etRemarks.getText().toString());
            startActivity(intent);
        });

        btnCancel.setOnClickListener(v -> startActivity(new Intent(this, CancelActivity.class)));

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

    private void updateSerialNumbers(String searchText) {
        List<String> serials = new ArrayList<>();
        String author = "";
        
        for (Issue i : activeIssues) {
            if (i.getBookName().equalsIgnoreCase(searchText) || i.getAuthorName().equalsIgnoreCase(searchText)) {
                if (!serials.contains(i.getSerialNo())) {
                    serials.add(i.getSerialNo());
                }
                if (author.isEmpty()) {
                    author = i.getAuthorName();
                }
            }
        }

        if (!serials.isEmpty()) {
            etAuthor.setText(author);
            ArrayAdapter<String> serialAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, serials);
            actvSerialNo.setAdapter(serialAdapter);
            actvSerialNo.setText("");
            etIssueDate.setText("");
            selectedIssue = null;
        }
    }

    private void updateIssueDetails(String serial) {
        String searchText = actvBookName.getText().toString().trim();
        for (Issue i : activeIssues) {
            boolean matchesName = i.getBookName().equalsIgnoreCase(searchText) || 
                               i.getAuthorName().equalsIgnoreCase(searchText);
            if (matchesName && i.getSerialNo().equalsIgnoreCase(serial)) {
                selectedIssue = i;
                etIssueDate.setText(sdf.format(i.getIssueDate()));
                break;
            }
        }
    }

    private void loadActiveIssues() {
        progressBar.setVisibility(View.VISIBLE);
        
        // Remove membershipId filter to allow library staff (user login) to see all issues for return
        firebaseHelper.getDb().collection(Constants.ISSUES)
                .whereEqualTo("status", "active")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    progressBar.setVisibility(View.GONE);
                    activeIssues.clear();
                    List<String> suggestions = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Issue issue = doc.toObject(Issue.class);
                        activeIssues.add(issue);
                        
                        if (!suggestions.contains(issue.getBookName())) {
                            suggestions.add(issue.getBookName());
                        }
                        if (!suggestions.contains(issue.getAuthorName())) {
                            suggestions.add(issue.getAuthorName());
                        }
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, suggestions);
                    actvBookName.setAdapter(adapter);
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    tvPageError.setText("Error loading issues: " + e.getMessage());
                    tvPageError.setVisibility(View.VISIBLE);
                });
    }
}
