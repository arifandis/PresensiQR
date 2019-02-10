package com.rookie.presensiqr.fragment;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rookie.presensiqr.R;
import com.rookie.presensiqr.adapter.ListJadwalKuliahAdapter;
import com.rookie.presensiqr.model.MataKuliah;

import java.util.ArrayList;
import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class JadwalHariIniFragment extends Fragment {
    private DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();

    private RecyclerView matkulRecycler;
    private TextView tvDay,tvRefresh;
    private ProgressBar loading;
    private ListJadwalKuliahAdapter listAdapter;
    private ArrayList<MataKuliah> matkuls = new ArrayList<>();

    private String MY_PREFS_NAME = "PrefsUser";
    private String nim, mDay;


    public JadwalHariIniFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_jadwal_hari_ini, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        matkulRecycler = view.findViewById(R.id.homeMahasiswa_recyclerView);
        tvDay = view.findViewById(R.id.homeMahasiswa_dayTv);
        tvRefresh = view.findViewById(R.id.homeMahasiswa_refreshTv);
        loading = view.findViewById(R.id.homeMahasiswa_loading);

        SharedPreferences prefs = this.getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        nim = prefs.getString("nim","No nim definied");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        matkulRecycler.setLayoutManager(linearLayoutManager);

        mDay = getCurrentDay();
        Log.d("day",mDay);

        initData();
        tvDay.setText(mDay);

        tvRefresh.setOnClickListener(v -> {
            initData();
            listAdapter.notifyDataSetChanged();
        });
    }

    private void initData(){
        loading.setVisibility(View.VISIBLE);
        matkuls.clear();
        DatabaseReference ref = mRef.child("mahasiswa").child(nim).child("jadwal_kuliah").child(mDay);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data: dataSnapshot.getChildren()){
                    String kodeMatkul = data.getKey();
                    for (DataSnapshot data2: data.getChildren()){
                        String kelas = data2.getKey();
                        Log.d("Kode Matkul",kodeMatkul);
                        Log.d("Kelas",kelas);
                        readJadwalKuliah(kodeMatkul,kelas);
                    }
                }
                loading.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}});
    }

    private void readJadwalKuliah(String kodeMatkul, String kelas){
        DatabaseReference ref = mRef.child("matkul").child(kodeMatkul).child(kelas).child("hari").child(mDay);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String classroom = String.valueOf(dataSnapshot.child("classroom").getValue());
                String jam = String.valueOf(dataSnapshot.child("jam-mulai").getValue())+" - "+
                        String.valueOf(dataSnapshot.child("jam-selesai").getValue());
                Log.d("Hari",mDay);
                Log.d("classroom",classroom);
                Log.d("jam",jam);

                DatabaseReference refChild = mRef.child("matkul").child(kodeMatkul);
                refChild.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String[] matkul = new String[2];
                        matkul[0] = String.valueOf(dataSnapshot.child("name").getValue());
                        matkul[1] = String.valueOf(dataSnapshot.child(kelas).child("kelas").getValue());
                        String nama = matkul[0]+" "+matkul[1];

                        matkuls.add(new MataKuliah(kodeMatkul,nama,classroom,jam,mDay,kelas));
                        listAdapter = new ListJadwalKuliahAdapter(getActivity(),matkuls);
                        matkulRecycler.setAdapter(listAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}});
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}});
    }

    private String getCurrentDay(){
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        String sDay = "";

        switch (day) {
            case Calendar.SUNDAY:
                sDay = "Sunday";
                break;
            case Calendar.MONDAY:
                sDay = "Monday";
                break;
            case Calendar.TUESDAY:
                sDay = "Tuesday";
                break;
            case Calendar.WEDNESDAY:
                sDay = "Wednesday";
                break;
            case Calendar.THURSDAY:
                sDay = "Thursday";
                break;
            case Calendar.FRIDAY:
                sDay = "Friday";
                break;
            case Calendar.SATURDAY:
                sDay = "Saturday";
                break;
        }
        return sDay;
    }
}
