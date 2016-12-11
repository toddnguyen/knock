package com.example.todd.knock;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.IntegerRes;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;

import static java.lang.Math.abs;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    // set to true to enable debug CSV
    private static final boolean DEBUG = false;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final int RECORD_REQUEST_CODE = 101;

    //Sensor handler
    private SensorManager mSensorManager;
    private Sensor accel_sensor;
    private Sensor gyro_sensor;

    //State variables
    private enum State { READY, RECORDING };
    private State state = State.READY;
    private Handler handler;

    private FileOutputStream os;

    //Sensor readings
    private float accel_X;
    private float accel_Y;
    private float accel_Z;
    private float gyro_X;
    private float gyro_Y;
    private float gyro_Z;

    //Audio
    private final Audio_Record mAudioRecorder = new Audio_Record();

    //Knock Variables
    private long prevKnock = 0;
    private long currKnock = 0;
    private long initialKnock = 0;

    private boolean knockdetected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Create sensor manager
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        //Create sensor
        accel_sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyro_sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        mSensorManager.registerListener(this, accel_sensor, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, gyro_sensor, SensorManager.SENSOR_DELAY_NORMAL);

        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }

        permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECORD_AUDIO},
                RECORD_REQUEST_CODE);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int integer){

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;


        handler = new Handler();
        Runnable stopRecordRunnable = new Runnable() {
            @Override
            public void run() {
                long startTime = System.nanoTime();
                TextView center = (TextView) findViewById(R.id.textView8);
                short[] sData = mAudioRecorder.getValues();
                mAudioRecorder.stopRecording();

                try{
                    int length = sData.length;
                    short[] A = new short[length/2];
                    short[] B = new short[length/2];

                    for(int i = 0; i < length/2; i++){
                        A[i] = sData[2*i];
                        B[i] = sData[2*i + 1];
                    }

                    if(DEBUG){
                        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/Knock", "audio.csv");
                        os = new FileOutputStream(file);
                        int sDataLength = sData.length;
                        for(int i = 0; i < sDataLength; i++){
                            String data = String.valueOf(sData[i]) + "\n";
                            os.write(data.getBytes());
                        }
                        os.close();
                    }

                    int xcorrOffset = 100;
                    CrossCorrelation corr = new CrossCorrelation();
                    long[] xcorrelation = corr.crossCorrelate(A, B, 150);

                    if(xcorrelation != null){
                        int maxindex = 0;
                        long maxvalue = 0;
                        for(int i = (xcorrelation.length/2)-xcorrOffset; i < xcorrelation.length/2 + xcorrOffset; i++){
                            if(xcorrelation[i] >= 800 && xcorrelation[i] > maxvalue){
                                maxindex = i;
                                maxvalue = xcorrelation[i];
                            }
                        }
                        int difference = abs(maxindex - xcorrelation.length/2);
                        if(!knockdetected || maxindex == 0 || difference < 14) {
                            Log.d("KNOCK", "X");
                            center.setText("X");
                        } else if(maxindex > xcorrelation.length/2 - 1){
                            Log.d("KNOCK", "^");
                            center.setText("^");
                        } else {
                            Log.d("KNOCK", "v");
                            center.setText("v");
                        }

                        if(DEBUG){
                            File xcorrfile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/Knock", "xcorr.csv");
                            os = new FileOutputStream(xcorrfile);
                            int xcorrlength = xcorrelation.length;
                            for(int i = 0; i < xcorrlength; i++){
                                String data = String.valueOf(xcorrelation[i]) + "\n";
                                os.write(data.getBytes());
                            }
                            os.close();
                        }

                    } else {
                        center.setText("X");
                    }

                } catch(Exception e){
                    Log.e("KNOCK", e.toString());
                }

                knockdetected = false;
                state = State.READY;
                Log.d("KNOCK", "Computation time is: " + String.valueOf((System.nanoTime() - startTime)));
            }
        };

        //Case statement for different types of sensor events
        switch (sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                accel_X = event.values[0];
                accel_Y = event.values[1];
                accel_Z = event.values[2];

                //Process knock using accel z - THRESHOLD METHOD
                if((accel_Z>10.5 || accel_Z<-10.5) && (abs(gyro_X) < 0.1) && (abs(gyro_Y) < 0.1) && (abs(gyro_Z) < 0.1)){
                    currKnock = event.timestamp;
                    if(state == State.READY){
                        mAudioRecorder.startRecording();
                        state = state.RECORDING;
                        initialKnock = event.timestamp;
                        TextView center = (TextView) findViewById(R.id.textView8);
                        center.setText("!!!");
                        handler.postDelayed(stopRecordRunnable, 1000);
                        prevKnock = currKnock;
                    } else if(state == state.RECORDING){
                        knockdetected = true;
                    }
                }
                break;

            case Sensor.TYPE_GYROSCOPE:
                gyro_X = event.values[0];
                gyro_Y = event.values[1];
                gyro_Z = event.values[2];
                break;
        }
    }
}
