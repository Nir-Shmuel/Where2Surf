package com.example.where2surf.model;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SpotFirebase {
    private final static String SPOT_COLLECTION = "Spots";

    static void addSpot(Spot spot, final SpotModel.Listener<Boolean> listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(SPOT_COLLECTION).document(spot.getName()).set(jsonFromSpot(spot)).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (listener != null) {
                    listener.onComplete(task.isSuccessful());
                }
            }
        });
    }

    static void getAllSpots(final SpotModel.Listener<List<Spot>> listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(SPOT_COLLECTION).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<Spot> spots = null;
                if (task.isSuccessful()) {
                    spots = new LinkedList<>();
                    if (task.getResult() != null)
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            Map<String, Object> json = doc.getData();
                            Spot spot = spotFromJson(json);
                            spots.add(spot);
                        }
                }
                listener.onComplete(spots);
            }
        });
    }

    static void getAllSpotsSince(long since, final SpotModel.Listener<List<Spot>> listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Timestamp timestamp = new Timestamp(new Date(since));
        db.collection(SPOT_COLLECTION).whereGreaterThanOrEqualTo("lastUpdated", since).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<Spot> spots = null;
                if (task.isSuccessful()) {
                    spots = new LinkedList<>();
                    if (task.getResult() != null)
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            Map<String, Object> json = doc.getData();
                            Spot spot = spotFromJson(json);
                            spots.add(spot);
                        }
                }
                listener.onComplete(spots);
            }
        });
    }

    private static Map<String, Object> jsonFromSpot(Spot spot) {
        HashMap<String, Object> json = new HashMap<>();
        json.put("name", spot.getName());
        json.put("location", spot.getLocation());
        json.put("isWindProtected", spot.isWindProtected());
        json.put("lastUpdated", FieldValue.serverTimestamp());
        return json;
    }

    private static Spot spotFromJson(Map<String, Object> json) {
        Spot spot = new Spot();
        String name = (String)json.get("name");
        if ((name != null)) {
            spot.setName(name);
        } else {
            spot.setName("");
        }
        spot.setLocation((String) json.get("location"));
        spot.setWindProtected((boolean) json.get("isWindProtected"));
        Timestamp timestamp = (Timestamp) json.get("lastUpdated");
        if (timestamp != null) spot.setLastUpdated(timestamp.toDate().getTime());
        return spot;
    }
}
