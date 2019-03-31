package com.example.laila.miniprojet;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class infoLocation extends BottomSheetDialogFragment {
    Location location;
    ImageView image;
    private FirebaseAuth mAuth;

    public void setLocation(Location location){
        this.location = location;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.info_location, container, false);
        TextView loc = (TextView) v.findViewById(R.id.location);
        TextView desc = (TextView) v.findViewById(R.id.desc);
        TextView tag = (TextView) v.findViewById(R.id.tag);
        RatingBar rateBar = v.findViewById(R.id.ratingBar);
        image= (ImageView) v.findViewById(R.id.imgAuth);
        rateBar.setRating(location.getRate());
        loc.setText(location.nom);
        desc.setText(location.description);
        tag.setText(location.point_interet+" : ");

        mAuth = FirebaseAuth.getInstance();

        Log.d("url ", " urrrrrllllllll "+location.getPhoto());
       // StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(location.getPhoto());
       // StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(location.getPhoto());
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
     //   Log.d("photo", "photoooooooo : "+storageReference.getDownloadUrl().getResult());
        // Load the image using Glide
        StorageReference pathRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://androidprojet-26228.appspot.com/Photos/"+location.getPhoto());
        pathRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d("photo", "succed : "+uri);

               Picasso.with(getActivity()).load(uri).fit().centerCrop().into(image);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
      //  Picasso.with(getActivity()).load(storageReference).into(image);
//        Glide.with(this)
//                .load(storageReference.getDownloadUrl().getResult())
//                .into(image);
        RecyclerView mRecyclerView = v.findViewById(R.id.recyclerView);
        //ImageButton addComment = v.findViewById(R.id.rate_button);
        //ImageButton addRate = v.findViewById(R.id.comment_button);
 Button addComment = v.findViewById(R.id.rate_button);
        Button addRate = v.findViewById(R.id.comment_button);

        addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
                mBuilder.setTitle("Ajouter votre commentaire");
                final View mView = getLayoutInflater().inflate(R.layout.add_comment, null);
                mBuilder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        EditText commentaireSaisi = mView.findViewById(R.id.editText3);
                        String commSaisi = commentaireSaisi.getText().toString();
                        saveComent(commSaisi);
                        Toast.makeText(getContext() , "ok"+commSaisi , Toast.LENGTH_LONG).show();
                        dialogInterface.dismiss();
                    }
                });
                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();
            }
        });

        addRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
                mBuilder.setTitle("Evaluer cet endroit");
                final View mView = getLayoutInflater().inflate(R.layout.add_rate, null);
                mBuilder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        RatingBar rateBar = mView.findViewById(R.id.ratingBar2);
                        int rate =(int) rateBar.getRating();
                        saveRate(rate);
                        Toast.makeText(getContext() , "ok"+rate , Toast.LENGTH_LONG).show();
                        dialogInterface.dismiss();
                    }
                });
                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();
            }
        });
        //test
        //saveComent("tessst comment ");

        mRecyclerView.setHasFixedSize(true);
        ArrayList<Comments> exampleList = location.commentaires;

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        RecyclerView.Adapter mAdapter = new CommentaireAdapter(exampleList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        return v;

    }

    private void saveRate(int rate) {
        final FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        String id = location.id;
        Map<String, Object> rating = new HashMap<>();
        rating.put("valeur", rate);


        // Add a new document with a generated ID
        db.collection("Location/"+id+"/commentaire")
                .add(rating)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("tt", "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("tt", "Error adding document", e);
                    }
                });
    }

    private void saveComent(String commSaisi) {
        final FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        FirebaseUser user= mAuth.getCurrentUser();
        Log.d("user", " useeer : "+user.getDisplayName());
        String id = location.id;
        Map<String, Object> comment = new HashMap<>();
        comment.put("auteur", user.getDisplayName());
        comment.put("contenu", commSaisi);
        comment.put("image", user.getPhotoUrl().toString());

        // Add a new document with a generated ID
        db.collection("Location/"+id+"/commentaire")
                .add(comment)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                        Log.d("tt", "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("tt", "Error adding document", e);
                    }
                });

    }



}
