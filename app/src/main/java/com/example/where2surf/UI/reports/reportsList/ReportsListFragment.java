package com.example.where2surf.UI.reports.reportsList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.where2surf.R;
import com.example.where2surf.model.Report;
import com.example.where2surf.model.UserModel;

import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

public abstract class ReportsListFragment extends Fragment {
    View view;
    LinearLayoutManager layoutManager;
    protected List<Report> reportsData = new LinkedList<>();
    protected RecyclerView reportsList;
    protected ReportsListAdapter adapter;
    protected LiveData<List<Report>> liveData;

    public interface Delegate {
        void OnItemSelected(Report report);
    }

    Delegate parent;

    interface OnItemClickListener {
        void onClick(int position);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_reports_list, container, false);
        setHasOptionsMenu(UserModel.instance.isLoggedIn());

        reportsList = view.findViewById(R.id.reports_list_list);
        reportsList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        reportsList.setLayoutManager(layoutManager);
        adapter = new ReportsListAdapter();
        reportsList.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onClick(int position) {
                Report report = reportsData.get(position);
                parent.OnItemSelected(report);
            }
        });

        reportsList.addItemDecoration(new DividerItemDecoration(reportsList.getContext(), layoutManager.getOrientation()));

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        parent = null;
    }

    static class ReportRowViewHolder extends RecyclerView.ViewHolder {
        TextView spotNameTv;
        TextView wavesHeightTv;
        TextView windSpeedTv;
        TextView dateTv;
        RatingBar reportRating;

        public ReportRowViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onClick(position);
                        }
                    }
                }
            });
            spotNameTv = itemView.findViewById(R.id.row_spot_name_tv);
            wavesHeightTv = itemView.findViewById(R.id.row_report_wave_height_tv);
            windSpeedTv = itemView.findViewById(R.id.row_report_wind_speed_tv);
            dateTv = itemView.findViewById(R.id.row_report_date_tv);
            reportRating = itemView.findViewById(R.id.row_report_rating_bar);
        }

        void bind(Report report) {
            spotNameTv.setText(String.format("%s", report.getSpotName()));
            wavesHeightTv.setText(String.format("Waves: %s cm", report.getWavesHeight()));
            windSpeedTv.setText(String.format("Wind: %s knots", report.getWindSpeed()));
            dateTv.setText(String.format("%s", SimpleDateFormat.getDateInstance().format(report.getDate())));
            reportRating.setRating(report.getReliabilityRating());
        }
    }

    class ReportsListAdapter extends RecyclerView.Adapter<ReportRowViewHolder> {
        private OnItemClickListener listener;

        void setOnItemClickListener(OnItemClickListener listener) {
            this.listener = listener;
        }

        @NonNull
        @Override
        public ReportRowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.list_row_report, parent, false);
            return new ReportRowViewHolder(view, listener);
        }

        @Override
        public void onBindViewHolder(@NonNull ReportRowViewHolder holder, int position) {
            Report report = reportsData.get(position);
            holder.bind(report);
        }

        @Override
        public int getItemCount() {
            return reportsData.size();
        }
    }

}
