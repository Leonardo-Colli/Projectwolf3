package com.example.project_wolf_3;

import static java.lang.Double.valueOf;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
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
    FirebaseAuth mAuth;
    private FirebaseFirestore db;
    String userID, fundId;

    TextView comision1,Totalc,save, StoplossPorcentaje;
    NavigationView navigationView;
    ImageView profileImage;
    ImageView menuIcon;
    DrawerLayout drawerLayout;
    Switch E1;
    public double total=0,c1,co1;
    SeekBar seekBar;
    public float valorP,EnviarP;
    public int bandera;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_saving);

        ahorro_amt = findViewById(R.id.monto);
        ahorro_plz = findViewById(R.id.plazos);
        save = findViewById(R.id.btnSave);
        E1 = findViewById(R.id.switch1);
        comision1 = findViewById(R.id.comisionE1);
        Totalc = findViewById(R.id.Total);
        seekBar = findViewById(R.id.seekBar);
        StoplossPorcentaje = findViewById(R.id.txtvalor);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();

        Intent data = getIntent();
        fundId = data.getStringExtra("id");

        drawerLayout = findViewById(R.id.menupincipal);
        navigationView = findViewById(R.id.nav_view);
        menuIcon = findViewById(R.id.menu_icon);

        navigationDrawer();
        ImageView profile = findViewById(R.id.button_profile);
        profile.setOnClickListener(view -> {
            Intent colorsIntent = new Intent(NewSaving.this, UserProfile.class);
            startActivity(colorsIntent);
        });

        E1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    comisiones();
                }else{
                    comision1.setText("0.00");
                    total = 0;
                    Totalc.setText(""+ahorro_amt.getText());
                }

            }
        });

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
    private void comisiones(){
        if (TextUtils.isEmpty(ahorro_amt.getText())) {
            comision1.setText("0.00");
            valorP = 0;
            bandera = 0;
            return;
        }else{
             c1 = Double.parseDouble(String.valueOf(ahorro_amt.getText()));
             co1 = (c1 * 3) / 100;
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (progress>=10){
                        valorP = progress;
                        bandera = 1;
                        StoplossPorcentaje.setText(valorP+"/90%");
                    }

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            comision1.setText("" + co1);
            total = c1 + co1;
            Totalc.setText(""+total);
            return;
        }
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
            String totals = String.valueOf(total);
        Double t = Double.valueOf(totals);
            Intent intent = new Intent(NewSaving.this, card.class);
            if (total != 0) {
                intent.putExtra("ahorro_plz", plazo);
                intent.putExtra("ahorro_amt", totals);
                intent.putExtra("E1", valorP);
                intent.putExtra("B", bandera);

            }else{
                intent.putExtra("ahorro_plz", plazo);
                intent.putExtra("ahorro_amt", amount);
                intent.putExtra("E1", valorP);
                intent.putExtra("B", bandera);
            }
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
            case R.id.nav_transaccion:
                Intent fondosIntent = new Intent(NewSaving.this, TablaEstatus.class);
                startActivity(fondosIntent);
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
