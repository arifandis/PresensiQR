package com.rookie.presensiqr.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rookie.presensiqr.R;
import com.rookie.presensiqr.model.JadwalKelas;

import java.util.List;

public class ListHariAdapter extends RecyclerView.Adapter<ListHariAdapter.ListHariAdapterViewHolder> {
    private Context context;
    private List<JadwalKelas> jadwalKelas;

    public ListHariAdapter(Context context, List<JadwalKelas> jadwalKelas) {
        this.context = context;
        this.jadwalKelas = jadwalKelas;
    }

    @NonNull
    @Override
    public ListHariAdapter.ListHariAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_hari,parent,false);
        return new ListHariAdapter.ListHariAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListHariAdapter.ListHariAdapterViewHolder holder, int position) {
        JadwalKelas kelas = jadwalKelas.get(position);

        holder.bindItem(kelas);
    }

    @Override
    public int getItemCount() {
        return jadwalKelas.size();
    }

    class ListHariAdapterViewHolder extends RecyclerView.ViewHolder{
        private TextView tvHari,tvKelas,tvPukul;

        public ListHariAdapterViewHolder(View itemView) {
            super(itemView);

            tvHari = itemView.findViewById(R.id.rekap_dayTv);
            tvPukul = itemView.findViewById(R.id.rekap_pukulTv);
            tvKelas = itemView.findViewById(R.id.rekap_classroomTv);
        }

        private void bindItem(JadwalKelas kelas){
            tvHari.setText(kelas.getHari());
            tvKelas.setText("CLASSROOM : "+kelas.getKelas());
            tvPukul.setText(kelas.getPukul());
        }
    }
}
