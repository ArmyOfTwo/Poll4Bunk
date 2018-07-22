package com.armyof2.poll4bunk;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import static com.armyof2.poll4bunk.SignInActivity.userUid;

public class CreateActivity extends FragmentActivity {

    static EditText bunkDate;
    private EditText bunkName;
    private EditText bunkNum;
    private EditText bunkAdmin;
    private Intent intent;
    private FirebaseDatabase database;
    public static DatabaseReference myRef;
    private HashMap<String, String> dataMap;
    private DialogInterface.OnClickListener dialogClickListener;
    private boolean serverExists = false;
    private ArrayList<String> bunkTitle;

    private class BunkServer {
        String name;
        String date;
        String num;
        String admin;
    }

    BunkServer bunk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adjustFontScale( getResources().getConfiguration());
        adjustDisplayScale( getResources().getConfiguration());
        setContentView(R.layout.activity_create);

        bunkName = (EditText) findViewById(R.id.et_pollname);
        bunkAdmin = (EditText) findViewById(R.id.et_name);
        bunkDate = (EditText) findViewById(R.id.et_date);
        bunkDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });
        bunkNum = (EditText) findViewById(R.id.et_numofparti);
        intent = new Intent(this, LaunchActivity.class);
        bunk = new BunkServer();
        bunkTitle = new ArrayList<>();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(userUid))
                    serverExists = true;
                for (DataSnapshot child : dataSnapshot.getChildren())
                    bunkTitle.add(child.child("Bunk Title").getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });

        //Create yes / no dialog box
        dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        myRef.child(userUid).setValue(dataMap);
                        startActivity(intent);
                        finish();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        startActivity(intent);
                        finish();
                        //No button clicked
                        break;
                }
            }
        };
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public static class DatePickerFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), R.style.datepicker, this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            bunkDate.setText(day + "/" + (month + 1) + "/" + year);
        }
    }

    public void onCreateButtonClicked(View view) {
        bunk.name = bunkName.getText().toString();
        bunk.date = bunkDate.getText().toString();
        bunk.num = bunkNum.getText().toString();
        bunk.admin = bunkAdmin.getText().toString();

        if(bunk.name.equals("")){
            Toast.makeText(this, "You forgot to input title!", Toast.LENGTH_SHORT).show();
            return;
        } else if (bunkTitle.contains(bunk.name)) {
            Toast.makeText(this, "Server with same name already exists!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(bunk.name.contains(",")){
            Toast.makeText(this, "No commas allowed, sorry!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(bunk.date.equals("")){
            Toast.makeText(this, "You forgot to input date!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(bunk.num.equals("")){
            Toast.makeText(this, "You forgot to input participants!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(bunk.admin.equals("")){
            Toast.makeText(this, "You forgot to input name!", Toast.LENGTH_SHORT).show();
            return;
        }

        dataMap = new HashMap<String, String>();
        dataMap.put("Bunk Title", bunk.name);
        dataMap.put("Bunk Date", bunk.date);
        dataMap.put("Bunk Participants", bunk.num);
        dataMap.put("Bunker Admin", bunk.admin);

        if (!serverExists) {
            myRef.child(userUid).setValue(dataMap);
            Log.d("TAG", "If exec");
            startActivity(intent);
            finish();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext(), R.style.AlertDialog);
            builder.setMessage("You have already created a server before, Overwrite?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
            Log.d("TAG", "Else exec");
        }
    }

    public void adjustFontScale(Configuration configuration) {
        if (configuration != null && configuration.fontScale != 1.0) {
            configuration.fontScale = (float) 1.0;
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
            wm.getDefaultDisplay().getMetrics(metrics);
            metrics.scaledDensity = configuration.fontScale * metrics.density;
            this.getResources().updateConfiguration(configuration, metrics);
        }
    }

    public void adjustDisplayScale(Configuration configuration) {
        if (configuration != null) {
            Log.d("TAG", "adjustDisplayScale: " + configuration.densityDpi);
            if(configuration.densityDpi >= 300)
                configuration.densityDpi = 400;
            else if(configuration.densityDpi >= 100)
                configuration.densityDpi = 200;
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
            wm.getDefaultDisplay().getMetrics(metrics);
            metrics.scaledDensity = configuration.densityDpi * metrics.density;
            this.getResources().updateConfiguration(configuration, metrics);
        }
    }
}

