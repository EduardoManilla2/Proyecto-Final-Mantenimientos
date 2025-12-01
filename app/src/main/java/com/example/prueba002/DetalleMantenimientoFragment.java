package com.example.prueba002;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;

public class DetalleMantenimientoFragment extends Fragment {

    TextView idMantenimiento, nombreEquipo, nombreLaboratorio, nombreActividad,
            fechaProx, frecuenciaT, statusT, usuarioR, usuarioS, observaciones;
    private ImageView backButton;
    Button btnRTarea;

    String URL_API = "http://172.16.23.167:8001/getMantenimiento/";
    String idMante;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (getArguments() != null) {
            idMante = getArguments().getString("idM");
        } else {
            Toast.makeText(getContext(), "No se recibió ID de mantenimiento", Toast.LENGTH_SHORT).show();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detalle_mantenimiento, container, false);

        // 🔹 Referencias UI
        idMantenimiento = view.findViewById(R.id.tv_pm_id);
        nombreEquipo = view.findViewById(R.id.td_nombreEquipo);
        nombreLaboratorio = view.findViewById(R.id.td_nombreLaboratorio);
        nombreActividad = view.findViewById(R.id.td_actividades);
        statusT = view.findViewById(R.id.tv_estatuss);

        fechaProx = view.findViewById(R.id.td_fechaProx);
        frecuenciaT = view.findViewById(R.id.td_frecuencia);

        usuarioR = view.findViewById(R.id.td_usuarioR);
        usuarioS = view.findViewById(R.id.td_usuarioS);

        observaciones = view.findViewById(R.id.td_observaciones);
        btnRTarea = view.findViewById(R.id.btn_comenzar_tarea);


        backButton = view.findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            } else if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });


        if (idMante != null) {
            obtenerDatosMantenimiento(Integer.parseInt(idMante));
        }


        btnRTarea.setOnClickListener(v -> {


            if (!btnRTarea.isEnabled()) {
                Toast.makeText(getContext(), "El mantenimiento ya no se puede modificar", Toast.LENGTH_SHORT).show();
                return;
            }


            Bundle bundle = new Bundle();
            bundle.putString("idM", idMantenimiento.getText().toString());
            bundle.putString("idEquipo", nombreEquipo.getText().toString());
            bundle.putString("idLabo", nombreLaboratorio.getText().toString());
            bundle.putString("Observaciones", nombreLaboratorio.getText().toString());
            bundle.putString("UsuarioR", usuarioR.getText().toString());

            RealizarManteFrag fragment = new RealizarManteFrag();
            fragment.setArguments(bundle);

            FragmentTransaction ft = requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction();
            ft.replace(R.id.contenedorFragmentos, fragment);
            ft.addToBackStack(null);
            ft.commit();
        });

        return view;
    }

    private void obtenerDatosMantenimiento(int idMantenimientoValue) {
        String url = URL_API + "?idMantenimiento=" + idMante;

        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        String idMant = response.getString("idMantenimiento");
                        String Status = response.getString("estatus");
                        String fProgramada = response.optString("fechaProx", "No registrada");
                        String obs = response.optString("observaciones", "Sin observaciones");
                        String frecuencia = response.getString("nombreFrecuencia");
                        String equipo = response.getString("nombreEquipo");
                        String laboratorio = response.getString("nombreLabo");

                        String empleadoEncargado = response.optString("empleadoEncargado", "Desconocido");
                        String administradorSupervisa = response.optString("administradorSupervisa", "Desconocido");

                        String actividades = response.optString("actividades", "Sin actividades");

                        idMantenimiento.setText(idMant);
                        nombreEquipo.setText(equipo);
                        nombreLaboratorio.setText(laboratorio);

                        fechaProx.setText(fProgramada);
                        frecuenciaT.setText(frecuencia);


                        if ("Retrasado".equalsIgnoreCase(Status)) {
                            statusT.setText("Retrasado");
                            statusT.setTextColor(0xFFEF3012);
                            statusT.setBackgroundResource(R.drawable.rounded_status_retrazado);

                        } else if ("Pendiente".equalsIgnoreCase(Status)) {
                            statusT.setText("Pendiente");
                            statusT.setTextColor(0xFFEF6712);
                            statusT.setBackgroundResource(R.drawable.rounded_status_pendiente);

                        } else if ("Realizado".equalsIgnoreCase(Status)) {
                            statusT.setText("Realizado");
                            statusT.setTextColor(0xFF00AA00);
                            statusT.setBackgroundResource(R.drawable.rounded_status_realizado);


                            btnRTarea.setEnabled(false);
                            btnRTarea.setAlpha(0.4f);

                        } else if ("Postergado".equalsIgnoreCase(Status)) {
                            statusT.setText("Postergado");
                            statusT.setTextColor(0xFF9C9C27);
                            statusT.setBackgroundResource(R.drawable.rounded_status_postergado);
                        }

                        usuarioR.setText(empleadoEncargado);
                        usuarioS.setText(administradorSupervisa);

                        observaciones.setText(obs);


                        String[] listaActividades = actividades.split(",");

                        StringBuilder listado = new StringBuilder();
                        for (int i = 0; i < listaActividades.length; i++) {
                            listado.append(i + 1).append(".- ").append(listaActividades[i].trim()).append("\n");
                        }

                        nombreActividad.setText(listado.toString());

                    } catch (JSONException e) {
                        Toast.makeText(getContext(), "Error procesando datos", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(getContext(), "Error al obtener el mantenimiento", Toast.LENGTH_SHORT).show()
        );

        queue.add(request);
    }

}
