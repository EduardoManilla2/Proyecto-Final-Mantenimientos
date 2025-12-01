package com.example.prueba002;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MantenimientoAdapter extends RecyclerView.Adapter<MantenimientoAdapter.MantenimientoViewHolder> {

    private List<Mantenimiento> listaMantenimientos;
    private List<Mantenimiento> listaCompleta;

    public MantenimientoAdapter(List<Mantenimiento> listaMantenimientos) {
        this.listaMantenimientos = listaMantenimientos;
        this.listaCompleta = new ArrayList<>(listaMantenimientos);
    }

    @NonNull
    @Override
    public MantenimientoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mantenimiento, parent, false);
        return new MantenimientoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MantenimientoViewHolder holder, int position) {
        Mantenimiento mantenimiento = listaMantenimientos.get(position);

        holder.tvIdMantenimiento.setText(mantenimiento.getIdMantenimiento());

        if ("Retrazado".equalsIgnoreCase(mantenimiento.getEstatus())) {
            holder.tvEstatus.setText("Retrazado");
            holder.tvEstatus.setTextColor(0xFFEF3012);
            holder.tvEstatus.setBackgroundResource(R.drawable.rounded_status_retrazado);
            holder.franjaIndicadora.setBackgroundResource(R.drawable.rounded_background_retrazado);
        } else if ("Pendiente".equalsIgnoreCase(mantenimiento.getEstatus())) {
            holder.tvEstatus.setText("Pendiente");
            holder.tvEstatus.setTextColor(0xFFEF6712);
            holder.tvEstatus.setBackgroundResource(R.drawable.rounded_status_pendiente);
            holder.franjaIndicadora.setBackgroundResource(R.drawable.rounded_background_pendiente);
        } else if ("Realizado".equalsIgnoreCase(mantenimiento.getEstatus())) {
            holder.tvEstatus.setText("Realizado");
            holder.tvEstatus.setTextColor(0xFF00AA00);
            holder.tvEstatus.setBackgroundResource(R.drawable.rounded_status_realizado);
            holder.franjaIndicadora.setBackgroundResource(R.drawable.rounded_background_realizado);
        } else if ("Postergado".equalsIgnoreCase(mantenimiento.getEstatus())) {
            holder.tvEstatus.setText("Postergado");
            holder.tvEstatus.setTextColor(0xFF9C9C27);
            holder.tvEstatus.setBackgroundResource(R.drawable.rounded_status_postergado);
            holder.franjaIndicadora.setBackgroundResource(R.drawable.rounded_background_postergado);
        }

        holder.tvFechaCierre.setText("Fecha cierre: " + (mantenimiento.getFechaProx() != null ? mantenimiento.getFechaProx().split(" ")[0] : "N/A"));
        holder.tvIdEquipo.setText("Equipo: " + mantenimiento.getNombreEquipo());
        holder.tvLabo.setText("Laboratorio: " + mantenimiento.getNombreLabo());
    }

    @Override
    public int getItemCount() {
        return listaMantenimientos.size();
    }


    @SuppressLint("NotifyDataSetChanged")
    public void filter(String texto) {
        listaMantenimientos.clear();

        if (texto == null || texto.trim().isEmpty()) {
            listaMantenimientos.addAll(listaCompleta);
        } else {
            String query = texto.toLowerCase();
            for (Mantenimiento m : listaCompleta) {
                if ((String.valueOf(m.getIdMantenimiento()).toLowerCase().contains(query))
                        || (m.getNombreEquipo() != null && m.getNombreEquipo().toLowerCase().contains(query))
                        || (m.getEstatus() != null && m.getEstatus().toLowerCase().contains(query))
                        || (m.getActividad() != null && m.getActividad().toLowerCase().contains(query))
                        || (m.getNombreLabo() != null && m.getNombreLabo().toLowerCase().contains(query))) {
                    listaMantenimientos.add(m);
                }
            }
        }
        notifyDataSetChanged();
    }



    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<Mantenimiento> nuevosDatos) {
        listaMantenimientos.clear();
        listaMantenimientos.addAll(nuevosDatos);
        listaCompleta.clear();
        listaCompleta.addAll(nuevosDatos);
        notifyDataSetChanged();
    }


    public static class MantenimientoViewHolder extends RecyclerView.ViewHolder {
        TextView tvIdMantenimiento, tvEstatus, tvFechaCierre, tvIdEquipo, tvLabo;
        Button btnDetalle;
        View franjaIndicadora;

        public MantenimientoViewHolder(@NonNull View itemView) {
            super(itemView);

            tvEstatus = itemView.findViewById(R.id.tv_estatus);
            franjaIndicadora = itemView.findViewById(R.id.franja_indicadora);
            tvIdMantenimiento = itemView.findViewById(R.id.tv_id_mantenimiento);
            tvIdEquipo = itemView.findViewById(R.id.tv_id_equipo);
            tvLabo = itemView.findViewById(R.id.tv_Labo);
            tvFechaCierre = itemView.findViewById(R.id.tv_fecha_cierre);
            btnDetalle = itemView.findViewById(R.id.btnDetalles);

            btnDetalle.setOnClickListener(view -> {
                DetalleMantenimientoFragment frag = new DetalleMantenimientoFragment();

                Bundle bundle = new Bundle();
                bundle.putString("idM", tvIdMantenimiento.getText().toString());
                frag.setArguments(bundle);

                FragmentTransaction transaction = ((AppCompatActivity) itemView.getContext())
                        .getSupportFragmentManager()
                        .beginTransaction();

                transaction.replace(R.id.contenedorFragmentos, frag);
                transaction.addToBackStack(null);
                transaction.commit();
            });

        }
    }
}
