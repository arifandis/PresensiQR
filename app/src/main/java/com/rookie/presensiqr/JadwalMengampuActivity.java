package com.rookie.presensiqr;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rookie.presensiqr.adapter.ListJadwalMengampuAdapter;
import com.rookie.presensiqr.model.MataKuliah;

import java.util.ArrayList;
import java.util.Calendar;

public class JadwalMengampuActivity extends AppCompatActivity {
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();

    private String MY_PREFS_NAME = "PrefsUser";
    private String nip;
    private String kodeMatkul;

    private RecyclerView recyclerJadwalMengampu;
    private TextView tvDay,tvRefresh;
    private ProgressBar loading;

    private ListJadwalMengampuAdapter listJadwalMengampuAdapter;
    private ArrayList<MataKuliah> matkuls = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jadwal_mengampu);

        recyclerJadwalMengampu = findViewById(R.id.jadwalMengampu_recyclerView);
        tvDay = findViewById(R.id.jadwalMengampu_dayTv);
        tvRefresh = findViewById(R.id.jadwalMengampu_refreshTv);
        loading = findViewById(R.id.jadwalMengampu_progressBar);

        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        nip = prefs.getString("nip","No nip definied");

        Intent intent = getIntent();
        kodeMatkul = intent.getStringExtra("kodeMatkul");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerJadwalMengampu.setLayoutManager(linearLayoutManager);

        initData();
        tvDay.setText(getCurrentDay());

        tvRefresh.setOnClickListener(v->initData());
    }

    private void initData(){
        loading.setVisibility(View.VISIBLE);
        matkuls.clear();
        DatabaseReference ref = mRootRef.child("dosen").child(nip).child("jadwal-mengampu").child(getCurrentDay())
                .child(kodeMatkul);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data: dataSnapshot.getChildren()){
                    String kodeKelas = data.getKey();
                    readJadwalMengampu(kodeKelas);
                }
                loading.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}});
    }

    private void readJadwalMengampu(String kelas){
        DatabaseReference ref = mRootRef.child("matkul").child(kodeMatkul).child(kelas).child("hari").child(getCurrentDay());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String hari = dataSnapshot.getKey();
                String classroom = String.valueOf(dataSnapshot.child("classroom").getValue());
                String jam = String.valueOf(dataSnapshot.child("jam-mulai").getValue())+" - "+
                        String.valueOf(dataSnapshot.child("jam-selesai").getValue());
                Log.d("Hari",hari);
                Log.d("classroom",classroom);
                Log.d("jam",jam);

                DatabaseReference refChild = mRootRef.child("matkul").child(kodeMatkul);
                refChild.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String[] matkul = new String[2];
                        matkul[0] = String.valueOf(dataSnapshot.child("name").getValue());
                        matkul[1] = String.valueOf(dataSnapshot.child(kelas).child("kelas").getValue());
                        String nama = matkul[0]+" "+matkul[1];

                        matkuls.add(new MataKuliah(kodeMatkul,nama,classroom,jam,hari,kelas));
                        listJadwalMengampuAdapter = new ListJadwalMengampuAdapter(getApplicationContext(),matkuls);
                        recyclerJadwalMengampu.setAdapter(listJadwalMengampuAdapter);
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
