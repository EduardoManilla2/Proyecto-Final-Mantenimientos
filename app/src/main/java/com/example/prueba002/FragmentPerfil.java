package com.example.prueba002;

import android.content.SharedPreferences;
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
import org.json.JSONObject;

public class FragmentPerfil extends Fragment {

    TextView tvFullName, tvPhoneNumber, tvEmail, tvDateOfBirth;
    public String nombre, apellidoP, apellidoM;
    String URL_API = "http://192.168.0.199:8000/persona/"; // 👈 tu endpoint base (ajusta IP según tu red)

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        // 🔹 Referencias de los TextView
        tvFullName = view.findViewById(R.id.tvFullName);
        tvPhoneNumber = view.findViewById(R.id.tvPhoneNumber);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvDateOfBirth = view.findViewById(R.id.tvDateOfBirth);


        // 🔹 Recuperar usuario guardado en SharedPreferences
        SharedPreferences prefs = requireActivity().getSharedPreferences("sesion", getActivity().MODE_PRIVATE);
        String usuario = prefs.getString("usuario", "Invitado");

        // 🔹 Cargar datos del perfil desde la API
        if (!usuario.equals("Invitado")) {
            obtenerDatosPerfil(usuario);
        } else {
            Toast.makeText(getContext(), "No se encontró usuario en sesión", Toast.LENGTH_SHORT).show();
        }

        // 🔹 Botón atrás
        ImageView backButton = view.findViewById(R.id.back_arrow);
        backButton.setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            } else if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        Button btnActualizar = view.findViewById(R.id.btnActualizar);

        btnActualizar.setOnClickListener(v ->{
            Bundle bundle = new Bundle();
            FragmentAcPerfil facp = new FragmentAcPerfil();

            bundle.putString("nombre", nombre);
            bundle.putString("apellidoP", apellidoP);
            bundle.putString("apellidoM", apellidoM);
            bundle.putString("telefono", tvPhoneNumber.getText().toString());
            bundle.putString("email", tvEmail.getText().toString());
            bundle.putString("date", tvDateOfBirth.getText().toString());

            facp.setArguments(bundle);


            FragmentTransaction ft = requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction();

            ft.replace(R.id.contenedorFragmentos, facp);
            ft.addToBackStack(null);
            ft.commit();
        });





        return view;
    }


    private void obtenerDatosPerfil(String usuario) {
        String url = URL_API + usuario;

        RequestQueue queue = Volley.newRequestQueue(requireContext());
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        // 🔹 Obtener los datos desde la respuesta JSON
                         nombre = response.getString("nombre");
                         apellidoP = response.getString("apellidoPaterno");
                         apellidoM = response.optString("apellidoMaterno", "");
                        String telefono = response.optString("telefono", "Sin número");
                        String correo = response.optString("correoElectronico", "Sin correo");
                        String fechaNac = response.optString("fechaNacimiento", "No registrada");



                        // 🔹 Mostrar datos en la interfaz
                        tvFullName.setText(nombre + " " + apellidoP + " " + apellidoM);
                        tvPhoneNumber.setText(telefono);
                        tvEmail.setText(correo);
                        tvDateOfBirth.setText(fechaNac);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error al procesar los datos", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(getContext(), "Error al obtener datos del perfil", Toast.LENGTH_SHORT).show();
                }
        );

        queue.add(request);
    }
}
