package com.example.project_wolf_3;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class NewSaving extends AppCompatActivity {

    EditText ahorro_amt, ahorro_plz;
    Button save;
    FirebaseAuth mAuth;
    private FirebaseFirestore db;
    String userID, fundId;
    double bal, btc_p0, eth_p0, alt_p0, btc_amt, eth_amt, alt_amt,
        btc_vol, eth_vol, alt_vol, initial_amount;


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


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EnviarDatos();
            }
        });

    }
    public void EnviarDatos() {
        String plazo = ahorro_plz.getText().toString();
        if(TextUtils.isEmpty(plazo)){
            Toast.makeText(NewSaving.this, "Error! El plazo es necesario", Toast.LENGTH_SHORT).show();
            return;
        }
        String amount = ahorro_amt.getText().toString();
        if(TextUtils.isEmpty(amount)){
            Toast.makeText(NewSaving.this, "Error! El monto es necesario", Toast.LENGTH_SHORT).show();
            return;
        }
       // int amt = Integer.parseInt(amount);
        //int plz = Integer.parseInt(plazo);
        Intent intent = new Intent(NewSaving.this,card.class);
        intent.putExtra("ahorro_plz", plazo);
        intent.putExtra("ahorro_amt", amount);
        startActivity(intent);
    }



}
