package com.example.project_wolf_3;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

public class VariablesBD extends AppCompatActivity {
    FirebaseAuth mAuth;
    private FirebaseFirestore db;
    String userID, fundId;
    public double bal, btc_p0, eth_p0, alt_p0, btc_amt, eth_amt, alt_amt,
            btc_vol, eth_vol, alt_vol, initial_amount, inversion;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enviardatos();


    }

    private void enviardatos() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();

        DocumentReference documentReference1 = db.collection("funds").document(userID).collection("savings").document(fundId);
        ListenerRegistration listenerRegistration1 = documentReference1.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                assert value != null;
                btc_p0 = value.getLong("btc_p0").doubleValue();
                eth_p0 = value.getLong("eth_p0").doubleValue();
                alt_p0 = value.getLong("alt_p0").doubleValue();
                btc_vol = value.getLong("btc_vol").doubleValue();
                eth_vol = value.getLong("eth_vol").doubleValue();
                alt_vol = value.getLong("alt_vol").doubleValue();
                btc_amt = value.getLong("btc_amt").doubleValue();
                eth_amt = value.getLong("eth_amt").doubleValue();
                alt_amt = value.getLong("alt_amt").doubleValue();
                initial_amount = value.getLong("initial_amount").doubleValue();

                Intent intents = new Intent(VariablesBD.this,TransferenciaBancaria.class);
                intents.putExtra("btc_p0", btc_p0);
                intents.putExtra("eth_p0", eth_p0);
                intents.putExtra("alt_p0", alt_p0);
                intents.putExtra("btc_vol", btc_vol);
                intents.putExtra("eth_vol", eth_vol);
                intents.putExtra("alt_vol", alt_vol);
                intents.putExtra("btc_amt", btc_amt);
                intents.putExtra("eth_amt", eth_amt);
                intents.putExtra("eth_p0", alt_amt);
                intents.putExtra("initial_amount", initial_amount);
                startActivity(intents);
            }
        });
    }

}
