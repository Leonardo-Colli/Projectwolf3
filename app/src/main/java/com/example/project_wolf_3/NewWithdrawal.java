package com.example.project_wolf_3;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class NewWithdrawal extends AppCompatActivity {

    EditText ahorro_amt;

    private FirebaseFirestore db;
    String userID;
    long bal;

    String fund_position;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_withdrawal);

        ahorro_amt = findViewById(R.id.monto);

        db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();

        Intent data = getIntent();
        fund_position = (data.getStringExtra("fund"));

    }

    public void onWithdrawal(View v){
        Date plz_dt = Calendar.getInstance().getTime();

        // Filter if amount is null
        String amount = ahorro_amt.getText().toString();
        if(TextUtils.isEmpty(amount)){
            Toast.makeText(NewWithdrawal.this, "Error! El monto es necesario", Toast.LENGTH_SHORT).show();
            return;
        }

        // Parse string to int
        int amt = Integer.parseInt(amount);

        DocumentReference documentReference = db.collection("funds/").document(userID).collection("/savings/").document(fund_position);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        bal = document.getLong("final_amount");
                    }
                }
            }
        });


        // Filter amount of payment
        if(amt < 100){
            Toast.makeText(NewWithdrawal.this, "Error! Monto mínimo: $100", Toast.LENGTH_SHORT).show();
        } else if(amt > bal) {
            Toast.makeText(NewWithdrawal.this, "Error! Monto máximo: $" + bal, Toast.LENGTH_SHORT).show();
        } else {

            documentReference.update("balance", bal - amt);

            DocumentReference documentSnapshot = db.collection("funds/").document(userID).collection("/savings/").document(fund_position);
            documentReference.get().addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        bal = document.getLong("balance");
                        Toast.makeText(NewWithdrawal.this, "Balance: $" + bal, Toast.LENGTH_SHORT).show();
                    }
                }
            });

            // Volume of coins
            Map<String, Object> coin_vol = new HashMap<>();
            coin_vol.put("btc_vol", -0.0001);
            coin_vol.put("eth_vol", -0.001);
            coin_vol.put("alt_vol", -0.01);

            // Price Zero of coins
            Map<String, Object> coin_p0 = new HashMap<>();
            coin_p0.put("btc_p0", 1000000);
            coin_p0.put("eth_p0", 36000);
            coin_p0.put("alt_p0", 1000);

            // Price Current of coins
            Map<String, Object> coin_pC = new HashMap<>();
            coin_pC.put("btc_pC", 1001000);
            coin_pC.put("eth_pC", 36360);
            coin_pC.put("alt_pC", 2000);

            // Commissions to be deducted
            Map<String, Object> commission = new HashMap<>();
            commission.put("cash_com", 0.03);
            commission.put("exch_com", 0.01);
            commission.put("ref_com", 0.01);
            commission.put("own_com", 0.3);

            // Create new saving
            Map<String, Object> saving = new HashMap<>();
            saving.put("date", plz_dt);
            saving.put("months", plz_dt);
            saving.put("initial_amount", amt);
            saving.put("final_amount", 1.002275*amt);
            saving.put("roi_per", 0.002275);
            saving.put("roi_vol", 0.002275*amt);
            saving.put("coin_vol", coin_vol);
            saving.put("coin_p0", coin_p0);
            saving.put("coin_pC", coin_pC);
            saving.put("commission", commission);


            //db.collection("savings").document(userID).collection("transactions").document(String.valueOf(plz_dt)).set(saving)
                   // .addOnSuccessListener(aVoid -> Toast.makeText(NewWithdrawal.this, "Ahorro Guardado", Toast.LENGTH_SHORT).show())
                  //  .addOnFailureListener(e -> Toast.makeText(NewWithdrawal.this, "Error! Ahorro no guardado", Toast.LENGTH_SHORT).show());


            startActivity(new Intent(getApplicationContext(), TransactionVerif.class));

        }

    }


}
