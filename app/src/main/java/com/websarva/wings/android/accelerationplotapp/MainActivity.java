package com.websarva.wings.android.accelerationplotapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener , View.OnClickListener{

    private final String TAG="MainActivity";
    private File file;
    private CsvLogger mCsvLogger;

    private SensorManager sensorManager;
    private Sensor mAccelerometer;
    private boolean isRecording=true;

    private TextView AccView;
    private Button recordButton;

    private Context context;
    private Spinner spinnerSampleRate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //sensor
        sensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        context=getApplicationContext();

        //ボタンに関する記述
        recordButton = findViewById(R.id.bn_record);
        recordButton.setOnClickListener(this);

        AccView=findViewById(R.id.tv_acc);
    }

    @Override
    public void onSensorChanged(SensorEvent event){
        float x,y,z;
        long systemTime=0;
        if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
            x=event.values[0];
            y=event.values[1];
            z=event.values[2];
            systemTime=System.currentTimeMillis();
            StringBuilder stringBuilder=new StringBuilder();
            stringBuilder.append(x+"\n"+y+"\n"+z);
            AccView.setText(stringBuilder);
            mCsvLogger.appendHeader("System time (ms),X (m/s^2),Y (m/s^2),Z (m/s^2)");
            mCsvLogger.appendLine(String.format(Locale.getDefault(), "%d,%.6f,%.6f,%.6f", systemTime, x,y,z));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onClick(View view) {

        if(view.getId()==R.id.bn_record){
            if(isRecording==true){
                //file name
                // timestamp + device serial + data type,
                StringBuilder sb = new StringBuilder();
                int userId=0;
                // Get Current Timestamp in format suitable for file names (i.e. no : or other bad chars)
                Date date = new Date();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss");
                String currentTimestamp = formatter.format(date);
                sb.append(String.format(Locale.getDefault(), "%03d", userId)).append("_").append(currentTimestamp).append(".csv");
                Log.i(TAG,sb.toString());
                //manage file
                file=new File(context.getFilesDir(),sb.toString());
                mCsvLogger = new CsvLogger(file);
                sensorManager.registerListener(this,mAccelerometer,SensorManager.SENSOR_DELAY_NORMAL);
                isRecording=false;
                recordButton.setText("STOP");

            }else {
                isRecording=true;
                sensorManager.unregisterListener(this);
                mCsvLogger.finishSavingLogs();
                recordButton.setText("RECORD");
            }
        }
    }

    @Override
    public void onPause() {
        sensorManager.unregisterListener(this);
        isRecording=true;
        recordButton.setText("RECORD");
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_file_options_list,menu);
        return true;
    }

    //menu setting
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int itemId=item.getItemId();
        switch (itemId){
            case R.id.file_list:
                Intent intent = new Intent(MainActivity.this, FileListActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

}