package com.rookie.presensiqr;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;

import java.text.SimpleDateFormat;
import java.util.Date;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
    private ZXingScannerView mScannerView;

    private String kodeMatkul;
    private String kodeKelas;

    private String MY_PREFS_NAME = "PrefsUser";
    private String nim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);
        getSupportActionBar().setTitle("Presensi QR");

        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        nim = prefs.getString("nim","No nim definied");

        Intent intent = getIntent();
        kodeMatkul = intent.getStringExtra("kodeMatkul");
        kodeKelas = intent.getStringExtra("kodeKelas");
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result result) {
        addToPresensi(result.getText());
    }

    private void incrementKehadiran(){
        DatabaseReference ref = mRef.child(kodeMatkul).child(kodeKelas).child("mahasiswa").child(nim)
                .child("total-kehadiran");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String sTotalKehadiran = String.valueOf(dataSnapshot.getValue());
                int totalKehadiran = Integer.parseInt(sTotalKehadiran);

                totalKehadiran++;
                mRef.child(kodeMatkul).child(kodeKelas).child("mahasiswa").child(nim)
                        .child("total-kehadiran").setValue(totalKehadiran);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}});
    }

    private void addToPresensi(String kelas){
        if (kelas.equals(kodeKelas)){
            mRef.child("matkul").child(kodeMatkul).child(kodeKelas).child("attendance")
                    .child(getCurrentDate()).child(nim).setValue(true);

            mRef.child("matkul").child(kodeMatkul).child(kodeKelas).child("mahasiswa").child(nim).child("attendance")
                    .child(getCurrentDate()).setValue(true);

            incrementKehadiran();

            Toast.makeText(this, "Anda telah melakukan presensi...", Toast.LENGTH_SHORT).show();
            finish();
        }
    }



    private String getCurrentDate(){
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();
        String sDate = formatter.format(date);

        return sDate;
    }
}
