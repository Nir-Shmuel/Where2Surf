package com.example.where2surf.UI;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.where2surf.UI.registration.LoginFragmentDirections;
import com.example.where2surf.R;
import com.example.where2surf.UI.registration.SignupFragmentDirections;
import com.example.where2surf.UI.spots.SpotsListFragmentDirections;
import com.example.where2surf.model.UserModel;

public class AboutFragment extends Fragment {

    Button spotsListBtn;
    Button loginBtn;
    Button signupBtn;

    public AboutFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_about, container, false);
        setHasOptionsMenu(true);

        spotsListBtn = view.findViewById(R.id.about_spots_list_btn);
        loginBtn = view.findViewById(R.id.about_login_btn);
        signupBtn = view.findViewById(R.id.about_signup_btn);

        spotsListBtn.setOnClickListener(Navigation.createNavigateOnClickListener(SpotsListFragmentDirections.actionGlobalSpotsListFragment()));
        loginBtn.setOnClickListener(Navigation.createNavigateOnClickListener(LoginFragmentDirections.actionGlobalLoginFragment()));
        signupBtn.setOnClickListener(Navigation.createNavigateOnClickListener(SignupFragmentDirections.actionGlobalSignupFragment()));
        updateUI();
        return view;
    }

    private void updateUI() {
        boolean isLoggedIn = UserModel.instance.isLoggedIn();
        loginBtn.setVisibility(isLoggedIn ? View.INVISIBLE : View.VISIBLE);
        signupBtn.setVisibility(isLoggedIn ? View.INVISIBLE : View.VISIBLE);
    }
}

