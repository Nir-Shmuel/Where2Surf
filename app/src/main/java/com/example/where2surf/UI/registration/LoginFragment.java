package com.example.where2surf.UI.registration;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.Navigation;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.where2surf.MainActivity;
import com.example.where2surf.R;
import com.example.where2surf.model.UserModel;
import com.google.android.material.snackbar.Snackbar;

public class LoginFragment extends Fragment {
    private static final String AUTHENTICATION_FAILED_MESSAGE = "Authentication failed.";
    static final String INVALID_FORM_MESSAGE = "Form not valid. Please try again.";
    private static final String INVALID_EMAIL_MESSAGE ="Email not valid.";
    private static final String INVALID_PASSWORD_MESSAGE = "Password must be minimum 5 length.";

    View view;
    EditText usernameEt;
    EditText pwdEt;
    Button sendBtn;
    ProgressBar progressBar;

    public LoginFragment() {
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
        view = inflater.inflate(R.layout.fragment_login, container, false);
        usernameEt = view.findViewById(R.id.login_username_et);
        pwdEt = view.findViewById(R.id.login_password_et);
        progressBar = view.findViewById(R.id.login_loading);
        sendBtn = view.findViewById(R.id.login_send_btn);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                if (validateForm()) {
                    progressBar.setVisibility(View.VISIBLE);
                    sendBtn.setClickable(false);
                    signIn();
                } else {
                    signInError(INVALID_FORM_MESSAGE);
                }
            }
        });

        return view;
    }

    private void signIn() {
        UserModel.instance.signIn(usernameEt.getText().toString(), pwdEt.getText().toString(), new UserModel.Listener<Boolean>() {
            @Override
            public void onComplete(Boolean data) {
                if (data) {
                    Navigation.findNavController(view).navigateUp();
                    MainActivity activity = (MainActivity) getActivity();
                    if (activity != null)
                        activity.updateUI();
                } else {
                    signInError(AUTHENTICATION_FAILED_MESSAGE);
                }
            }
        });
    }

    private void signInError(String errorMsg) {
        progressBar.setVisibility(View.INVISIBLE);
        Snackbar mySnackbar = Snackbar.make(view, errorMsg, Snackbar.LENGTH_LONG);
        mySnackbar.show();
        sendBtn.setClickable(true);
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

    private boolean validateForm() {
        return checkEmail(usernameEt)
                && checkPassword(pwdEt);
    }

    private boolean checkEmail(EditText emailEt) {
        String email = emailEt.getText().toString();
        if (email.contains("@") && Patterns.EMAIL_ADDRESS.matcher(email).matches() && !email.trim().isEmpty())
            return true;
        emailEt.setError(INVALID_EMAIL_MESSAGE);
        return false;
    }

    private boolean checkPassword(EditText pwdEt) {
        String pwd = pwdEt.getText().toString();
        if (pwd.trim().length() > 5)
            return true;
        pwdEt.setError(INVALID_PASSWORD_MESSAGE);
        return false;
    }
}