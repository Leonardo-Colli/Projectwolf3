package com.example.project_wolf_3;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignUp extends AppCompatActivity {

    TextInputEditText textInputEditTextFullname, textInputEditTextUsername, textInputEditTextPassword,
            textInputEditTextEmail, textInputEditTextLada;
    Button buttonSignUp;
    TextView textViewLogin;
    ProgressBar progressBar;
    String userID;

    private FirebaseAuth mAuth;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        textInputEditTextFullname = findViewById(R.id.fullname);
        textInputEditTextUsername = findViewById(R.id.username);
        textInputEditTextPassword = findViewById(R.id.password);
        textInputEditTextEmail = findViewById(R.id.email);
        textInputEditTextLada = findViewById(R.id.lada);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        textViewLogin = findViewById(R.id.loginText);
        progressBar = findViewById(R.id.progress);

        mAuth = FirebaseAuth.getInstance();
        mAuth.setLanguageCode("es");

        db = FirebaseFirestore.getInstance();

        if(mAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        textViewLogin.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), LogIn.class);
            startActivity(intent);
            finish();
        });

        buttonSignUp.setOnClickListener(v -> {
            String fullname, username, password, email, lada;
            fullname = Objects.requireNonNull(textInputEditTextFullname.getText()).toString().trim();
            username = Objects.requireNonNull(textInputEditTextUsername.getText()).toString().trim();
            email = Objects.requireNonNull(textInputEditTextEmail.getText()).toString().trim();
            password = Objects.requireNonNull(textInputEditTextPassword.getText()).toString().trim();
            lada = Objects.requireNonNull(textInputEditTextLada.getText()).toString().trim();

            Log.d("TAG", "onSuccess: " + lada + username);

            if(TextUtils.isEmpty(email)){
                textInputEditTextEmail.setError("El correo es necesario");
                return;
            }

            if(TextUtils.isEmpty(password)){
                textInputEditTextPassword.setError("La contraseña es necesaria");
                return;
            }

            if(TextUtils.isEmpty(fullname)){
                textInputEditTextFullname.setError("El nombre es necesario");
                return;
            }

            if(TextUtils.isEmpty(lada)){
                textInputEditTextLada.setError("La lada es necesaria");
                return;
            }

            if(TextUtils.isEmpty(username)){
                textInputEditTextUsername.setError("El teléfono es necesario");
                return;
            }

            if(password.length() < 6){
                textInputEditTextPassword.setError("La contraseña debe ser de al menos 6 dígitos");
                return;
            }

            if(username.length() != 10){
                textInputEditTextUsername.setError("El teléfono debe ser de 10 dígitos");
                return;
            }

            progressBar.setVisibility(View.VISIBLE);

            // Creation of new User
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if(task.isSuccessful()){


                    // Saving profile in database
                    userID = mAuth.getCurrentUser().getUid();
                    DocumentReference documentReference = db.collection("users").document(userID);

                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("fullname", fullname);
                    userMap.put("email", email);
                    userMap.put("username", username);
                    userMap.put("lada", lada);
                    userMap.put("phoneVerif", false);
                    userMap.put("emailVerif", false);
                    userMap.put("timeVerif", false);
                    userMap.put("rank", "Cachorro");
                    userMap.put("balance", 0);
                    userMap.put("inversion", 0);

                    documentReference.set(userMap).addOnSuccessListener(aVoid -> {
                        Toast.makeText(SignUp.this, "Registro Exitoso", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignUp.this, UserProfile.class));
                    }).addOnFailureListener(e -> Log.d("TAG", "onFailure: " + e.toString()));

                    // Send verification email
                    FirebaseUser user = mAuth.getCurrentUser();
                    user.sendEmailVerification().addOnSuccessListener(aVoid -> Toast.makeText(SignUp.this, "Email de verificación enviado", Toast.LENGTH_SHORT).show()).addOnFailureListener(e -> Log.d("SignUpActivity", "onFailure: Email not sent" + e.getMessage()));
                    Log.d("TAG", "onSuccess: " + lada + username);

                } else {
                    Toast.makeText(SignUp.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            });


        });
    }

}