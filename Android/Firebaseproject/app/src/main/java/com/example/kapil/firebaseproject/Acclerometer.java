package com.example.kapil.firebaseproject;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.nitri.gauge.Gauge;

public class Acclerometer extends AppCompatActivity  {

    private static final String TAG = "Acclerometer";
    DatabaseReference ref;

    CurrentMotorReading cmr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acclerometer);
        final Gauge gauge = (Gauge) findViewById(R.id.gauge);

        ref = FirebaseDatabase.getInstance().getReference("Current_Motor_Readings");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //for(DataSnapshot ds: dataSnapshot.getChildren()){
                 //Integer rpm = ds.child("userRPM").getValue(Integer.class);
                   cmr = dataSnapshot.getValue(CurrentMotorReading.class);
                 gauge.moveToValue(Integer.parseInt(cmr.getEncoderRPM().toString()));

                //}
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //gauge.moveToValue(800);

    }
}
