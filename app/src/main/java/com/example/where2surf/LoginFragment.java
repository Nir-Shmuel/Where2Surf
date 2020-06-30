package com.example.where2surf;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.where2surf.model.UserModel;
import com.google.android.material.snackbar.Snackbar;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {
    private static final String AUTHENTICATION_FAILED_MESSAGE = "Authentication failed.";
    private static final String INVALID_FORM_MESSAGE = "Form not valid. Please try again.";

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
                    UserModel.instance.signIn(usernameEt.getText().toString(), pwdEt.getText().toString(), new UserModel.Listener<Boolean>() {
                        @Override
                        public void onComplete(Boolean data) {
                            if (data) {
                                Navigation.findNavController(view).navigateUp();
                                MainActivity activity = (MainActivity) getActivity();
                                if (activity != null)
                                    activity.updateUI();
                            } else {
                                signInFailed(AUTHENTICATION_FAILED_MESSAGE);
                            }
                        }
                    });
                }else{
                    signInFailed(INVALID_FORM_MESSAGE);
                }
            }
        });

        return view;
    }

    private void signInFailed(String errorMsg) {
        progressBar.setVisibility(View.INVISIBLE);
        Snackbar mySnackbar = Snackbar.make(view, errorMsg, Snackbar.LENGTH_LONG);
        mySnackbar.show();
        sendBtn.setClickable(true);
    }

    public void hideKeyboard() {
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private boolean validateForm() {
        return checkEmail(usernameEt.getText().toString())
                && checkPassword(pwdEt.getText().toString());
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

    private boolean checkPassword(String pwd) {
        return pwd != null && !pwd.equals("");
    }
}