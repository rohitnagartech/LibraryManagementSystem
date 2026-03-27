package com.acxiom.librarymgmt.utils;

public class Constants {
    // Firestore Collections
    public static final String USERS = "users";
    public static final String BOOKS = "books";
    public static final String MEMBERSHIPS = "memberships";
    public static final String ISSUES = "issues";
    public static final String ISSUE_REQUESTS = "issueRequests";

    // Book Categories
    public static final String[] CATEGORIES = {"Science", "Economics", "Fiction", "Children", "Personal Development"};
    public static final String[] CATEGORY_PREFIXES = {"SC", "EC", "FC", "CH", "PD"};

    // Fine configuration
    public static final double FINE_PER_DAY = 5.0;

    // Membership Durations
    public static final String SIX_MONTHS = "6months";
    public static final String ONE_YEAR = "1year";
    public static final String TWO_YEARS = "2years";
}
