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

public class MainActivity extends AppCompatActivity {

    private ArrayAdapter adapter;
    private WifiManager wifiManager;
    private ArrayList<String> wifis;
    private SoundPool sound = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
    private long lastTime = System.currentTimeMillis();
    private int chapterFactor = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.floriantepelmann.wifiwithstabledrone.R.layout.activity_main);
        Button btn2 = findViewById(com.example.floriantepelmann.wifiwithstabledrone.R.id.btn2);
        btn2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                chapterFactor++;
                if (chapterFactor > 5){
                    chapterFactor = 1;
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
        MediaPlayer drone = MediaPlayer.create(this, com.example.floriantepelmann.wifiwithstabledrone.R.raw.dronesame);
        drone.start();
        drone.setLooping(true);
        wifiManager.startScan();
    }

    public int determineSoundFile(int idCalc){
        double soundVal = Math.ceil(idCalc / 50) + 2131427329;
        return (int) soundVal;
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
        Handler thirdHit = new Handler();
        thirdHit.postDelayed(new Runnable() {
            public void run() {
                try {
                    sound.play(soundId, 1, 1, 0,0,1);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }, (startDelay) + (interDelay) + (interDelay));
        Handler fourthHit = new Handler();
        fourthHit.postDelayed(new Runnable() {
            public void run() {
                try {
                    sound.play(soundId, 1, 1, 0,0,1);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }, (startDelay) + (interDelay) + (interDelay) + (interDelay));
    }

    class MyBroadCastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            wifis.clear();
            List<ScanResult> results = wifiManager.getScanResults();
            Collections.sort(results, new ScanResultComparator());
            int numberOfNetworks = results.size();
            int iterations = 5;
            if (numberOfNetworks < 5) {
                iterations = numberOfNetworks;
            }
            for (int i = 0; i < iterations; i++) {
                String bssid = results.get(i).BSSID;
                int decA = 0;
                int decB = 0;
                int decC = 0;
                int decD = 0;
                int decE = 0;
                int decF = 0;
                String hexA = "0x" + bssid.substring(0, 2);
                try {
                    decA = Integer.decode(hexA);
                } catch(Exception e){
                    e.printStackTrace();
                }
                String hexB = "0x" + bssid.substring(3, 5);
                try {
                    decB = Integer.decode(hexB);
                } catch(Exception e){
                    e.printStackTrace();
                }
                String hexC = "0x" + bssid.substring(6, 8);
                try {
                    decC = Integer.decode(hexC);
                } catch(Exception e){
                    e.printStackTrace();
                }
                String hexD = "0x" + bssid.substring(9, 11);
                try {
                    decD = Integer.decode(hexD);
                } catch(Exception e){
                    e.printStackTrace();
                }
                String hexE = "0x" + bssid.substring(12, 14);
                try {
                    decE = Integer.decode(hexE);
                } catch(Exception e){
                    e.printStackTrace();
                }
                String hexF = "0x" + bssid.substring(15, 17);
                try {
                    decF = Integer.decode(hexF);
                } catch(Exception e){
                    e.printStackTrace();
                }
                int idCalc = Math.abs(decA - decB + decC - decD + decE - decF);
                if(idCalc > 600) {
                    idCalc = 400;
                }
                else if (idCalc > 500) {
                    idCalc = 300;
                }
                else if (idCalc > 400) {
                    idCalc = 200;
                }
                if(System.currentTimeMillis() - lastTime >= 180000){
                    chapterFactor++;
                    if (chapterFactor > 5){
                        chapterFactor = 1;
                    }
                    lastTime = System.currentTimeMillis();
                }
                long startDel = (long) Math.abs(decE * 7 + 500);
                long interDel = (long) Math.abs(decF * 7 + 500);
                playSound(determineSoundFile(idCalc * chapterFactor), startDel, interDel);
                wifis.add(numberOfNetworks + " " + idCalc + " " + chapterFactor + " " + idCalc * chapterFactor);
            }
            wifis.add("Current Chapter: " + chapterFactor);
            adapter.notifyDataSetChanged();
            wifiManager.startScan();
        }
    }
}