package com.rookie.presensiqr;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rookie.presensiqr.adapter.ListKehadiranAdapter;
import com.rookie.presensiqr.model.Mahasiswa;

import java.util.ArrayList;

public class LihatKehadiranMahasiswa extends AppCompatActivity {
    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();

    private RecyclerView recyclerView;
    private ListKehadiranAdapter adapter;
    private ArrayList<Mahasiswa> mahasiswas = new ArrayList<>();

    private String nama,kodeMatkul,kodeKelas,tanggal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lihat_kehadiran_mahasiswa);

        recyclerView = findViewById(R.id.lihatKehadiran_recyclerView);

        Intent intent = getIntent();
        kodeMatkul = intent.getStringExtra("kodeMatkul");
        kodeKelas = intent.getStringExtra("kodeKelas");
        tanggal = intent.getStringExtra("tanggal");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        initData();
    }

    private void initData(){
        mahasiswas.clear();
        DatabaseReference ref = mRef.child("matkul").child(kodeMatkul).child(kodeKelas).child("attendance")
                .child(tanggal);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data: dataSnapshot.getChildren()){
                    String nim = data.getKey();
                    getNama(nim);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}});
    }

    private void getNama(String nim){
        DatabaseReference ref = mRef.child("mahasiswa").child(nim).child("nama");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                nama = String.valueOf(dataSnapshot.getValue());

                mahasiswas.add(new Mahasiswa(nim,nama));
                adapter = new ListKehadiranAdapter(getApplicationContext(),mahasiswas);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}});
    }
}
