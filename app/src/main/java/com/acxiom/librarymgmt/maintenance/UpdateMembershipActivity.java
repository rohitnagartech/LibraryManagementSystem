package com.acxiom.librarymgmt.maintenance;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class UpdateMembershipActivity extends AppCompatActivity {

    private TextInputEditText etMembershipId, etFirstName, etLastName;
    private EditText etStartDate, etEndDate;
    private RadioGroup rgExtension;
    private CheckBox cbRemove;
    private Button btnFetch, btnConfirm, btnCancel, btnHome, btnLogout;
    private TextView tvPageError;
    private ProgressBar progressBar;
    private SessionManager session;
    private FirebaseHelper firebaseHelper;
    private Membership currentMembership;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private Calendar startCalendar = Calendar.getInstance();
    private Calendar endCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_membership);
        // Force Light Mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        session = new SessionManager(this);
        if (!session.isAdmin()) {
            Toast.makeText(this, R.string.err_access_denied, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        firebaseHelper = FirebaseHelper.getInstance();

        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Update Membership");

        etMembershipId = findViewById(R.id.et_membership_id);
        etFirstName = findViewById(R.id.et_first_name);
        etLastName = findViewById(R.id.et_last_name);
        etStartDate = findViewById(R.id.et_start_date);
        etEndDate = findViewById(R.id.et_end_date);
        rgExtension = findViewById(R.id.rg_extension);
        cbRemove = findViewById(R.id.rb_remove);
        btnFetch = findViewById(R.id.btn_fetch);
        btnConfirm = findViewById(R.id.btn_confirm);
        btnCancel = findViewById(R.id.btn_cancel);
        btnHome = findViewById(R.id.btn_home);
        btnLogout = findViewById(R.id.btn_logout);
        tvPageError = findViewById(R.id.tv_page_error);
        progressBar = findViewById(R.id.progress_bar);

        btnFetch.setOnClickListener(v -> fetchMembership());

        etStartDate.setOnClickListener(v -> showDatePicker(true));
        etEndDate.setOnClickListener(v -> showDatePicker(false));

        rgExtension.setOnCheckedChangeListener((group, checkedId) -> {
            if (currentMembership != null && checkedId != -1) {
                extendMembership();
            }
        });

        btnConfirm.setOnClickListener(v -> updateMembership());
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

    private void fetchMembership() {
        String memId = etMembershipId.getText().toString().trim();
        if (memId.isEmpty()) {
            tvPageError.setText("Please enter Membership Number");
            tvPageError.setVisibility(View.VISIBLE);
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        firebaseHelper.getDb().collection(Constants.MEMBERSHIPS).document(memId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    progressBar.setVisibility(View.GONE);
                    if (documentSnapshot.exists()) {
                        currentMembership = documentSnapshot.toObject(Membership.class);
                        if (currentMembership != null) {
                            etFirstName.setText(currentMembership.getFirstName());
                            etLastName.setText(currentMembership.getLastName());
                            etStartDate.setText(sdf.format(currentMembership.getStartDate()));
                            etEndDate.setText(sdf.format(currentMembership.getEndDate()));
                            startCalendar.setTime(currentMembership.getStartDate());
                            endCalendar.setTime(currentMembership.getEndDate());
                            tvPageError.setVisibility(View.GONE);
                        }
                    } else {
                        tvPageError.setText("Membership not found");
                        tvPageError.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    tvPageError.setText("Error: " + e.getMessage());
                    tvPageError.setVisibility(View.VISIBLE);
                });
    }

    private void extendMembership() {
        endCalendar.setTime(currentMembership.getEndDate());
        int checkedId = rgExtension.getCheckedRadioButtonId();
        if (checkedId == R.id.rb_ext_6months) endCalendar.add(Calendar.MONTH, 6);
        else if (checkedId == R.id.rb_ext_1year) endCalendar.add(Calendar.YEAR, 1);
        else if (checkedId == R.id.rb_ext_2years) endCalendar.add(Calendar.YEAR, 2);
        etEndDate.setText(sdf.format(endCalendar.getTime()));
    }

    private void showDatePicker(boolean isStartDate) {
        if (currentMembership == null) return;
        Calendar cal = isStartDate ? startCalendar : endCalendar;
        DatePickerDialog dpd = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            if (isStartDate) {
                startCalendar.set(year, month, dayOfMonth);
                etStartDate.setText(sdf.format(startCalendar.getTime()));
            } else {
                endCalendar.set(year, month, dayOfMonth);
                etEndDate.setText(sdf.format(endCalendar.getTime()));
            }
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        dpd.show();
    }

    private void updateMembership() {
        if (currentMembership == null) {
            tvPageError.setText("Please fetch a membership first");
            tvPageError.setVisibility(View.VISIBLE);
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        Map<String, Object> updates = new HashMap<>();
        updates.put("startDate", startCalendar.getTime());
        updates.put("endDate", endCalendar.getTime());
        
        if (cbRemove.isChecked()) {
            updates.put("status", "inactive");
        }

        firebaseHelper.getDb().collection(Constants.MEMBERSHIPS).document(currentMembership.getMembershipId()).update(updates)
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    startActivity(new Intent(UpdateMembershipActivity.this, ConfirmationActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    tvPageError.setText("Error: " + e.getMessage());
                    tvPageError.setVisibility(View.VISIBLE);
                });
    }
}
