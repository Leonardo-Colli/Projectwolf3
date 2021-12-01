package com.example.project_wolf_3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class RetirosActivity extends AppCompatActivity {

    private double cantidad;
    TextView CantidadR;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retiros);

        CantidadR = findViewById(R.id.monto);

        Bundle bundle = this.getIntent().getExtras();
        cantidad = (bundle.getDouble("CantidadR"));
        CantidadR.setText(String.valueOf(cantidad));
    }
}