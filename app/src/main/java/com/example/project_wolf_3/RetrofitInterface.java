package com.example.project_wolf_3;

import com.example.project_wolf_3.model.Post;
import com.example.project_wolf_3.model.Posts;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Body;
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
                           @Field("status") String status);


    @GET("transactions/{id}")
    public Call<List<Posts>> find(@Path("id") String id);
}
