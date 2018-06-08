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

public class HomeFragment extends Fragment implements View.OnClickListener {
    static public int USER_TYPE;
    static public String UID;
    static public String UNAME;
    CardView regNewCouch, manageCouches, adminPanel;
    TextView welcome;

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
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                welcome.setText(welcome.getText() + " " + UNAME.substring(0, UNAME.indexOf(' ')) + "!");
                welcome.setAlpha(0.0f);
                welcome.setVisibility(View.VISIBLE);
                welcome.animate().alpha(1.0f);
            }
        }, 500);
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
        final SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
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

        thread.start();


        regNewCouch = v.findViewById(R.id.registerCouch);
        manageCouches = v.findViewById(R.id.manageCouch);
        adminPanel = v.findViewById(R.id.adminPanel);
        regNewCouch.setOnClickListener(this);
        manageCouches.setOnClickListener(this);
        adminPanel.setOnClickListener(this);
        //SHOW APPROPRIATE CARDS FOR THE TYPE OF USER
        switch (USER_TYPE) {
            case 0:
                showViewWithAnim(100, adminPanel);
                break;
            case 1:
                showViewWithAnim(100, adminPanel);
                showViewWithAnim(100, manageCouches);
                showViewWithAnim(800, regNewCouch);
                break;
            case 2:
                showViewWithAnim(100, adminPanel);
                showViewWithAnim(600, manageCouches);
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

        }
    }
}
