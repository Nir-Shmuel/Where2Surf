package com.example.surfind;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.example.surfind.model.Coast;
import com.example.surfind.model.Model;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class CoastsListFragment extends Fragment {
    private RecyclerView coastsList;
    private List<Coast> coasts;
    private CoastsListsAdapter adapter;


    interface Delegate {
        void OnItemSelected(Coast coast);
    }

    Delegate parent;

    public CoastsListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_coasts_list, container, false);
        coastsList = view.findViewById(R.id.coasts_list_list);
        coastsList.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        coastsList.setLayoutManager(layoutManager);

        adapter = new CoastsListsAdapter();
        coastsList.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onClick(int position) {
                Coast coast = coasts.get(position);
                parent.OnItemSelected(coast);
            }
        });
        coasts = Model.instance.getCoasts();
        coastsList.addItemDecoration(new DividerItemDecoration(coastsList.getContext(), layoutManager.getOrientation()));

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
    }

    interface OnItemClickListener {
        void onClick(int position);
    }

    static class CoastsViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView location;
        CheckedTextView isProtected;

        public CoastsViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
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
            name = itemView.findViewById(R.id.row_coast_name_tv);
            location = itemView.findViewById(R.id.row_coast_location_tv);
            isProtected = itemView.findViewById(R.id.row_coast_protected_ctv);
        }

        void bind(Coast coast) {
            name.setText(coast.getName());
            location.setText(coast.getLocation());
            isProtected.setChecked(coast.isProtected());
        }
    }

    class CoastsListsAdapter extends RecyclerView.Adapter<CoastsViewHolder> {
        private OnItemClickListener listener;

        void setOnItemClickListener(OnItemClickListener listener) {
            this.listener = listener;
        }

        @NonNull
        @Override
        public CoastsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.list_row_coast, parent, false);
            return new CoastsViewHolder(view, listener);
        }

        @Override
        public void onBindViewHolder(@NonNull CoastsViewHolder holder, int position) {
            Coast coast = coasts.get(position);
            holder.bind(coast);
        }

        @Override
        public int getItemCount() {
            return coasts.size();
        }
    }
}
