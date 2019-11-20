package com.ms.projectlecturer.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ms.projectlecturer.R;
import com.ms.projectlecturer.model.Lecturer;
import com.ms.projectlecturer.model.Presence;
import com.ms.projectlecturer.util.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LecturerFragment extends Fragment implements RecyclerViewAdapter.ItemClickListener,
        View.OnClickListener {


    //private String lecturerId;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    private List<String> dataset;
    private List<Presence> presences;
    private ValueEventListener currentListener;
    private DatabaseReference lecturerReference;
    private TextView lecturerStats;
    private LecturerFragment lecturerFragment;
    private LayoutInflater inflater;
    private MapFragment mapFragment;
    private LecturersActivity lecturersActivity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.inflater = inflater;
        return inflater.inflate(R.layout.fragment_lecturer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lecturerFragment = this;
        lecturersActivity = (LecturersActivity) getActivity();
        mapFragment = lecturersActivity.getMapScreenFragment();
    }

    @Override
    public void onItemClick(View view, int position) {
        Presence presence = presences.get(position);
        mapFragment.addMarkerAt(new LatLng(presence.getLat(), presence.getLat()), presence.getBuildingName(),
                presence.getDayOfTheWeek() + " " + presence.getStartTime() + "-" +
                        presence.getEndTime());
        lecturersActivity.setCurrentFragment(mapFragment);
    }

    @Override
    public void onClick(View view) {

    }


    public void setLecturer(Lecturer lecturer) {
        this.lecturerStats.setText(lecturer.toString(getContext()));
        if (currentListener != null) {
            lecturerReference.removeEventListener(currentListener);
        }
        lecturerReference = FirebaseDatabase.getInstance().getReference().child("Lecturers").child(lecturer.getLecturerId());
        currentListener = lecturerReference.child("presences").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataset = new ArrayList<>();
                presences = new ArrayList<>();
               for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                     Presence presence = childSnapshot.getValue(Presence.class);
                     dataset.add(presence.toString(getContext()));
                     presences.add(presence);
               }
                adapter = new RecyclerViewAdapter(inflater, dataset);
                recyclerView.setAdapter(adapter);
                adapter.setClickListener(lecturerFragment);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                databaseError.toException().printStackTrace();
            }
        });

    }
}
