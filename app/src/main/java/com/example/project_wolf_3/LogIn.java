package com.example.project_wolf_3;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LogIn extends AppCompatActivity {

    TextInputEditText textInputEditTextEmail, textInputEditTextPassword;
    Button buttonLogin;
    TextView textViewSignUp, textViewForgot;
    ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        textInputEditTextEmail = findViewById(R.id.email);
        textInputEditTextPassword = findViewById(R.id.password);
        buttonLogin = findViewById(R.id.btnLogin);
        textViewSignUp = findViewById(R.id.signupText);
        textViewForgot = findViewById(R.id.forgotText);
        progressBar = findViewById(R.id.progress);

        mAuth = FirebaseAuth.getInstance();
        mAuth.setLanguageCode("es");

        if(mAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), BalanceTotal.class));
            finish();

        }

        textViewSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), SignUp.class);
            startActivity(intent);
            finish();
        });

        textViewForgot.setOnClickListener(v -> {

            // Password reset dialog
            EditText resetMail = new EditText(v.getContext());
            AlertDialog.Builder psswdResetDialog = new AlertDialog.Builder(v.getContext());
            psswdResetDialog.setTitle("Recuperación de cuenta");
            psswdResetDialog.setMessage("Ingresa tu correo para recibir Link de reseteo");
            psswdResetDialog.setView(resetMail);

            psswdResetDialog.setPositiveButton("Si", (dialog, which) -> {

                // Extract email and reset link
                String mail = resetMail.getText().toString();
                mAuth.sendPasswordResetEmail(mail).addOnSuccessListener(aVoid -> Toast.makeText(LogIn.this, "Link de reseteo enviado a tu correo", Toast.LENGTH_SHORT).show()).addOnFailureListener(e -> Toast.makeText(LogIn.this, "Error! Link de reseteo no enviado" + e.getMessage(), Toast.LENGTH_SHORT).show());
            });

            psswdResetDialog.setNegativeButton("No", (dialog, which) -> {
                // Close the dialog
            });

            psswdResetDialog.create().show();

        });

        buttonLogin.setOnClickListener(this::onClick);

    }

    private void onClick(View v) {
        String email, password;
        email = Objects.requireNonNull(textInputEditTextEmail.getText()).toString().trim();
        password = Objects.requireNonNull(textInputEditTextPassword.getText()).toString().trim();

        if(TextUtils.isEmpty(email)){
            textInputEditTextEmail.setError("El correo es necesario");
            return;
        }

        if(TextUtils.isEmpty(password)){
            textInputEditTextPassword.setError("La contraseña es necesaria");
            return;
        }

        if(password.length() < 6){
            textInputEditTextPassword.setError("La contraseña debe ser de al menos 6 dígitos");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        //Authentication
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Toast.makeText(LogIn.this, "Registro Exitoso", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), BalanceTotal.class));
            } else{
                Toast.makeText(LogIn.this, "Error: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });

    }
}