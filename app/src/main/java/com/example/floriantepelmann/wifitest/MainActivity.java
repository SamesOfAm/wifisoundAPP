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
import java.util.Timer;
import java.util.TimerTask;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import android.media.MediaPlayer;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.example.floriantepelmann.wifiwithstabledrone.R;

public class MainActivity extends AppCompatActivity {

    private ArrayAdapter adapter;
    private WifiManager wifiManager;
    private ArrayList<String> wifis;
    private SoundPool sound = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
    private int chapterFactor = 0;
    private MediaPlayer drone;
    int numberOfNetworks = 0;
    float droneVol = 0;
    boolean stop = false;
    Handler nextScan = new Handler();
    Handler firstHit = new Handler();
    Handler secondHit = new Handler();

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
            }
        });
        Button btn3 = findViewById(com.example.floriantepelmann.wifiwithstabledrone.R.id.btn3);
        btn3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                stop = false;
                Handler firstScan = new Handler();
                firstScan.postDelayed(new Runnable() {
                    public void run() {
                        try {
                            wifiManager.startScan();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, 3000);
                drone.start();
                drone.setVolume((float) 0.16, (float) 0.16);
                drone.setLooping(true);
            }
        });
        Button btn4 = findViewById(com.example.floriantepelmann.wifiwithstabledrone.R.id.btn4);
        btn4.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                stop = true;
                try {
                        fadeVolume(0);
                    nextScan.removeCallbacksAndMessages(null);
                    firstHit.removeCallbacksAndMessages(null);
                    secondHit.removeCallbacksAndMessages(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        ListView list = findViewById(com.example.floriantepelmann.wifiwithstabledrone.R.id.listView);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        MyBroadCastReceiver myBroadCastReceiver = new MyBroadCastReceiver();
        registerReceiver(myBroadCastReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifis = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, wifis);
        list.setAdapter(adapter);
        drone = MediaPlayer.create(this, R.raw.dronesame);
    }

    private int avoidDuplicateSounds(List playingSounds, int soundCalcPre){
        soundCalcPre--;
        if(soundCalcPre <= 0){
            soundCalcPre = 28;
        }
        if(playingSounds.contains(soundCalcPre)){
            soundCalcPre = avoidDuplicateSounds(playingSounds, soundCalcPre);
        }
        return soundCalcPre;
    }

    private void fadeVolume(final float destVolume){
        final int fadeDuration = 1000;
        final int fadeInterval = 80;
        final int numberOfSteps = fadeDuration / fadeInterval;
        final float volumeDifference = destVolume - droneVol;
        final boolean isFadingUp;

        final float volumeStep = Math.abs(volumeDifference / numberOfSteps);
        isFadingUp = volumeDifference > 0;

        final Timer timer = new Timer(true);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                fadeStep(volumeStep, isFadingUp);
                if((isFadingUp && droneVol >= destVolume) || (!isFadingUp && droneVol <= destVolume)){
                    timer.cancel();
                    timer.purge();
                }
            }
        };
        timer.schedule(timerTask,fadeInterval,fadeInterval);
    }

    private void fadeStep(float volumeStep, boolean fadeUp){
        final float deltaVolume;
        if(fadeUp){
            deltaVolume = droneVol + volumeStep;
        }
        else{
            deltaVolume = droneVol - volumeStep;
        }
        drone.setVolume(deltaVolume, deltaVolume);
        droneVol = deltaVolume;
        System.out.println("ZZZ " + droneVol);
    }

    public int determineSoundFile(int soundCalc){
        double soundFile = soundCalc + 2131427329;
        return (int) soundFile;
    }

    private void playSound(final int soundNum, long startDelay, long interDelay) {
        final int soundId = sound.load(this, soundNum, 1);
        firstHit.postDelayed(new Runnable() {
            public void run() {
                try {
                    sound.play(soundId, 1, 1, 0,0,1);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }, startDelay);
        secondHit.postDelayed(new Runnable() {
            public void run() {
                try {
                    sound.play(soundId, 1, 1, 0,0,1);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }, (startDelay) + (interDelay));
    }

    class MyBroadCastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            wifis.clear();
            List<ScanResult> results = wifiManager.getScanResults();
            Collections.sort(results, new ScanResultComparator());
            List<Integer> playingSounds = new ArrayList<>();
            numberOfNetworks = results.size();
            int droneVolNum = results.size();
            if (droneVolNum > 60){
                droneVolNum = 60;
            }
            if (droneVolNum < 10) {
                droneVolNum = 10;
            }
            droneVol = (float) droneVolNum / 60;
            fadeVolume(droneVol);
            int iterations = 5;
            if (numberOfNetworks < 5) {
                iterations = results.size();
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
                int soundCalcPre = (int) Math.ceil(adCalc / 27);
                if(playingSounds.contains(soundCalcPre)){
                    soundCalcPre = avoidDuplicateSounds(playingSounds, soundCalcPre);
                }
                playingSounds.add(soundCalcPre);
                int soundCalc = soundCalcPre + chapterFactor * 28;
                if(soundCalc > 127){
                    soundCalc = 127;
                }
                int randTime = 100 + (int)(Math.random() * ((1000 - 100) + 1));
                long startDel = (long) Math.abs(Integer.decode("0x" + bssid.substring(12, 14)) * 7 + randTime);
                long interDel = (long) Math.abs(Integer.decode("0x" + bssid.substring(15, 17)) * 7 + 1000);
                playSound(determineSoundFile(soundCalc), startDel, interDel);
                wifis.add(soundCalc + " " + randTime + " " + startDel + " " + interDel);
            }
            wifis.add("Current Chapter: " + chapterFactor);
            wifis.add("Found Networks: " + numberOfNetworks);
            wifis.add("Drone Volume: " + droneVol);
            wifis.add("All Sounds: " + playingSounds);
            adapter.notifyDataSetChanged();
            if(!stop) {
                nextScan.postDelayed(new Runnable() {
                    public void run() {
                        try {
                            wifiManager.startScan();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, 2500);
            }
        }
    }
}