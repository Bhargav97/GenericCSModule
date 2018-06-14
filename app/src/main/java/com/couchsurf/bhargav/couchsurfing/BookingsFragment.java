package com.couchsurf.bhargav.couchsurfing;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BookingsFragment extends Fragment {
    View v;
    View divider, dividerView;
    TextView heading;
    final private static String COUCHCOUNTER_KEY = "No_Of_Couch";
    final private static String BOOKING_COUNTER_KEY = "No_Of_Bookings";
    final private String COUCH_OWNER_UNAME_KEY = "Couch_Owner_Name";

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
    final public String REQUEST_HOSTAPPROVED_KEY = "Host_Approved";
    final public String REQUEST_HOSTREJECTED_KEY = "Host_Rejected";
    final public String GUEST_NAME_KEY = "Name_Of_Guest";
    final public String GUEST_GENDER_KEY = "Gender_Of_Guest";
    final public String GUEST_AGE_KEY = "Age_Of_Guest";


    static TextView noCouchTV;

    static SharedPreferences sharedpreferences;
    static ArrayList<String> hostSeenData, nameData, accData, urlData, globalRidData, couchNameData, fromDateData, toDateData, couchLocData, uidContainingReq;
    static RecyclerView recyclerView;
    static RVBDAdapter rvAdapter;
    static RecyclerViewClickListener listener;
    static List<RVBookingDisplay> dataList;
    public static int selectedReq;
    static FirebaseAuth mAuth;
    static FirebaseFirestore db;
    static FirebaseUser firebaseUser;
    static public String UID;
    private static final String BUCKET_NAME = "couchsurfing-userfiles-mobilehub-151528593";
    static DocumentReference databaseReference;
    public static ArrayList<Map<String, Object>> mapOfRequests;
    public static int noOfFoundReq;
    public static ProgressBar markerProg;
    LinearLayout mainLayout;
    public static int pending_req_count;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = getLayoutInflater().inflate(R.layout.request_status, container, false);
        mainLayout = v.findViewById(R.id.mainLayoutRS);
        ((MainActivity) getActivity()).setActionBarTitle("Confirmed Bookings");
        // ((MainActivity) getActivity()).setNavItem(R.id.navreq);
        noCouchTV = v.findViewById(R.id.noCouchTextViewRS);
        recyclerView = v.findViewById(R.id.rs_rv);
        markerProg = v.findViewById(R.id.marker_progressRS);
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        if (sharedpreferences.getString("UID", "").trim().equals(""))
            Toast.makeText(getActivity(), "Error retrieving UID", Toast.LENGTH_LONG).show();
        else
            UID = sharedpreferences.getString("UID", "");
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        getData();


        LinearLayoutManager llm = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(llm);
        listener = new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
               /* String gridItem = (String) rvAdapter.data.get(position).GRid;
                String urlItem = (String) rvAdapter.data.get(position).url;
                String nameItem = (String) rvAdapter.data.get(position).name;
                String accForItem = (String) rvAdapter.data.get(position).accFor;
                String fromItem = (String) rvAdapter.data.get(position).fromDate;
                String toItem = (String) rvAdapter.data.get(position).toDate;
                getterSetterForCouchRequest.setGrid(gridItem);
                getterSetterForCouchRequest.setAcc(accForItem);
                getterSetterForCouchRequest.setFromDate(fromItem);
                getterSetterForCouchRequest.setToDate(toItem);
                getterSetterForCouchRequest.setGuestName(nameItem);
                getterSetterForCouchRequest.setGuestUID(UtilityClass.getUidFromUrl(urlItem));
                getterSetterForCouchRequest.setMap(mapOfRequests);

                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.fragment_container, new CouchReqDisplay(), "COUCH_REQ_DISP").commit();*/
            }
        };

        return v;

    }

    public void getData() {
        noOfFoundReq = 0;
        dataList = new ArrayList<>();
        nameData = new ArrayList<>();
        accData = new ArrayList<>();
        urlData = new ArrayList<>();
        globalRidData = new ArrayList<>();
        couchNameData = new ArrayList<>();
        couchLocData = new ArrayList<>();
        fromDateData = new ArrayList<>();
        toDateData = new ArrayList<>();
        hostSeenData = new ArrayList<>();
        mapOfRequests = new ArrayList<>();
        db.collection("requests").whereEqualTo(REQUEST_GUEST_ID_KEY, UID).whereEqualTo(REQUEST_HOSTREJECTED_KEY,false).whereEqualTo(REQUEST_HOSTAPPROVED_KEY,true).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                mapOfRequests.add(document.getData());
                                nameData.add(document.get(COUCH_OWNER_UNAME_KEY).toString());
                                couchNameData.add(document.get(COUCH_NAME_KEY).toString());
                                couchLocData.add(document.get(COUCH_CITY_KEY).toString() + ", " + document.get(COUCH_STATE_KEY).toString() + ", " + document.get(COUCH_COUNTRY_KEY).toString());
                                urlData.add(UtilityClass.returnUrlForUid(document.get(REQUEST_GUEST_ID_KEY).toString()));
                                globalRidData.add(document.get(REQUEST_GLOBAL_ID_KEY).toString());
                                fromDateData.add(document.get(REQUEST_STARTDATE_KEY).toString());
                                toDateData.add(document.get(REQUEST_ENDDATE_KEY).toString());
                                accData.add(document.get(REQUEST_ACC_KEY).toString());
                                hostSeenData.add(document.get(REQUEST_HOSTSEEN_KEY).toString());
                                noOfFoundReq++;
                                Log.d("TAG", document.getId() + " => " + document.getData());
                            }
                            if (noOfFoundReq == 0) {
                                noCouchTV.setText("No Bookings yet, Explore and Book!");
                                markerProg.setVisibility(View.GONE);
                            } else {
                                for (int i = 0; i < noOfFoundReq; i++) {
                                    RVBookingDisplay rvCouchReq = new RVBookingDisplay();
                                    rvCouchReq.GRid = globalRidData.get(i);
                                    rvCouchReq.hostName = nameData.get(i);
                                    rvCouchReq.url = urlData.get(i);
                                    rvCouchReq.accFor = accData.get(i);
                                    //rvCouchReq.hostSeen = hostSeenData.get(i);
                                    rvCouchReq.fromDate = fromDateData.get(i);
                                    rvCouchReq.toDate = toDateData.get(i);
                                    rvCouchReq.couchName = couchNameData.get(i);
                                    rvCouchReq.couchLoc = couchLocData.get(i);
                                    dataList.add(rvCouchReq);
                                }
                                if (getActivity() != null)
                                    tryToPopulateRV();
                            }
                        } else {

                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    public void tryToPopulateRV() {
        noCouchTV.setVisibility(View.GONE);
        markerProg.setVisibility(View.GONE);
        rvAdapter = new RVBDAdapter(getActivity(), dataList, listener);
        recyclerView.setAdapter(rvAdapter);

    }

}
