package com.couchsurf.bhargav.couchsurfing;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    static private final int RC_SIGN_IN = 2;
    FirebaseAuth.AuthStateListener mAuthListener;
    Button loginButton, signupButton;
    EditText emailInput, passInput;
    FirebaseFirestore db;
    final private String NAME_KEY = "uid";
    final private String PASS_KEY = "password";

    @Override
    protected void onStart() {
        super.onStart();
        ExtraInfoForm.setColor(getWindow(), this, R.color.login);
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        //GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        //updateUI(account);
        mAuth.addAuthStateListener(mAuthListener);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean("SIGNED_IN", false);
        editor.apply();
        SignInButton signInButton = findViewById(R.id.googleSignIn);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        loginButton = findViewById(R.id.loginButton);
        signupButton = findViewById(R.id.signupButton);
        emailInput = findViewById(R.id.username);
        passInput = findViewById(R.id.password);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emailInput.getText().toString().trim().equalsIgnoreCase("")) {
                    emailInput.setError("This field can not be blank");
                } else if (passInput.getText().toString().trim().equalsIgnoreCase("")) {
                    passInput.setError("This field can not be blank");
                } else {
                    String email = emailInput.getText().toString();
                    String password = passInput.getText().toString();
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d("TAG", "signInWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        // updateUI(user);
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w("TAG", "signInWithEmail:failure", task.getException());
                                        Toast.makeText(LoginActivity.this, "Invalid Credentials. If you are a new user, please register first. ",
                                                Toast.LENGTH_SHORT).show();
                                        //updateUI(null);
                                    }

                                    // ...
                                }
                            });
                    Toast.makeText(LoginActivity.this, "Sign in clicked", Toast.LENGTH_LONG).show();
                }
            }
        });
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (firebaseAuth.getCurrentUser() != null) {
                    updateUI(user);
                    //startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }
            }
        };
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.oauthclientID))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        findViewById(R.id.googleSignIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.googleSignIn:
                        signIn();
                        break;
                }
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emailInput.getText().toString().trim().equalsIgnoreCase("")) {
                    emailInput.setError("This field can not be blank");
                } else if (passInput.getText().toString().trim().equalsIgnoreCase("")) {
                    passInput.setError("This field can not be blank");
                } else {
                    String email = emailInput.getText().toString();
                    String password = passInput.getText().toString();
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d("TAG", "createUserWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        // updateUI(user);
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w("TAG", "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                        // updateUI(null);
                                    }

                                    // ...
                                }
                            });
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("TAG", "Google sign in failed", e);
                // ...
            }
        }
    }


    private void updateUI(final FirebaseUser user) {


        //Toast.makeText(LoginActivity.this,"SOMETHING WENT WRONG",Toast.LENGTH_LONG).show();

        /*final String uid =user.getUid();
        DocumentReference docRef = db.collection("users").document(uid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if (documentSnapshot.exists()) {
                    Log.e("TAG", "Exits");
                    SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    final SharedPreferences.Editor editor = sharedpreferences.edit();

                    editor.putString("UID",uid);
                    editor.commit();

                    editor.putString("UNAME",documentSnapshot.get("Name").toString());
                    editor.commit();
                    //Check if he is a host or a guest
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("users").document(uid).get().addOnCompleteListener(new OnCompleteListener < DocumentSnapshot > () {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot doc = task.getResult();
                                int type = Integer.parseInt( doc.get("User_Type").toString());
                                //int pr = Integer.parseInt( doc.get("Pending_Requests").toString());
                                //Toast.makeText(LoginActivity.this,"Found- "+type,Toast.LENGTH_LONG).show();
                                if(type==1) {
                                    editor.putInt("USER_TYPE", 1);
                                    editor.commit();
                                }
                                else if(type==0) {
                                    editor.putInt("USER_TYPE", 0);
                                    editor.commit();
                                }
                                else {
                                    editor.putInt("USER_TYPE", 2);
                                    editor.commit();
                                }
                                int DP_COUNT = Math.round((Long)doc.get("DP_CHANGE_COUNT"));
                                editor.putInt("DP_CHANGE_COUNTER",DP_COUNT);
                                //editor.putInt("PENDING_REQUESTS",pr);
                                editor.commit();
                            }
                            //Put users who are whitelisted to access without PhoneAuth in here
                            if(user.getUid().trim().equals("")){

                            }else {
                                Intent i = new Intent(LoginActivity.this, PhoneAuth.class);
                                i.putExtra("new", "no");
                                // startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                startActivity(i);
                            }
                            finish();

                        }


                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(LoginActivity.this,"SOMETHING WENT WRONG",Toast.LENGTH_LONG).show();
                                }
                            });


                } else {
                    Log.e("TAG", "Does Not Exits");
                    Intent i = new Intent(LoginActivity.this, PhoneAuth.class);
                    i.putExtra("new", "yes");
                    startActivity(i);
                    finish();

                }
            }
        });*/


        if (user.getUid().trim().equals("TEtRI4BAGDQvXdsEcl4TooRi8es2") || user.getUid().trim().equals("UmluwDSH7HapDTK5ZsC1KSS2LLw1") || user.getUid().trim().equals("kDPSjUG6BifK9Jnz65QtjaEZvSU2") || user.getUid().trim().equals("lTahbHyf1sQuKLP3XF6bKngWzEd2")) {
            DocumentReference docRef;
            SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            final SharedPreferences.Editor editor = sharedpreferences.edit();

            docRef = db.collection("users").document(user.getUid());


            docRef.get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot documentSnapshot = task.getResult();
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
                        }
                    });
            editor.putString("UID", user.getUid());
            editor.commit();

            editor.putBoolean("SIGNED_IN", true);
            editor.commit();
            //Toast.makeText(getBaseContext(),"your email is "+email,Toast.LENGTH_LONG).show();
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            //i.putExtra("new", "no");
            startActivity(i);
            /*Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 3000);*/

        } else {

            String email = user.getEmail();
            Intent i = new Intent(LoginActivity.this, PhoneAuth.class);
            i.putExtra("email", email);
            startActivity(i);
           /* Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 3000);*/
        }


    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("TAG", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.loginPageLL), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            Toast.makeText(LoginActivity.this, "AUTH FAILED", Toast.LENGTH_LONG).show();
                            // updateUI(null);
                        }

                        // ...
                    }
                });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}
