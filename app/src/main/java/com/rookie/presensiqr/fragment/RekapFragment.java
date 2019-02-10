package com.rookie.presensiqr.fragment;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rookie.presensiqr.R;
import com.rookie.presensiqr.adapter.ListJadwalPerHariAdapter;
import com.rookie.presensiqr.model.Hari;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class RekapFragment extends Fragment {
    private DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
    private ListJadwalPerHariAdapter adapter;
    private ArrayList<Hari> haris = new ArrayList<>();

    private RecyclerView recyclerView;
    private ProgressBar loading;

    private String nim;
    private String MY_PREFS_NAME = "PrefsUser";

    public RekapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_rekap, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.rekam_recyclerView);
        loading = view.findViewById(R.id.rekap_loading);

        SharedPreferences prefs = this.getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        nim = prefs.getString("nim","No nim definied");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        initData();
    }

    private void initData(){
        loading.setVisibility(View.VISIBLE);
        haris.clear();
        DatabaseReference ref = mRef.child("mahasiswa").child(nim).child("jadwal_kuliah");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data: dataSnapshot.getChildren()){
                    String hari = data.getKey();
                    haris.add(new Hari(hari));
                    adapter = new ListJadwalPerHariAdapter(getContext(),haris,nim);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
                loading.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}});
    }
}
