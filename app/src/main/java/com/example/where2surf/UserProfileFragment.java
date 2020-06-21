package com.example.where2surf;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.where2surf.model.User;
import com.example.where2surf.model.UserModel;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserProfileFragment extends Fragment {
    ImageView userImage;
    TextView firstNameTv;
    TextView lastNameTv;
    ProgressBar progressBar;

    public UserProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        userImage = view.findViewById(R.id.proflie_user_image);
        firstNameTv = view.findViewById(R.id.profile_first_name_tv);
        lastNameTv = view.findViewById(R.id.profile_last_name_tv);
        progressBar = view.findViewById(R.id.profile_loading);
        hideView();
        UserModel.instance.getCurrentUserDetails(new UserModel.Listener<User>() {
            @Override
            public void onComplete(User data) {
                updateView(data);
            }
        });

        return view;
    }
    void hideView(){
        userImage.setVisibility(View.INVISIBLE);
        firstNameTv.setVisibility(View.INVISIBLE);
        lastNameTv.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }
    void showView(){
        userImage.setVisibility(View.VISIBLE);
        firstNameTv.setVisibility(View.VISIBLE);
        lastNameTv.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }
    void updateView(User user) {
        if (user != null) {
            String imgUrl=user.getImageUrl();
            if(imgUrl != null && !imgUrl.equals("")) {
                Picasso.get().load(imgUrl).placeholder(R.drawable.avatar).into(userImage);
            }else{
                userImage.setImageResource(R.drawable.avatar);
            }
            firstNameTv.setText(String.format("First Name: %s", user.getFirstName()));
            lastNameTv.setText(String.format("Last Name: %s", user.getLastName()));
            showView();

        }
    }
}