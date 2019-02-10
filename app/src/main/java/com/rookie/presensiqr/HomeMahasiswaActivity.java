package com.rookie.presensiqr;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;
import com.rookie.presensiqr.adapter.HomeMahasiswaViewAdapter;
import com.rookie.presensiqr.adapter.ListJadwalKuliahAdapter;
import com.rookie.presensiqr.model.MataKuliah;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class HomeMahasiswaActivity extends AppCompatActivity {
    private DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();

    private ViewPager viewPager;
    private ImageView imgIndicator;
    private TextView tvLabelSchedule;

    private String MY_PREFS_NAME = "PrefsUser";
    SharedPreferences prefs;
    private String nim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_mahasiswa);

        viewPager = findViewById(R.id.homeMahasiswa_viewPager);
        imgIndicator = findViewById(R.id.homeMahasiswa_slideIndicatorImg);
        tvLabelSchedule = findViewById(R.id.homeMahasiswa_labelTodayScheduleTv);

        HomeMahasiswaViewAdapter adapter = new HomeMahasiswaViewAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);

        prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        nim = prefs.getString("nim","No nim definied");

        setTitleActionBar(nim);
        getCurrentTime();

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        tvLabelSchedule.setText(R.string.home_mhs_today_shcedule);
                        imgIndicator.setImageResource(R.drawable.slide2);
                        break;
                    case 1:
                        tvLabelSchedule.setText(R.string.home_mhs_all_shcedule);
                        imgIndicator.setImageResource(R.drawable.slide1);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {}});

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

    private void setTitleActionBar(String nim){
        DatabaseReference ref = mRef.child("mahasiswa").child(nim);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String title = String.valueOf(dataSnapshot.child("nama").getValue());
                getSupportActionBar().setTitle(title);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}});
    }

    private void getCurrentTime(){
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        String sDate = formatter.format(date);
        String limmit = "16:10";

        String[] parts = sDate.split(":");
        Calendar time1 = Calendar.getInstance();
        time1.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parts[0]));
        time1.set(Calendar.MINUTE, Integer.parseInt(parts[1]));

        parts = limmit.split(":");
        Calendar time2 = Calendar.getInstance();
        time2.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parts[0]));
        time2.set(Calendar.MINUTE, Integer.parseInt(parts[1]));
//        time2.add(Calendar.DATE,1);

        if(time1.before(time2)){
            Log.d("cek waktu","Masih bisa presensi");
        }else{
            Log.d("cek waktu","Udah telat");
        }
    }
}
