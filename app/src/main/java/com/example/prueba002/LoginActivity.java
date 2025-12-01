package com.example.prueba002;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    EditText etUsuario, etContrasena;
    Button btnLogin;
    String URL = "http://172.16.23.167:8001/login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsuario = findViewById(R.id.etUsuario);
        etContrasena = findViewById(R.id.etContrasena);
        btnLogin = findViewById(R.id.btnLogin);


        SharedPreferences prefs = getSharedPreferences("sesion", MODE_PRIVATE);
        if (prefs.contains("usuario")) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        btnLogin.setOnClickListener(v -> login());
    }

    private void login() {
        String usuario = etUsuario.getText().toString().trim();
        String contrasena = etContrasena.getText().toString().trim();

        if (usuario.isEmpty() || contrasena.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject parametros = new JSONObject();
        try {
            parametros.put("usuario", usuario);
            parametros.put("contrasena", contrasena);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                URL,
                parametros,
                response -> {
                    try {
                        String message = response.getString("message");

                        if (message.contains("Inicio de sesión exitoso")) {


                            SharedPreferences prefs = getSharedPreferences("sesion", MODE_PRIVATE);
                            prefs.edit().putString("usuario", usuario).apply();

                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, MainActivity.class));
                            finish();

                        } else {
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        Toast.makeText(this, "Error procesando respuesta", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    String errorMsg = (error.getMessage() != null)
                            ? error.getMessage()
                            : "Error desconocido";
                    Toast.makeText(this, "Error: " + errorMsg, Toast.LENGTH_SHORT).show();
                }
        );

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}
