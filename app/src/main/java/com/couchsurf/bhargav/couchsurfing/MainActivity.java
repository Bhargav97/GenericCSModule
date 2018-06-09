package com.couchsurf.bhargav.couchsurfing;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.amazonaws.mobile.auth.core.IdentityHandler;
import com.amazonaws.mobile.auth.core.IdentityManager;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.AWSStartupHandler;
import com.amazonaws.mobile.client.AWSStartupResult;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.bumptech.glide.Glide;
import com.bumptech.glide.annotation.GlideExtension;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import java.io.File;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    static FirebaseAuth mAuth;
    static FirebaseFirestore db;
    static int DP_CHANGE;
    android.support.v7.widget.Toolbar toolbar;
    static FirebaseUser firebaseUser;
    DrawerLayout drawer;
    static NavigationView navigationView;
    Button b;
    static RequestOptions options;
    ActionBarDrawerToggle toggle;
    static public int USER_TYPE;
    static public String UID;
    TextView userName, userId;
    static private ActionBar actionBar;
    static ImageView userImage;
    static boolean doneFlag = false;
    static SharedPreferences sharedpreferences;
    public static void setTitleToHome(){
        actionBar.setTitle("Home");
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //AWS Connection init
        AWSMobileClient.getInstance().initialize(this, new AWSStartupHandler() {
            @Override
            public void onComplete(AWSStartupResult awsStartupResult) {

                //Make a network call to retrieve the identity ID
                // using IdentityManager. onIdentityId happens UPon success.
                IdentityManager.getDefaultIdentityManager().getUserID(new IdentityHandler() {

                    @Override
                    public void onIdentityId(String s) {
                        //Toast.makeText(MainActivity.this,"Happy Face",Toast.LENGTH_LONG).show();

                        //The network call to fetch AWS credentials succeeded, the cached
                        // user ID is available from IdentityManager throughout your app
                        Log.d("MainActivity", "Identity ID is: " + s);
                        Log.d("MainActivity", "Cached Identity ID: " + IdentityManager.getDefaultIdentityManager().getCachedUserID());
                    }

                    @Override
                    public void handleError(Exception e) {
                        Toast.makeText(MainActivity.this,"ERROROROR",Toast.LENGTH_LONG).show();
                        Log.e("MainActivity", "Error in retrieving Identity ID: " + e.getMessage());
                    }
                });
            }
        }).execute();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        actionBar = getSupportActionBar();
        setTitleToHome();
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        USER_TYPE=sharedpreferences.getInt("USER_TYPE",0);
        if(sharedpreferences.getString("UID","").trim().equals(""))
            Toast.makeText(MainActivity.this, "Error retrieving UID",Toast.LENGTH_LONG).show();
        else
            UID = sharedpreferences.getString("UID","");
        options = new RequestOptions().signature(new ObjectKey(System.currentTimeMillis()));

        //Initializing firebase objects
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        //Go to your specific database directory or Child
        DocumentReference databaseReference = FirebaseFirestore.getInstance().collection("users").document(firebaseUser.getUid());
        //Connect the views of navigation bar
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        userName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.navUserName);
        //userId   = (TextView)navigationView.getHeaderView(0).findViewById(R.id.textViewHeaderSID);
        userImage = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.navUserImage);

        //Initializing nav drawer info and Welcome text
        databaseReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(USER_TYPE==1)
                    userName.setText(documentSnapshot.get("Name").toString() + " (Host)");
                else if(USER_TYPE==0)
                    userName.setText(documentSnapshot.get("Name").toString() + " (Guest)");
                else
                    userName.setText(documentSnapshot.get("Name").toString() + " (Admin)");

                boolean customDP = (Boolean) documentSnapshot.get("CUSTOM_DP");
                final SharedPreferences.Editor editor = sharedpreferences.edit();

                updateNavProfilePic(getBaseContext());

            }
        });
        //show appropriate menu items
        switch (USER_TYPE){
            case 0:
                hideItem(R.id.navmanage);
                hideItem(R.id.navreq);
                break;
            case 1:
                hideItem(R.id.navadmin);
                break;
            case 2:
                //
                break;
        }

        //Use you DB reference object and add this method to access realtime data
       /* databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //Fetch values from you database child and set it to the specific view object.
                userId.setText(dataSnapshot.child("name").getValue().toString());
                //.setText(dataSnapshot.child("sid").getValue().toString());

                String link =dataSnapshot.child("profile_picture").getValue().toString();
                Picasso.with(getBaseContext()).load(link).into(mImageView);
            }

            //SIMPLE BRO. HAVE FUN IN ANDROID <3 GOOD LUCK

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/
        // Uri imgUri=Uri.parse("android.resource://my.package.name/"+R.drawable.image);
        //imageView.setImageURI(null);
        //imageView.setImageURI(imgUri);

        //Set listeners to clicks on nav menu items
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //Hamburger icon and toggle for drawer
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        if(savedInstanceState==null) {
            //Do when the app begins
            getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.fragment_container, new HomeFragment(),"HOME_FRAGMENT").addToBackStack(null).commit();
            //startActivity(new Intent(getApplicationContext(),HomeActivity.class));
            navigationView.setCheckedItem(R.id.navhome);
        }

    }
    public static void setNavItem(int id){
        navigationView.setCheckedItem(id);
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item){
        int id = item.getItemId();
        switch(id){
            case R.id.navhome:
                getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.fragment_container,new HomeFragment(),"HOME_FRAGMENT").addToBackStack(null).commit();
                //getSupportActionBar().show();
                setTitleToHome();
                drawer.closeDrawers();
               // showSearch();
                break;
            case R.id.navmanage:
                getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.fragment_container,new ManageCouches(),"MC").addToBackStack(null).commit();
                break;
            case R.id.navexplore:
                getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.fragment_container,new ExploreFragment(),"EF").addToBackStack(null).commit();
                break;
            case R.id.navadmin:
                startActivity(new Intent(
                        MainActivity.this, AdminPanel.class
                ));
                break;
            case R.id.navreq:
                getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.fragment_container,new CouchRequests(),"REQ_FRAGMENT").addToBackStack(null).commit();
                break;
            case R.id.navfeedback:
                Toast.makeText(this, "What's the hurry dude!!",
                        Toast.LENGTH_LONG).show();
                break;

            case R.id.navprofile:
                getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.fragment_container,new ProfileFragment(),"PROFILE_FRAGMENT").addToBackStack(null).commit();
                //startActivity(new Intent(MainActivity.this,ProfileScreen.class));
                break;
            case R.id.navstatus:
                getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.fragment_container,new StatusFragment(),"STATUS_FRAGMENT").addToBackStack(null).commit();

                break;
            case R.id.navsettings:
                Toast.makeText(this, "Really dude?",
                        Toast.LENGTH_LONG).show();
                break;
            case R.id.navsignout:
                FirebaseAuth.getInstance().signOut();
                SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.clear();
                editor.putBoolean("SIGNED_IN", false);
                editor.commit();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        toggle.syncState();
    }

    @Override
    public void onBackPressed() {
        FragmentManager mFragmentManager = ((FragmentActivity) this).getSupportFragmentManager();

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if(mFragmentManager.getBackStackEntryCount() == 1){
            //showSearch();
           // getSupportActionBar().show();
            //setNavItem(R.id.navhome);
           // setTitleToHome();
            //MainActivity.toggleTutIcon(this,false);
            finish();
        }
        else if(mFragmentManager.findFragmentByTag("BOOKING_STATUS_FRAGMENT")!=null||mFragmentManager.findFragmentByTag("ARRIVAL_STATUS_FRAGMENT")!=null||mFragmentManager.findFragmentByTag("REQUEST_STATUS_FRAGMENT")!=null){
            if(mFragmentManager.findFragmentByTag("BOOKING_STATUS_FRAGMENT").isVisible()||mFragmentManager.findFragmentByTag("ARRIVAL_STATUS_FRAGMENT").isVisible()||mFragmentManager.findFragmentByTag("REQUEST_STATUS_FRAGMENT").isVisible()) {
                /*for(int i = 0; i < mFragmentManager.getBackStackEntryCount(); ++i) {
                    mFragmentManager.popBackStack();
                }*/
                //performClick(
               // getSupport)FragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.fragment_container, new HomeFragment(), "HOME_FRAGMENT").addToBackStack(null).commit();
                finish();
                startActivity(getIntent());
                //oast.makeText(getBaseContext(),"I was here",Toast.LENGTH_SHORT).show();
            }

        }

               // getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.fragment_container,new HomeFragment(),"HOME_FRAGMENT").addToBackStack(null).commit();



        else if(mFragmentManager.findFragmentByTag("HOME_FRAGMENT")!=null){
            if(mFragmentManager.findFragmentByTag("HOME_FRAGMENT").isVisible())
                finish();
        }
        super.onBackPressed();
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
    public static void updateNavProfilePic( final Context context){
        DocumentReference databaseReference = FirebaseFirestore.getInstance().collection("users").document(firebaseUser.getUid());

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
                        String link =  ProfileFragment.url;
                        //Toast.makeText(getActivity(), link,Toast.LENGTH_LONG).show();
                        boolean customDP = (Boolean) documentSnapshot.get("CUSTOM_DP");
                        if (!glink.trim().equals("") && !customDP) {
                            //Glide.with(getBaseContext()).load("").thumbnail(0.5f).into(userImage);
                            Glide.with(context).load(glink).apply(new RequestOptions().placeholder(R.drawable.def_user_icon).signature(new ObjectKey(System.currentTimeMillis()))).thumbnail(0.5f).thumbnail(0.5f).into(userImage);
                        } else if(!link.trim().equals("")){
                            Glide.with(context).load(link).apply(new RequestOptions().placeholder(R.drawable.def_user_icon).signature(new ObjectKey(System.currentTimeMillis()))).thumbnail(0.5f).thumbnail(0.5f).into(userImage);
                            // options = new RequestOptions().signature(new ObjectKey((sharedpreferences.getInt("DP_CHANGE_COUNTER",0))+2));
                            // Glide.with(context).load(link).apply(options).thumbnail(0.5f).into(userImage);
                        }
                        else {
                            Glide.with(context).load(R.drawable.def_user_icon).thumbnail(0.5f).into(userImage);
                        }
                    }
                },1000);
            }
        }) ;


    }

    public static int getDpChangeCounter(){
         return sharedpreferences.getInt("DP_CHANGE_COUNTER",0);
    }

    private void hideItem(int id)
    {
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(id).setVisible(false);
    }

}
