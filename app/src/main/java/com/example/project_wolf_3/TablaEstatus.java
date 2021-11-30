package com.example.project_wolf_3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project_wolf_3.model.Posts;
import com.example.project_wolf_3.model.RetirosModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

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
    String userID, username;
    FirebaseFirestore db;
    private FirebaseFirestore firebaseFirestore;

    NavigationView navigationView;
    ImageView menuIcon,profileImage;
    DrawerLayout drawerLayout;
    StorageReference storageReference;
    private RetrofitInterface myApi;

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

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        userID = mAuth.getCurrentUser().getUid();

        profileImage = navigationView.getHeaderView(0).findViewById(R.id.user_image_nav);

        storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference profileRef = storageReference.child("users/"+mAuth.getCurrentUser().getUid()+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage);
            }
        });

        ImageView inversiones = findViewById(R.id.button_profile);
        inversiones.setOnClickListener(view -> {
            Intent colorsIntent = new Intent(TablaEstatus.this, MainActivity.class);
            startActivity(colorsIntent);
        });


        Fondeos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fondeos.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_transacciones_active));
                Fondeos.setTextColor(getResources().getColor(R.color.white));
                Retiros.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_transacciones));
                Retiros.setTextColor(getResources().getColor(R.color.black));
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
                Fondeos.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_transacciones));
                Fondeos.setTextColor(getResources().getColor(R.color.black));
                Retiros.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_transacciones_active));
                Retiros.setTextColor(getResources().getColor(R.color.white));
                mJsonText.setText("");
                mJsonTextCantidad.setText("");
                mJsonTextEstatus.setText("");
                funduseridRetiros();
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
    public void funduseridRetiros(){
        db.collection("users").document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(@NonNull DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    username = documentSnapshot.getString("username");
                }
                findRetiros(username);

            }
        });
    }
    private void findRetiros(String codigo){
        Call<List<RetirosModel>> calls = myApi.retirosfind(codigo);
        calls.enqueue(new Callback<List<RetirosModel>>() {
            @Override
            public void onResponse(Call<List<RetirosModel>> call, Response<List<RetirosModel>> response) {
                if (!response.isSuccessful()){
                    mJsonText.setText("Codigo: "+ response.code());
                    return;
                }
                List<RetirosModel> postsList = response.body();
                for(RetirosModel post: postsList){
                    String Fecha=post.getFecha() +"\n";
                    String fecha_user = Fecha.substring(0, Math.min(Fecha.length(), 10))+"\n"+"\n";
                    String Cantidad =post.getTotal()+" MXN"+"\n"+"\n";
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
            public void onFailure(Call<List<RetirosModel>> call, Throwable t) {

            }
        });
    }
    private void find(String codigo){

        RetrofitInterface retrofit = RetrofitClient.getInstance();
        myApi = retrofit;
        Call<List<Posts>> call = myApi.find(codigo);
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
        navigationView.setCheckedItem(R.id.nav_transaccion);
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
            case R.id.nav_transaccion:
                Intent fondosIntent = new Intent(TablaEstatus.this, TablaEstatus.class);
                startActivity(fondosIntent);
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