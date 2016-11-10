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
import android.support.annotation.IntegerRes;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    //Sensor handler
    private SensorManager mSensorManager;
    private Sensor accel_sensor;
    private Sensor gyro_sensor;

    private TextView mAccel_x;
    private TextView mAccel_y;
    private TextView mAccel_z;

    private TextView mGyro_x;
    private TextView mGyro_y;
    private TextView mGyro_z;

    private AudioRecord mAudioRecorder;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final int RECORD_REQUEST_CODE = 101;


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
        mSensorManager.registerListener(this, gyro_sensor, SensorManager.SENSOR_DELAY_FASTEST);

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


        mAccel_x = (TextView) findViewById(R.id.textView);
        mAccel_y = (TextView) findViewById(R.id.textView2);
        mAccel_z = (TextView) findViewById(R.id.textView3);

        mGyro_x = (TextView) findViewById(R.id.textView4);
        mGyro_y = (TextView) findViewById(R.id.textView5);
        mGyro_z = (TextView) findViewById(R.id.textView6);

        int freq = 44100;
        int chan = AudioFormat.CHANNEL_IN_STEREO;
        int enc  = AudioFormat.ENCODING_PCM_16BIT;
        int src  = MediaRecorder.AudioSource.DEFAULT;
        int buflen = AudioRecord.getMinBufferSize(freq, chan, enc);
        mAudioRecorder = new AudioRecord(src,freq,chan,enc,20*buflen);
        if(mAudioRecorder.getState() == AudioRecord.STATE_INITIALIZED) {
            Log.d("AUDIO", "AudioRecord Initialized");
        } else {
            Log.d("AUDIO", "AudioRecord failed to initialize");
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int integer){

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;

        //Case statement for different types of sensor events
        switch (sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                mAccel_x.setText("X Accel: " + event.values[0]);
                mAccel_y.setText("Y Accel: " + event.values[1]);
                mAccel_z.setText("Z Accel: " + event.values[2]);
                break;
            case Sensor.TYPE_GYROSCOPE:
                mGyro_x.setText("X Gyro: " + event.values[0]);
                mGyro_y.setText("Y Gyro: " + event.values[1]);
                mGyro_z.setText("Z Gyro: " + event.values[2]);
                break;
        }
    }
}
