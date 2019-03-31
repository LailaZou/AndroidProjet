package com.example.laila.miniprojet;

import android.content.Context;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AddLocation extends Fragment{

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Spinner spinner;//point interet
    Spinner lista;
     List<String> spinnerArray;
     Button next ;
    EditText label;
    EditText description;
    private Context mContext;
    String selected_Point_interet;

    LocationManager locationManager;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_add_location, container, false);
         spinner = v.findViewById(R.id.point_interet);
         label= v.findViewById(R.id.label);
         description= v.findViewById(R.id.description);

         next=v.findViewById(R.id.next);
         spinnerArray = new ArrayList<String>();
         spinner.setPrompt("Selectionner un point d'interet ...");
         ReadData();


        //return inflater.inflate(R.layout.fragment_add_location, container, false);
        spinner.setSelection(1);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinner.setSelection(position);

               String provider = spinner.getSelectedItem().toString();

                 selected_Point_interet = parent.getItemAtPosition(position).toString();


              /*  Toast.makeText(getContext(), "provider  "+provider,
                        Toast.LENGTH_LONG).show();
                Toast.makeText(getContext(), "position  "+position,
                        Toast.LENGTH_LONG).show();*/

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });


    next.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String Label=label.getText().toString();
            String Description=description.getText().toString();
            Log.d("params"," before sending "+Label+"" +Description+" "+selected_Point_interet);

            // Fragment fragment = new Take_photo();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Fragment fragment = Take_photo.newInstance(Label,Description,selected_Point_interet);

            fragmentTransaction.replace(R.id.fragment_containerr, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

        }
    });

        return  v;
    }





    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d("show", "on view created");


    }

    public void ReadData(){
        final List<String> list = new ArrayList<>();
        db.collection("Point_interet")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {

                                //  spinnerArray.add(document.get("nom").toString());
                                list.add(document.get("nom").toString());

                                Log.d("tag", document.getId() + " => " + document.get("contenu"));
                                Log.d("size",  String.valueOf(list.size()));
                            }

                            Log.d("size after on cmp",  String.valueOf(list.size()));
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item,list);
                            Log.d("size adapter", adapter.toString());
                            adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                            //  adapter.setDropDownViewResource(android.R.layout.simple_list_item_activated_1);

                            //  Toast.makeText(getContext(), "size  "+spinnerArray.size(),
                            //      Toast.LENGTH_LONG).show();
                            spinner.setAdapter(adapter);

                        } else {
                            Log.w("tag", "Error getting documents.", task.getException());
                        }
                    }
                });



    }

    // return the current context
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }
}
