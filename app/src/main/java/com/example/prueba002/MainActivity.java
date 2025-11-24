package com.example.prueba002;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // ----------------------------
        // PERMISO DE NOTIFICACIONES
        // ----------------------------
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        1001
                );
            }
        }

        // ----------------------------
        // CARGAR PRIMER FRAGMENTO
        // ----------------------------
        bottomNavigation = findViewById(R.id.bottom_navigation);
        reemplazarFragmento(new FragmentInicio());

        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.btnInicio) {
                reemplazarFragmento(new FragmentInicio());
            } else if (id == R.id.btnPlanes) {
                reemplazarFragmento(new FragmentPlanes());
            } else if (id == R.id.btnConfiguracion) {
                reemplazarFragmento(new FragmentConfiguracion());
            }
            return true;
        });

        // ----------------------------
        // ENVIAR TOKEN CUANDO YA HAY USUARIO LOGEADO
        // ----------------------------
        SharedPreferences prefs = getSharedPreferences("sesion", MODE_PRIVATE);
        String usuario = prefs.getString("usuario", null);

        if (usuario != null) {
            enviarTokenAlEntrar(usuario);
        }
    }

    // ======================================================
    // MÉTODO PARA ENVIAR TOKEN FCM CUANDO ENTRA A MAIN
    // ======================================================
    private void enviarTokenAlEntrar(String usuario) {

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                android.util.Log.e("MAIN_TOKEN", "Error al obtener token", task.getException());
                return;
            }

            String token = task.getResult();
            android.util.Log.d("MAIN_TOKEN", "Token obtenido en MainActivity: " + token);

            // Reutilizamos el método del servicio
            MyFirebaseService service = new MyFirebaseService();
            service.enviarTokenManual(this, usuario, token);
        });
    }

    // ------------------------------------------------------
    private void reemplazarFragmento(Fragment fragmento) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.contenedorFragmentos, fragmento);
        ft.addToBackStack(null);
        ft.commit();
    }
}
