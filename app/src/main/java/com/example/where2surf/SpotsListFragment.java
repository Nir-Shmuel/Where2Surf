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
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.example.where2surf.model.Spot;
import com.example.where2surf.model.SpotModel;

import java.util.LinkedList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class SpotsListFragment extends Fragment {
    private RecyclerView spotsList;
    private List<Spot> spotsData = new LinkedList<>();
    private SpotsListViewModel viewModel;
    private SpotsListAdapter adapter;
    private LiveData<List<Spot>> liveData;


    interface Delegate {
        void OnItemSelected(Spot spot);
    }

    Delegate parent;

    public SpotsListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_spots_list, container, false);
        spotsList = view.findViewById(R.id.spots_list_list);
        spotsList.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        spotsList.setLayoutManager(layoutManager);

        adapter = new SpotsListAdapter();
        spotsList.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onClick(int position) {
                Spot spot = spotsData.get(position);
                parent.OnItemSelected(spot);
            }
        });

        spotsList.addItemDecoration(new DividerItemDecoration(spotsList.getContext(), layoutManager.getOrientation()));

        liveData = viewModel.getLiveData();
        liveData.observe(getViewLifecycleOwner(), new Observer<List<Spot>>() {
            @Override
            public void onChanged(List<Spot> spots) {
                spotsData = spots;
                adapter.notifyDataSetChanged();
            }
        });

        final SwipeRefreshLayout swipeRefresh = view.findViewById(R.id.spots_list_swipe_refresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.refresh(new SpotModel.CompleteListener() {
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

        viewModel = new ViewModelProvider(this).get(SpotsListViewModel.class);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        parent = null;
    }

    interface OnItemClickListener {
        void onClick(int position);
    }

    static class SpotRowViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView location;
        CheckedTextView isWindProtected;

        public SpotRowViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
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
            name = itemView.findViewById(R.id.row_spot_name_tv);
            location = itemView.findViewById(R.id.row_spot_location_tv);
            isWindProtected = itemView.findViewById(R.id.row_spot_protected_ctv);
        }

        void bind(Spot spot) {
            name.setText(spot.getName());
            location.setText(spot.getLocation());
            isWindProtected.setChecked(spot.isWindProtected());
        }
    }

    class SpotsListAdapter extends RecyclerView.Adapter<SpotRowViewHolder> {
        private OnItemClickListener listener;

        void setOnItemClickListener(OnItemClickListener listener) {
            this.listener = listener;
        }

        @NonNull
        @Override
        public SpotRowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.list_row_spot, parent, false);
            return new SpotRowViewHolder(view, listener);
        }

        @Override
        public void onBindViewHolder(@NonNull SpotRowViewHolder holder, int position) {
            Spot spot = spotsData.get(position);
            holder.bind(spot);
        }

        @Override
        public int getItemCount() {
            return spotsData.size();
        }
    }
}
