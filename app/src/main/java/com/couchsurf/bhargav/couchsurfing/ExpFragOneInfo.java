package com.couchsurf.bhargav.couchsurfing;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ExpFragOneInfo extends Fragment implements View.OnClickListener {
    View v;
    final private String CITY_KEY = "City";
    final private String STATE_KEY = "State";
    final private String COUNTRY_KEY = "Country";
    TextView defCity, newCity, newCityDesc;
    static FirebaseAuth mAuth;
    static FirebaseFirestore db;
    static FirebaseUser firebaseUser;
    static public String UID;
    static SharedPreferences sharedpreferences;
    String defCityName, defStateName, newCityName = "", newStateName = "", defCountryName;
    CardView defCityCard, newCityCard, defCitySub;
    TextView defCitySubTV;
    EditText newCityET;
    Button submitNewCity;
    LinearLayout newCityLL;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = getLayoutInflater().inflate(R.layout.exp_step_one_info, container, false);
        defCity = v.findViewById(R.id.defCityExp); //TV
        newCity = v.findViewById(R.id.newCityExp); //TV
        newCityET = v.findViewById(R.id.enterCityExp);
        submitNewCity = v.findViewById(R.id.submitCityExp);

        newCityDesc = v.findViewById(R.id.newCityDescExp);
        defCityCard = v.findViewById(R.id.defaultLocCardExp);
        newCityCard = v.findViewById(R.id.addNewLocationCardExp);
        defCitySub = v.findViewById(R.id.defCitySubstitute);
        defCitySubTV = v.findViewById(R.id.defCitySubName);
        newCityLL = v.findViewById(R.id.newCityLL);
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        if (sharedpreferences.getString("UID", "").trim().equals(""))
            Toast.makeText(getActivity(), "Error retrieving UID", Toast.LENGTH_LONG).show();
        else
            UID = sharedpreferences.getString("UID", "");

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        //Go to your specific database directory or Child
        DocumentReference databaseReference = FirebaseFirestore.getInstance().collection("users").document(UID);
        databaseReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    defCityName = doc.get(CITY_KEY).toString();
                    defStateName = doc.get(STATE_KEY).toString();
                    defCountryName = doc.get(COUNTRY_KEY).toString();
                    defCity.setText(defCityName + ",\n" + defStateName);
                    defCitySubTV.setText("\t"+defCityName + "," + defStateName+ " - Explore nearby");
                }
            }
        });
        defCityCard.setOnClickListener(this);
        newCityCard.setOnClickListener(this);
        defCitySub.setOnClickListener(this);
        submitNewCity.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.defaultLocCardExp:
                getterSetterForExploreDisplay.setCity(defCityName);
                //getActivity().getSupportFragmentManager().beginTransaction().remove(getActivity().getSupportFragmentManager().findFragmentByTag("EFIO1")).commit();
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.expFragContainerInfo, new ExploreMatchDisplay(),"EMD").commit();
                break;
            case R.id.addNewLocationCardExp:
                newCityET.setVisibility(View.VISIBLE);
                newCity.setText("Explore New Location");
                newCityDesc.setText("Enter the city you want to explore and press EXPLORE");
                submitNewCity.setVisibility(View.VISIBLE);
                defCityCard.setVisibility(View.GONE);
                defCitySub.setVisibility(View.VISIBLE);
                newCityCard.setClickable(false);
                LinearLayout.LayoutParams cardViewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                newCityCard.setLayoutParams(cardViewParams);
                ViewGroup.MarginLayoutParams cardViewMarginParams = (ViewGroup.MarginLayoutParams) newCityCard.getLayoutParams();
                cardViewMarginParams.setMargins(20, 30, 20, 80);
                newCityCard.requestLayout();
                newCityLL.setPadding(20,40,20,20);
                break;

            case R.id.submitCityExp:
                getterSetterForExploreDisplay.setCity(newCityET.getText().toString());
                //getActivity().getSupportFragmentManager().beginTransaction().remove(getActivity().getSupportFragmentManager().findFragmentByTag("EFIO1")).commit();
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.expFragContainerInfo, new ExploreMatchDisplay(),"EMD").commit();
                break;
            case R.id.defCitySubstitute:
                getterSetterForExploreDisplay.setCity(defCityName);
                //getActivity().getSupportFragmentManager().beginTransaction().remove(getActivity().getSupportFragmentManager().findFragmentByTag("EFIO1")).commit();
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.expFragContainerInfo, new ExploreMatchDisplay(),"EMD").commit();
                break;
        }
    }


}
