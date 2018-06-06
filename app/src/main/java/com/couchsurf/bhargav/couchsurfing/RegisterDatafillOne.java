package com.couchsurf.bhargav.couchsurfing;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterDatafillOne extends Fragment implements View.OnClickListener{
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



    TextView defCity, newCity, newCityDesc;
    static FirebaseAuth mAuth;
    static FirebaseFirestore db;
    static FirebaseUser firebaseUser;
    static public String UID;
    static SharedPreferences sharedpreferences;
    String defCityName, defStateName, newCityName="", newStateName="", defCountryName;
    CardView defCityCard, newCityCard;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.reg_datafill_one, container, false);
        defCity = v.findViewById(R.id.defCity);
        newCity = v.findViewById(R.id.newCity);
        newCityDesc = v.findViewById(R.id.newCityDesc);
        defCityCard = v.findViewById(R.id.defaultLocCard);
        newCityCard = v.findViewById(R.id.addNewLocationCard);
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        if(sharedpreferences.getString("UID","").trim().equals(""))
            Toast.makeText(getActivity(), "Error retrieving UID",Toast.LENGTH_LONG).show();
        else
            UID = sharedpreferences.getString("UID","");

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        //Go to your specific database directory or Child
        DocumentReference databaseReference = FirebaseFirestore.getInstance().collection("users").document(UID);
        databaseReference.get().addOnCompleteListener(new OnCompleteListener< DocumentSnapshot >() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    defCityName = doc.get(CITY_KEY).toString();
                    defStateName = doc.get(STATE_KEY).toString();
                    defCountryName = doc.get(COUNTRY_KEY).toString();
                    defCity.setText(defCityName+",\n"+defStateName);
                }
            }
        });
        defCityCard.setOnClickListener(this);
        newCityCard.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.defaultLocCard:
                RegisterDatafillTwo.pushData(defCityName,defStateName,defCountryName);
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.refFragContainerRegInfo, new RegisterDatafillTwo(),"REG_DF_TWO").commit();
                getActivity().getSupportFragmentManager().beginTransaction().remove(getActivity().getSupportFragmentManager().findFragmentByTag("REG_IMAGE_ONE")).commit();
                break;

            case R.id.addNewLocationCard:
                if(newCityName.equals("")||newStateName.equals("")) {
                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                    alertDialog.setTitle("Enter New Location");
                    alertDialog.setMessage("Enter city and State, seperated by comma (like - Thane, Mumbai)");
                    final EditText input = new EditText(getActivity());
                    alertDialog.setView(input);

                    alertDialog.setPositiveButton("DONE",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (!input.getText().toString().contains(",") || input.getText().toString().trim().equals("")) {
                                        input.setError("Enter data in a valid format");
                                        Toast.makeText(getActivity(),"Enter valid data",Toast.LENGTH_LONG).show();

                                    } else {
                                        String inputStr = input.getText().toString();
                                        String[] arr = inputStr.split(",");
                                        newCityName = arr[0].trim();
                                        newStateName = arr[1].trim();
                                        newCity.setText(newCityName+",\n"+newStateName);
                                        newCityDesc.setText("Press here again to confirm this location");
                                        //Toast.makeText(getActivity(),"I was clicked",Toast.LENGTH_LONG).show();

                                    }

                                }
                            });

                    alertDialog.setNegativeButton("CANCEL",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //Toast.makeText(getActivity(),"I was clicked",Toast.LENGTH_LONG).show();

                                    dialog.cancel();
                                }
                            });

                    alertDialog.show();
                    //set Data to Activity
                }
                else{
                    RegisterDatafillTwo.pushData(newCityName,newStateName,defCountryName);
                    getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.refFragContainerRegInfo, new RegisterDatafillTwo(),"REG_DF_TWO").commit();
                    getActivity().getSupportFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentByTag("REG_IMAGE_ONE")).commit();

                }
                break;
        }
    }
}
