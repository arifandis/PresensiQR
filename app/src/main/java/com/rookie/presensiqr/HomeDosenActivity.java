package com.rookie.presensiqr;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.rookie.presensiqr.adapter.ListMatkulAdapter;
import com.rookie.presensiqr.model.MataKuliah;

import java.util.ArrayList;
import java.util.Calendar;

public class HomeDosenActivity extends AppCompatActivity {
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();

    private String MY_PREFS_NAME = "PrefsUser";
    private String nip, mDay;

    private RecyclerView matkulRecycler;
    private ProgressBar loading;

    private ListMatkulAdapter listMatkulAdapter;
    private ArrayList<MataKuliah> matkuls = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_dosen);

        matkulRecycler = findViewById(R.id.homeDosen_recyclerView);
        loading = findViewById(R.id.homeDosen_progressBar);

        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        nip = prefs.getString("nip","No nip definied");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        matkulRecycler.setLayoutManager(linearLayoutManager);

        mDay = getCurrentDay();
        setActionBarTitle();
        initData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_logout,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout){
            SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
            editor.clear();
            editor.apply();
            startActivity(new Intent(this,LoginActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initData(){
        loading.setVisibility(View.VISIBLE);
        DatabaseReference ref = mRootRef.child("dosen").child(nip).child("jadwal-mengampu").child(getCurrentDay());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data: dataSnapshot.getChildren()){
                    String kodeMatkul = data.getKey();
                    readMataKuliah(kodeMatkul);
                }
                loading.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}});
    }

    private void readMataKuliah(String kodeMatkul){
        DatabaseReference ref = mRootRef.child("matkul").child(kodeMatkul);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String kode = dataSnapshot.getKey();
                String nama = String.valueOf(dataSnapshot.child("name").getValue());
                matkuls.add(new MataKuliah(kode,nama,"","","",""));
                listMatkulAdapter = new ListMatkulAdapter(getApplicationContext(),matkuls);
                matkulRecycler.setAdapter(listMatkulAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}});
    }

    private void setActionBarTitle(){
        DatabaseReference ref = mRootRef.child("dosen").child(nip);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String nama = String.valueOf(dataSnapshot.child("nama").getValue());
                String gelar = String.valueOf(dataSnapshot.child("gelar").getValue());

                getSupportActionBar().setTitle(nama+", "+gelar);
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
