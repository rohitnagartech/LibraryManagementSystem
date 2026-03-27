package com.acxiom.librarymgmt.transactions;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.acxiom.librarymgmt.R;
import com.acxiom.librarymgmt.activities.AdminHomeActivity;
import com.acxiom.librarymgmt.activities.ConfirmationActivity;
import com.acxiom.librarymgmt.activities.LoginActivity;
import com.acxiom.librarymgmt.activities.UserHomeActivity;
import com.acxiom.librarymgmt.models.Issue;
import com.acxiom.librarymgmt.utils.Constants;
import com.acxiom.librarymgmt.utils.FirebaseHelper;
import com.acxiom.librarymgmt.utils.SessionManager;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PayFineActivity extends AppCompatActivity {

    private TextInputEditText etBookName, etAuthor, etSerialNo, etIssueDate, etReturnDate, etFineCalculated, etRemarks;
    private EditText etActualReturnDate;
    private CheckBox cbFinePaid;
    private Button btnConfirm, btnCancel, btnHome, btnLogout;
    private TextView tvPageError;
    private ProgressBar progressBar;
    private SessionManager session;
    private FirebaseHelper firebaseHelper;
    private String issueId;
    private Issue currentIssue;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private Calendar actualReturnCalendar = Calendar.getInstance();
    private double fineCalculated = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_fine);
        // Force Light Mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        session = new SessionManager(this);
        firebaseHelper = FirebaseHelper.getInstance();
        issueId = getIntent().getStringExtra("issueId");

        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(R.string.title_pay_fine);

        etBookName = findViewById(R.id.et_book_name);
        etAuthor = findViewById(R.id.et_author);
        etSerialNo = findViewById(R.id.et_serial_no);
        etIssueDate = findViewById(R.id.et_issue_date);
        etReturnDate = findViewById(R.id.et_return_date);
        etActualReturnDate = findViewById(R.id.et_actual_return_date);
        etFineCalculated = findViewById(R.id.et_fine_calculated);
        cbFinePaid = findViewById(R.id.cb_fine_paid);
        etRemarks = findViewById(R.id.et_remarks);
        btnConfirm = findViewById(R.id.btn_confirm);
        btnCancel = findViewById(R.id.btn_cancel);
        btnHome = findViewById(R.id.btn_home);
        btnLogout = findViewById(R.id.btn_logout);
        tvPageError = findViewById(R.id.tv_page_error);
        progressBar = findViewById(R.id.progress_bar);

        etActualReturnDate.setText(sdf.format(actualReturnCalendar.getTime()));

        loadIssueDetails();

        etActualReturnDate.setOnClickListener(v -> {
            DatePickerDialog dpd = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                actualReturnCalendar.set(year, month, dayOfMonth);
                etActualReturnDate.setText(sdf.format(actualReturnCalendar.getTime()));
                calculateFine();
            }, actualReturnCalendar.get(Calendar.YEAR), actualReturnCalendar.get(Calendar.MONTH), actualReturnCalendar.get(Calendar.DAY_OF_MONTH));
            dpd.show();
        });

        btnConfirm.setOnClickListener(v -> {
            if (fineCalculated > 0 && !cbFinePaid.isChecked()) {
                tvPageError.setText(R.string.err_fine_not_paid);
                tvPageError.setVisibility(View.VISIBLE);
                return;
            }
            confirmReturn();
        });

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

    private void loadIssueDetails() {
        if (issueId == null) return;
        progressBar.setVisibility(View.VISIBLE);
        firebaseHelper.getDb().collection(Constants.ISSUES).document(issueId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    progressBar.setVisibility(View.GONE);
                    if (documentSnapshot.exists()) {
                        currentIssue = documentSnapshot.toObject(Issue.class);
                        if (currentIssue != null) {
                            etBookName.setText(currentIssue.getBookName());
                            etAuthor.setText(currentIssue.getAuthorName());
                            etSerialNo.setText(currentIssue.getSerialNo());
                            etIssueDate.setText(sdf.format(currentIssue.getIssueDate()));
                            etReturnDate.setText(sdf.format(currentIssue.getReturnDate()));
                            calculateFine();
                        }
                    }
                }).addOnFailureListener(e -> progressBar.setVisibility(View.GONE));
    }

    private void calculateFine() {
        if (currentIssue == null) return;
        long diff = actualReturnCalendar.getTimeInMillis() - currentIssue.getReturnDate().getTime();
        long days = TimeUnit.MILLISECONDS.toDays(diff);
        if (days > 0) {
            fineCalculated = days * Constants.FINE_PER_DAY;
        } else {
            fineCalculated = 0.0;
        }
        etFineCalculated.setText(String.format(Locale.getDefault(), "%.2f", fineCalculated));
    }

    private void confirmReturn() {
        progressBar.setVisibility(View.VISIBLE);
        Map<String, Object> updateIssue = new HashMap<>();
        updateIssue.put("actualReturnDate", actualReturnCalendar.getTime());
        updateIssue.put("status", "returned");
        updateIssue.put("fineCalculated", fineCalculated);
        updateIssue.put("finePaid", cbFinePaid.isChecked());
        updateIssue.put("remarks", etRemarks.getText().toString());

        firebaseHelper.getDb().collection(Constants.ISSUES).document(issueId).update(updateIssue)
                .addOnSuccessListener(aVoid -> {
                    Map<String, Object> updateBook = new HashMap<>();
                    updateBook.put("status", "available");
                    firebaseHelper.getDb().collection(Constants.BOOKS).document(currentIssue.getSerialNo()).update(updateBook)
                            .addOnSuccessListener(aVoid1 -> {
                                progressBar.setVisibility(View.GONE);
                                startActivity(new Intent(PayFineActivity.this, ConfirmationActivity.class));
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
