package com.example.project_wolf_3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project_wolf_3.model.Posts;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TablaEstatus extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    public TextView mJsonText;
    public TextView mJsonTextEstatus;
    public TextView mJsonTextCantidad;
    Button Fondeos, Retiros;

    FirebaseAuth mAuth;
    String userID, fundId, name, username;
    FirebaseFirestore db;

    NavigationView navigationView;
    ImageView menuIcon;
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabla_estatus);
        mJsonText = findViewById(R.id.jsonText);
        mJsonTextEstatus = findViewById(R.id.jsonTextEstatus);
        mJsonTextCantidad = findViewById(R.id.jsonTextCantidad);
        Fondeos = findViewById(R.id.btnFondeos);
        Retiros = findViewById(R.id.btnRetiros);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();

        drawerLayout = findViewById(R.id.menupincipal);
        navigationView = findViewById(R.id.nav_view);
        menuIcon = findViewById(R.id.menu_icon);

        navigationDrawer();
        ImageView profile = findViewById(R.id.button_profile);
        profile.setOnClickListener(view -> {
            Intent colorsIntent = new Intent(TablaEstatus.this, UserProfile.class);
            startActivity(colorsIntent);
        });



        Fondeos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fondeos.setBackgroundColor(Color.parseColor("#BABABA"));
                Retiros.setBackgroundColor(Color.parseColor("#D30F0F"));
                mJsonText.setText("");
                mJsonTextCantidad.setText("");
                mJsonTextEstatus.setText("");
                funduserid();
                //getPosts();
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
                mJsonTextEstatus.setText("");
                funduserid();
                //getPosts();
                Fondeos.setEnabled(true);
                Retiros.setEnabled(false);
            }
        });

    }
    public void funduserid(){
        db.collection("users").document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(@NonNull DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    username = documentSnapshot.getString("username");
                }
                find(username);
            }
        });
    }
    private void find(String codigo){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/api/")
                //.baseUrl("http://192.168.1.81:8080/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);
        Call<List<Posts>> call = retrofitInterface.find(codigo);
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
                    String fecha_user = Fecha.substring(0, Math.min(Fecha.length(), 10))+"\n"+"\n";
                    String Cantidad =post.getAmount()+" MXN"+"\n"+"\n";
                    if(post.getIsConfirmed() == 0){
                        mJsonTextEstatus.append("Pendiente"+"\n"+"\n");
                    }
                    if (post.getIsConfirmed() == 1){
                        mJsonTextEstatus.append("Aprobado"+"\n"+"\n");
                    }
                    mJsonText.append(fecha_user);
                    mJsonTextCantidad.append(Cantidad);

                }
            }

            @Override
            public void onFailure(Call<List<Posts>> call, Throwable t) {
                mJsonText.setText(t.getMessage());
            }
        });
    }
    private void navigationDrawer() {
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_fondos);
        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(drawerLayout.isDrawerVisible(GravityCompat.START)){
                    drawerLayout.closeDrawer(GravityCompat.START);
                }else drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerVisible(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else
            super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_perfil:
                Intent colorsIntent = new Intent(TablaEstatus.this, UserProfile.class);
                startActivity(colorsIntent);
                break;
            case R.id.nav_balance:
                Intent numbersIntent = new Intent(TablaEstatus.this, BalanceTotal.class);
                startActivity(numbersIntent);
                break;
            case R.id.nav_inversiones:
                Intent inversionesIntent = new Intent(TablaEstatus.this, MainActivity.class);
                startActivity(inversionesIntent);
                break;

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}