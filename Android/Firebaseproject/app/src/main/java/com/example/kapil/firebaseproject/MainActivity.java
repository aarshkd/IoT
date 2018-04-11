package com.example.kapil.firebaseproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;



    public class MainActivity extends AppCompatActivity {


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);


            String[] NAMES = {"Read Data", "Write Data", "AcclerometerView"};
            ListAdapter kapilAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, NAMES);
            ListView KapilsListView = (ListView) findViewById(R.id.KapilsListView);
            KapilsListView.setAdapter(kapilAdapter);

            //now we will create onclick listener
            KapilsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {


                    if (position == 0) {
                        Intent myIntent = new Intent(view.getContext(), ReadData.class);
                        startActivityForResult(myIntent, 0);
                        overridePendingTransition(R.anim.right_in, R.anim.left_out);
                    }
                    if (position == 1) {
                        Intent myIntent = new Intent(view.getContext(), WriteData.class);
                        startActivityForResult(myIntent, 0);
                        overridePendingTransition(R.anim.right_in, R.anim.left_out);
                    }
                    if (position == 2) {
                        Intent myIntent = new Intent(view.getContext(), Acclerometer.class);
                        startActivityForResult(myIntent, 0);
                        overridePendingTransition(R.anim.right_in, R.anim.left_out);
                    }
                }
            });
        }
    }
