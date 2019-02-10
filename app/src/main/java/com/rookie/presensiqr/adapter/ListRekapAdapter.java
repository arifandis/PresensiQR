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

import com.rookie.presensiqr.R;
import com.rookie.presensiqr.RekapActivity;
import com.rookie.presensiqr.model.MataKuliah;

import java.util.List;

public class ListRekapAdapter extends RecyclerView.Adapter<ListRekapAdapter.ListRekapViewHolder> {
    private Context context;
    private List<MataKuliah> matkuls;

    public ListRekapAdapter(Context context, List<MataKuliah> matkuls) {
        this.context = context;
        this.matkuls = matkuls;
    }

    @NonNull
    @Override
    public ListRekapAdapter.ListRekapViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_rekap,parent,false);
        return new ListRekapAdapter.ListRekapViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListRekapAdapter.ListRekapViewHolder holder, int position) {
        MataKuliah matkul = matkuls.get(position);

        holder.bindItem(matkul);
    }

    @Override
    public int getItemCount() {
        return matkuls.size();
    }

    class ListRekapViewHolder extends RecyclerView.ViewHolder{
        private TextView tvNamaMatkul,tvKelasMatkul,tvJamMatkul;
        private Button btnRekap;

        public ListRekapViewHolder(View itemView) {
            super(itemView);

            tvNamaMatkul = itemView.findViewById(R.id.itemrekap_matkulTv);
            tvKelasMatkul = itemView.findViewById(R.id.itemrekap_classroomTv);
            tvJamMatkul = itemView.findViewById(R.id.itemrekap_pukulTv);
            btnRekap = itemView.findViewById(R.id.itemrekap_reapBtn);
        }

        private void bindItem(MataKuliah matkul){
            tvNamaMatkul.setText(matkul.getNamaMatkul());
            tvKelasMatkul.setText("CLASSROOM : "+matkul.getKelasMatkul());
            tvJamMatkul.setText(matkul.getJamMatkul());

            btnRekap.setOnClickListener(v->{
                Intent intent = new Intent(context,RekapActivity.class);
                intent.putExtra("kodeMatkul",matkul.getKodeMatkul());
                intent.putExtra("kodeKelas",matkul.getKodeKelas());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            });
        }
    }
}
