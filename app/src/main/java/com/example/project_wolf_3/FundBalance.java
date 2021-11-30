package com.example.project_wolf_3;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_wolf_3.model.FundModel;
import com.example.project_wolf_3.model.Posts;
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
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.juang.jplot.PlotBarritas;
import com.juang.jplot.PlotPastelito;
import com.juang.jplot.PlotPlanitoXY;

import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FundBalance extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private TextView btnRetiro;
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
    Context context;
    private Posts itemDetail;
    private TextView Balance;
    private TextView Inversion;
    private TextView Ganancia;
    private TextView Fecha;
    private TextView Retorno;
    private double inversionR;
    LineGraphSeries<DataPoint> series;
    ValueLineChart valueLineChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fund_balance);
        drawerLayout = findViewById(R.id.menupincipal);
        navigationView = findViewById(R.id.nav_view);
        menuIcon = findViewById(R.id.menu_icon);


        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();

        firebaseFirestore = FirebaseFirestore.getInstance();


        navigationDrawer();
        Inversion = findViewById(R.id.Inversion);
        Retorno = findViewById(R.id.retorno);
        Balance = findViewById(R.id.Balance);
        Ganancia = findViewById(R.id.Ganancia);
        Fecha = findViewById(R.id.date);
        btnRetiro = findViewById(R.id.new_retiro);

        itemDetail = (Posts) getIntent().getExtras().getSerializable("itemDetail");
        Inversion.setText(String.format("$%s", String.format("%,.2f", itemDetail.getAmount())));
        inversionR = itemDetail.getAmount();

        Date date_raw = itemDetail.getDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy");
        Fecha.setText(String.format("Fecha: %s", dateFormat.format(date_raw)));

        Ganancia.setText(String.format("$%s", String.format("%,.2f", itemDetail.getGananciap())));

//        progressBar.setVisibility(View.GONE);
        if(userID != null){
            profileImage = navigationView.getHeaderView(0).findViewById(R.id.user_image_nav);
            storageReference = FirebaseStorage.getInstance().getReference();
            StorageReference profileRef = storageReference.child("users/"+mAuth.getCurrentUser().getUid()+"/profile.jpg");
            profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                   Picasso.get().load(uri).into(profileImage);
                }
            });

           /* Query query = firebaseFirestore.collection("funds")
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
                    holder.Retorno.setText(String.format("$%s", String.format("%,.1f", model.getRoi_per()*100)));
                    holder.Balance.setText(String.format("$%s", String.format("%,.2f", model.getFinal_amount())));
                    holder.Inversion.setText(String.format("$%s", String.format("%,.2f", model.getInitial_amount())));
                    holder.Ganancia.setText(String.format("$%s", String.format("%,.2f", model.getRoi_vol())));
                    holder.Fecha.setText(String.valueOf(model.getDate()));

                }
            };
            mFirestoreList.setHasFixedSize(true);
            mFirestoreList.setLayoutManager(new LinearLayoutManager(this));
            mFirestoreList.setAdapter(adapter);*/

        }

        valueLineChart  = findViewById(R.id.graph);
        ValueLineSeries series = new ValueLineSeries();
        series.setColor(0xFF050212);
        series.addPoint(new ValueLinePoint("Lun",1f));
        series.addPoint(new ValueLinePoint("Mar",7.0f));
        series.addPoint(new ValueLinePoint("Mie",3.3f));
        series.addPoint(new ValueLinePoint("Jue",2.5f));
        series.addPoint(new ValueLinePoint("Vie",8.0f));
        series.addPoint(new ValueLinePoint("Sab",1.0f));
        series.addPoint(new ValueLinePoint("Dom",5.0f));
        valueLineChart.addSeries(series);
        valueLineChart.startAnimation();

        ImageView profile = findViewById(R.id.profileimg);
        profile.setOnClickListener(view -> {
            Intent colorsIntent = new Intent(FundBalance.this, MainActivity.class);
            startActivity(colorsIntent);
        });
        FloatingActionButton invest = findViewById(R.id.btnRegresar);
        invest.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), BalanceTotal.class));
            finish();

        });
        btnRetiro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FundBalance.this,RetirosActivity.class);
                intent.putExtra("CantidadR",inversionR);
                startActivity(intent);
            }
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


}
