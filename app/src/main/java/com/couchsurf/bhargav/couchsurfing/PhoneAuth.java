package com.couchsurf.bhargav.couchsurfing;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

public class PhoneAuth extends AppCompatActivity {

    private EditText editTextMobile;
    RelativeLayout container;
    ProgressBar progressBar;

    @Override
    protected void onStart() {
        super.onStart();
        if(UtilityClass.phoneVerifyDone){
            container.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }
        else {
            container.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_auth_one);

        editTextMobile = findViewById(R.id.editTextMobile);
        progressBar = findViewById(R.id.marker_progressPA);
        container = findViewById(R.id.containerPA);
        findViewById(R.id.buttonContinue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mobile = editTextMobile.getText().toString().trim();
                Intent intent = getIntent();
                String userEmail = intent.getStringExtra("email");
                if(mobile.isEmpty() || mobile.length() < 10){
                    editTextMobile.setError("Enter a valid mobile");
                    editTextMobile.requestFocus();
                    return;
                }


                Intent intent2 = new Intent(PhoneAuth.this, VeifyPhoneAuth.class);
                intent2.putExtra("mobile", mobile);
                intent2.putExtra("email",userEmail);
                startActivity(intent2);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
        super.onBackPressed();
    }
}
