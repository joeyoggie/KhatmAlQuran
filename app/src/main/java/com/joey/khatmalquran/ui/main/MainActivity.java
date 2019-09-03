package com.joey.khatmalquran.ui.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.app.ActivityCompat;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.joey.khatmalquran.R;
import com.joey.khatmalquran.data.db.entities.Group;
import com.joey.khatmalquran.data.db.entities.Part;
import com.joey.khatmalquran.ui.authentication.IntroductionActivity;
import com.joey.khatmalquran.utils.CustomProgressDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class MainActivity extends Activity {

    private final static int RC_PERMISSION_WRITE_EXTERNAL_STORAGE = 101;

    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    DatabaseReference storageReference, configurationReference;

    TextView noGroupsTextView;
    ListView groupsListView;
    List<Group> groups;
    GroupsListAdapter groupsAdapter;

    String latestGroupIDString;
    long latestGroupIDValue;

    SharedPreferences prefs;
    long userID;

    //boolean flag to know if main FAB is in open or closed state.
    private boolean fabExpanded = false;
    private FloatingActionButton groupsFabButton;

    //Linear layout holding the Edit submenu
    private LinearLayout joinGroupFabMenu;
    private LinearLayout createGroupFabMenu;

    CustomProgressDialog customProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //check if user saved his name or not
        prefs = getSharedPreferences("KhatmAlQuran.Login", 0);
        if(prefs.getString("name", "def") == null || prefs.getString("name", "def").equals("def")){
            Intent intent = new Intent(this, IntroductionActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        userID = prefs.getLong("userID", 0);

        customProgressDialog = CustomProgressDialog.show(this, "", "");

        checkPermissions();

        //init views
        noGroupsTextView = (TextView) findViewById(R.id.no_groups_textview);
        groupsListView = (ListView) findViewById(R.id.groups_listview);
        groups = new ArrayList<>();
        groupsAdapter = new GroupsListAdapter(this, groups);
        groupsListView.setAdapter(groupsAdapter);

        joinGroupFabMenu = (LinearLayout) findViewById(R.id.join_group_fab_layout);
        createGroupFabMenu = (LinearLayout) findViewById(R.id.create_group_fab_layout);

        //When main Fab (Groups) is clicked, it expands if not expanded already.
        //Collapses if main FAB was open already.
        //This gives FAB (Groups) open/close behavior
        groupsFabButton = (FloatingActionButton) findViewById(R.id.groups_fab_button);
        groupsFabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fabExpanded == true){
                    closeSubMenusFab();
                } else {
                    openSubMenusFab();
                }
            }
        });

        FloatingActionButton createGroupFabButton = (FloatingActionButton) findViewById(R.id.create_group_fab_button);
        createGroupFabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeSubMenusFab();
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Create new group");
                builder.setMessage("Do you want to create a new group? ");
                final EditText input = new EditText(MainActivity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                //input.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setView(input);
                builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(input.getText().toString() != null && input.getText().toString().length() >= 1) {
                            //add group to database then show the group unique ID
                            addGroupToDatabase(input.getText().toString());
                        }
                        else{
                            Toast.makeText(MainActivity.this, "Please enter a group name", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.show();
            }
        });

        FloatingActionButton joinGroupFabButton = (FloatingActionButton) findViewById(R.id.join_group_fab_button);
        joinGroupFabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeSubMenusFab();
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Join group");
                builder.setMessage("Enter the ID of the group");
                final EditText input = new EditText(MainActivity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setView(input);
                builder.setPositiveButton("Join", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(input.getText().toString() != null && input.getText().toString().length() >= 1) {
                            joinGroup(input.getText().toString());
                        }
                        else{
                            Toast.makeText(MainActivity.this, "Please enter a group ID", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.show();
            }
        });

        //Only main FAB is visible in the beginning
        closeSubMenusFab();

        groupsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GroupFragment groupFragment = new GroupFragment();
                groupFragment.show(getFragmentManager(), "groupFragment");
                groupFragment.setPartItems(groups.get(position).getParts());
                groupFragment.setGroupName(groups.get(position).getName());
                groupFragment.setGroupID(groups.get(position).getID());
            }
        });
        groupsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final Group group = groups.get(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Leave group");
                builder.setMessage("Are you sure you want to leave the group?");
                builder.setPositiveButton("Leave", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        leaveGroup(group.getID());
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.show();
                return true;
            }
        });

        //get info from database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        storageReference = mDatabase.getReference("root/groups/");
        storageReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //TODO use root/users/groups list to fetch groups from the database
                groups.clear();
                Group group;
                for(DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                    group = childSnapshot.getValue(Group.class);
                    if(group != null && group.getMembers() != null &&group.getMembers().contains(userID)){
                        groups.add(group);
                    }
                }
                if(groups.size() >= 1){
                    groupsListView.setVisibility(View.VISIBLE);
                    noGroupsTextView.setVisibility(View.INVISIBLE);
                }
                else{
                    groupsListView.setVisibility(View.INVISIBLE);
                    noGroupsTextView.setVisibility(View.VISIBLE);
                }
                groupsAdapter.notifyDataSetChanged();
                customProgressDialog.dismiss();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Error connecting to database. " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                customProgressDialog.dismiss();
            }
        });
    }

    private void addGroupToDatabase(final String groupName){
        customProgressDialog = CustomProgressDialog.show(this, "", "");
        mAuth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //TODO add groupID to root/users/groups list as well, and use that groups list to fetch groups from the database
                    configurationReference = mDatabase.getReference("root/config/");
                    configurationReference.child("latestGroupID").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot != null){
                                latestGroupIDString = String.valueOf(dataSnapshot.getValue());
                                if(latestGroupIDString != null && latestGroupIDString.length() >= 1){
                                    try {
                                        latestGroupIDValue = Long.parseLong(latestGroupIDString);
                                    }catch (NumberFormatException e){
                                        latestGroupIDValue = 0;
                                    }
                                }
                            }
                            else{
                                latestGroupIDString = "0";
                                latestGroupIDValue = 0;
                            }

                            long groupID = ++latestGroupIDValue;
                            String[] partNames = getResources().getStringArray(R.array.quran_part_numbers);
                            List<Part> parts = new ArrayList<Part>();
                            Part part;
                            for(int i = 0; i <= 29; i++){
                                int partID = i+1;
                                part = new Part(i,partID + "-" + partNames[i], groupID, "", Part.PART_STATE_UNTAKEN, getCurrentTime());
                                parts.add(i, part);
                            }
                            List<Long> members = new ArrayList<Long>();
                            members.add(userID);
                            Group group = new Group(groupID, groupName, parts, 0, prefs.getLong("userID", 0), members/*new HashMap<String, Long>(), partStates, new HashMap<String, Long>()*/);

                            storageReference = mDatabase.getReference("root/groups");
                            storageReference.child(""+group.getID()).setValue(group.toMap());

                            configurationReference.child("latestGroupID").setValue(group.getID());

                            customProgressDialog.dismiss();
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(MainActivity.this, "Error uploading group data. " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            customProgressDialog.dismiss();
                        }
                    });
                }
                else{
                    customProgressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Failed to authenticate with server. " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void joinGroup(final String groupID){
        customProgressDialog = CustomProgressDialog.show(this, "", "");
        mAuth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //TODO add groupID to root/users/groups list as well, and use that groups list to fetch groups from the database
                    storageReference = mDatabase.getReference("root/groups");
                    storageReference.child(groupID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Group group = dataSnapshot.getValue(Group.class);
                            if(group != null){
                                List<Long> members = group.getMembers();
                                if(members == null){
                                    members = new ArrayList<Long>();
                                    members.add(userID);
                                    group.setMembers(members);
                                    storageReference.child("" + group.getID()).setValue(group.toMap());
                                    customProgressDialog.dismiss();
                                }
                                else if(members != null && !members.contains(userID)) {
                                    members.add(userID);
                                    group.setMembers(members);
                                    storageReference.child("" + group.getID()).setValue(group.toMap());
                                    customProgressDialog.dismiss();
                                }
                                else if(members != null && members.contains(userID)){
                                    Toast.makeText(MainActivity.this, "Group already joined before.", Toast.LENGTH_SHORT).show();
                                    customProgressDialog.dismiss();
                                }
                            }
                            else{
                                Toast.makeText(MainActivity.this, "Group not found.", Toast.LENGTH_SHORT).show();
                                customProgressDialog.dismiss();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(MainActivity.this, "Group not found. " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            customProgressDialog.dismiss();
                        }
                    });
                }
                else{
                    customProgressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Failed to authenticate with server. " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void leaveGroup(final Long groupID){
        customProgressDialog = CustomProgressDialog.show(this, "", "");
        mAuth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    storageReference = mDatabase.getReference("root/groups");
                    storageReference.child(""+groupID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Group group = dataSnapshot.getValue(Group.class);
                            if(group != null){
                                List<Long> members = group.getMembers();
                                if(members != null && members.contains(userID)) {
                                    members.remove(userID);
                                    group.setMembers(members);
                                    storageReference.child("" + group.getID()).setValue(group.toMap());
                                    customProgressDialog.dismiss();
                                }
                            }
                            else{
                                Toast.makeText(MainActivity.this, "Group not found.", Toast.LENGTH_SHORT).show();
                                customProgressDialog.dismiss();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(MainActivity.this, "Group not found. " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            customProgressDialog.dismiss();
                        }
                    });
                }
                else{
                    customProgressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Failed to authenticate with server. " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private long getCurrentTime(){
        Date date = new Date();
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Egypt"));
        calendar.setTime(date);
        Log.d("SummaryFragment", "Calendar - Current time in milliseconds: " + calendar.getTime().getTime());
        return calendar.getTime().getTime();
    }

    //closes FAB submenus
    private void closeSubMenusFab(){
        joinGroupFabMenu.setVisibility(View.INVISIBLE);
        createGroupFabMenu.setVisibility(View.INVISIBLE);
        groupsFabButton.setImageResource(android.R.drawable.ic_input_add);
        fabExpanded = false;
    }

    //Opens FAB submenus
    private void openSubMenusFab(){
        joinGroupFabMenu.setVisibility(View.VISIBLE);
        createGroupFabMenu.setVisibility(View.VISIBLE);
        //Change settings icon to 'X' icon
        groupsFabButton.setImageResource(R.drawable.ic_action_cancel);
        fabExpanded = true;
    }

    @Override
    public void onBackPressed(){
        if (fabExpanded == true){
            closeSubMenusFab();
        } else {
            super.onBackPressed();
        }
    }

    private void checkPermissions(){
        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.WRITE_EXTERNAL_STORAGE")) {
            new AlertDialog.Builder(this)
                    .setTitle("External Storage Write permission")
                    .setMessage("You need to enable permissions for the app to function properly")
                    .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, RC_PERMISSION_WRITE_EXTERNAL_STORAGE);
                        }
                    })
                    .show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, RC_PERMISSION_WRITE_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,  String permissions[], int[] grantResults) {
        switch (requestCode){
            case RC_PERMISSION_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if(grantResults.length >= 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //allowed
                }
                else{
                    //denied
                    Toast.makeText(this, "You need to enable permissions for the app to function properly.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }
}

