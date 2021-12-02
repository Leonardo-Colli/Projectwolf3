package com.example.project_wolf_3;

import com.example.project_wolf_3.model.ComisionesModel;
import com.example.project_wolf_3.model.EstrategiasModel;
import com.example.project_wolf_3.model.Post;
import com.example.project_wolf_3.model.Posts;
import com.example.project_wolf_3.model.RetirosModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RetrofitInterface {


    @POST("transactions")
    @FormUrlEncoded
    Call<Post> createPost (@Field("name") String name,
                           @Field("user") String user,
                           @Field("price") Double price,
                           @Field("amount") Double amount,
                           @Field("installments") int installments,
                           @Field("transactionid") String transactionid,
                           @Field("isConfirmed") int status,
                           @Field("firebaseSavingId") String firebaseSavingId,
                           @Field("bandera") int bandera);



    @GET("transactions/{id}")
    public Call<List<Posts>> find(@Path("id") String id);
    @GET("retiros/{id}")
    public Call<List<RetirosModel>> retirosfind(@Path("id") String id);

    @GET("comisiones/{id}")
    public Call<List<ComisionesModel>> comisionesfind(@Path("id") String id);


    @POST("retiros")
    @FormUrlEncoded
    Call<RetirosModel> retirosPost(@Field("user") String user,
                                   @Field("nombre") String nombre,
                                   @Field("correo") String correo,
                                   @Field("banco") String banco,
                                   @Field("clave") String clave,
                                   @Field("monto") double monto,
                                   @Field("total") double total);
    @POST("estrategias")
    @FormUrlEncoded
    Call<EstrategiasModel>  estrategiaPost(@Field("stoploss") float stoploss);
}
