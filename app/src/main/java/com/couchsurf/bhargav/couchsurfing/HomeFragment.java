package com.couchsurf.bhargav.couchsurfing;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hanks.htextview.base.HTextView;
import com.hanks.htextview.scale.ScaleTextView;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements View.OnClickListener {
    static public int USER_TYPE;
    static public String UID;
    SharedPreferences sharedpreferences;
    static public String UNAME;
    CardView regNewCouch, manageCouches, adminPanel, statusCard, couchReq;   //adminPanel is explore
    ScaleTextView welcome;
    ArrayList<String> arrayList = new ArrayList<>();
    static int position = 0;
    static int delay=2000;
    boolean flagForAnim = true;
    private void showViewWithAnim(int delay, View v) {
        Handler handler = new Handler();
        final View w = v;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                w.setAlpha(0.0f);
                w.setVisibility(View.VISIBLE);
                w.animate().alpha(1.0f);
            }
        }, delay);
    }

    public void setWelcomeText() {
        position=0;
        flagForAnim = false;
        if(UNAME.trim().contains(" "))
            arrayList.add("Hey," + " " + UNAME.substring(0, UNAME.indexOf(' ')) + "!");
        else
            arrayList.add("Hey," + " " + UNAME + "!");
        arrayList.add("Remember the earth is vaaaaast");
        arrayList.add("Because there isn't enough of travelling");
        arrayList.add("Life is for travelling!");
        arrayList.add("Every person is a new experience");
        arrayList.add("People are Awesome!");
        arrayList.add("Give it a try!");
        welcome.animateText(arrayList.get(position));
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable(){
            public void run(){

                handler.postDelayed(this, delay);
                if(position>=arrayList.size())
                    position=0;
                welcome.animateText(arrayList.get(position));
                position++;
            }
        }, delay);
    }

    @Override
    public void onStart() {
        super.onStart();



        MainActivity.setTitleToHome();
        MainActivity.setNavItem(R.id.navhome);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = getLayoutInflater().inflate(R.layout.home_layout, container, false);
        //ExtraInfoForm.setColor(getActivity().getWindow(),getActivity(),R.color.colorPrimaryDark);
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        USER_TYPE = sharedpreferences.getInt("USER_TYPE", 0);
        if (sharedpreferences.getString("UID", "").trim().equals(""))
            Toast.makeText(getActivity(), "Error retrieving UID", Toast.LENGTH_LONG).show();
        else
            UID = sharedpreferences.getString("UID", "");
        welcome = v.findViewById(R.id.welcome_text);
        //showViewWithAnim(500, welcome);
        Thread thread = new Thread() {
            @Override
            public void run() {
                while (sharedpreferences.getString("UNAME", "").equals("")) { }
                UNAME = sharedpreferences.getString("UNAME", "");
                setWelcomeText();

            }
        };
        if(sharedpreferences.getString("UNAME", "").equals(""))
            thread.start();
        else {
            UNAME = sharedpreferences.getString("UNAME", "");
            if(flagForAnim)setWelcomeText();
        }

        regNewCouch = v.findViewById(R.id.registerCouch);
        manageCouches = v.findViewById(R.id.manageCouch);
        adminPanel = v.findViewById(R.id.adminPanel);
        statusCard = v.findViewById(R.id.status);
        couchReq = v.findViewById(R.id.couchReq);
        regNewCouch.setOnClickListener(this);
        manageCouches.setOnClickListener(this);
        adminPanel.setOnClickListener(this);
        statusCard.setOnClickListener(this);
        couchReq.setOnClickListener(this);
        //SHOW APPROPRIATE CARDS FOR THE TYPE OF USER
        switch (USER_TYPE) {
            case 0:
                showViewWithAnim(100, adminPanel);
                showViewWithAnim(400,statusCard);
                couchReq.setVisibility(View.GONE);
                manageCouches.setVisibility(View.GONE);
                regNewCouch.setVisibility(View.GONE);
                break;
            case 1:
                showViewWithAnim(100, adminPanel);
                showViewWithAnim(400,statusCard);
                showViewWithAnim(700,couchReq);
                showViewWithAnim(900, manageCouches);
                showViewWithAnim(1100, regNewCouch);
                break;
            case 2:
                showViewWithAnim(100, adminPanel);
                showViewWithAnim(400,statusCard);
                showViewWithAnim(700,couchReq);
                showViewWithAnim(900, manageCouches);
                showViewWithAnim(1100, regNewCouch);
                break;
        }
        return v;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.registerCouch:
                startActivity(new Intent(getActivity(), RegisterNewCouch.class));
                break;
            case R.id.manageCouch:
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.fragment_container,new ManageCouches(),"MC").addToBackStack(null).commit();
                break;
            case R.id.adminPanel:
                //
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.fragment_container,new ExploreFragment(),"EF").addToBackStack(null).commit();
                break;
            case R.id.status:
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.fragment_container,new StatusFragment(),"SF").addToBackStack(null).commit();
                break;

            case R.id.couchReq:
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.fragment_container,new CouchRequests(),"CR").addToBackStack(null).commit();
                break;

        }
    }
}
