package com.example.where2surf.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
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

    private static User userFromJson(String id, Map<String, Object> json) {
        String email = (String) json.get("email");
        User user = new User(id, email != null ? email : "");
        user.setFirstName((String) json.get("firstName"));
        user.setLastName((String) json.get("lastName"));
        user.setImageUrl((String) json.get("imageUrl"));
        Timestamp timestamp = (Timestamp) json.get("lastUpdated");
        if (timestamp != null) user.setLastUpdated(timestamp.toDate().getTime());
        return user;
    }

    public static void getCurrentUserDetails(final UserModel.Listener<User> listener) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            final String id = firebaseUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection(USER_COLLECTION).document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot taskResult = task.getResult();
                    if (taskResult != null) {
                        Map<String, Object> data = taskResult.getData();
                        if (data != null) {
                            listener.onComplete(userFromJson(id, data));
                        } else {
                            listener.onComplete(null);
                        }
                    }
                }
            });
        }
    }

    public static boolean isSignedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    public static void signIn(String email, String password, final UserModel.Listener<Boolean> listener) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("TAG", "signInWithEmail:success");
                        } else {
                            Log.w("TAG", "signInWithEmail:failure", task.getException());
                        }
                        listener.onComplete(task.isSuccessful());
                    }
                });
    }

    public static void signOut() {
        FirebaseAuth.getInstance().signOut();
    }

    public static void singUp(String email, String password, final UserModel.Listener<String> listener) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        String id = getCurrentUserId();
                        listener.onComplete(id);
                    }
                });
    }

    public static String getCurrentUserId() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null)
            return firebaseUser.getUid();
        return null;
    }

    public static void updateUserDetails(String firstName, String lastName, final UserModel.Listener<Boolean> listener) {
        String userId = getCurrentUserId();
        if (userId != null) {
            Map<String, Object> fields = new HashMap<>();
            fields.put("firstName", firstName);
            fields.put("lastName", lastName);
            fields.put("lastUpdated", FieldValue.serverTimestamp());
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection(USER_COLLECTION).document(userId).update(fields)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            listener.onComplete(task.isSuccessful());
                        }
                    });
        } else {
            listener.onComplete(false);
        }
    }

    public static void setUserImageUrl(String url, final UserModel.Listener<Boolean> listener) {
        String userId = getCurrentUserId();
        if (userId != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection(USER_COLLECTION).document(userId).update("imageUrl", url)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            listener.onComplete(task.isSuccessful());
                        }
                    });
        } else {
            listener.onComplete(false);
        }
    }
}
