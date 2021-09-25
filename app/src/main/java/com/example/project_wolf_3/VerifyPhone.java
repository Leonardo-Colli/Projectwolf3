package com.example.project_wolf_3;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class VerifyPhone extends AppCompatActivity {

    EditText otpNumOne, otpNumTwo, otpNumThree, otpNumFour, otpNumFive, otpNumSix;
    Button  verifyPhone, resendOTP;
    Boolean otpValid = true;
    ProgressBar progressBar;
    String userID;

    TextView textViewEditProfile;

    String verificationId;
    String phone;

    FirebaseAuth fAuth;
    FirebaseFirestore db;
    PhoneAuthProvider.ForceResendingToken token;

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verif);

        Intent data = getIntent();
        phone = data.getStringExtra("phone");

        Log.i("TAG", "phone" + phone);

        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        fAuth.setLanguageCode("es");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            userID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();


            //Verifying phone callbacks
            mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                @Override
                public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent(s, forceResendingToken);

                    verificationId = s;
                    token = forceResendingToken;

                    resendOTP.setVisibility(View.GONE);
                }

                @Override
                public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                    super.onCodeAutoRetrievalTimeOut(s);

                    resendOTP.setVisibility(View.VISIBLE);
                }

                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                    String code = phoneAuthCredential.getSmsCode();
                    if(code != null){
                        progressBar.setVisibility(View.VISIBLE);
                        verifyAuthentication(phoneAuthCredential);
                        resendOTP.setVisibility(View.GONE);
                    }



                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    Toast.makeText(VerifyPhone.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            };

            sendOTP(phone);

        } else {
            // No user is signed in
            startActivity(new Intent(getApplicationContext(), LogIn.class));
            finish();
        }

        otpNumOne = findViewById(R.id.otpNumberOne);
        otpNumTwo = findViewById(R.id.otpNumberTwo);
        otpNumThree = findViewById(R.id.otpNumberThree);
        otpNumFour = findViewById(R.id.otpNumberFour);
        otpNumFive = findViewById(R.id.otpNumberFive);
        otpNumSix = findViewById(R.id.otpNumberSix);

        progressBar = findViewById(R.id.progress);

        verifyPhone = findViewById(R.id.btnVerif);
        resendOTP = findViewById(R.id.btnResend);
        textViewEditProfile = findViewById(R.id.profileText);

        textViewEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), EditProfile.class);
            startActivity(intent);
            finish();
        });

        verifyPhone.setOnClickListener(v -> {
            //Validate fields are not empty
            validateField(otpNumOne);
            validateField(otpNumTwo);
            validateField(otpNumThree);
            validateField(otpNumFour);
            validateField(otpNumFive);
            validateField(otpNumSix);

            // Verify password in phone
            if(otpValid){
                String otp = otpNumOne.getText().toString()+otpNumTwo.getText().toString()+otpNumThree.getText().toString()
                        +otpNumFour.getText().toString()+otpNumFive.getText().toString()+otpNumSix.getText().toString();

                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);

                verifyAuthentication(credential);

            }
        });


        resendOTP.setOnClickListener(v -> resendOTP(phone));

    }

    public void sendOTP(String phoneNumber){
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(fAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    public void resendOTP(String phoneNumber){
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(fAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)
                        .setForceResendingToken(token)// OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    public void validateField(EditText field){
        if(field.getText().toString().isEmpty()){
            field.setError("Requerido");
            otpValid = false;
        } else {
            otpValid = true;
        }
    }

    public void verifyAuthentication(PhoneAuthCredential credential){
        Objects.requireNonNull(fAuth.getCurrentUser()).linkWithCredential(credential).addOnSuccessListener(authResult -> {
            DocumentReference documentReference = db.collection("users").document(userID);
            documentReference.update("phoneVerif", true);
            Toast.makeText(VerifyPhone.this, "Cuenta creada y verificada", Toast.LENGTH_SHORT).show();
            startActivity(new Intent (getApplicationContext(), UserProfile.class));
            finish();
        });
    }

}
