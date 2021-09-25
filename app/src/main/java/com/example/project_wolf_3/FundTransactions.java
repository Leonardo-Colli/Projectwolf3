package com.example.project_wolf_3;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_wolf_3.model.TransactionModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FundTransactions extends AppCompatActivity {

    private static final int EARTHQUAKE_LOADER_ID = 1;

    private static final String USGS_REQUEST_URL = "https://api.bitso.com/v3/ticker/";
    String bitsoKey = "whkjNlbaki";
    String bitsoSecret = "89fceabf4e7e86ba7cbf22a9977beb9f";
    long nonce = System.currentTimeMillis();
    String HTTPMethod = "GET";
    String RequestPath = "/v3/ticker/";
    String JSONPayload = "";

    /** TextView that is displayed when the list is empty */
    private TextView mEmptyStateTextView;

    private FirebaseFirestore firebaseFirestore;
    private RecyclerView mFirestoreList;

    String userID;
    private FirebaseAuth mAuth;

    private FirestoreRecyclerAdapter adapter;

    int fund_position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fund_transactions);

        ProgressBar progressBar = findViewById(R.id.loading_indicator);
        mEmptyStateTextView = findViewById(R.id.empty_view);

        Intent data = getIntent();
        fund_position = Integer.parseInt(data.getStringExtra("fund"));

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();


        firebaseFirestore = FirebaseFirestore.getInstance();
        mFirestoreList = findViewById(R.id.list);

        if(userID != null){
            progressBar.setVisibility(View.GONE);

            //Query
            Query query = firebaseFirestore.collection("products/" + userID  + "/individual_funds/").whereEqualTo("id", fund_position);

            //Recycler Options
            FirestoreRecyclerOptions<TransactionModel> options = new FirestoreRecyclerOptions.Builder<TransactionModel>()
                    .setQuery(query, TransactionModel.class)
                    .build();

            adapter = new FirestoreRecyclerAdapter<TransactionModel, TransactionViewHolder> (options){

                @NonNull
                @Override
                public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
                    return new TransactionViewHolder(view);
                }

                @Override
                protected void onBindViewHolder(@NonNull TransactionViewHolder holder, int position, @NonNull TransactionModel model) {
                    holder.objective.setText(model.getObjective());
                    holder.initial_amount.setText(String.format("$%s", String.format("%,.2f", model.getInitial_amount())));
                    holder.final_amount.setText(String.format("$%s", String.format("%,.2f", model.getFinal_amount())));
                    if(model.getDate() != null) {
                        Date date_raw = model.getDate();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy");
                        holder.date.setText(String.format(" %s", dateFormat.format(date_raw)));
                    }
                }
            };

            mFirestoreList.setHasFixedSize(true);
            mFirestoreList.setLayoutManager(new LinearLayoutManager(this));
            mFirestoreList.setAdapter(adapter);

        } else {
            progressBar.setVisibility(View.GONE);
            mEmptyStateTextView.setText("Conexión no disponible");
        }


        // Find the View that shows the numbers category
        Button invest = findViewById(R.id.button_invest);

        // Set a click listener on that View
        // The code in this method will be executed when the numbers View is clicked on.
        invest.setOnClickListener(view -> {
            Intent numbersIntent = new Intent(FundTransactions.this, NuevoAhorro.class);
            startActivity(numbersIntent);

        });

        // Find the View that shows the numbers category
        Button profile = findViewById(R.id.button_profile);

        // Set a click listener on that View
        // The code in this method will be executed when the numbers View is clicked on.
        profile.setOnClickListener(view -> {
            Intent colorsIntent = new Intent(FundTransactions.this, UserProfile.class);
            startActivity(colorsIntent);
        });

    }

    private class TransactionViewHolder extends RecyclerView.ViewHolder {

        private TextView objective;
        private TextView initial_amount;
        private TextView final_amount;
        private TextView date;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);

            objective = itemView.findViewById(R.id.objective);
            initial_amount = itemView.findViewById(R.id.price_zero);
            final_amount = itemView.findViewById(R.id.price_current);
            date = itemView.findViewById(R.id.date_zero);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

}