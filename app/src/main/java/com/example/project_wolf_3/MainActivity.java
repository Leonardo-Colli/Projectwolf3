package com.example.project_wolf_3;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_wolf_3.model.FundModel;
import com.example.project_wolf_3.model.InvModel;
import com.example.project_wolf_3.model.Posts;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, RecyclerAdapter.RecyclerItemClick {


    private static final int EARTHQUAKE_LOADER_ID = 1;

    private static final String USGS_REQUEST_URL = "https://api.bitso.com/v3/ticker/";
    String bitsoKey = "whkjNlbaki";
    String bitsoSecret = "89fceabf4e7e86ba7cbf22a9977beb9f";
    long nonce = System.currentTimeMillis();
    String HTTPMethod = "GET";
    String RequestPath = "/v3/ticker/";
    String JSONPayload = "";
    Date pos_fund;
    TextView Balance;
    TextView fullname;

    ImageView profileImage;
    ImageView menuIcon;
    NavigationView navigationView;
    DrawerLayout drawerLayout;

    String userID;
    String username;
    //FundAdapter adapter;
    StorageReference storageReference;
    FirebaseAuth fAuth;
    FirebaseFirestore db;
   // private RecyclerView mFirestoreList;
    private RecyclerView rvLista;
    private RetrofitClient retrofitClient;

    private List<Posts> items;
    private RecyclerAdapter adapter;
    private RetrofitInterface retrofitApiService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ProgressBar progressBar = findViewById(R.id.loading_indicator);
        TextView mEmptyStateTextView = findViewById(R.id.empty_view);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        drawerLayout = findViewById(R.id.menupincipal);
        navigationView = findViewById(R.id.nav_view);
        menuIcon = findViewById(R.id.menu_icon);

        //toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        navigationDrawer();
        rvLista = findViewById(R.id.list);
        retrofitApiService = RetrofitClient.getInstance();

        LinearLayoutManager manager = new LinearLayoutManager(this);
        rvLista.setLayoutManager(manager);
        funduserid();

        if(userID != null) {
            profileImage = navigationView.getHeaderView(0).findViewById(R.id.user_image_nav);
            progressBar.setVisibility(View.GONE);
            storageReference = FirebaseStorage.getInstance().getReference();
            StorageReference profileRef = storageReference.child("users/" + fAuth.getCurrentUser().getUid() + "/profile.jpg");
            profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(profileImage);
                }
            });
        }

        Balance = findViewById(R.id.new_saving);
        Balance.setOnClickListener(view -> {
            Intent numbersIntent = new Intent(MainActivity.this, NewSaving.class);
            startActivity(numbersIntent);
        });
        ImageView profile = findViewById(R.id.profileimg);
        profile.setOnClickListener(view -> {
            Intent colorsIntent = new Intent(MainActivity.this, MainActivity.class);
            startActivity(colorsIntent);
        });
        FloatingActionButton invest = findViewById(R.id.btnRegresar);
        invest.setOnClickListener(view -> {
            Intent numbersIntent = new Intent(MainActivity.this, BalanceTotal.class);
            startActivity(numbersIntent);

        });

    }


    public void funduserid(){
        db.collection("users").document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(@NonNull DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    username = documentSnapshot.getString("username");
                }
                getItemsSQL(username);
            }
        });
    }
    private void getItemsSQL(String codigo){
        retrofitApiService.find(codigo).enqueue(new Callback<List<Posts>>() {
            @Override
            public void onResponse(Call<List<Posts>> call, Response<List<Posts>> response) {
                items = response.body();
                if (items==null){
                    Toast.makeText(MainActivity.this, "Realiza una inversion", Toast.LENGTH_SHORT).show();
                }else{

                    adapter = new RecyclerAdapter(items, MainActivity.this);
                    rvLista.setAdapter(adapter);
                }
                return;
            }

            @Override
            public void onFailure(Call<List<Posts>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error: "+"Revise su conexión a internet e intente de nuevo", Toast.LENGTH_LONG).show();
            }
        });
    }
    @Override
    public void itemClick(Posts item) {
        Intent intent = new Intent(this, FundBalance.class);
        intent.putExtra("itemDetail", item);
        startActivity(intent);
    }





    private void navigationDrawer() {
        navigationView.bringToFront();
        // navigationView.setBackgroundColor(getResources().getColor(R.color.Fondo_menu));
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_inversiones);
        navigationView.setItemTextColor(ColorStateList.valueOf(getResources().getColor(R.color.white)));

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
                Intent colorsIntent = new Intent(MainActivity.this, UserProfile.class);
                startActivity(colorsIntent);
                break;
            case R.id.nav_balance:
                Intent numbersIntent = new Intent(MainActivity.this, BalanceTotal.class);
                startActivity(numbersIntent);
                break;
            case R.id.nav_transaccion:
                Intent fondosIntent = new Intent(MainActivity.this, TablaEstatus.class);
                startActivity(fondosIntent);
                break;
            case R.id.nav_inversiones:
                Intent inversionesIntent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(inversionesIntent);
                break;

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }



}