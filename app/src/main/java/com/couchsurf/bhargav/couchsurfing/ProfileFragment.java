package com.couchsurf.bhargav.couchsurfing;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.renderscript.ScriptGroup;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.w3c.dom.Text;

import java.io.File;
import java.util.HashMap;

import static android.provider.Contacts.SettingsColumns.KEY;
import static java.security.KeyRep.Type.SECRET;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    ScrollView mainLayout;
    final static private String FOLDER = "s3Folder/userIcons/";
    final static private String EXT = ".jpg";
    static FirebaseAuth mAuth;
    TextView extProfile;
    static FirebaseFirestore db;
    static FirebaseUser firebaseUser;
    final private int PICKFILE_RESULT_CODE = 2; //just a req code
    private static String Uid;
    private String imagePath;
    private static final String KEY = "AKIAJAK4TMOGHLIM4IUQ";
    private static final String SECRET = "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";
    private static final String BUCKET_NAME = "couchsurfing-userfiles-mobilehub-151528593";
    ImageView profileImage;
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
    private int counter = 0;
    final private String CUSTOM_DP_KEY = "CUSTOM_DP";
    final private String DESC_KEY = "DESC";
    static int DP_CHANGE_COUNT;
    static boolean flag;
    final private static String DP_CHANGE_KEY = "DP_CHANGE_COUNT";
    SharedPreferences sharedpreferences;
    RequestOptions options;
    private TextView nameTitle;
    private Button typeTitle;
    static String url = "";
    private ImageButton descEdit, descDone, nameEdit, nameDone, numberEdit, numberDone, ageEdit, ageDone, addressEdit, addressDone, cityEdit, cityDone, stateEdit, stateDone, countryEdit, countryDone;

    private EditText desc, name, number, age, address, city, state, country;

    private boolean descEdited = false, nameEdited = false, numberEdited = false, ageEdited = false, addressEdited = false, cityEdited = false, stateEdited = false, countryEdited = false;

    public void downloadWithTransferUtility(String key) {

        BasicAWSCredentials credentials = new BasicAWSCredentials(KEY, SECRET);
        AmazonS3Client s3Client = new AmazonS3Client(credentials);

        TransferUtility transferUtility =
                TransferUtility.builder()
                        .context(getActivity().getApplicationContext())
                        .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                        .s3Client(s3Client)
                        .build();

        TransferObserver downloadObserver =
                transferUtility.download(FOLDER + key + EXT, new File("/sdcard/Pictures/" + key + ".jpg"));

        downloadObserver.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                if (TransferState.COMPLETED == state) {
                    Toast.makeText(getActivity(), "Image saved!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                int percentDone = (int) percentDonef;
            }

            @Override
            public void onError(int id, Exception ex) {
                Toast.makeText(getActivity(), "Shit happened, but it happens you know!", Toast.LENGTH_LONG).show();
                // Handle errors
            }

        });

    }

    public static void getUrlFromAws(final String key) {  //sets and unsets url variable
        url = "";
        flag = true;
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        Uid = firebaseUser.getUid();
                //lets make sure that this isnt some new user with zero dp, if thats the case return an empty string
                db.collection("users").document(Uid).get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                DocumentSnapshot doc = task.getResult();
                                DP_CHANGE_COUNT = Integer.parseInt(doc.get(DP_CHANGE_KEY).toString());
                                if (DP_CHANGE_COUNT == 0)
                                    flag = false;
                                else {
                                    BasicAWSCredentials credentials = new BasicAWSCredentials(KEY, SECRET);
                                    AmazonS3Client s3Client = new AmazonS3Client(credentials);
                                    url = s3Client.getResourceUrl(BUCKET_NAME, FOLDER + key + EXT);
                                }
                            }
                        });


    }

    public void uploadAndChangeDpWithTransferUtility(String key, String filepath) {


        BasicAWSCredentials credentials = new BasicAWSCredentials(KEY, SECRET);
        AmazonS3Client s3Client = new AmazonS3Client(credentials);

        TransferUtility transferUtility =
                TransferUtility.builder()
                        .context(getActivity().getApplicationContext())
                        .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                        .s3Client(s3Client)
                        .build();

        TransferObserver uploadObserver =
                transferUtility.upload(
                        FOLDER + key + EXT,
                        new File(filepath));

        // Attach a listener to the observer to get state update and progress notifications
        uploadObserver.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                if (TransferState.COMPLETED == state) {
                    updateProfilePic(profileImage);
                    MainActivity.updateNavProfilePic(getActivity().getBaseContext());
                    Toast.makeText(getActivity(), "Upload complete!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                int percentDone = (int) percentDonef;

                Log.d("YourActivity", "ID:" + id + " bytesCurrent: " + bytesCurrent
                        + " bytesTotal: " + bytesTotal + " " + percentDone + "%");
            }

            @Override
            public void onError(int id, Exception ex) {
                // Handle errors
                ex.printStackTrace();
                Toast.makeText(getActivity(), "Error uploading!!" + id, Toast.LENGTH_LONG).show();

            }

        });

        Log.d("YourActivity", "Bytes Transferrred: " + uploadObserver.getBytesTransferred());
        Log.d("YourActivity", "Bytes Total: " + uploadObserver.getBytesTotal());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = getLayoutInflater().inflate(R.layout.profile_layout, container, false);
        ((MainActivity) getActivity()).setActionBarTitle("Profile Setup");
        ((MainActivity) getActivity()).setNavItem(R.id.navprofile);
        mainLayout = v.findViewById(R.id.mainLayoutPL);
        extProfile = v.findViewById(R.id.extProfile);
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        Uid = sharedpreferences.getString("UID", "");
        profileImage = v.findViewById(R.id.userProfileImage);
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                galleryIntent.setType("image/jpg");
                Intent chooser = Intent.createChooser(galleryIntent, "Choose a new profile pic");
                startActivityForResult(chooser, PICKFILE_RESULT_CODE);

            }
        });
        // Toast.makeText(getActivity(), "sp dp counter is"+MainActivity.getDpChanegCounter(),Toast.LENGTH_LONG).show();
        options = new RequestOptions().signature(new ObjectKey(System.currentTimeMillis()));

        //Initializations
        nameTitle = v.findViewById(R.id.nameTitle);
        typeTitle = v.findViewById(R.id.typeTitle);
        descEdit = v.findViewById(R.id.descEditButton);
        nameEdit = v.findViewById(R.id.nameEditButton);
        //numberEdit = v.findViewById(R.id.phoneEditButton);
        ageEdit = v.findViewById(R.id.ageEditButton);
        addressEdit = v.findViewById(R.id.addressEditButton);
        cityEdit = v.findViewById(R.id.cityEditButton);
        stateEdit = v.findViewById(R.id.stateEditButton);
        countryEdit = v.findViewById(R.id.countryEditButton);
        descDone = v.findViewById(R.id.descDoneButton);
        nameDone = v.findViewById(R.id.nameDoneButton);
        //numberDone = v.findViewById(R.id.phoneDoneButton);
        ageDone = v.findViewById(R.id.ageDoneButton);
        addressDone = v.findViewById(R.id.addressDoneButton);
        cityDone = v.findViewById(R.id.cityDoneButton);
        stateDone = v.findViewById(R.id.stateDoneButton);
        countryDone = v.findViewById(R.id.countryDoneButton);
        desc = v.findViewById(R.id.desc);
        name = v.findViewById(R.id.name);
        number = v.findViewById(R.id.phone);
        age = v.findViewById(R.id.age);
        address = v.findViewById(R.id.address);
        city = v.findViewById(R.id.city);
        state = v.findViewById(R.id.state);
        country = v.findViewById(R.id.country);
        desc.setInputType(InputType.TYPE_NULL);
        name.setInputType(InputType.TYPE_NULL);
        number.setInputType(InputType.TYPE_NULL);
        age.setInputType(InputType.TYPE_NULL);
        city.setInputType(InputType.TYPE_NULL);
        state.setInputType(InputType.TYPE_NULL);
        country.setInputType(InputType.TYPE_NULL);
        address.setInputType(InputType.TYPE_NULL);
        extProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ExternalProfileViewActivity.class).putExtra("UID",Uid));

            }
        });
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        Uid = firebaseUser.getUid();
        Toast.makeText(getActivity().getBaseContext(),"your email is "+firebaseUser.getEmail(),Toast.LENGTH_LONG).show();
        updateProfilePic(profileImage);
        DocumentReference user = db.collection("users").document(Uid);
        user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (!doc.get(DESC_KEY).toString().trim().equals(""))
                        desc.setText(doc.get(DESC_KEY).toString());
                    name.setText(doc.get(NAME_KEY).toString());
                    nameTitle.setText(doc.get(NAME_KEY).toString());
                    number.setText(doc.get(PHONE_KEY).toString());
                    age.setText(doc.get(AGE_KEY).toString());
                    address.setText(doc.get(ADDRESS_KEY).toString());
                    city.setText(doc.get(CITY_KEY).toString());
                    state.setText(doc.get(STATE_KEY).toString());
                    country.setText(doc.get(COUNTRY_KEY).toString());
                    if (Math.floor((Long) doc.get(USERTYPE_KEY)) == 0)
                        typeTitle.setText("GUEST");
                    else if (Math.floor((Long) doc.get(USERTYPE_KEY)) == 1)
                        typeTitle.setText("HOST");
                    else
                        typeTitle.setText("ADMIN");


                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "ERROR retrieving info from server", Toast.LENGTH_LONG).show();

                    }
                });
        descEdit.setOnClickListener(this);
        nameEdit.setOnClickListener(this);
        //numberEdit.setOnClickListener(this);
        ageEdit.setOnClickListener(this);
        cityEdit.setOnClickListener(this);
        stateEdit.setOnClickListener(this);
        countryEdit.setOnClickListener(this);
        addressEdit.setOnClickListener(this);
        descDone.setOnClickListener(this);
        nameDone.setOnClickListener(this);
        //numberDone.setOnClickListener(this);
        ageDone.setOnClickListener(this);
        addressDone.setOnClickListener(this);
        cityDone.setOnClickListener(this);
        stateDone.setOnClickListener(this);
        countryDone.setOnClickListener(this);
        typeTitle.setOnClickListener(this);
        return v;

    }

    public void updateProfilePic(final ImageView profileImage) {
        DocumentReference databaseReference = FirebaseFirestore.getInstance().collection("users").document(firebaseUser.getUid());

        /*databaseReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                String glink = documentSnapshot.get("Photo_Uri").toString();
                String UID = firebaseUser.getUid();
                String link = ProfileFragment.getUrlFromAws(UID);
                boolean customDP = (Boolean) documentSnapshot.get("CUSTOM_DP");
                if (!glink.trim().equals("") && !customDP) {
                    //Glide.with(getBaseContext()).load("").thumbnail(0.5f).into(userImage);
                    Glide.with(getActivity().getBaseContext()).load(glink).apply(options).thumbnail(0.5f).into(profileImage);
                } else if (!link.trim().equals("")) {
                    Glide.with(getActivity().getBaseContext()).load(link).apply(options).thumbnail(0.5f).into(profileImage);
                    //options = new RequestOptions().signature(new ObjectKey((sharedpreferences.getInt("DP_CHANGE_COUNTER",0))+2));
                    // Glide.with(getActivity().getBaseContext()).load(link).apply(options).thumbnail(0.5f).into(profileImage);

                } else {
                    Glide.with(getActivity().getBaseContext()).load(R.drawable.def_user_icon).thumbnail(0.5f).into(profileImage);
                }
            }
        });*/
        databaseReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                final DocumentSnapshot documentSnapshot = task.getResult();
                final String glink = documentSnapshot.get("Photo_Uri").toString();
                String UID = firebaseUser.getUid();
                ProfileFragment.getUrlFromAws(UID);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String link = ProfileFragment.url;
                        //Toast.makeText(getActivity(),"got this"+link,Toast.LENGTH_LONG).show();
                        boolean customDP = (Boolean) documentSnapshot.get("CUSTOM_DP");
                        if (!glink.trim().equals("") && !customDP) {
                            //Glide.with(getBaseContext()).load("").thumbnail(0.5f).into(userImage);
                            Glide.with(getActivity().getBaseContext()).load(glink).apply(new RequestOptions().placeholder(R.drawable.def_user_icon).signature(new ObjectKey(System.currentTimeMillis()))).thumbnail(0.5f).thumbnail(0.5f).into(profileImage);
                        } else if (!link.trim().equals("")) {
                           // Toast.makeText(getActivity(),"got to here",Toast.LENGTH_LONG).show();

                            Glide.with(getActivity().getBaseContext()).load(link).apply(new RequestOptions().placeholder(R.drawable.def_user_icon).signature(new ObjectKey(System.currentTimeMillis()))).into(profileImage);
                            //options = new RequestOptions().signature(new ObjectKey((sharedpreferences.getInt("DP_CHANGE_COUNTER",0))+2));
                            // Glide.with(getActivity().getBaseContext()).load(link).apply(options).thumbnail(0.5f).into(profileImage);

                        } else {
                            Glide.with(getActivity().getBaseContext()).load(R.drawable.def_user_icon).thumbnail(0.5f).into(profileImage);
                        }
                    }
                },1000);

            }
        });
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICKFILE_RESULT_CODE && resultCode == Activity.RESULT_OK) {
            Uri content_describer = data.getData();
            int current = sharedpreferences.getInt("DP_CHANGE_COUNTER", 0);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putInt("DP_CHANGE_COUNTER", current + 1);
            editor.commit();
            imagePath = getRealPathFromURI(getActivity().getApplicationContext(), content_describer);
            DocumentReference updateDoc = db.collection("users").document(Uid);
            updateDoc.update(CUSTOM_DP_KEY, true);
            updateDoc.update(DP_CHANGE_KEY, ++current);
            uploadAndChangeDpWithTransferUtility(Uid, imagePath);


            //Toast.makeText(ProfileScreen.this,"Received"+imagePath,Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        final DocumentReference updateDoc = db.collection("users").document(Uid);
        int id = v.getId();
        switch (id) {
            case R.id.descEditButton:
                descEdited = true;
                desc.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                desc.requestFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(desc, InputMethodManager.SHOW_IMPLICIT);
                break;
            case R.id.nameEditButton:
                nameEdited = true;
                name.setInputType(InputType.TYPE_CLASS_TEXT);
                name.requestFocus();
                imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(name, InputMethodManager.SHOW_IMPLICIT);
                break;
            /*case R.id.phoneEditButton:
                numberEdited = true;
                number.setInputType(InputType.TYPE_CLASS_PHONE);
                number.requestFocus();
                imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(number, InputMethodManager.SHOW_IMPLICIT);
                break;*/
            case R.id.addressEditButton:
                addressEdited = true;
                address.setInputType(InputType.TYPE_CLASS_TEXT);
                address.requestFocus();
                imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(address, InputMethodManager.SHOW_IMPLICIT);
                break;
            case R.id.ageEditButton:
                ageEdited = true;
                age.setInputType(InputType.TYPE_CLASS_NUMBER);
                age.requestFocus();
                imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(age, InputMethodManager.SHOW_IMPLICIT);
                break;
            case R.id.cityEditButton:
                cityEdited = true;
                city.setInputType(InputType.TYPE_CLASS_TEXT);
                city.requestFocus();
                imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(city, InputMethodManager.SHOW_IMPLICIT);
                break;
            case R.id.stateEditButton:
                stateEdited = true;
                state.setInputType(InputType.TYPE_CLASS_TEXT);
                state.requestFocus();
                imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(state, InputMethodManager.SHOW_IMPLICIT);
                break;
            case R.id.countryEditButton:
                countryEdited = true;
                country.setInputType(InputType.TYPE_CLASS_TEXT);
                country.requestFocus();
                imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(country, InputMethodManager.SHOW_IMPLICIT);
                break;
            case R.id.descDoneButton:
                if (descEdited) {
                    imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);
                    String descText = desc.getText().toString();
                    descEdited = false;
                    desc.setInputType(InputType.TYPE_NULL);
                    updateDoc.update(DESC_KEY, descText);
                }
                break;
            case R.id.nameDoneButton:
                if (nameEdited) {
                    imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);
                    String nameText = name.getText().toString();
                    nameEdited = false;
                    name.setInputType(InputType.TYPE_NULL);
                    nameTitle.setText(nameText);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("UNAME", nameText);
                    editor.commit();
                    updateDoc.update(NAME_KEY, nameText);
                }
                break;
            /*case R.id.phoneDoneButton:
                if (numberEdited) {
                    imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);
                    String phoneNumber = number.getText().toString();
                    numberEdited = false;
                    number.setInputType(InputType.TYPE_NULL);
                    updateDoc.update(PHONE_KEY, phoneNumber);
                }
                break;*/
            case R.id.ageDoneButton:
                if (ageEdited) {
                    imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);
                    String ageNumber = age.getText().toString();
                    ageEdited = false;
                    age.setInputType(InputType.TYPE_NULL);
                    updateDoc.update(AGE_KEY, ageNumber);
                }
                break;
            case R.id.cityDoneButton:
                if (cityEdited) {
                    imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);
                    String cityText = city.getText().toString();
                    cityEdited = false;
                    city.setInputType(InputType.TYPE_NULL);
                    updateDoc.update(CITY_KEY, cityText);
                }
                break;
            case R.id.stateDoneButton:
                if (stateEdited) {
                    imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);
                    String stateText = state.getText().toString();
                    descEdited = false;
                    state.setInputType(InputType.TYPE_NULL);
                    updateDoc.update(STATE_KEY, stateText);
                }
                break;
            case R.id.countryDoneButton:
                if (countryEdited) {
                    imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);
                    String countryText = country.getText().toString();
                    countryEdited = false;
                    country.setInputType(InputType.TYPE_NULL);
                    updateDoc.update(COUNTRY_KEY, countryText);
                }
                break;
            case R.id.addressDoneButton:
                if (addressEdited) {
                    imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);
                    String addressText = address.getText().toString();
                    addressEdited = false;
                    address.setInputType(InputType.TYPE_NULL);
                    updateDoc.update(ADDRESS_KEY, addressText);
                }
                break;
            case R.id.typeTitle:
                if (typeTitle.getText().equals("GUEST")) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("BECOME A HOST")
                            .setMessage("Are you really ready to be a Host? (You can still travel if you're a host Don't worry!)")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    switch (whichButton) {
                                        case DialogInterface.BUTTON_POSITIVE:
                                            updateDoc.update(USERTYPE_KEY, 1);
                                            final SharedPreferences.Editor editor = sharedpreferences.edit();
                                            editor.putInt("USER_TYPE", 1);
                                            editor.commit();
                                            break;

                                        case DialogInterface.BUTTON_NEGATIVE:
                                            //No button clicked
                                            break;
                                    }
                                }
                            })
                            .setNegativeButton(android.R.string.no, null).show();
                } else if (typeTitle.getText().equals("HOST")) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("BECOME A GUEST")
                            .setMessage("Are you sure about retiring from being a wonderful Host?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    switch (whichButton) {
                                        case DialogInterface.BUTTON_POSITIVE:
                                            updateDoc.update(USERTYPE_KEY, 0);
                                            final SharedPreferences.Editor editor = sharedpreferences.edit();
                                            editor.putInt("USER_TYPE", 0);
                                            editor.commit();
                                            break;

                                        case DialogInterface.BUTTON_NEGATIVE:
                                            //No button clicked
                                            break;
                                    }
                                }
                            })
                            .setNegativeButton(android.R.string.no, null).show();

                } else {
                    Toast.makeText(getActivity(), "You're an ADMIN dumbass!!!!", Toast.LENGTH_LONG).show();
                }


        }
    }

    //FOR CAMERA INTENT ---- WIP
    /* public Uri getOutputMediaFileUri(String uniqueImageId){

        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "IMAGE_DIRECTORY_NAME");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }


        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + uniqueImageId + ".jpg");

        return Uri.fromFile(mediaFile);
    }*/


}
