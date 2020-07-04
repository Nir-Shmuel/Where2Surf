package com.example.where2surf.UI.reports.reportDetails;

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

import com.example.where2surf.R;
import com.example.where2surf.model.Report;
import com.example.where2surf.model.ReportModel;
import com.example.where2surf.model.StoreModel;
import com.example.where2surf.model.UserModel;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import static android.app.Activity.RESULT_OK;

public class ReportDetailsFragment extends Fragment {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final String REPORT_FAILED_MESSAGE = "Failed to report. Please try again.";
    private static final String SAVE_IMAGE_FAILED_MESSAGE = "Failed to save image. Please try again.";
    private static final String RATE_REPORT_SUCCEEDED_MESSAGE = "Thank you for rating the report.";
    private static final String RATE_REPORT_FAILED_MESSAGE = "Failed to rate report. Please try again.";
    private static final String INVALID_FORM_MESSAGE = "Form not valid. Please fill all fields.";
    private static final String EMPTY_FIELD_MESSAGE = "Field must not be empty.";

    ReportDetailsViewModel viewModel;
    LiveData<Report> reportLiveData;
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
    Button rateBtn;
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
        rateBtn = view.findViewById(R.id.report_details_rate_report_btn);


        rateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateReportReliability();
            }
        });

        takePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateForm()) {
                    progressBar.setVisibility(View.VISIBLE);
                    setEditable(false);
                    editReport();
                } else {
                    messageUser(INVALID_FORM_MESSAGE);
                    setEditable(true);
                }
            }
        });
        if (getArguments() != null)
            report = ReportDetailsFragmentArgs.fromBundle(getArguments()).getReport();
        reportLiveData = viewModel.getLiveData(report.getId());
        reportLiveData.observe(getViewLifecycleOwner(), new Observer<Report>() {
            @Override
            public void onChanged(Report _report) {
                report = _report;
            }
        });
        setBtnVisibility(false);
        setEditable(false);
        updateView(report);

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        viewModel = new ViewModelProvider(this).get(ReportDetailsViewModel.class);
    }

    private void updateReportReliability() {
        rateBtn.setClickable(false);
        progressBar.setVisibility(View.VISIBLE);
        ReportModel.instance.updateReportRating(report, reliabilityRating.getRating(), new ReportModel.Listener<Boolean>() {
            @Override
            public void onComplete(Boolean data) {
                progressBar.setVisibility(View.INVISIBLE);
                if (data) {
                    updateView(report);
                    reliabilityRating.setIsIndicator(true);
                }
                messageUser(data ? RATE_REPORT_SUCCEEDED_MESSAGE : RATE_REPORT_FAILED_MESSAGE);
            }
        });
    }

    private void editReport() {
        final String reportId = report.getId();
        StoreModel.uploadReportImage(imageBitmap, reportId, new StoreModel.Listener() {
            @Override
            public void onSuccess(String url) {
                setChanges(url);
            }

            @Override
            public void onFail() {
                messageUser(SAVE_IMAGE_FAILED_MESSAGE);
                setEditable(true);
            }
        });


    }

    private void setChanges(String url) {
        report.setWavesHeight(Integer.parseInt(wavesHeightEt.getText().toString()));
        report.setWindSpeed(Integer.parseInt(windSpeedEt.getText().toString()));
        report.setNumOfSurfers(Integer.parseInt(surfersNumEt.getText().toString()));
        report.setContaminated(spotContaminationCb.isChecked());
        report.setImageUrl(url);
        ReportModel.instance.updateReport(report, new ReportModel.Listener<Boolean>() {
            @Override
            public void onComplete(Boolean data) {
                if (data) {
                    progressBar.setVisibility(View.INVISIBLE);
                    setBtnVisibility(false);
                } else {
                    messageUser(REPORT_FAILED_MESSAGE);
                    setEditable(true);
                }
            }
        });
    }

    private void messageUser(String msg) {
        progressBar.setVisibility(View.INVISIBLE);
        Snackbar mySnackbar = Snackbar.make(view, msg, Snackbar.LENGTH_LONG);
        mySnackbar.show();
    }

    private void setEditable(boolean b) {
        wavesHeightEt.setEnabled(b);
        windSpeedEt.setEnabled(b);
        surfersNumEt.setEnabled(b);
        takePhotoBtn.setClickable(b);
        spotContaminationCb.setClickable(b);
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
        setSignedInView();
    }

    private void setSignedInView() {
        boolean isSignedIn = UserModel.instance.isLoggedIn();
        boolean isReportOwner = report.getReporterId().equals(UserModel.instance.getCurrentUserId());
        setHasOptionsMenu(isReportOwner);
        rateBtn.setVisibility(isSignedIn && !isReportOwner ? View.VISIBLE : View.INVISIBLE);
        reliabilityRating.setIsIndicator(!isSignedIn || isReportOwner);
    }

}