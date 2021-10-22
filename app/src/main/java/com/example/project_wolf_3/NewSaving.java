package com.example.project_wolf_3;

import static java.lang.Double.valueOf;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.FormBody;
import retrofit2.Retrofit;

public class NewSaving extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    EditText ahorro_amt, ahorro_plz;
    TextView save;
    FirebaseAuth mAuth;
    private FirebaseFirestore db;
    String userID, fundId;
    double bal, btc_p0, eth_p0, alt_p0, btc_amt, eth_amt, alt_amt,
        btc_vol, eth_vol, alt_vol, initial_amount;

    NavigationView navigationView;
    ImageView menuIcon;
    DrawerLayout drawerLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_saving);

        ahorro_amt = findViewById(R.id.monto);
        ahorro_plz = findViewById(R.id.plazos);
        save = findViewById(R.id.btnSave);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();

        Intent data = getIntent();
        fundId = data.getStringExtra("id");

        drawerLayout = findViewById(R.id.menupincipal);
        navigationView = findViewById(R.id.nav_view);
        menuIcon = findViewById(R.id.menu_icon);

        navigationDrawer();



        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EnviarDatos();
            }
        });
        FloatingActionButton invest = findViewById(R.id.btnRegresar);
        invest.setOnClickListener(view -> {
            Intent numbersIntent = new Intent(NewSaving.this, MainActivity.class);
            startActivity(numbersIntent);

        });
    }

    private void navigationDrawer() {
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_perfil);
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

    public void EnviarDatos() {
        String plazo = ahorro_plz.getText().toString();
        if (TextUtils.isEmpty(plazo)) {
            Toast.makeText(NewSaving.this, "Error! El plazo es necesario", Toast.LENGTH_SHORT).show();
            return;
        }
        String amount = ahorro_amt.getText().toString();
        if (TextUtils.isEmpty(amount)) {
            Toast.makeText(NewSaving.this, "Error! El monto es necesario", Toast.LENGTH_SHORT).show();
            return;
        }
        if (Integer.parseInt(plazo) < 6) {
            Toast.makeText(NewSaving.this, "Error! Plazo mínimo: 6 meses", Toast.LENGTH_SHORT).show();
            return;
        } 
        else if (Integer.parseInt(plazo) > 36) {
            Toast.makeText(NewSaving.this, "Error! Plazo máximo: 3 años", Toast.LENGTH_SHORT).show();
            return;
        }
        // Filter amount of payment
        if (Integer.parseInt(amount) < 100) {
                Toast.makeText(NewSaving.this, "Error! Monto mínimo: $100", Toast.LENGTH_SHORT).show();
                return;
            } else if (Integer.parseInt(amount) > 100000) {
                Toast.makeText(NewSaving.this, "Error! Monto máximo: $100,000", Toast.LENGTH_SHORT).show();
                return;
            }
            //int amt = Integer.parseInt(amount);

            //int plz = Integer.parseInt(plazo);
            Intent intent = new Intent(NewSaving.this, card.class);
            intent.putExtra("ahorro_plz", plazo);intent.putExtra("ahorro_amt", amount);
            startActivity(intent);


        }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_perfil:
                Intent colorsIntent = new Intent(NewSaving.this, UserProfile.class);
                startActivity(colorsIntent);
                break;
            case R.id.nav_balance:
                Intent numbersIntent = new Intent(NewSaving.this, BalanceTotal.class);
                startActivity(numbersIntent);
                break;
            case R.id.nav_inversiones:
                Intent inversionesIntent = new Intent(NewSaving.this, MainActivity.class);
                startActivity(inversionesIntent);
                break;

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
