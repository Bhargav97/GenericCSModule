package com.couchsurf.bhargav.couchsurfing;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.retry.RetryUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExploreMatchDisplay extends Fragment {
    View v;
    View divider, dividerView;
    TextView heading;
    final private static String COUCHCOUNTER_KEY = "No_Of_Couch";
    final private static String BOOKING_COUNTER_KEY = "No_Of_Bookings";

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

    static TextView noCouchTV;

    static SharedPreferences sharedpreferences;
    static ArrayList<String> nameData, vacData, urlData, globalCidData, uidContainingCouch;
    static RecyclerView recyclerView;
    static RVEDAdapter rvAdapter;
    static RecyclerViewClickListener listener;
    static List<RVExploreDisplay> dataList;
    public static int selectedCouch;
    static FirebaseAuth mAuth;
    static FirebaseFirestore db;
    static FirebaseUser firebaseUser;
    static public String UID;
    private static final String KEY = "AKIAJAK4TMOGHLIM4IUQ";
    private static final String SECRET = "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";
    private static final String BUCKET_NAME = "couchsurfing-userfiles-mobilehub-151528593";
    static DocumentReference databaseReference;
    public static int numberOfMatchedCouch;
    public static ArrayList<Map<String, Object>> mapOfMatchedCouch;
    public static ProgressBar markerProg;

    public static void getData(final Activity act, final String city) {
        numberOfMatchedCouch =0;
        dataList = new ArrayList<>();
        nameData = new ArrayList<>();
        vacData = new ArrayList<>();
        urlData = new ArrayList<>();
        globalCidData = new ArrayList<>();
        mapOfMatchedCouch = new ArrayList<>();
        uidContainingCouch = new ArrayList<>();
        db.collection("users").whereGreaterThan(COUCHCOUNTER_KEY, 0).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                uidContainingCouch.add(document.getId());
                                Log.d("TAG", document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
        //Fill data in arraylists for RV adapter, after above fetch is complete, hopefully in 4 secs
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < uidContainingCouch.size(); i++) {
                    db.collection("users").document(uidContainingCouch.get(i)).collection("couches").whereEqualTo(COUCH_CITY_KEY, city).get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            mapOfMatchedCouch.add(document.getData());
                                            numberOfMatchedCouch++;
                                            nameData.add(document.get(COUCH_NAME_KEY).toString());
                                            urlData.add(UtilityClass.returnUrlForUid(document.get(COUCH_OWNER_UID_KEY).toString()));
                                            vacData.add(Integer.toString(Integer.parseInt(document.get(NO_OF_ROOMS_KEY).toString()) * Integer.parseInt(document.get(NO_OF_ADULTS_KEY).toString())));
                                            globalCidData.add(document.get(COUCH_GLOBAL_ID_KEY).toString());
                                            Log.d("TAG", document.getId() + " => " + document.getData());
                                        }
                                    } else {
                                        Log.d("TAG", "Error getting documents: ", task.getException());
                                    }
                                }
                            });

                }
            }
        }, 2000);


        Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < numberOfMatchedCouch; i++) {
                    RVExploreDisplay rvExploreDisplay = new RVExploreDisplay();
                    rvExploreDisplay.global_cid = globalCidData.get(i);
                    rvExploreDisplay.name = nameData.get(i);
                    rvExploreDisplay.url = urlData.get(i);
                    rvExploreDisplay.vacancyFor = Integer.parseInt(vacData.get(i));
                    dataList.add(rvExploreDisplay);
                }
            }
        }, 4000);
        //return dataList;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = getLayoutInflater().inflate(R.layout.explore_match_display, container, false);

        noCouchTV = v.findViewById(R.id.noCouchTextViewExplore);
        markerProg = v.findViewById(R.id.marker_progressExplore);
        heading = v.findViewById(R.id.headingEMD);
        divider = v.findViewById(R.id.headingDividerEMD);
        dividerView = v.findViewById(R.id.dividerView);
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        if (sharedpreferences.getString("UID", "").trim().equals(""))
            Toast.makeText(getActivity(), "Error retrieving UID", Toast.LENGTH_LONG).show();
        else
            UID = sharedpreferences.getString("UID", "");
        //Toast.makeText(getActivity(), UID, Toast.LENGTH_LONG).show();
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        getData(getActivity(), getterSetterForExploreDisplay.getCity());

        heading.setText(heading.getText().toString()+getterSetterForExploreDisplay.getCity()+" returned these");
        recyclerView = (RecyclerView) v.findViewById(R.id.ed_rv);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (numberOfMatchedCouch == 0) {
                    noCouchTV.setText("No Couch are available at this location currently");
                    markerProg.setVisibility(View.GONE);
                } else {
                    heading.setVisibility(View.VISIBLE);
                    divider.setVisibility(View.VISIBLE);
                    dividerView.setVisibility(View.VISIBLE);
                    LinearLayoutManager llm = new LinearLayoutManager(recyclerView.getContext());
                    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                            llm.getOrientation());
                    recyclerView.addItemDecoration(dividerItemDecoration);
                    recyclerView.setLayoutManager(llm);
                    listener = new RecyclerViewClickListener() {
                        @Override
                        public void onClick(View view, int position) {
                            String uidItem = (String) rvAdapter.data.get(position).url;
                            String gcidItem = (String) rvAdapter.data.get(position).global_cid;
                            //Toast.makeText(getActivity(),selectedNameItem,Toast.LENGTH_SHORT).show();
                           // Toast.makeText(getActivity(),uidItem,Toast.LENGTH_LONG).show();
                            getterSetterForExploreDisplay.setUid(uidItem);
                            getterSetterForExploreDisplay.setGcid(gcidItem);
                            getterSetterForExploreDisplay.setMap(mapOfMatchedCouch);
                           getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.expFragContainerInfo, new ExploreCouchDisplay(), "EXPLORE_COUCH_DISPLAY").addToBackStack(null).commit();
                        }
                    };
                    tryToPopulateRV(getActivity());
                }

            }
        }, 5000);


        // llm.setReverseLayout(true);
        //llm.setStackFromEnd(true);


        //recyclerView.setNestedScrollingEnabled(false);
        //Understand this code


        return v;
    }

    public static void tryToPopulateRV(Activity activity) {
        noCouchTV.setVisibility(View.GONE);
        markerProg.setVisibility(View.GONE);
        rvAdapter = new RVEDAdapter(activity, dataList, listener);
        recyclerView.setAdapter(rvAdapter);

    }

}



