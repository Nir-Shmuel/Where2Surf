package com.example.where2surf;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.where2surf.model.ReportModel;
import com.example.where2surf.model.Spot;
import com.example.where2surf.model.Report;
import com.example.where2surf.model.SpotModel;

import java.util.LinkedList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class SpotReportsListFragment extends Fragment {
    private RecyclerView reportsList;
    private ReportsListAdapter adapter;
    private List<Report> reportsData = new LinkedList<>();
    private Spot reportedSpot;
    private SpotReportsListViewModel viewModel;
    private LiveData<List<Report>> liveData;

    interface Delegate {
        void OnItemSelected(Report report);
    }

    Delegate parent;

    public SpotReportsListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_spot_reports_list, container, false);
        reportsList = view.findViewById(R.id.spot_reports_list_list);
        reportsList.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
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

        reportedSpot = SpotReportsListFragmentArgs.fromBundle(getArguments()).getSpot();
        liveData = viewModel.getLiveData(reportedSpot);
        liveData.observe(getViewLifecycleOwner(), new Observer<List<Report>>() {
            @Override
            public void onChanged(List<Report> reports) {
                reportsData = reports;
                adapter.notifyDataSetChanged();
            }
        });

        reportsList.addItemDecoration(new DividerItemDecoration(reportsList.getContext(), layoutManager.getOrientation()));
        final SwipeRefreshLayout swipeRefresh = view.findViewById(R.id.spot_reports_list_swipe_refresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.refresh(reportedSpot, new ReportModel.CompleteListener() {
                    @Override
                    public void onComplete() {
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Delegate) {
            parent = (Delegate) getActivity();
        } else {
            throw new RuntimeException("Parent activity must implement Delegate interface");
        }

        viewModel = new ViewModelProvider(this).get(SpotReportsListViewModel.class);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        parent = null;
    }

    interface OnItemClickListener {
        void onClick(int position);
    }

    static class ReportRowViewHolder extends RecyclerView.ViewHolder {
        TextView reporterNameTv;
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
            reporterNameTv = itemView.findViewById(R.id.row_reporter_name_tv);
            wavesHeightTv = itemView.findViewById(R.id.row_report_wave_height_tv);
            windSpeedTv = itemView.findViewById(R.id.row_report_wind_speed_tv);
            dateTv = itemView.findViewById(R.id.row_report_date_tv);
            reportRating = itemView.findViewById(R.id.row_report_rating_bar);
        }

        void bind(Report report) {
            reporterNameTv.setText(String.format("Reported by: %s", report.getReporterName()));
            wavesHeightTv.setText(String.format("Waves: %s meters", report.getWavesHeight()));
            windSpeedTv.setText(String.format("Wind: %s knots", report.getWindSpeed()));
            dateTv.setText(String.format("%s", report.getDate()));
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