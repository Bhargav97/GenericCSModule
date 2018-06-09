package com.couchsurf.bhargav.couchsurfing;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class CouchReqDisplay extends Fragment {
    View v;
    View divider, dividerView;
    TextView heading;
    final private static String COUCHCOUNTER_KEY = "No_Of_Couch";
    final private static String BOOKING_COUNTER_KEY = "No_Of_Bookings";
    final public String REQUEST_HOSTAPPROVED_KEY = "Host_Approved";
    final public String REQUEST_HOSTREJECTED_KEY = "Host_Rejected";
    final private static String COUCH_ID_COUNTER_KEY = "Couch_Created_Till_Date"; //includes deleted
    final private static String COUCH_ID_KEY = "Couch_Id";
    final private static String COUCH_IMAGES_COUNTER_KEY = "No_Of_Images";
    final private static String COUCH_NAME_KEY = "Name";
    final private static String NO_OF_ROOMS_KEY = "No_Of_Rooms";
    final private static String NO_OF_ADULTS_KEY = "No_Of_Adults";
    final private static String COUCH_DESC_KEY = "Desc_Of_Couch";
    final private static String COUCH_CITY_KEY = "City_Of_Couch";
    final private static String COUCH_STATE_KEY = "State_Of_Couch";
    final private static String COUCH_COUNTRY_KEY = "Country_Of_Couch";
    final private static String COUCH_PET_KEY = "Pets_Allowed";
    final private static String TIME_ADDED_KEY = "Time_Of_Posting";
    final private static String COUCH_ADD_KEY = "Address_Of_Couch";
    final private static String COUCH_OWNER_UID_KEY = "Owner_Of_Couch_Is";
    final private static String COUCH_GLOBAL_ID_KEY = "Global_Couch_Id";
    final private String PENDING_REQ_KEY = "PENDING_REQUEST";

    final public String REQUEST_GLOBAL_COUNTER_KEY = "Global_Request_Counter";
    final public String REQUEST_GUEST_ID_KEY = "Requested_By";
    final public String REQUEST_GLOBAL_ID_KEY = "Global_Request_Id";
    final public String REQUEST_ACC_KEY = "Accommodation_Requested_For";
    final public String REQUEST_STARTDATE_KEY = "Arrival_Of_Guests_On";
    final public String REQUEST_ENDDATE_KEY = "Guests_Leave_On";
    final public String REQUEST_HOSTSEEN_KEY = "Host_Seen_Req";
    final public String REQUEST_GUEST_NAME_KEY = "Requested_By_Name";

    final public String GUEST_NAME_KEY = "Name_Of_Guest";
    final public String GUEST_GENDER_KEY = "Gender_Of_Guest";
    final public String GUEST_AGE_KEY = "Age_Of_Guest";

    static FirebaseAuth mAuth;
    static FirebaseFirestore db;
    static FirebaseUser firebaseUser;
    static public String UID, UNAME;
    static SharedPreferences sharedpreferences;
    static ArrayList<TextView> nameArray;
    static ArrayList<TextView> ageArray;
    static ArrayList<TextView> genderArray;
    static ArrayList<String> nameArrayStr;
    static ArrayList<String> ageArrayStr;
    static ArrayList<String> genderArrayStr;
    int noOfGuests;
    CircleImageView guestPic;
    TextView guestName, fromDate, toDate, accData;
    String guestImgUrl;
    ArrayList reqMap;
    LinearLayout dynamicLL;
    ProgressBar progressBar;
    Button approveButton, rejectButton, cancelButton;
    boolean postDisplay = false;  //this flag being true means the req is approved and this fragment is being viewed in status section

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = getLayoutInflater().inflate(R.layout.couch_req_display, container, false);
        guestName = v.findViewById(R.id.nameOfGuestCRD);
        progressBar = v.findViewById(R.id.marker_progressCRD);
        fromDate = v.findViewById(R.id.fromDateCRD);
        toDate = v.findViewById(R.id.toDateCRD);
        accData = v.findViewById(R.id.accDataCRD);
        guestPic = v.findViewById(R.id.guestPicCRD);
        reqMap = getterSetterForCouchRequest.getMapForMatchedCouch();
        guestImgUrl = UtilityClass.returnUrlForUid(getterSetterForCouchRequest.getGuestUID());
        guestName.setText(getterSetterForCouchRequest.getGuestName());
        Glide.with(getContext()).load(guestImgUrl).apply(new RequestOptions().placeholder(R.drawable.def_user_icon).signature(new ObjectKey(System.currentTimeMillis()))).into(guestPic);
        accData.setText(getterSetterForCouchRequest.getAcc());
        noOfGuests = Integer.parseInt(getterSetterForCouchRequest.getAcc());
        fromDate.setText(getterSetterForCouchRequest.getFromDate());
        toDate.setText(getterSetterForCouchRequest.getToDate());
        dynamicLL = v.findViewById(R.id.dynamicGuestView);
        approveButton = v.findViewById(R.id.approveButton);
        rejectButton = v.findViewById(R.id.denyButton);
        cancelButton = v.findViewById(R.id.cancelButton);
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        if (sharedpreferences.getString("UID", "").trim().equals(""))
            Toast.makeText(getActivity(), "Error retrieving UID", Toast.LENGTH_LONG).show();
        else
            UID = sharedpreferences.getString("UID", "");
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        android.support.v7.widget.Toolbar toolbar = getActivity().findViewById(R.id.toolbar);

        if (toolbar.getTitle().toString().trim().equals("Arrivals' Status")) {
            cancelButton.setVisibility(View.VISIBLE);
            rejectButton.setVisibility(View.GONE);
            approveButton.setVisibility(View.GONE);
        }else if(toolbar.getTitle().toString().trim().equals("Pending Requests' Status")){
            cancelButton.setVisibility(View.GONE);
            rejectButton.setVisibility(View.GONE);
            approveButton.setVisibility(View.GONE);
        }


        db.collection("requests").document(getterSetterForCouchRequest.getGrid()).update(REQUEST_HOSTSEEN_KEY,true);
        nameArrayStr = new ArrayList<>();
        genderArrayStr = new ArrayList<>();
        ageArrayStr = new ArrayList<>();
        db.collection("requests").document(getterSetterForCouchRequest.getGrid()).collection("guests").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            nameArrayStr.add(document.get(GUEST_NAME_KEY).toString());
                            genderArrayStr.add(document.get(GUEST_GENDER_KEY).toString());
                            ageArrayStr.add(document.get(GUEST_AGE_KEY).toString());
                        }
                        progressBar.setVisibility(View.GONE);
                        if (getActivity() != null)
                            generateAndPutLL(noOfGuests, getContext(), dynamicLL);
                    }
                });

        approveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                approveGuest(true);
            }
        });
        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                approveGuest(false);
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Are you sure?")
                        .setMessage("Do you really want to cancel this deal?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                db.collection("requests").document(getterSetterForCouchRequest.getGrid()).delete();
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.fragment_container, new HomeFragment(), "HOME_FRAGMENT").addToBackStack(null).commit();
                                    }
                                },2000);

                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();

            }
        });
        return v;
    }

    public void generateAndPutLL(int i, Context context, LinearLayout ll) {

        for (int j = 0; j < i; j++) {

            TextView tv = new TextView(context);
            LinearLayout.LayoutParams paramstv = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            tv.setLayoutParams(paramstv);
            tv.setPadding(40, 10, 40, 10);
            tv.setText("Details of Guest #" + (j + 1));
            tv.setTextColor(context.getResources().getColor(R.color.white));
            tv.setTextSize(20f);
            tv.setBackground(context.getResources().getDrawable(R.color.colorPrimary));


            LinearLayout parent = new LinearLayout(context);
            parent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            parent.setOrientation(LinearLayout.VERTICAL);
            ll.addView(tv);
            ll.addView(parent);
            //children of parent linearlayout

            View div = new View(context);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    2
            );
            div.setLayoutParams(layoutParams);
            div.setBackground(getResources().getDrawable(R.color.black_overlay));


            TextView nameText = new TextView(context);
            nameText.setText("Name: " + nameArrayStr.get(j));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(30, 30, 30, 10);
            nameText.setLayoutParams(params);
            nameText.setPadding(10, 10, 10, 10);
            nameText.setTextSize(17f);

            TextView gender = new TextView(context);
            gender.setText("Gender: " + genderArrayStr.get(j));
            LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params2.setMargins(30, 10, 30, 10);
            gender.setLayoutParams(params2);
            gender.setPadding(10, 10, 10, 10);
            gender.setTextSize(17f);

            TextView ageText = new TextView(context);
            ageText.setText("Age: " + ageArrayStr.get(j));

            LinearLayout.LayoutParams paramsage = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            paramsage.setMargins(30, 10, 30, 40);
            ageText.setLayoutParams(paramsage);
            ageText.setPadding(10, 10, 10, 10);
            ageText.setTextSize(17f);
            parent.addView(div);
            parent.addView(nameText);
            parent.addView(gender);
            parent.addView(ageText);


        }

    }

    public void approveGuest(boolean status) {
        //true is to approve and false to reject
        if (status) {
            db.collection("requests").document(getterSetterForCouchRequest.getGrid()).update(REQUEST_HOSTAPPROVED_KEY, true);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.fragment_container, new HomeFragment(), "HOME_FRAGMENT").addToBackStack(null).commit();
                }
            },2000);
        } else {
            db.collection("requests").document(getterSetterForCouchRequest.getGrid()).update(REQUEST_HOSTREJECTED_KEY, true);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.fragment_container, new HomeFragment(), "HOME_FRAGMENT").addToBackStack(null).commit();
                }
            },2000);
        }

    }
}
