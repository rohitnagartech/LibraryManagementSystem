package com.acxiom.librarymgmt.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.acxiom.librarymgmt.R;
import com.acxiom.librarymgmt.models.Book;
import com.acxiom.librarymgmt.models.Membership;
import com.acxiom.librarymgmt.utils.Constants;
import com.acxiom.librarymgmt.utils.FirebaseHelper;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SeedDataActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private int taskCount = 0;
    private int completedTasks = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seed_data);
        
        // Force Light Mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        
        progressBar = findViewById(R.id.progressBar);
        Button btnSeedData = findViewById(R.id.btn_seed_data);
        btnSeedData.setOnClickListener(v -> seedData());
    }

    private void seedData() {
        FirebaseHelper helper = FirebaseHelper.getInstance();
        progressBar.setVisibility(View.VISIBLE);
        completedTasks = 0;
        taskCount = 0;

// ─────────────────────────────────────────────────────────────
//  SECTION 1 — SEED USERS (Admin + Regular Users)
// ─────────────────────────────────────────────────────────────

        taskCount++;
        Map<String, Object> adminUser = new HashMap<>();
        adminUser.put("username", "adm");
        adminUser.put("password", "adm");
        adminUser.put("name", "Administrator");
        adminUser.put("isAdmin", true);
        adminUser.put("isActive", true);
        helper.getDb().collection(Constants.USERS).document("adm").set(adminUser).addOnCompleteListener(task -> checkCompletion());

        taskCount++;
        Map<String, Object> regularUser = new HashMap<>();
        regularUser.put("username", "user");
        regularUser.put("password", "user");
        regularUser.put("name", "Library Staff");
        regularUser.put("isAdmin", false);
        regularUser.put("isActive", true);
        helper.getDb().collection(Constants.USERS).document("user").set(regularUser).addOnCompleteListener(task -> checkCompletion());

        taskCount++;
        Map<String, Object> staffUser = new HashMap<>();
        staffUser.put("username", "staff01");
        staffUser.put("password", "staff01");
        staffUser.put("name", "Priya Nair");
        staffUser.put("isAdmin", false);
        staffUser.put("isActive", true);
        helper.getDb().collection(Constants.USERS).document("staff01").set(staffUser).addOnCompleteListener(task -> checkCompletion());

// ─────────────────────────────────────────────────────────────
//  SECTION 2 — SEED BOOKS
// ─────────────────────────────────────────────────────────────

        Book[] books = {
            // Science
            new Book("SC-B-000001", "Introduction to Physics", "H.C. Verma", "Science", "book", "available", 450.0, new Date(), 2),
            new Book("SC-B-000002", "Concepts of Chemistry", "O.P. Tandon", "Science", "book", "available", 380.0, new Date(), 3),
            new Book("SC-B-000003", "Biology NCERT Class 12", "NCERT", "Science", "book", "issued", 220.0, new Date(), 2),
            new Book("SC-B-000004", "A Brief History of Time", "Stephen Hawking", "Science", "book", "available", 499.0, new Date(), 1),
            // Economics
            new Book("EC-B-000001", "Principles of Economics", "N. Gregory Mankiw", "Economics", "book", "available", 520.0, new Date(), 2),
            new Book("EC-B-000002", "Indian Economy", "Ramesh Singh", "Economics", "book", "available", 310.0, new Date(), 4),
            new Book("EC-B-000003", "The Wealth of Nations", "Adam Smith", "Economics", "book", "available", 399.0, new Date(), 1),
            new Book("EC-B-000004", "Freakonomics", "Steven D. Levitt", "Economics", "book", "issued", 275.0, new Date(), 2),
            // Fiction
            new Book("FC-B-000001", "Harry Potter and the Sorcerer's Stone", "J.K. Rowling", "Fiction", "book", "available", 350.0, new Date(), 3),
            new Book("FC-B-000002", "The Alchemist", "Paulo Coelho", "Fiction", "book", "available", 299.0, new Date(), 4),
            new Book("FC-B-000003", "To Kill a Mockingbird", "Harper Lee", "Fiction", "book", "available", 320.0, new Date(), 2),
            new Book("FC-B-000004", "The Great Gatsby", "F. Scott Fitzgerald", "Fiction", "book", "issued", 249.0, new Date(), 1),
            // Children
            new Book("CH-B-000001", "The Jungle Book", "Rudyard Kipling", "Children", "book", "available", 180.0, new Date(), 5),
            new Book("CH-B-000002", "Charlie and the Chocolate Factory", "Roald Dahl", "Children", "book", "available", 210.0, new Date(), 3),
            new Book("CH-B-000003", "Panchatantra Stories", "Vishnu Sharma", "Children", "book", "available", 150.0, new Date(), 4),
            new Book("CH-B-000004", "Diary of a Wimpy Kid", "Jeff Kinney", "Children", "book", "issued", 195.0, new Date(), 2),
            // Personal Development
            new Book("PD-B-000001", "The 7 Habits of Highly Effective People", "Stephen R. Covey", "Personal Development", "book", "available", 499.0, new Date(), 3),
            new Book("PD-B-000002", "Atomic Habits", "James Clear", "Personal Development", "book", "available", 399.0, new Date(), 4),
            new Book("PD-B-000003", "Think and Grow Rich", "Napoleon Hill", "Personal Development", "book", "available", 299.0, new Date(), 2),
            new Book("PD-B-000004", "Rich Dad Poor Dad", "Robert Kiyosaki", "Personal Development", "book", "issued", 349.0, new Date(), 2)
        };

        for (Book book : books) {
            taskCount++;
            helper.getDb().collection(Constants.BOOKS).document(book.getSerialNo()).set(book).addOnCompleteListener(task -> checkCompletion());
        }

// ─────────────────────────────────────────────────────────────
//  SECTION 3 — SEED MOVIES
// ─────────────────────────────────────────────────────────────

        Book[] movies = {
            // Science
            new Book("SC-M-000001", "Cosmos: A Spacetime Odyssey", "Carl Sagan", "Science", "movie", "available", 200.0, new Date(), 1),
            new Book("SC-M-000002", "Interstellar", "Christopher Nolan", "Science", "movie", "available", 350.0, new Date(), 2),
            new Book("SC-M-000003", "The Theory of Everything", "James Marsh", "Science", "movie", "issued", 280.0, new Date(), 1),
            new Book("SC-M-000004", "Hidden Figures", "Theodore Melfi", "Science", "movie", "available", 310.0, new Date(), 1),
            // Economics
            new Book("EC-M-000001", "The Big Short", "Adam McKay", "Economics", "movie", "available", 300.0, new Date(), 2),
            new Book("EC-M-000002", "Inside Job", "Charles Ferguson", "Economics", "movie", "available", 250.0, new Date(), 1),
            new Book("EC-M-000003", "Too Big to Fail", "Curtis Hanson", "Economics", "movie", "available", 275.0, new Date(), 1),
            new Book("EC-M-000004", "Wall Street", "Oliver Stone", "Economics", "movie", "issued", 220.0, new Date(), 1),
            // Fiction
            new Book("FC-M-000001", "Harry Potter and the Chamber of Secrets", "Chris Columbus", "Fiction", "movie", "available", 300.0, new Date(), 3),
            new Book("FC-M-000002", "The Lord of the Rings: Fellowship", "Peter Jackson", "Fiction", "movie", "available", 350.0, new Date(), 2),
            new Book("FC-M-000003", "Life of Pi", "Ang Lee", "Fiction", "movie", "available", 260.0, new Date(), 1),
            new Book("FC-M-000004", "The Great Gatsby", "Baz Luhrmann", "Fiction", "movie", "issued", 280.0, new Date(), 1),
            // Children
            new Book("CH-M-000001", "The Jungle Book", "Jon Favreau", "Children", "movie", "available", 220.0, new Date(), 3),
            new Book("CH-M-000002", "Lion King", "Jon Favreau", "Children", "movie", "available", 250.0, new Date(), 2),
            new Book("CH-M-000003", "Finding Nemo", "Andrew Stanton", "Children", "movie", "available", 200.0, new Date(), 2),
            new Book("CH-M-000004", "Taare Zameen Par", "Aamir Khan", "Children", "movie", "issued", 190.0, new Date(), 1),
            // Personal Development
            new Book("PD-M-000001", "The Pursuit of Happyness", "Gabriele Muccino", "Personal Development", "movie", "available", 270.0, new Date(), 2),
            new Book("PD-M-000002", "3 Idiots", "Rajkumar Hirani", "Personal Development", "movie", "available", 230.0, new Date(), 3),
            new Book("PD-M-000003", "Dead Poets Society", "Peter Weir", "Personal Development", "movie", "available", 260.0, new Date(), 1),
            new Book("PD-M-000004", "Chak De India", "Shimit Amin", "Personal Development", "movie", "issued", 210.0, new Date(), 1)
        };

        for (Book movie : movies) {
            taskCount++;
            helper.getDb().collection(Constants.BOOKS).document(movie.getSerialNo()).set(movie).addOnCompleteListener(task -> checkCompletion());
        }

// ─────────────────────────────────────────────────────────────
//  SECTION 4 — SEED MEMBERSHIPS
// ─────────────────────────────────────────────────────────────

        Calendar cal = Calendar.getInstance();
        Date start = cal.getTime();
        cal.add(Calendar.MONTH, 6);
        Date end = cal.getTime();

        Membership[] memberships = {
            new Membership("MEM001", "Rahul", "Sharma", "9876543210", "123 Main St, Delhi", "123456789012", "6months", "active", start, end, 0.0),
            new Membership("MEM002", "Sneha", "Gupta", "9123456789", "456 Park Ave, Mumbai", "987654321098", "1year", "active", start, end, 0.0),
            new Membership("MEM003", "Amit", "Verma", "8887776665", "Sector 15, Gurgaon", "555544443333", "2years", "active", start, end, 50.0)
        };

        for (Membership m : memberships) {
            taskCount++;
            helper.getDb().collection(Constants.MEMBERSHIPS).document(m.getMembershipId()).set(m).addOnCompleteListener(task -> checkCompletion());
        }
    }

    private void checkCompletion() {
        completedTasks++;
        if (completedTasks >= taskCount) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Complete seed data added successfully!", Toast.LENGTH_LONG).show();
        }
    }
}
