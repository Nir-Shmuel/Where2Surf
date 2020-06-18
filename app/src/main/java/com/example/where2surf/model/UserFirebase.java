package com.example.where2surf.model;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UserFirebase {
    private final static String USER_COLLECTION = "Users";

    public static void addUser(User user, final UserModel.Listener<Boolean> listener) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            String id = firebaseUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection(USER_COLLECTION).document(id).set(jsonFromUser(user)).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (listener != null) {
                        listener.onComplete(task.isSuccessful());
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }
    }

    private static Map<String, Object> jsonFromUser(User user) {
        HashMap<String, Object> json = new HashMap<>();
        json.put("email", user.getEmail());
        json.put("firstName", user.getFirstName());
        json.put("lastName", user.getLastName());
        json.put("imageUrl", user.getImageUrl());
        json.put("lastUpdated", FieldValue.serverTimestamp());
        return json;
    }

    private static User userFromJson(Map<String, Object> json) {
        User user = new User();
        user.setEmail((String) json.get("email"));
        user.setFirstName((String) json.get("firstName"));
        user.setLastName((String) json.get("lastName"));
        user.setImageUrl((String) json.get("imageUrl"));
        Timestamp timestamp = (Timestamp) json.get("lastUpdated");
        if (timestamp != null) user.setLastUpdated(timestamp.getSeconds());
        return user;
    }

    public static void getCurrentUser(final UserModel.Listener<User> listener) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            String id = firebaseUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection(USER_COLLECTION).document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    Map<String, Object> data = task.getResult().getData();
                    if (data != null){
                        listener.onComplete(userFromJson(task.getResult().getData()));
                    }
                    else{
                        listener.onComplete(null);
                    }
                }
            });
        }
    }
}
