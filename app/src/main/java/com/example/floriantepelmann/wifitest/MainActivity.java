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
import android.media.MediaPlayer;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.example.floriantepelmann.wifiwithstabledrone.R;

public class MainActivity extends AppCompatActivity {

    private ArrayAdapter adapter;
    private WifiManager wifiManager;
    private ArrayList<String> wifis;
    private SoundPool sound = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
    private int chapterFactor = 0;
    private MediaPlayer drone;
    int numberOfNetworks = 0;
    float droneVol = 0;

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
        ListView list = findViewById(com.example.floriantepelmann.wifiwithstabledrone.R.id.listView);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        MyBroadCastReceiver myBroadCastReceiver = new MyBroadCastReceiver();
        registerReceiver(myBroadCastReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
        wifis = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, wifis);
        list.setAdapter(adapter);
        wifiManager.startScan();
        drone = MediaPlayer.create(this, R.raw.dronesame);
        drone.start();
        drone.setVolume((float) 0, (float) 0);
        drone.setLooping(true);
    }

    private int avoidDuplicateSounds(List playingSounds, int soundCalcPre){
        soundCalcPre--;
        if(soundCalcPre <= 0){
            soundCalcPre = 30;
        }
        if(playingSounds.contains(soundCalcPre)){
            soundCalcPre = avoidDuplicateSounds(playingSounds, soundCalcPre);
        }
        return soundCalcPre;
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
            if (numberOfNetworks <= 10){
                droneVol = (float) 0.4;
            }
            else if (numberOfNetworks <= 20){
                droneVol = (float) 0.5;
            }
            else if (numberOfNetworks <= 30){
                droneVol = (float) 0.6;
            }
            else if (numberOfNetworks <= 40){
                droneVol = (float) 0.7;
            }
            else if (numberOfNetworks <= 50){
                droneVol = (float) 0.8;
            }
            else if (numberOfNetworks <= 60){
                droneVol = (float) 0.9;
            }
            else if (numberOfNetworks > 60) {
                droneVol = (float) 1;
            }
            drone.setVolume(droneVol, droneVol);
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
                int soundCalcPre = (int) Math.ceil(adCalc / 16);
                if(playingSounds.contains(soundCalcPre)){
                    soundCalcPre = avoidDuplicateSounds(playingSounds, soundCalcPre);
                }
                playingSounds.add(soundCalcPre);
                int soundCalc = soundCalcPre + chapterFactor * 32;
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