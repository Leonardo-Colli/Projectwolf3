package com.example.project_wolf_3;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TransactionVerif extends AppCompatActivity {

    TextView verif_phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_verif);

        String current_phone = "9982417342";

        verif_phone = findViewById(R.id.verif_phone);

        verif_phone.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + current_phone));
            startActivity(intent);
        });

    }

    public void onVerifTransaction(View v){
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }
}