package com.websarva.wings.android.accelerationplotapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import com.github.mikephil.charting.utils.ColorTemplate;



public class ChartActivity extends AppCompatActivity {

    private final String TAG="ChartActivity";
    private String filename;
    private LineChart mChart;
    private LineChart hzChart;


    private String [] labels=new String[]{
            "AccX",
            "AccY",
            "AccZ"
    };

    private ArrayList<Float> xDataList;
    private ArrayList<Float> yDataList;
    private ArrayList<Float> zDataList;
    private ArrayList<Float> timeDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        Intent intent=getIntent();
        filename=intent.getStringExtra("FILE_NAME");

        //??????????????????
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        mChart = findViewById(R.id.LineChart_acc);
        hzChart=findViewById(R.id.LineChart_hz);

        //csv????????????
        readCsvFile(filename);

        //??????????????????
        plotChart(timeDataList,xDataList,yDataList,zDataList);
        //Hz?????????
        plotHz(timeDataList);
        
    }

    //???????????????????????????
    private void readCsvFile(String file){
        try {
            FileInputStream fileInputStream=openFileInput(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream,"UTF-8"));
            String line;


            //?????????0:systemTime,1:x,2:y,3:z
            String[] data;
            //????????????????????????????????????
            boolean isFirst=true;
            boolean isTimeFirst=true;
            double firstTime=0;
            double time=0;
            //systemCurrent???3????????????
            timeDataList=new ArrayList<>();
            xDataList =new ArrayList<>();
            yDataList =new ArrayList<>();
            zDataList =new ArrayList<>();


            while((line=reader.readLine())!=null) {
                data = line.split(",");
//                Log.i("ChartActivity",data[3].getClass().getSimpleName());
                if (isFirst) {
                    isFirst = false;
                } else {
                    //csv?????????????????????
                    if (isTimeFirst){
                        firstTime= Double.parseDouble(data[0]);
                        isTimeFirst=false;
                    }
                    time=(Double.parseDouble(data[0])-firstTime)/1000;
                    Log.i(TAG, String.valueOf(time));
                    timeDataList.add((float) time);
                    xDataList.add(Float.valueOf(data[1]));
                    yDataList.add(Float.valueOf(data[2]));
                    zDataList.add(Float.valueOf(data[3]));
                }
            }
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void plotChart(ArrayList<Float>timeList,ArrayList<Float>xList,ArrayList<Float>yList,ArrayList<Float>zList){
        //???????????????????????????
        //????????????Entry?????????
        ArrayList<Entry>xEntryList=new ArrayList<>();
        ArrayList<Entry>yEntryList=new ArrayList<>();
        ArrayList<Entry>zEntryList=new ArrayList<>();
        if(timeList.size()!=0){
            for (int i=0;i<timeList.size();i++){
                xEntryList.add(new Entry(timeList.get(i),xList.get(i)));
                yEntryList.add(new Entry(timeList.get(i),yList.get(i)));
                zEntryList.add(new Entry(timeList.get(i),zList.get(i)));
                Log.d(TAG, String.valueOf(timeList.get(i)));
            }
        }
        //3???????????????????????????????????????LineDataSet???????????????List?????????
        ArrayList<ILineDataSet>lineDataSets=new ArrayList<>();
        //Entry???List???DataSet?????????
        LineDataSet xLineDataSet=new LineDataSet(xEntryList,labels[0]);
        LineDataSet yLineDataSet=new LineDataSet(yEntryList,labels[1]);
        LineDataSet zLineDataSet=new LineDataSet(zEntryList,labels[2]);

        //DataSet(?????????)??????????????????????????????
        xLineDataSet.setColor(getResources().getColor(android.R.color.holo_red_light));
        xLineDataSet.setLineWidth(2.0f);
        xLineDataSet.setDrawCircles(false);
        xLineDataSet.setDrawValues(false);

        yLineDataSet.setColor(getResources().getColor(android.R.color.holo_green_light));
        yLineDataSet.setLineWidth(2.0f);
        yLineDataSet.setDrawCircles(false);
        yLineDataSet.setDrawValues(false);

        zLineDataSet.setColor(getResources().getColor(android.R.color.holo_blue_light));
        zLineDataSet.setLineWidth(2.0f);
        zLineDataSet.setDrawCircles(false);
        zLineDataSet.setDrawValues(false);

        //??????????????????
        lineDataSets.add(xLineDataSet);
        lineDataSets.add(yLineDataSet);
        lineDataSets.add(zLineDataSet);
        //DataSet?????????????????????Data?????????
        LineData lineData=new LineData(lineDataSets);

        //Data???Chart?????????
        mChart.setData(lineData);
        //Chart??????????????????????????????
        mChart.getDescription().setEnabled(false);//???????????????????????????
//        mChart.setDrawGridBackground(true);//Grid?????????

        //??????
        Legend legend=mChart.getLegend();
        legend.setEnabled(true);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);//??????????????????
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);//??????????????????
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);//?????????
        legend.setDrawInside(true);//?????????????????????????????????
        legend.setTextColor(Color.BLACK);
        legend.setTextSize(5f);

        //?????????????????????
        //x???
        XAxis xAxis=mChart.getXAxis();
        xAxis.setEnabled(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawLabels(true);
        xAxis.setTextColor(Color.BLACK);

        //y???(??????)
        YAxis yAxis=mChart.getAxisLeft();
        yAxis.setEnabled(true);
        yAxis.setTextColor(Color.BLACK);



        //y???(??????)
        mChart.getAxisRight().setEnabled(false);//??????????????????????????????????????????????????????

        mChart.invalidate();

    }

    private void plotHz(ArrayList<Float>time){
        int isFirstTime=0;
        int sampleCount=0;
        ArrayList<Entry> secEntryList=new ArrayList<>();
        for (float t:time){
            if(isFirstTime!=(int) t){
                Log.i(TAG, "\nTime:"+String.valueOf(isFirstTime)+" s\nHz:"+String.valueOf(sampleCount)+" hz");
                secEntryList.add(new Entry(isFirstTime,sampleCount));
                isFirstTime=(int)t;
                sampleCount=0;
            }else{
                Log.i(TAG, String.valueOf(t));
                sampleCount++;
            }
        }
        //1?????????????????????
        if (isFirstTime==0){
            hzChart.setVisibility(View.GONE);
            TextView tvNotHz=findViewById(R.id.tv_not_Hz);
            tvNotHz.setVisibility(View.VISIBLE);
        }else {
            //DataSet???Entry?????????
            //????????????????????????????????????
            LineDataSet lineDataSet=new LineDataSet(secEntryList,"Hz");
            lineDataSet.getColor(getResources().getColor(android.R.color.holo_orange_light));
            lineDataSet.setLineWidth(2.0f);
            lineDataSet.setDrawCircles(false);
            lineDataSet.setDrawValues(false);

            //LineData
            LineData lineData=new LineData(lineDataSet);
            hzChart.setData(lineData);
            hzChart.getDescription().setEnabled(false);

            //??????
            Legend legend=hzChart.getLegend();
            legend.setEnabled(true);
            legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);//??????????????????
            legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);//??????????????????
            legend.setDrawInside(true);//?????????????????????????????????
            legend.setTextColor(Color.BLACK);
            legend.setTextSize(5f);

            //?????????????????????
            //x???
            XAxis xAxis=hzChart.getXAxis();
            xAxis.setEnabled(true);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawLabels(true);
            xAxis.setTextColor(Color.BLACK);

            //y???(??????)
            YAxis yAxis=hzChart.getAxisLeft();
            yAxis.setEnabled(true);
            yAxis.setTextColor(Color.BLACK);


            //y???(??????)
            hzChart.getAxisRight().setEnabled(false);//??????????????????????????????????????????????????????

            hzChart.invalidate();

        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_chart_options_list,menu);
        return true;
    }

    //??????????????????
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int itemId=item.getItemId();
        boolean returnVal=true;
        //????????????????????????
        switch (itemId){
            case R.id.menu_delete_file:
//              ????????????????????????
                DeleteFileDialogFragment dialogFragment=new DeleteFileDialogFragment(filename,ChartActivity.this);
                dialogFragment.show(getSupportFragmentManager(),"DeleteFileDialogFragment");
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }


}