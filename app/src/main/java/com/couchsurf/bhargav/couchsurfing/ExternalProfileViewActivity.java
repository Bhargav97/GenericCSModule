package com.couchsurf.bhargav.couchsurfing;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;

public class ExternalProfileViewActivity extends Activity {
    android.widget.Toolbar toolbar;static FirebaseAuth mAuth;
    static FirebaseFirestore db;
    static FirebaseUser firebaseUser;
    final private String PHONE_KEY = "Phone";
    final private String DESC_KEY = "DESC";
    final private String ADDRESS_KEY = "Address";
    final private String EMAIL_KEY = "Email";
    final private String NAME_KEY = "Name";
    final private String PHOTO_KEY = "Photo_Uri";
    final private String CITY_KEY = "City";
    final private String STATE_KEY = "State";
    final private String COUNTRY_KEY = "Country";
    final private String AGE_KEY = "Age";
    final private String USERTYPE_KEY = "User_Type";
    final private String COUCHCOUNTER_KEY = "No_Of_Couch";
    final private String BOOKING_COUNTER_KEY = "No_Of_Bookings";
    CircleImageView profilePic;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ext_profile_layout);
        String currentUid = getIntent().getStringExtra("UID");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        /*toolbar = findViewById(R.id.toolbarEP);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setTitle("Profile");
        setActionBar(toolbar);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(true);*/
        final TextView nameText, phoneText, emailText, cityText, descText;
        nameText = findViewById(R.id.nameTextEP);
        cityText = findViewById(R.id.cityTextEP);
        phoneText = findViewById(R.id.phoneTextEP);
        emailText = findViewById(R.id.emailTextEP);
        profilePic = findViewById(R.id.profilePic);
        View view = findViewById(R.id.emptyView);
        descText = findViewById(R.id.descTextEP);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExternalProfileViewActivity.super.onBackPressed();
            }
        });
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        final DocumentReference databaseReference = FirebaseFirestore.getInstance().collection("users").document(currentUid);
        databaseReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                final DocumentSnapshot documentSnapshot = task.getResult();
                final String glink = documentSnapshot.get("Photo_Uri").toString();
                String UID = firebaseUser.getUid();
                ProfileFragment.getUrlFromAws(UID);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String link = ProfileFragment.url;
                        //Toast.makeText(getActivity(),"got this"+link,Toast.LENGTH_LONG).show();
                        boolean customDP = (Boolean) documentSnapshot.get("CUSTOM_DP");
                        if (!glink.trim().equals("") && !customDP) {
                            //Glide.with(getBaseContext()).load("").thumbnail(0.5f).into(userImage);
                            Glide.with(getBaseContext()).load(glink).apply(new RequestOptions().placeholder(R.drawable.def_user_icon).signature(new ObjectKey(System.currentTimeMillis()))).into(profilePic);
                        } else if (!link.trim().equals("")) {
                            // Toast.makeText(getActivity(),"got to here",Toast.LENGTH_LONG).show();

                            Glide.with(getBaseContext()).load(link).apply(new RequestOptions().placeholder(R.drawable.def_user_icon).signature(new ObjectKey(System.currentTimeMillis()))).into(profilePic);
                            //options = new RequestOptions().signature(new ObjectKey((sharedpreferences.getInt("DP_CHANGE_COUNTER",0))+2));
                            // Glide.with(getActivity().getBaseContext()).load(link).apply(options).thumbnail(0.5f).into(profileImage);

                        } else {
                            Glide.with(getBaseContext()).load(R.drawable.def_user_icon).into(profilePic);
                        }
                    }
                },1000);

            }
        });

        db.collection("users").document(currentUid).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        descText.setText(" " + documentSnapshot.get(DESC_KEY).toString());
                        nameText.setText("  " + documentSnapshot.get(NAME_KEY).toString());
                        cityText.setText("  " + documentSnapshot.get(CITY_KEY).toString() + ", " + documentSnapshot.get(STATE_KEY).toString() + ", " + documentSnapshot.get(COUNTRY_KEY).toString());
                        emailText.setText("  "+ documentSnapshot.get(EMAIL_KEY).toString());
                        phoneText.setText("  "+ documentSnapshot.get(PHONE_KEY).toString());
                    }
                });

    }

}
