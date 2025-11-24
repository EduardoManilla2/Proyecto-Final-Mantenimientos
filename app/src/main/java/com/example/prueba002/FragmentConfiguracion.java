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

    TextView tvBienvenida; // Ya no necesitas 'logoutT' para el click
    String URL_LOGOUT = "http://192.168.0.199:8000/logout/";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_configuracion, container, false);

        tvBienvenida = view.findViewById(R.id.tvBienvenida);
        LinearLayout llOptionLogout = view.findViewById(R.id.llOptionLogout);

        SharedPreferences prefs = requireActivity().getSharedPreferences("sesion", getActivity().MODE_PRIVATE);
        String usuario = prefs.getString("usuario", "Invitado");
        tvBienvenida.setText("Bienvenido, " + usuario);

        // Al hacer clic en la opción de Logout, mostramos el diálogo de confirmación
        llOptionLogout.setOnClickListener(v -> {
            showLogoutDialog(usuario, prefs);
        });

        // **IMPORTANTE**: Si el TextView 'logout' (que tenía ID 'logout') tenía un click listener
        // directo en tu XML o lo manejabas por separado, es mejor usar solo el click del LinearLayout.
        // Si lo necesitas, descomenta y adapta esta parte.

        return view;
    }

    /**
     * Muestra el Bottom Sheet Dialog para confirmar el cierre de sesión.
     * @param usuario El nombre del usuario logeado.
     * @param prefs Las SharedPreferences de la sesión.
     */
    private void showLogoutDialog(String usuario, SharedPreferences prefs) {
        // 1. Crear el Bottom Sheet Dialog
        // Usamos requireContext() porque estamos en un Fragment
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());

        // 2. Inflar el layout personalizado (dialog_logout.xml)
        View bottomSheetView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_logout, null);

        // 3. Asignar el layout al diálogo
        bottomSheetDialog.setContentView(bottomSheetView);

        // 4. Referencias a los botones del diálogo
        Button btnCancel = bottomSheetView.findViewById(R.id.btnCancelLogout);
        Button btnConfirm = bottomSheetView.findViewById(R.id.btnConfirmLogout);

        // 5. Asignar OnClickListener a CANCELAR
        btnCancel.setOnClickListener(v -> {
            bottomSheetDialog.dismiss(); // Cierra el diálogo
        });

        // 6. Asignar OnClickListener a CONFIRMAR (Sí, Cerrar Sesión)
        btnConfirm.setOnClickListener(v -> {
            bottomSheetDialog.dismiss(); // Cierra el diálogo antes de iniciar la operación
            performLogout(usuario, prefs); // Llama a la lógica de cierre de sesión
        });

        // 7. Mostrar el diálogo
        bottomSheetDialog.show();
    }

    /**
     * Contiene la lógica para realizar la petición de cierre de sesión y redirigir.
     * @param usuario El nombre del usuario logeado.
     * @param prefs Las SharedPreferences de la sesión.
     */
    private void performLogout(String usuario, SharedPreferences prefs) {
        if (usuario != null && !usuario.equals("Invitado")) {
            String urlLogout = URL_LOGOUT + usuario;

            StringRequest request = new StringRequest(
                    Request.Method.POST,
                    urlLogout,
                    response -> {
                        // Éxito al cerrar sesión en el servidor
                        prefs.edit().clear().apply();
                        startActivity(new Intent(requireActivity(), LoginActivity.class));
                        requireActivity().finish();
                    },
                    error -> {
                        // Error al cerrar sesión en el servidor
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
            // Si es invitado, simplemente vamos al login
            startActivity(new Intent(requireActivity(), LoginActivity.class));
            requireActivity().finish();
        }
    }

    // --- Animación Táctil (sin cambios) ---
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
            return false; // dejar que el click normal siga funcionando
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Referencias a tus LinearLayouts
        LinearLayout llOptionPerfil = view.findViewById(R.id.llOptionPerfil);
        LinearLayout llOptionPoliticas = view.findViewById(R.id.llOptionPoliticas);
        LinearLayout llOptionConfiguracion = view.findViewById(R.id.llOptionConfiguracion);
        LinearLayout llOptionAyuda = view.findViewById(R.id.llOptionAyuda);
        LinearLayout llOptionLogout = view.findViewById(R.id.llOptionLogout);

        // Aplica la animación táctil
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


    }
}