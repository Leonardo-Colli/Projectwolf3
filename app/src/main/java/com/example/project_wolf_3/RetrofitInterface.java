package com.example.project_wolf_3;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RetrofitInterface {
    @POST("/buy")
    Call<ValueResult> executeResult(@Body HashMap<String, List<Map<String, Double>>> map);

    @POST("/btc")
    Call<Void> executeBuy(@Body HashMap<String, Double> map);
}
