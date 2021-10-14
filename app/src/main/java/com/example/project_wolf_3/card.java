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
    TextView amountText;
    String cantidad, plazo;
    CardInputWidget cardInputWidget;
    Button payButton;


    ImageView btnTransferencia;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_pago);

        amountText = findViewById(R.id.amount_id);
        cardInputWidget = findViewById(R.id.cardInputWidget);
        payButton = findViewById(R.id.payButton);
        btnTransferencia = findViewById(R.id.Transferencia);

        Bundle bundle = this.getIntent().getExtras();
        cantidad = (bundle.getString("ahorro_amt"));
        plazo = bundle.getString("ahorro_plz");
        amountText.setText(cantidad);
        price = Double.valueOf(cantidad);


        btnTransferencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intents = new Intent(card.this,TransferenciaBancaria.class);
                intents.putExtra("ahorro_amt", cantidad);
                intents.putExtra("ahorro_plz", plazo);
                startActivity(intents);


            }
        });

    }
}
