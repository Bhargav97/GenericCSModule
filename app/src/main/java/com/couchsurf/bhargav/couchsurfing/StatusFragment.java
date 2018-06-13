package com.couchsurf.bhargav.couchsurfing;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hanks.htextview.scale.ScaleTextView;

import java.util.ArrayList;

public class StatusFragment extends Fragment implements View.OnClickListener{
    View v;
    static FirebaseAuth mAuth;
    static FirebaseFirestore db;
    static FirebaseUser firebaseUser;
    static public String UID, UNAME;
    static SharedPreferences sharedpreferences;
    static int UTYPE;
    TextView arrivalHeading, bookingHeading, requestHeading;
    View arrBottom, bookBottom, reqBottom;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = getLayoutInflater().inflate(R.layout.status_layout, container, false);
        arrivalHeading = v.findViewById(R.id.arrivalsHeading);
        bookingHeading = v.findViewById(R.id.bookingsHeading);
        requestHeading = v.findViewById(R.id.requestsHeading);
        arrBottom = v.findViewById(R.id.arrBottom);
        bookBottom = v.findViewById(R.id.bookBottom);
        reqBottom = v.findViewById(R.id.reqBottom);
        ((MainActivity) getActivity()).setActionBarTitle("Status");
        ((MainActivity) getActivity()).setNavItem(R.id.navstatus);
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        if (sharedpreferences.getString("UID", "").trim().equals(""))
            Toast.makeText(getActivity(), "Error retrieving UID", Toast.LENGTH_LONG).show();
        else
            UID = sharedpreferences.getString("UID", "");
        if (sharedpreferences.getString("UID", "").trim().equals(""))
            Toast.makeText(getActivity(), "Error retrieving User Type", Toast.LENGTH_LONG).show();
        else
            UTYPE = sharedpreferences.getInt("USER_TYPE", 0);
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        if(UTYPE==1||UTYPE==2){
            arrivalHeading.setVisibility(View.VISIBLE);
            arrBottom.setVisibility(View.VISIBLE);
            makeDull(bookingHeading,bookBottom);
            makeDull(requestHeading,reqBottom);
            makeBright(arrivalHeading,arrBottom);
            getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.fragment_container_status,new ArrivalsFragment(),"ARRIVAL_STATUS_FRAGMENT").commit();

        }
        else {
            arrBottom.setVisibility(View.GONE);
            arrBottom.setLayoutParams(new LinearLayout.LayoutParams(0,0));
            makeDull(arrivalHeading,arrBottom);
            makeDull(requestHeading,reqBottom);
            makeBright(bookingHeading,bookBottom);
            getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.fragment_container_status,new BookingsFragment(),"BOOKING_STATUS_FRAGMENT").commit();

        }

        arrivalHeading.setOnClickListener(this);
        bookingHeading.setOnClickListener(this);
        requestHeading.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.arrivalsHeading:
                makeDull(bookingHeading,bookBottom);
                makeDull(requestHeading,reqBottom);
                makeBright(arrivalHeading,arrBottom);
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.fragment_container_status,new ArrivalsFragment(),"ARRIVAL_STATUS_FRAGMENT").addToBackStack(null).commit();
                break;
            case R.id.bookingsHeading:
                makeDull(arrivalHeading,arrBottom);
                makeDull(requestHeading,reqBottom);
                makeBright(bookingHeading,bookBottom);
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.fragment_container_status,new BookingsFragment(),"BOOKING_STATUS_FRAGMENT").addToBackStack(null).commit();
                break;
            case R.id.requestsHeading:
                makeDull(bookingHeading,bookBottom);
                makeDull(arrivalHeading,arrBottom);
                makeBright(requestHeading,reqBottom);
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.fragment_container_status,new RequestsFragment(),"REQUEST_STATUS_FRAGMENT").addToBackStack(null).commit();
                break;

        }
    }

    public void makeDull(TextView tv, View view){
        view.setVisibility(View.INVISIBLE);
        tv.setTextColor(getResources().getColor(R.color.grey));
        tv.setTypeface(Typeface.DEFAULT);
    }

    public void makeBright(TextView tv, View view){
        view.setVisibility(View.VISIBLE);
        tv.setTextColor(getResources().getColor(R.color.white));
        tv.setTypeface(Typeface.DEFAULT_BOLD);
    }


}
