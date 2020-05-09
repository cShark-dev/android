package com.example.self;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import util.JournalApi;

public class CreateAccountActivity extends AppCompatActivity {

    private Button loginButton;
    private Button createAcctButton;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;   //We want to be able to fetch current user that is logged in so we can do things later


    //Firestore connection
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference collectionReference = db.collection("Users");       //Name our collection path, "Users"

    private EditText emailEditText;
    private EditText passwordEditText;
    private ProgressBar progressBar;
    private EditText userNameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        //Authentication
        firebaseAuth = FirebaseAuth.getInstance();


        //All our items widgets
        createAcctButton = findViewById(R.id.create_acct_button);
        progressBar = findViewById(R.id.create_acct_progress);
        emailEditText = findViewById(R.id.email_account);
        passwordEditText = findViewById(R.id.password_account);
        userNameEditText = findViewById(R.id.username_account);

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();

                if (currentUser != null) {
                    //user is already logged in
                } else {
                    //no user yet
                }
            }
        };

        createAcctButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Call the method, create an if statement first that checks if we have data

                if (!TextUtils.isEmpty(emailEditText.getText().toString())
                && !TextUtils.isEmpty(passwordEditText.getText().toString())
                && !TextUtils.isEmpty(userNameEditText.getText().toString())) {

                    String email = emailEditText.getText().toString().trim();
                    String password = passwordEditText.getText().toString().trim();
                    String username = userNameEditText.getText().toString().trim();

                    createUserEmailAccount(email, password, username);


                } else  {
                    Toast.makeText(CreateAccountActivity.this,
                            "Empty Fields Not Allowed",
                            Toast.LENGTH_LONG)
                            .show();
                }





            }
        });
    }

    private void createUserEmailAccount(String email, String password, final String username) {
        if (!TextUtils.isEmpty(email)
        && !TextUtils.isEmpty(password)
        && !TextUtils.isEmpty(username)) {

            progressBar.setVisibility(View.VISIBLE);   //Show progress when all OK

            firebaseAuth.createUserWithEmailAndPassword(email, password)

               .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                   @Override
                   public void onComplete(@NonNull Task<AuthResult> task) {                 //The task will have the user that we created
                        if (task.isSuccessful()) {
                           currentUser = firebaseAuth.getCurrentUser();                                              //we take user to AddJournalActivity ... when we create our user, when we create our account (authenticated) we want to create in our Database a collection of users to keep track of users we are adding
                            final String currentUserId = currentUser.getUid();

                            //Create a user Map so we can create a user in the User collection
                            Map<String, String> userObj = new HashMap<>();
                            userObj.put("userId", currentUserId);                   //Construct a user object, the key is "userId" the actual value pass currentUserId
                            userObj.put("username", username);                      //Pass a username that we are passing in our method,

                            //save to our firestore database, we need to pass our userobject here to our collection

                            collectionReference.add(userObj)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {            //The moment we pass our collection, we have the reference store document, at this point we need to do another nesting
                                            documentReference.get()
                                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {             //We have created our authenticated user, once we created our authenticated user, we went ahead and created our user object, and now we are prepping so we can take this user and allow this user to add one journal entry
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {      //The document object,
                                                            if (Objects.requireNonNull(task.getResult()).exists()) {
                                                                progressBar.setVisibility(View.INVISIBLE);
                                                                String name = task.getResult()
                                                                        .getString("username");

                                                                JournalApi journalApi = JournalApi.getInstance(); //Global API

                                                                journalApi.setUserId(currentUserId);
                                                                journalApi.setUsername(name);

                                                                Intent intent = new Intent(CreateAccountActivity.this,
                                                                        PostJournalActivity.class);
                                                                intent.putExtra("username", name);
                                                                intent.putExtra("userId", currentUserId);
                                                                startActivity(intent);


                                                            } else {

                                                            }


                                                        }
                                                    });

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });

                        } else {                    //If the result we get from our DocumentSnapshop is null, doesnt exit,  get then rid of the progressbar
                            progressBar.setVisibility(View.INVISIBLE);
                        }


                   }
               })                                             //Add a listener, when this happens we need to make sure what we want happens
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });


                //If they are NOT empty then do something...
        } else {


        }
    }

    //This is where we get the current user, on start we want to make sure that all of the FB stuff is compelte
    @Override
    protected void onStart() {
        super.onStart();

        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);   //Constantly listening for changes within our Firebase authorization


    }
}
