package com.rookie.presensiqr;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class ShowBarcodeActivity extends AppCompatActivity {
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private String kodeMatkul,kodeKelas;

    private ImageView imgBarcode;
    private TextView tvDay,tvClassroom,tvMatkul,tvPukul;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_barcode);

        imgBarcode = findViewById(R.id.barcode_barcodeImg);
        tvDay = findViewById(R.id.barcode_dayTv);


        Intent intent = getIntent();
        kodeMatkul = intent.getStringExtra("kodeMatkul");
        kodeKelas = intent.getStringExtra("kodeKelas");

        showBarcode();
    }

    private void checking(){
        DatabaseReference ref = mRootRef.child("matkul").child(kodeMatkul).child(kodeKelas);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String pertemuan = String.valueOf(dataSnapshot.child("pertemuan").getValue());
                int total = Integer.parseInt(pertemuan);
                total++;
                ref.child("pertemuan").setValue(total);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}});
    }

    private void showBarcode(){
        try {
            checking();
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(kodeKelas, BarcodeFormat.QR_CODE, 400, 400);
            imgBarcode.setImageBitmap(bitmap);
        } catch(Exception e) {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }
    }
}
