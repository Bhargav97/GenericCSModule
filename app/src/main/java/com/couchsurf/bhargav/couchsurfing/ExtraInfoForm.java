package com.couchsurf.bhargav.couchsurfing;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ExtraInfoForm extends AppCompatActivity {
    Button submitButton;
    EditText phoneInput, addressInput;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    final private String PHONE_KEY = "phone";
    final private String ADDRESS_KEY = "address";
    final private String EMAIL_KEY = "email";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userinfo_layout);
        submitButton = findViewById(R.id.submitButton);
        mAuth = FirebaseAuth.getInstance();
        phoneInput = findViewById(R.id.phone);
        addressInput = findViewById(R.id.address);


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                String UID = currentUser.getUid();
                String email = currentUser.getEmail();
                String phoneNumber = phoneInput.getText().toString();
                String address = addressInput.getText().toString();
                Map<String, Object> newUser = new HashMap<>();
                newUser.put(EMAIL_KEY,email);
                newUser.put(PHONE_KEY, phoneNumber);
                newUser.put(ADDRESS_KEY, address);
                db = FirebaseFirestore.getInstance();
                db.collection("users").document(UID).set(newUser)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(ExtraInfoForm.this, "User Registered",
                                        Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ExtraInfoForm.this, "ERROR" + e.toString(),
                                        Toast.LENGTH_SHORT).show();
                                Log.d("TAG", e.toString());
                            }
                        });
                startActivity(new Intent(ExtraInfoForm.this, MainActivity.class));

            }
        });
    }
}
