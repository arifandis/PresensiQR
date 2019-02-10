package com.rookie.presensiqr.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rookie.presensiqr.R;
import com.rookie.presensiqr.ScannerActivity;
import com.rookie.presensiqr.model.MataKuliah;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class ListJadwalKuliahAdapter extends RecyclerView.Adapter<ListJadwalKuliahAdapter.ListJadwalKuliahViewHolder> {
    private Context context;
    private List<MataKuliah> matkuls;

    private DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
    private String MY_PREFS_NAME = "PrefsUser";
    private String nim;

    public ListJadwalKuliahAdapter(Context context, List<MataKuliah> matkuls) {
        this.context = context;
        this.matkuls = matkuls;
    }

    @NonNull
    @Override
    public ListJadwalKuliahAdapter.ListJadwalKuliahViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_jadwal_kuliah,parent,false);
        return new ListJadwalKuliahAdapter.ListJadwalKuliahViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListJadwalKuliahAdapter.ListJadwalKuliahViewHolder holder, int position) {
        MataKuliah matkul = matkuls.get(position);

        holder.tvNamaMatkul.setText(matkul.getNamaMatkul());
        holder.tvKelasMatkul.setText("Classroom : "+matkul.getKelasMatkul());
        holder.tvJamMatkul.setText(matkul.getJamMatkul());

        holder.checkTime(matkul.getJamMatkul());
        holder.checkAttended(matkul);

        holder.btnAttend.setOnClickListener(v ->{
            Intent intent = new Intent(context,ScannerActivity.class);
            intent.putExtra("kodeMatkul",matkul.getKodeMatkul());
            intent.putExtra("kodeKelas",matkul.getKodeKelas());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return matkuls.size();
    }

    class ListJadwalKuliahViewHolder extends RecyclerView.ViewHolder{
        private TextView tvNamaMatkul,tvKelasMatkul,tvJamMatkul;
        private Button btnAttend;
        private ImageView attendIndicator,absenIndicator;

        public ListJadwalKuliahViewHolder(View itemView) {
            super(itemView);

            tvNamaMatkul = itemView.findViewById(R.id.itemjadwalkuliath_matkulTv);
            tvKelasMatkul = itemView.findViewById(R.id.itemjadwalkuliah_classroomTv);
            tvJamMatkul = itemView.findViewById(R.id.itemjadwalkuliah_pukulTv);
            btnAttend = itemView.findViewById(R.id.itemjadwalkuliah_attendBtn);
            attendIndicator = itemView.findViewById(R.id.itemJadwalKuliah_checkedImg);
            absenIndicator = itemView.findViewById(R.id.itemJadwalKuliah_cancelImg);

            SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
            nim = prefs.getString("nim","No nim definied");

        }

        private void checkTime(String time){
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
            Date date = new Date();
            String sDate = formatter.format(date);

            String[] current = time.split(" - ");
            String time1 = current[0];
            String time2 = current[1];

            String[] parts = sDate.split(":");
            Calendar actual = Calendar.getInstance();
            actual.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parts[0]));
            actual.set(Calendar.MINUTE, Integer.parseInt(parts[1]));

            parts = time1.split(":");
            Calendar date1 = Calendar.getInstance();
            date1.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parts[0]));
            date1.set(Calendar.MINUTE, Integer.parseInt(parts[1]));

            parts = time2.split(":");
            Calendar date2 = Calendar.getInstance();
            date2.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parts[0]));
            date2.set(Calendar.MINUTE, Integer.parseInt(parts[1]));

            if(actual.before(date1)){
                btnAttend.setEnabled(false);
                btnAttend.setBackgroundResource(R.color.grey);
            }else if (actual.after(date1) && actual.before(date2)){
                btnAttend.setEnabled(true);
                btnAttend.setBackgroundResource(R.drawable.bg_text_view);
            }else if (actual.after(date2)){
                btnAttend.setEnabled(false);
                btnAttend.setVisibility(View.GONE);
                absenIndicator.setImageResource(R.drawable.ic_cancel_red);
            }
        }

        private String getCurrentDate(){
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            Date date = new Date();
            String sDate = formatter.format(date);

            return sDate;
        }

        private void checkAttended(MataKuliah matkul){
            DatabaseReference ref = mRef.child("matkul").child(matkul.getKodeMatkul()).child(matkul.getKodeKelas())
                    .child("attendance");
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot data: dataSnapshot.getChildren()){
                        String date = data.getKey();
                        for (DataSnapshot dataNim: data.getChildren()){
                            String dNim = dataNim.getKey();
                            if (date.equals(getCurrentDate()) && dNim.equals(nim)){
                                attendIndicator.setImageResource(R.drawable.ic_checked_green);
                                absenIndicator.setImageResource(R.drawable.ic_cancel_black);
                                btnAttend.setVisibility(View.GONE);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {}});
        }
    }
}
