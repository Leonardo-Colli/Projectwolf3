package com.example.project_wolf_3;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.project_wolf_3.model.Posts;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TablaEstatus extends AppCompatActivity {

    public TextView mJsonText;
    public TextView mJsonTextEstatus;
    public TextView mJsonTextCantidad;
    Button Fondeos, Retiros;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabla_estatus);
        mJsonText = findViewById(R.id.jsonText);
        mJsonTextEstatus = findViewById(R.id.jsonTextEstatus);
        mJsonTextCantidad = findViewById(R.id.jsonTextCantidad);
        Fondeos = findViewById(R.id.btnFondeos);
        Retiros = findViewById(R.id.btnRetiros);
        Fondeos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fondeos.setBackgroundColor(Color.parseColor("#BABABA"));
                Retiros.setBackgroundColor(Color.parseColor("#D30F0F"));
                mJsonText.setText("");
                mJsonTextCantidad.setText("");
                getPosts();
                Fondeos.setEnabled(false);
                Retiros.setEnabled(true);

            }
        });

        Retiros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fondeos.setBackgroundColor(Color.parseColor("#FF018786"));
                Retiros.setBackgroundColor(Color.parseColor("#BABABA"));
                mJsonText.setText("");
                mJsonTextCantidad.setText("");
                getPosts();
                Fondeos.setEnabled(true);
                Retiros.setEnabled(false);
            }
        });

    }
    private void getPosts(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);
        Call<List<Posts>> call = retrofitInterface.getPosts();
        call.enqueue(new Callback<List<Posts>>() {
            @Override
            public void onResponse(Call<List<Posts>> call, Response<List<Posts>> response) {
                if (!response.isSuccessful()){
                    mJsonText.setText("Codigo: "+ response.code());
                    return;
                }
                List<Posts> postsList = response.body();
                for(Posts post: postsList){
                    String Fecha=post.getDate() +"\n";
                    String email_user = Fecha.substring(0, Math.min(Fecha.length(), 10))+"\n"+"\n";
                    String Cantidad =post.getAmount()+"\n"+"\n";
                    mJsonText.append(email_user);
                    mJsonTextCantidad.append(Cantidad);
                }

            }

            @Override
            public void onFailure(Call<List<Posts>> call, Throwable t) {
                mJsonText.setText(t.getMessage());
            }
        });
    }
}