package com.acxiom.librarymgmt.transactions;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.acxiom.librarymgmt.R;
import com.acxiom.librarymgmt.activities.AdminHomeActivity;
import com.acxiom.librarymgmt.activities.ConfirmationActivity;
import com.acxiom.librarymgmt.activities.LoginActivity;
import com.acxiom.librarymgmt.activities.UserHomeActivity;
import com.acxiom.librarymgmt.models.Book;
import com.acxiom.librarymgmt.models.Issue;
import com.acxiom.librarymgmt.utils.Constants;
import com.acxiom.librarymgmt.utils.FirebaseHelper;
import com.acxiom.librarymgmt.utils.SessionManager;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class BookIssueActivity extends AppCompatActivity {

    private TextInputEditText etBookName, etAuthor, etMembershipId, etRemarks;
    private EditText etIssueDate, etReturnDate;
    private Button btnConfirm, btnCancel, btnHome, btnLogout;
    private TextView tvPageError;
    private ProgressBar progressBar;
    private SessionManager session;
    private FirebaseHelper firebaseHelper;
    private String serialNo;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private Calendar calendarIssue = Calendar.getInstance();
    private Calendar calendarReturn = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_issue);

        session = new SessionManager(this);
        firebaseHelper = FirebaseHelper.getInstance();
        serialNo = getIntent().getStringExtra("serialNo");

        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(R.string.title_book_issue);

        etBookName = findViewById(R.id.et_book_name);
        etAuthor = findViewById(R.id.et_author);
        etMembershipId = findViewById(R.id.et_membership_id);
        etIssueDate = findViewById(R.id.et_issue_date);
        etReturnDate = findViewById(R.id.et_return_date);
        etRemarks = findViewById(R.id.et_remarks);
        btnConfirm = findViewById(R.id.btn_confirm);
        btnCancel = findViewById(R.id.btn_cancel);
        btnHome = findViewById(R.id.btn_home);
        btnLogout = findViewById(R.id.btn_logout);
        tvPageError = findViewById(R.id.tv_page_error);
        progressBar = findViewById(R.id.progress_bar);

        // Set default dates
        etIssueDate.setText(sdf.format(calendarIssue.getTime()));
        calendarReturn.add(Calendar.DAY_OF_YEAR, 15);
        etReturnDate.setText(sdf.format(calendarReturn.getTime()));

        loadBookDetails();

        etIssueDate.setOnClickListener(v -> showDatePicker(true));
        etReturnDate.setOnClickListener(v -> showDatePicker(false));

        btnConfirm.setOnClickListener(v -> issueBook());
        btnCancel.setOnClickListener(v -> finish());

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

    private void loadBookDetails() {
        if (serialNo == null) return;
        progressBar.setVisibility(View.VISIBLE);
        firebaseHelper.getDb().collection(Constants.BOOKS).document(serialNo).get()
                .addOnSuccessListener(documentSnapshot -> {
                    progressBar.setVisibility(View.GONE);
                    if (documentSnapshot.exists()) {
                        Book book = documentSnapshot.toObject(Book.class);
                        if (book != null) {
                            etBookName.setText(book.getName());
                            etAuthor.setText(book.getAuthorName());
                        }
                    }
                }).addOnFailureListener(e -> progressBar.setVisibility(View.GONE));
    }

    private void showDatePicker(boolean isIssueDate) {
        Calendar cal = isIssueDate ? calendarIssue : calendarReturn;
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            Calendar selected = Calendar.getInstance();
            selected.set(year, month, dayOfMonth);
            
            if (isIssueDate) {
                if (selected.before(Calendar.getInstance())) {
                    tvPageError.setText("Issue date cannot be less than today");
                    tvPageError.setVisibility(View.VISIBLE);
                    return;
                }
                calendarIssue = selected;
                etIssueDate.setText(sdf.format(calendarIssue.getTime()));
                // Update default return date to issue + 15
                calendarReturn = (Calendar) calendarIssue.clone();
                calendarReturn.add(Calendar.DAY_OF_YEAR, 15);
                etReturnDate.setText(sdf.format(calendarReturn.getTime()));
            } else {
                Calendar maxReturn = (Calendar) calendarIssue.clone();
                maxReturn.add(Calendar.DAY_OF_YEAR, 15);
                if (selected.after(maxReturn)) {
                    tvPageError.setText("Return date cannot be more than 15 days from issue date");
                    tvPageError.setVisibility(View.VISIBLE);
                    return;
                }
                calendarReturn = selected;
                etReturnDate.setText(sdf.format(calendarReturn.getTime()));
            }
            tvPageError.setVisibility(View.GONE);
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void issueBook() {
        String bookName = etBookName.getText().toString().trim();
        String membershipId = etMembershipId.getText().toString().trim();

        if (bookName.isEmpty() || membershipId.isEmpty()) {
            tvPageError.setText(R.string.err_all_fields_mandatory);
            tvPageError.setVisibility(View.VISIBLE);
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        String issueId = UUID.randomUUID().toString();
        Issue issue = new Issue(issueId, serialNo, bookName, etAuthor.getText().toString(), membershipId, 
                calendarIssue.getTime(), calendarReturn.getTime(), null, "active", 0.0, false, etRemarks.getText().toString());

        firebaseHelper.getDb().collection(Constants.ISSUES).document(issueId).set(issue)
                .addOnSuccessListener(aVoid -> {
                    Map<String, Object> update = new HashMap<>();
                    update.put("status", "issued");
                    firebaseHelper.getDb().collection(Constants.BOOKS).document(serialNo).update(update)
                            .addOnSuccessListener(aVoid1 -> {
                                progressBar.setVisibility(View.GONE);
                                startActivity(new Intent(BookIssueActivity.this, ConfirmationActivity.class));
                                finish();
                            });
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    tvPageError.setText("Error: " + e.getMessage());
                    tvPageError.setVisibility(View.VISIBLE);
                });
    }
}
