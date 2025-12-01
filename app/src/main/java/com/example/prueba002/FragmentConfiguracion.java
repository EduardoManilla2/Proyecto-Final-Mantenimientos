package com.example.prueba002;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialog; // Importante para el diálogo

import org.json.JSONObject;

public class FragmentConfiguracion extends Fragment {

    TextView tvBienvenida;
    String URL_LOGOUT = "http://172.16.23.167:8001/logout/";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_configuracion, container, false);

        tvBienvenida = view.findViewById(R.id.tvBienvenida);
        LinearLayout llOptionLogout = view.findViewById(R.id.llOptionLogout);

        SharedPreferences prefs = requireActivity().getSharedPreferences("sesion", getActivity().MODE_PRIVATE);
        String usuario = prefs.getString("usuario", "Invitado");
        tvBienvenida.setText("Bienvenido, " + usuario);


        llOptionLogout.setOnClickListener(v -> {
            showLogoutDialog(usuario, prefs);
        });

        return view;
    }


    private void showLogoutDialog(String usuario, SharedPreferences prefs) {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());


        View bottomSheetView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_logout, null);


        bottomSheetDialog.setContentView(bottomSheetView);


        Button btnCancel = bottomSheetView.findViewById(R.id.btnCancelLogout);
        Button btnConfirm = bottomSheetView.findViewById(R.id.btnConfirmLogout);


        btnCancel.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
        });


        btnConfirm.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            performLogout(usuario, prefs);
        });


        bottomSheetDialog.show();
    }


    private void performLogout(String usuario, SharedPreferences prefs) {
        if (usuario != null && !usuario.equals("Invitado")) {
            String urlLogout = URL_LOGOUT + usuario;

            StringRequest request = new StringRequest(
                    Request.Method.POST,
                    urlLogout,
                    response -> {

                        prefs.edit().clear().apply();
                        startActivity(new Intent(requireActivity(), LoginActivity.class));
                        requireActivity().finish();
                    },
                    error -> {

                        String errorMsg = "Error al cerrar sesión";
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            try {
                                String responseBody = new String(error.networkResponse.data, "utf-8");
                                JSONObject data = new JSONObject(responseBody);
                                if (data.has("detail")) {
                                    errorMsg = data.getString("detail");
                                }
                            } catch (Exception e) {
                                errorMsg = "Error al procesar la respuesta";
                            }
                        }
                        Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show();
                    }
            );

            RequestQueue queue = Volley.newRequestQueue(requireContext());
            queue.add(request);
        } else {

            startActivity(new Intent(requireActivity(), LoginActivity.class));
            requireActivity().finish();
        }
    }


    private void setTouchAnimation(View view) {
        view.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.animate().scaleX(0.97f).scaleY(0.97f).setDuration(100).start();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                    break;
            }
            return false;
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        LinearLayout llOptionPerfil = view.findViewById(R.id.llOptionPerfil);
        LinearLayout llOptionPoliticas = view.findViewById(R.id.llOptionPoliticas);
        LinearLayout llOptionConfiguracion = view.findViewById(R.id.llOptionConfiguracion);
        LinearLayout llOptionAyuda = view.findViewById(R.id.llOptionAyuda);
        LinearLayout llOptionLogout = view.findViewById(R.id.llOptionLogout);


        setTouchAnimation(llOptionPerfil);
        setTouchAnimation(llOptionPoliticas);
        setTouchAnimation(llOptionConfiguracion);
        setTouchAnimation(llOptionAyuda);
        setTouchAnimation(llOptionLogout);

        llOptionPerfil.setOnClickListener(v ->{
            FragmentTransaction ft = requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction();
            ft.replace(R.id.contenedorFragmentos, new FragmentPerfil());
            ft.addToBackStack(null);
            ft.commit();
        });


        llOptionPoliticas.setOnClickListener(v ->{
            FragmentTransaction ft = requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction();
            ft.replace(R.id.contenedorFragmentos, new FragmentPoliticas());
            ft.addToBackStack(null);
            ft.commit();
        });

        llOptionAyuda.setOnClickListener(v ->{
            FragmentTransaction ft = requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction();
            ft.replace(R.id.contenedorFragmentos, new FragmentAyuda());
            ft.addToBackStack(null);
            ft.commit();
        });


        llOptionConfiguracion.setOnClickListener(v ->{
            FragmentTransaction ft = requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction();
            ft.replace(R.id.contenedorFragmentos, new FragmentCPass());
            ft.addToBackStack(null);
            ft.commit();
        });

    }
}