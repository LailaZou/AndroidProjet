package com.example.laila.miniprojet;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.multidex.MultiDexApplication;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity implements com.google.android.gms.maps.OnMapReadyCallback , View.OnClickListener ,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, ResultCallback,
        GoogleMap.OnMarkerClickListener
{

    private com.google.android.gms.maps.GoogleMap mMap;
    private ArrayList<Location> locations;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private android.location.Location lastLocation;
    private Marker currentUserPosition;
    private static final int Request_User_Loaction_Code = 99;
    private FloatingActionButton filter;
    private FloatingActionButton AddNewLocation;
    private RatingBar rateBarMax , rateBarMin;

    int minRate=0;
    int maxRate=5;
    ArrayList<String> tags = new ArrayList<String>();

    int REQUEST_CHECK_SETTINGS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        filter = (FloatingActionButton) findViewById(R.id.filter);
        AddNewLocation=findViewById(R.id.fab);
        AddNewLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.container, new AddLocation());
                ft.commit();

            }
        });
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(Main2Activity.this);
                mBuilder.setTitle("Filter les résultats");
                View mView = getLayoutInflater().inflate(R.layout.filter_form, null);
                final EditText input = (EditText) mView.findViewById(R.id.input);
                String tagsSelected = "";
                if (tags.size()> 0) for(int i = 0 ; i < tags.size() ; i++) tagsSelected += tags.get(i) + " ; ";

                input.setText(tagsSelected);

                rateBarMax = (RatingBar) mView.findViewById(R.id.ratingBarMax);
                rateBarMin = (RatingBar) mView.findViewById(R.id.ratingBarMin);

                rateBarMin.setNumStars(3);
                rateBarMax.setNumStars(3);

                rateBarMax.setStepSize(1);
                rateBarMin.setStepSize(1);

                rateBarMax.setRating(maxRate);
                rateBarMin.setRating(minRate);

                input.setKeyListener(null);

                mBuilder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        minRate = (int)rateBarMin.getRating();
                        maxRate = (int)rateBarMax.getRating();
                        Toast.makeText(Main2Activity.this , minRate+" > "+maxRate , Toast.LENGTH_LONG).show();
                        refreshMap();
                        dialogInterface.dismiss();
                    }
                });

                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();
                input.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String[] listItems = getAllTags();
                        final boolean[] checkedItems = new boolean[listItems.length];

                        AlertDialog.Builder mmBuilder = new AlertDialog.Builder(Main2Activity.this);
                        mmBuilder.setTitle("Liste des tags");
                        mmBuilder.setMultiChoiceItems(listItems, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                                //  Toast.makeText(Main2Activity.this, "ch", Toast.LENGTH_LONG).show();
                            }
                        });

                        mmBuilder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                String text = "";
                                tags.removeAll(tags);
                                for (int i = 0; i < checkedItems.length; i++) {
                                    if(checkedItems[i]){
                                        text =  text + listItems[i]+" " ;
                                        tags.add(listItems[i]);
                                    }
                                }
                                input.setText(text);
                                dialogInterface.dismiss();
                            }
                        });

                        mmBuilder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });

                        mmBuilder.setNeutralButton("Clear all", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                for (int i = 0; i < checkedItems.length; i++) {
                                    checkedItems[i] = false;
                                    tags.removeAll(tags);
                                }
                                dialogInterface.dismiss();
                            }
                        });

                        final AlertDialog dialog2 = mmBuilder.create();
                        dialog2.show();

                    }
                });
            }
        });



        checkUserLocationPermission();
        GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            Toast.makeText(this , "Activez le GPS pour repérer votre position actuelle ", Toast.LENGTH_LONG).show();
        }

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(ContextCompat.checkSelfPermission(this , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            BuildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
            /*View locationButton = ((View) findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            // position on right bottom
            rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
            rlp.setMargins(0, 180, 180, 0);*/
        }
        mMap.setMyLocationEnabled(true);

        mMap.setOnMarkerClickListener(this);

        final FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        db.collection("Location")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                //Log.d( "tt", "Size while retriving "+ document.getData() );
                                String nom = document.getString("nom");
                                long lat = document.getLong("latitude");
                                long lon = document.getLong("longitude");
                                String description = document.getString("description");
                                String point_interet = document.getString("point_interet");
                                String Photo = document.getString("Photo");

                                Log.d("tt" , document.getDouble("latitude")+"  "+document.getDouble("longitude") );
                                LatLng pos = new LatLng( document.getDouble("latitude") , document.getDouble("longitude") );
                                Marker marker = mMap.addMarker(new MarkerOptions().position(pos).title("new"));
                                marker.setTag(pos);

                                final ArrayList<Comments> comments = new ArrayList<>();
                                final ArrayList<Integer> rates = new ArrayList<>();

                                String id = document.getId();
                                db.collection("Location/"+id+"/commentaire")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (DocumentSnapshot document : task.getResult()) {
                                                        //Log.d( "tt", "Size while retriving "+ document.getData() );
                                                        comments.add(new Comments(document.getString("auteur"), document.getString("contenu"), document.getString("image")));
                                                    }
                                                }
                                            }
                                        });
                                db.collection("Location/"+id+"/rate")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (DocumentSnapshot document : task.getResult()) {
                                                        //Log.d( "tt", "Size while retriving "+ document.getData() );
                                                        long n = document.getLong("valeur");
                                                        rates.add( (int) n );
                                                    }
                                                }
                                            }
                                        });

                                Location l = new Location( document.getId() , Photo, description,  lat,  lon, nom, point_interet , rates , comments);
                                marker.setTag(l);



                            }
                        } else {
                            Log.w( "tt", "Error getting documents.", task.getException());
                        }
                    }
                });

        mMap.moveCamera(CameraUpdateFactory.newLatLng( new LatLng(32.3008, 9.2272)) );
    }



    @Override
    public void onClick(View v){
    }

    public boolean checkUserLocationPermission(){
        if(ContextCompat.checkSelfPermission(this , Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this , Manifest.permission.ACCESS_FINE_LOCATION )){
                ActivityCompat.requestPermissions(this , new String[]{Manifest.permission.ACCESS_FINE_LOCATION} , Request_User_Loaction_Code);
            }
            else {
                ActivityCompat.requestPermissions(this , new String[]{Manifest.permission.ACCESS_FINE_LOCATION} , Request_User_Loaction_Code);
            }
            return false;
        }
        else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case Request_User_Loaction_Code:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(this , Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                        if(googleApiClient == null){
                            BuildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                }
                else{
                    Toast.makeText(this , "Permission denied" , Toast.LENGTH_LONG).show();
                }
                return;
        }
    }

    protected synchronized void BuildGoogleApiClient(){
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(11000);
        locationRequest.setFastestInterval(11000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if(ContextCompat.checkSelfPermission(this , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,locationRequest,this);
        }

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        PendingResult result =
                LocationServices.SettingsApi.checkLocationSettings(
                        googleApiClient,
                        builder.build()
                );

        result.setResultCallback(this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this , "Enable to locate your position" , Toast.LENGTH_LONG);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this , "Enable to locate your position" , Toast.LENGTH_LONG);
    }

    @Override
    public void onLocationChanged(android.location.Location location) {
        lastLocation = location;
        if(currentUserPosition != null){
            currentUserPosition.remove();
        }
        LatLng latLng = new LatLng(location.getLatitude() , location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("User current location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
        currentUserPosition = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(14));

        if(googleApiClient != null){
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient , this);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS) {

            if (resultCode == RESULT_OK) {

                Toast.makeText(getApplicationContext(), "GPS enabled", Toast.LENGTH_LONG).show();
            } else {

                Toast.makeText(getApplicationContext(), "GPS is not enabled", Toast.LENGTH_LONG).show();
            }

        }
    }

    @Override
    public void onResult(@NonNull Result result) {
        final Status status = result.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:

                // NO need to show the dialog;

                break;

            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                //  Location settings are not satisfied. Show the user a dialog

                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().

                    status.startResolutionForResult(Main2Activity.this, REQUEST_CHECK_SETTINGS);

                } catch (Exception e) {

                    //failed to show
                }
                break;

            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                // Location settings are unavailable so not possible to show any dialog now
                break;
        }
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        Location location = (Location) marker.getTag();
        if(location!=null){
            Log.d("location ", "onMarkerClickkkkkkkkk: "+location.getLatitude()+" "+location.getLongitude());
            infoLocation bottomSheet = new infoLocation();
            bottomSheet.setLocation(location);
            bottomSheet.show(getSupportFragmentManager(), "infoLocation");
            return true;
        }
       return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_search,menu);
        MenuItem item = menu.findItem(R.id.menuSearch);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search_place(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }



    private void search_place(final String tagToSearch) {
        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        //LocationListener locationListener = new LocationListener();
        try {
            // locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        } catch (SecurityException e) {

        }

        mMap.clear();
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        db.collection("Location").whereEqualTo("tag", tagToSearch)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d("tt", document.getId() + " => " + document.getData());
                                String tag = document.getString("tag");
                                if (tag.equals(tagToSearch)) {
                                    String titre = document.getString("titre");
                                    GeoPoint latLng = document.getGeoPoint("location");
                                    LatLng pos = new LatLng(latLng.getLatitude(), latLng.getLongitude());
                                    mMap.addMarker(new MarkerOptions().position(pos).title(tag + " : " + titre));

                                }

                            }
                        } else {
                            Log.w("tt", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private String[] getAllTags(){
        String[] tags =  {"AA" , "BB" , "CC"};
        return tags;
    }

    private void refreshMap(){
        Toast.makeText(this, "refresh map" , Toast.LENGTH_LONG).show();
    }

}