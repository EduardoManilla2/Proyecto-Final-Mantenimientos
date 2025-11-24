package com.example.prueba002;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentInicio extends Fragment {

    private CalendarView calendarView;
    private RecyclerView rvMantenimientos;
    private MantenimientoAdapter adapter;
    private TextView tvNoPendientes;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_inicio, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        calendarView = view.findViewById(R.id.calendarMantenimientos);

        rvMantenimientos = view.findViewById(R.id.rv_mantenimientos);
        rvMantenimientos.setLayoutManager(new LinearLayoutManager(getContext()));
        tvNoPendientes = view.findViewById(R.id.tv_no_pendientes);

        adapter = new MantenimientoAdapter(new ArrayList<>());
        rvMantenimientos.setAdapter(adapter);

        // 🔹 Recuperar usuario
        SharedPreferences prefs = requireActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);
        String usuario = prefs.getString("usuario", "Invitado");

        // 🔹 Obtener calendario y mantenimientos
        obtenerFechasCalendario(usuario);
        obtenerMantenimientosPendientes(usuario);
    }

    // =================== CALENDARIO ===================
    private void obtenerFechasCalendario(String usuario) {
        new AsyncTask<String, Void, HashSet<String>>() {
            @Override
            protected HashSet<String> doInBackground(String... params) {
                String usuario = params[0];
                HashSet<String> fechas = new HashSet<>();
                try {
                    URL url = new URL("http://192.168.1.8:8000/getFechasMantenimientos?usuario=" + usuario);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");

                    InputStream in = conn.getInputStream();
                    InputStreamReader reader = new InputStreamReader(in);

                    StringBuilder result = new StringBuilder();
                    int data = reader.read();
                    while (data != -1) {
                        result.append((char) data);
                        data = reader.read();
                    }

                    JSONObject json = new JSONObject(result.toString());
                    JSONArray array = json.getJSONArray("fechas");

                    for (int i = 0; i < array.length(); i++) {
                        fechas.add(array.getString(i)); // yyyy-MM-dd
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return fechas;
            }

            @Override
            protected void onPostExecute(HashSet<String> fechas) {
                super.onPostExecute(fechas);
                mostrarFechasEnCalendario(fechas);
            }
        }.execute(usuario);
    }

    private void mostrarFechasEnCalendario(HashSet<String> fechas) {
        ArrayList<EventDay> eventos = new ArrayList<>();
        for (String fechaStr : fechas) {
            try {
                String fechaSinHora = fechaStr.split(" ")[0];
                String[] parts = fechaSinHora.split("-");

                int year = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]);
                int day = Integer.parseInt(parts[2]);

                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month - 1);
                cal.set(Calendar.DAY_OF_MONTH, day);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);

                eventos.add(new EventDay(cal, R.drawable.circle_bg));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        calendarView.setEvents(eventos);

        calendarView.setOnDayClickListener(eventDay -> {
            Calendar clickedDay = eventDay.getCalendar();
            String fechaSeleccionada = String.format(Locale.getDefault(), "%04d-%02d-%02d",
                    clickedDay.get(Calendar.YEAR),
                    clickedDay.get(Calendar.MONTH) + 1,
                    clickedDay.get(Calendar.DAY_OF_MONTH));

            if (fechas.contains(fechaSeleccionada)) {
                Toast.makeText(getContext(), "Hay mantenimiento(s) este día", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // =================== MANTENIMIENTOS PENDIENTES ===================
    private void obtenerMantenimientosPendientes(String usuario) {
        ApiService apiService = RetrofitClient.getApiService();
        Call<List<Mantenimiento>> call = apiService.getMantenimientos(usuario);

        call.enqueue(new Callback<List<Mantenimiento>>() {
            @Override
            public void onResponse(Call<List<Mantenimiento>> call, Response<List<Mantenimiento>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Mantenimiento> lista = response.body();

                    List<Mantenimiento> filtrados = new ArrayList<>();
                    for (String estatus : new String[]{"Retrasado", "Pendiente"}) {
                        for (Mantenimiento m : lista) {
                            if (m.getEstatus().equalsIgnoreCase(estatus)) {
                                filtrados.add(m);
                                if (filtrados.size() >= 3) break;
                            }
                        }
                        if (filtrados.size() >= 3) break;
                    }

                    if (filtrados.isEmpty()) {
                        tvNoPendientes.setVisibility(View.VISIBLE);
                        rvMantenimientos.setVisibility(View.GONE);
                    } else {
                        tvNoPendientes.setVisibility(View.GONE);
                        rvMantenimientos.setVisibility(View.VISIBLE);
                        adapter.updateData(filtrados);
                    }

                } else {
                    Toast.makeText(getContext(), "Error en la API", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Mantenimiento>> call, Throwable t) {
                Toast.makeText(getContext(), "Fallo en la conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
