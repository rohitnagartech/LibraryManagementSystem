package com.acxiom.librarymgmt.maintenance;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.acxiom.librarymgmt.R;
import com.acxiom.librarymgmt.activities.AdminHomeActivity;
import com.acxiom.librarymgmt.activities.ConfirmationActivity;
import com.acxiom.librarymgmt.activities.LoginActivity;
import com.acxiom.librarymgmt.models.Book;
import com.acxiom.librarymgmt.utils.Constants;
import com.acxiom.librarymgmt.utils.FirebaseHelper;
import com.acxiom.librarymgmt.utils.SessionManager;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddBookActivity extends AppCompatActivity {

    private RadioGroup rgType;
    private TextInputEditText etName, etAuthor, etQuantity, etCost;
    private Spinner spinnerCategory;
    private EditText etProcurementDate;
    private Button btnConfirm, btnCancel, btnHome, btnLogout;
    private TextView tvPageError;
    private ProgressBar progressBar;
    private SessionManager session;
    private FirebaseHelper firebaseHelper;
    private Calendar procCalendar = Calendar.getInstance();
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);
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
        toolbarTitle.setText("Add Book/Movie");

        rgType = findViewById(R.id.rg_type);
        etName = findViewById(R.id.et_name);
        etAuthor = findViewById(R.id.et_author);
        spinnerCategory = findViewById(R.id.spinner_category);
        etProcurementDate = findViewById(R.id.et_procurement_date);
        etQuantity = findViewById(R.id.et_quantity);
        etCost = findViewById(R.id.et_cost);
        btnConfirm = findViewById(R.id.btn_confirm);
        btnCancel = findViewById(R.id.btn_cancel);
        btnHome = findViewById(R.id.btn_home);
        btnLogout = findViewById(R.id.btn_logout);
        tvPageError = findViewById(R.id.tv_page_error);
        progressBar = findViewById(R.id.progress_bar);

        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Constants.CATEGORIES);
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(catAdapter);

        etProcurementDate.setText(sdf.format(procCalendar.getTime()));
        etProcurementDate.setOnClickListener(v -> {
            DatePickerDialog dpd = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                procCalendar.set(year, month, dayOfMonth);
                etProcurementDate.setText(sdf.format(procCalendar.getTime()));
            }, procCalendar.get(Calendar.YEAR), procCalendar.get(Calendar.MONTH), procCalendar.get(Calendar.DAY_OF_MONTH));
            dpd.show();
        });

        btnConfirm.setOnClickListener(v -> addBook());
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

    private void addBook() {
        String name = etName.getText().toString().trim();
        String author = etAuthor.getText().toString().trim();
        String qtyStr = etQuantity.getText().toString().trim();
        String costStr = etCost.getText().toString().trim();

        if (name.isEmpty() || author.isEmpty() || qtyStr.isEmpty() || costStr.isEmpty()) {
            tvPageError.setText(R.string.err_all_fields_mandatory);
            tvPageError.setVisibility(View.VISIBLE);
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        int typeIdx = rgType.getCheckedRadioButtonId() == R.id.rb_book ? 0 : 1;
        String typeStr = typeIdx == 0 ? "book" : "movie";
        String typeCode = typeIdx == 0 ? "B" : "M";
        
        int catIdx = spinnerCategory.getSelectedItemPosition();
        String catPrefix = Constants.CATEGORY_PREFIXES[catIdx];
        String category = Constants.CATEGORIES[catIdx];

        String serialNo = catPrefix + "-" + typeCode + "-" + System.currentTimeMillis();
        
        Book book = new Book(serialNo, name, author, category, typeStr, "available", 
                Double.parseDouble(costStr), procCalendar.getTime(), Integer.parseInt(qtyStr));

        firebaseHelper.getDb().collection(Constants.BOOKS).document(serialNo).set(book)
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    startActivity(new Intent(AddBookActivity.this, ConfirmationActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    tvPageError.setText("Error: " + e.getMessage());
                    tvPageError.setVisibility(View.VISIBLE);
                });
    }
}
