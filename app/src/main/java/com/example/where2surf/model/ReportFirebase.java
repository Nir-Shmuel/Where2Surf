package com.example.where2surf.model;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ReportFirebase {
    private static final String REPORT_COLLECTION = "Reports";

    static void addReport(Report report, final ReportModel.Listener<Boolean> listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(REPORT_COLLECTION).document(report.getId()).set(jsonFromReport(report)).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (listener != null) {
                    listener.onComplete(task.isSuccessful());
                }
            }
        });
    }

    static void getAllReports(Spot spot, long since, final ReportModel.Listener<List<Report>> listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Timestamp timestamp = new Timestamp(since, 0);
        db.collection(REPORT_COLLECTION).whereGreaterThanOrEqualTo("lastUpdated", timestamp).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<Report> reports = null;
                if (task.isSuccessful()) {
                    reports = new LinkedList<>();
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        Map<String, Object> json = doc.getData();
                        Report report = reportFromJson(json);
                        reports.add(report);
                    }
                }
                listener.onComplete(reports);
            }
        });
    }

    private static Map<String, Object> jsonFromReport(Report report) {
        HashMap<String, Object> json = new HashMap<>();
        json.put("id", report.getId());
        json.put("reporterName", report.getReporterName());
        json.put("spotName", report.getSpotName());
        json.put("date", report.getDate());
        json.put("imageUrl", report.getImageUrl());
        json.put("wavesHeight", report.getWavesHeight());
        json.put("windSpeed", report.getWindSpeed());
        json.put("numOfSurfers", report.getNumOfSurfers());
        json.put("isContaminated", report.isContaminated());
        json.put("reliabilityRating", report.getReliabilityRating());
        json.put("lastUpdated", FieldValue.serverTimestamp());
        return json;
    }

    private static Report reportFromJson(Map<String, Object> json) {
        Report report = new Report();
        report.setId((String) json.get("id"));
        report.setReporterName((String) json.get("reporterName"));
        report.setSpotName((String) json.get("spotName"));
        report.setDate((long) json.get("date"));
        report.setImageUrl((String) json.get("imageUrl"));
        report.setWavesHeight(((Long) json.get("wavesHeight")).intValue());
        report.setWindSpeed(((Long) json.get("windSpeed")).intValue());
        report.setNumOfSurfers(((Long) json.get("numOfSurfers")).intValue());
        report.setContaminated((boolean) json.get("isContaminated"));
        report.setReliabilityRating(((Long) json.get("reliabilityRating")).intValue());
        Timestamp timestamp = (Timestamp) json.get("lastUpdated");
        if (timestamp != null) report.setLastUpdated(timestamp.getSeconds());
        return report;
    }
}
