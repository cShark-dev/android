package com.example.self;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

public class JournalListActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_list);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.action_add:           //Whenever users click the add button we have to take users to the add journal activity, but first we need all firebase authorization, collection
                //Take users to add Journal
                //check first if user is not equal to null and firebase is also not null at this point, only if that is true that is when we start activity new intent coming from journal list and going to our PostJournal class
                if (user != null && firebaseAuth != null) {
                    startActivity(new Intent(JournalListActivity.this,
                            PostJournalActivity.class));
                    //finish();
                }
                break;
            case R.id.action_signout:
                //Sign user out
                if (user != null && firebaseAuth != null) {
                    firebaseAuth.signOut();

                    startActivity(new Intent(JournalListActivity.this,          //the user has been signed out must go back to main activity
                            MainActivity.class));
                    //finish();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {          //This is where we go get all of our journals from fire store
        super.onStart();
    }
}
