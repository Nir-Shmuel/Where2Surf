package com.example.where2surf.UI.userProfile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.where2surf.R;
import com.example.where2surf.model.StoreModel;
import com.example.where2surf.model.User;
import com.example.where2surf.model.UserModel;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserProfileFragment extends Fragment {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final String REPORT_FAILED_ERROR = "Failed to report. Please try again.";
    private static final String SAVE_IMAGE_FAILED_ERROR = "Failed to save image. Please try again.";
    private static final String INVALID_FORM_ERROR = "Form not valid. Please fill all fields.";

    View view;
    ImageView userImage;
    Button takePhotoBtn;
    TextView emailTv;
    EditText emailEt;
    TextView firstNameTv;
    EditText firstNameEt;
    TextView lastNameTv;
    EditText lastNameEt;
    Button editProfileBtn;
    ProgressBar progressBar;
    Bitmap imageBitmap;

    public UserProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        setHasOptionsMenu(true);
        userImage = view.findViewById(R.id.profile_user_image);
        takePhotoBtn = view.findViewById(R.id.profile_take_photo_btn);
        emailTv = view.findViewById(R.id.profile_email_tv);
        emailEt = view.findViewById(R.id.profile_email_et);
        firstNameTv = view.findViewById(R.id.profile_first_name_tv);
        firstNameEt = view.findViewById(R.id.profile_first_name_et);
        lastNameTv = view.findViewById(R.id.profile_last_name_tv);
        lastNameEt = view.findViewById(R.id.profile_last_name_et);
        editProfileBtn = view.findViewById(R.id.profile_edit_btn);
        progressBar = view.findViewById(R.id.profile_loading);

        takePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });

        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                setEditable(false);
                editProfile();
            }
        });

        setBtnVisibility(false);
        updateVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        emailEt.setEnabled(false);
        setEditable(false);
        UserModel.instance.getCurrentUserDetails(new UserModel.Listener<User>() {
            @Override
            public void onComplete(User data) {
                updateUserDetails(data);
            }
        });

        return view;
    }

    private void editProfile() {
        if (validateForm()) {
            String firstName = firstNameEt.getText().toString();
            String lastName = lastNameEt.getText().toString();
            UserModel.instance.updateUserDetails(firstName, lastName, new UserModel.Listener<Boolean>() {
                @Override
                public void onComplete(Boolean data) {
                    if (data) {
                        saveImage(emailEt.getText().toString());
                    } else {
                        messageUser(INVALID_FORM_ERROR);
                        setEditable(true);
                    }
                }
            });

        } else {
            messageUser(INVALID_FORM_ERROR);
            setEditable(true);
        }

    }


    private void saveImage(final String userName) {
        if (imageBitmap != null) {
            StoreModel.uploadUserImage(imageBitmap, userName, new StoreModel.Listener() {
                @Override
                public void onSuccess(String url) {
                    UserModel.instance.setUserImageUrl(url, new UserModel.Listener<Boolean>() {
                        @Override
                        public void onComplete(Boolean data) {
                            if (data) {
                                progressBar.setVisibility(View.INVISIBLE);
                                setBtnVisibility(false);
                            } else {
                                messageUser(SAVE_IMAGE_FAILED_ERROR);
                                setEditable(true);
                            }
                        }
                    });
                }

                @Override
                public void onFail() {
                    messageUser(REPORT_FAILED_ERROR);
                    setEditable(true);
                }
            });
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            setBtnVisibility(false);
        }
    }

    private void messageUser(String msg) {
        progressBar.setVisibility(View.INVISIBLE);
        Snackbar mySnackbar = Snackbar.make(view, msg, Snackbar.LENGTH_LONG);
        mySnackbar.show();
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
                    userImage.setImageBitmap(imageBitmap);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean validateForm() {
        return checkName(firstNameEt)
                && checkName(lastNameEt);
    }

    private boolean checkName(EditText nameEt) {
        String name = nameEt.getText().toString();
        if (!name.trim().isEmpty() && Character.isUpperCase(name.charAt(0)))
            return true;
        nameEt.setError("Name must start with capital letter.");
        return false;
    }

    private void setEditable(boolean b) {
        firstNameEt.setEnabled(b);
        lastNameEt.setEnabled(b);
        takePhotoBtn.setClickable(b);
        editProfileBtn.setClickable(b);
    }

    private void setBtnVisibility(boolean b) {
        takePhotoBtn.setVisibility((b ? View.VISIBLE : View.INVISIBLE));
        editProfileBtn.setVisibility((b ? View.VISIBLE : View.INVISIBLE));
    }

    void updateVisibility(int visibility) {
        userImage.setVisibility(visibility);
        emailTv.setVisibility(visibility);
        emailEt.setVisibility(visibility);
        firstNameTv.setVisibility(visibility);
        firstNameEt.setVisibility(visibility);
        lastNameTv.setVisibility(visibility);
        lastNameEt.setVisibility(visibility);
    }

    void updateUserDetails(User user) {
        if (user != null) {
            String imgUrl = user.getImageUrl();
            if (imgUrl != null && !imgUrl.equals("")) {
                Picasso.get().load(imgUrl).placeholder(R.drawable.avatar).into(userImage);
            } else {
                userImage.setImageResource(R.drawable.avatar);
            }
            emailEt.setText(user.getEmail());
            firstNameEt.setText(user.getFirstName());
            lastNameEt.setText(user.getLastName());
            updateVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.user_profile_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_user_profile_edit:
                setEditable(true);
                setBtnVisibility(true);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}