package com.example.kapil.firebaseproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ReadData extends AppCompatActivity {

    ListView mUserList;

    DatabaseReference ref;
    FirebaseDatabase mDatabase;

    ArrayList<String> list;
    ArrayAdapter<String> adapter;
    CurrentMotorReading user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_data);

        user = new CurrentMotorReading();
        mUserList = (ListView) findViewById(R.id.motor_list);
        mDatabase = FirebaseDatabase.getInstance();
        ref = mDatabase.getReference("Current_Motor_Readings");
        list = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this, R.layout.user_info, R.id.userinfo, list);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //for(DataSnapshot ds: dataSnapshot.getChildren()){

                    user = dataSnapshot.getValue(CurrentMotorReading.class);
                    list.add("Current " + user.getCurrent().toString());
                    list.add("Power " + user.getPower().toString());
                    list.add("Encoder RPM " + user.getEncoderRPM().toString());
                    list.add("User RPM " + user.getUserRPM().toString());

               // }
                mUserList.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
