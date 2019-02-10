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

import com.rookie.presensiqr.JadwalMengampuActivity;
import com.rookie.presensiqr.R;
import com.rookie.presensiqr.model.MataKuliah;

import java.util.List;

public class ListMatkulAdapter extends RecyclerView.Adapter<ListMatkulAdapter.ListMatkulViewHolder> {
    private Context context;
    private List<MataKuliah> matkuls;

    public ListMatkulAdapter(Context context, List<MataKuliah> matkuls) {
        this.context = context;
        this.matkuls = matkuls;
    }

    @NonNull
    @Override
    public ListMatkulAdapter.ListMatkulViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_matkul,parent,false);
        return new ListMatkulAdapter.ListMatkulViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListMatkulAdapter.ListMatkulViewHolder holder, int position) {
        MataKuliah mataKuliah = matkuls.get(position);

        holder.tvMatkul.setText(mataKuliah.getNamaMatkul());

        holder.parent.setOnClickListener(v -> {
            Intent intent = new Intent(context,JadwalMengampuActivity.class);
            intent.putExtra("kodeMatkul",mataKuliah.getKodeMatkul());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return matkuls.size();
    }

    class ListMatkulViewHolder extends RecyclerView.ViewHolder{
        private TextView tvMatkul;
        private ConstraintLayout parent;

        public ListMatkulViewHolder(View itemView) {
            super(itemView);
            tvMatkul = itemView.findViewById(R.id.itemMatkul_namaMatkulTv);
            parent = itemView.findViewById(R.id.itemMatkul_parent);
        }
    }
}
