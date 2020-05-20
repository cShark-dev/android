package com.example.self;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

import model.Journal;
import util.JournalApi;

public class MainActivity extends AppCompatActivity {
    private Button getStartedButton;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users"); //We want to go and check if there is a user logged in.


    @Override           //A method in both parent and child class which has the same name
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getStartedButton = findViewById(R.id.startButton);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {                                     //Create an Authstatelistener,  new firebase authstate listener,

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();
                if (currentUser != null)                  {
                 currentUser = firebaseAuth.getCurrentUser();
                 final String currentUserId = currentUser.getUid();                      //Create a string to store our current user ID, we need this to add to our journalAPI which GOVERNS the entire application
                    collectionReference                                                 //INVOKE our collectionreference,  go and TRY to find in our firestore in our storage reference, find someone that has the current User
                        .whereEqualTo("userId", currentUserId)
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {

                            @Override
                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                if (e != null) {
                                    return;
                                }
                                String name;

                                if (!queryDocumentSnapshots.isEmpty()) {                //if NOT empty
                                    for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots)    {
                                        JournalApi journalApi = JournalApi.getInstance();                   //THEN query through
                                        journalApi.setUserId(snapshot.getString("userId"));             //All these fields are the same as in our database, reference collection
                                        journalApi.setUsername(snapshot.getString("username"));
                                        startActivity(new Intent(MainActivity.this,
                                                JournalListActivity.class));                  //, COMING from MainActivity and going to LIST, We can take users to where they can add an item or to the list, from the list they can click the plus icon and do whatever they want
                                                finish();                                  //Add finish() to stop user from coming BACK to main activity EVER if they hit the back button,
                                    }
                                }
                            }
                        });                                                      //We are listening.. for new event.. create new eventlistener
                }

                else {

                }
            }
        };


        getStartedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //we go to LoginActivity
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
                finish(); //Get rid of this activity as user moves forward
            }
        });
    }


    @Override                                               //
    protected void onStart() {
        super.onStart();
        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (firebaseAuth != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}
