package com.example.project_wolf_3;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class TransferenciaBancaria extends AppCompatActivity {
    TextView nombre,cuenta,banco,concepto,btn,btnNotificar,amountText,btnListo;

    ImageView CNombre,Ccuenta,CBanco,Cconcepto;

    FirebaseAuth mAuth;
    String userID, fundId, fullname, username;
    FirebaseFirestore db;

    public double bal, btc_p0, eth_p0, alt_p0, btc_amt, eth_amt, alt_amt,
            btc_vol, eth_vol, alt_vol, initial_amount, inversion;
    public double inversionTotal, balanceTotal;
    String cantidad, plazo;
    Double price;
    VideoView videoView;


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
        videoView = findViewById(R.id.videoView);



        Bundle bundle = this.getIntent().getExtras();
        cantidad = (bundle.getString("ahorro_amt"));
        plazo = bundle.getString("ahorro_plz");
        amountText.setText(cantidad);
        price = Double.valueOf(cantidad);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();

        //init Api
        Retrofit retrofit = RetrofitClient.getInstance();
        myApi = retrofit.create(RetrofitInterface.class);

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
                String enviarasunto = "Pago";
                String enviarmensaje = "Anexo mi comprobante de Pago";

                // Defino mi Intent y hago uso del objeto ACTION_SEND
                Intent intent = new Intent(Intent.ACTION_SEND);

                // Defino los Strings Email, Asunto y Mensaje con la función putExtra
                intent.putExtra(Intent.EXTRA_EMAIL,
                        new String[] { enviarcorreo });
                intent.putExtra(Intent.EXTRA_SUBJECT, enviarasunto);
                intent.putExtra(Intent.EXTRA_TEXT, enviarmensaje);

                // Establezco el tipo de Intent
                intent.setType("message/rfc822");

                // Lanzo el selector de cliente de Correo
                startActivity(
                        Intent
                                .createChooser(intent,
                                        "Elije un cliente de Correo:"));
                btn.setVisibility(View.GONE);
                new Handler().postDelayed(new Runnable(){
                    public void run(){

                        btnListo.setVisibility(View.VISIBLE);  //esconde botón.

                    }
                }, 8000); //10 segundos;


            }
        });
        btnListo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                db.collection("users").document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(@NonNull DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()){
                            username = documentSnapshot.getString("username");
                        }
                        int longitud = 10;
                        String cadena = cadenaAleatoria(longitud);
                        float price = Integer.parseInt(cantidad);
                        float amount1 = Integer.parseInt(cantidad);
                        int installments =Integer.parseInt(plazo);
                        String transactionid = cadena;
                        compositeDisposable.add(myApi.registroDatos(username,price, amount1, installments, transactionid)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<String>() {
                                    @Override
                                    public void accept(String s) throws Exception {
                                        Toast.makeText(TransferenciaBancaria.this, ""+s, Toast.LENGTH_SHORT).show();
                                    }
                                }));
                    }
                });

                /*String path = "android.resource://"+getPackageName()+"/"+R.raw.transaccion;
                Uri uri = Uri.parse(path);
                videoView.setVideoURI(uri);
                videoView.setVisibility(View.VISIBLE);
                videoView.requestFocus();
                videoView.start();*/


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
    public void video(){
       //
        //videoView.setVideoURI(Uri.parse(path));
        videoView.setVisibility(View.VISIBLE);
        videoView.start();
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
        int amt = Integer.parseInt(amount);
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
                double eth_mix = 0.3;
                double alt_mix = 0.3;
                double cash_com = 0.03; //comision
                double exch_com = 0.01; //comision
                double ref_com = 0.01;
                double own_com = 0.3;
                double btc_amt_new = amt * (1 - cash_com) * (1 - exch_com) * btc_mix;
                double eth_amt_new = amt * (1 - cash_com) * (1 - exch_com) * eth_mix;
                double alt_amt_new = amt * (1 - cash_com) * (1 - exch_com) * alt_mix;
                double fin_amt_new = btc_amt_new + eth_amt_new + alt_amt_new;


                if(fundId != null) {
                    double mix_new = amt/(bal + amt); // Nueva inversion, pocentaje nuevo del balance



                    double btc_p0_new = 100000; // cambia
                    double eth_p0_new = 36300;
                    double alt_p0_new = 1250; //ada
                    double final_amount = btc_amt + btc_amt_new + eth_amt + eth_amt_new + alt_amt + alt_amt_new;

                    DocumentReference documentReference1 = db.collection("funds").document(userID).collection("savings").document(fundId);

                    documentReference1.update("btc_p0", btc_p0*(1 - mix_new) + btc_p0_new*mix_new);
                    documentReference1.update("eth_p0", eth_p0*(1 - mix_new) + eth_p0_new*mix_new);
                    documentReference1.update("alt_p0", alt_p0*(1 - mix_new) + alt_p0_new*mix_new);

                    documentReference1.update("btc_amt", btc_amt + btc_amt_new);
                    documentReference1.update("eth_amt", eth_amt + eth_amt_new);
                    documentReference1.update("alt_amt", alt_amt + alt_amt_new);

                    documentReference1.update("btc_vol", btc_vol + btc_amt_new/btc_p0_new);
                    documentReference1.update("eth_vol", eth_vol + eth_amt_new/eth_p0_new);
                    documentReference1.update("alt_vol", alt_vol + alt_amt_new/alt_p0_new);

                    //Esto si cambia
                    documentReference1.update("initial_amount", initial_amount + amt);
                    documentReference1.update("final_amount", final_amount);
                    documentReference1.update("roi_per", (final_amount - initial_amount - amt) / (initial_amount + amt));
                    documentReference1.update("roi_vol", final_amount - initial_amount - amt);
                    double value1= btc_vol + (btc_amt_new/btc_p0_new);
                    double value2= initial_amount + amt;
                    Log.i("Val", "btc: "+value1+ "initial: "+ value2);

                }else {


                    double btc_p0_new = 1000000;
                    double eth_p0_new = 36000;
                    double alt_p0_new = 1000;

                    inversionTotal = inversion + amt;
                    documentReference.update("Inversion", inversionTotal);
                    balanceTotal = bal + fin_amt_new;
                    documentReference.update("balance", balanceTotal);


                    // Create new saving
                    Map<String, Object> saving = new HashMap<>();
                    saving.put("date", plz_dt);
                    saving.put("months", plz);
                    saving.put("initial_amount", amt);
                    saving.put("final_amount", fin_amt_new);
                    saving.put("roi_per", (fin_amt_new - amt) / amt);
                    saving.put("roi_vol", fin_amt_new - amt);

                    saving.put("btc_mix", btc_mix);
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
                    saving.put("own_com", own_com);

                    db.collection("funds").document(userID).collection("savings").document(String.valueOf(plz_dt)).set(saving)
                            .addOnSuccessListener(aVoid -> Toast.makeText(TransferenciaBancaria.this, "Ahorro Guardado", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(TransferenciaBancaria.this, "Error! Ahorro no guardado", Toast.LENGTH_SHORT).show());

                }

                startActivity(new Intent(getApplicationContext(), TransactionVerif.class));

            }
        }


    }
}