package com.example.where2surf;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignupFragment extends Fragment {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int RESULT_SUCCESS = 0;

    ImageView image;
    EditText emailEt;
    EditText firstNameEt;
    EditText lastNameEt;
    EditText pwdEt;
    EditText valPedEt;
    Button takePhotoBtn;
    Button sendBtn;

    public SignupFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        image = view.findViewById(R.id.signup_image);
        emailEt = view.findViewById(R.id.signup_email_et);
        firstNameEt = view.findViewById(R.id.signup_first_name_et);
        lastNameEt = view.findViewById(R.id.signup_last_name_et);
        pwdEt = view.findViewById(R.id.signup_password_et);
        valPedEt = view.findViewById(R.id.signup_password_validation_et);
        takePhotoBtn = view.findViewById(R.id.signup_take_photo_btn);
        sendBtn = view.findViewById(R.id.signup_send_btn);

        takePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity activity = (MainActivity) getActivity();
                if (activity != null && validateForm())
                    activity.createAccount(emailEt.getText().toString(), pwdEt.getText().toString());
            }
        });
        return view;
    }

    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (data != null) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    image.setImageBitmap(imageBitmap);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean validateForm() {
        return checkEmail(emailEt.getText().toString())
                && checkName(firstNameEt.getText().toString())
                && checkName(lastNameEt.getText().toString())
                && checkPassword(pwdEt.getText().toString(), valPedEt.getText().toString());
    }

    private boolean checkEmail(String email) {
        if (email == null) {
            return false;
        }
        if (email.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(email).matches();
        } else {
            return !email.trim().isEmpty();
        }
    }

    private boolean checkName(String name) {
        if (name == null)
            return false;
        return name.trim().isEmpty();
    }

    private boolean checkPassword(String pwd, String valPwd) {
        if (pwd == null || valPwd == null) {
            return false;
        }
        if (pwd.length() < 5)
            return false;
        return pwd.equals(valPwd);
    }

}