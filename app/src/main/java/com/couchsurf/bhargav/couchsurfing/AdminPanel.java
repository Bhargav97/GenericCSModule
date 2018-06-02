package com.couchsurf.bhargav.couchsurfing;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

public class AdminPanel extends AppCompatActivity {

    private void generateDivider(LinearLayout ll) {
        LayoutInflater myInflater = getLayoutInflater();
        View myView = myInflater.inflate(R.layout.divider_line_view, ll, false);
        ll.addView(myView);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_panel);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
       /* TextView tv = new TextView(getApplicationContext());
        tv.setText(Html.fromHtml(currentOutput));
        tv.setTextAppearance(ctx, R.style.BinarySearchDynamicOutputView);
        ll.addView(tv);
        generateDivider(ll,linf);*/
        final LinearLayout ll = findViewById(R.id.mainLayoutAP);
        //DISPLAY HOSTS
        db.collection("users")
                .whereEqualTo("User_Type", 1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        int flag = 0;
                        if (task.isSuccessful()) {
                            TextView tv = new TextView(getBaseContext());
                            tv.setText("DISPLAYING ALL OUR HOSTS");
                            tv.setTextAppearance(getApplicationContext(), R.style.AdminPanelDynamicOutputView);
                            ll.addView(tv);
                            generateDivider(ll);
                            for (final QueryDocumentSnapshot document : task.getResult()) {

                                String str = document.getData().toString();
                                tv = new TextView(getBaseContext());
                                tv.setText(str);
                                tv.setTextAppearance(getBaseContext(), R.style.AdminPanelDynamicOutputView);
                                ll.addView(tv);
                                //Get his bookings

                                db.collection("users").document(document.getId()).collection("bookings").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                        if (task.isSuccessful()) {
                                            TextView tv = new TextView(getBaseContext());
                                            tv.setText(document.getString("Name") + "'s " + "BOOKINGS ARE-");
                                            tv.setTextAppearance(getApplicationContext(), R.style.AdminPanelDynamicOutputView);
                                            ll.addView(tv);
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                String str = document.getData().toString();
                                                tv = new TextView(getBaseContext());
                                                tv.setText(str);
                                                tv.setTextAppearance(getBaseContext(), R.style.AdminPanelDynamicOutputView);
                                                ll.addView(tv);
                                                generateDivider(ll);
                                            }
                                        } else {
                                            Toast.makeText(getBaseContext(), "SOMETHINGS WRONG", Toast.LENGTH_LONG).show();

                                        }
                                    }
                                });


                                //Get his couches


                                db.collection("users").document(document.getId()).collection("couches").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                        if (task.isSuccessful()) {
                                            TextView tv = new TextView(getBaseContext());
                                            tv.setText(document.getString("Name") + "'s " + " Couches ARE-");
                                            tv.setTextAppearance(getApplicationContext(), R.style.AdminPanelDynamicOutputView);
                                            ll.addView(tv);
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                String str = document.getData().toString();
                                                tv = new TextView(getBaseContext());
                                                tv.setText(str);
                                                tv.setTextAppearance(getBaseContext(), R.style.AdminPanelDynamicOutputView);
                                                ll.addView(tv);
                                                generateDivider(ll);
                                            }
                                        }
                                    }
                                });

                                generateDivider(ll);
                                //Log.d("TAG", document.getId() + " => " + document.getData());
                            }

                            //Toast.makeText(getBaseContext(),"I was here for - "+counter,Toast.LENGTH_LONG).show();

                        } else {
                            Toast.makeText(getBaseContext(), "SOMETHINGS WRONG", Toast.LENGTH_LONG).show();
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });

        //DISLAY GUESTS

        db.collection("users")
                .

                        whereEqualTo("User_Type", 0)
                .

                        get()
                .

                        addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    TextView tv2 = new TextView(getBaseContext());
                                    tv2.setText("DISPLAYING ALL OUR GUESTS");
                                    tv2.setTextAppearance(getApplicationContext(), R.style.AdminPanelDynamicOutputView);
                                    ll.addView(tv2);
                                    generateDivider(ll);
                                    for (final QueryDocumentSnapshot document : task.getResult()) {

                                        String str = document.getData().toString();
                                        tv2 = new TextView(getBaseContext());
                                        tv2.setText(str);
                                        tv2.setTextAppearance(getBaseContext(), R.style.AdminPanelDynamicOutputView);
                                        ll.addView(tv2);
                                        generateDivider(ll);
                                        //Get his bookings

                                        db.collection("users").document(document.getId()).collection("bookings").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                if (task.isSuccessful()) {
                                                    TextView tv = new TextView(getBaseContext());
                                                    tv.setText(document.getString("Name") + "'s " + "BOOKINGS ARE-");
                                                    tv.setTextAppearance(getApplicationContext(), R.style.AdminPanelDynamicOutputView);
                                                    ll.addView(tv);
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        String str = document.getData().toString();
                                                        tv = new TextView(getBaseContext());
                                                        tv.setText(str);
                                                        tv.setTextAppearance(getBaseContext(), R.style.AdminPanelDynamicOutputView);
                                                        ll.addView(tv);
                                                        generateDivider(ll);
                                                    }
                                                } else {
                                                    Toast.makeText(getBaseContext(), "SOMETHINGS WRONG", Toast.LENGTH_LONG).show();

                                                }
                                            }
                                        });
                                        //Log.d("TAG", document.getId() + " => " + document.getData());
                                    }
                                } else {
                                    Toast.makeText(getBaseContext(), "SOMETHINGS WRONG", Toast.LENGTH_LONG).show();
                                    Log.d("TAG", "Error getting documents: ", task.getException());
                                }
                            }
                        });
        //DISLAY ADMINS
        db.collection("users")
                .

                        whereEqualTo("User_Type", 2)
                .

                        get()
                .

                        addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    TextView tv3 = new TextView(getBaseContext());
                                    tv3.setText("DISPLAYING ALL OUR ADMINS");
                                    tv3.setTextAppearance(getApplicationContext(), R.style.AdminPanelDynamicOutputView);
                                    ll.addView(tv3);
                                    generateDivider(ll);
                                    for (final QueryDocumentSnapshot document : task.getResult()) {

                                        String str = document.getData().toString();
                                        tv3 = new TextView(getBaseContext());
                                        tv3.setText(str);
                                        tv3.setTextAppearance(getBaseContext(), R.style.AdminPanelDynamicOutputView);
                                        ll.addView(tv3);
                                        generateDivider(ll);
                                        //Get his bookings

                                        db.collection("users").document(document.getId()).collection("couches").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                if (task.isSuccessful()) {
                                                    TextView tv = new TextView(getBaseContext());
                                                    tv.setText(document.getString("Name") + "'s " + "Couches ARE-");
                                                    tv.setTextAppearance(getApplicationContext(), R.style.AdminPanelDynamicOutputView);
                                                    ll.addView(tv);
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        String str = document.getData().toString();
                                                        tv = new TextView(getBaseContext());
                                                        tv.setText(str);
                                                        tv.setTextAppearance(getBaseContext(), R.style.AdminPanelDynamicOutputView);
                                                        ll.addView(tv);
                                                        generateDivider(ll);
                                                    }
                                                } else {
                                                    Toast.makeText(getBaseContext(), "SOMETHINGS WRONG", Toast.LENGTH_LONG).show();

                                                }
                                            }
                                        });
                                        //Get his bookings

                                        db.collection("users").document(document.getId()).collection("bookings").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                if (task.isSuccessful()) {
                                                    TextView tv = new TextView(getBaseContext());
                                                    tv.setText(document.getString("Name") + "'s " + "BOOKINGS ARE-");
                                                    tv.setTextAppearance(getApplicationContext(), R.style.AdminPanelDynamicOutputView);
                                                    ll.addView(tv);
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        String str = document.getData().toString();
                                                        tv = new TextView(getBaseContext());
                                                        tv.setText(str);
                                                        tv.setTextAppearance(getBaseContext(), R.style.AdminPanelDynamicOutputView);
                                                        ll.addView(tv);
                                                        generateDivider(ll);
                                                    }
                                                } else {
                                                    Toast.makeText(getBaseContext(), "SOMETHINGS WRONG", Toast.LENGTH_LONG).show();

                                                }
                                            }
                                        });
                                        //Log.d("TAG", document.getId() + " => " + document.getData());
                                    }
                                } else {
                                    Toast.makeText(getBaseContext(), "SOMETHINGS WRONG", Toast.LENGTH_LONG).show();
                                    Log.d("TAG", "Error getting documents: ", task.getException());
                                }
                            }
                        });

    }


}
