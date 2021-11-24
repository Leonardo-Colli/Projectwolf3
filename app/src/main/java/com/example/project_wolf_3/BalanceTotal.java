package com.example.project_wolf_3;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
    double balancetotal;
    String userID,username;
    View fondo;
    View G1,G2,G3,G4,G5,G6;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;
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
        float m = 410;
        fondo.getLayoutParams().height = getPixels(fondo,m);

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
                balance.setText(String.format("$%s", String.format("%,.2f", balancetotal)));
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
    int getPixels(View context, float dp) {
        return (int) (context.getResources().getDisplayMetrics().density * dp + .5f);
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
        Retrofit retrofit = new Retrofit.Builder()
                //.baseUrl("http://10.0.2.2:8080/api/")
                .baseUrl("http://192.168.1.81:8080/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);
        Call<List<Posts>> call = retrofitInterface.find(codigo);
        call.enqueue(new Callback<List<Posts>>() {
            @Override
            public void onResponse(Call<List<Posts>> call, Response<List<Posts>> response) {
                if (!response.isSuccessful()){
                    gananciap.setText("Codigo: "+ response.code());
                    return;
                }
                List<Posts> postsList = response.body();
                for(Posts post: postsList){
                    double ganancias = post.getGananciap();
                    double gananciapor = post.getGanancia();
                    double porcentaje = gananciapor * 100;
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
            case R.id.nav_inversiones:
                Intent inversionesIntent = new Intent(BalanceTotal.this, MainActivity.class);
                startActivity(inversionesIntent);
                break;

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
