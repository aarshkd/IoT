package com.example.kapil.firebaseproject;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class WriteData extends AppCompatActivity {

    private Button mFirebasebtn;

    private DatabaseReference mDatabase;
    private EditText mMotorfield;
    private EditText mRPMfield;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_data);

        mFirebasebtn = (Button) findViewById(R.id.Firebasebutton);
        mDatabase = FirebaseDatabase.getInstance().getReference();  //get reference of our linked database using google id
        mMotorfield = (EditText) findViewById(R.id.Motorfield);
        mRPMfield = (EditText) findViewById(R.id.RPMfield);

        mFirebasebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //when button is clicked we need two things to be happened
                //1- create child in our root object
                //2- Assign some value to the child object

                String name = mMotorfield.getText().toString().trim();
                String rpm = mRPMfield.getText().toString().trim();

                HashMap<String, String> dataMap = new HashMap<String, String>();
                dataMap.put("MotorNumber",name);
                dataMap.put("RPM_Value",rpm);

                mDatabase.child("Motor_History").child(dataMap.get("MotorNumber")).setValue(dataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()) {
                            Toast.makeText(WriteData.this, "Stored..", Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(WriteData.this, "error..", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                mDatabase.child("Current_Motor_Details").setValue(dataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()) {
                            Toast.makeText(WriteData.this, "Stored..", Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(WriteData.this, "error..", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });
    }
}
