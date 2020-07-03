package com.example.where2surf.UI.reports.reportsList;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.example.where2surf.R;
import com.example.where2surf.model.Report;
import com.example.where2surf.model.ReportModel;
import com.example.where2surf.model.UserModel;

import java.util.Comparator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserReportsListFragment extends ReportsListFragment {

    private UserReportsListViewModel viewModel;
    private String userId;

    public UserReportsListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState);

        userId = UserModel.instance.getCurrentUserId();
        liveData = viewModel.getLiveData(userId);
        liveData.observe(getViewLifecycleOwner(), new Observer<List<Report>>() {
            @Override
            public void onChanged(List<Report> reports) {
                reportsData = reports;
                reportsData.sort(new Comparator<Report>() {
                    @Override
                    public int compare(Report o1, Report o2) {
                        return Long.compare(o2.getDate(), o1.getDate());
                    }
                });
                adapter.notifyDataSetChanged();
            }
        });
        final SwipeRefreshLayout swipeRefresh = view.findViewById(R.id.reports_list_swipe_refresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.refresh(userId, new ReportModel.CompleteListener() {
                    @Override
                    public void onComplete() {
                        swipeRefresh.setRefreshing(false);
                        adapter.notifyDataSetChanged();
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

        viewModel = new ViewModelProvider(this).get(UserReportsListViewModel.class);
    }


}