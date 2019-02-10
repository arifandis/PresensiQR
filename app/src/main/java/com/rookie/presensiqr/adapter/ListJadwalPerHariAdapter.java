package com.rookie.presensiqr.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.rookie.presensiqr.R;
import com.rookie.presensiqr.model.Hari;
import com.rookie.presensiqr.model.MataKuliah;

import java.util.ArrayList;
import java.util.List;

public class ListJadwalPerHariAdapter extends RecyclerView.Adapter<ListJadwalPerHariAdapter.ListHariViewHolder> {
    private DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
    private Context context;
    private List<Hari> haris;
    private String nim;

    public ListJadwalPerHariAdapter(Context context, List<Hari> haris, String nim) {
        this.context = context;
        this.haris = haris;
        this.nim = nim;
    }

    @NonNull
    @Override
    public ListJadwalPerHariAdapter.ListHariViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_jadwal_by_hari,parent,false);
        return new ListJadwalPerHariAdapter.ListHariViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListJadwalPerHariAdapter.ListHariViewHolder holder, int position) {
        Hari hari = haris.get(position);

        holder.bindItem(hari);
    }

    @Override
    public int getItemCount() {
        return haris.size();
    }

    class ListHariViewHolder extends RecyclerView.ViewHolder{
        private TextView tvHari;
        private RecyclerView recyclerView;
        private Button btnRekap;

        private ListRekapAdapter listAdapter;
        private ArrayList<MataKuliah> matkuls = new ArrayList<>();

        public ListHariViewHolder(View itemView) {
            super(itemView);

            tvHari = itemView.findViewById(R.id.itemhari_dayTv);
            recyclerView = itemView.findViewById(R.id.itemhari_recyclerView);
        }

        private void bindItem(Hari hari){
            tvHari.setText(hari.getNama());

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            linearLayoutManager.setReverseLayout(true);
            linearLayoutManager.setStackFromEnd(true);
            recyclerView.setLayoutManager(linearLayoutManager);

            initData(hari.getNama());
        }

        private void initData(String hari){
            matkuls.clear();
            DatabaseReference ref = mRef.child("mahasiswa").child(nim).child("jadwal_kuliah").child(hari);
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot data: dataSnapshot.getChildren()){
                        String kodeMatkul = data.getKey();
                        for (DataSnapshot data2: data.getChildren()){
                            String kelas = data2.getKey();
                            Log.d("Kode Matkul",kodeMatkul);
                            Log.d("Kelas",kelas);
                            readJadwalKuliah(kodeMatkul,kelas, hari);
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {}});
        }

        private void readJadwalKuliah(String kodeMatkul, String kelas, String hari){
            DatabaseReference ref = mRef.child("matkul").child(kodeMatkul).child(kelas).child("hari").child(hari);
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String hari = dataSnapshot.getKey();
                    String classroom = String.valueOf(dataSnapshot.child("classroom").getValue());
                    String jam = String.valueOf(dataSnapshot.child("jam-mulai").getValue())+" - "+
                            String.valueOf(dataSnapshot.child("jam-selesai").getValue());
                    Log.d("Hari",hari);
                    Log.d("classroom",classroom);
                    Log.d("jam",jam);

                    DatabaseReference refChild = mRef.child("matkul").child(kodeMatkul);
                    refChild.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String[] matkul = new String[2];
                            matkul[0] = String.valueOf(dataSnapshot.child("name").getValue());
                            matkul[1] = String.valueOf(dataSnapshot.child(kelas).child("kelas").getValue());
                            String nama = matkul[0]+" "+matkul[1];

                            matkuls.add(new MataKuliah(kodeMatkul,nama,classroom,jam,hari,kelas));
                            listAdapter = new ListRekapAdapter(context,matkuls);
                            recyclerView.setAdapter(listAdapter);
                            listAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {}});
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {}});
        }
    }
}
