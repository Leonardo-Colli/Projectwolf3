package com.example.project_wolf_3;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_wolf_3.model.FundModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Date;
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{


    private static final int EARTHQUAKE_LOADER_ID = 1;

    private static final String USGS_REQUEST_URL = "https://api.bitso.com/v3/ticker/";
    String bitsoKey = "whkjNlbaki";
    String bitsoSecret = "89fceabf4e7e86ba7cbf22a9977beb9f";
    long nonce = System.currentTimeMillis();
    String HTTPMethod = "GET";
    String RequestPath = "/v3/ticker/";
    String JSONPayload = "";
    Date pos_fund;
    TextView Balance;
    TextView fullname;

    ImageView profileImage;
    ImageView menuIcon;
    NavigationView navigationView;
    DrawerLayout drawerLayout;

    String userID;
    String username;
    FundAdapter adapter;
    StorageReference storageReference;
    FirebaseAuth fAuth;
    FirebaseFirestore db;
    private RecyclerView mFirestoreList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ProgressBar progressBar = findViewById(R.id.loading_indicator);
        TextView mEmptyStateTextView = findViewById(R.id.empty_view);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        drawerLayout = findViewById(R.id.menupincipal);
        navigationView = findViewById(R.id.nav_view);
        menuIcon = findViewById(R.id.menu_icon);

        //toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        navigationDrawer();

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        mFirestoreList = findViewById(R.id.list);
        FirebaseUser user = fAuth.getCurrentUser();
        if(userID != null){
            profileImage = navigationView.getHeaderView(0).findViewById(R.id.user_image_nav);
            progressBar.setVisibility(View.GONE);
            storageReference = FirebaseStorage.getInstance().getReference();
            StorageReference profileRef = storageReference.child("users/"+fAuth.getCurrentUser().getUid()+"/profile.jpg");
            profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(profileImage);
                }
            });

            //Query
            Query query = firebaseFirestore.collection("funds/" + userID + "/savings/");

            //Recycler Options
            FirestoreRecyclerOptions<FundModel> options = new FirestoreRecyclerOptions.Builder<FundModel>()
                    .setQuery(query, FundModel.class)
                    .build();

            adapter = new FundAdapter(options);
            mFirestoreList.setHasFixedSize(true);
            mFirestoreList.setLayoutManager(new LinearLayoutManager(this));
            mFirestoreList.setAdapter(adapter);
            adapter.setOnItemClickListener(new FundAdapter.onItemClickListener() {
                @Override
                public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                    String docId = documentSnapshot.getId();
                    Intent doc = new Intent(getApplicationContext(), FundBalance.class);
                    doc.putExtra("id", docId);
                    startActivity(doc);
                }
            });
            DocumentReference documentReference = db.collection("users").document(userID);
            ListenerRegistration listenerRegistration = documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException e) {

                    assert value != null;
                    TextView txtV_Username = (TextView) navigationView.getHeaderView(0).findViewById(R.id.user_name);
                    txtV_Username.setText(value.getString("fullname"));

                }
                });

        } else {
            progressBar.setVisibility(View.GONE);
            mEmptyStateTextView.setText("Conexión no disponible");
        }




        Balance = findViewById(R.id.new_saving);
        Balance.setOnClickListener(view -> {
            Intent numbersIntent = new Intent(MainActivity.this, NewSaving.class);
            startActivity(numbersIntent);
        });
        ImageView profile = findViewById(R.id.button_profile);
        profile.setOnClickListener(view -> {
            Intent colorsIntent = new Intent(MainActivity.this, UserProfile.class);
            startActivity(colorsIntent);
        });

    }

    private void navigationDrawer() {
        navigationView.bringToFront();
        // navigationView.setBackgroundColor(getResources().getColor(R.color.Fondo_menu));
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_inversiones);
        navigationView.setItemTextColor(ColorStateList.valueOf(getResources().getColor(R.color.white)));

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
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_perfil:
                Intent colorsIntent = new Intent(MainActivity.this, UserProfile.class);
                startActivity(colorsIntent);
                break;
            case R.id.nav_balance:
                Intent numbersIntent = new Intent(MainActivity.this, BalanceTotal.class);
                startActivity(numbersIntent);
                break;
            case R.id.nav_transaccion:
                Intent fondosIntent = new Intent(MainActivity.this, TablaEstatus.class);
                startActivity(fondosIntent);
                break;
            case R.id.nav_inversiones:
                Intent inversionesIntent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(inversionesIntent);
                break;

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

}