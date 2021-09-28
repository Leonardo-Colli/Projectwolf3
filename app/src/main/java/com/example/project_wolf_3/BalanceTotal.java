package com.example.project_wolf_3;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class BalanceTotal extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    NavigationView navigationView;
    ImageView menuIcon;
    ImageView profileImage;
    DrawerLayout drawerLayout;
    StorageReference storageReference;

    private FirebaseFirestore firebaseFirestore;
    private RecyclerView mFirestoreList;
    private FirebaseAuth mAuth;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.balance_total);
        ImageView profile = findViewById(R.id.profileimg);

        drawerLayout = findViewById(R.id.menupincipal);
        navigationView = findViewById(R.id.nav_view);
        menuIcon = findViewById(R.id.menu_icon);
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        navigationDrawer();

        profileImage = navigationView.getHeaderView(0).findViewById(R.id.user_image_nav);

        storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference profileRef = storageReference.child("users/"+mAuth.getCurrentUser().getUid()+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage);
            }
        });
        profile.setOnClickListener(view -> {
            Intent colorsIntent = new Intent(BalanceTotal.this, UserProfile.class);
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
        navigationView.setCheckedItem(R.id.nav_balance);

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
                Intent colorsIntent = new Intent(BalanceTotal.this, UserProfile.class);
                startActivity(colorsIntent);
                break;
            case R.id.nav_balance:
                Intent numbersIntent = new Intent(BalanceTotal.this, BalanceTotal.class);
                startActivity(numbersIntent);
                break;
            case R.id.nav_inversiones:
                Intent inversionesIntent = new Intent(BalanceTotal.this, MainActivity.class);
                startActivity(inversionesIntent);
                break;

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
