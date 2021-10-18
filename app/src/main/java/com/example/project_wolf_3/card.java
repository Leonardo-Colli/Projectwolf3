package com.example.project_wolf_3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.stripe.android.view.CardInputWidget;

import retrofit2.Retrofit;

public class card extends AppCompatActivity {

    // 10.0.2.2 is the Android emulator's alias to localhost
    // 192.168.1.6 If you are testing in real device with usb connected to same network then use your IP address
    private static final String BACKEND_URL = "http://10.0.2.2:4242/"; //4242 is port mentioned in server i.e index.js
    private static final String BACKEND_URL2 = "http://10.0.2.2:3000/";
    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    private String BASE_URL ="http://10.0.2.2:3000";
    Double price;
    TextView amountText, bienvenido;
    String cantidad, plazo;
    CardInputWidget cardInputWidget;
    Button payButton;
    View cuerpo,comofunciona;
    ImageView mostrarT,ocultarT;


    ImageView btnTransferencia, btnDeposito;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_pago);

        amountText = findViewById(R.id.amount_id);
        cardInputWidget = findViewById(R.id.cardInputWidget);
        payButton = findViewById(R.id.payButton);
        btnTransferencia = findViewById(R.id.Transferencia);
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
}
