package com.example.project_wolf_3;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.project_wolf_3.model.EstrategiasModel;
import com.example.project_wolf_3.model.Post;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TransferenciaBancaria extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    TextView nombre,cuenta,banco,concepto,btn,btnNotificar,amountText,btnListo;

    ImageView CNombre,Ccuenta,CBanco,Cconcepto;

    FirebaseAuth mAuth;
    String userID, fundId, name, username;
    FirebaseFirestore db;

    public double bal, btc_p0, eth_p0, alt_p0, btc_amt, eth_amt, alt_amt,
            btc_vol, eth_vol, alt_vol, initial_amount, inversion;
    public double inversionTotal, balanceTotal;
    String cantidad, plazo;
    Double price;
    public String cadena;

    NavigationView navigationView;
    ImageView menuIcon;
    DrawerLayout drawerLayout;

    public float v1, E1;
    public int bandera;

    LottieAnimationView animationView;
    private RequestQueue queue;

    private RetrofitInterface myApi;
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
        setContentView(R.layout.activity_transferencia_bancaria);
        nombre = findViewById(R.id.txtnombre);
        cuenta = findViewById(R.id.txtcuenta);
        banco = findViewById(R.id.txtbanco);
        concepto = findViewById(R.id.txtconcepto);
        btn = findViewById(R.id.btnEnviar);
        btnNotificar = findViewById(R.id.btnNotificar);
        amountText = findViewById(R.id.amount_id);
        btnListo = findViewById(R.id.btnListo);

        CNombre = findViewById(R.id.copiarName);
        Ccuenta = findViewById(R.id.copiarCuenta);
        CBanco = findViewById(R.id.copiarBanco);
        Cconcepto = findViewById(R.id.copiarConcepto);
        animationView = findViewById(R.id.animacion);
        animationView.setVisibility(View.GONE);

        drawerLayout = findViewById(R.id.menupincipal);
        navigationView = findViewById(R.id.nav_view);
        menuIcon = findViewById(R.id.menu_icon);

        navigationDrawer();
        ImageView profile = findViewById(R.id.button_profile);
        profile.setOnClickListener(view -> {
            Intent colorsIntent = new Intent(TransferenciaBancaria.this, UserProfile.class);
            startActivity(colorsIntent);
        });

        Bundle bundle = this.getIntent().getExtras();
        cantidad = (bundle.getString("ahorro_amt"));
        plazo = bundle.getString("ahorro_plz");
        cadena = bundle.getString("numero_orden");
        v1 = bundle.getFloat("E1");
        bandera = bundle.getInt("B");
        amountText.setText(cantidad);
        price = Double.valueOf(cantidad);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();

        queue = Volley.newRequestQueue(this);

        //init Api
        RetrofitInterface retrofit = RetrofitClient.getInstance();
        myApi = retrofit;
        E1 = (v1/100);

        Intent data = getIntent();
        fundId = data.getStringExtra("id");
        FloatingActionButton invest = findViewById(R.id.btnRegresar);
        invest.setOnClickListener(view -> {
            Intent numbersIntent = new Intent(TransferenciaBancaria.this, NewSaving.class);
            startActivity(numbersIntent);

        });

        if(fundId != null) {
            Bundle bundles = this.getIntent().getExtras();
            btc_p0 = (bundles.getDouble("btc_p0"));
            eth_p0 = bundles.getDouble("eth_p0");
            alt_p0 = bundles.getDouble("alt_p0");
            btc_vol = bundles.getDouble("btc_vol");
            eth_vol = bundles.getDouble("eth_vol");
            alt_vol = bundles.getDouble("alt_vol");
            btc_amt = bundles.getDouble("btc_amt");
            eth_amt = bundles.getDouble("eth_amt");
            alt_amt = bundles.getDouble("alt_amt");
            initial_amount = bundles.getDouble("initial_amount");

        }

        CNombre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // obtenemos el texto del textView3
                String text = nombre.getText().toString();
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("text",  text);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(TransferenciaBancaria.this, "Texto copiado al portapapeles", Toast.LENGTH_SHORT).show();
            }
        });
        Ccuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // obtenemos el texto del textView3
                String text = cuenta.getText().toString();
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("text",  text);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(TransferenciaBancaria.this, "Texto copiado al portapapeles", Toast.LENGTH_SHORT).show();
            }
        });
        CBanco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // obtenemos el texto del textView3
                String text = banco.getText().toString();
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("text",  text);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(TransferenciaBancaria.this, "Texto copiado al portapapeles", Toast.LENGTH_SHORT).show();
            }
        });
        Cconcepto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // obtenemos el texto del textView3
                String text = concepto.getText().toString();
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("text",  text);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(TransferenciaBancaria.this, "Texto copiado al portapapeles", Toast.LENGTH_SHORT).show();
            }
        });
        // El setOnClickListener del botón Enviar


        btn.setVisibility(View.GONE);
        btnListo.setVisibility(View.GONE);
        btnNotificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn.setVisibility(View.VISIBLE);
                btnNotificar.setVisibility(View.GONE);
                invest.setVisibility(View.GONE);
                //onSaveNote();
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view)
            {
                String enviarcorreo = "savvytechmail@gmail.com";
                String enviarmensaje = "Anexo mi comprobante de Pago";

                // Defino mi Intent y hago uso del objeto ACTION_SEND
                Intent intent = new Intent(Intent.ACTION_SEND);

                // Defino los Strings Email, Asunto y Mensaje con la función putExtra
                intent.putExtra(Intent.EXTRA_EMAIL, new String[] { enviarcorreo });
                intent.putExtra(Intent.EXTRA_SUBJECT, cadena);
                intent.putExtra(Intent.EXTRA_TEXT, enviarmensaje);

                // Establezco el tipo de Intent
                intent.setType("message/rfc822");

                try {
                    // Lanzo el selector de cliente de Correo
                    startActivity(Intent.createChooser(intent, "Elije un cliente de Correo:"));
                    btn.setVisibility(View.GONE);
                    new Handler().postDelayed(new Runnable(){
                        public void run(){
                            btnListo.setVisibility(View.VISIBLE);  //esconde botón.
                        }
                    }, 8000); //8 segundos;

                }catch (Exception ex){
                    Toast.makeText(TransferenciaBancaria.this, "ERROR"+ ex, Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnListo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onSaveNote();
                createPost();
                animationView.setVisibility(View.VISIBLE);
                animationView.playAnimation();

                new Handler().postDelayed(new Runnable(){
                    public void run(){
                        Intent intent = new Intent(TransferenciaBancaria.this, MainActivity.class);
                        startActivity(intent);
                    }
                }, 6000);

            }
        });

    }

    private void createPost(){
    db.collection("users").document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
        @Override
        public void onSuccess(@NonNull DocumentSnapshot documentSnapshot) {
            if (documentSnapshot.exists()){
                username = documentSnapshot.getString("username");
                name = documentSnapshot.getString("fullname");
            }

            double price = Double.parseDouble(cantidad);
            double amount1 = Double.parseDouble(cantidad);
            int installments =Integer.parseInt(plazo);
            String transactionid = cadena;
            String firebaseSavingId = cadena;
            int isConfirmed = 0;
            Call<Post> call = myApi.createPost(name,username,price, amount1, installments, transactionid, isConfirmed, firebaseSavingId, bandera );
            call.enqueue(new Callback<Post>() {
                @Override
                public void onResponse(Call<Post> call, Response<Post> response) {
                    if (!response.isSuccessful()){
                        Toast.makeText(TransferenciaBancaria.this, "Pago exitoso!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Post postResponse = response.body();
                }

                @Override
                public void onFailure(Call<Post> call, Throwable t) {
                    Toast.makeText(TransferenciaBancaria.this, "Error: "+t, Toast.LENGTH_SHORT).show();
                }
            });
            Call<EstrategiasModel> call1 = myApi.estrategiaPost(E1);
            call1.enqueue(new Callback<EstrategiasModel>() {
                @Override
                public void onResponse(Call<EstrategiasModel> call, Response<EstrategiasModel> response) {
                    if (!response.isSuccessful()){
                        return;
                    }
                    EstrategiasModel postResponse = response.body();
                }

                @Override
                public void onFailure(Call<EstrategiasModel> call, Throwable t) {
                    Toast.makeText(TransferenciaBancaria.this, "Error: "+t, Toast.LENGTH_SHORT).show();
                }
            });
        }
    });

}

    public void onSaveNote(){
        Date plz_dt = Calendar.getInstance().getTime();

        DocumentReference documentReference = db.collection("users").document(userID);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(@NonNull DocumentSnapshot documentSnapshot) {

                inversion = documentSnapshot.getDouble("Inversion");
                bal = documentSnapshot.getDouble("balance");


            }
        });

        // Parse string to int
        // Filter if period is null
        String plzo = plazo;
        if(TextUtils.isEmpty(plzo)){
            Toast.makeText(TransferenciaBancaria.this, "Error! El plazo es necesario", Toast.LENGTH_SHORT).show();
            return;
        }

        // Filter if amount is null
        String amount = cantidad;
        if(TextUtils.isEmpty(amount)){
            Toast.makeText(TransferenciaBancaria.this, "Error! El monto es necesario", Toast.LENGTH_SHORT).show();
            return;
        }
        Double amt = Double.valueOf(cantidad);
        int plz = Integer.parseInt(plzo);
        // Filter number of payments
        if(plz < 6) {
            Toast.makeText(TransferenciaBancaria.this, "Error! Plajzo mínimo: 6 meses", Toast.LENGTH_SHORT).show();
        } else if(plz > 36) {
            Toast.makeText(TransferenciaBancaria.this, "Error! Plazo máximo: 3 años", Toast.LENGTH_SHORT).show();
        } else {

            // Filter amount of payment
            if(amt < 100){
                Toast.makeText(TransferenciaBancaria.this, "Error! Monto mínimo: $100", Toast.LENGTH_SHORT).show();
            } else if(amt > 100000) {
                Toast.makeText(TransferenciaBancaria.this, "Error! Monto máximo: $100,000", Toast.LENGTH_SHORT).show();
            } else {

                if(bal == 0.0){
                    documentReference.update("first_saving", plz_dt);
                }

                //Parameters

                double btc_mix = 0.4;
                double eth_mix = 0;
                double alt_mix = 0;
                double cash_com = 0.03; //comision
                double exch_com = 0.01; //comision
                double ref_com = 0.01;
                double own_com = 0.3;
                double btc_amt_new = amt * (1 - cash_com) * (1 - exch_com) * btc_mix;
              //  double eth_amt_new = amt * (1 - cash_com) * (1 - exch_com) * eth_mix;
                //double alt_amt_new = amt * (1 - cash_com) * (1 - exch_com) * alt_mix;
                double fin_amt_new = btc_amt_new; //+ eth_amt_new + alt_amt_new;


                if(fundId != null) {
                    double mix_new = amt/(bal + amt); // Nueva inversion, pocentaje nuevo del balance



                    double btc_p0_new = 100000; // cambia
                   // double eth_p0_new = 36300;
                    //double alt_p0_new = 1250; //ada
                    double final_amount = btc_amt + btc_amt_new; //+ eth_amt + eth_amt_new + alt_amt + alt_amt_new;

                    DocumentReference documentReference1 = db.collection("funds").document(userID).collection("savings").document(fundId);

                    documentReference1.update("btc_p0", btc_p0*(1 - mix_new) + btc_p0_new*mix_new);
                    //documentReference1.update("eth_p0", eth_p0*(1 - mix_new) + eth_p0_new*mix_new);
                    //documentReference1.update("alt_p0", alt_p0*(1 - mix_new) + alt_p0_new*mix_new);

                    documentReference1.update("btc_amt", btc_amt + btc_amt_new);
                    //documentReference1.update("eth_amt", eth_amt + eth_amt_new);
                    //documentReference1.update("alt_amt", alt_amt + alt_amt_new);

                    documentReference1.update("btc_vol", btc_vol + btc_amt_new/btc_p0_new);
                    //documentReference1.update("eth_vol", eth_vol + eth_amt_new/eth_p0_new);
                    //documentReference1.update("alt_vol", alt_vol + alt_amt_new/alt_p0_new);

                    //Esto si cambia
                    documentReference1.update("initial_amount", initial_amount + amt);
                    documentReference1.update("final_amount", final_amount);
                    documentReference1.update("roi_per", (final_amount - initial_amount - amt) / (initial_amount + amt));
                    documentReference1.update("roi_vol", final_amount - initial_amount - amt);
                    double value1= btc_vol + (btc_amt_new/btc_p0_new);
                    double value2= initial_amount + amt;
                    Log.i("Val", "btc: "+value1+ "initial: "+ value2);

                }else {


                    //double btc_p0_new = 1000000;
                    //double eth_p0_new = 36000;
                    //double alt_p0_new = 1000;

                    inversionTotal = inversion + amt;
                    documentReference.update("Inversion", inversionTotal);
                    balanceTotal = bal + fin_amt_new;
                    documentReference.update("balance", balanceTotal);


                    // Create new saving
                    Map<String, Object> saving = new HashMap<>();
                    saving.put("date", plz_dt);
                    saving.put("months", plz);
                    saving.put("initial_amount", 0);
                    saving.put("final_amount", 0);
                    //saving.put("roi_per", (fin_amt_new - amt) / amt);
                    //saving.put("roi_vol", fin_amt_new - amt);

                   /* saving.put("btc_mix", btc_mix);
                    saving.put("eth_mix", eth_mix);
                    saving.put("alt_mix", alt_mix);

                    saving.put("btc_amt", btc_amt_new);
                    saving.put("eth_amt", eth_amt_new);
                    saving.put("alt_amt", alt_amt_new);

                    saving.put("btc_vol", btc_amt_new/btc_p0_new);
                    saving.put("eth_vol", eth_amt_new/eth_p0_new);
                    saving.put("alt_vol", alt_amt_new/alt_p0_new);

                    saving.put("btc_p0", btc_p0_new);
                    saving.put("eth_p0", eth_p0_new);
                    saving.put("alt_p0", alt_p0_new);

                    saving.put("btc_pC", btc_p0_new);
                    saving.put("eth_pC", eth_p0_new);
                    saving.put("alt_pC", alt_p0_new);

                    saving.put("cash_com", cash_com);
                    saving.put("exch_com", exch_com);
                    saving.put("ref_com", ref_com);
                    saving.put("own_com", own_com);*/

                    db.collection("funds").document(userID).collection("savings").document(cadena).set(saving)
                            .addOnSuccessListener(aVoid -> Toast.makeText(TransferenciaBancaria.this, "Ahorro Guardado", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(TransferenciaBancaria.this, "Error! Ahorro no guardado", Toast.LENGTH_SHORT).show());

                }

               // startActivity(new Intent(getApplicationContext(), TransactionVerif.class));

            }
        }


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
                Intent colorsIntent = new Intent(TransferenciaBancaria.this, UserProfile.class);
                startActivity(colorsIntent);
                break;
            case R.id.nav_balance:
                Intent numbersIntent = new Intent(TransferenciaBancaria.this, BalanceTotal.class);
                startActivity(numbersIntent);
                break;
            case R.id.nav_transaccion:
                Intent fondosIntent = new Intent(TransferenciaBancaria.this, TablaEstatus.class);
                startActivity(fondosIntent);
                break;
            case R.id.nav_inversiones:
                Intent inversionesIntent = new Intent(TransferenciaBancaria.this, MainActivity.class);
                startActivity(inversionesIntent);
                break;

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
