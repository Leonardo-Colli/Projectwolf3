package com.example.project_wolf_3;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.stripe.android.view.CardInputWidget;

import retrofit2.Retrofit;

public class card extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    // 10.0.2.2 is the Android emulator's alias to localhost
    // 192.168.1.6 If you are testing in real device with usb connected to same network then use your IP address
    Double price;
    TextView amountText, bienvenido;
    String cantidad, plazo;
    Button payButton;
    View cuerpo,comofunciona, rctTransferencia;
    ImageView mostrarT,ocultarT;


    ImageView btnTransferencia, btnDeposito;

    NavigationView navigationView;
    ImageView menuIcon;
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_pago);

        amountText = findViewById(R.id.amount_id);
        payButton = findViewById(R.id.payButton);
        btnTransferencia = findViewById(R.id.Transferencia);
        rctTransferencia = findViewById(R.id.rctTransferencia);
        btnDeposito = findViewById(R.id.DepositoOxxo);
        cuerpo = findViewById(R.id.terminosC);
        mostrarT = findViewById(R.id.mostrarT);
        ocultarT = findViewById(R.id.ocultarT);
        bienvenido = findViewById(R.id.txtbienvenido);
        comofunciona = findViewById(R.id.btnComofunciona);

        Bundle bundle = this.getIntent().getExtras();
        cantidad = (bundle.getString("ahorro_amt"));
        plazo = bundle.getString("ahorro_plz");
        amountText.setText(cantidad);
        price = Double.valueOf(cantidad);

        cuerpo.setVisibility(View.GONE);
        bienvenido.setVisibility(View.GONE);
        ocultarT.setVisibility(View.GONE);

        drawerLayout = findViewById(R.id.menupincipal);
        navigationView = findViewById(R.id.nav_view);
        menuIcon = findViewById(R.id.menu_icon);

        navigationDrawer();
        ImageView profile = findViewById(R.id.button_profile);
        profile.setOnClickListener(view -> {
            Intent colorsIntent = new Intent(card.this, UserProfile.class);
            startActivity(colorsIntent);
        });

        mostrarT.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              cuerpo.setVisibility(View.VISIBLE);
              bienvenido.setVisibility(View.VISIBLE);
              mostrarT.setVisibility(View.GONE);
              ocultarT.setVisibility(View.VISIBLE);
          }
        });
        ocultarT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarT.setVisibility(View.VISIBLE);
                cuerpo.setVisibility(View.GONE);
                bienvenido.setVisibility(View.GONE);
                ocultarT.setVisibility(View.GONE);
            }
        });

        btnTransferencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intents = new Intent(card.this,TransferenciaBancaria.class);
                intents.putExtra("ahorro_amt", cantidad);
                intents.putExtra("ahorro_plz", plazo);
                startActivity(intents);
            }
        });
        rctTransferencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intents = new Intent(card.this,TransferenciaBancaria.class);
                intents.putExtra("ahorro_amt", cantidad);
                intents.putExtra("ahorro_plz", plazo);
                startActivity(intents);
            }
        });
        btnDeposito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(card.this,DepositoActivity.class);
                startActivity(intent);
            }
        });

        comofunciona.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(card.this, PopupActivity.class));
            }
        });

    }
    private void navigationDrawer() {
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_inversiones);
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
                Intent colorsIntent = new Intent(card.this, UserProfile.class);
                startActivity(colorsIntent);
                break;
            case R.id.nav_balance:
                Intent numbersIntent = new Intent(card.this, BalanceTotal.class);
                startActivity(numbersIntent);
                break;
            case R.id.nav_inversiones:
                Intent inversionesIntent = new Intent(card.this, MainActivity.class);
                startActivity(inversionesIntent);
                break;

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
