package com.example.prueba002;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActividadAdapter extends RecyclerView.Adapter<ActividadAdapter.ViewHolder> {

    private List<Actividad> lista;
    private Context context;

    public ActividadAdapter(List<Actividad> lista, Context context) {
        this.lista = lista;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombreAct;
        CheckBox cb1, cb2, cb3, cb4, cb5;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreAct = itemView.findViewById(R.id.tvNombreAct);
            cb1 = itemView.findViewById(R.id.cb1);
            cb2 = itemView.findViewById(R.id.cb2);
            cb3 = itemView.findViewById(R.id.cb3);
            cb4 = itemView.findViewById(R.id.cb4);
            cb5 = itemView.findViewById(R.id.cb5);
        }
    }

    @NonNull
    @Override
    public ActividadAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_actividad, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActividadAdapter.ViewHolder holder, int position) {
        Actividad act = lista.get(position);
        holder.nombreAct.setText(act.getNombre());

        // Limpiar listeners antes de setear estados
        holder.cb1.setOnCheckedChangeListener(null);
        holder.cb2.setOnCheckedChangeListener(null);
        holder.cb3.setOnCheckedChangeListener(null);
        holder.cb4.setOnCheckedChangeListener(null);
        holder.cb5.setOnCheckedChangeListener(null);

        // Marcar el CheckBox correspondiente al cumplimiento
        holder.cb1.setChecked(act.getCumplimiento() == 1);
        holder.cb2.setChecked(act.getCumplimiento() == 2);
        holder.cb3.setChecked(act.getCumplimiento() == 3);
        holder.cb4.setChecked(act.getCumplimiento() == 4);
        holder.cb5.setChecked(act.getCumplimiento() == 5);

        // Listener para cada CheckBox
        holder.cb1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) actualizarCumplimiento(holder, act, 1);
        });
        holder.cb2.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) actualizarCumplimiento(holder, act, 2);
        });
        holder.cb3.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) actualizarCumplimiento(holder, act, 3);
        });
        holder.cb4.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) actualizarCumplimiento(holder, act, 4);
        });
        holder.cb5.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) actualizarCumplimiento(holder, act, 5);
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }


    private void actualizarCumplimiento(ViewHolder holder, Actividad act, int valor) {
        // Desmarcar todos los demás CheckBoxes
        holder.cb1.setChecked(valor == 1);
        holder.cb2.setChecked(valor == 2);
        holder.cb3.setChecked(valor == 3);
        holder.cb4.setChecked(valor == 4);
        holder.cb5.setChecked(valor == 5);

        act.setCumplimiento(valor); // actualizar localmente

        // Enviar PUT al backend
        String url = "http://192.168.0.199:8000/putActividad/" + act.getIdManteAct();


        JSONObject json = new JSONObject();
        try {
            json.put("cumplimiento", valor);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, json,
                response -> Toast.makeText(context, "Actividad actualizada", Toast.LENGTH_SHORT).show(),
                error -> {
                    Toast.makeText(context, "Error al actualizar: " + error.toString(), Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }

            @Override
            public byte[] getBody() {
                return json.toString().getBytes(StandardCharsets.UTF_8);
            }
        };


        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }

}
