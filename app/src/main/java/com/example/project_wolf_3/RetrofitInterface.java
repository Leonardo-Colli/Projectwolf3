package com.example.project_wolf_3;

import com.example.project_wolf_3.model.PrecioRespuesta;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RetrofitInterface {
   // @POST("/buy")
    //Call<ValueResult> executeResult(@Body HashMap<String, List<Map<String, Double>>> map);

  //  @POST("/btc")
   // Call<Void> executeBuy(@Body HashMap<String, Double> map);

    @GET("precios")
    Call<PrecioRespuesta> obtenerListaPrecio();
}
