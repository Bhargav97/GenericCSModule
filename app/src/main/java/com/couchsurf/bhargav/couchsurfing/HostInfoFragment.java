package com.couchsurf.bhargav.couchsurfing;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class HostInfoFragment extends Fragment {
    Button submitButton;
    EditText phoneInput, addressInput, nameInput, cityInput, countryInput, stateInput, ageInput;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

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
    final private int counter = 0;
    final private String CUSTOM_DP_KEY = "CUSTOM_DP";
    final private String DESC_KEY = "DESC";
    final private String DP_CHANGE_KEY = "DP_CHANGE_COUNT";
    final private String COUCH_ID_COUNTER_KEY = "Couch_Created_Till_Date";

    final private String PENDING_REQ_KEY = "PENDING_REQUEST";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.host_info_layout, container, false);


        submitButton = v.findViewById(R.id.submitButton);
        mAuth = FirebaseAuth.getInstance();
        phoneInput = v.findViewById(R.id.phone);
        phoneInput.setInputType(InputType.TYPE_NULL);
        addressInput = v.findViewById(R.id.address);
        nameInput = v.findViewById(R.id.name);
        cityInput = v.findViewById(R.id.city);
        countryInput = v.findViewById(R.id.country);
        stateInput = v.findViewById(R.id.state);
        ageInput = v.findViewById(R.id.age);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //Auto-fill name and phone number if available
        try {
            if (currentUser.getDisplayName() != null) {
                String name = currentUser.getDisplayName();
                nameInput.setText(name);
            }
        }
        catch (Exception e){}
        try {
            if (currentUser.getPhoneNumber() != null) {
                String phoneNumber = currentUser.getPhoneNumber();
                phoneInput.setText(phoneNumber);
            }
        }
        catch (Exception e){}
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                String UID = currentUser.getUid();
                String email = currentUser.getEmail();
                String name = "", photoUrl = "", phoneNumber = "", country = "", state = "", city = "", address = "", age = "";

                if (nameInput.getText().toString().trim().equalsIgnoreCase("")) {
                    nameInput.setError("This field can not be blank");
                } else {
                    name = nameInput.getText().toString();
                }

                if (currentUser.getPhotoUrl() != null)
                    photoUrl = currentUser.getPhotoUrl().toString();


                if (phoneInput.getText().toString().trim().equalsIgnoreCase("")) {
                    phoneInput.setError("This field can not be blank");
                } else {
                    phoneNumber = phoneInput.getText().toString();
                }

                if (ageInput.getText().toString().trim().equalsIgnoreCase("")) {
                    ageInput.setError("This field can not be blank");
                } else {
                    age = ageInput.getText().toString();
                }
                if (addressInput.getText().toString().trim().equalsIgnoreCase("")) {
                    addressInput.setError("This field can not be blank");
                } else {
                    address = addressInput.getText().toString();
                }
                if (cityInput.getText().toString().trim().equalsIgnoreCase("")) {
                    cityInput.setError("This field can not be blank");
                } else {
                    city = cityInput.getText().toString();
                }
                if (stateInput.getText().toString().trim().equalsIgnoreCase("")) {
                    stateInput.setError("This field can not be blank");
                } else {
                    state = stateInput.getText().toString();
                }
                if (countryInput.getText().toString().trim().equalsIgnoreCase("")) {
                    countryInput.setError("This field can not be blank");
                } else {
                    country = countryInput.getText().toString();
                }


                if (!name.equals("") && !phoneNumber.equals("") && !country.equals("") && !state.equals("") && !city.equals("") && !address.equals("") && !age.equals("")) {
                    Map<String, Object> newUser = new HashMap<>();
                    newUser.put(EMAIL_KEY, email);
                    newUser.put(PHONE_KEY, phoneNumber);
                    newUser.put(ADDRESS_KEY, address);
                    newUser.put(NAME_KEY, name);
                    newUser.put(CITY_KEY, city);
                    newUser.put(STATE_KEY, state);
                    newUser.put(PHOTO_KEY, photoUrl);
                    newUser.put(COUNTRY_KEY, country);
                    newUser.put(AGE_KEY, age);
                    newUser.put(USERTYPE_KEY, 1);
                    newUser.put(COUCHCOUNTER_KEY, 0);
                    newUser.put(BOOKING_COUNTER_KEY, 0);
                    newUser.put(CUSTOM_DP_KEY,false);
                    newUser.put(DESC_KEY,"");
                    newUser.put(DP_CHANGE_KEY,0);
                    newUser.put(COUCH_ID_COUNTER_KEY,0);
                    newUser.put(PENDING_REQ_KEY,0);
                    //Creating an empty couch subcollection first
                    HashMap<String,Object> init = new HashMap<>();
                    init.put("ContainsData",false); //ContainsData will be true if a couch has been registered
                    db = FirebaseFirestore.getInstance();
                    db.collection("users").document(UID).collection("couches").document(Integer.toString(counter)).set(init)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //Toast.makeText(getActivity(), "NEW STUNT SUCCESSFUL",
                                      //      Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), "FAILED FAILED FAILED" + e.toString(),
                                            Toast.LENGTH_SHORT).show();
                                    Log.d("TAG", e.toString());
                                }
                            });
                    HashMap<String,Object> init2 = new HashMap<>();
                    init2.put("ContainsData",false); //ContainsData will be true if a couch has been registered
                    db = FirebaseFirestore.getInstance();
                    db.collection("users").document(UID).collection("bookings").document(Integer.toString(counter)).set(init2)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //Toast.makeText(getActivity(), "NEW STUNT SUCCESSFUL",
                                    //      Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), "FAILED FAILED FAILED" + e.toString(),
                                            Toast.LENGTH_SHORT).show();
                                    Log.d("TAG", e.toString());
                                }
                            });
                    db = FirebaseFirestore.getInstance();
                    db.collection("users").document(UID).set(newUser)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getContext(), "User Registered",
                                            Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), "ERROR" + e.toString(),
                                            Toast.LENGTH_SHORT).show();
                                    Log.d("TAG", e.toString());
                                }
                            });
                    SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putBoolean("SIGNED_IN", true);
                    editor.putInt("USER_TYPE",1); //True is host
                    editor.putString("UID",UID);
                    editor.putString("UNAME",name);
                    editor.putInt("DP_CHANGE_COUNTER",0);
                    editor.putInt("PENDING_REQUESTS",0);
                    editor.commit();
                    startActivity(new Intent(getActivity(), MainActivity.class));
                    getActivity().finish();
                }

            }
        });
        return v;
    }
}
