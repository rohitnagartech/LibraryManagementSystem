package com.acxiom.librarymgmt.maintenance;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.acxiom.librarymgmt.R;
import com.acxiom.librarymgmt.activities.AdminHomeActivity;
import com.acxiom.librarymgmt.activities.ConfirmationActivity;
import com.acxiom.librarymgmt.activities.LoginActivity;
import com.acxiom.librarymgmt.models.Book;
import com.acxiom.librarymgmt.utils.Constants;
import com.acxiom.librarymgmt.utils.FirebaseHelper;
import com.acxiom.librarymgmt.utils.SessionManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class UpdateBookActivity extends AppCompatActivity {

    private RadioGroup rgType;
    private AutoCompleteTextView actvName;
    private TextInputEditText etSerialNo;
    private Spinner spinnerStatus;
    private EditText etDate;
    private Button btnConfirm, btnCancel, btnHome, btnLogout;
    private TextView tvPageError;
    private ProgressBar progressBar;
    private SessionManager session;
    private FirebaseHelper firebaseHelper;
    private Calendar calendar = Calendar.getInstance();
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private String[] statuses = {"available", "issued", "damaged", "lost"};
    private List<Book> bookList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_book);

        session = new SessionManager(this);
        if (!session.isAdmin()) {
            Toast.makeText(this, R.string.err_access_denied, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        firebaseHelper = FirebaseHelper.getInstance();

        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Update Book/Movie");

        rgType = findViewById(R.id.rg_type);
        actvName = findViewById(R.id.actv_name);
        etSerialNo = findViewById(R.id.et_serial_no);
        spinnerStatus = findViewById(R.id.spinner_status);
        etDate = findViewById(R.id.et_date);
        btnConfirm = findViewById(R.id.btn_confirm);
        btnCancel = findViewById(R.id.btn_cancel);
        btnHome = findViewById(R.id.btn_home);
        btnLogout = findViewById(R.id.btn_logout);
        tvPageError = findViewById(R.id.tv_page_error);
        progressBar = findViewById(R.id.progress_bar);

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statuses);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);

        loadBooks();

        rgType.setOnCheckedChangeListener((group, checkedId) -> loadBooks());

        actvName.setOnItemClickListener((parent, view, position, id) -> {
            String selectedName = (String) parent.getItemAtPosition(position);
            for (Book b : bookList) {
                if (b.getName().equals(selectedName)) {
                    etSerialNo.setText(b.getSerialNo());
                    for (int i = 0; i < statuses.length; i++) {
                        if (statuses[i].equals(b.getStatus())) {
                            spinnerStatus.setSelection(i);
                            break;
                        }
                    }
                    if (b.getProcurementDate() != null) {
                        etDate.setText(sdf.format(b.getProcurementDate()));
                        calendar.setTime(b.getProcurementDate());
                    }
                    break;
                }
            }
        });

        etDate.setOnClickListener(v -> {
            DatePickerDialog dpd = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                calendar.set(year, month, dayOfMonth);
                etDate.setText(sdf.format(calendar.getTime()));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            dpd.show();
        });

        btnConfirm.setOnClickListener(v -> updateBook());
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

    private void loadBooks() {
        String type = rgType.getCheckedRadioButtonId() == R.id.rb_book ? "book" : "movie";
        progressBar.setVisibility(View.VISIBLE);
        firebaseHelper.getDb().collection(Constants.BOOKS)
                .whereEqualTo("type", type)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    progressBar.setVisibility(View.GONE);
                    bookList.clear();
                    List<String> names = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Book b = doc.toObject(Book.class);
                        bookList.add(b);
                        names.add(b.getName());
                    }
                    ArrayAdapter<String> nameAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, names);
                    actvName.setAdapter(nameAdapter);
                })
                .addOnFailureListener(e -> progressBar.setVisibility(View.GONE));
    }

    private void updateBook() {
        String serialNo = etSerialNo.getText().toString().trim();
        if (serialNo.isEmpty()) {
            tvPageError.setText(R.string.err_serial_no_required);
            tvPageError.setVisibility(View.VISIBLE);
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", spinnerStatus.getSelectedItem().toString());
        updates.put("procurementDate", calendar.getTime());

        firebaseHelper.getDb().collection(Constants.BOOKS).document(serialNo).update(updates)
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    startActivity(new Intent(UpdateBookActivity.this, ConfirmationActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    tvPageError.setText("Error: " + e.getMessage());
                    tvPageError.setVisibility(View.VISIBLE);
                });
    }
}
