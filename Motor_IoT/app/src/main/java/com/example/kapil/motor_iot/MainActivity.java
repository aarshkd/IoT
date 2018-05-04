package com.example.kapil.motor_iot;
import  android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kapil.motor_iot.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.nitri.gauge.Gauge;

public class MainActivity extends AppCompatActivity {//implements SensorEventListener {

    private static SeekBar seek_bar;
    private static TextView text_view;
    private ValueEventListener mMessageListener;
    private SensorManager sensorManager;
    Sensor accelerometer;
    FirebaseDatabase mDatabase;
    Switch sw, sw1, sw2;
    DatabaseReference ref;

    CurrentMotorReading cmr;
    CurrentMotorReading switch_state;
    CurrentMotorReading PID_state;

    private GestureDetectorCompat gestureobject;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        gestureobject = new GestureDetectorCompat(this, new LearnGesture());


        seekbarr();
        PID_state();
        Switch_state();
        Direction();
        // accelerometerr();
        final Gauge gauge = (Gauge) findViewById(R.id.gauge);
        final Gauge gauge2 = (Gauge) findViewById(R.id.gauge2);

        ref = FirebaseDatabase.getInstance().getReference("Current_Motor_Readings");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //for(DataSnapshot ds: dataSnapshot.getChildren()){
                //Integer rpm = ds.child("userRPM").getValue(Integer.class);
                cmr = dataSnapshot.getValue(CurrentMotorReading.class);
                gauge.moveToValue(Integer.parseInt(cmr.getEncoderRPM().toString()));
                gauge.setLowerText(cmr.getEncoderRPM().toString());
                String mytext=String.format("%.2f", cmr.getPower());
                float value = cmr.getPower();
                gauge2.moveToValue(value);
                gauge2.setLowerText(mytext);

                //}
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //gauge.moveToValue(800);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.gestureobject.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    class LearnGesture extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if(e2.getX() > e1.getX()){

            }
            else
            if(e2.getX() < e1.getX()){
                Intent myIntent = new Intent(MainActivity.this, Graph.class);
                finish();
                startActivity(myIntent);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }


            return true;
        }
    }

    public void seekbarr() {
        seek_bar = (SeekBar) findViewById(R.id.seekBar);
        text_view = (TextView) findViewById(R.id.textView);
        text_view.setText(seek_bar.getProgress() + "/" + seek_bar.getMax());
        mDatabase = FirebaseDatabase.getInstance();
        ref = mDatabase.getReference("Current_Motor_Readings");

        seek_bar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    int Progress_Value;

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        Progress_Value = progress;
                        text_view.setText(progress + "/" + seek_bar.getMax());



                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        text_view.setText(Progress_Value + "/" + seek_bar.getMax());
                        ref.child("userRPM").setValue(Progress_Value).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {
                                    Toast.makeText(MainActivity.this, "RPM Changed", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MainActivity.this, "error..", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                }
        );


    }



    public void PID_state() {
        sw = (Switch) findViewById(R.id.PID);
        mDatabase = FirebaseDatabase.getInstance();
        ref = mDatabase.getReference("Current_Motor_Readings");

        sw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean check = sw.isChecked();
                if (check) {
                    ref.child("PID").setValue(1).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "PID is ON", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, "error..", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else {
                    ref.child("PID").setValue(0).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "PID is OFF", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, "error..", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }


        });


        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                PID_state = dataSnapshot.getValue(CurrentMotorReading.class);
                long value = PID_state.getPID();
                if(value == 1){
                    sw.setChecked(true);
                }
                else{
                    sw.setChecked(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }




    public void Switch_state() {
        sw1 = (Switch) findViewById(R.id.Switch);
        mDatabase = FirebaseDatabase.getInstance();
        ref = mDatabase.getReference("Current_Motor_Readings");

        sw1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean check = sw1.isChecked();
                if (check) {
                    ref.child("Switch").setValue(1).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "Power is ON", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, "error..", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else {
                    ref.child("Switch").setValue(0).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "Power is OFF", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, "error..", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }


        });


        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                switch_state = dataSnapshot.getValue(CurrentMotorReading.class);
                long value = switch_state.getSwitch();
                if(value == 1){
                    sw1.setChecked(true);
                }
                else{
                    sw1.setChecked(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    public void Direction() {
        sw2 = (Switch) findViewById(R.id.Direction);
        mDatabase = FirebaseDatabase.getInstance();
        ref = mDatabase.getReference("Current_Motor_Readings");

        sw2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean check = sw2.isChecked();
                if (check) {
                    ref.child("Direction").setValue(1).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "Clockwise", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, "error..", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else {
                    ref.child("Direction").setValue(0).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "AntiClockwise", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, "error..", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }


        });

    }




    @Override
    protected void onStart() {
        super.onStart();
        ref = FirebaseDatabase.getInstance().getReference("Current_Motor_Readings");

        ValueEventListener messageListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //for(DataSnapshot ds: dataSnapshot.getChildren()){
                //Integer rpm = ds.child("userRPM").getValue(Integer.class);
                cmr = dataSnapshot.getValue(CurrentMotorReading.class);
                seek_bar.setProgress(Integer.parseInt(cmr.getUserRPM().toString()));

                //}
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        ref.addListenerForSingleValueEvent(messageListener);
        mMessageListener = messageListener;
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mMessageListener != null) {
            ref.removeEventListener(mMessageListener);
        }
    }
}
