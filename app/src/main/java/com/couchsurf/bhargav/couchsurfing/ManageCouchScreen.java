package com.couchsurf.bhargav.couchsurfing;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
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
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static com.couchsurf.bhargav.couchsurfing.SecretKeys.SECRET;

public class ManageCouchScreen extends Fragment implements View.OnClickListener {
    int i, j;
    ProgressBar progressBar;
    ImageView img1, img2, img3, img4, img5, img6;
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


    final private String COUCH_IMAGES_COUNTER_KEY = "No_Of_Images";
    final private String COUCH_NAME_KEY = "Name";
    final private String NO_OF_ROOMS_KEY = "No_Of_Rooms";
    final private String NO_OF_ADULTS_KEY = "No_Of_Adults";
    final private String COUCH_DESC_KEY = "Desc_Of_Couch";
    final private String COUCH_CITY_KEY = "City_Of_Couch";
    final private String COUCH_STATE_KEY = "State_Of_Couch";
    final private String COUCH_COUNTRY_KEY = "Country_Of_Couch";
    final private String TIME_ADDED_KEY = "Time_Of_Posting";
    final private String COUCH_PET_KEY = "Pets_Allowed";
    final private String COUCH_ADD_KEY = "Address_Of_Couch";
    final private String COUCH_ID_KEY = "Couch_Id";
    View v;
    static FirebaseAuth mAuth;
    static FirebaseFirestore db;
    static FirebaseUser firebaseUser;
    static public String UID;
    static SharedPreferences sharedpreferences;
    final static private String INITURL = "https://s3.amazonaws.com/couchsurfing-userfiles-mobilehub-151528593/";
    final static private String COUCHFOLDER = "s3Folder/couchPics/"; //format- "s3Folder/couchPics/UID/CouchId(>0)/ImgId"
    final static private String EXT = ".jpg";
    ImageView current;
    public int current_couch_id;
    public int current_couch_counter; //this represents successfully registered number of couches at the moment, it will be updated when this form is submitted
    public int current_couch_images_counter;
    ImageButton decrooms, incrooms, decadults, incadults;
    TextView roomNumber, adultNumber;
    private RadioGroup radioPetsGroup;
    private RadioButton radioPetButton;
    EditText nameCouch, addressCouch, descCouch;
    Button submitButton, deleteButton;
    ScrollView mainLayout;
    public int selected;
    String imageFilePath;
    TextView cityAndState, timePosted;
    String url1, url2, url3, url4, url5, url6;
    ImageButton nameEditButton, nameDoneButton, addEditButton, addDoneButton, descEditButton, descDoneButton;
    public boolean img1clicked = false, img2clicked = false, img3clicked = false, img4clicked = false, img5clicked = false, img6clicked = false;
    Map<String, Object>[] reAssignMap;int mapcounter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = getLayoutInflater().inflate(R.layout.manage_couch_screen, container, false);
        selected = ManageCouches.selectedCouch;
        progressBar = v.findViewById(R.id.marker_progressReview);
        img1 = v.findViewById(R.id.img1Review);
        img2 = v.findViewById(R.id.img2Review);
        img3 = v.findViewById(R.id.img3Review);
        img4 = v.findViewById(R.id.img4Review);
        img5 = v.findViewById(R.id.img5Review);
        img6 = v.findViewById(R.id.img6Review);
        decrooms = v.findViewById(R.id.decRoomsReview);
        incrooms = v.findViewById(R.id.incRoomsReview);
        decadults = v.findViewById(R.id.decAdultsReview);
        incadults = v.findViewById(R.id.incAdultsReview);
        adultNumber = v.findViewById(R.id.adultNumberReview);
        roomNumber = v.findViewById(R.id.roomNumberReview);
        radioPetsGroup = (RadioGroup) v.findViewById(R.id.radioPetsReview);
        nameCouch = v.findViewById(R.id.nameCouchReview);
        addressCouch = v.findViewById(R.id.addressCouchReview);
        descCouch = v.findViewById(R.id.descCouchReview);
        submitButton = v.findViewById(R.id.submitButtonReview);
        deleteButton = v.findViewById(R.id.deleteButtonReview);
        mainLayout = v.findViewById(R.id.mainLayoutMCS);

        addEditButton = v.findViewById(R.id.addEditButton);
        addDoneButton = v.findViewById(R.id.addDoneButton);
        nameEditButton = v.findViewById(R.id.nameEditButtonReview);
        nameDoneButton = v.findViewById(R.id.nameDoneButtonReview);
        descEditButton = v.findViewById(R.id.descEditButtonReview);
        descDoneButton = v.findViewById(R.id.descDoneButtonReview);
        cityAndState = v.findViewById(R.id.cityAndState);
        timePosted = v.findViewById(R.id.timePosted);
        //PERMISSION CHECK
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
        FirebaseFirestore.getInstance().collection("users").document(UID).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            current_couch_counter = Math.round((Long) doc.get(COUCHCOUNTER_KEY));
                        }
                    }
                });

        //Get number of images and init all fields
        DocumentReference databaseReference = FirebaseFirestore.getInstance().collection("users").document(UID).collection("couches").document(Integer.toString(selected));
        databaseReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    current_couch_id = Integer.parseInt(doc.get(COUCH_ID_KEY).toString());
                   // Toast.makeText(getActivity(),"se is"+current_couch_id,Toast.LENGTH_LONG).show();

                    current_couch_images_counter = Integer.parseInt(doc.get(COUCH_IMAGES_COUNTER_KEY).toString());
                    // Toast.makeText(getActivity(),"got "+current_couch_images_counter,Toast.LENGTH_LONG).show();
                    nameCouch.setText(doc.get(COUCH_NAME_KEY).toString());
                    timePosted.setText(doc.get(TIME_ADDED_KEY).toString());
                    descCouch.setText(doc.get(COUCH_DESC_KEY).toString());
                    roomNumber.setText(doc.get(NO_OF_ROOMS_KEY).toString());
                    adultNumber.setText(doc.get(NO_OF_ADULTS_KEY).toString());
                    addressCouch.setText(doc.get(COUCH_ADD_KEY).toString());
                    if ((Boolean) doc.get(COUCH_PET_KEY)) {
                        radioPetButton = (RadioButton) v.findViewById(R.id.radioYesReview);
                        radioPetButton.setChecked(true);
                    } else {
                        radioPetButton = (RadioButton) v.findViewById(R.id.radioNoReview);
                        radioPetButton.setChecked(true);
                    }
                    String city = doc.get(COUCH_CITY_KEY).toString() + ", " + doc.get(COUCH_STATE_KEY) + ", " + doc.get(COUCH_COUNTRY_KEY).toString();
                    //Toast.makeText(getActivity(), city, Toast.LENGTH_LONG).show();
                    cityAndState.setText(city);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "ERROR retrieving info from server", Toast.LENGTH_LONG).show();

            }
        });

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                initBind();
            }
        }, 2000);
        descCouch.setInputType(InputType.TYPE_NULL);
        nameCouch.setInputType(InputType.TYPE_NULL);
        addressCouch.setInputType(InputType.TYPE_NULL);
        img1.setOnClickListener(this);
        img2.setOnClickListener(this);
        img3.setOnClickListener(this);
        img4.setOnClickListener(this);
        img5.setOnClickListener(this);
        img6.setOnClickListener(this);
        decrooms.setOnClickListener(this);
        incrooms.setOnClickListener(this);
        decadults.setOnClickListener(this);
        incadults.setOnClickListener(this);
        submitButton.setOnClickListener(this);
        deleteButton.setOnClickListener(this);

        nameEditButton.setOnClickListener(this);
        nameDoneButton.setOnClickListener(this);
        descEditButton.setOnClickListener(this);
        descDoneButton.setOnClickListener(this);
        addEditButton.setOnClickListener(this);
        addDoneButton.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.img1Review:
                if (url1 == null) {
                    if (img1clicked) {

                    } else {
                        img1clicked = true;
                        current = img1;
                        selectImage();
                    }
                } else {
                    Intent intent = new Intent(getActivity().getBaseContext(), FullScreenImageView.class);
                    intent.putExtra("CURRENT_IMG", url1);
                    startActivity(intent);

                }
                break;
            case R.id.img2Review:
                if (url2 == null) {
                    if (img2clicked) {

                    } else {
                        img2clicked = true;
                        current = img2;
                        selectImage();
                    }
                } else {
                    Intent intent = new Intent(getActivity().getBaseContext(), FullScreenImageView.class);
                    intent.putExtra("CURRENT_IMG", url2);
                    startActivity(intent);

                }
                break;
            case R.id.img3Review:
                if (url3 == null) {
                    if (img3clicked) {

                    } else {
                        img3clicked = true;
                        current = img3;
                        selectImage();
                    }
                } else {
                    Intent intent = new Intent(getActivity().getBaseContext(), FullScreenImageView.class);
                    intent.putExtra("CURRENT_IMG", url3);
                    startActivity(intent);

                }
                break;
            case R.id.img4Review:
                if (url4 == null) {
                    if (img4clicked) {

                    } else {
                        img4clicked = true;
                        current = img4;
                        selectImage();
                    }
                } else {
                    Intent intent = new Intent(getActivity().getBaseContext(), FullScreenImageView.class);
                    intent.putExtra("CURRENT_IMG", url4);
                    startActivity(intent);
                }
                break;
            case R.id.img5Review:
                if (url5 == null) {
                    if (img5clicked) {

                    } else {
                        img5clicked = true;
                        current = img5;
                        selectImage();
                    }
                } else {
                    Intent intent = new Intent(getActivity().getBaseContext(), FullScreenImageView.class);
                    intent.putExtra("CURRENT_IMG", url5);
                    startActivity(intent);
                }
                break;
            case R.id.img6Review:
                if (url6 == null) {
                    if (img6clicked) {

                    } else {
                        img6clicked = true;
                        current = img6;
                        selectImage();
                    }
                } else {
                    Intent intent = new Intent(getActivity().getBaseContext(), FullScreenImageView.class);
                    intent.putExtra("CURRENT_IMG", url6);
                    startActivity(intent);
                }
                break;
            case R.id.nameEditButton:
                nameCouch.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
            case R.id.descEditButton:
                descCouch.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                descCouch.setLines(8);
                break;
            case R.id.addEditButton:
                addressCouch.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                addressCouch.setLines(4);
                break;
            case R.id.nameDoneButton:
                nameCouch.setInputType(InputType.TYPE_NULL);
                break;
            case R.id.descDoneButton:
                descCouch.setInputType(InputType.TYPE_NULL);
                break;
            case R.id.addDoneButton:
                addressCouch.setInputType(InputType.TYPE_NULL);
                break;

            case R.id.submitButtonReview:
                submitTheCouch();
                Snackbar snackbar = Snackbar.make(mainLayout, "Your couch data has been updated", Snackbar.LENGTH_LONG);
                snackbar.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.fragment_container, new HomeFragment(), "HOME_FRAGMENT").addToBackStack(null).commit();
                    }
                }, 2000);
                break;

            case R.id.deleteButtonReview:
                deleteThisCouch();
                snackbar = Snackbar.make(mainLayout, "Your couch has been deleted", Snackbar.LENGTH_LONG);
                snackbar.show();
                handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.fragment_container, new HomeFragment(), "HOME_FRAGMENT").addToBackStack(null).commit();
                    }
                }, 6000);
                break;
            case R.id.nameEditButtonReview:
                nameCouch.setInputType(InputType.TYPE_CLASS_TEXT);
                nameCouch.requestFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(nameCouch, InputMethodManager.SHOW_IMPLICIT);
                break;
            case R.id.nameDoneButtonReview:
                nameCouch.setInputType(InputType.TYPE_NULL);
                imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);
                break;
            case R.id.addressEditButton:
                addressCouch.setInputType(InputType.TYPE_CLASS_TEXT);
                addressCouch.requestFocus();
                imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(addressCouch, InputMethodManager.SHOW_IMPLICIT);
                break;
            case R.id.addressDoneButton:
                addressCouch.setInputType(InputType.TYPE_NULL);
                imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);
                break;
            case R.id.descEditButtonReview:
                descCouch.setInputType(InputType.TYPE_CLASS_TEXT);
                descCouch.requestFocus();
                imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(descCouch, InputMethodManager.SHOW_IMPLICIT);
                break;
            case R.id.descDoneButtonReview:
                descCouch.setInputType(InputType.TYPE_NULL);
                imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);
                break;
            case R.id.decRoomsReview:
                int num = Integer.parseInt(roomNumber.getText().toString());
                if (num == 1) {
                } else {
                    num--;
                    roomNumber.setText(Integer.toString(num));
                }
                break;
            case R.id.incRoomsReview:
                num = Integer.parseInt(roomNumber.getText().toString());
                num++;
                roomNumber.setText(Integer.toString(num));
                break;

            case R.id.decAdultsReview:
                num = Integer.parseInt(adultNumber.getText().toString());
                if (num == 1) {
                } else {
                    num--;
                    adultNumber.setText(Integer.toString(num));
                }
                break;
            case R.id.incAdultsReview:
                num = Integer.parseInt(adultNumber.getText().toString());
                num++;
                adultNumber.setText(Integer.toString(num));
                break;

        }
    }


    public void initBind() {

        ImageView currentIV;
        RequestOptions options = new RequestOptions().signature(new ObjectKey(System.currentTimeMillis()));
        for (int i = 0; i < current_couch_images_counter; i++) {
            Toast.makeText(getActivity(),"selected is" +current_couch_id,Toast.LENGTH_LONG).show();
            String link = INITURL + COUCHFOLDER + UID + "/" + Integer.toString(current_couch_id) + "/" + Integer.toString(i) + ".jpg";
            switch (i) {
                case 0:
                    currentIV = img1;
                    url1 = link;
                    break;
                case 1:
                    currentIV = img2;
                    url2 = link;
                    break;
                case 2:
                    currentIV = img3;
                    url3 = link;
                    break;
                case 3:
                    currentIV = img4;
                    url4 = link;
                    break;
                case 4:
                    currentIV = img5;
                    url5 = link;
                    break;
                case 5:
                    currentIV = img6;
                    url6 = link;
                    break;
                default:
                    currentIV = null;
                    break;
            }
           // nameCouch.setText(link);
            // Toast.makeText(getActivity(), link, Toast.LENGTH_LONG).show();
            //nameCouch.setText(link);
            Glide.with(getActivity().getBaseContext()).load(link).thumbnail(0.5f).into(currentIV);

        }
    }


    public void updateImageCounterLocalAndAtFirestore() {
        current_couch_images_counter += 1;

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

    public void uploadWithTransferUtility(String filepath) {


        BasicAWSCredentials credentials = new BasicAWSCredentials(SecretKeys.KEY, SecretKeys.SECRET);
        AmazonS3Client s3Client = new AmazonS3Client(credentials);

        TransferUtility transferUtility =
                TransferUtility.builder()
                        .context(getActivity().getApplicationContext())
                        .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                        .s3Client(s3Client)
                        .build();
       // int couchcounter = sharedpreferences.getInt(COUCHCOUNTER_KEY, 0);
        //Toast.makeText(getActivity(), COUCHFOLDER + UID + "/" + selected + "/" + current_couch_images_counter + EXT, Toast.LENGTH_LONG).show();
        TransferObserver uploadObserver =
                transferUtility.upload(
                        COUCHFOLDER + UID + "/" + current_couch_id + "/" + current_couch_images_counter + EXT,
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


    public void submitTheCouch() {

        int selectedId = radioPetsGroup.getCheckedRadioButtonId();
        radioPetButton = (RadioButton) v.findViewById(selectedId);
        boolean pet = false;
        if (radioPetButton.getText().toString().equals("YES"))
            pet = true;

        String couchName = nameCouch.getText().toString();
        String couchDesc = descCouch.getText().toString();
        String couchAdd = addressCouch.getText().toString();
        int rooms = Integer.parseInt(roomNumber.getText().toString());
        int adults = Integer.parseInt(adultNumber.getText().toString());
        //Toast.makeText(getActivity(),"slected is "+selected, Toast.LENGTH_LONG).show();
        DocumentReference documentReference = db.collection("users").document(UID).collection("couches").document(Integer.toString(selected));
        documentReference.update(COUCH_NAME_KEY, couchName);
        documentReference.update(COUCH_ADD_KEY, couchAdd);
        documentReference.update(COUCH_DESC_KEY, couchDesc);
        documentReference.update(NO_OF_ADULTS_KEY, adults);
        documentReference.update(NO_OF_ROOMS_KEY, rooms);
        documentReference.update(COUCH_PET_KEY, pet);
        documentReference.update(COUCH_IMAGES_COUNTER_KEY,current_couch_images_counter);
    }

    public void deleteThisCouch() {
        progressBar.setVisibility(View.VISIBLE);
        db = FirebaseFirestore.getInstance();
        if (current_couch_counter == 1) {
            Toast.makeText(getActivity(), "Asexpcted, IF", Toast.LENGTH_LONG).show();

            db.collection("users").document(UID).collection("couches").document(Integer.toString(1)).delete();
            db.collection("users").document(UID).update(COUCHCOUNTER_KEY, 0);

        } else if (selected == current_couch_counter) {
            Toast.makeText(getActivity(), "Asexpcted, ELSE IF", Toast.LENGTH_LONG).show();
            db.collection("users").document(UID).collection("couches").document(Integer.toString(current_couch_counter)).delete();
            db.collection("users").document(UID).update(COUCHCOUNTER_KEY, --current_couch_counter);

        } else {

            reAssignMap = new Map[current_couch_counter-selected];
            mapcounter = 0;
            //Toast.makeText(getActivity(), "selected is" + selected, Toast.LENGTH_LONG).show();
            //Toast.makeText(getActivity(), "cc is" + current_couch_counter, Toast.LENGTH_LONG).show();
            for (i = selected + 1; i <= current_couch_counter; i++) {

                db.collection("users").document(UID).collection("couches").document(Integer.toString(i)).get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot doc = task.getResult();

                                    reAssignMap[mapcounter] = doc.getData();
                                    reAssignMap[mapcounter].put("id",doc.getId());
                                    mapcounter++;
                                    //Toast.makeText(getActivity(),"cc is"+current_couch_counter,Toast.LENGTH_LONG).show();
                                }


                            }
                        });


            }
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    db.collection("users").document(UID).collection("couches").document(Integer.toString(i - 1)).delete();
                    db.collection("users").document(UID).update(COUCHCOUNTER_KEY, current_couch_counter-1);
                }
            }, 4000);
            Handler handler2 = new Handler();
            handler2.postDelayed(new Runnable() {
                @Override
                public void run() {
                    reAssignDocuments(reAssignMap, current_couch_counter-selected);
                }
            }, 6000);



        }
    }
    //What we are doing is....IF we are deleting couch at id=2 (selected=2) then we move the couch at id=3 to id=2 and so on until we reach the couch with id=current_couch_counter and then we delete that last couch....just like a bubble sort
    public void reAssignDocuments(Map<String, Object>[] map, int noOfRearrangements) {
        int current = selected;
        //current=1
        //re = 2
        //Toast.makeText(getActivity(),"no of rearrange"+noOfRearrangements,Toast.LENGTH_LONG).show();
        for(int i = 0; i<noOfRearrangements; i++){
            int j = 0;
            while(Integer.parseInt(map[j].get("id").toString())!=current+1){
                if(j<noOfRearrangements) {
                    j++;
                }
            }
            db.collection("users").document(UID).collection("couches").document(Integer.toString(current)).set(map[j]);
            current++;
        }
    }
}
