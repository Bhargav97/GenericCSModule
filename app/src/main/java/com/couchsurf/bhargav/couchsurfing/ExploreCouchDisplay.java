package com.couchsurf.bhargav.couchsurfing;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ExploreCouchDisplay extends Fragment implements View.OnClickListener {
    View v;
    ProgressBar progressBar;
    ImageView img1, img2, img3, img4, img5, img6;
    private static final String KEY = "AKIAJAK4TMOGHLIM4IUQ";
    private static final String SECRET = "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";
    private static final String BUCKET_NAME = "couchsurfing-userfiles-mobilehub-151528593";
    final private String PHONE_KEY = "Phone";
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


    final private String COUCH_IMAGES_COUNTER_KEY = "No_Of_Images";
    final private String COUCH_NAME_KEY = "Name";
    final private String NO_OF_ROOMS_KEY = "No_Of_Rooms";
    final private String NO_OF_ADULTS_KEY = "No_Of_Adults";
    final private String COUCH_DESC_KEY = "Desc_Of_Couch";
    final private String COUCH_CITY_KEY = "City_Of_Couch";
    final private String COUCH_STATE_KEY = "State_Of_Couch";
    final private String COUCH_COUNTRY_KEY = "Country_Of_Couch";
    final private String TIME_ADDED_KEY = "Time_Of_Posting";
    final private String COUCH_PET_KEY = "Pets_Allowed";
    final private String COUCH_ADD_KEY = "Address_Of_Couch";
    final private String COUCH_ID_KEY = "Couch_Id";
    final private String COUCH_OWNER_UID_KEY = "Owner_Of_Couch_Is";
    final private String COUCH_GLOBAL_ID_KEY = "Global_Couch_Id";
    static FirebaseAuth mAuth;
    static FirebaseFirestore db;
    static FirebaseUser firebaseUser;
    static public String UID;
    static SharedPreferences sharedpreferences;
    final static private String INITURL = "https://s3.amazonaws.com/couchsurfing-userfiles-mobilehub-151528593/";
    final static private String COUCHFOLDER = "s3Folder/couchPics/"; //format- "s3Folder/couchPics/UID/CouchId(>0)/ImgId"
    final static private String EXT = ".jpg";
    ImageView current;
    ImageButton decrooms, incrooms, decadults, incadults;
    TextView roomNumber, adultNumber, loc, accTotal, petsPref, nameCouch, descCouch, hostName;
    String urls[];
    CircleImageView hostPic;
    Button submitButton, deleteButton;
    ScrollView mainLayout;
    public int selected;
    String imageFilePath;
    TextView cityAndState, timePosted;
    String url1, url2, url3, url4, url5, url6;

    public static ArrayList<Map> currentMap;
    public static String currentUid;
    public static String currentHostUrl;
    public static String currentHostName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = getLayoutInflater().inflate(R.layout.explore_couch_display, container, false);
        progressBar = v.findViewById(R.id.marker_progressExplore);
        img1 = v.findViewById(R.id.img1Explore);
        img2 = v.findViewById(R.id.img2Explore);
        img3 = v.findViewById(R.id.img3Explore);
        img4 = v.findViewById(R.id.img4Explore);
        img5 = v.findViewById(R.id.img5Explore);
        img6 = v.findViewById(R.id.img6Explore);
        adultNumber = v.findViewById(R.id.adultNumberExplore);
        roomNumber = v.findViewById(R.id.roomNumberExplore);
        nameCouch = v.findViewById(R.id.nameOfCouch);
        descCouch = v.findViewById(R.id.descOfCouch);
        loc = v.findViewById(R.id.locOfCouch);
        accTotal = v.findViewById(R.id.accomoExplore);
        petsPref = v.findViewById(R.id.petsPrefExplore);
        submitButton = v.findViewById(R.id.requestButton);
        mainLayout = v.findViewById(R.id.mainLayoutECD);
        timePosted = v.findViewById(R.id.timePostedExplore);
        hostName = v.findViewById(R.id.nameOfHost);
        hostPic = v.findViewById(R.id.couchOwnerPicECD);

        currentMap = getterSetterForExploreDisplay.getMapForMatchedCouch();
        currentHostUrl = getterSetterForExploreDisplay.getUid();
        currentUid = getUidFromUrl(currentHostUrl);

        //PERMISSION CHECK
        PermissionUtil permissionUtil = new PermissionUtil();
        ActivityCompat.requestPermissions(getActivity(),
                permissionUtil.getCameraPermissions(),
                1);
        ActivityCompat.requestPermissions(getActivity(),
                permissionUtil.getGalleryPermissions(),
                2);

        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        if (sharedpreferences.getString("UID", "").trim().equals(""))
            Toast.makeText(getActivity(), "Error retrieving UID", Toast.LENGTH_LONG).show();
        else
            UID = sharedpreferences.getString("UID", "");
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        db.collection("users").document(currentUid).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        currentHostName = documentSnapshot.get(NAME_KEY).toString();
                        Toast.makeText(getActivity(),currentHostName,Toast.LENGTH_LONG).show();
                    }
                });

        Glide.with(getContext()).load(currentHostUrl).into(hostPic);

        //Lets figure out the map for current
        Toast.makeText(getActivity(), "size is"+currentMap.size(), Toast.LENGTH_LONG).show();

        Map<String, Object> thisMap=null;
        for (int i = 0; i < currentMap.size(); i++) {
            if (currentMap.get(i).get(COUCH_OWNER_UID_KEY).toString().trim().equals(currentUid.trim())) {
                thisMap = currentMap.get(i);
                break;
            }
           // nameCouch.setText(nameCouch.getText().toString()+currentMap.get(i).get(COUCH_OWNER_UID_KEY).toString()+"\n");
        }
        //Lets figure the Couch Id to obtain images from and also no of images
        String couchId = thisMap.get(COUCH_ID_KEY).toString().trim();
        int numOfImages = Integer.parseInt(thisMap.get(COUCH_IMAGES_COUNTER_KEY).toString().trim());
        urls = buildImgUrlsFromCouchId(couchId, numOfImages);
        //set images
        for (int i = 0; i < numOfImages; i++) {
            ImageView currentIV = null;
            switch (i) {
                case 0:
                    currentIV = img1;
                    break;
                case 1:
                    currentIV = img2;
                    break;
                case 2:
                    currentIV = img3;
                    break;
                case 3:
                    currentIV = img4;
                    break;
                case 4:
                    currentIV = img5;
                    break;
                case 5:
                    currentIV = img6;
                    break;
            }
            Glide.with(getActivity().getBaseContext()).load(urls[i]).apply(new RequestOptions().signature(new ObjectKey(System.currentTimeMillis()))).into(currentIV);
        }
        nameCouch.setText(thisMap.get(COUCH_NAME_KEY).toString());
        descCouch.setText(thisMap.get(COUCH_DESC_KEY).toString());
        loc.setText("- " + thisMap.get(COUCH_CITY_KEY).toString() + ", " + thisMap.get(COUCH_STATE_KEY).toString() + ", " + thisMap.get(COUCH_COUNTRY_KEY).toString());
        hostName.setText(currentHostName);
        timePosted.setText(thisMap.get(TIME_ADDED_KEY).toString());
        roomNumber.setText(thisMap.get(NO_OF_ROOMS_KEY).toString());
        adultNumber.setText(thisMap.get(NO_OF_ADULTS_KEY).toString());
        accTotal.setText(Integer.toString(Integer.parseInt(thisMap.get(NO_OF_ROOMS_KEY).toString()) * Integer.parseInt(thisMap.get(NO_OF_ADULTS_KEY).toString())));
        petsPref.setText(thisMap.get(COUCH_PET_KEY).toString());
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                hostName.setText(currentHostName);
            }
        },1000);
        img1.setOnClickListener(this);
        img2.setOnClickListener(this);
        img3.setOnClickListener(this);
        img4.setOnClickListener(this);
        img5.setOnClickListener(this);
        img6.setOnClickListener(this);
        return v;
    }

    public static String getUidFromUrl(String url) {
        int startIndex = url.lastIndexOf('/') + 1;
        int endIndex = url.lastIndexOf('.');
        return url.substring(startIndex, endIndex);
    }

    public static String[] buildImgUrlsFromCouchId(String cid, int counter) {
        // https://s3.amazonaws.com/couchsurfing-userfiles-mobilehub-151528593/s3Folder/couchPics/e7LZKHtPNoafcttt4DRvpb9gzs33/2/1.jpg
        String finalurls[] = new String[counter];
        for (int i = 0; i < counter; i++) {
            String current = "https://s3.amazonaws.com/couchsurfing-userfiles-mobilehub-151528593/s3Folder/couchPics/" + currentUid + "/" + cid + "/" + i + ".jpg";
            finalurls[i] = current;
        }
        return finalurls;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.img1Explore:
                Intent intent = new Intent(getActivity().getBaseContext(), FullScreenImageView.class);
                intent.putExtra("CURRENT_IMG", urls[0]);
                startActivity(intent);
                break;
            case R.id.img2Explore:
                intent = new Intent(getActivity().getBaseContext(), FullScreenImageView.class);
                intent.putExtra("CURRENT_IMG", urls[1]);
                startActivity(intent);
                break;
            case R.id.img3Explore:
                intent = new Intent(getActivity().getBaseContext(), FullScreenImageView.class);
                intent.putExtra("CURRENT_IMG", urls[2]);
                startActivity(intent);
                break;
            case R.id.img4Explore:
                intent = new Intent(getActivity().getBaseContext(), FullScreenImageView.class);
                intent.putExtra("CURRENT_IMG", urls[3]);
                startActivity(intent);
                break;
            case R.id.img5Explore:
                intent = new Intent(getActivity().getBaseContext(), FullScreenImageView.class);
                intent.putExtra("CURRENT_IMG", urls[4]);
                startActivity(intent);
                break;
            case R.id.img6Explore:
                intent = new Intent(getActivity().getBaseContext(), FullScreenImageView.class);
                intent.putExtra("CURRENT_IMG", urls[5]);
                startActivity(intent);
                break;

        }
    }
}
