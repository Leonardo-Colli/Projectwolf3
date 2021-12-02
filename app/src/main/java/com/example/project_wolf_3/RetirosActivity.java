package com.example.project_wolf_3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.project_wolf_3.model.ComisionesModel;
import com.example.project_wolf_3.model.Post;
import com.example.project_wolf_3.model.Posts;
import com.example.project_wolf_3.model.RetirosModel;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RetirosActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
        private Spinner spinner;
        private double cantidad;
        TextView CantidadR, Clabe, Name, Newretiro, ComisionE, ComisionP, Total;

        FirebaseAuth mAuth;
        String userID, username, correo;
        FirebaseFirestore db;
        private FirebaseFirestore firebaseFirestore;
        public double Cexchange, Cpasarela;

        LottieAnimationView animationView;

        ImageView Inversiones, menuIcon, profileImage;
        NavigationView navigationView;
        DrawerLayout drawerLayout;
        StorageReference storageReference;

        private RetrofitInterface myApi;
        private RequestQueue queue;
        CompositeDisposable compositeDisposable = new CompositeDisposable();

        @Override
        protected void onStop() {
            compositeDisposable.clear();
            super.onStop();
        }

        @Override
        protected void onDestroy() {
            compositeDisposable.clear();
            super.onDestroy();
        }


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_retiros);

            Name = findViewById(R.id.fullname);
            Clabe = findViewById(R.id.CLABE);
            CantidadR = findViewById(R.id.monto);
            spinner = findViewById(R.id.spinner);
            Newretiro = findViewById(R.id.new_retiro);
            ComisionE = findViewById(R.id.Cexchange);
            ComisionP = findViewById(R.id.Cpasarela);
            Total = findViewById(R.id.Total);
            Inversiones = findViewById(R.id.inversiones);

            animationView = findViewById(R.id.animacion);
            animationView.setVisibility(View.GONE);

            drawerLayout = findViewById(R.id.menupincipal);
            navigationView = findViewById(R.id.nav_view);
            menuIcon = findViewById(R.id.menu_icon);

            mAuth = FirebaseAuth.getInstance();
            firebaseFirestore = FirebaseFirestore.getInstance();
            userID = mAuth.getCurrentUser().getUid();

            profileImage = navigationView.getHeaderView(0).findViewById(R.id.user_image_nav);

            storageReference = FirebaseStorage.getInstance().getReference();
            StorageReference profileRef = storageReference.child("users/" + mAuth.getCurrentUser().getUid() + "/profile.jpg");
            profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(profileImage);
                }
            });

            Bundle bundle = this.getIntent().getExtras();
            cantidad = (bundle.getDouble("CantidadR"));

            CantidadR.setText(String.valueOf(cantidad));


            db = FirebaseFirestore.getInstance();
            mAuth = FirebaseAuth.getInstance();
            userID = mAuth.getCurrentUser().getUid();

            navigationDrawer();
            queue = Volley.newRequestQueue(this);
            RetrofitInterface retrofit = RetrofitClient.getInstance();
            myApi = retrofit;
            comsiones("1");

            String[] opciones = {"BBVA", "Santander", "Citibanamex", "Banorte", "stp"};
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, opciones);
            spinner.setAdapter(adapter);

            Newretiro.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createPost();
                }
            });

            Inversiones.setOnClickListener(view -> {
                Intent colorsIntent = new Intent(RetirosActivity.this, MainActivity.class);
                startActivity(colorsIntent);
            });
            FloatingActionButton invest = findViewById(R.id.btnRegresar);
            invest.setOnClickListener(view -> {
                Intent numbersIntent = new Intent(RetirosActivity.this, MainActivity.class);
                startActivity(numbersIntent);

            });

        }

        private void comsiones(String codigo) {
            Call<List<ComisionesModel>> call = myApi.comisionesfind(codigo);
            call.enqueue(new Callback<List<ComisionesModel>>() {
                @Override
                public void onResponse(Call<List<ComisionesModel>> call, Response<List<ComisionesModel>> response) {
                    if (!response.isSuccessful()) {
                        ComisionE.setText(response.code());
                        ComisionP.setText(response.code());
                        return;
                    }
                    List<ComisionesModel> postsList = response.body();
                    for (ComisionesModel post : postsList) {
                        Cexchange = post.getCexchange();
                        Cpasarela = post.getCpasarela();
                        double formula = cantidad * (1 - Cexchange) * (1 - Cpasarela);
                        double formulae = (cantidad - (cantidad * (1 - Cexchange)));
                        double formulap = (cantidad - (cantidad * (1 - Cpasarela)));
                        ComisionE.setText(String.format("%s", String.format("%,.2f", formulae)));
                        ComisionP.setText(String.format("%s", String.format("%,.2f", formulap)));
                        Total.setText(String.format("%s", String.format("%,.2f", formula)));
                    }
                }

                @Override
                public void onFailure(Call<List<ComisionesModel>> call, Throwable t) {
                    Toast.makeText(RetirosActivity.this, "Error: " + t, Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void createPost() {
            db.collection("users").document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(@NonNull DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        username = documentSnapshot.getString("username");
                        correo = documentSnapshot.getString("email");
                    }


                    String names = String.valueOf(Name.getText());
                    String clave = String.valueOf(Clabe.getText());
                    if (TextUtils.isEmpty(names)) {
                        Toast.makeText(RetirosActivity.this, "Error! El nombre es necesario", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(clave)) {
                        Toast.makeText(RetirosActivity.this, "Error! El Número de tarjeta, cuenta o CLABE es necesario", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        double monto = Double.parseDouble(String.valueOf(cantidad));
                        double total = Double.parseDouble(String.valueOf(cantidad));
                        String seleccion = spinner.getSelectedItem().toString();

                        Call<RetirosModel> call = myApi.retirosPost(username, names, correo, seleccion, clave, monto, total);
                        call.enqueue(new Callback<RetirosModel>() {
                            @Override
                            public void onResponse(Call<RetirosModel> call, Response<RetirosModel> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(RetirosActivity.this, "Retiro en proceso!", Toast.LENGTH_SHORT).show();
                                    animationView.setVisibility(View.VISIBLE);
                                    animationView.playAnimation();
                                    new Handler().postDelayed(new Runnable() {
                                        public void run() {
                                            Intent intent = new Intent(RetirosActivity.this, BalanceTotal.class);
                                            startActivity(intent);
                                        }
                                    }, 6000);
                                    return;
                                }
                                RetirosModel postResponse = response.body();
                            }

                            @Override
                            public void onFailure(Call<RetirosModel> call, Throwable t) {
                                Toast.makeText(RetirosActivity.this, "Error: " + t, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }


                }
            });

        }

        private void navigationDrawer() {
            navigationView.bringToFront();
            // navigationView.setBackgroundColor(getResources().getColor(R.color.Fondo_menu));
            navigationView.setNavigationItemSelectedListener(this);
            navigationView.setCheckedItem(R.id.nav_inversiones);

            menuIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
                        drawerLayout.closeDrawer(GravityCompat.START);
                    } else drawerLayout.openDrawer(GravityCompat.START);
                }
            });
        }

        @Override
        public void onBackPressed() {
            if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else
                super.onBackPressed();
        }

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_perfil:
                    Intent colorsIntent = new Intent(RetirosActivity.this, UserProfile.class);
                    startActivity(colorsIntent);
                    break;
                case R.id.nav_balance:
                    Intent numbersIntent = new Intent(RetirosActivity.this, BalanceTotal.class);
                    startActivity(numbersIntent);
                    break;
                case R.id.nav_transaccion:
                    Intent fondosIntent = new Intent(RetirosActivity.this, TablaEstatus.class);
                    startActivity(fondosIntent);
                    break;
                case R.id.nav_inversiones:
                    Intent inversionesIntent = new Intent(RetirosActivity.this, MainActivity.class);
                    startActivity(inversionesIntent);
                    break;

            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }
}