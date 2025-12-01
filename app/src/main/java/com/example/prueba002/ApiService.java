// ApiService.java
package com.example.prueba002;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    @GET("getMantenimientos")
    Call<List<Mantenimiento>> getMantenimientos(@Query("usuario") String usuario);

    @GET("estadisticas/mantenimientos-por-mes")
    Call<List<MantenimientosMes>> getMantenimientosMes(@Query("usuario") String usuario);

    @GET("estadisticas/mantenimientos-por-laboratorio")
    Call<List<MantenimientosLab>> getMantenimientosLab(@Query("usuario") String usuario);

    @GET("estadisticas/cumplimiento-por-actividad")
    Call<List<CumplimientoActividad>> getCumplimientoActividad(@Query("usuario") String usuario);


    @GET("getTotalesMantenimientos")
    Call<TotalesResponse> getTotales(@Query("usuario") String usuario);

    @GET("getNombreUsuario")
    Call<NombreResponse> getNombreUsuario(@Query("usuario") String usuario);

    @POST("usuarios/cambiar-password")
    Call<RespuestaServidor> cambiarPassword(
            @Query("usuario") String usuario,
            @Query("password_actual") String actual,
            @Query("password_nueva") String nueva
    );
}
