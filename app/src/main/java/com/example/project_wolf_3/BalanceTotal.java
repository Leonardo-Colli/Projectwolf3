package com.example.project_wolf_3;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_wolf_3.model.Posts;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;


import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.models.BarModel;

import java.util.ArrayList;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BalanceTotal extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    NavigationView navigationView;
    ImageView menuIcon,flechaA,flechaD;
    ImageView profileImage;
    DrawerLayout drawerLayout;
    StorageReference storageReference;
    TextView balance,gananciap,newsaving;
    public double balancetotal;
    public double Balance, preciobtc;
    public float BalanceF[] = new float[7];
    public float valorb;
    String userID,username;
    View fondo;
    View G1,G2,G3,G4,G5,G6;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;
    private RetrofitInterface myApi;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.balance_total);
        ImageView profile = findViewById(R.id.profileimg);
        balance = findViewById(R.id.user_balance);
        gananciap = findViewById(R.id.txtGanancia);
        newsaving = findViewById(R.id.new_saving);
        flechaA = findViewById(R.id.flecha_aumento);
        flechaD = findViewById(R.id.flecha_perdida);
        fondo = findViewById(R.id.rectangle_23_ek4);

        drawerLayout = findViewById(R.id.menupincipal);
        navigationView = findViewById(R.id.nav_view);
        menuIcon = findViewById(R.id.menu_icon);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        navigationDrawer();
        flechaD.setVisibility(View.INVISIBLE);
        flechaA.setVisibility(View.INVISIBLE);

        RetrofitInterface retrofit = RetrofitClient.getInstance();
        myApi = retrofit;

        preciobtc = 57441.2700 *20;
        profileImage = navigationView.getHeaderView(0).findViewById(R.id.user_image_nav);

        storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference profileRef = storageReference.child("users/"+mAuth.getCurrentUser().getUid()+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage);
            }
        });
        DocumentReference documentReference = firebaseFirestore.collection("users").document(userID);
        ListenerRegistration listenerRegistration = documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                balancetotal = value.getDouble("balance");
                //balance.setText(String.format("$%s", String.format("%,.2f", balancetotal)));
                funduserid();

            }
        });

        profile.setOnClickListener(view -> {
            Intent colorsIntent = new Intent(BalanceTotal.this, MainActivity.class);
            startActivity(colorsIntent);
        });
        newsaving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BalanceTotal.this, NewSaving.class);
                startActivity(intent);
            }
        });

    }

    private void navigationDrawer() {
        navigationView.bringToFront();
        // navigationView.setBackgroundColor(getResources().getColor(R.color.Fondo_menu));
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_balance);

        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(drawerLayout.isDrawerVisible(GravityCompat.START)){
                    drawerLayout.closeDrawer(GravityCompat.START);
                }else drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }
    public void funduserid(){
        firebaseFirestore.collection("users").document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
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

        Call<List<Posts>> call = myApi.find(codigo);
        call.enqueue(new Callback<List<Posts>>() {
            @Override
            public void onResponse(Call<List<Posts>> call, Response<List<Posts>> response) {
                if (!response.isSuccessful()){
                    gananciap.setText("Codigo: "+ response.code());
                    return;
                }
                List<Posts> postsList = response.body();
                int j = 1;
                for(Posts post: postsList){
                        double ganancias = post.getGananciap();
                        double gananciapor = post.getGanancia();
                        double porcentaje = gananciapor * 100;
                        valorb = post.getValorbtc();
                        Balance = Balance + post.getAmount();
                        BalanceF[j] = (float) (valorb*preciobtc);
                        j++;
                        gananciap.setText(String.format("$%s", String.format("%,.2f", ganancias)) + "   "+"(% "+String.format("%s", String.format("%,.2f", porcentaje))+")");
                        if (ganancias<0){
                            flechaA.setVisibility(View.INVISIBLE);
                            flechaD.setVisibility(View.VISIBLE);
                        }else{
                            if(ganancias>0){
                                flechaA.setVisibility(View.VISIBLE);
                                flechaD.setVisibility(View.GONE);
                            }
                        }
                }
                balance.setText(String.format("$%s", String.format("%,.2f", Balance)));
                BarChart mBarChart = (BarChart) findViewById(R.id.barGraph);
                if(Balance == 0){
                    mBarChart.addBar(new BarModel("1",2.3f, 0xFFEFEFEF));
                    mBarChart.addBar(new BarModel("2",1.5f,  0xFFEFEFEF));
                    mBarChart.addBar(new BarModel("3",3.3f, 0xFFEFEFEF));
                    mBarChart.addBar(new BarModel("4",1.1f, 0xFFEFEFEF));
                    mBarChart.addBar(new BarModel("5",2.7f, 0xFFEFEFEF));
                    mBarChart.addBar(new BarModel("6",2.f,  0xFFEFEFEF));
                    mBarChart.addBar(new BarModel("7",0.4f, 0xFFEFEFEF));
                    mBarChart.setShowValues(false);
                    mBarChart.startAnimation();
                }
                for (int i=1; i<BalanceF.length;i++){
                    if (BalanceF[i] != 0){
                        mBarChart.addBar(new BarModel(""+i,BalanceF[i], 0xFF050212));
                    }
                }
                mBarChart.startAnimation();

                //Toast.makeText(BalanceTotal.this, ""+Balance, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<List<Posts>> call, Throwable t) {
                gananciap.setText(t.getMessage());
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
                Intent colorsIntent = new Intent(BalanceTotal.this, UserProfile.class);
                startActivity(colorsIntent);
                break;
            case R.id.nav_balance:
                Intent numbersIntent = new Intent(BalanceTotal.this, BalanceTotal.class);
                startActivity(numbersIntent);
                break;
            case R.id.nav_transaccion:
                Intent fondosIntent = new Intent(BalanceTotal.this, TablaEstatus.class);
                startActivity(fondosIntent);
                break;
            case R.id.nav_inversiones:
                Intent inversionesIntent = new Intent(BalanceTotal.this, MainActivity.class);
                startActivity(inversionesIntent);
                break;

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
