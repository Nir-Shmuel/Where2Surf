package com.example.where2surf.UI.reports.addReport;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
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

import com.example.where2surf.R;
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
    private static final String REPORT_FAILED_MESSAGE = "Failed to report. Please try again.";
    private static final String SAVE_IMAGE_FAILED_MESSAGE = "Failed to save image. Please try again by editing your report.";
    private static final String INVALID_FORM_MESSAGE = "Form not valid. Please fill all fields.";
    private static final String EMPTY_FIELD_MESSAGE = "Field must not be empty.";

    AddReportViewModel viewModel;
    LiveData<Spot> spotLiveData;
    View view;
    Spot spot;
    ImageView image;
    EditText wavesHeightEt;
    EditText windSpeedEt;
    EditText surfersNumEt;
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
        spotLiveData = viewModel.getSpotLiveData(spot.getName());
        spotLiveData.observe(getViewLifecycleOwner(), new Observer<Spot>() {
            @Override
            public void onChanged(Spot _spot) {
                spot = _spot;
            }
        });
        image = view.findViewById(R.id.add_report_image);
        wavesHeightEt = view.findViewById(R.id.add_report_waves_height_et);
        windSpeedEt = view.findViewById(R.id.add_report_wind_speed_et);
        surfersNumEt = view.findViewById(R.id.add_report_num_of_surfers_et);
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
                if (validateForm()) {
                    progressBar.setVisibility(View.VISIBLE);
                    takePhotoBtn.setClickable(false);
                    sendBtn.setClickable(false);
                    saveReport();
                } else {
                    reportFailedMessage(INVALID_FORM_MESSAGE);
                }
            }
        });
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        viewModel = new ViewModelProvider(this).get(AddReportViewModel.class);
    }

    private boolean validateForm() {
        return isNotEmpty(wavesHeightEt) &&
                isNotEmpty(windSpeedEt) &&
                isNotEmpty(surfersNumEt);
    }

    private boolean isNotEmpty(EditText fieldEt) {
        String text = fieldEt.getText().toString();
        if (!text.trim().isEmpty())
            return true;
        fieldEt.setError(EMPTY_FIELD_MESSAGE);
        return false;
    }

    private void saveImage(final String reportId) {
        if (imageBitmap != null) {
            StoreModel.uploadReportImage(imageBitmap, reportId, new StoreModel.Listener() {
                @Override
                public void onSuccess(String url) {
                    ReportModel.instance.setReportImageUrl(reportId, url, new ReportModel.Listener<Boolean>() {
                        @Override
                        public void onComplete(Boolean data) {
                            if (data) {
                                ReportModel.instance.refreshSpotReportsList(spot, null);
                                Navigation.findNavController(view).navigateUp();
                            } else {
                                reportFailedMessage(SAVE_IMAGE_FAILED_MESSAGE);
                            }
                        }
                    });
                }

                @Override
                public void onFail() {
                    reportFailedMessage(REPORT_FAILED_MESSAGE);
                }
            });
        } else {
            ReportModel.instance.refreshSpotReportsList(spot, null);
            Navigation.findNavController(view).navigateUp();
        }
    }

    private void saveReport() {
        UserModel.instance.getCurrentUserDetails(new UserModel.Listener<User>() {
            @Override
            public void onComplete(User data) {
                Report report = new Report();
                report.setReporterName(data.getFirstName() + " " + data.getLastName());
                report.setSpotName(spot.getName());
                report.setWavesHeight(Integer.parseInt(wavesHeightEt.getText().toString()));
                report.setWindSpeed(Integer.parseInt(windSpeedEt.getText().toString()));
                report.setNumOfSurfers(Integer.parseInt(surfersNumEt.getText().toString()));
                report.setContaminated(spotContaminationCtv.isChecked());
                report.setImageUrl("");
                ReportModel.instance.addReport(report, new ReportModel.Listener<Report>() {
                    @Override
                    public void onComplete(Report data) {
                        if (data != null) {
                            saveImage(data.getId());
                        } else {
                            reportFailedMessage(REPORT_FAILED_MESSAGE);
                        }
                    }
                });
            }
        });
    }

    private void reportFailedMessage(String errorMsg) {
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