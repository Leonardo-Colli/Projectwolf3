package com.example.project_wolf_3;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Ahorro extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ahorro_main);

        // Find the View that shows the numbers category
        TextView numbers = findViewById(R.id.t_ahorro_ind);

        // Set a click listener on that View
        // The code in this method will be executed when the numbers View is clicked on.
        numbers.setOnClickListener(view -> {
            Intent numbersIntent = new Intent(Ahorro.this, NuevoAhorro.class);
            startActivity(numbersIntent);

        });

        // Find the View that shows the numbers category
        TextView colors = findViewById(R.id.t_ahorro_gru);

        // Set a click listener on that View
        // The code in this method will be executed when the numbers View is clicked on.
        colors.setOnClickListener(view -> {
            Intent colorsIntent = new Intent(Ahorro.this, NuevoAhorroGrup.class);
            startActivity(colorsIntent);
        });


    }


}
