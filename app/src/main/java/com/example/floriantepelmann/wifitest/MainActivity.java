package com.example.floriantepelmann.wifitest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.lang.*;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import android.media.MediaPlayer;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private ArrayAdapter adapter;
    private WifiManager wifiManager;
    private ArrayList<String> wifis;
    private SoundPool sound = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
    private long lastTime = System.currentTimeMillis();
    private int chapterFactor = 0;
    MediaPlayer drone;
    {
        drone = MediaPlayer.create(this, com.example.floriantepelmann.wifiwithstabledrone.R.raw.dronesame);
    }

    private float droneVolume = (float) 0.2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.floriantepelmann.wifiwithstabledrone.R.layout.activity_main);
        Button btn2 = findViewById(com.example.floriantepelmann.wifiwithstabledrone.R.id.btn2);
        btn2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                chapterFactor++;
                if (chapterFactor > 3){
                    chapterFactor = 0;
                }
                wifis.clear();
                wifis.add("Current chapter: " + chapterFactor);
                lastTime = System.currentTimeMillis();
            }
        });
        ListView list = findViewById(com.example.floriantepelmann.wifiwithstabledrone.R.id.listView);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        MyBroadCastReceiver myBroadCastReceiver = new MyBroadCastReceiver();
        registerReceiver(myBroadCastReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
        wifis = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, wifis);
        list.setAdapter(adapter);
        wifiManager.startScan();
        drone.start();
        drone.setVolume(droneVolume, droneVolume);
        drone.setLooping(true);
    }

    public int determineSoundFile(int soundCalc){
        double soundFile = soundCalc + 2131427329;
        return (int) soundFile;
    }

    private void playSound(final int soundNum, long startDelay, long interDelay) {
        final int soundId = sound.load(this, soundNum, 1);
        Handler firstHit = new Handler();
        firstHit.postDelayed(new Runnable() {
            public void run() {
                try {
                    sound.play(soundId, 1, 1, 0,0,1);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }, startDelay);
        Handler secondHit = new Handler();
        secondHit.postDelayed(new Runnable() {
            public void run() {
                try {
                    sound.play(soundId, 1, 1, 0,0,1);
                    sound.release();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }, (startDelay) + (interDelay));
    }

    private void fadeVolume(final float destVolume){
        final int fadeDuration = 1000;
        final int fadeInterval = 80;
        int numberOfSteps = fadeDuration / fadeInterval;
        final float deltaVolume = destVolume / (float)numberOfSteps;
        final Timer timer = new Timer(true);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                fadeStep(deltaVolume);
                if(droneVolume >= destVolume){
                    timer.cancel();
                    timer.purge();
                }
            }
        };
        timer.schedule(timerTask,fadeInterval,fadeInterval);
    }

    private void fadeStep(float deltaVolume){
        drone.setVolume(droneVolume, droneVolume);
        droneVolume += deltaVolume;
    }
    class MyBroadCastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            wifis.clear();
            List<ScanResult> results = wifiManager.getScanResults();
            Collections.sort(results, new ScanResultComparator());
            // List<Integer> playingSounds = new ArrayList<>();
            int droneMaxVolume = 60;
            int calcDroneVol = results.size();
            if (calcDroneVol >= 60){
                calcDroneVol = 60;
            }
            else if (calcDroneVol <= 10){
                calcDroneVol = 10;
            }
            final float droneVol = (float) (1 - (Math.log(droneMaxVolume - calcDroneVol) / Math.log(droneMaxVolume)));
            fadeVolume(droneVol);
            if(System.currentTimeMillis() - lastTime >= 180000){
                chapterFactor++;
                if (chapterFactor > 3){
                    chapterFactor = 0;
                }
                lastTime = System.currentTimeMillis();
            }
            int numberOfNetworks = results.size();
            int iterations = 5;
            if (numberOfNetworks < 5) {
                iterations = numberOfNetworks;
            }
            for (int i = 0; i < iterations; i++) {
                String bssid = results.get(i).BSSID;
                int adCalc = Math.abs(
                        Integer.decode("0x" + bssid.substring(0, 2)) -
                        Integer.decode("0x" + bssid.substring(3, 5)) +
                        Integer.decode("0x" + bssid.substring(6, 8)) -
                        Integer.decode("0x" + bssid.substring(9, 11)) +
                        Integer.decode("0x" + bssid.substring(12, 14)) -
                        Integer.decode("0x" + bssid.substring(15, 17))
                );
                int soundCalcPre = (int) Math.ceil(adCalc / 16);
                /* if (playingSounds.contains(soundCalcPre)){
                    soundCalcPre -= 1;
                    if(soundCalcPre <= 0){
                        soundCalcPre = 0;
                    }
                }
                playingSounds.add(soundCalcPre); */
                int soundCalc = soundCalcPre + chapterFactor * 32;
                long startDel = (long) Math.abs(Integer.decode("0x" + bssid.substring(12, 14)) * 7 + 100);
                long interDel = (long) Math.abs(Integer.decode("0x" + bssid.substring(15, 17)) * 7 + 1000);
                if (startDel >= 1000){
                    startDel = 1000;
                }
                if (interDel >= 2000){
                    interDel = 2000;
                }
                playSound(determineSoundFile(soundCalc), startDel, interDel);
                wifis.add(soundCalc + " " + startDel + " " + interDel);
            }
            wifis.add("Current Chapter: " + chapterFactor);
            wifis.add("Found Networks: " + results.size());
            wifis.add("Drone Volume: " + droneVol);
            adapter.notifyDataSetChanged();
            Handler nextScan = new Handler();
            nextScan.postDelayed(new Runnable() {
                public void run() {
                    try {
                        wifiManager.startScan();
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 2500);
        }
    }
}