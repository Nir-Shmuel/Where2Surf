package com.example.where2surf;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.where2surf.model.Report;
import com.example.where2surf.model.ReportModel;
import com.example.where2surf.model.Spot;
import com.example.where2surf.model.StoreModel;
import com.example.where2surf.model.User;
import com.example.where2surf.model.UserModel;
import com.google.android.material.snackbar.Snackbar;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddReportFragment extends Fragment {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static String REPORT_FAILED_ERROR = "Failed to report. Please try again.";
    private static final String INVALID_FORM_ERROR = "Form not valid. Please fill all fields.";

    View view;
    Spot spot;
    ImageView image;
    EditText wavesHeightEt;
    EditText windSpeedEt;
    EditText numOfSurfersEt;
    CheckBox spotContaminationCtv;
    ProgressBar progressBar;
    Button takePhotoBtn;
    Button sendBtn;
    Bitmap imageBitmap;

    public AddReportFragment() {
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
        view = inflater.inflate(R.layout.fragment_add_report, container, false);
        if (getArguments() != null)
            spot = AddReportFragmentArgs.fromBundle(getArguments()).getSpot();
        image = view.findViewById(R.id.add_report_image);
        wavesHeightEt = view.findViewById(R.id.add_report_waves_height_et);
        windSpeedEt = view.findViewById(R.id.add_report_wind_speed_et);
        numOfSurfersEt = view.findViewById(R.id.add_report_num_of_surfers_et);
        spotContaminationCtv = view.findViewById(R.id.add_report_is_contaminated_ctv);
        progressBar = view.findViewById(R.id.add_report_progress);
        progressBar.setVisibility(View.INVISIBLE);
        takePhotoBtn = view.findViewById(R.id.add_report_take_photo_btn);
        takePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });

        sendBtn = view.findViewById(R.id.add_report_send_btn);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                takePhotoBtn.setClickable(false);
                sendBtn.setClickable(false);
                report();
            }
        });
        return view;
    }

    private boolean validateForm() {
        return isNotEmpty(wavesHeightEt.getText().toString()) &&
                isNotEmpty(windSpeedEt.getText().toString()) &&
                isNotEmpty(numOfSurfersEt.getText().toString());
    }

    private boolean isNotEmpty(String field) {
        return field != null && !field.trim().isEmpty();
    }

    private void report() {
        if (imageBitmap != null) {

            StoreModel.uploadReportImage(imageBitmap, spot.getName(), new StoreModel.Listener() {
                @Override
                public void onSuccess(String url) {
                    saveReport(url);
                }

                @Override
                public void onFail() {
                    reportFailed(REPORT_FAILED_ERROR);
                }
            });
        } else {
            saveReport("");
        }

    }

    private void saveReport(final String imageUrl) {
        final MainActivity activity = (MainActivity) getActivity();
        if (activity != null && validateForm()) {
            UserModel.instance.getCurrentUserDetails(new UserModel.Listener<User>() {
                @Override
                public void onComplete(User data) {
                    Report report = new Report();
                    report.setReporterName(data.getFirstName() + " " + data.getLastName());
                    report.setSpotName(spot.getName());
                    report.setWavesHeight(Integer.parseInt(wavesHeightEt.getText().toString()));
                    report.setWindSpeed(Integer.parseInt(windSpeedEt.getText().toString()));
                    report.setNumOfSurfers(Integer.parseInt(numOfSurfersEt.getText().toString()));
                    report.setContaminated(spotContaminationCtv.isChecked());
                    report.setImageUrl(imageUrl);
                    ReportModel.instance.addReport(report, new ReportModel.Listener<Boolean>() {
                        @Override
                        public void onComplete(Boolean data) {
                            if (data) {
                                Navigation.findNavController(view).navigateUp();
                            } else {
                                reportFailed(REPORT_FAILED_ERROR);
                            }
                        }
                    });
                }
            });

        } else {
            reportFailed(INVALID_FORM_ERROR);
        }

    }

    private void reportFailed(String errorMsg) {
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
}