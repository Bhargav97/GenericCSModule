package com.couchsurf.bhargav.couchsurfing;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class RegisterNewCouch extends AppCompatActivity {
    android.support.v7.widget.Toolbar toolbar2;
    static ActionBar actionBar;
    static public String UID;
    static SharedPreferences sharedpreferences;
    public static void setTitleToHome(){
        actionBar.setTitle("Register new couch");
    }
    @Override
    public boolean onSupportNavigateUp() {
        startActivity(new Intent(RegisterNewCouch.this,MainActivity.class));
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_new_couch);
        toolbar2 = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar2);
        toolbar2.setTitleTextColor(Color.parseColor("#FFFFFF"));
        actionBar = getSupportActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if(sharedpreferences.getString("UID","").trim().equals(""))
            Toast.makeText(RegisterNewCouch.this, "Error retrieving UID",Toast.LENGTH_LONG).show();
        else
            UID = sharedpreferences.getString("UID","");

        setTitleToHome();
        getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).add(R.id.regFragContainerImage, new RegisterImageone(),"REG_IMAGE_ONE").commit();
        getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).add(R.id.refFragContainerRegInfo, new RegisterDatafillOne(),"REG_DATAFILL_ONE").commit();




    }

}
