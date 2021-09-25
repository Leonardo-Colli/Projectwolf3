package com.example.project_wolf_3;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.getbase.floatingactionbutton.FloatingActionButton;

public class BalanceTotal extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.balance_total);
        ImageView profile = findViewById(R.id.profileimg);
        profile.setOnClickListener(view -> {
            Intent colorsIntent = new Intent(BalanceTotal.this, UserProfile.class);
            startActivity(colorsIntent);
        });
        FloatingActionButton invest = findViewById(R.id.btnRegresar);
        invest.setOnClickListener(view -> {
            Intent numbersIntent = new Intent(BalanceTotal.this, MainActivity.class);
            startActivity(numbersIntent);

        });
    }
}
