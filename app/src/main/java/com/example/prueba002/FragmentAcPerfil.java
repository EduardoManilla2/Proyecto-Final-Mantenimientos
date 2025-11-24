package com.example.prueba002;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

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

import org.json.JSONObject;

import java.util.Calendar;


public class FragmentAcPerfil extends Fragment {

    TextView etNameC, etApPC, etApMC, etPhoneNumberC, etEmailC, etDateOfBirthC;
    Button btnCActualizarC;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view  = inflater.inflate(R.layout.fragment_acperfil, container, false);

        etNameC = view.findViewById(R.id.etName);
        etApPC = view.findViewById(R.id.etApP);
        etApMC = view.findViewById(R.id.etApM);
        etPhoneNumberC = view.findViewById(R.id.etPhoneNumber);
        etEmailC = view.findViewById(R.id.etEmail);
        etDateOfBirthC = view.findViewById(R.id.etDateOfBirth);
        btnCActualizarC = view.findViewById(R.id.btnCActualizar);




        if (getArguments() != null) {
            String nom = getArguments().getString("nombre");
            etNameC.setText(nom);

            String aP = getArguments().getString("apellidoP");
            etApPC.setText(aP);

            String aM = getArguments().getString("apellidoM");
            etApMC.setText(aM);

            String telefono = getArguments().getString("telefono");
            etPhoneNumberC.setText(telefono);

            String email = getArguments().getString("email");
            etEmailC.setText(email);

            String date = getArguments().getString("date");
            etDateOfBirthC.setText(date);
        }


        etDateOfBirthC.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireContext(),
                    (view1, yearSelected, monthSelected, daySelected) -> {
                        // Mes empieza desde 0
                        monthSelected = monthSelected + 1;

                        // Formato AAAA-MM-DD
                        String fecha = yearSelected + "-" +
                                (monthSelected < 10 ? "0" + monthSelected : monthSelected) + "-" +
                                (daySelected < 10 ? "0" + daySelected : daySelected);

                        etDateOfBirthC.setText(fecha);
                    },
                    year, month, day
            );

            datePickerDialog.show();
        });


        ImageView backButton = view.findViewById(R.id.back_arrow);
        backButton.setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            } else if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        btnCActualizarC.setOnClickListener(v -> {

            // Obtener usuario desde SharedPreferences
            SharedPreferences prefs = requireActivity().getSharedPreferences("sesion", getActivity().MODE_PRIVATE);
            String usuario = prefs.getString("usuario", null);

            if (usuario == null) {
                Toast.makeText(getContext(), "Usuario no encontrado en sesión", Toast.LENGTH_SHORT).show();
                return;
            }

            // Obtener los datos escritos en los TextView
            String nombre = etNameC.getText().toString().trim();
            String apP = etApPC.getText().toString().trim();
            String apM = etApMC.getText().toString().trim();
            String telefono = etPhoneNumberC.getText().toString().trim();
            String email = etEmailC.getText().toString().trim();
            String fechaNac = etDateOfBirthC.getText().toString().trim();

            // Crear JSON a enviar
            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("nombre", nombre);
                jsonBody.put("apellidoPaterno", apP);
                jsonBody.put("apellidoMaterno", apM);
                jsonBody.put("telefono", telefono);
                jsonBody.put("correoElectronico", email);
                jsonBody.put("fechaNacimiento", fechaNac);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Error al crear JSON", Toast.LENGTH_SHORT).show();
                return;
            }

            // URL
            String url = "http://192.168.0.199:8000/persona/" + usuario;

            // Enviar PUT con Volley
            RequestQueue queue = Volley.newRequestQueue(requireContext());

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.PUT,
                    url,
                    jsonBody,
                    response -> {
                        Toast.makeText(getContext(), "Datos actualizados correctamente", Toast.LENGTH_LONG).show();

                        // Crear instancia del FragmentPerfil
                        FragmentPerfil fragmentPerfil = new FragmentPerfil();

                        // Reemplazar el fragment actual con FragmentPerfil
                        requireActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.contenedorFragmentos, fragmentPerfil) // asegurarte que este es tu contenedor
                                .commit();
                    },
                    error -> {
                        error.printStackTrace();
                        Toast.makeText(getContext(), "Error al actualizar datos", Toast.LENGTH_LONG).show();
                    }
            );

            queue.add(request);
        });



        return view;
    }
}