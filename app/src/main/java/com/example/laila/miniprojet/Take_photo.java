package com.example.laila.miniprojet;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.media.ExifInterface;


public class Take_photo extends Fragment {

    private Context mContext;

    ImageView imageView;
    File photoFile = null;
    static final int CAPTURE_IMAGE_REQUEST = 1;
    static final int RESULT_OK = 1;
    Uri imageUri;
    private StorageReference mStorageRef;
    String Point_interet;
    String Label;
    String Description;
    TextView lbl_Point_interet;
    TextView lbl_Label;
    TextView lbl_Description;
    GoogleApiClient mGoogleApiClient;
    LocationManager locationManager;
    private Location lastLocation;
    FirebaseFirestore db;
    public int count=0;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    String currentPhotoPath;
    static final int REQUEST_TAKE_PHOTO = 1;

    public static final int IMAGE_CAPTURE_CODE = 3;

    public Take_photo() {
        // Required empty public constructor
    }

    public static Take_photo newInstance(String Label, String Description, String Point_interet) {
        Bundle bundle = new Bundle();
        bundle.putString("Label", Label);
        bundle.putString("Description", Description);
        bundle.putString("Point_interet", Point_interet);

        Take_photo fragment = new Take_photo();
        fragment.setArguments(bundle);

        return fragment;
    }

    private void readBundle(Bundle bundle) {
        if (bundle != null) {
            Label = bundle.getString("Label");
            Description = bundle.getString("Description");
            Point_interet = bundle.getString("Point_interet");
            Log.v("params read ", Label + "" + Description + " " + Point_interet);

        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_take_photo, container, false);
        imageView = v.findViewById(R.id.imageView);
        mStorageRef = FirebaseStorage.getInstance().getReference();

        lbl_Point_interet = v.findViewById(R.id.Point_interet);
        lbl_Label = v.findViewById(R.id.Label);
        lbl_Description = v.findViewById(R.id.Description);

        lbl_Point_interet.setText(getArguments().getString("Point_interet"));
        lbl_Label.setText(getArguments().getString("Label"));
        lbl_Description.setText(getArguments().getString("Description"));

        db = FirebaseFirestore.getInstance();
        Log.d("params on createview ", Label + "" + Description + " " + Point_interet);



        dispatchTakePictureIntent();

       // captureImage();
        //
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);


        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 500.0f, locationListenerGPS);
        isLocationEnabled();


        return v;
    }

    private void captureImage() {
//
//          if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//              ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
//          } else{
//       Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//        startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST);
//
//          }
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {


        lbl_Point_interet.setText(getArguments().getString("Point_interet"));
        lbl_Label.setText(getArguments().getString("Label"));
        lbl_Description.setText(getArguments().getString("Description"));
//        if (requestCode == CAPTURE_IMAGE_REQUEST) {
//            Toast.makeText(mContext, "Showing image ", Toast.LENGTH_LONG).show();
//            Log.d("params", Label + "" + Description + " " + Point_interet);
////            Bundle extras = data.getExtras();
////            Bitmap imageBitmap = (Bitmap) extras.get("data");
////            imageView.setImageBitmap(imageBitmap);
////            Log.d("tag", " bef uploading ............... "+extras.get("data").toString());
//            Picasso.with(mContext).load(uri).into(imageView);
//
//        }


        if (requestCode ==1) {
            final Uri uri =  Uri.fromFile(new File(currentPhotoPath));

            Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
           // imageView.setImageBitmap(bitmap);
            Picasso.with(mContext).load(uri).fit().centerCrop().into(imageView);



            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage("Are you sure you want to save this location")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {


                            Toast.makeText(mContext, "Uploading...", Toast.LENGTH_LONG).show();

                            Toast.makeText(mContext, "uri "+uri, Toast.LENGTH_SHORT).show();
                            Log.d("tag", "uploading ............... "+uri.getLastPathSegment());
                            StorageReference filepath = mStorageRef.child("Photos").child(uri.getLastPathSegment());
                            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    // Toast.makeText(mContext, "uri " + uri.getLastPathSegment(), Toast.LENGTH_LONG).show();
                                    //  GET current location problem
                                    double latitude = 0;
                                    double longitude = 0;
                                    latitude = lastLocation.getLatitude();
                                    longitude = lastLocation.getLongitude();
                                    Toast.makeText(mContext, "location " + latitude + " l " + longitude, Toast.LENGTH_LONG).show();

// Create a new user with a first, middle, and last name

                                    Map<String, Object> loc = new HashMap<>();
                                    //loc.put("id", GetCountCollection("Location")+1);
                                    // loc.put("id", db.collection("Location"));
                                    loc.put("nom", lbl_Label.getText());
                                    loc.put("description", lbl_Description.getText());
                                    loc.put("point_interet", lbl_Point_interet.getText());
                                    loc.put("longitude", lastLocation.getLongitude());
                                    loc.put("latitude", lastLocation.getLatitude());
                                    loc.put("Photo",  uri.getLastPathSegment());
                                    loc.put("date", new Date());
                                    //final Map<String, Object> commentaire = new HashMap<>();
                                    //commentaire.put("test","jfj");
                                    //loc.put("commentaire",commentaire);
                                    // loc.put("Photo", img.getRoot().getPath());
// Add a new document with a generated ID


                                    db.collection("Location")
                                            .add(loc)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    Log.d("tag", "DocumentSnapshot added with ID: " + documentReference.getId());
                                                    Map<String, Object> COM = new HashMap<>();
                                                    COM.put("auteur", "Android user");
                                                    COM.put("contenu", "Very good place");
                                                    //image de android
                                                    COM.put("image", "https://images.frandroid.com/wp-content/uploads/2017/02/bugdroid.png");
                                                    Map<String, Object> RATE = new HashMap<>();
                                                    RATE.put("valeur", 3);


                                                   // db.collection("Location").document().set(commentaire);
                                                    CollectionReference ref = db
                                                            .collection("Location").document(documentReference.getId())
                                                            .collection("commentaire");
                                                    CollectionReference refRate = db
                                                            .collection("Location").document(documentReference.getId())
                                                            .collection("rate");
                                                    ref.add(COM);
                                                    refRate.add(RATE);
                                                    Intent homepage = new Intent(mContext, Main2Activity.class);
                                                    startActivity(homepage);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w("tag", "Error adding document", e);
                                                }
                                            });
                                }
                            });
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();


        }








    }

//   @Override
//   public void onActivityResult(int requestCode, int resultCode, Intent data) {
//       super.onActivityResult(requestCode, resultCode, data);
//       Toast.makeText(mContext, "on activityy result ", Toast.LENGTH_SHORT).show();
//
//            if(requestCode ==1 ){
//                Toast.makeText(mContext, "on activityy result ", Toast.LENGTH_SHORT).show();
//
//
//                //    Uri uri = data.getData();
//                Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
//                imageView.setImageBitmap(bitmap);
//                Toast.makeText(mContext, "URIII "+currentPhotoPath, Toast.LENGTH_SHORT).show();
//                //  Picasso.with(mContext).load(Uri.fromFile(photoFile)).into(imageView);
//
//            }
//
//
//   }
    // TODO: Rename method, update argument and hook method into UI event
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    private void displayMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                captureImage();
            } else {
                displayMessage(getContext(), " This app is not going to work without camera permession !!");
            }
        }

    }


    LocationListener locationListenerGPS = new LocationListener() {
        @Override
        public void onLocationChanged(android.location.Location location) {
            lastLocation = location;
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            String msg = "New Latitude: " + latitude + "New Longitude: " + longitude;
            // Toast.makeText(mContext,msg,Toast.LENGTH_LONG).show();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };


    public void onResume() {
        super.onResume();
       isLocationEnabled();
    }

    private void isLocationEnabled() {

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
            alertDialog.setTitle("Enable Location");
            alertDialog.setMessage("Your locations setting is not enabled. Please enabled it in settings menu.");
            alertDialog.setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog alert = alertDialog.create();
            alert.show();
        }
        else{
            /*AlertDialog.Builder alertDialog=new AlertDialog.Builder(mContext);
            alertDialog.setTitle("Confirm Location");
            alertDialog.setMessage("Your Location is enabled, please enjoy");
            alertDialog.setNegativeButton("Back to interface",new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            AlertDialog alert=alertDialog.create();
            alert.show();*/
            lastLocation=getLastKnownLocation();
        }
    }

    private Location getLastKnownLocation() {
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            }
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    public int GetCountCollection(String col){
        db.collection(col)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                           count= task.getResult().size();
                            Log.d("tag", "count location "+count, task.getException());

                        } else {
                            Log.d("tag", "Error getting documents: ", task.getException());
                        }
                    }
                });
        Toast.makeText(mContext, " before return count " + count, Toast.LENGTH_LONG).show();

        return count;
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
                currentPhotoPath=photoFile.getAbsolutePath();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(mContext,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, 1);
            }
        }
    }
}
