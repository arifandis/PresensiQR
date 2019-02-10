package com.rookie.presensiqr;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SplashActivity extends AppCompatActivity {
    private String MY_PREFS_NAME = "PrefsUser";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

        new Thread(() -> {
            try{
                Thread.sleep(2000);
            }catch (Exception e){
                e.printStackTrace();
            }
            if (prefs.contains("nim")){
                startActivity(new Intent(getApplicationContext(),HomeMahasiswaActivity.class));
                finish();
            }else if (prefs.contains("nip")){
                startActivity(new Intent(getApplicationContext(),HomeDosenActivity.class));
                finish();
            }else {
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                finish();
            }
        }).start();
    }
}
