package com.rookie.presensiqr.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rookie.presensiqr.R;
import com.rookie.presensiqr.model.Mahasiswa;

import java.util.List;

public class ListKehadiranAdapter extends RecyclerView.Adapter<ListKehadiranAdapter.ListKehadiranViewHolder> {
    private Context context;
    private List<Mahasiswa> mahasiswaList;

    public ListKehadiranAdapter(Context context, List<Mahasiswa> mahasiswaList) {
        this.context = context;
        this.mahasiswaList = mahasiswaList;
    }

    @NonNull
    @Override
    public ListKehadiranAdapter.ListKehadiranViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_mahasiswa,parent,false);
        return new ListKehadiranAdapter.ListKehadiranViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListKehadiranAdapter.ListKehadiranViewHolder holder, int position) {
        Mahasiswa mahasiswa = mahasiswaList.get(position);

        holder.bindItem(mahasiswa);
    }

    @Override
    public int getItemCount() {
        return mahasiswaList.size();
    }

    class ListKehadiranViewHolder extends RecyclerView.ViewHolder{
        TextView tvNim,tvNama;

        public ListKehadiranViewHolder(View itemView) {
            super(itemView);

            tvNim = itemView.findViewById(R.id.itemMahasiswa_nimTv);
            tvNama = itemView.findViewById(R.id.itemMahasiswa_namaTv);
        }

        private void bindItem(Mahasiswa mahasiswa){
            tvNim.setText(mahasiswa.getNim());
            tvNama.setText(mahasiswa.getNama());
        }
    }
}
