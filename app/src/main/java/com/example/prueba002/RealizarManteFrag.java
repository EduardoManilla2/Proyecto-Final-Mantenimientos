package com.example.prueba002;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class RealizarManteFrag extends Fragment {

    private RecyclerView recyclerView;
    private ActividadAdapter adapter;
    private ArrayList<Actividad> listaActividades;
    private String idMantenimiento;

    private TextView RidM, fechaActu, nombreEquipo, usuarioR, nombreLaboratorio;
    private EditText observaciones_finales;
    private Button btnFinalizar;
  private Button confirmar_finalizar;
    private ImageView backButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_realizar_mante, container, false);

        recyclerView = view.findViewById(R.id.recyclerActividades);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        RidM = view.findViewById(R.id.R_RidM);
        fechaActu = view.findViewById(R.id.R_fechaReal);
        nombreEquipo = view.findViewById(R.id.R_nombreEquipo);
        usuarioR = view.findViewById(R.id.R_usuarioR);
        nombreLaboratorio = view.findViewById(R.id.R_nombreLaboratorio);
        observaciones_finales = view.findViewById(R.id.R_observaciones_finales);
        btnFinalizar = view.findViewById(R.id.btn_finalizar_registrar);


        String fechaFormateada = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        fechaActu.setText(fechaFormateada);


        if (getArguments() != null) {
            idMantenimiento = getArguments().getString("idM");
            String idEquipo = getArguments().getString("idEquipo");
            String idLabo = getArguments().getString("idLabo");
            String observaciones = getArguments().getString("Observaciones");
            String responsable = getArguments().getString("UsuarioR");

            RidM.setText(idMantenimiento);
            nombreEquipo.setText(idEquipo);
            nombreLaboratorio.setText(idLabo);
            usuarioR.setText(responsable);
            observaciones_finales.setText(observaciones);
        }

        listaActividades = new ArrayList<>();
        adapter = new ActividadAdapter(listaActividades, getContext());
        recyclerView.setAdapter(adapter);

        if (idMantenimiento != null) {
            cargarActividades(idMantenimiento);
        } else {
            Toast.makeText(getContext(), "ID de mantenimiento no recibido", Toast.LENGTH_SHORT).show();
        }


        btnFinalizar.setOnClickListener(v -> mostrarDialogoFinalizar());


        backButton = view.findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                    getParentFragmentManager().popBackStack();
                } else {

                    if (getActivity() != null) {
                        getActivity().onBackPressed();
                    }
                }
            }
        });



        return view;
    }

    private void cargarActividades(String idMante) {
        String url = "http://172.16.23.167:8001/getManteActi/" + idMante;

        JsonArrayRequest request = new JsonArrayRequest(url,
                response -> {
                    listaActividades.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            int idManteAct = obj.getInt("idManteAct");
                            String nombreAct = obj.getString("nombreAct");
                            int cumplimiento = obj.getInt("cumplimiento");
                            listaActividades.add(new Actividad(idManteAct, nombreAct, cumplimiento));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    adapter.notifyDataSetChanged();
                },
                error -> Toast.makeText(getContext(), "Error al cargar actividades", Toast.LENGTH_SHORT).show()
        );

        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }

    private void mostrarDialogoFinalizar() {
        View customView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_finalizar, null);

        MaterialButton btnConfirmar = customView.findViewById(R.id.btn_confirmar_finalizar);
        MaterialButton btnCancelar = customView.findViewById(R.id.btn_cancelar_finalizar);

        final androidx.appcompat.app.AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setView(customView)
                .setCancelable(false)
                .create();

        btnConfirmar.setOnClickListener(v -> {
            dialog.dismiss();
            finalizarMantenimiento();
        });

        btnCancelar.setOnClickListener(v -> dialog.dismiss());



        dialog.show();
    }

    private void finalizarMantenimiento() {

        String nuevoEstatus = "Realizado";

        String nuevasObs = observaciones_finales.getText().toString().trim();
        actualizarObservaciones(nuevasObs, nuevoEstatus);
    }


    private void actualizarObservaciones(String obs, String estatus) {
        String url = "http://172.16.23.167:8001/putObservaciones/" + idMantenimiento;

        JSONObject json = new JSONObject();
        try {
            json.put("observaciones", obs);
        } catch (JSONException e) {
            e.printStackTrace();
        }

       JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, json,
                response -> actualizarEstatus(estatus),
              error -> Toast.makeText(getContext(), "Error al actualizar observaciones", Toast.LENGTH_SHORT).show()
       );

      Volley.newRequestQueue(getContext()).add(request);
    }

    private void actualizarEstatus(String estatus) {
        String url = "http://172.16.23.167:8001/putEstatus/" + idMantenimiento;

        JSONObject json = new JSONObject();
        try {
            json.put("estatus", estatus);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, json,
                response -> {
                    Toast.makeText(getContext(), "Mantenimiento " + estatus, Toast.LENGTH_LONG).show();
                    irADetalles();
                },
                error -> Toast.makeText(getContext(), "Error al actualizar estatus", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(getContext()).add(request);
    }

    private void irADetalles() {

        Bundle result = new Bundle();
        result.putBoolean("actualizar", true);

        getParentFragmentManager().setFragmentResult("actualizacion_mantenimiento", result);


        if (getParentFragmentManager().getBackStackEntryCount() > 0) {
            getParentFragmentManager().popBackStack();
        } else if (getActivity() != null) {
            getActivity().onBackPressed();
        }
    }


}
