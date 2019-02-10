package com.rookie.presensiqr.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.rookie.presensiqr.LihatKehadiranMahasiswa;
import com.rookie.presensiqr.R;
import com.rookie.presensiqr.model.Hari;

import java.util.List;

public class ListTanggalAdapter extends RecyclerView.Adapter<ListTanggalAdapter.ListTanggalViewHolder> {
    private Context context;
    private List<Hari> haris;
    private String kodeMatkul,kodeKelas;

    public ListTanggalAdapter(Context context, List<Hari> haris, String kodeMatkul, String kodeKelas) {
        this.context = context;
        this.haris = haris;
        this.kodeMatkul = kodeMatkul;
        this.kodeKelas = kodeKelas;
    }

    @NonNull
    @Override
    public ListTanggalAdapter.ListTanggalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_matkul,parent,false);
        return new ListTanggalAdapter.ListTanggalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListTanggalAdapter.ListTanggalViewHolder holder, int position) {
        Hari tanggal = haris.get(position);

        holder.tvTangga.setText(tanggal.getNama());

        holder.parent.setOnClickListener(v->{
            Intent intent = new Intent(context,LihatKehadiranMahasiswa.class);
            intent.putExtra("kodeMatkul",kodeMatkul);
            intent.putExtra("kodeKelas",kodeKelas);
            intent.putExtra("tanggal",tanggal.getNama());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return haris.size();
    }

    class ListTanggalViewHolder extends RecyclerView.ViewHolder{
        private TextView tvTangga;
        private ConstraintLayout parent;

        public ListTanggalViewHolder(View itemView) {
            super(itemView);

            tvTangga = itemView.findViewById(R.id.itemMatkul_namaMatkulTv);
            parent = itemView.findViewById(R.id.itemMatkul_parent);
        }
    }
}
