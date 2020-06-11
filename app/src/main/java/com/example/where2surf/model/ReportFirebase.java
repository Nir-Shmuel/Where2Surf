package com.example.where2surf.model;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.LinkedList;
import java.util.List;

public class ReportFirebase {
    private static final String REPORT_COLLECTION = "Reports";

    static void addReport(Report report, final ReportModel.Listener<Boolean> listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(REPORT_COLLECTION).document(report.getId()).set(report).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (listener != null) {
                    listener.onComplete(task.isSuccessful());
                }
            }
        });
    }

    static void getAllReports(Spot spot, final ReportModel.Listener<List<Report>> listener) {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(REPORT_COLLECTION).whereEqualTo("spotName", spot.getName()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<Report> reports = null;
                if (task.isSuccessful()) {
                    reports = new LinkedList<>();
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        Report report = doc.toObject(Report.class);
                        reports.add(report);
                    }
                }
                listener.onComplete(reports);
            }
        });
    }
}
