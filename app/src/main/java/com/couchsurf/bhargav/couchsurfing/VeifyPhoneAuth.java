package com.couchsurf.bhargav.couchsurfing;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stfalcon.smsverifycatcher.OnSmsCatchListener;
import com.stfalcon.smsverifycatcher.SmsVerifyCatcher;

import java.util.concurrent.TimeUnit;

public class VeifyPhoneAuth extends AppCompatActivity {
    String email;
    SharedPreferences sharedpreferences;

    private String mVerificationId;

    //The edittext to input the code
    private EditText editTextCode;

    //firebase auth object
    private FirebaseAuth mAuth;

    //sms verify catcher to detect the sms automatically
    private SmsVerifyCatcher smsVerifyCatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verify_phone_auth);

        //initializing objects
        mAuth = FirebaseAuth.getInstance();
        editTextCode = findViewById(R.id.editTextCode);


        //getting mobile number from the previous activity
        //and sending the verification code to the number
        Intent intent = getIntent();
        String mobile = intent.getStringExtra("mobile");
        email = intent.getStringExtra("email");
        sendVerificationCode(mobile);

        //this will detect the message received
        smsVerifyCatcher = new SmsVerifyCatcher(this, new OnSmsCatchListener<String>() {
            @Override
            public void onSmsCatch(String message) {

                //extracting the code from the sms
                String code = message.substring(0, 6);

                //setting the code in edittext
                editTextCode.setText(code);

                //verifying the code
                verifyVerificationCode(code);
            }
        });

        //if the automatic sms detection did not work, user can also enter the code manually
        //so adding a click listener to the button

        findViewById(R.id.buttonSignIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = editTextCode.getText().toString().trim();
                if (code.isEmpty() || code.length() < 6) {
                    editTextCode.setError("Enter valid code");
                    editTextCode.requestFocus();
                    return;
                }

                //verifying the code entered manually
                verifyVerificationCode(code);
            }
        });

    }

    //to detect SMS we also need to ask the permission on runtime
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        smsVerifyCatcher.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //the method is sending verification code
    //the country id is concatenated
    //you can take the country id as user input as well
    private void sendVerificationCode(String mobile) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + mobile,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }


    //the callback to detect the verification status
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(VeifyPhoneAuth.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            //storing the verification id that is sent to the user
            mVerificationId = s;
        }
    };


    @Override
    protected void onStart() {
        super.onStart();
        smsVerifyCatcher.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        smsVerifyCatcher.onStop();
    }

    private void verifyVerificationCode(String code) {
        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

        //signing the user
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(VeifyPhoneAuth.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //verification successful we will start the profile activity
                            //Intent intent = new Intent(VeifyPhoneAuth.this, ProfileActivity.class);
                            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            //startActivity(intent);
                            //Toast.makeText(getBaseContext(),"this was success",Toast.LENGTH_LONG).show();
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            updateUI(firebaseUser);

                        } else {

                            //verification unsuccessful.. display an error message

                            String message = "Somthing is wrong, we will fix it soon...";

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }

                            Snackbar snackbar = Snackbar.make(findViewById(R.id.parent), message, Snackbar.LENGTH_LONG);
                            snackbar.setAction("Dismiss", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });
                            snackbar.show();
                        }
                    }
                });
    }

    public void updateUI(final FirebaseUser user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(user.getUid());
        docRef.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot.exists()) {
                            Log.e("TAG", "Exits");
                            sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            final SharedPreferences.Editor editor = sharedpreferences.edit();

                            editor.putString("UID", user.getUid());
                            editor.commit();
                            editor.putString("UNAME", documentSnapshot.get("Name").toString());
                            editor.commit();
                            int type = Integer.parseInt(documentSnapshot.get("User_Type").toString());
                            if (type == 1) {
                                editor.putInt("USER_TYPE", 1);
                                editor.commit();
                            } else if (type == 0) {
                                editor.putInt("USER_TYPE", 0);
                                editor.commit();
                            } else {
                                editor.putInt("USER_TYPE", 2);
                                editor.commit();
                            }
                            int DP_COUNT = Math.round((Long) documentSnapshot.get("DP_CHANGE_COUNT"));
                            editor.putInt("DP_CHANGE_COUNTER", DP_COUNT);
                            editor.commit();
                            editor.putBoolean("SIGNED_IN", true);
                            editor.commit();
                            editor.putString("UEMAIL", email);
                            editor.commit();
                            //Toast.makeText(getBaseContext(),"your email is "+email,Toast.LENGTH_LONG).show();
                            Intent i = new Intent(VeifyPhoneAuth.this, MainActivity.class);
                            //i.putExtra("new", "no");
                            startActivity(i);
                            /*Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    finish();
                                }
                            },3000);*/


                        } else {
                            Log.e("TAG", "Does Not Exits");
                            sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            final SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString("UEMAIL", email);
                            editor.commit();
                            //Toast.makeText(getBaseContext(),"your email is "+email,Toast.LENGTH_LONG).show();
                            Intent i = new Intent(VeifyPhoneAuth.this, ExtraInfoForm.class);
                            //i.putExtra("new", "yes");
                            startActivity(i);
                            // finish();

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(VeifyPhoneAuth.this, "SOMETHING WENT WRONG", Toast.LENGTH_LONG).show();

            }
        });
        // Toast.makeText(getBaseContext(),newUser,Toast.LENGTH_LONG).show();
       /* if(newUser.trim().equals("no")){
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("SIGNED_IN", true);
            editor.commit();
            startActivity(new Intent(VeifyPhoneAuth.this, MainActivity.class));
            finish();
        }
        else{
            startActivity(new Intent(VeifyPhoneAuth.this, ExtraInfoForm.class));
            finish();
        }*/
    }


}
