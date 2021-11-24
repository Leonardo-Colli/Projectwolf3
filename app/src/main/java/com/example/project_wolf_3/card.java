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

import java.util.concurrent.ThreadLocalRandom;

import retrofit2.Retrofit;

public class card extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    // 10.0.2.2 is the Android emulator's alias to localhost
    // 192.168.1.6 If you are testing in real device with usb connected to same network then use your IP address
    Double price;
    TextView amountText,  NumeroOrden;
    TextView bienvenido, p1,p2,p3,p4,p5;
    ImageView i0,i1,i2,i3,i4;
    String cantidad, plazo;
    View cuerpo,comofunciona, btnTranferencia,btnDeposito, rctTerminos;
    TextView MTerninos, OTerminos;

    NavigationView navigationView;
    ImageView menuIcon;
    DrawerLayout drawerLayout;
    public String cadena;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_pago);

        amountText = findViewById(R.id.amount_id);
        btnTranferencia = findViewById(R.id.rctTransferencia);
        btnDeposito = findViewById(R.id.depositoOxxo);
        rctTerminos = findViewById(R.id.Terminos);
        cuerpo = findViewById(R.id.terminosC);
        bienvenido = findViewById(R.id.txtbienvenido);
        comofunciona = findViewById(R.id.btnComofunciona);
        MTerninos = findViewById(R.id.t_rminos);
        OTerminos = findViewById(R.id.t_rminos_ocultar);
        NumeroOrden = findViewById(R.id.numero_orden);
        p1 = findViewById(R.id.txtP1);
        p2 = findViewById(R.id.txtp2);
        p3 = findViewById(R.id.txtp3);
        p4 = findViewById(R.id.txtp4);
        p5 = findViewById(R.id.txtp5);
        i0 = findViewById(R.id.imageView);
        i1 = findViewById(R.id.imageView1);
        i2 = findViewById(R.id.imageView2);
        i3 = findViewById(R.id.imageView3);
        i4 = findViewById(R.id.imageView4);

        Bundle bundle = this.getIntent().getExtras();
        cantidad = (bundle.getString("ahorro_amt"));
        plazo = bundle.getString("ahorro_plz");
        amountText.setText(cantidad);
        price = Double.valueOf(cantidad);

        cuerpo.setVisibility(View.GONE);
        bienvenido.setVisibility(View.GONE);
        p1.setVisibility(View.GONE);
        p2.setVisibility(View.GONE);
        p3.setVisibility(View.GONE);
        p4.setVisibility(View.GONE);
        p5.setVisibility(View.GONE);
        i0.setVisibility(View.GONE);
        i1.setVisibility(View.GONE);
        i2.setVisibility(View.GONE);
        i3.setVisibility(View.GONE);
        i4.setVisibility(View.GONE);

        OTerminos.setVisibility(View.GONE);

        drawerLayout = findViewById(R.id.menupincipal);
        navigationView = findViewById(R.id.nav_view);
        menuIcon = findViewById(R.id.menu_icon);

        navigationDrawer();
        ImageView profile = findViewById(R.id.button_profile);
        profile.setOnClickListener(view -> {
            Intent colorsIntent = new Intent(card.this, UserProfile.class);
            startActivity(colorsIntent);
        });

        int longitud = 10;
        cadena ="TD-"+cadenaAleatoria(longitud);
        NumeroOrden.setText("Número de orden: "+cadena);

        MTerninos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cuerpo.setVisibility(View.VISIBLE);
                bienvenido.setVisibility(View.VISIBLE);
                MTerninos.setVisibility(View.GONE);
                OTerminos.setVisibility(View.VISIBLE);
                p1.setVisibility(View.VISIBLE);
                p2.setVisibility(View.VISIBLE);
                p3.setVisibility(View.VISIBLE);
                p4.setVisibility(View.VISIBLE);
                p5.setVisibility(View.VISIBLE);
                i0.setVisibility(View.VISIBLE);
                i1.setVisibility(View.VISIBLE);
                i2.setVisibility(View.VISIBLE);
                i3.setVisibility(View.VISIBLE);
                i4.setVisibility(View.VISIBLE);
            }
        });
        OTerminos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MTerninos.setVisibility(View.VISIBLE);
                cuerpo.setVisibility(View.GONE);
                bienvenido.setVisibility(View.GONE);
                OTerminos.setVisibility(View.GONE);
                p1.setVisibility(View.GONE);
                p2.setVisibility(View.GONE);
                p3.setVisibility(View.GONE);
                p4.setVisibility(View.GONE);
                p5.setVisibility(View.GONE);
                i0.setVisibility(View.GONE);
                i1.setVisibility(View.GONE);
                i2.setVisibility(View.GONE);
                i3.setVisibility(View.GONE);
                i4.setVisibility(View.GONE);
            }
        });


        btnTranferencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intents = new Intent(card.this,TransferenciaBancaria.class);
                intents.putExtra("ahorro_amt", cantidad);
                intents.putExtra("ahorro_plz", plazo);
                intents.putExtra("numero_orden", cadena);
                startActivity(intents);
            }
        });
        btnDeposito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(card.this,DepositoActivity.class);
                intent.putExtra("ahorro_amt", cantidad);
                intent.putExtra("ahorro_plz", plazo);
                intent.putExtra("numero_orden", cadena);
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
    public static String cadenaAleatoria(int longitud) {
        // El banco de caracteres
        String banco = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        // La cadena en donde iremos agregando un carácter aleatorio
        String cadena = "";
        for (int x = 0; x < longitud; x++) {
            int indiceAleatorio = numeroAleatorioEnRango(0, banco.length() - 1);
            char caracterAleatorio = banco.charAt(indiceAleatorio);
            cadena += caracterAleatorio;
        }
        return cadena;
    }

    public static int numeroAleatorioEnRango(int minimo, int maximo) {
        // nextInt regresa en rango pero con límite superior exclusivo, por eso sumamos 1
        return ThreadLocalRandom.current().nextInt(minimo, maximo + 1);
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
