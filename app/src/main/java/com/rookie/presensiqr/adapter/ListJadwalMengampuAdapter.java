package com.rookie.presensiqr.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rookie.presensiqr.LihatTanggalKehadiranActivity;
import com.rookie.presensiqr.R;
import com.rookie.presensiqr.ShowBarcodeActivity;
import com.rookie.presensiqr.model.MataKuliah;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ListJadwalMengampuAdapter extends RecyclerView.Adapter<ListJadwalMengampuAdapter.ListJadwalMengampuViewHolder>{
    private Context context;
    private List<MataKuliah> matkuls;

    private DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();

    public ListJadwalMengampuAdapter(Context context, List<MataKuliah> matkuls) {
        this.context = context;
        this.matkuls = matkuls;
    }

    @NonNull
    @Override
    public ListJadwalMengampuAdapter.ListJadwalMengampuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_jadwal_mengampu,parent,false);
        return new ListJadwalMengampuAdapter.ListJadwalMengampuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListJadwalMengampuAdapter.ListJadwalMengampuViewHolder holder, int position) {
        MataKuliah mataKuliah = matkuls.get(position);

        holder.tvNamaMatkul.setText(mataKuliah.getNamaMatkul());
        holder.tvKelasMatkul.setText("Classroom : "+mataKuliah.getKelasMatkul());
        holder.tvJamMatkul.setText(mataKuliah.getJamMatkul());

        holder.checkTime(mataKuliah.getJamMatkul());

        holder.btnShowBarcode.setOnClickListener(v->{
            checkDate(mataKuliah);
            Intent intent = new Intent(context,ShowBarcodeActivity.class);
            intent.putExtra("kodeMatkul",mataKuliah.getKodeMatkul());
            intent.putExtra("kodeKelas",mataKuliah.getKodeKelas());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });

        holder.lihatKehadiranBtn.setOnClickListener(v->{
            Intent intent1 = new Intent(context,LihatTanggalKehadiranActivity.class);
            intent1.putExtra("kodeMatkul",mataKuliah.getKodeMatkul());
            intent1.putExtra("kodeKelas",mataKuliah.getKodeKelas());
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent1);
        });
    }

    @Override
    public int getItemCount() {
        return matkuls.size();
    }

    class ListJadwalMengampuViewHolder extends RecyclerView.ViewHolder{
        private TextView tvNamaMatkul,tvKelasMatkul,tvJamMatkul;
        private Button btnShowBarcode,lihatKehadiranBtn;

        public ListJadwalMengampuViewHolder(View itemView) {
            super(itemView);

            tvNamaMatkul = itemView.findViewById(R.id.itemJadwalMengampu_matkulTv);
            tvKelasMatkul = itemView.findViewById(R.id.itemJadwalMengampu_classroomTv);
            tvJamMatkul = itemView.findViewById(R.id.itemJadwalMengampu_pukulTv);
            btnShowBarcode = itemView.findViewById(R.id.itemJadwalMengampu_showBtn);
            lihatKehadiranBtn = itemView.findViewById(R.id.itemJadwalMengampu_lihatKehadiranBtn);
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
                btnShowBarcode.setEnabled(false);
                btnShowBarcode.setBackgroundResource(R.color.grey);
            }else if (actual.after(date1) && actual.before(date2)){
                btnShowBarcode.setEnabled(true);
                btnShowBarcode.setBackgroundResource(R.drawable.bg_text_view);
            }else if (actual.after(date2)){
                btnShowBarcode.setEnabled(false);
                btnShowBarcode.setVisibility(View.GONE);
            }
        }
    }

    private void checkPertemuan(MataKuliah matkul){
        DatabaseReference ref = mRef.child("matkul").child(matkul.getKodeMatkul()).child(matkul.getKodeKelas())
                .child("attendance");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot data: dataSnapshot.getChildren()){
                    i++;
                }
                ref.child(getCurrentDate()).setValue(true);
                mRef.child("matkul").child(matkul.getKodeMatkul()).child(matkul.getKodeKelas()).child("pertemuan")
                        .setValue(i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}});
    }

    private void checkDate(MataKuliah matkul){
        DatabaseReference ref = mRef.child("matkul").child(matkul.getKodeMatkul()).child(matkul.getKodeKelas())
                .child("attendance");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data: dataSnapshot.getChildren()){
                    String date = data.getKey();
                    if (!date.equals(getCurrentDate())){
                        checkPertemuan(matkul);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}});
    }

    private String getCurrentDate(){
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();
        String sDate = formatter.format(date);

        return sDate;
    }
}
