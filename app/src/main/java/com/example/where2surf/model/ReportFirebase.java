package com.example.where2surf.model;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
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
        db.collection(REPORT_COLLECTION).add(jsonFromReport(report)).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (listener != null) {
                    listener.onComplete(task.isSuccessful());
                }
            }
        });
    }

    static void getAllReportsSince(Spot spot, long since, final ReportModel.Listener<List<Report>> listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Timestamp timestamp = new Timestamp(since, 0);
        db.collection(REPORT_COLLECTION).whereEqualTo("spotName", spot.getName()).whereGreaterThanOrEqualTo("lastUpdated", timestamp).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<Report> reports = null;
                if (task.isSuccessful()) {
                    reports = new LinkedList<>();
                    if (task.getResult() != null)
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

    static void getAllReports(Spot spot, final ReportModel.Listener<List<Report>> listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(REPORT_COLLECTION).whereEqualTo("spotName", spot.getName()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<Report> reports = null;
                if (task.isSuccessful()) {
                    reports = new LinkedList<>();
                    if (task.getResult() != null)
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
        json.put("reporterName", report.getReporterName());
        json.put("spotName", report.getSpotName());
        json.put("date", FieldValue.serverTimestamp());
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
        String reporterName = (String) json.get("reporterName");
        String spotName = (String) json.get("spotName");
        if(reporterName==null || spotName==null) {
            throw new RuntimeException("Invalid reporter and spot names");
        }
        report.setReporterName(reporterName);
        report.setSpotName(spotName);
        Timestamp dateTimestamp = (Timestamp) json.get("date");
        if (dateTimestamp != null) report.setDate(dateTimestamp.toDate().getTime());
        report.setImageUrl((String) json.get("imageUrl"));
        report.setWavesHeight(((Long) json.get("wavesHeight")).intValue());
        report.setWindSpeed(((Long) json.get("windSpeed")).intValue());
        report.setNumOfSurfers(((Long) json.get("numOfSurfers")).intValue());
        report.setContaminated((boolean) json.get("isContaminated"));
        report.setReliabilityRating(((Long) json.get("reliabilityRating")).intValue());
        Timestamp lastUpdatedTimestamp = (Timestamp) json.get("lastUpdated");
        if (lastUpdatedTimestamp != null)
            report.setLastUpdated(lastUpdatedTimestamp.toDate().getTime());
        return report;
    }
}
