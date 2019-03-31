package com.example.laila.miniprojet;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class Navigation_Drawer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    String photo ;
    String   nom;
    ImageView image;
    TextView nomuser;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation__drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        db = FirebaseFirestore.getInstance();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
        View parentView = navigationView.getHeaderView(0);
        image= (ImageView) parentView.findViewById(R.id.profile_image);
        nomuser=parentView.findViewById(R.id.NomUser);
        Bundle b = getIntent().getExtras();
        if(b != null){

            photo = b.getString("photo");
            nom = b.getString("nom");
           // Toast.makeText(this, "photo "+photo+" nom "+nom, Toast.LENGTH_SHORT).show();
            Log.v("photoxxxxxxxxxxxxxxxxxx", " photooo "+photo);

        }
        nomuser.setText(nom);
        Picasso.with(this).load(photo+"?type=large").into(image);
        final DocumentReference docRef = db.collection("Location").document();
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("tag", "Listen failed.", e);
                    Toast.makeText(Navigation_Drawer.this, "listen failed", Toast.LENGTH_SHORT).show();

                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d("tag", "Current data: " + snapshot.getData());
                    Toast.makeText(Navigation_Drawer.this, "Current data: " + snapshot.getData(), Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("tag", "Current data: null");
                }
            }
        });


//        Thread thread = new Thread() {
//            @Override
//            public void run() {
//                try {
//                    while(true) {
//                        sleep(1000);
//                        db.collection("Location")
//
//                                .addSnapshotListener(new EventListener<QuerySnapshot>() {
//                                    @Override
//                                    public void onEvent(@Nullable QuerySnapshot snapshots,
//                                                        @Nullable FirebaseFirestoreException e) {
//                                        if (e != null) {
//                                            Log.w("tag", "listen:error", e);
//                                            return;
//                                        }
//
//                                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
//                                            switch (dc.getType()) {
//                                                case ADDED:
//                                                    Log.d("tag", "New city: " + dc.getDocument().getData());
//                                                    Toast.makeText(Navigation_Drawer.this, "New city: " + dc.getDocument().getData(), Toast.LENGTH_SHORT).show();
//                                                   Notification();
//                                                    break;
//                                            }
//                                        }
//
//                                    }
//                                });
//                    }
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//
//        thread.start();




    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation__drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment fragment=null;
        if (id == R.id.nav_add) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_containerr,new AddLocation()).commit();

            fragment= new AddLocation();

            //Intent homepage = new Intent(Navigation_Drawer.this, Test.class);
            //startActivity(homepage);

        } else if (id == R.id.nav_view) {

          //  Intent homepage = new Intent(Navigation_Drawer.this, AllLocation.class);
            Intent homepage = new Intent(Navigation_Drawer.this, Main2Activity.class);
            startActivity(homepage);
        } else if (id == R.id.nav_search) {

        } else if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();

            Intent homepage = new Intent(Navigation_Drawer.this, MainActivity.class);
            startActivity(homepage);

        }

        if(fragment!=null){

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.fragment_containerr,fragment);
            ft.commit();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void  Notification(){
        Uri alarmSound= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        int notifyID=1;
        String CHANNEL_ID="my_channel_01";
        int importance= NotificationManager.IMPORTANCE_HIGH;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle("titree")
                .setContentText("contenuuu")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
    }

    public  void OpenAddLocation(){
        Fragment fragment=null;

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_containerr,new AddLocation()).commit();

        fragment= new AddLocation();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.fragment_containerr,fragment);
        ft.commit();
    }
}
