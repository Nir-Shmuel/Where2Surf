package com.example.where2surf.model;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.LinkedList;
import java.util.List;

public class SpotFirebase {
    private final static String SPOT_COLLECTION = "Spots";

    static void addSpot(Spot spot, final SpotModel.Listener<Boolean> listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(SPOT_COLLECTION).document(spot.getId()).set(spot).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (listener != null) {
                    listener.onComplete(task.isSuccessful());
                }
            }
        });
    }

    static void getAllSpots(final SpotModel.Listener<List<Spot>> listener) {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(SPOT_COLLECTION).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<Spot> spots = null;
                if (task.isSuccessful()) {
                    spots = new LinkedList<>();
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        Spot spot = doc.toObject(Spot.class);
                        spots.add(spot);
                    }
                }
                listener.onComplete(spots);
            }
        });
    }
}
