// ApiService.java
package com.example.prueba002;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("getMantenimientos")
    Call<List<Mantenimiento>> getMantenimientos(@Query("usuario") String usuario);
}
