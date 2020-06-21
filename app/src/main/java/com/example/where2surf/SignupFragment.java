package com.example.where2surf;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.where2surf.model.StoreModel;
import com.example.where2surf.model.User;
import com.example.where2surf.model.UserModel;
import com.google.android.material.snackbar.Snackbar;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignupFragment extends Fragment {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int RESULT_SUCCESS = 0;
    static final String REGISTRATION_FAILED_ERROR = "Registration failed. Please try again.";
    static final String INVALID_FORM_ERROR = "Form not valid. Please try again.";

    View view;
    ImageView image;
    Bitmap imageBitmap;
    EditText emailEt;
    EditText firstNameEt;
    EditText lastNameEt;
    EditText pwdEt;
    EditText valPedEt;
    Button takePhotoBtn;
    Button sendBtn;
    ProgressBar progressBar;

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
        view = inflater.inflate(R.layout.fragment_signup, container, false);

        image = view.findViewById(R.id.signup_image);
        emailEt = view.findViewById(R.id.signup_email_et);
        firstNameEt = view.findViewById(R.id.signup_first_name_et);
        lastNameEt = view.findViewById(R.id.signup_last_name_et);
        pwdEt = view.findViewById(R.id.signup_password_et);
        valPedEt = view.findViewById(R.id.signup_password_validation_et);
        progressBar = view.findViewById(R.id.signup_progress);
        progressBar.setVisibility(View.INVISIBLE);
        takePhotoBtn = view.findViewById(R.id.signup_take_photo_btn);
        takePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });

        sendBtn = view.findViewById(R.id.signup_send_btn);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                progressBar.setVisibility(View.VISIBLE);
                takePhotoBtn.setClickable(false);
                sendBtn.setClickable(false);
                signUp();
            }
        });

        return view;
    }

    public void hideKeyboard() {
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    void signUp() {
        if (imageBitmap != null) {
            StoreModel.uploadUserImage(imageBitmap, emailEt.getText().toString(), new StoreModel.Listener() {
                @Override
                public void onSuccess(final String url) {
                    saveUser(url);
                }

                @Override
                public void onFail() {
                    registrationFailed(REGISTRATION_FAILED_ERROR);
                }
            });
        } else
            saveUser("");
    }

    private void saveUser(final String imageUrl) {
        final MainActivity activity = (MainActivity) getActivity();
        if (activity != null && validateForm()) {
            final String email = emailEt.getText().toString();
            final String pwd = pwdEt.getText().toString();
            activity.createAccount(email, pwd, new UserModel.Listener<Boolean>() {
                @Override
                public void onComplete(Boolean data) {
                    if (data) {
                        User user = new User();
                        user.setEmail(emailEt.getText().toString());
                        user.setFirstName(firstNameEt.getText().toString());
                        user.setLastName(lastNameEt.getText().toString());
                        user.setImageUrl(imageUrl);
                        UserModel.instance.addUser(user, null);
                        Navigation.findNavController(view).navigateUp();
                    } else {
                        registrationFailed(INVALID_FORM_ERROR);
                    }
                }
            });
        } else {
            registrationFailed(INVALID_FORM_ERROR);
        }
    }

    private void registrationFailed(String errorMsg) {
        progressBar.setVisibility(View.INVISIBLE);
        Snackbar mySnackbar = Snackbar.make(view, errorMsg, Snackbar.LENGTH_LONG);
        mySnackbar.show();
        takePhotoBtn.setClickable(true);
        sendBtn.setClickable(true);
    }

    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (getActivity() != null && intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (data != null) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    imageBitmap = (Bitmap) extras.get("data");
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
        return !name.trim().isEmpty();
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