package com.rookie.presensiqr;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private EditText etNim,etPassword;
    private Button btnLoginMahasiswa,btnLoginDosen;
    private ProgressBar loading;

    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private String MY_PREFS_NAME = "PrefsUser";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etNim= findViewById(R.id.login_usernameEt);
        etPassword = findViewById(R.id.login_passwordEt);
        btnLoginDosen = findViewById(R.id.login_DosenBtn);
        btnLoginMahasiswa = findViewById(R.id.login_mahasiswaBtn);
        loading = findViewById(R.id.login_loading);


        btnLoginMahasiswa.setOnClickListener(v ->
                loginMahasiswaHandler(etNim.getText().toString(),etPassword.getText().toString()));

        btnLoginDosen.setOnClickListener(v ->
                loginDosenHandler(etNim.getText().toString(),etPassword.getText().toString()));
    }

    private void loginMahasiswaHandler(final String nim, final String password){
        loading.setVisibility(View.VISIBLE);
        DatabaseReference ref = mRootRef.child("mahasiswa");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data: dataSnapshot.getChildren()){
                    String dataNim = data.getKey();
                    Log.d("NIM:",dataNim);
                    String dataPassword = String.valueOf(data.child("password").getValue());

                    if (nim.equals(dataNim) && password.equals(dataPassword)){
                        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                        editor.putString("nim",nim);
                        editor.apply();
                        loading.setVisibility(View.GONE);
                        startActivity(new Intent(getApplicationContext(),HomeMahasiswaActivity.class));
                        finish();
                    }else{
                        loading.setVisibility(View.GONE);
                        Toast.makeText(LoginActivity.this, "NIM atau password salah...", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }});
    }

    private void loginDosenHandler(final String nip, final String password){
        loading.setVisibility(View.VISIBLE);
        DatabaseReference ref = mRootRef.child("dosen");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data: dataSnapshot.getChildren()){
                    String dataNip = data.getKey();
                    String dataPassword = String.valueOf(data.child("password").getValue());
                    Log.d("NIP",dataNip);
                    Log.d("Password",dataPassword);

                    if (nip.equals(dataNip) && password.equals(dataPassword)){
                        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                        editor.putString("nip",nip);
                        editor.apply();
                        loading.setVisibility(View.GONE);
                        startActivity(new Intent(getApplicationContext(),HomeDosenActivity.class));
                        finish();
                        break;
                    }else{
                        loading.setVisibility(View.GONE);
                        Toast.makeText(LoginActivity.this, "NIP atau password salah...", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}});
    }
}
