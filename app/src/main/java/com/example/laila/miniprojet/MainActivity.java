package com.example.laila.miniprojet;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import Models.User;

public class MainActivity extends AppCompatActivity {



    CallbackManager mcallbackManager;
    private static final String EMAIL = "email";
    LoginButton loginButton;
    TextView text;

    SignInButton signInButton ;// Google sign in button
    GoogleSignInClient mGoogleSignInClient;
    int RC_SIGN_IN;
    String CurrentLogin="Facebook";
    private FirebaseAuth mAuth;
    String photoUrl ="";

    FirebaseFirestore db ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         signInButton = findViewById(R.id.sign_in_button);// google button
        mAuth = FirebaseAuth.getInstance();//google
        db = FirebaseFirestore.getInstance();

        FirebaseMessaging.getInstance().subscribeToTopic("news");
        // Initialize Facebook Login button
        mcallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile");       // LoginManager.getInstance().logOut();
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CurrentLogin="Facebook";

                Log.v("CurrentLogin",CurrentLogin);


            }
        });
        loginButton.registerCallback(mcallbackManager, new FacebookCallback<LoginResult>() {


            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("TAG", "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d("TAG", "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("TAG", "facebook:onError", error);
                // ...
            }
        });






        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CurrentLogin="Google";
                signIn();
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v("resultat","onactivity result "+this.CurrentLogin);

        if(this.CurrentLogin.equals("Facebook")){// si il s'agit d'une cnx par fcb
            mcallbackManager.onActivityResult(requestCode, resultCode, data);

        }else{ // si il s'agit d'une cnx par fcb
            super.onActivityResult(requestCode, resultCode, data);

            // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
            if (requestCode == RC_SIGN_IN) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    firebaseAuthWithGoogle(account);
                } catch (ApiException e) {
                    // Google Sign In failed, update UI appropriately
                    Log.w("tag", "Google sign in failed", e);
                    // ...
                }
            }
       }

    }



    private void signIn() {

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("tag", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("tag", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                           // Authentifacation a reussi pour Google
                            Toast.makeText(getApplicationContext(), "Authentication succeed with Google ",
                                    Toast.LENGTH_LONG).show();

                             photoUrl =  user.getPhotoUrl().toString()+"?type=large";

                            Log.v("photoxxxxxxxxxxxxxxxxxx", " "+photoUrl);
                            GestionUtilisateur(user);

                            Intent homepage = new Intent(MainActivity.this, Navigation_Drawer.class);
                            homepage.putExtra("photo", photoUrl);
                            homepage.putExtra("nom", user.getDisplayName());

                            startActivity(homepage);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("tag", "signInWithCredential:failure", task.getException());

                            Toast.makeText(getApplicationContext(), "Authentication failed with Google ",
                                    Toast.LENGTH_LONG).show();
                            Toast.makeText(getApplicationContext(), "signInWithCredential:failure "+task.getException(),
                                    Toast.LENGTH_LONG).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
      //  updateUI(currentUser);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("TAG", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();


                           // String photoUrl = "https://graph.facebook.com/" + user.getProviderData().get(0).getProviderId() + "/picture?type=large";
                             photoUrl = user.getPhotoUrl().toString();

                            Log.v("photoxxxxxxxxxxxxxxxxxx", " "+photoUrl);

                            GestionUtilisateur(user);

                            // Authentification a reussi pour Facebook
                              Intent homepage = new Intent(MainActivity.this, Navigation_Drawer.class);
                            homepage.putExtra("photo", photoUrl);
                            homepage.putExtra("nom", user.getDisplayName());
                              startActivity(homepage);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public void GestionUtilisateur(final FirebaseUser user ){
           final User utilisateur=new User();

       db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        //Toast.makeText(MainActivity.this, "size "+task.getResult().size(), Toast.LENGTH_SHORT).show();
                        Log.d("tag", "size of task: "+task.getResult().size());
                           if (task.isSuccessful()) {
                                int count=0;
                                for (DocumentSnapshot document : task.getResult()) {

                                    if(document.get("id").toString().equals(user.getUid())){

                                        utilisateur.setid(document.get("id").toString());
                                        utilisateur.setemail(document.get("email").toString());
                                        utilisateur.setnom( document.get("nom").toString());


                                    }else{

                                        if(count+1==task.getResult().size()){
                                         //   Toast.makeText(MainActivity.this, " doesnt exists ", Toast.LENGTH_SHORT).show();
                                            // Create a new user with a first and last name
                                            Map<String, Object> Newuser = new HashMap<>();
                                            Newuser.put("id",user.getUid() );
                                            Newuser.put("nom", user.getDisplayName());
                                            Newuser.put("email", user.getEmail());



// Add a new document with a generated ID
                                            db.collection("users")
                                                    .add(Newuser)
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                        @Override
                                                        public void onSuccess(DocumentReference documentReference) {
                                                            Log.d("tag", "DocumentSnapshot added with ID: " + documentReference.getId());
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w("tag", "Error adding document", e);
                                                        }
                                                    });
                                        }
                                        count++;

                                    }
                                }


                        }
                    }
                });




        }




}
