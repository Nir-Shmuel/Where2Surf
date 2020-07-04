package com.example.where2surf.UI.registration;

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

import com.example.where2surf.MainActivity;
import com.example.where2surf.R;
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
    static final String REGISTRATION_FAILED_MESSAGE = "Registration failed. Please try again.";
    static final String INVALID_FORM_MESSAGE = "Form not valid. Please try again.";
    static final String INVALID_EMAIL_MESSAGE = "Email not valid.";
    static final String INVALID_NAME_MESSAGE = "Name must start with capital letter.";
    static final String INVALID_PASSWORD_MESSAGE = "Password must be minimum 5 length.";
    static final String INVALID_VAL_PASSWORD_MESSAGE = "Validation password is not equal to password.";

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
                if (validateForm()) {
                    progressBar.setVisibility(View.VISIBLE);
                    takePhotoBtn.setClickable(false);
                    sendBtn.setClickable(false);
                    signUp();
                } else {
                    registrationFailed(INVALID_FORM_MESSAGE);
                }
            }
        });

        return view;
    }

    public void hideKeyboard() {
        if (view != null) {
            FragmentActivity activity = getActivity();
            InputMethodManager inputManager;
            if (activity != null) {
                inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (inputManager != null)
                    inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
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
                    registrationFailed(REGISTRATION_FAILED_MESSAGE);
                }
            });
        } else
            saveUser("");
    }

    private void saveUser(final String imageUrl) {
        final String email = emailEt.getText().toString();
        final String pwd = pwdEt.getText().toString();
        UserModel.instance.signUp(email, pwd, new UserModel.Listener<String>() {
            @Override
            public void onComplete(String data) {
                if (data != null) {
                    String email = emailEt.getText().toString();
                    User user = new User(data, email);
                    user.setFirstName(firstNameEt.getText().toString());
                    user.setLastName(lastNameEt.getText().toString());
                    user.setImageUrl(imageUrl);
                    UserModel.instance.addUser(user, null);
                    MainActivity activity = (MainActivity) getActivity();
                    if (activity != null)
                        activity.updateUI();
                    Navigation.findNavController(view).navigateUp();
                } else {
                    registrationFailed(INVALID_FORM_MESSAGE);
                }
            }
        });
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
        return checkEmail(emailEt)
                && checkName(firstNameEt)
                && checkName(lastNameEt)
                && checkPassword(pwdEt, valPedEt);
    }

    private boolean checkEmail(EditText emailEt) {
        String email = emailEt.getText().toString();
        if (email.contains("@") && Patterns.EMAIL_ADDRESS.matcher(email).matches() && !email.trim().isEmpty())
            return true;
        emailEt.setError(INVALID_EMAIL_MESSAGE);
        return false;
    }

    private boolean checkName(EditText nameEt) {
        String name = nameEt.getText().toString();
        if (!name.trim().isEmpty() && Character.isUpperCase(name.charAt(0)))
            return true;
        nameEt.setError(INVALID_NAME_MESSAGE);
        return false;
    }

    private boolean checkPassword(EditText pwdEt, EditText valPwdEt) {
        String pwd = pwdEt.getText().toString();
        String valPwd = valPwdEt.getText().toString();

        if (pwd.length() < 5) {
            pwdEt.setError(INVALID_PASSWORD_MESSAGE);
            return false;
        } else if (!pwd.equals(valPwd)) {
            valPwdEt.setError(INVALID_VAL_PASSWORD_MESSAGE);
            return false;
        }
        return true;
    }

}