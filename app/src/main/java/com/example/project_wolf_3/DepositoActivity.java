package com.example.project_wolf_3;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DepositoActivity extends AppCompatActivity {
    TextView btn,btnNotificar,btnListo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposito);
        btn = findViewById(R.id.btnEnviar);
        btnNotificar = findViewById(R.id.btnNotificar);
        btnListo = findViewById(R.id.btnListo);


        btn.setVisibility(View.GONE);
        btnListo.setVisibility(View.GONE);
    }
}