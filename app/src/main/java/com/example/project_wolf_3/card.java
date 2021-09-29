package com.example.project_wolf_3;

import static java.lang.Double.parseDouble;
import static java.lang.Double.valueOf;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.view.CardInputWidget;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
    String id;
    // we need paymentIntentClientSecret to start transaction
    private String paymentIntentClientSecret;
    private Object fondos;
    private double FondosT;
    private Object currency;
    //declare stripe
    private Stripe stripe;

    Double amountDouble=null;

    private OkHttpClient httpClient;

    static ProgressDialog progressDialog;
    FirebaseAuth mAuth;
    private FirebaseFirestore db;
    String userID, fundId;
    double bal, btc_p0, eth_p0, alt_p0, btc_amt, eth_amt, alt_amt,
            btc_vol, eth_vol, alt_vol, initial_amount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);
        amountText = findViewById(R.id.amount_id);
        cardInputWidget = findViewById(R.id.cardInputWidget);
        payButton = findViewById(R.id.payButton);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Transaction in progress");
        progressDialog.setCancelable(false);
        httpClient = new OkHttpClient();

        Bundle bundle = this.getIntent().getExtras();
        cantidad = (bundle.getString("ahorro_amt"));
        plazo = bundle.getString("ahorro_plz");
        amountText.setText(cantidad);
        price = Double.valueOf(cantidad);


        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();

        Intent data = getIntent();
        fundId = data.getStringExtra("id");

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitInterface = retrofit.create(RetrofitInterface.class);

        if(fundId != null) {


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

                }
            });

        }

        //Initialize
        stripe = new Stripe(
                getApplicationContext(),
                Objects.requireNonNull("pk_test_51JZJIlDgaelGiJo8nxbAIkhV2f8pwAoTkZGMDD6Phyo7ETCfMlTOapBpLy1ajaMxMUr2SAvA1ODLBbXEyZpEHNYk00ubUgB1lD")
        );


        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get Amount
                amountDouble = valueOf(amountText.getText().toString());
                HashMap<String, List<Map<String, Double>>> payMap=new HashMap<>();
                HashMap<String, Double> map = new HashMap<>();
                Double num= Double.valueOf(1);
                Double num2 = Double.valueOf(350);
                List<Map<String,Double>> itemList =new ArrayList<>();
                map.put("quantity", num);
                //map.put("price", num2);
                itemList.add(map);
                payMap.put("item",itemList);


                retrofit2.Call<ValueResult> call = retrofitInterface.executeResult(payMap);
                call.enqueue(new retrofit2.Callback<ValueResult>() {
                    @Override
                    public void onResponse(retrofit2.Call<ValueResult> call, retrofit2.Response<ValueResult> response) {

                        ValueResult result = response.body();
                        AlertDialog.Builder builder = new AlertDialog.Builder(card.this);
                        builder.setTitle(String.valueOf(result.getPrice()));
                        builder.show();
                    }

                    @Override
                    public void onFailure(retrofit2.Call<ValueResult> call, Throwable t) {
                        Toast.makeText(card.this, "Error", Toast.LENGTH_LONG).show();
                    }
                });
                Toast.makeText(card.this,"Dinero: "+amountDouble, Toast.LENGTH_LONG).show();


                //call checkout to get paymentIntentClientSecret key
                startCheckout();
                progressDialog.show();
                onSaveNote();

            }
        });
    }
    public void onSaveNote(){
        Date plz_dt = Calendar.getInstance().getTime();

        DocumentReference documentReference = db.collection("users").document(userID);
        ListenerRegistration listenerRegistration = documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                assert value != null;
                bal = value.getLong("balance");
            }
        });

        // Filter if period is null
        String plzo = plazo;
        if(TextUtils.isEmpty(plzo)){
            Toast.makeText(card.this, "Error! El plazo es necesario", Toast.LENGTH_SHORT).show();
            return;
        }

        // Filter if amount is null
        String amount = cantidad;
        if(TextUtils.isEmpty(amount)){
            Toast.makeText(card.this, "Error! El monto es necesario", Toast.LENGTH_SHORT).show();
            return;
        }
        int amt = Integer.parseInt(amount);
        int plz = Integer.parseInt(plzo);
        // Filter number of payments
        if(plz < 6) {
            Toast.makeText(card.this, "Error! Plazo mínimo: 6 meses", Toast.LENGTH_SHORT).show();
        } else if(plz > 36) {
            Toast.makeText(card.this, "Error! Plazo máximo: 3 años", Toast.LENGTH_SHORT).show();
        } else {

            // Filter amount of payment
            if(amt < 100){
                Toast.makeText(card.this, "Error! Monto mínimo: $100", Toast.LENGTH_SHORT).show();
            } else if(amt > 100000) {
                Toast.makeText(card.this, "Error! Monto máximo: $100,000", Toast.LENGTH_SHORT).show();
            } else {




                double balT = bal + amt;
                documentReference.update("balance", balT);
                if(bal == 0.0){
                    documentReference.update("first_saving", plz_dt);
                }

                //Parameters

                double btc_mix = 0.4;
                double eth_mix = 0.3;
                double alt_mix = 0.3;
                double cash_com = 0.03;
                double exch_com = 0.01;
                double ref_com = 0.01;
                double own_com = 0.3;
                double btc_amt_new = amt * (1 - cash_com) * (1 - exch_com) * btc_mix;
                double eth_amt_new = amt * (1 - cash_com) * (1 - exch_com) * eth_mix;
                double alt_amt_new = amt * (1 - cash_com) * (1 - exch_com) * alt_mix;
                double fin_amt_new = btc_amt_new + eth_amt_new + alt_amt_new;


                if(fundId != null) {
                    double mix_new = amt/(bal + amt);

                    double btc_p0_new = 1010000;
                    double eth_p0_new = 36300;
                    double alt_p0_new = 1250;
                    double final_amount = btc_amt + btc_amt_new + eth_amt + eth_amt_new + alt_amt + alt_amt_new;

                    DocumentReference documentReference1 = db.collection("funds").document(userID).collection("savings").document(fundId);

                    documentReference1.update("btc_p0", btc_p0*(1 - mix_new) + btc_p0_new*mix_new);
                    documentReference1.update("eth_p0", eth_p0*(1 - mix_new) + eth_p0_new*mix_new);
                    documentReference1.update("alt_p0", alt_p0*(1 - mix_new) + alt_p0_new*mix_new);

                    documentReference1.update("btc_amt", btc_amt + btc_amt_new);
                    documentReference1.update("eth_amt", eth_amt + eth_amt_new);
                    documentReference1.update("alt_amt", alt_amt + alt_amt_new);

                    documentReference1.update("btc_vol", btc_vol + btc_amt_new/btc_p0_new);
                    documentReference1.update("eth_vol", eth_vol + eth_amt_new/eth_p0_new);
                    documentReference1.update("alt_vol", alt_vol + alt_amt_new/alt_p0_new);

                    documentReference1.update("initial_amount", initial_amount + amt);
                    documentReference1.update("final_amount", final_amount);
                    documentReference1.update("roi_per", (final_amount - initial_amount - amt) / (initial_amount + amt));
                    documentReference1.update("roi_vol", final_amount - initial_amount - amt);

                }else {


                    double btc_p0_new = 1000000;
                    double eth_p0_new = 36000;
                    double alt_p0_new = 1000;

                    // Create new saving
                    Map<String, Object> saving = new HashMap<>();
                    saving.put("date", plz_dt);
                    saving.put("months", plz);
                    saving.put("initial_amount", amt);
                    saving.put("final_amount", fin_amt_new);
                    saving.put("roi_per", (fin_amt_new - amt) / amt);
                    saving.put("roi_vol", fin_amt_new - amt);

                    saving.put("btc_mix", btc_mix);
                    saving.put("eth_mix", eth_mix);
                    saving.put("alt_mix", alt_mix);

                    saving.put("btc_amt", btc_amt_new);
                    saving.put("eth_amt", eth_amt_new);
                    saving.put("alt_amt", alt_amt_new);

                    saving.put("btc_vol", btc_amt_new/btc_p0_new);
                    saving.put("eth_vol", eth_amt_new/eth_p0_new);
                    saving.put("alt_vol", alt_amt_new/alt_p0_new);

                    saving.put("btc_p0", btc_p0_new);
                    saving.put("eth_p0", eth_p0_new);
                    saving.put("alt_p0", alt_p0_new);

                    saving.put("btc_pC", btc_p0_new);
                    saving.put("eth_pC", eth_p0_new);
                    saving.put("alt_pC", alt_p0_new);

                    saving.put("cash_com", cash_com);
                    saving.put("exch_com", exch_com);
                    saving.put("ref_com", ref_com);
                    saving.put("own_com", own_com);

                    db.collection("funds").document(userID).collection("savings").document(String.valueOf(plz_dt)).set(saving)
                            .addOnSuccessListener(aVoid -> Toast.makeText(card.this, "Ahorro Guardado", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(card.this, "Error! Ahorro no guardado", Toast.LENGTH_SHORT).show());

                }

                startActivity(new Intent(getApplicationContext(), TransactionVerif.class));

            }
        }

        // Parse string to int


    }

    private void startCheckout() {
        {
            // Create a PaymentIntent by calling the server's endpoint.
            MediaType mediaType = MediaType.get("application/json; charset=utf-8");
//        String json = "{"
//                + "\"currency\":\"usd\","
//                + "\"items\":["
//                + "{\"id\":\"photo_subscription\"}"
//                + "]"
//                + "}";
            double amount=amountDouble*100;
            Map<String,Object> payMap=new HashMap<>();
            Map<String,Object> itemMap=new HashMap<>();
            List<Map<String,Object>> itemList =new ArrayList<>();
            payMap.put("currency","MXN");
            itemMap.put("id","photo_subscription");
            itemMap.put("amount",amount);
            itemList.add(itemMap);
            payMap.put("items",itemList);
            String json = new Gson().toJson(payMap);
            RequestBody body = RequestBody.create(json, mediaType);
            Request request = new Request.Builder()
                    .url(BACKEND_URL + "create-payment-intent")
                    .post(body)
                    .build();
            httpClient.newCall(request)
                    .enqueue(new PayCallback(card.this));
           // fondos = itemMap.get("amount");

        }
    }

    private static final class PayCallback implements Callback {
        @NonNull
        private final WeakReference<card> activityRef;
        PayCallback(@NonNull card activity) {
            activityRef = new WeakReference<>(activity);
        }
        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException e) {
            progressDialog.dismiss();
            final card activity = activityRef.get();
            if (activity == null) {
                return;
            }
            activity.runOnUiThread(() ->
                    Toast.makeText(
                            activity, "Error: " + e.toString(), Toast.LENGTH_LONG
                    ).show()
            );
        }
        @Override
        public void onResponse(@NonNull Call call, @NonNull final Response response)
                throws IOException {
            final card activity = activityRef.get();
            if (activity == null) {
                return;
            }
            if (!response.isSuccessful()) {
                activity.runOnUiThread(() ->
                        Toast.makeText(
                                activity, "Error: " + response.toString(), Toast.LENGTH_LONG
                        ).show()
                );
            } else {
                activity.onPaymentSuccess(response);
            }
        }
    }

    private void onPaymentSuccess(@NonNull final Response response) throws IOException {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String>>(){}.getType();
        Map<String, String> responseMap = gson.fromJson(
                Objects.requireNonNull(response.body()).string(),
                type
        );
        paymentIntentClientSecret = responseMap.get("clientSecret");

        //once you get the payment client secret start transaction
        //get card detail
        PaymentMethodCreateParams params = cardInputWidget.getPaymentMethodCreateParams();
        if (params != null) {
            //now use paymentIntentClientSecret to start transaction
            ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams
                    .createWithPaymentMethodCreateParams(params, paymentIntentClientSecret);
            //start payment
            stripe.confirmPayment(card.this, confirmParams);
        }
        Log.i("TAG", "onPaymentSuccess: "+paymentIntentClientSecret);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Handle the result of stripe.confirmPayment
        stripe.onPaymentResult(requestCode, data, new PaymentResultCallback(this));

    }

    private final class PaymentResultCallback
            implements ApiResultCallback<PaymentIntentResult> {
        @NonNull private final WeakReference<card> activityRef;
        PaymentResultCallback(@NonNull card activity) {
            activityRef = new WeakReference<>(activity);
        }
        //If Payment is successful
        @Override
        public void onSuccess(@NonNull PaymentIntentResult result) {
            progressDialog.dismiss();
            final card activity = activityRef.get();
            if (activity == null) {
                return;
            }
            PaymentIntent paymentIntent = result.getIntent();
            PaymentIntent.Status status = paymentIntent.getStatus();
            if (status == PaymentIntent.Status.Succeeded) {
                // Payment completed successfully
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                Toast toast =Toast.makeText(activity, "Ordered Successful", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                double num = parseDouble(String.valueOf(fondos));
                FondosT = Math.round( num ) / 100;
                Toast.makeText(activity,"Tienes: "+ FondosT +" MXN "+" id: "+paymentIntentClientSecret,Toast.LENGTH_LONG).show();
            } else if (status == PaymentIntent.Status.RequiresPaymentMethod) {
                // Payment failed – allow retrying using a different payment method
                activity.displayAlert(
                        "Payment failed",
                        Objects.requireNonNull(paymentIntent.getLastPaymentError()).getMessage()
                );
            }
        }
        //If Payment is not successful
        @Override
        public void onError(@NonNull Exception e) {
            progressDialog.dismiss();
            final card activity = activityRef.get();
            if (activity == null) {
                return;
            }
            // Payment request failed – allow retrying using the same payment method
            activity.displayAlert("Error", e.toString());
        }
    }
    private void displayAlert(@NonNull String title,
                              @Nullable String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message);
        builder.setPositiveButton("Ok", null);
        builder.create().show();
    }
}
