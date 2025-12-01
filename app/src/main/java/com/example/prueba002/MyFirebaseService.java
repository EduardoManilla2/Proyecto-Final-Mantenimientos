package com.example.prueba002;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MyFirebaseService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseService";


    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "Token nuevo FCM: " + token);

        SharedPreferences prefs = getSharedPreferences("sesion", MODE_PRIVATE);
        String usuario = prefs.getString("usuario", null);

        if (usuario == null) {
            Log.w(TAG, "No hay usuario logeado; NO se enviará el token ahora");
            return;
        }

        enviarTokenAlServidor(usuario, token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String title = "Mantenimiento";
        String body = "Tienes una actualización";

        if (remoteMessage.getNotification() != null) {
            title = remoteMessage.getNotification().getTitle();
            body = remoteMessage.getNotification().getBody();
        }


        mostrarNotificacion(title, body);
        guardarNotificacion(getApplicationContext(), title, body);
    }


    public static void enviarTokenManual(Context context, String usuario, String token) {
        new Thread(() -> {
            try {
                URL url = new URL("http://172.16.23.167:8001/saveToken");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                String jsonBody = "{\"usuario\":\"" + usuario + "\",\"token\":\"" + token + "\"}";

                OutputStream os = conn.getOutputStream();
                os.write(jsonBody.getBytes("UTF-8"));
                os.flush();
                os.close();

                Log.d("TOKEN_MANUAL", "Código respuesta: " + conn.getResponseCode());
                conn.disconnect();

            } catch (Exception e) {
                Log.e("TOKEN_MANUAL", "ERROR", e);
            }
        }).start();
    }

    private void enviarTokenAlServidor(String usuario, String token) {
        new Thread(() -> {
            try {
                String params =
                        "usuario=" + URLEncoder.encode(usuario, "UTF-8") +
                                "&token=" + URLEncoder.encode(token, "UTF-8");

                byte[] postData = params.getBytes("UTF-8");

                URL url = new URL("http://172.16.23.167:8001/saveToken");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setFixedLengthStreamingMode(postData.length);

                OutputStream os = conn.getOutputStream();
                os.write(postData);
                os.flush();
                os.close();

                Log.d(TAG, "Token enviado automáticamente. Código: " + conn.getResponseCode());
                conn.disconnect();

            } catch (Exception e) {
                Log.e(TAG, "ERROR enviando token automático", e);
            }
        }).start();
    }

    private void mostrarNotificacion(String titulo, String mensaje) {
        createNotificationChannel();

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, "channel_mantenimiento")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(titulo)
                        .setContentText(mensaje)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true);

        NotificationManagerCompat.from(this)
                .notify((int) System.currentTimeMillis(), builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "channel_mantenimiento",
                    "Notificaciones Mantenimiento",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Canal para alertas de mantenimiento");

            NotificationManager manager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
        }
    }

    public static void enviarTokenDesdeLogin(Context context, String usuario, String token) {
        new Thread(() -> {
            try {
                String urlParameters =
                        "usuario=" + URLEncoder.encode(usuario, "UTF-8") +
                                "&token=" + URLEncoder.encode(token, "UTF-8");

                byte[] postData = urlParameters.getBytes("UTF-8");

                URL url = new URL("http://172.16.23.167:8001/saveToken");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setFixedLengthStreamingMode(postData.length);

                OutputStream os = conn.getOutputStream();
                os.write(postData);
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();
                Log.d("TOKEN_LOGIN", "Token enviado desde login/main: " + responseCode);

                conn.disconnect();

            } catch (Exception e) {
                Log.e("TOKEN_LOGIN", "Error enviando token", e);
            }
        }).start();
    }

    private void guardarNotificacion(Context context, String titulo, String mensaje) {
        SharedPreferences prefs = context.getSharedPreferences("notificaciones", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        String noti1 = prefs.getString("noti1", null);
        String noti2 = prefs.getString("noti2", null);
        String noti3 = prefs.getString("noti3", null);

        String fecha1 = prefs.getString("fecha1", null);
        String fecha2 = prefs.getString("fecha2", null);
        String fecha3 = prefs.getString("fecha3", null);

        String fechaActual = java.text.DateFormat.getDateTimeInstance().format(new java.util.Date());

        editor.putString("noti3", noti2);
        editor.putString("noti2", noti1);
        editor.putString("noti1", titulo + ": " + mensaje);

        editor.putString("fecha3", fecha2);
        editor.putString("fecha2", fecha1);
        editor.putString("fecha1", fechaActual);

        editor.apply();


        context.sendBroadcast(new android.content.Intent("NUEVA_NOTIFICACION"));
    }


}
