package com.example.prueba002;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentCPass extends Fragment {

    EditText etPassActual, etPassNueva;
    Button btnCambiarPass;
    ApiService apiService;
    String usuario;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_cpass, container, false);


        etPassActual = view.findViewById(R.id.etPassActual);
        etPassNueva = view.findViewById(R.id.etPassNueva);
        btnCambiarPass = view.findViewById(R.id.btnCambiarPass);


        apiService = RetrofitClient.getClient().create(ApiService.class);


        SharedPreferences prefs = requireActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);
        usuario = prefs.getString("usuario", "Invitado");

        btnCambiarPass.setOnClickListener(v -> cambiarPassword());

        return view;
    }

    private void cambiarPassword() {
        String actual = etPassActual.getText().toString().trim();
        String nueva = etPassNueva.getText().toString().trim();

        if (actual.isEmpty() || nueva.isEmpty()) {
            Toast.makeText(getContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        apiService.cambiarPassword(usuario, actual, nueva)
                .enqueue(new Callback<RespuestaServidor>() {
                    @Override
                    public void onResponse(Call<RespuestaServidor> call, Response<RespuestaServidor> response) {
                        if (response.isSuccessful()) {
                            RespuestaServidor r = response.body();

                            if (r.status.equals("success")) {
                                Toast.makeText(getContext(), r.message, Toast.LENGTH_LONG).show();
                                etPassActual.setText("");
                                etPassNueva.setText("");
                            } else {
                                Toast.makeText(getContext(), r.message, Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<RespuestaServidor> call, Throwable t) {
                        Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_LONG).show();
                    }
                });
    }
}
