package com.example.project_wolf_3;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class EditProfile extends AppCompatActivity {

    public static final String TAG = "TAG";
    EditText textInputEditTextFullname, textInputEditTextUsername,
            textInputEditTextEmail, textInputEditTextLada;
    Button buttonSave;
    TextView textViewBack, cancel;
    ProgressBar progressBar;

    ImageView profileImage;

    StorageReference storageReference;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Intent i = getIntent();
        String fullname = i.getStringExtra("fullname");
        String email = i.getStringExtra("email");
        String username = i.getStringExtra("username");
        String lada = i.getStringExtra("lada");

        fAuth = FirebaseAuth.getInstance();
        fAuth.setLanguageCode("es");
        fStore = FirebaseFirestore.getInstance();
        user =fAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();

        textInputEditTextFullname = findViewById(R.id.fullname);
        textInputEditTextUsername = findViewById(R.id.username);
        textInputEditTextEmail = findViewById(R.id.email);
        textInputEditTextLada = findViewById(R.id.lada);
        buttonSave = findViewById(R.id.buttonSave);
        cancel = findViewById(R.id.TxtBack);
        textViewBack = findViewById(R.id.loginText);
        progressBar = findViewById(R.id.progress);
        profileImage = findViewById(R.id.user_image);

        storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference profileRef = storageReference.child("users/"+fAuth.getCurrentUser().getUid()+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage);
            }
        });

        textInputEditTextFullname.setText(fullname);
        textInputEditTextUsername.setText(username);
        textInputEditTextEmail.setText(email);
        textInputEditTextLada.setText(lada);

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGallery, 1000);
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(textInputEditTextFullname.getText().toString().isEmpty() || textInputEditTextUsername.getText().toString().isEmpty()
                        || textInputEditTextEmail.getText().toString().isEmpty() || textInputEditTextLada.getText().toString().isEmpty()){
                    Toast.makeText(EditProfile.this, "Todos los campos son necesarios", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Update the info
                String email = textInputEditTextEmail.getText().toString();

                user.updateEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        DocumentReference documentReference = fStore.collection("users").document(user.getUid());
                        Map<String, Object> edited = new HashMap<>();
                        edited.put("email", email);
                        edited.put("username", textInputEditTextUsername.getText().toString());
                        edited.put("fullname", textInputEditTextFullname.getText().toString());
                        edited.put("lada", textInputEditTextLada.getText().toString());
                        documentReference.update(edited).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                startActivity(new Intent(getApplicationContext(), UserProfile.class));
                                finish();
                            }
                        });

                        Toast.makeText(EditProfile.this, "Perfil actualizado", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), UserProfile.class));
                finish();
            }
        });

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
                            Toast.makeText(EditProfile.this, "Contraseña cambiada exitosamente", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditProfile.this, "Error! Al cambiar la contraseña", Toast.LENGTH_SHORT).show();
                        }
                    });

                });

                psswdResetDialog.setNegativeButton("No", (dialog, which) -> {
                    // Close the dialog
                });

                psswdResetDialog.create().show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Identify result from galleryIntent and update profile image
        if(requestCode == 1000){
            if(resultCode == Activity.RESULT_OK){
                Uri imageUri = data.getData();
                profileImage.setImageURI(imageUri);

                uploadImagetoFirebase(imageUri);


            }

        }
    }


    private void uploadImagetoFirebase(Uri imageUri){
        StorageReference fileRef = storageReference.child("users/"+fAuth.getCurrentUser().getUid()+"/profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(profileImage);
                        Toast.makeText(EditProfile.this, "Imagen cambiada", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditProfile.this, "Error! Imagen sin cambiar", Toast.LENGTH_SHORT).show();
            }
        });

    }

}