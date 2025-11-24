package com.example.prueba002;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentPlanes extends Fragment {

    private RecyclerView recyclerView;
    private MantenimientoAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_planes, container, false);

        recyclerView = view.findViewById(R.id.rv_mantenimientos);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new MantenimientoAdapter(new java.util.ArrayList<>());
        recyclerView.setAdapter(adapter);

        // Campo de búsqueda
        EditText etSearch = view.findViewById(R.id.et_search);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        obtenerMantenimientos();

        return view;
    }

    private void obtenerMantenimientos() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);
        String usuario = prefs.getString("usuario", "Invitado");

        ApiService apiService = RetrofitClient.getApiService();
        Call<List<Mantenimiento>> call = apiService.getMantenimientos(usuario);

        call.enqueue(new Callback<List<Mantenimiento>>() {
            @Override
            public void onResponse(Call<List<Mantenimiento>> call, Response<List<Mantenimiento>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Mantenimiento> listaMantenimientos = response.body();
                    adapter.updateData(listaMantenimientos); // 🔹 Actualiza datos sin perder referencia
                } else {
                    Toast.makeText(getContext(), "Error en la respuesta de la API", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Mantenimiento>> call, Throwable t) {
                Toast.makeText(getContext(), "Fallo en la conexión", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        obtenerMantenimientos();
    }
}
