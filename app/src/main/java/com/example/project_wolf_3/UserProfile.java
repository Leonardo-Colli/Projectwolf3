package com.example.project_wolf_3;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class UserProfile extends AppCompatActivity {


    TextView fullname, balance, rank, verif_phone, verif_email, phone, email, back,logout;
    Button  password, profile;
    String username, lada, email_all;
    Double balancetotal;

    ImageView profileImage;

    FirebaseAuth fAuth;
    FirebaseFirestore db;
    String userID;

    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        fullname = findViewById(R.id.user_name);
        balance = findViewById(R.id.user_balance);
        //rank = findViewById(R.id.user_rank);
        phone = findViewById(R.id.button_phone);
        email = findViewById(R.id.button_email);
        back = findViewById(R.id.TxtBack);

        profileImage = findViewById(R.id.user_image);

        verif_phone = findViewById(R.id.phone_verif_text);
        verif_email = findViewById(R.id.email_verif_text);

        logout = findViewById(R.id.button_logout);

        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference profileRef = storageReference.child("users/"+fAuth.getCurrentUser().getUid()+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage);
            }
        });

        FirebaseUser user = fAuth.getCurrentUser();

        if(fAuth.getCurrentUser() == null){
            startActivity(new Intent(getApplicationContext(), LogIn.class));
            finish();
        } else {

            userID = fAuth.getCurrentUser().getUid();

            assert user != null;
            if (!user.isEmailVerified()) {
                verif_email.setVisibility(View.VISIBLE);
            } else{
                DocumentReference documentReference = db.collection("users").document(userID);
                documentReference.update("emailVerif", true);
            }


            DocumentReference documentReference = db.collection("users").document(userID);
            ListenerRegistration listenerRegistration = documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException e) {

                    assert value != null;
                    username = value.getString("username");
                    phone.setText(username);

                    balancetotal = value.getDouble("balance");
                    balance.setText(String.format("$%s", String.format("%,.2f", balancetotal)));
                    //balance.setText(String.valueOf(balancetotal));

                    lada = value.getString("lada");
                    fullname.setText(value.getString("fullname"));
                    //rank.setText(String.format("Rango: %s", value.getString("rank")));


                    // Email - extract first 10 characters
                    email_all = value.getString("email");
                    assert email_all != null;
                    String email_user = email_all.substring(0, Math.min(email_all.length(), 16));
                    email.setText(email_user);

                    Boolean phone_verif = value.getBoolean("phoneVerif");
                    Log.v("TAG", "phone_verif: " + phone_verif);

                    if (!phone_verif) {
                        verif_phone.setVisibility(View.VISIBLE);
                    }


                }

            });
            verif_phone.setOnClickListener(v -> {
                Intent phone = new Intent(getApplicationContext(), VerifyPhone.class);
                phone.putExtra("phone", "+"+ lada + username);
                startActivity(phone);
            });

            verif_email.setOnClickListener(v -> {
                user.sendEmailVerification().addOnSuccessListener(aVoid -> Toast.makeText(UserProfile.this, "Email de verificación enviado", Toast.LENGTH_SHORT).show()).addOnFailureListener(e -> Log.d("SignUpActivity", "onFailure: Email not sent" + e.getMessage()));
            });
            back.setOnClickListener(v -> {
                Intent colorsIntent = new Intent(UserProfile.this, MainActivity.class);
                startActivity(colorsIntent);
            });

            logout.setOnClickListener(v -> {
                listenerRegistration.remove();
                fAuth.signOut();
                Intent colorsIntent = new Intent(UserProfile.this, LogIn.class);
                startActivity(colorsIntent);
                finish();
            });


        }
        FloatingActionButton ButtonPass = findViewById(R.id.editpass);
        ButtonPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Password reset dialog
                EditText resetPassword = new EditText(v.getContext());

                AlertDialog.Builder psswdResetDialog = new AlertDialog.Builder(v.getContext());
                psswdResetDialog.setTitle("Recuperación de contraseña");
                psswdResetDialog.setMessage("Ingresa tu nueva contraseña, mínimo 6 dígitos");
                psswdResetDialog.setView(resetPassword);

                psswdResetDialog.setPositiveButton("Si", (dialog, which) -> {

                    // Extract email and reset link
                    String newPassword = resetPassword.getText().toString();
                    user.updatePassword(newPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(UserProfile.this, "Contraseña cambiada exitosamente", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UserProfile.this, "Error! Al cambiar la contraseña", Toast.LENGTH_SHORT).show();
                        }
                    });

                });

                psswdResetDialog.setNegativeButton("No", (dialog, which) -> {
                    // Close the dialog
                });

                psswdResetDialog.create().show();
            }
        });

        FloatingActionButton ButtonProfile = findViewById(R.id.editprofile);
        ButtonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), EditProfile.class);
                i.putExtra("fullname", fullname.getText().toString());
                i.putExtra("email", email_all);
                i.putExtra("username", username);
                i.putExtra("lada", lada);
                startActivity(i);
            }
        });
    }

}