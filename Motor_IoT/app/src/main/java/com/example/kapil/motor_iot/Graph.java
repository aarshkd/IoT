package com.example.kapil.motor_iot;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Random;

public class Graph extends AppCompatActivity implements OnChartValueSelectedListener {



    private LineChart mChart;
   // private Thread thread;
    private boolean plotData = true;

    FirebaseDatabase mDatabase;
    DatabaseReference ref1;
    CurrentMotorReading cmr;
    private ValueEventListener mMessageListener;


    private static final Random RANDOM = new Random();
    private LineGraphSeries<DataPoint> series;
    private int lastX = 0;
    private Long value;
    private Long encoderRPM = 0L;
    private Long userRPM = 0L;

    private GestureDetectorCompat gestureobject;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        gestureobject = new GestureDetectorCompat(this, new LearnGesture());


        mChart = (LineChart) findViewById(R.id.chart1);
        mChart.setOnChartValueSelectedListener(this);

        // enable description text
        mChart.getDescription().setEnabled(false);

        // enable touch gestures
        mChart.setTouchEnabled(false);

        // enable scaling and dragging
        mChart.setDragEnabled(false);
        mChart.setScaleEnabled(false);
        mChart.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(false);

        // set an alternative background color
        mChart.setBackgroundColor(Color.WHITE);


        LineData data = new LineData();
        data.setValueTextColor(Color.BLACK);
//        LineData data1 = new LineData();
//        data1.setValueTextColor(Color.BLACK);

        // add empty data
        mChart.setData(data);
       // mChart.setData(data1);

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.BLACK);
        l.setTextSize(24f);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);

        XAxis xl = mChart.getXAxis();
        xl.setTextColor(Color.BLACK);
        xl.setTextSize(16f);
        xl.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);


        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setTextSize(16f);
        leftAxis.setAxisMaximum(800f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);

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
                Intent myIntent = new Intent(Graph.this, MainActivity.class);
                finish();
                startActivity(myIntent);
                overridePendingTransition(R.anim.left_in, R.anim.right_out);
            }
            else
            if(e2.getX() < e1.getX()){
                Intent myIntent = new Intent(Graph.this, Power.class);
                finish();
                startActivity(myIntent);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }


            return true;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        feedMultiple();
    }

    private void addEntry() {

        LineData data = mChart.getData();
        LineData data1 = mChart.getData();

        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);
            ILineDataSet set1 = data.getDataSetByIndex(1);
            // set.addEntry(...); // can be called as well

            if (set == null) {
                set = createSet();
                set1 = createSet1();
                data.addDataSet(set);
                data.addDataSet(set1);
            }


            data.addEntry(new Entry(set.getEntryCount(),  (float)(encoderRPM)), 0);
            data.addEntry(new Entry(set.getEntryCount(), (float) (userRPM)), 1);
            data.notifyDataChanged();
           // System.out.println("RPM: "+encoderRPM);

            // let the chart know it's data has changed
            mChart.notifyDataSetChanged();

            // limit the number of visible entries
            mChart.setVisibleXRangeMaximum(50);
            // mChart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            mChart.moveViewToX(data.getEntryCount());

            // this automatically refreshes the chart (calls invalidate())
            // mChart.moveViewTo(data.getXValCount()-7, 55f,
            // AxisDependency.LEFT);
        }


//        if (data1 != null) {
//
//            ILineDataSet set1 = data1.getDataSetByIndex(0);
//            // set.addEntry(...); // can be called as well
//
//            if (set1 == null) {
//                set1 = createSet1();
//                data1.addDataSet(set1);
//            }
//
//
//            data1.addEntry(new Entry(set1.getEntryCount(),  (float)(userRPM)), 0);
//            data1.notifyDataChanged();
//            // System.out.println("RPM: "+encoderRPM);
//
//            // let the chart know it's data has changed
//            mChart.notifyDataSetChanged();
//
//            // limit the number of visible entries
//            mChart.setVisibleXRangeMaximum(50);
//            // mChart.setVisibleYRange(30, AxisDependency.LEFT);
//
//            // move to the latest entry
//            mChart.moveViewToX(data1.getEntryCount());
//
//            // this automatically refreshes the chart (calls invalidate())
//            // mChart.moveViewTo(data.getXValCount()-7, 55f,
//            // AxisDependency.LEFT);
//        }
    }



    private LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, "Encoder RPM");

        set.setAxisDependency(AxisDependency.LEFT);
        set.setColor(Color.rgb(0, 0, 255));
       // set.setCircleColor(Color.WHITE);
        set.setLineWidth(5f);
        set.setCircleRadius(1f);
        set.setFillAlpha(65);
      //  set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setValueTextColor(Color.BLACK);
        set.setValueTextSize(12f);
        set.setDrawValues(false);
        return set;
    }

    private LineDataSet createSet1(){
        LineDataSet set1 = new LineDataSet(null, "User RPM");
        set1.setAxisDependency(AxisDependency.LEFT);
        set1.setColor(Color.rgb(255, 0, 0));
        // set.setCircleColor(Color.WHITE);
        set1.setLineWidth(3f);
        set1.setCircleRadius(1f);
        set1.setFillAlpha(65);
        //  set.setFillColor(ColorTemplate.getHoloBlue());
        set1.setHighLightColor(Color.rgb(244, 117, 117));
        set1.setValueTextColor(Color.BLACK);
        set1.setValueTextSize(12f);
        set1.setDrawValues(false);
        return set1;
    }


//    private LineDataSet createSet1() {
//
//        LineDataSet set1 = new LineDataSet(null, "User RPM");
//        set1.setAxisDependency(AxisDependency.LEFT);
//        set1.setColor(Color.BLACK);
//        // set.setCircleColor(Color.WHITE);
//        set1.setLineWidth(3f);
//        set1.setCircleRadius(1f);
//        set1.setFillAlpha(65);
//        //  set.setFillColor(ColorTemplate.getHoloBlue());
//        set1.setHighLightColor(Color.rgb(244, 117, 117));
//        set1.setValueTextColor(Color.BLACK);
//        set1.setValueTextSize(12f);
//        set1.setDrawValues(false);
//        return set1;
//    }

    private Thread thread;

    private void feedMultiple() {

        ref1 = FirebaseDatabase.getInstance().getReference("Current_Motor_Readings");

        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //for(DataSnapshot ds: dataSnapshot.getChildren()){
                //Integer rpm = ds.child("userRPM").getValue(Integer.class);
                cmr = dataSnapshot.getValue(CurrentMotorReading.class);
                encoderRPM = cmr.getEncoderRPM();
                userRPM = cmr.getUserRPM();
                System.out.println("RPM1: "+encoderRPM);
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        if (thread != null)
            thread.interrupt();

        final Runnable runnable = new Runnable() {

            @Override
            public void run() {
                addEntry();
            }
        };

        thread = new Thread(new Runnable() {

            @Override
            public void run() {
                for (int i = 0; ; i++) {

                    // Don't generate garbage runnables inside the loop.
                    runOnUiThread(runnable);

                    try {
                        Thread.sleep(350);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        });

        thread.start();
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("Entry selected", e.toString());
    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (thread != null) {
            thread.interrupt();
        }
    }

}
