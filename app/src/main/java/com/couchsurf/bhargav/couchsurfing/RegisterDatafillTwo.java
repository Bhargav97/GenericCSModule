package com.couchsurf.bhargav.couchsurfing;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class RegisterDatafillTwo extends Fragment implements View.OnClickListener {
    ImageView img1, img2, img3, img4, img5, img6;
    private static final String KEY = "AKIAJAK4TMOGHLIM4IUQ";
    private static final String SECRET = "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";
    private static final String BUCKET_NAME = "couchsurfing-userfiles-mobilehub-151528593";
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

    final private String COUCH_OWNER_UNAME_KEY = "Couch_Owner_Name";
    final private String COUCH_ID_COUNTER_KEY = "Couch_Created_Till_Date"; //includes deleted
    final private String COUCH_ID_KEY = "Couch_Id";
    final private String COUCH_IMAGES_COUNTER_KEY = "No_Of_Images";
    final private String COUCH_NAME_KEY = "Name";
    final private String NO_OF_ROOMS_KEY = "No_Of_Rooms";
    final private String NO_OF_ADULTS_KEY = "No_Of_Adults";
    final private String COUCH_DESC_KEY = "Desc_Of_Couch";
    final private String COUCH_CITY_KEY = "City_Of_Couch";
    final private String COUCH_STATE_KEY = "State_Of_Couch";
    final private String COUCH_COUNTRY_KEY = "Country_Of_Couch";
    final private String COUCH_PET_KEY = "Pets_Allowed";
    final private String TIME_ADDED_KEY = "Time_Of_Posting";
    final private String COUCH_ADD_KEY = "Address_Of_Couch";
    final private String COUCH_OWNER_UID_KEY = "Owner_Of_Couch_Is";
    final private String COUCH_GLOBAL_ID_KEY = "Global_Couch_Id";

    final private static String GLOBAL_COUCH_COUNTER = "global_couch_counter";

    View v;
    static FirebaseAuth mAuth;
    static FirebaseFirestore db;
    static FirebaseUser firebaseUser;
    static public String UID, UNAME;
    static SharedPreferences sharedpreferences;
    final static private String COUCHFOLDER = "s3Folder/couchPics/";
    final static private String EXT = ".jpg";
    ImageView current;
    public int current_couch_id_counter;
    public int current_couch_counter; //this represents successfully registered number of couches at the moment, it will be updated when this form is submitted
    public int current_couch_images_counter = 0;
    ImageButton dec, inc, decadults, incadults;
    TextView roomNumber, adultNumber;
    private RadioGroup radioPetsGroup;
    private RadioButton radioPetButton;
    EditText nameCouch, addressCouch, descCouch;
    Button submitButton;
    ScrollView mainLayout;
    String[] mimeTypes = {"image/jpeg", "image/png"};
    public boolean img1clicked = false, img2clicked = false, img3clicked = false, img4clicked = false, img5clicked = false, img6clicked = false;
    public static String couchCity, couchState, couchCountry;
    public static void pushData(String city, String state, String country){
        couchCity=city; couchState=state; couchCountry=country;
    }
    public static int global_couch_counter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.reg_form, container, false);
        current_couch_images_counter = 0;
        img1 = v.findViewById(R.id.img1);
        img2 = v.findViewById(R.id.img2);
        img3 = v.findViewById(R.id.img3);
        img4 = v.findViewById(R.id.img4);
        img5 = v.findViewById(R.id.img5);
        img6 = v.findViewById(R.id.img6);
        dec = v.findViewById(R.id.decRooms);
        inc = v.findViewById(R.id.incRooms);
        decadults = v.findViewById(R.id.decAdults);
        incadults = v.findViewById(R.id.incAdults);
        adultNumber = v.findViewById(R.id.adultNumber);
        roomNumber = v.findViewById(R.id.roomNumber);
        radioPetsGroup = (RadioGroup) v.findViewById(R.id.radioPets);
        nameCouch = v.findViewById(R.id.nameCouch);
        addressCouch = v.findViewById(R.id.addressCouch);
        descCouch = v.findViewById(R.id.descCouch);
        submitButton = v.findViewById(R.id.submitButton);
        mainLayout = v.findViewById(R.id.mainLayoutRC);
        PermissionUtil permissionUtil = new PermissionUtil();
        ActivityCompat.requestPermissions(getActivity(),
                permissionUtil.getCameraPermissions(),
                1);
        ActivityCompat.requestPermissions(getActivity(),
                permissionUtil.getGalleryPermissions(),
                2);
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        if (sharedpreferences.getString("UID", "").trim().equals(""))
            Toast.makeText(getActivity(), "Error retrieving UID", Toast.LENGTH_LONG).show();
        else
            UID = sharedpreferences.getString("UID", "");
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        //Init COUCHCOUNTER_KEY to be utilised by uploadUtility
        DocumentReference databaseReference = FirebaseFirestore.getInstance().collection("users").document(UID);
        databaseReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    current_couch_id_counter = Math.round((Long) doc.get(COUCH_ID_COUNTER_KEY));
                    current_couch_counter = Math.round((Long) doc.get(COUCHCOUNTER_KEY));
                    UNAME = doc.get(NAME_KEY).toString();
                    //Toast.makeText(getActivity(),Integer.toString(current_couch_counter),Toast.LENGTH_LONG).show();
                    editor.putInt(COUCHCOUNTER_KEY, current_couch_counter);
                    editor.commit();
                }
            }
        });

        db.collection("metadata").document("couch").get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        global_couch_counter = Integer.parseInt(documentSnapshot.get(GLOBAL_COUCH_COUNTER).toString());
                    }
                });
        /*DocumentReference newCouchReference = FirebaseFirestore.getInstance().collection("users").document(UID).collection("couches").document(Integer.toString(current_couch_counter + 1));
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(COUCH_IMAGES_COUNTER_KEY, 0);
        db.collection("users").document(UID).collection("couches").document(Integer.toString(current_couch_counter + 1)).set(hashMap)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "FAILED FAILED FAILED" + e.toString(),
                                Toast.LENGTH_SHORT).show();
                        Log.d("TAG", e.toString());
                    }
                });
`       */

        img1.setOnClickListener(this);
        img2.setOnClickListener(this);
        img3.setOnClickListener(this);
        img4.setOnClickListener(this);
        img5.setOnClickListener(this);
        img6.setOnClickListener(this);
        dec.setOnClickListener(this);
        inc.setOnClickListener(this);
        decadults.setOnClickListener(this);
        incadults.setOnClickListener(this);
        submitButton.setOnClickListener(this);
        return v;
    }

    public void updateImageCounterLocalAndAtFirestore() {
        current_couch_images_counter += 1;
        //HashMap<String, Object> hashMap = new HashMap<>();
       // int newCounter = current_couch_images_counter + 1;
        /*hashMap.put(COUCH_IMAGES_COUNTER_KEY, newCounter);
        Toast.makeText(getActivity(), Integer.toString(current_couch_images_counter),
                Toast.LENGTH_SHORT).show();

        db.collection("users").document(UID).collection("couches").document(Integer.toString(current_couch_counter + 1)).set(hashMap)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "FAILED FAILED FAILED" + e.toString(),
                                Toast.LENGTH_SHORT).show();
                        Log.d("TAG", e.toString());
                    }
                });*/
    }

    public void submitTheCouch() {
        Map<String, Object> map;
        int selectedId = radioPetsGroup.getCheckedRadioButtonId();
        radioPetButton = (RadioButton) v.findViewById(selectedId);
        boolean pet = false;
        if (radioPetButton.getText().toString().equals("YES"))
            pet = true;

        String couchName = nameCouch.getText().toString();
        String couchDesc = descCouch.getText().toString();
        final String couchAdd = addressCouch.getText().toString();
        int rooms = Integer.parseInt(roomNumber.getText().toString());
        int adults = Integer.parseInt(adultNumber.getText().toString());
        Date currentTime = Calendar.getInstance().getTime();
        String timeAdded = currentTime.toString();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(COUCH_NAME_KEY, couchName);
        hashMap.put(COUCH_DESC_KEY, couchDesc);
        hashMap.put(NO_OF_ROOMS_KEY, rooms);
        hashMap.put(TIME_ADDED_KEY, timeAdded);
        hashMap.put(NO_OF_ADULTS_KEY, adults);
        hashMap.put(COUCH_CITY_KEY,couchCity);
        hashMap.put(COUCH_STATE_KEY,couchState);
        hashMap.put(COUCH_ADD_KEY,couchAdd);
        hashMap.put(COUCH_COUNTRY_KEY,couchCountry);
        hashMap.put(COUCH_PET_KEY,pet);
        hashMap.put(COUCH_ID_KEY,(current_couch_id_counter+1));
        hashMap.put(COUCH_IMAGES_COUNTER_KEY, current_couch_images_counter);
        hashMap.put(COUCH_OWNER_UID_KEY, UID);
        hashMap.put(COUCH_OWNER_UNAME_KEY, UNAME);
        hashMap.put(COUCH_GLOBAL_ID_KEY,(global_couch_counter+1));
        db.collection("users").document(UID).collection("couches").document(Integer.toString(current_couch_counter + 1)).set(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //Toast.makeText(getActivity(), "Data successfully submitted",
                               // Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "FAILED FAILED FAILED" + e.toString(),
                        Toast.LENGTH_SHORT).show();
                Log.d("TAG", e.toString());
            }
        });



        global_couch_counter++;
        current_couch_counter++;
        current_couch_id_counter++;

        db.collection("metadata").document("couch").update(GLOBAL_COUCH_COUNTER,global_couch_counter);

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt(COUCHCOUNTER_KEY,current_couch_counter);
        editor.commit();
        db.collection("users").document(UID).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            Map<String, Object> map;
                            map = doc.getData();
                            map.put(COUCHCOUNTER_KEY, current_couch_counter);
                            map.put(COUCH_ID_COUNTER_KEY, current_couch_id_counter);
                            db.collection("users").document(UID).set(map)
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getActivity(), "FAILED FAILED FAILED" + e.toString(),
                                                    Toast.LENGTH_SHORT).show();
                                            Log.d("TAG", e.toString());
                                        }
                                    });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "ERROR retrieving info from server", Toast.LENGTH_LONG).show();

            }
        });
        //COUCHCOUNTER_KEY++;

        //
        //
        //			// find the radiobutton by returned id
        //
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.submitButton:
                submitTheCouch();
                Snackbar snackbar = Snackbar.make(mainLayout, "Your couch has been submitted", Snackbar.LENGTH_LONG);
                snackbar.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(getActivity(), MainActivity.class));
                    }
                }, 3000);
                break;

            case R.id.decRooms:
                int num = Integer.parseInt(roomNumber.getText().toString());
                if (num == 1) {
                } else {
                    num--;
                    roomNumber.setText(Integer.toString(num));
                }
                break;
            case R.id.incRooms:
                num = Integer.parseInt(roomNumber.getText().toString());
                num++;
                roomNumber.setText(Integer.toString(num));
                break;

            case R.id.decAdults:
                num = Integer.parseInt(adultNumber.getText().toString());
                if (num == 1) {
                } else {
                    num--;
                    adultNumber.setText(Integer.toString(num));
                }
                break;
            case R.id.incAdults:
                num = Integer.parseInt(adultNumber.getText().toString());
                num++;
                adultNumber.setText(Integer.toString(num));
                break;
            case R.id.img1:
                if (img1clicked) {

                } else {
                    img1clicked = true;
                    current = img1;
                    selectImage();
                }
                break;
            case R.id.img2:
                if (img2clicked) {

                } else {
                    img2clicked = true;
                    current = img2;
                    selectImage();
                }
                break;
            case R.id.img3:
                if (img3clicked) {

                } else {
                    img3clicked = true;
                    current = img3;
                    selectImage();
                }
                break;
            case R.id.img4:
                if (img4clicked) {

                } else {
                    img4clicked = true;
                    current = img4;
                    selectImage();
                }
                break;
            case R.id.img5:
                if (img5clicked) {

                } else {
                    img5clicked = true;
                    current = img5;
                    selectImage();
                }
                break;
            case R.id.img6:
                if (img6clicked) {

                } else {
                    img6clicked = true;
                    current = img6;
                    selectImage();
                }
                break;
        }
    }


    private void selectImage() {

        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Photo!");
        builder.setCancelable(false);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo"))
                    openCameraIntent();
                else if (options[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });

        builder.show();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            if (requestCode == 1) {
                Glide.with(this).load(imageFilePath).into(current);
                uploadWithTransferUtility(imageFilePath);
                updateImageCounterLocalAndAtFirestore();
            } else if (requestCode == 2) {
                Uri selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = getActivity().getContentResolver().query(selectedImage, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                Log.w("TAG", "path of image from gallery......******************........." + picturePath + "");
                Glide.with(this).load(picturePath).into(current);
                uploadWithTransferUtility(picturePath);
                updateImageCounterLocalAndAtFirestore();
            }
        }
    }


    public void uploadWithTransferUtility(String filepath) {


        BasicAWSCredentials credentials = new BasicAWSCredentials(KEY, SECRET);
        AmazonS3Client s3Client = new AmazonS3Client(credentials);

        TransferUtility transferUtility =
                TransferUtility.builder()
                        .context(getActivity().getApplicationContext())
                        .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                        .s3Client(s3Client)
                        .build();
        int couchcounter = sharedpreferences.getInt(COUCHCOUNTER_KEY, 0);
        //Toast.makeText(getActivity(), filepath, Toast.LENGTH_LONG).show();
        TransferObserver uploadObserver =
                transferUtility.upload(
                        COUCHFOLDER + UID + "/" + (current_couch_id_counter + 1) + "/" + current_couch_images_counter + EXT,
                        new File(filepath));

        // Attach a listener to the observer to get state update and progress notifications
        uploadObserver.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                if (TransferState.COMPLETED == state) {
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

    String imageFilePath;

    private File createImageFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir =
                getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        imageFilePath = image.getAbsolutePath();
        return image;
    }

    private void openCameraIntent() {
        Intent pictureIntent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        if (pictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            //Create a file to store the image
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(), "com.couchsurf.bhargav.couchsurfing.provider", photoFile);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        photoURI);
                startActivityForResult(pictureIntent,
                        1);
            }
        }
    }
}

