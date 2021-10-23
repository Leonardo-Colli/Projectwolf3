package com.example.project_wolf_3;

import com.example.project_wolf_3.model.PrecioRespuesta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface RetrofitInterface {
    @POST("transactions")
    @FormUrlEncoded
    Observable<String> registroDatos (@Field("name") String name,
                              @Field("user") String user,
                              @Field("price") Float price,
                              @Field("amount") Float amount,
                              @Field("installments") int installments,
                              @Field("transactionid") String transactionid);


  //  @POST("/btc")
   // Call<Void> executeBuy(@Body HashMap<String, Double> map);

    @GET("precios")
    Call<PrecioRespuesta> obtenerListaPrecio();
}
