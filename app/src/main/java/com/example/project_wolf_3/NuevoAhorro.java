package com.example.project_wolf_3;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class NuevoAhorro extends AppCompatActivity {
    RadioButton rdbtn_sem, rdbtn_sem_r, rdbtn_sem_m;
    RadioButton rdbtn_pla, rdbtn_pla_r, rdbtn_pla_m;
    EditText ahorro_nm, ahorro_amt, ahorro_plz;

    private int tipo_plz = 1;

    private FirebaseFirestore db;

    String userID;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nuevo_ahorro);

        rdbtn_sem = findViewById(R.id.rdbtn_semanal);
        rdbtn_sem_r = findViewById(R.id.rdbtn_semanal_r);
        rdbtn_sem_m = findViewById(R.id.rdbtn_semanal_m);

        ahorro_nm = findViewById(R.id.objective);
        ahorro_amt = findViewById(R.id.monto);
        ahorro_plz = findViewById(R.id.plazos);

        db = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();

        userID = mAuth.getCurrentUser().getUid();

    }

    public void onRdbtnClickedSemanal(View view){
        boolean isSelected = ((AppCompatRadioButton)view).isChecked();
        switch (view.getId()){
            case R.id.rdbtn_semanal:
                if(isSelected){
                    rdbtn_sem.setTextColor(Color.WHITE);
                    rdbtn_sem_m.setTextColor(Color.parseColor("#009688"));
                    rdbtn_sem_r.setTextColor(Color.parseColor("#009688"));
                    tipo_plz = 1;
                }
                break;
            case R.id.rdbtn_semanal_m:
                if(isSelected){
                    rdbtn_sem.setTextColor(Color.parseColor("#009688"));
                    rdbtn_sem_m.setTextColor(Color.WHITE);
                    rdbtn_sem_r.setTextColor(Color.parseColor("#009688"));
                    tipo_plz = 2;
                }
                break;
            case R.id.rdbtn_semanal_r:
                if(isSelected){
                    rdbtn_sem.setTextColor(Color.parseColor("#009688"));
                    rdbtn_sem_m.setTextColor(Color.parseColor("#009688"));
                    rdbtn_sem_r.setTextColor(Color.WHITE);
                    tipo_plz = 3;
                }
                break;
        }
    }

    public void onSaveNote(View v){
        Date plz_dt = Calendar.getInstance().getTime();
        String obj = ahorro_nm.getText().toString();

        //Filter if name is null
        if(TextUtils.isEmpty(obj)){
            Toast.makeText(NuevoAhorro.this, "Error! El objetivo es necesario", Toast.LENGTH_SHORT).show();
            return;
        }

        // Filter if period is null
        String plazo = ahorro_plz.getText().toString();
        if(TextUtils.isEmpty(plazo)){
            Toast.makeText(NuevoAhorro.this, "Error! El plazo es necesario", Toast.LENGTH_SHORT).show();
            return;
        }

        // Filter if amount is null
        String amount = ahorro_amt.getText().toString();
        if(TextUtils.isEmpty(amount)){
            Toast.makeText(NuevoAhorro.this, "Error! El monto es necesario", Toast.LENGTH_SHORT).show();
            return;
        }

        // Parse string to int
        int amt = Integer.parseInt(amount);
        int plz = Integer.parseInt(plazo);


        // Filter number of payments
        if((tipo_plz == 1 && plz < 26) || (tipo_plz == 2 && plz < 13) || (tipo_plz == 3 && plz < 6)){
            Toast.makeText(NuevoAhorro.this, "Error! Plazo mínimo 6 meses", Toast.LENGTH_SHORT).show();
        } else if((tipo_plz == 1 && plz > 260) || (tipo_plz == 2 && plz > 130) || (tipo_plz == 3 && plz > 60)) {
            Toast.makeText(NuevoAhorro.this, "Error! Plazo máximo 5 años", Toast.LENGTH_SHORT).show();
        } else {

            // Filter amount of payment
            if((tipo_plz == 1 && amt < 25) || (tipo_plz == 2 && amt < 50) || (tipo_plz == 3 && amt < 100)){
                Toast.makeText(NuevoAhorro.this, "Error! Monto mínimo $100 al mes", Toast.LENGTH_SHORT).show();
            } else if((tipo_plz == 1 && amt > 25000) || (tipo_plz == 2 && amt > 50000) || (tipo_plz == 3 && amt > 100000)) {
                Toast.makeText(NuevoAhorro.this, "Error! Monto máximo $100,000 al mes", Toast.LENGTH_SHORT).show();
            } else {

                double cash_tax = 0.03;
                double exch_tax = 0.01;
                double ref_tax = 0.01;
                double wolf_tax = 0.30;

                //Create mapping of colums and values
                Map<String, Object> note = new HashMap<>();
                note.put("objective", obj);
                note.put("id", 0);
                note.put("periods", plz);
                note.put("period_type", tipo_plz);
                note.put("date", plz_dt);
                note.put("initial_amount", amt);
                note.put("final_amount", amt);
                note.put("btc", 0.0001);
                note.put("eth", 0.001);
                note.put("ada", 0.01);
                note.put("price_btc", 1000000);
                note.put("price_eth", 36000);
                note.put("price_ada", 1000);



                Map<String, Object> invest = new HashMap<>();
                invest.put("id", plz_dt);
                invest.put("type", "buy");
                invest.put("date", plz_dt);
                invest.put("initial_amount", amt);
                invest.put("final_amount", amt);
                invest.put("btc", 0.0001);
                invest.put("eth", 0.001);
                invest.put("ada", 0.01);
                invest.put("price_btc", 1000000);
                invest.put("price_eth", 36000);
                invest.put("price_ada", 1000);


                db.collection("products").document(userID).collection("individual_funds").document(obj).set(note)
                        .addOnSuccessListener(aVoid -> Toast.makeText(NuevoAhorro.this, "Fondo Guardado", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(NuevoAhorro.this, "Error!", Toast.LENGTH_SHORT).show());

                db.collection("products").document(userID).collection("individual_funds").document(obj).collection("transactions").document(String.valueOf(plz_dt)).set(invest)
                        .addOnSuccessListener(aVoid -> Toast.makeText(NuevoAhorro.this, "Fondo Guardado", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(NuevoAhorro.this, "Error!", Toast.LENGTH_SHORT).show());


                /**Add values to database
                db.collection("transactions").document(userID).collection(obj).document(nameInvest).set(invest)
                        .addOnSuccessListener(aVoid -> Toast.makeText(NuevoAhorro.this, "Ahorro Guardado", Toast.LENGTH_LONG).show()).addOnFailureListener(e -> {
                            Toast.makeText(NuevoAhorro.this, "Error!", Toast.LENGTH_SHORT).show();
                        });
                 */


                startActivity(new Intent(getApplicationContext(), MainActivity.class));

            }
        }
    }


}
