package com.example.surfind;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class CoastReportsListFragment extends Fragment {
    RecyclerView reportsList;

    public CoastReportsListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_coast_reports_list, container, false);
        reportsList = view.findViewById(R.id.coast_reports_list_list);

        return view;
    }

    static class ReportsViewHoler extends RecyclerView.ViewHolder{

        public ReportsViewHoler(@NonNull View itemView) {
            super(itemView);
        }
    }

}
