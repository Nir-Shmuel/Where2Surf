package com.example.where2surf;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.where2surf.model.Report;
import com.example.where2surf.model.ReportModel;
import com.example.where2surf.model.UserModel;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReportDetailsFragment extends Fragment {

    Report report;
    ImageView imageView;
    TextView wavesHeightTv;
    TextView windSpeedTv;
    TextView surfersNumTv;
    CheckedTextView spotContaminationCtv;
    RatingBar reliabilityRating;
    Button editBtn;
    Button deleteBtn;

    public ReportDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_report_details, container, false);
        imageView = view.findViewById(R.id.report_details_image);
        wavesHeightTv = view.findViewById(R.id.report_details_waves_height_tv);
        windSpeedTv = view.findViewById(R.id.report_details_wind_speed_tv);
        surfersNumTv = view.findViewById(R.id.report_details_surfers_num_tv);
        spotContaminationCtv = view.findViewById(R.id.report_details_is_contaminated_ctv);
        reliabilityRating = view.findViewById(R.id.report_details_rating_bar);

        editBtn = view.findViewById(R.id.report_details_edit_btn);
        deleteBtn = view.findViewById(R.id.report_details_delete_btn);
        setSignedInView(UserModel.instance.isSignedIn());
        if (getArguments() != null)
            report = ReportDetailsFragmentArgs.fromBundle(getArguments()).getReport();
        updateView(report);

        return view;
    }

    void updateView(Report report) {
        String imgUrl = report.getImageUrl();
        if (imgUrl != null && !imgUrl.equals("")) {
            Picasso.get().load(imgUrl).placeholder(R.drawable.avatar).into(imageView);
        } else {
            imageView.setImageResource(R.drawable.avatar);
        }
        wavesHeightTv.setText(String.format("Waves height: %s cm", report.getWavesHeight()));
        windSpeedTv.setText(String.format("Wind speed: %s knots", report.getWindSpeed()));
        surfersNumTv.setText(String.format("Approximately %s surfers", report.getNumOfSurfers()));
        spotContaminationCtv.setChecked(report.isContaminated());
        reliabilityRating.setRating(report.getReliabilityRating());
        setSignedInView(UserModel.instance.isSignedIn());
    }

    private void setSignedInView(boolean signedIn) {
        if (signedIn) {
            editBtn.setVisibility(View.VISIBLE);
            deleteBtn.setVisibility(View.VISIBLE);
        } else {
            editBtn.setVisibility(View.INVISIBLE);
            deleteBtn.setVisibility(View.INVISIBLE);
        }
    }
}