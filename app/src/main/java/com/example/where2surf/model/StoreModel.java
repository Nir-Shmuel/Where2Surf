package com.example.where2surf.model;

import android.graphics.Bitmap;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class StoreModel {
    static final String USER_IMAGES = "users_images";
    static final String REPORT_IMAGES = "reports_images";

    public interface Listener {
        void onSuccess(String url);

        void onFail();
    }

    public static void uploadImage(Bitmap imageBmp, String name, final Listener listener) {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        final StorageReference imageReference = firebaseStorage.getReference().child(USER_IMAGES).child(name);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = imageReference.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.onFail();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        listener.onSuccess(uri.toString());
                    }
                });
            }
        });
    }
}
