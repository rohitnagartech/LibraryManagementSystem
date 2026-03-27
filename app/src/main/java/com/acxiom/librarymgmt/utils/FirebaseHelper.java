package com.acxiom.librarymgmt.utils;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

public class FirebaseHelper {
    private static FirebaseHelper instance;
    private FirebaseFirestore db;

    private FirebaseHelper() {
        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
    }

    public static synchronized FirebaseHelper getInstance() {
        if (instance == null) {
            instance = new FirebaseHelper();
        }
        return instance;
    }

    public FirebaseFirestore getDb() {
        return db;
    }

    public void addDocument(String collection, Map<String, Object> data, OnSuccessListener<Void> successListener, OnFailureListener failureListener) {
        db.collection(collection).document().set(data)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    public void updateDocument(String collection, String docId, Map<String, Object> data, OnSuccessListener<Void> successListener, OnFailureListener failureListener) {
        db.collection(collection).document(docId).update(data)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    public void getCollection(String collection, OnSuccessListener<QuerySnapshot> successListener, OnFailureListener failureListener) {
        db.collection(collection).get()
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    public void queryCollection(String collection, String field, Object value, OnSuccessListener<QuerySnapshot> successListener, OnFailureListener failureListener) {
        db.collection(collection).whereEqualTo(field, value).get()
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }
}
