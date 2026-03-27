package com.acxiom.librarymgmt.maintenance;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.acxiom.librarymgmt.R;
import com.acxiom.librarymgmt.activities.AdminHomeActivity;
import com.acxiom.librarymgmt.activities.ConfirmationActivity;
import com.acxiom.librarymgmt.activities.LoginActivity;
import com.acxiom.librarymgmt.models.Membership;
import com.acxiom.librarymgmt.utils.Constants;
import com.acxiom.librarymgmt.utils.FirebaseHelper;
import com.acxiom.librarymgmt.utils.SessionManager;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddMembershipActivity extends AppCompatActivity {

    private TextInputEditText etFirstName, etLastName, etContactNumber, etContactAddress, etAadharNo;
    private EditText etStartDate, etEndDate;
    private RadioGroup rgDuration;
    private Button btnConfirm, btnCancel, btnHome, btnLogout;
    private TextView tvPageError;
    private ProgressBar progressBar;
    private SessionManager session;
    private FirebaseHelper firebaseHelper;
    private Calendar startCalendar = Calendar.getInstance();
    private Calendar endCalendar = Calendar.getInstance();
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_membership);

        session = new SessionManager(this);
        if (!session.isAdmin()) {
            Toast.makeText(this, R.string.err_access_denied, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        firebaseHelper = FirebaseHelper.getInstance();

        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Add Membership");

        etFirstName = findViewById(R.id.et_first_name);
        etLastName = findViewById(R.id.et_last_name);
        etContactNumber = findViewById(R.id.et_contact_number);
        etContactAddress = findViewById(R.id.et_contact_address);
        etAadharNo = findViewById(R.id.et_aadhar_no);
        etStartDate = findViewById(R.id.et_start_date);
        etEndDate = findViewById(R.id.et_end_date);
        rgDuration = findViewById(R.id.rg_duration);
        btnConfirm = findViewById(R.id.btn_confirm);
        btnCancel = findViewById(R.id.btn_cancel);
        btnHome = findViewById(R.id.btn_home);
        btnLogout = findViewById(R.id.btn_logout);
        tvPageError = findViewById(R.id.tv_page_error);
        progressBar = findViewById(R.id.progress_bar);

        etStartDate.setText(sdf.format(startCalendar.getTime()));
        updateEndDate();

        etStartDate.setOnClickListener(v -> showDatePicker(true));
        etEndDate.setOnClickListener(v -> showDatePicker(false));

        rgDuration.setOnCheckedChangeListener((group, checkedId) -> updateEndDate());

        btnConfirm.setOnClickListener(v -> addMembership());
        btnCancel.setOnClickListener(v -> finish());
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

    private void updateEndDate() {
        endCalendar = (Calendar) startCalendar.clone();
        int checkedId = rgDuration.getCheckedRadioButtonId();
        if (checkedId == R.id.rb_6months) endCalendar.add(Calendar.MONTH, 6);
        else if (checkedId == R.id.rb_1year) endCalendar.add(Calendar.YEAR, 1);
        else if (checkedId == R.id.rb_2years) endCalendar.add(Calendar.YEAR, 2);
        etEndDate.setText(sdf.format(endCalendar.getTime()));
    }

    private void showDatePicker(boolean isStartDate) {
        Calendar cal = isStartDate ? startCalendar : endCalendar;
        DatePickerDialog dpd = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            if (isStartDate) {
                startCalendar.set(year, month, dayOfMonth);
                etStartDate.setText(sdf.format(startCalendar.getTime()));
                updateEndDate();
            } else {
                endCalendar.set(year, month, dayOfMonth);
                etEndDate.setText(sdf.format(endCalendar.getTime()));
            }
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        dpd.show();
    }

    private void addMembership() {
        String fname = etFirstName.getText().toString().trim();
        String lname = etLastName.getText().toString().trim();
        String contact = etContactNumber.getText().toString().trim();
        String address = etContactAddress.getText().toString().trim();
        String aadhar = etAadharNo.getText().toString().trim();

        if (fname.isEmpty() || lname.isEmpty() || contact.isEmpty() || address.isEmpty() || aadhar.isEmpty()) {
            tvPageError.setText(R.string.err_all_fields_mandatory);
            tvPageError.setVisibility(View.VISIBLE);
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        String memId = "MEM" + System.currentTimeMillis();
        String type = "6months";
        int checkedId = rgDuration.getCheckedRadioButtonId();
        if (checkedId == R.id.rb_1year) type = "1year";
        else if (checkedId == R.id.rb_2years) type = "2years";

        Membership m = new Membership(memId, fname, lname, contact, address, aadhar, type, "active", startCalendar.getTime(), endCalendar.getTime(), 0.0);
        firebaseHelper.getDb().collection(Constants.MEMBERSHIPS).document(memId).set(m)
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    startActivity(new Intent(AddMembershipActivity.this, ConfirmationActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    tvPageError.setText("Error: " + e.getMessage());
                    tvPageError.setVisibility(View.VISIBLE);
                });
    }
}
