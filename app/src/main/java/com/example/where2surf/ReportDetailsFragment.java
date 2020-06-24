package com.example.where2surf;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;

import com.example.where2surf.model.Report;
import com.example.where2surf.model.ReportModel;
import com.example.where2surf.model.StoreModel;
import com.example.where2surf.model.UserModel;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReportDetailsFragment extends Fragment {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static String REPORT_FAILED_ERROR = "Failed to report. Please try again.";
    private static String SAVE_IMAGE_FAILED_ERROR = "Failed to save image. Please try again.";
    private static final String INVALID_FORM_ERROR = "Form not valid. Please fill all fields.";

    Report report;
    View view;
    Button takePhotoBtn;
    ImageView imageView;
    EditText wavesHeightEt;
    EditText windSpeedEt;
    EditText surfersNumEt;
    CheckBox spotContaminationCb;
    RatingBar reliabilityRating;
    Button editBtn;
    ProgressBar progressBar;
    Bitmap imageBitmap;

    public ReportDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_report_details, container, false);
        takePhotoBtn = view.findViewById(R.id.report_details_take_photo_btn);
        imageView = view.findViewById(R.id.report_details_image);
        wavesHeightEt = view.findViewById(R.id.report_details_waves_height_et);
        windSpeedEt = view.findViewById(R.id.report_details_wind_speed_et);
        surfersNumEt = view.findViewById(R.id.report_details_surfers_num_et);
        spotContaminationCb = view.findViewById(R.id.report_details_is_contaminated_cb);
        reliabilityRating = view.findViewById(R.id.report_details_rating_bar);
        editBtn = view.findViewById(R.id.report_details_send_btn);
        progressBar = view.findViewById(R.id.report_details_progress);


        takePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                setEditable(false);
                editReport();
            }
        });

        if (getArguments() != null)
            report = ReportDetailsFragmentArgs.fromBundle(getArguments()).getReport();
        setBtnVisibility(false);
        setEditable(false);
        updateView(report);

        return view;
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
                                progressBar.setVisibility(View.INVISIBLE);
                                setBtnVisibility(false);
                            } else {
                                reportFailed(SAVE_IMAGE_FAILED_ERROR);
                            }
                        }
                    });
                }

                @Override
                public void onFail() {
                    reportFailed(REPORT_FAILED_ERROR);
                }
            });
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            setBtnVisibility(false);
        }
    }

    private void editReport() {
        if (validateForm()) {
            report.setWavesHeight(Integer.parseInt(wavesHeightEt.getText().toString()));
            report.setWindSpeed(Integer.parseInt(windSpeedEt.getText().toString()));
            report.setNumOfSurfers(Integer.parseInt(surfersNumEt.getText().toString()));
            report.setContaminated(spotContaminationCb.isChecked());
            ReportModel.instance.updateReport(report, new ReportModel.Listener<Boolean>() {
                @Override
                public void onComplete(Boolean data) {
                    if (data) {
                        saveImage(report.getId());
                    } else {
                        reportFailed(REPORT_FAILED_ERROR);
                    }
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
        setEditable(true);
    }

    private void setEditable(boolean b) {
        wavesHeightEt.setEnabled(b);
        windSpeedEt.setEnabled(b);
        surfersNumEt.setEnabled(b);
        takePhotoBtn.setClickable(b);
        editBtn.setClickable(b);
    }

    private void setBtnVisibility(boolean b) {
        takePhotoBtn.setVisibility((b ? View.VISIBLE : View.INVISIBLE));
        editBtn.setVisibility((b ? View.VISIBLE : View.INVISIBLE));
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
                    imageView.setImageBitmap(imageBitmap);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean validateForm() {
        return isNotEmpty(wavesHeightEt.getText().toString()) &&
                isNotEmpty(windSpeedEt.getText().toString()) &&
                isNotEmpty(surfersNumEt.getText().toString());
    }

    private boolean isNotEmpty(String field) {
        return field != null && !field.trim().isEmpty();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.report_details_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_report_details_edit:
                setEditable(true);
                setBtnVisibility(true);
                return true;

            case R.id.menu_report_details_delete:
                deleteReport();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteReport() {
        ReportModel.instance.deleteReport(report, new ReportModel.Listener<Boolean>() {
            @Override
            public void onComplete(Boolean data) {
                if (data) {
                    Navigation.findNavController(view).navigateUp();
                }
            }
        });
    }

    void updateView(Report report) {
        String imgUrl = report.getImageUrl();
        if (imgUrl != null && !imgUrl.equals("")) {
            Picasso.get().load(imgUrl).placeholder(R.drawable.avatar).into(imageView);
        } else {
            imageView.setImageResource(R.drawable.avatar);
        }
        wavesHeightEt.setText(String.format("%s", report.getWavesHeight()));
        windSpeedEt.setText(String.format("%s", report.getWindSpeed()));
        surfersNumEt.setText(String.format("%s", report.getNumOfSurfers()));
        spotContaminationCb.setChecked(report.isContaminated());
        reliabilityRating.setRating(report.getReliabilityRating());
//        setSignedInView();
        setReportOwnerView();
    }

    private void setSignedInView() {

    }

    private void setReportOwnerView() {
        boolean isReportOwner = report.getReporterId().equals(UserModel.instance.getCurrentUserId());
        setHasOptionsMenu(isReportOwner);
    }
}