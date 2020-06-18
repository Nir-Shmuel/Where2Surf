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

import com.example.where2surf.model.UserModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class SigninFragment extends Fragment {
    View view;
    EditText usernameEt;
    EditText pwdEt;

    public SigninFragment() {
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
        view = inflater.inflate(R.layout.fragment_signin, container, false);
        usernameEt = view.findViewById(R.id.signin_username_et);
        pwdEt = view.findViewById(R.id.signin_password_et);
        Button sendBtn = view.findViewById(R.id.signin_send_btn);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                MainActivity activity = (MainActivity) getActivity();
                if (activity != null && validateForm()) {
                    activity.signIn(usernameEt.getText().toString(), pwdEt.getText().toString(), new UserModel.Listener<Boolean>() {
                        @Override
                        public void onComplete(Boolean data) {
                            if(data) {
                                Navigation.findNavController(view).navigateUp();
                            }
                        }
                    });
                }
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