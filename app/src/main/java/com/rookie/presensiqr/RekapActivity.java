package com.rookie.presensiqr;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rookie.presensiqr.adapter.ListHariAdapter;
import com.rookie.presensiqr.model.JadwalKelas;

import java.util.ArrayList;

public class RekapActivity extends AppCompatActivity {
    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();

    private ListHariAdapter adapter;
    private ArrayList<JadwalKelas> jadwalKelas = new ArrayList<>();

    private RecyclerView rekapRecycler;
    private TextView tvTotalKelas,tvKehadiran,tvTdkHadir,tvPresentase,tvLihatKelas,tvMatkul;

    private String MY_PREFS_NAME = "PrefsUser";
    private String kodeMatkul,kodeKelas,nim;
    private int totalKehadiran,totalPertemuan,totalTdkHadir,totalKelas;
    private double presentase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rekap);

        Intent intent = getIntent();
        kodeMatkul = intent.getStringExtra("kodeMatkul");
        kodeKelas = intent.getStringExtra("kodeKelas");

        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        nim = prefs.getString("nim","No nim definied");

        rekapRecycler = findViewById(R.id.rekap_recyclerView);
        tvTotalKelas = findViewById(R.id.rekap_totalKelasTv);
        tvKehadiran = findViewById(R.id.rekap_totalHadirTv);
        tvTdkHadir = findViewById(R.id.rekap_totalTdkHadirTv);
        tvPresentase = findViewById(R.id.rekap_presentasiHadirTv);
        tvLihatKelas = findViewById(R.id.rekap_lihatKedatanganTv);
        tvMatkul = findViewById(R.id.rekap_matkulTv);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rekapRecycler.setLayoutManager(linearLayoutManager);

        getNamaMatkul();
        initJadwalKelas();
        getTotalKelas();
        getTotalKehadiran();
        getPresentase();

        tvLihatKelas.setOnClickListener(v->{
            Intent intent1 = new Intent(this,LihatTanggalKehadiranActivity.class);
            intent1.putExtra("kodeMatkul",kodeMatkul);
            intent1.putExtra("kodeKelas",kodeKelas);
            startActivity(intent1);
        });
    }

    private void initJadwalKelas(){
        DatabaseReference ref = mRef.child("matkul").child(kodeMatkul).child(kodeKelas).child("hari");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data: dataSnapshot.getChildren()){
                    String hari = data.getKey();
                    String kelas = String.valueOf(data.child("classroom").getValue());
                    String jam = String.valueOf(data.child("jam-mulai").getValue())+" - "+
                            String.valueOf(data.child("jam-selesai").getValue());

                    jadwalKelas.add(new JadwalKelas(hari,kelas,jam));
                    adapter = new ListHariAdapter(getApplicationContext(),jadwalKelas);
                    rekapRecycler.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}});
    }

    private void getNamaMatkul(){
        DatabaseReference refChild = mRef.child("matkul").child(kodeMatkul);
        refChild.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String[] matkul = new String[2];
                matkul[0] = String.valueOf(dataSnapshot.child("name").getValue());
                matkul[1] = String.valueOf(dataSnapshot.child(kodeKelas).child("kelas").getValue());
                String nama = matkul[0]+" "+matkul[1];

                tvMatkul.setText(nama);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}});
    }

    private void getTotalKelas(){
        DatabaseReference ref = mRef.child("matkul").child(kodeMatkul).child(kodeKelas).child("mahasiswa");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot data: dataSnapshot.getChildren()){
                    i++;
                }
                totalKelas = i;
                tvTotalKelas.setText(String.valueOf("Total Kelas: "+totalKelas));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}});
    }

    private void getTotalKehadiran(){
        DatabaseReference ref = mRef.child("matkul").child(kodeMatkul).child(kodeKelas).child("mahasiswa")
                .child(nim);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String sTotalKehadiran = String.valueOf(dataSnapshot.child("total-kehadiran").getValue());
                totalKehadiran = Integer.parseInt(sTotalKehadiran);
                tvKehadiran.setText("Total Kehadiran: "+sTotalKehadiran);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}});
    }

    private void getPresentase(){
        DatabaseReference ref = mRef.child("matkul").child(kodeMatkul).child(kodeKelas);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String pertemuan = String.valueOf(dataSnapshot.child("pertemuan").getValue());
                Log.d("pertemuan",pertemuan);
                totalPertemuan = Integer.parseInt(pertemuan);
                totalTdkHadir = totalPertemuan - totalKehadiran;
                if (totalPertemuan==0){
                    presentase = 100;
                }else{
                    presentase = (totalKehadiran / totalPertemuan) * 100;
                }
                double roundPresentase = Math.ceil(presentase);

                tvTdkHadir.setText("Total Tidak Hadir: "+totalTdkHadir);
                tvPresentase.setText("Presentase Kehadiran: "+roundPresentase+"%");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}});
    }
}
