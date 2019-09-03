package com.joey.khatmalquran.ui.main;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.joey.khatmalquran.R;
import com.joey.khatmalquran.data.db.entities.Part;
import com.joey.khatmalquran.utils.CustomProgressDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by Joey on 11/26/2017.
 */

public class GroupFragment extends DialogFragment {

    TextView groupNameTextView;
    ListView partsListView;
    PartsListAdapter partsAdapter;
    List<Part> parts;

    SharedPreferences prefs;
    String groupName;
    long groupID;

    CustomProgressDialog customProgressDialog;
    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    DatabaseReference storageReference;
    public GroupFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            /*mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);*/
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_group, container, false);
        groupNameTextView = (TextView) v.findViewById(R.id.group_name_textview);
        partsListView = (ListView) v.findViewById(R.id.parts_listview);
        partsAdapter = new PartsListAdapter(getActivity(), parts);
        partsListView.setAdapter(partsAdapter);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        prefs = getActivity().getSharedPreferences("KhatmAlQuran.Login", 0);

        groupNameTextView.setText(groupName);

        partsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Part part = parts.get(position);
                if(part.getState() == Part.PART_STATE_UNTAKEN){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Assign part");
                    builder.setMessage("Do you want to claim this part for yourself?");
                    builder.setPositiveButton("Assign to myself", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            part.setAssociatedPerson(prefs.getString("name", "def"));
                            part.setAssociatedPersonID(prefs.getLong("userID", 0));
                            part.setLastActionTimestamp(getCurrentTime());
                            part.setState(Part.PART_STATE_TAKEN);
                            updatePart(part);
                        }
                    });
                    builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    builder.setNegativeButton("Assign to someone else", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setTitle("Assign to someone else");
                            builder.setMessage("Enter the name of the person reading this part");
                            final EditText input = new EditText(getActivity());
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.MATCH_PARENT);
                            input.setLayoutParams(lp);
                            //input.setInputType(InputType.TYPE_CLASS_NUMBER);
                            builder.setView(input);
                            builder.setPositiveButton("Assign", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if(input.getText().toString() != null && input.getText().toString().length() >= 1) {
                                        part.setAssociatedPerson(input.getText().toString());
                                        part.setAssociatedPersonID(prefs.getLong("userID", -1));
                                        part.setLastActionTimestamp(getCurrentTime());
                                        part.setState(Part.PART_STATE_TAKEN);
                                        updatePart(part);
                                    }
                                    else{
                                        Toast.makeText(getActivity(), "Please enter a name", Toast.LENGTH_SHORT).show();
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
                    builder.show();
                }
                else if(part.getState() == Part.PART_STATE_TAKEN){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Mark part as completed?");
                    builder.setMessage("Do you want to mark this part as completed or assign it to someone else?");
                    builder.setPositiveButton("Mark as completed", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //part.setAssociatedPerson(prefs.getString("name", "def"));
                            part.setLastActionTimestamp(getCurrentTime());
                            part.setState(Part.PART_STATE_COMPLETED);
                            updatePart(part);
                        }
                    });
                    builder.setNegativeButton("Unclaim part", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            part.setAssociatedPerson("");
                            part.setAssociatedPersonID(0);
                            part.setState(Part.PART_STATE_UNTAKEN);
                            part.setLastActionTimestamp(getCurrentTime());
                            updatePart(part);
                        }
                    });
                    /*builder.setNeutralButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });*/
                    builder.show();
                }
                else if(part.getState() == Part.PART_STATE_COMPLETED){
                    Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Part already completed.");
                    builder.setMessage("Do you want to mark it in progress?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            part.setLastActionTimestamp(getCurrentTime());
                            part.setState(Part.PART_STATE_TAKEN);
                            updatePart(part);
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    builder.show();
                }
            }
        });

        ImageButton shareButton = (ImageButton) v.findViewById(R.id.share_parts_list_button);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap bitmap = createBitmap();
                //bitmap is now ready
                String imageName = cacheBitmap(bitmap);

                File imagePath = new File(getActivity().getCacheDir(), "images");
                File newFile = new File(imagePath, imageName);
                Uri contentUri = FileProvider.getUriForFile(getActivity(), "com.joey.khatmalquran.fileprovider", newFile);

                if (contentUri != null) {
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
                    shareIntent.setDataAndType(contentUri, getActivity().getContentResolver().getType(contentUri));
                    shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                    startActivity(Intent.createChooser(shareIntent, "Choose an app"));
                }
            }
        });

        ImageButton addPersonButton = (ImageButton) v.findViewById(R.id.add_person_button);
        addPersonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                TextView titleTextView = new TextView(getActivity());
                TextView groupNameTextView = new TextView(getActivity());
                TextView groupIDTextView = new TextView(getActivity());
                TextView descriptionTextView = new TextView(getActivity());
                Button doneButton = new Button(getActivity());

                titleTextView.setText("Invite people");
                titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
                titleTextView.setTypeface(titleTextView.getTypeface(), Typeface.BOLD);
                groupNameTextView.setText("Group name: " + groupName);
                groupIDTextView.setText("Unique Group ID: " + groupID);
                groupIDTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32);
                groupIDTextView.setTypeface(titleTextView.getTypeface(), Typeface.BOLD);
                descriptionTextView.setText("Give the above unique ID to the person you want to invite.");
                doneButton.setText("Done");
                doneButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                LinearLayout.LayoutParams textViewLayoutParam = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                textViewLayoutParam.setMargins(8, 8, 8, 8);
                titleTextView.setLayoutParams(textViewLayoutParam);
                groupIDTextView.setLayoutParams(textViewLayoutParam);
                groupNameTextView.setLayoutParams(textViewLayoutParam);
                descriptionTextView.setLayoutParams(textViewLayoutParam);

                LinearLayout.LayoutParams buttonLayoutParam = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                buttonLayoutParam.gravity = Gravity.CENTER;
                buttonLayoutParam.setMargins(8, 16, 8, 16);
                doneButton.setLayoutParams(buttonLayoutParam);

                titleTextView.setGravity(Gravity.CENTER);
                groupIDTextView.setGravity(Gravity.CENTER);
                groupNameTextView.setGravity(Gravity.CENTER);
                descriptionTextView.setGravity(Gravity.CENTER);

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                LinearLayout layout = new LinearLayout(getActivity());
                layout.setLayoutParams(layoutParams);
                layout.setOrientation(LinearLayout.VERTICAL);

                layout.addView(titleTextView);
                layout.addView(groupNameTextView);
                layout.addView(groupIDTextView);
                layout.addView(descriptionTextView);
                layout.addView(doneButton);

                alertDialog.setView(layout);
                alertDialog.setCancelable(true);
                alertDialog.show();
            }
        });
        return v;
    }

    public void setPartItems(List<Part> parts){
        this.parts = parts;
    }

    public void setGroupName(String groupName){
        this.groupName = groupName;
    }

    public void setGroupID(long id){
        this.groupID = id;
    }

    private void updatePart(final Part part){
        customProgressDialog = CustomProgressDialog.show(getActivity(), "", "");
        mAuth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    storageReference = mDatabase.getReference("root/groups");
                    storageReference.child(""+part.getGroupID()).child("parts").child(""+part.getPartID()).setValue(part.toMap()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            partsAdapter.notifyDataSetChanged();
                            customProgressDialog.dismiss();
                        }
                    });
                }
                else{
                    customProgressDialog.dismiss();
                    Toast.makeText(getActivity(), "Failed to authenticate with server. " + task.getException(), Toast.LENGTH_SHORT).show();
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

    private Bitmap createBitmap(){
        int allItemsHeight   = 0;
        List<Bitmap> bitmaps    = new ArrayList<Bitmap>();

        for(int item = 0; item < 30; item++){
            View childView = partsAdapter.getView(item, null, partsListView);
            childView.measure(View.MeasureSpec.makeMeasureSpec(partsListView.getWidth(), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            childView.layout(0, 0, childView.getMeasuredWidth(), childView.getMeasuredHeight());
            childView.setDrawingCacheEnabled(true);
            childView.buildDrawingCache();
            bitmaps.add(childView.getDrawingCache());
            allItemsHeight+=childView.getMeasuredHeight();
        }
        Bitmap bitmap = Bitmap.createBitmap(partsListView.getMeasuredWidth(), allItemsHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        Paint paint = new Paint();
        int iHeight = 0;

        for (int i = 0; i < bitmaps.size(); i++) {
            Bitmap bmp = bitmaps.get(i);
            canvas.drawBitmap(bmp, 0, iHeight, paint);
            iHeight+=bmp.getHeight();

            bmp.recycle();
            bmp=null;
        }

        /*//Define a bitmap with the same size as the view
        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas c = new Canvas(bitmap);
        //Get the view's background
        Drawable bgDrawable = v.getBackground();
        if (bgDrawable != null)
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(c);
        else
            //does not have background drawable, then draw white background on the canvas
            c.drawColor(Color.WHITE);
        // draw the view on the canvas
        v.draw(c);*/

        return bitmap;
    }

    public String saveBitmap(Bitmap bitmap){
        //Toast.makeText(context, "saveBitmap", Toast.LENGTH_SHORT).show();
        String DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/KhatmAlQuran/";
        String[] paths = new String[] {DATA_PATH, DATA_PATH+"Images/" };

        for (String path : paths) {
            File dir = new File(path);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    Log.v("MainActivity", "ERROR: Creation of directory " + path + " on sdcard failed");
                    return "";
                } else {
                    Log.v("MainActivity", "Created directory " + path + " on sdcard");
                }
            }
        }
        String fileName = "image1.bmp";
        String fullPath = DATA_PATH + "Images/" + fileName;
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(fullPath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (java.io.IOException e) {
            e.printStackTrace();
        }
        return fullPath;
    }

    private String cacheBitmap(Bitmap bitmap){
        String imageName = "image.png";
        // save bitmap to cache directory
        try {

            File cachePath = new File(getActivity().getCacheDir(), "images");
            cachePath.mkdirs(); // don't forget to make the directory
            FileOutputStream stream = new FileOutputStream(cachePath + "/" + imageName); // overwrites this image every time
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageName;
    }
}
