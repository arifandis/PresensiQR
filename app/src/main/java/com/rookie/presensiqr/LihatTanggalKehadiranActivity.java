package com.rookie.presensiqr;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rookie.presensiqr.adapter.ListHariAdapter;
import com.rookie.presensiqr.adapter.ListJadwalPerHariAdapter;
import com.rookie.presensiqr.adapter.ListKehadiranAdapter;
import com.rookie.presensiqr.adapter.ListTanggalAdapter;
import com.rookie.presensiqr.model.Hari;
import com.rookie.presensiqr.model.Mahasiswa;

import java.util.ArrayList;
import java.util.Calendar;

public class LihatTanggalKehadiranActivity extends AppCompatActivity {
    private DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();

    private RecyclerView recyclerTanggal;

    private ListTanggalAdapter tanggalAdapter;
    private ArrayList<Hari> tanggal = new ArrayList<>();

    private String kodeMatkul,kodeKelas,nim;
    private String MY_PREFS_NAME = "PrefsUser";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lihat_tanggal_kehadiran);

        recyclerTanggal = findViewById(R.id.listTglKehadiran_recyclerView);

        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        nim = prefs.getString("nim","No nim definied");

        Intent intent = getIntent();
        kodeMatkul = intent.getStringExtra("kodeMatkul");
        kodeKelas = intent.getStringExtra("kodeKelas");
        Log.d("Kode Matkul",kodeMatkul);
        Log.d("Kode Kelas",kodeKelas);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerTanggal.setLayoutManager(linearLayoutManager);

        initData();
    }

    private void initData(){
        tanggal.clear();
        DatabaseReference ref = mRef.child("matkul").child(kodeMatkul).child(kodeKelas)
                .child("attendance");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data: dataSnapshot.getChildren()){
                    String key = data.getKey();
                    tanggal.add(new Hari(key));
                    tanggalAdapter = new ListTanggalAdapter(getApplicationContext(),tanggal, kodeMatkul,kodeKelas);
                    recyclerTanggal.setAdapter(tanggalAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}});
    }
}
