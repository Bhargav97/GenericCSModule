package com.couchsurf.bhargav.couchsurfing;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Collections;
import java.util.List;

public class ManageCouches extends Fragment {
    final private static String COUCHCOUNTER_KEY = "No_Of_Couch";
    final private static String COUCH_IMAGES_COUNTER_KEY = "No_Of_Images";
    final private static String COUCH_NAME_KEY = "Name";
    final private static String NO_OF_ROOMS_KEY = "No_Of_Rooms";
    final private static String NO_OF_ADULTS_KEY = "No_Of_Adults";
    final private static String PETS_KEY = "Are_Pets_Allowed";
    final private static String COUCH_DESC_KEY = "Desc_Of_Couch";
    final private static String COUCH_CITY_KEY = "City_Of_Couch";
    final private static String COUCH_STATE_KEY = "State_Of_Couch";
    final private static String COUCH_COUNTRY_KEY = "Country_Of_Couch";
    final private static String TIME_ADDED_KEY = "Time_Of_Posting";
    ImageView img1, img2, img3, img4, img5, img6;
    static SharedPreferences sharedpreferences;
    static FirebaseAuth mAuth;
    static FirebaseFirestore db;
    static FirebaseUser firebaseUser;
    static public String UID;
    private static final String BUCKET_NAME = "couchsurfing-userfiles-mobilehub-151528593";
    static DocumentReference databaseReference;
    public static int couchcounter;
    public static TextView noCouchTV;
    public static boolean filledList = false;
    static ArrayList<String> nameData, locData, couchId;
    static RecyclerView recyclerView;
    static RVMCAdapter rvAdapter;
    static RecyclerViewClickListener listener;
    static List<RVManageCouch> dataList;
    static List<RVManageCouch> alignedList;
    public static int selectedCouch;
    public static ProgressBar markerProg;
    public static Button delAll;
    public static NestedScrollView mainLayout;
    public List<RVManageCouch> getData(final Activity act) {
        dataList = new ArrayList<>();
        /*nameData = new ArrayList<>();
        locData = new ArrayList<>();
        couchId = new ArrayList<>();*/
       /* for (int i = 1; i <= couchcounter; i++) {
            final int j = i;  //works?
            db.collection("users").document(UID).collection("couches").document(Integer.toString(i))
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot doc = task.getResult();
                        nameData[j - 1] = doc.get(COUCH_NAME_KEY).toString();
                        locData[j - 1] = doc.get(COUCH_CITY_KEY).toString() + ", " + doc.get(COUCH_STATE_KEY).toString() + "\n" + doc.get(COUCH_COUNTRY_KEY).toString();
                        RVManageCouch current = new RVManageCouch();
                        current.name = nameData[j - 1];
                        current.loc = locData[j - 1];
                        current.id = Integer.toString(j);
                        dataList.add(current);
                    }
                }
            });

        }*/
        Toast.makeText(getActivity(),"Uid is"+UID,Toast.LENGTH_LONG).show();
        db.collection("users").document(UID).collection("couches").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        for(QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            if(documentSnapshot.getId().trim().equals("0")) {
                            }
                            else {
                                RVManageCouch current = new RVManageCouch();
                                current.name = documentSnapshot.get(COUCH_NAME_KEY).toString();
                                current.loc = documentSnapshot.get(COUCH_CITY_KEY).toString() + ", " + documentSnapshot.get(COUCH_STATE_KEY).toString() + "\n" + documentSnapshot.get(COUCH_COUNTRY_KEY).toString();
                                current.id = documentSnapshot.getId();
                                dataList.add(current);
                            }
                        }
                        if((getActivity())!=null)
                            tryToPopulateRV(getActivity());
                    }
                });


        return dataList;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = getLayoutInflater().inflate(R.layout.manage_couches, container, false);
        ((MainActivity) getActivity()).setActionBarTitle("Your couches");
        ((MainActivity) getActivity()).setNavItem(R.id.navmanage);
        noCouchTV = v.findViewById(R.id.noCouchTextView);
        markerProg = v.findViewById(R.id.marker_progress);
        delAll = v.findViewById(R.id.deleteAll);
        delAll.setVisibility(View.GONE);
        mainLayout = v.findViewById(R.id.mainLayoutMC);
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        if (sharedpreferences.getString("UID", "").trim().equals(""))
            Toast.makeText(getActivity(), "Error retrieving UID", Toast.LENGTH_LONG).show();
        else
            UID = sharedpreferences.getString("UID", "");
        //Toast.makeText(getActivity(), UID, Toast.LENGTH_LONG).show();
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        databaseReference = FirebaseFirestore.getInstance().collection("users").document(UID);
        databaseReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    couchcounter = Integer.parseInt(doc.get(COUCHCOUNTER_KEY).toString());
                    if (couchcounter > 0) {
                        markerProg.setVisibility(View.VISIBLE);
                        noCouchTV.setText("Please wait while loading...");

                        recyclerView = (RecyclerView) v.findViewById(R.id.mc_rv);
                        LinearLayoutManager llm = new LinearLayoutManager(recyclerView.getContext());
                       // llm.setReverseLayout(true);
                        //llm.setStackFromEnd(true);
                        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                                llm.getOrientation());
                        recyclerView.addItemDecoration(dividerItemDecoration);
                        recyclerView.setLayoutManager(llm);
                      //  recyclerView.setHasFixedSize(true);
                        recyclerView.setNestedScrollingEnabled(false);
                        //recyclerView.setNestedScrollingEnabled(false);
                        //Understand this code
                        listener = new RecyclerViewClickListener() {
                            @Override
                            public void onClick(View view, int position) {
                                String selectedNameItem = (String) rvAdapter.data.get(position).id;
                                //Toast.makeText(getActivity(),selectedNameItem,Toast.LENGTH_SHORT).show();
                                selectedCouch = Integer.parseInt(selectedNameItem);
                                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.fragment_container,new ManageCouchScreen(),"MANAGE_COUCH_SCREEN").addToBackStack(null).commit();

                            }
                        };
                        getData(getActivity());

                        /*handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                alignedList = new ArrayList<>();
                                int len = dataList.size();
                                int temp;
                                for(int i=0;i<len;i++){
                                    temp=0;
                                    while(!dataList.get(temp).id.trim().equals(Integer.toString(i+1))){
                                        temp++;
                                    }
                                    alignedList.add(dataList.get(temp));
                                    //Toast.makeText(getActivity(),"Im working"+i,Toast.LENGTH_LONG).show();
                                }
                            }
                        },2000);
                        Handler handler2 = new Handler();
                        handler2.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(getActivity()!=null)
                                    tryToPopulateRV(getActivity());
                            }
                        },4000);*/
                    }
                }
            }
        });

        delAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 1; i<=couchcounter; i++) {
                    db.collection("users").document(UID).collection("couches").document(Integer.toString(i)).delete();
                    db.collection("users").document(UID).update(COUCHCOUNTER_KEY,0);
                }
                Snackbar snackbar = Snackbar.make(mainLayout, "All your Couches have been deleted", Snackbar.LENGTH_LONG);
                snackbar.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(getActivity(), MainActivity.class));
                    }
                }, 3000);
            }
        });

        return v;
    }

    public static void tryToPopulateRV(Activity activity){
        noCouchTV.setVisibility(View.GONE);
        markerProg.setVisibility(View.GONE);
        rvAdapter = new RVMCAdapter(activity, dataList, listener);
        recyclerView.setAdapter(rvAdapter);
        delAll.setVisibility(View.VISIBLE);

    }

}
