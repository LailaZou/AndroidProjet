package com.example.laila.miniprojet;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class modele {
    FirebaseFirestore db;
    public modele(){
        db = FirebaseFirestore.getInstance();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        firestore.setFirestoreSettings(settings);
        Log.d("tt", "connected to database" );


    }

    public ArrayList<Location> getAll(){
        final ArrayList<Location> results = new ArrayList<>();
        db.collection("locations")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                String nom = document.getString("nom");
                                long lat = document.getLong("latitude");
                                long lon = document.getLong("longitude");
                                String description = document.getString("description");
                                String point_interet = document.getString("point_interet");
                                String Photo = document.getString("Photo");
                                final ArrayList<Comments> comments = new ArrayList<>();
                                final ArrayList<Integer> rates = new ArrayList<>();
                                String id = document.getId();
                                db.collection("locations."+id+".commentaire")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (DocumentSnapshot document : task.getResult()) {
                                                        comments.add(new Comments(document.getString("auteur"), document.getString("contenu"), document.getString("image")));
                                                    }
                                                }
                                            }
                                        });
                                db.collection("locations."+id+".rate")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (DocumentSnapshot document : task.getResult()) {
                                                        rates.add( (int) document.get("rate") );
                                                    }
                                                }
                                            }
                                        });

                                Location l = new Location( document.getId() , Photo, description,  lat,  lon, nom, point_interet , rates , comments);
                                results.add(l);
                            }
                        } else {
                            Log.w( "tt", "Error getting documents.", task.getException());
                        }
                    }
                });
        Log.d( "tt", "Size while retriving "+results.size());
        return results;
    }



}
