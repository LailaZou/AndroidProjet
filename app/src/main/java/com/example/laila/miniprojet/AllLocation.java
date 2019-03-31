package com.example.laila.miniprojet;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AllLocation extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    FirebaseFirestore db ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        db = FirebaseFirestore.getInstance();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        List<LatLng> locations= getLocation();
        Toast.makeText(this, " size "+locations.size(), Toast.LENGTH_SHORT).show();
        for(LatLng loc  :locations){
            mMap.addMarker(new MarkerOptions().position(loc).title("Marker "));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
        }

        db.collection("Location")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {

                                LatLng sydney = new LatLng(document.getDouble("latitude"), document.getDouble("longitude"));
                                mMap.addMarker(new MarkerOptions().position(sydney).title("Marker "));
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));


                            }



                        } else {
                            Log.w("tag", "Error getting documents.", task.getException());
                        }
                    }
                });
        // Add a marker in Sydney and move the camera
       LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker "));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }



    public List<LatLng> getLocation(){
       final List<LatLng> loc= new ArrayList<>() ;
        db.collection("Location")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d("tag", "locationnnnnnnnnnnn: "+document.get("latitude")+" "+document.get("longitude"));
                                //  spinnerArray.add(document.get("nom").toString());
                                loc.add( new LatLng(
                                        (document.getDouble("latitude"))
                                        ,(document.getDouble("longitude"))
                                ));

                            }



                        } else {
                            Log.w("tag", "Error getting documents.", task.getException());
                        }
                    }
                });
        return loc;
    }
}
