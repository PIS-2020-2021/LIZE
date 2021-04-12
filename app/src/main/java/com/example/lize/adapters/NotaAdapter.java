package com.example.lize.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lize.R;
import com.example.lize.model.Nota;

import java.util.List;

public class NotaAdapter extends RecyclerView.Adapter<NotaAdapter.NotaViewHolder>{

    private List<Nota> notas;

    public NotaAdapter(List<Nota> notas) {
        this.notas = notas;
    }

    @NonNull
    @Override
    public NotaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NotaViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_container_nota,parent,false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull NotaViewHolder holder, int position) {
        holder.setNota(notas.get(position));
    }

    @Override
    public int getItemCount() {
        return notas.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class NotaViewHolder extends RecyclerView.ViewHolder{
        TextView textTitulo,textFecha;

        NotaViewHolder(@NonNull View itemView){
            super(itemView);
            textTitulo = itemView.findViewById(R.id.textTitulo);
            textFecha = itemView.findViewById(R.id.textDateTime);
        }

        void setNota(Nota nota){
            textTitulo.setText(nota.getTitulo());
            textFecha.setText(nota.getFecha());
        }

    }
}
