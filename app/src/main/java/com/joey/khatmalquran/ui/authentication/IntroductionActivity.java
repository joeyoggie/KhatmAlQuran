package com.joey.khatmalquran.ui.authentication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.joey.khatmalquran.R;
import com.joey.khatmalquran.data.db.entities.User;
import com.joey.khatmalquran.ui.main.MainActivity;
import com.joey.khatmalquran.utils.CustomProgressDialog;

import java.util.ArrayList;

public class IntroductionActivity extends Activity {

    EditText nameEditText;
    Button continueButton;

    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    DatabaseReference storageReference, configurationReference;

    String latestUserIDString;
    long latestUserIDValue;

    CustomProgressDialog customProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduction);

        nameEditText = (EditText) findViewById(R.id.name_edittext);
        continueButton = (Button) findViewById(R.id.continue_button);

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateName();
            }
        });

        nameEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    validateName();
                    return true;
                }
                return false;
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
    }

    private void validateName(){
        if(nameEditText.getText().toString() != null && nameEditText.getText().toString().length() >= 1){
            uploadUserData();
        }
        else{
            nameEditText.setError("Please enter your name");
            YoYo.with(Techniques.Shake)
                    .duration(700)
                    .repeat(1)
                    .playOn(nameEditText);
        }
    }

    private void uploadUserData(){
        customProgressDialog = CustomProgressDialog.show(this, "", "");
        continueButton.setEnabled(false);
        continueButton.setBackgroundColor(Color.GRAY);
        mAuth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    configurationReference = mDatabase.getReference("root/config/");
                    configurationReference.child("latestUserID").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot != null){
                                latestUserIDString = String.valueOf(dataSnapshot.getValue());
                                if(latestUserIDString != null && latestUserIDString.length() >= 1){
                                    try {
                                        latestUserIDValue = Long.parseLong(latestUserIDString);
                                    }catch (NumberFormatException e){
                                        latestUserIDValue = 0;
                                    }
                                }
                            }
                            else{
                                latestUserIDString = "0";
                                latestUserIDValue = 0;
                            }

                            String fcmToken = FirebaseInstanceId.getInstance().getToken();
                            User user = new User(++latestUserIDValue, nameEditText.getText().toString(), new ArrayList<Integer>(), fcmToken);

                            storageReference = mDatabase.getReference("root/users");
                            storageReference.child(""+user.getId()).setValue(user);

                            configurationReference.child("latestUserID").setValue(user.getId());

                            SharedPreferences prefs = getSharedPreferences("KhatmAlQuran.Login", 0);
                            SharedPreferences.Editor prefsEditor = prefs.edit();
                            prefsEditor.putString("name", nameEditText.getText().toString());
                            prefsEditor.putLong("userID", latestUserIDValue);
                            prefsEditor.apply();
                            Intent mainActivityIntent = new Intent(IntroductionActivity.this, MainActivity.class);
                            startActivity(mainActivityIntent);
                            finish();
                            customProgressDialog.dismiss();
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(IntroductionActivity.this, "Error uploading user data. " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            customProgressDialog.dismiss();
                        }
                    });
                }
                else{
                    customProgressDialog.dismiss();
                    Toast.makeText(IntroductionActivity.this, "Failed to authenticate with server. " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
