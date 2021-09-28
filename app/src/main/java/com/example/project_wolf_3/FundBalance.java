package com.example.project_wolf_3;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_wolf_3.model.FundModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class FundBalance extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private TextView mEmptyStateTextView;
    private FirebaseFirestore firebaseFirestore;
    private RecyclerView mFirestoreList;
    private FirestoreRecyclerAdapter adapter;
    private FirebaseAuth mAuth;
    String userID, fundId;

    StorageReference storageReference;
    ImageView profileImage;
    ImageView menuIcon;
    NavigationView navigationView;
    DrawerLayout drawerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ProgressBar progressBar = findViewById(R.id.loading_indicator);
        mEmptyStateTextView = findViewById(R.id.empty_view);

        drawerLayout = findViewById(R.id.menupincipal);
        navigationView = findViewById(R.id.nav_view);
        menuIcon = findViewById(R.id.menu_icon);

        Intent data = getIntent();
        fundId = data.getStringExtra("id");
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();

        firebaseFirestore = FirebaseFirestore.getInstance();
        mFirestoreList = findViewById(R.id.list);

        navigationDrawer();

        progressBar.setVisibility(View.GONE);
        if(userID != null){
            profileImage = navigationView.getHeaderView(0).findViewById(R.id.user_image_nav);
            progressBar.setVisibility(View.GONE);
            storageReference = FirebaseStorage.getInstance().getReference();
            StorageReference profileRef = storageReference.child("users/"+mAuth.getCurrentUser().getUid()+"/profile.jpg");
            profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(profileImage);
                }
            });
            Query query = firebaseFirestore.collection("funds")
                    .document(userID).collection("savings").whereEqualTo(FieldPath.documentId(), fundId);
            //Query query = firebaseFirestore.collection("users/" + id  + "/Citas/");//.whereEqualTo("id", 1);
            //Recycler Options
            FirestoreRecyclerOptions<FundModel> options = new FirestoreRecyclerOptions.Builder<FundModel>()
                    .setQuery(query, FundModel.class)
                    .build();

            adapter = new FirestoreRecyclerAdapter<FundModel, TransactionViewHolder>(options){

                @NonNull
                @Override
                public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_fund_balance, parent, false);
                    return new TransactionViewHolder(view);
                }

                @Override
                protected void onBindViewHolder(@NonNull TransactionViewHolder holder, int position, @NonNull FundModel model) {
                    holder.Retorno.setText(String.format("$%s", String.format("%,.1f", model.getRoi_vol())));
                    holder.Balance.setText(String.format("$%s", String.format("%,.2f", model.getFinal_amount())));
                    holder.Inversion.setText(String.format("$%s", String.format("%,.2f", model.getInitial_amount())));
                    holder.Ganancia.setText(String.format("$%s", String.format("%,.2f", model.getRoi_vol())));
                    holder.Fecha.setText(String.valueOf(model.getDate()));


                }
            };
            mFirestoreList.setHasFixedSize(true);
            mFirestoreList.setLayoutManager(new LinearLayoutManager(this));
            mFirestoreList.setAdapter(adapter);

        }

        ImageView profile = findViewById(R.id.button_profile);
        profile.setOnClickListener(view -> {
            Intent colorsIntent = new Intent(FundBalance.this, UserProfile.class);
            startActivity(colorsIntent);
        });
        FloatingActionButton invest = findViewById(R.id.btnRegresar);
        invest.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();

        });

    }

    private void navigationDrawer() {
        navigationView.bringToFront();
        // navigationView.setBackgroundColor(getResources().getColor(R.color.Fondo_menu));
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_inversiones);

        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(drawerLayout.isDrawerVisible(GravityCompat.START)){
                    drawerLayout.closeDrawer(GravityCompat.START);
                }else drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerVisible(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else
            super.onBackPressed();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_perfil:
                Intent colorsIntent = new Intent(FundBalance.this, UserProfile.class);
                startActivity(colorsIntent);
                break;
            case R.id.nav_balance:
                Intent numbersIntent = new Intent(FundBalance.this, BalanceTotal.class);
                startActivity(numbersIntent);
                break;
            case R.id.nav_inversiones:
                Intent inversionesIntent = new Intent(FundBalance.this, MainActivity.class);
                startActivity(inversionesIntent);
                break;

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    static class TransactionViewHolder extends RecyclerView.ViewHolder {

        private TextView Balance;
        private TextView Inversion;
        private TextView Ganancia;
        private TextView Fecha;
        private TextView Retorno;


        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);

            //Titulo = itemView.findViewById(R.id.titulo);
            Retorno = itemView.findViewById(R.id.retorno);
            Balance = itemView.findViewById(R.id.Balance);
            Inversion = itemView.findViewById(R.id.Inversion);
            Ganancia = itemView.findViewById(R.id.Ganancia);
            Fecha = itemView.findViewById(R.id.date);


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
