package com.example.prueba002;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

public class FragmentLogeo extends Fragment {

    EditText L_Usuario, L_contraseña;
    Button L_btnLogin;
    RequestQueue requestQueue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_logeo, container, false);

        L_Usuario = view.findViewById(R.id.L_Usuario);
        L_contraseña = view.findViewById(R.id.L_contraseña);
        L_btnLogin = view.findViewById(R.id.L_btnLogin);

        requestQueue = Volley.newRequestQueue(requireContext());

        L_btnLogin.setOnClickListener(v -> {
            String usuario = L_Usuario.getText().toString().trim();
            String contrasena = L_contraseña.getText().toString().trim();

            if (usuario.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(requireContext(), "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            } else {
                iniciarSesion(usuario, contrasena);
            }
        });

        return view;
    }

    private void iniciarSesion(String usuario, String contrasena) {
        String url = "http://192.168.0.199:8000/Login"; // tu API CodeIgniter

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("usuario", usuario);
            jsonBody.put("contrasena", contrasena);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonBody,
                response -> {
                    try {
                        String status = response.getString("status");

                        if (status.equals("success")) {
                            JSONObject data = response.getJSONObject("data");
                            String tipo = data.getString("tipo");
                            String user = data.getString("usuario");

                            Toast.makeText(requireContext(), "Bienvenido " + user, Toast.LENGTH_SHORT).show();

                            // 🔹 Aquí puedes redirigir según el tipo
                            if (tipo.equals("1")) {
                                // abrir pantalla de admin
                            } else {
                                // abrir pantalla de empleado
                            }

                        } else {
                            Toast.makeText(requireContext(), "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(requireContext(), "Error en la respuesta", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(requireContext(), "Error de conexión: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        );

        requestQueue.add(request);
    }
}
