package com.couchsurf.bhargav.couchsurfing;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
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


    final public String REQUEST_GLOBAL_COUNTER_KEY = "Global_Request_Counter";
    final public String REQUEST_GUEST_ID_KEY = "Requested_By";
    final public String REQUEST_GUEST_NAME_KEY = "Requested_By_Name";
    final public String REQUEST_GLOBAL_ID_KEY = "Global_Request_Id";
    final public String REQUEST_ACC_KEY = "Accommodation_Requested_For";
    final public String REQUEST_STARTDATE_KEY = "Arrival_Of_Guests_On";
    final public String REQUEST_ENDDATE_KEY = "Guests_Leave_On";
    final public String REQUEST_HOSTSEEN_KEY = "Host_Seen_Req";

    final public String GUEST_NAME_KEY = "Name_Of_Guest";
    final public String GUEST_GENDER_KEY = "Gender_Of_Guest";
    final public String GUEST_AGE_KEY = "Age_Of_Guest";
    final private String PENDING_REQ_KEY = "PENDING_REQUEST";


    static FirebaseAuth mAuth;
    static FirebaseFirestore db;
    static FirebaseUser firebaseUser;
    static public String UID, UNAME;
    static SharedPreferences sharedpreferences;
    final static private String INITURL = "https://s3.amazonaws.com/couchsurfing-userfiles-mobilehub-151528593/";
    final static private String COUCHFOLDER = "s3Folder/couchPics/"; //format- "s3Folder/couchPics/UID/CouchId(>0)/ImgId"
    final static private String EXT = ".jpg";
    ImageView current;
    ImageButton decrooms, incrooms, decadults, incadults;
    TextView roomNumber, adultNumber, loc, accTotal, petsPref, nameCouch, descCouch, hostName, fancyText, adultNumAcc;
    String urls[];int letsUpdateReqCounter;
    CircleImageView hostPic;
    Button submitButton, deleteButton;
    ScrollView mainLayout;
    public int selected;
    String imageFilePath;
    TextView cityAndState, timePosted, noImgText, accText;
    String url1, url2, url3, url4, url5, url6;
    GridLayout imgGrid;
    public static ArrayList<Map> currentMap;
    public static String currentUid;
    public static String currentHostUrl;
    public static String currentHostName;
    public static String currentGcid;
    CardView dateCard, accCard, dataFillCard;
    Button reqButton, submitAcc;
    Calendar calendarFrom, calendarTo;
    static EditText fromDate;
    static EditText toDate;
    DatePickerDialog.OnDateSetListener dateFromListener, dateToListener;
    int current_global_req_counter;
    Map<String, Object> thisMap;
    ImageButton decAdults, incAdults;
    static LinearLayout dynamicLL;
    static ArrayList<EditText> nameArray;
    static ArrayList<EditText> ageArray;
    static ArrayList<Spinner> genderArray;
    public static String[] genders = {"Male", "Female", "Non-binary"};
    static int noOfGuests, maxAcc;
    static boolean guestDetailsClicked = false;

    public static void generateAndPutLL(int i, Context context, LinearLayout ll) {
        nameArray = new ArrayList<>();
        ageArray = new ArrayList<>();
        genderArray = new ArrayList<>();
        for (int j = 0; j < i; j++) {

            TextView tv = new TextView(context);
            LinearLayout.LayoutParams paramstv = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            paramstv.setMargins(30, 10, 30, 10);
            tv.setLayoutParams(paramstv);
            tv.setText("Details of Guest #" + (j + 1));
            tv.setTextColor(context.getResources().getColor(R.color.black_overlay));


            LinearLayout parent = new LinearLayout(context);
            parent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            parent.setOrientation(LinearLayout.VERTICAL);
            ll.addView(tv);
            ll.addView(parent);
            //children of parent linearlayout

            EditText nameText = new EditText(context);
            nameText.setHint("Name");
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(30, 10, 30, 10);
            nameText.setLayoutParams(params);
            nameArray.add(nameText);

            Spinner spinner = new Spinner(context);
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, genders);
            spinner.setAdapter(spinnerArrayAdapter);

            LinearLayout.LayoutParams spinnerParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
            spinnerParams.setMargins(30, 10, 30, 30);
            spinner.setLayoutParams(spinnerParams);
            genderArray.add(spinner);


            EditText ageText = new EditText(context);
            ageText.setHint("Age");
            ageText.setInputType(InputType.TYPE_CLASS_NUMBER);
            LinearLayout.LayoutParams paramsage = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            paramsage.setMargins(30, 10, 30, 10);
            ageText.setLayoutParams(paramsage);
            ageArray.add(ageText);


            parent.addView(nameText);
            parent.addView(ageText);
            parent.addView(spinner);

        }

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = getLayoutInflater().inflate(R.layout.explore_couch_display, container, false);
        progressBar = v.findViewById(R.id.marker_progressECD);
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
        imgGrid = v.findViewById(R.id.imgGrid);
        noImgText = v.findViewById(R.id.noImageText);
        reqButton = v.findViewById(R.id.requestButton);
        dateCard = v.findViewById(R.id.dateCard);
        fromDate = v.findViewById(R.id.fromDate);
        toDate = v.findViewById(R.id.toDate);
        fromDate.setInputType(InputType.TYPE_NULL);
        toDate.setInputType(InputType.TYPE_NULL);
        accCard = v.findViewById(R.id.accCard);
        decAdults = v.findViewById(R.id.decAdultsExplore);
        incAdults = v.findViewById(R.id.incAdultsExplore);
        submitAcc = v.findViewById(R.id.submitAcc);
        dataFillCard = v.findViewById(R.id.dataFillCard);
        dynamicLL = v.findViewById(R.id.dynamicUserInput);
        accText = v.findViewById(R.id.adultNumberAcc);
        fancyText = v.findViewById(R.id.fancyText);
        currentMap = getterSetterForExploreDisplay.getMapForMatchedCouch();
        currentHostUrl = getterSetterForExploreDisplay.getUid();
        currentUid = getUidFromUrl(currentHostUrl);
        currentGcid = getterSetterForExploreDisplay.getGcid();
        Glide.with(getContext()).load(currentHostUrl).apply(new RequestOptions().placeholder(R.drawable.ic_person).signature(new ObjectKey(System.currentTimeMillis()))).into(hostPic);
        //Toast.makeText(getActivity(), "Error retrieving UID", Toast.LENGTH_LONG).show();

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
                        //Toast.makeText(getActivity(),currentHostName,Toast.LENGTH_LONG).show();
                    }
                });

        db.collection("users").document(UID).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        UNAME = documentSnapshot.get(NAME_KEY).toString();
                        //Toast.makeText(getActivity(),currentHostName,Toast.LENGTH_LONG).show();
                    }
                });
        reqButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dateCard.getVisibility() == View.GONE) {
                    fancyText.setVisibility(View.VISIBLE);
                    reqButton.setText("Inititiate Request");
                    dateCard.setVisibility(View.VISIBLE);
                    accCard.setVisibility(View.VISIBLE);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mainLayout.fullScroll(View.FOCUS_DOWN);
                        }
                    }, 200);
                    calendarFrom = Calendar.getInstance();
                    calendarTo = Calendar.getInstance();

                    dateFromListener = new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear,
                                              int dayOfMonth) {
                            calendarFrom.set(Calendar.YEAR, year);
                            calendarFrom.set(Calendar.MONTH, monthOfYear);
                            calendarFrom.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            updateLabel(true);
                        }

                    };
                    dateToListener = new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear,
                                              int dayOfMonth) {
                            calendarTo.set(Calendar.YEAR, year);
                            calendarTo.set(Calendar.MONTH, monthOfYear);
                            calendarTo.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            updateLabel(false);
                        }

                    };
                    fromDate.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            new DatePickerDialog(getActivity(), dateFromListener, calendarFrom
                                    .get(Calendar.YEAR), calendarFrom.get(Calendar.MONTH),
                                    calendarFrom.get(Calendar.DAY_OF_MONTH)).show();
                        }
                    });
                    toDate.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            new DatePickerDialog(getActivity(), dateToListener, calendarTo
                                    .get(Calendar.YEAR), calendarTo.get(Calendar.MONTH),
                                    calendarTo.get(Calendar.DAY_OF_MONTH)).show();
                        }
                    });

                    submitAcc.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            guestDetailsClicked = true;
                            dataFillCard.setVisibility(View.VISIBLE);
                            accCard.setVisibility(View.GONE);
                            noOfGuests = Integer.parseInt(accText.getText().toString());
                            generateAndPutLL(noOfGuests, getContext(), dynamicLL);
                        }
                    });
                }else if(toDate == null || toDate.getText().toString().trim().equals("")||fromDate.getText()==null||fromDate.getText().toString().trim().equals("")){
                    fromDate.setError("Add the dates");
                    fromDate.requestFocus();
                    toDate.setError("Add the dates");
                    Handler errorRemove = new Handler();
                    errorRemove.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            fromDate.setError(null);
                            toDate.setError(null);
                        }
                    },3000);
                }
                else if(dateMistmatch()){
                    toDate.setError("This date can't be before Arrival date");
                    toDate.requestFocus();
                    Handler errorRemove = new Handler();
                    errorRemove.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            fromDate.setError(null);
                            toDate.setError(null);
                        }
                    },3000);
                }
                else if(!guestDetailsClicked){
                    submitAcc.requestFocus();
                    Snackbar snackbar = Snackbar.make(mainLayout, "Your need to enter some details of all of you", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
                else{

                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);
                    /*todo
                    if(db.collection("bookings").where(couch_id_key, current_couch_id).get()
                        .addOn....(new On...
                            public void onSuccess...(){
                                //todo Check if the selected date above lies between current bookings, if any
                            }
                        }
                     */
                    //check that all data has been filled and appropriately
                    if (fromDate == null || fromDate.getText().toString().trim().equals("")) {
                        fromDate.setError("Pick a date!");
                        fromDate.requestFocus();
                        Handler errorRemove = new Handler();
                        errorRemove.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                fromDate.setError(null);
                                toDate.setError(null);
                            }
                        },3000);
                    } else if (toDate == null || toDate.getText().toString().trim().equals("")) {
                        toDate.setError("Pick a date!");
                        toDate.requestFocus();
                        Handler errorRemove = new Handler();
                        errorRemove.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                fromDate.setError(null);
                                toDate.setError(null);
                            }
                        },3000);
                    }else if(dateMistmatch()){
                        toDate.requestFocus();
                        toDate.setError("This date can't be before Arrival date");
                        Handler errorRemove = new Handler();
                        errorRemove.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                fromDate.setError(null);
                                toDate.setError(null);
                            }
                        },3000);
                    }
                    else {

                        for (int i =0; i<noOfGuests; i++){
                            if(nameArray.get(i).getText()==null||nameArray.get(i).getText().toString().trim().equals("")){
                                nameArray.get(i).setError("You can't skip this!");
                            }
                            else if(ageArray.get(i).getText()==null||ageArray.get(i).getText().toString().trim().equals("")){
                                ageArray.get(i).setError("You can't skip this!");
                            }
                        }

                    }

                    //retrieve the latest global req counter
                    current_global_req_counter = 0;
                    db.collection("metadata").document("request").get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    DocumentSnapshot documentSnapshot = task.getResult();
                                    current_global_req_counter = Integer.parseInt(documentSnapshot.get(REQUEST_GLOBAL_COUNTER_KEY).toString());
                                }
                            });



                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //retrieve all the guests data and upload it
                            for(int i = 0; i<noOfGuests; i++){

                                HashMap<String,Object> hashMap = new HashMap<>();
                                hashMap.put(GUEST_NAME_KEY,nameArray.get(i).getText().toString());
                                hashMap.put(GUEST_GENDER_KEY,genderArray.get(i).getSelectedItem().toString());
                                hashMap.put(GUEST_AGE_KEY,ageArray.get(i).getText().toString());
                                db.collection("requests").document(Integer.toString(current_global_req_counter+1)).collection("guests").document(Integer.toString(i)).set(hashMap);


                                Snackbar.make(mainLayout, "GRC is"+current_global_req_counter,Snackbar.LENGTH_LONG).show();

                                //Set data for the request itself
                                hashMap = new HashMap<>();
                                hashMap = (HashMap) thisMap;
                                hashMap.put(REQUEST_GLOBAL_ID_KEY, (current_global_req_counter+1));
                                hashMap.put(REQUEST_GUEST_ID_KEY, UID);
                                hashMap.put(REQUEST_ACC_KEY,noOfGuests);
                                hashMap.put(REQUEST_STARTDATE_KEY,fromDate.getText().toString());
                                hashMap.put(REQUEST_ENDDATE_KEY,toDate.getText().toString());
                                hashMap.put(REQUEST_HOSTSEEN_KEY,false);
                                hashMap.put(REQUEST_GUEST_NAME_KEY,UNAME);
                                db.collection("requests").document(Integer.toString(current_global_req_counter + 1)).set(hashMap);
                                db.collection("metadata").document("request").update(REQUEST_GLOBAL_COUNTER_KEY, (current_global_req_counter+1));


                                db.collection("users").document(currentUid).get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                DocumentSnapshot documentSnapshot = task.getResult();
                                                letsUpdateReqCounter = Integer.parseInt(documentSnapshot.get(PENDING_REQ_KEY).toString());
                                                db.collection("users").document(currentUid).update(PENDING_REQ_KEY,(letsUpdateReqCounter+1));
                                            }
                                        });


                            }
                        }
                    },3000);

                    progressBar.setVisibility(View.VISIBLE);
                    Handler handler2 = new Handler();
                    handler2.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.fragment_container, new HomeFragment(),"HOME_FRAGMENT").addToBackStack(null).commit();
                        }
                    },6000);
                }
            }
        });
        //Lets figure out the map for current

        thisMap = null;
        for (int i = 0; i < currentMap.size(); i++) {
            if (currentMap.get(i).get(COUCH_GLOBAL_ID_KEY).toString().trim().equals(currentGcid.trim())) {
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

        if (numOfImages == 0) {
            noImgText.setVisibility(View.VISIBLE);
            imgGrid.setVisibility(View.GONE);
        }
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
            // Toast.makeText(getActivity(), "we got"+urls[i], Toast.LENGTH_SHORT).show();
            Glide.with(getActivity().getBaseContext()).load(urls[i]).apply(new RequestOptions().signature(new ObjectKey(System.currentTimeMillis()))).into(currentIV);
        }
        for (int i = 0; i < 6 - numOfImages; i++) {

            switch (i) {
                case 0:
                    img6.setVisibility(View.GONE);
                    break;
                case 1:
                    img5.setVisibility(View.GONE);
                    break;
                case 2:
                    img4.setVisibility(View.GONE);
                    break;
                case 3:
                    img3.setVisibility(View.GONE);
                    break;
                case 4:
                    img2.setVisibility(View.GONE);
                    break;
            }
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
        maxAcc = Integer.parseInt(thisMap.get(NO_OF_ROOMS_KEY).toString()) * Integer.parseInt(thisMap.get(NO_OF_ADULTS_KEY).toString());
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                hostName.setText(currentHostName);
            }
        }, 1000);
        img1.setOnClickListener(this);
        img2.setOnClickListener(this);
        img3.setOnClickListener(this);
        img4.setOnClickListener(this);
        img5.setOnClickListener(this);
        img6.setOnClickListener(this);
        decAdults.setOnClickListener(this);
        incAdults.setOnClickListener(this);
        return v;
    }
    public static boolean dateMistmatch() {
        Date date1=null,date2=null;
        try {
             date1 = new SimpleDateFormat("dd/MM/yy").parse(fromDate.getText().toString());
             date2 = new SimpleDateFormat("dd/MM/yy").parse(toDate.getText().toString());
        }
        catch (Exception e){
            Log.e("TAG","exception in date conversion caught",new Throwable());
        }
        if(date2.before(date1))
            return true;
        else
            return false;
    }
    public static String getUidFromUrl(String url) {
        int startIndex = url.lastIndexOf('/') + 1;
        int endIndex = url.lastIndexOf('.');
        return url.substring(startIndex, endIndex);
    }

    private void updateLabel(boolean b) {
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        if (b) {
            fromDate.setText(sdf.format(calendarFrom.getTime()));
        } else {
            toDate.setText(sdf.format(calendarTo.getTime()));

        }

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
            case R.id.decAdultsExplore:
                noOfGuests = Integer.parseInt(accText.getText().toString());
                if (noOfGuests == 1) {
                } else {
                    noOfGuests--;
                    accText.setText(Integer.toString(noOfGuests));
                }
                break;
            case R.id.incAdultsExplore:
                noOfGuests = Integer.parseInt(accText.getText().toString());
                if(noOfGuests==maxAcc) {
                }
                else{
                    noOfGuests++;
                    accText.setText(Integer.toString(noOfGuests));
                }
                break;
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
