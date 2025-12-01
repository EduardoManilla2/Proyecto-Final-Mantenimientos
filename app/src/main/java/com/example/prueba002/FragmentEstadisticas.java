package com.example.prueba002;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentEstadisticas extends Fragment {

    BarChart chartMantenimientosMes;
    PieChart chartMantenimientosLab;
    RadarChart chartCumplimientoActividad;

    ApiService apiService;

    String usuario;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_estadisticas, container, false);

        chartMantenimientosMes = view.findViewById(R.id.chartMantenimientosMes);
        chartMantenimientosLab = view.findViewById(R.id.chartMantenimientosLab);
        chartCumplimientoActividad = view.findViewById(R.id.chartCumplimientoActividad);

        apiService = RetrofitClient.getClient().create(ApiService.class);


        SharedPreferences prefs = requireActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);
        usuario = prefs.getString("usuario", "Invitado");


        cargarGraficaMantenimientosMes(usuario);
        cargarGraficaMantenimientosLab(usuario);
        cargarGraficaCumplimientoActividad(usuario);

        return view;
    }


    private void cargarGraficaMantenimientosMes(String usuario) {

        apiService.getMantenimientosMes(usuario).enqueue(new Callback<List<MantenimientosMes>>() {
            @Override
            public void onResponse(Call<List<MantenimientosMes>> call, Response<List<MantenimientosMes>> response) {
                if (!response.isSuccessful()) return;

                List<MantenimientosMes> data = response.body();
                ArrayList<BarEntry> entries = new ArrayList<>();

                for (int i = 0; i < data.size(); i++) {
                    entries.add(new BarEntry(data.get(i).mes, data.get(i).total));
                }

                BarDataSet dataSet = new BarDataSet(entries, "Mantenimientos por mes");
                BarData barData = new BarData(dataSet);

                chartMantenimientosMes.setData(barData);

                final String[] meses = {
                        "", "Ene", "Feb", "Mar", "Abr", "May", "Jun",
                        "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"
                };

                chartMantenimientosMes.getXAxis().setValueFormatter(
                        new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(meses)
                );
                chartMantenimientosMes.getXAxis().setGranularity(1f);
                chartMantenimientosMes.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

                Description desc = new Description();
                desc.setText("");
                chartMantenimientosMes.setDescription(desc);

                chartMantenimientosMes.getAxisLeft().setAxisMinimum(0f);
                chartMantenimientosMes.getAxisRight().setEnabled(false);

                chartMantenimientosMes.invalidate();
            }

            @Override
            public void onFailure(Call<List<MantenimientosMes>> call, Throwable t) { }
        });
    }



    private void cargarGraficaMantenimientosLab(String usuario) {

        apiService.getMantenimientosLab(usuario).enqueue(new Callback<List<MantenimientosLab>>() {
            @Override
            public void onResponse(Call<List<MantenimientosLab>> call, Response<List<MantenimientosLab>> response) {
                if (!response.isSuccessful()) return;

                List<MantenimientosLab> data = response.body();
                ArrayList<PieEntry> entries = new ArrayList<>();

                for (MantenimientosLab item : data) {
                    entries.add(new PieEntry(item.total, item.laboratorio));
                }

                PieDataSet dataSet = new PieDataSet(entries, "-");

                ArrayList<Integer> colors = new ArrayList<>();
                for (int c : ColorTemplate.MATERIAL_COLORS) colors.add(c);
                for (int c : ColorTemplate.COLORFUL_COLORS) colors.add(c);
                for (int c : ColorTemplate.VORDIPLOM_COLORS) colors.add(c);

                dataSet.setColors(colors);
                dataSet.setValueTextSize(14f);

                chartMantenimientosLab.setDrawHoleEnabled(false);
                chartMantenimientosLab.getDescription().setEnabled(false);

                PieData pieData = new PieData(dataSet);
                chartMantenimientosLab.setData(pieData);

                chartMantenimientosLab.invalidate();
            }

            @Override
            public void onFailure(Call<List<MantenimientosLab>> call, Throwable t) { }
        });
    }





    private void cargarGraficaCumplimientoActividad(String usuario) {

        apiService.getCumplimientoActividad(usuario).enqueue(new Callback<List<CumplimientoActividad>>() {
            @Override
            public void onResponse(Call<List<CumplimientoActividad>> call, Response<List<CumplimientoActividad>> response) {
                if (!response.isSuccessful()) return;

                List<CumplimientoActividad> data = response.body();
                ArrayList<RadarEntry> entries = new ArrayList<>();
                ArrayList<String> labels = new ArrayList<>();

                for (CumplimientoActividad item : data) {
                    entries.add(new RadarEntry(item.promedio_cumplimiento));
                    labels.add(item.actividad);
                }

                RadarDataSet dataSet = new RadarDataSet(entries, "Cumplimiento por Actividad");
                dataSet.setValueTextSize(12f);
                dataSet.setDrawFilled(true);

                RadarData radarData = new RadarData(dataSet);
                chartCumplimientoActividad.setData(radarData);

                XAxis xAxis = chartCumplimientoActividad.getXAxis();
                xAxis.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        int index = (int) value % labels.size();
                        return labels.get(index);
                    }
                });

                chartCumplimientoActividad.getDescription().setEnabled(false);
                chartCumplimientoActividad.invalidate();
            }

            @Override
            public void onFailure(Call<List<CumplimientoActividad>> call, Throwable t) { }
        });
    }
}
