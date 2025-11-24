package com.example.prueba002;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    // **Asegúrate que esta URL coincida con la base de tu servidor.**
    // La IP debe ser la de tu máquina en la red local (192.168.1.10 en tu caso)
    private static final String BASE_URL = "http://192.168.0.199:8000/";
    private static Retrofit retrofit = null;

    public static ApiService getApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiService.class);
    }
}