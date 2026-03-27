package com.acxiom.librarymgmt.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.acxiom.librarymgmt.R;
import com.acxiom.librarymgmt.models.Book;
import com.acxiom.librarymgmt.models.Membership;
import com.acxiom.librarymgmt.models.User;
import com.acxiom.librarymgmt.utils.Constants;
import com.acxiom.librarymgmt.utils.FirebaseHelper;

import java.util.Date;

public class SeedDataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seed_data);

        Button btnSeedData = findViewById(R.id.btn_seed_data);
        btnSeedData.setOnClickListener(v -> seedData());
    }

    private void seedData() {
        FirebaseHelper helper = FirebaseHelper.getInstance();

        // Seed Users
        User admin = new User("1", "Administrator", "adm", "adm", true, true);
        User user = new User("2", "Library User", "user", "user", false, true);

        helper.getDb().collection(Constants.USERS).document(admin.getUsername()).set(admin);
        helper.getDb().collection(Constants.USERS).document(user.getUsername()).set(user);

        // Seed Books
        Book b1 = new Book("SC-B-000001", "Introduction to Physics", "H.C. Verma", "Science", "book", "available", 450.0, new Date(), 2);
        Book b2 = new Book("FC-B-000001", "Harry Potter", "J.K. Rowling", "Fiction", "book", "available", 350.0, new Date(), 1);
        Book b3 = new Book("SC-M-000001", "Cosmos", "Carl Sagan", "Science", "movie", "available", 200.0, new Date(), 1);

        helper.getDb().collection(Constants.BOOKS).document(b1.getSerialNo()).set(b1);
        helper.getDb().collection(Constants.BOOKS).document(b2.getSerialNo()).set(b2);
        helper.getDb().collection(Constants.BOOKS).document(b3.getSerialNo()).set(b3);

        // Seed Membership
        Membership m1 = new Membership("MEM001", "Rahul", "Sharma", "9876543210", "123 Main St", "123456789012", "6months", "active", new Date(), new Date(), 0.0);
        helper.getDb().collection(Constants.MEMBERSHIPS).document(m1.getMembershipId()).set(m1);

        Toast.makeText(this, "Seed data added successfully", Toast.LENGTH_SHORT).show();
    }
}
