package com.joey.khatmalquran;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Joey on 12/2/2017.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("InstanceIDService", "Refreshed token: " + refreshedToken);

        SharedPreferences prefs = getSharedPreferences("KhatmAlQuran.Login", 0);
        long userID = prefs.getLong("userID", -1);
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken, userID);
    }

    private void sendRegistrationToServer(final String token, final long userID){
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference storageReference = mDatabase.getReference("root/users");

        mAuth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    storageReference.child(""+userID).child("fcmToken").setValue(token);
                }
                else{
                    Log.d("InstanceIDService", "Failed to authenticate with server. " + task.getException());
                }
            }
        });
    }

}
