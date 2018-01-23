package com.example.floriantepelmann.wifitest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.lang.*;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import android.media.MediaPlayer;

public class MainActivity extends AppCompatActivity {

    ArrayAdapter adapter;
    WifiManager wifiManager;
    ListView list;
    WifiInfo wifiInfo;
    ArrayList<String> wifis;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        list = findViewById(R.id.listView);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        MyBroadCastReceiver myBroadCastReceiver = new MyBroadCastReceiver();
        wifiInfo = wifiManager.getConnectionInfo();
        registerReceiver(myBroadCastReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
        wifis = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, wifis);
        list.setAdapter(adapter);
        MediaPlayer drone = MediaPlayer.create(this, R.raw.drone2);
        drone.start();
        drone.setLooping(true);
        wifiManager.startScan();
    }

    public int getMaxNum(List<ScanResult> results) {
        int maxNum = 15;
        if(results.size() < maxNum){
          maxNum = results.size();
        }
        return maxNum;
    }

    public int determineSoundFile(int idCalc){
        double soundVal = Math.ceil(idCalc / 10) + 2131427329;
        return (int) soundVal;
    }

    private void playSound(final int soundNum, long startDelay, long interDelay, int vol) {
        int maxVolume = 50;
        final MediaPlayer sound = MediaPlayer.create(this, soundNum);
        final float volume = (float) (1 - (Math.log(maxVolume - vol) / Math.log(maxVolume)));
        sound.setVolume(volume, volume);
        Handler firstHit = new Handler();
        firstHit.postDelayed(new Runnable() {
            public void run() {
                try {
                    sound.start();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }, startDelay);
        Handler secondHit = new Handler();
        secondHit.postDelayed(new Runnable() {
            public void run() {
                try {
                    sound.start();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }, (startDelay) + (interDelay));
        Handler thirdHit = new Handler();
        thirdHit.postDelayed(new Runnable() {
            public void run() {
                try {
                    sound.start();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }, (startDelay) + (interDelay) + (interDelay));
        Handler fourthHit = new Handler();
        fourthHit.postDelayed(new Runnable() {
            public void run() {
                try {
                    sound.start();
                    sound.release();
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
            for (int i = 0; i < getMaxNum(results); i++) {
                String bssid = results.get(i).BSSID;
                int vol = results.get(i).level + 100;
                int idCalc = Math.abs(Integer.decode("0x" + bssid.substring(0, 2))
                        - Integer.decode("0x" + bssid.substring(3, 5))
                        + Integer.decode("0x" + bssid.substring(6, 8))
                        - Integer.decode("0x" + bssid.substring(9, 11))
                        + Integer.decode("0x" + bssid.substring(12, 14))
                        - Integer.decode("0x" + bssid.substring(15, 17)));
                long startDel = (long) Integer.decode("0x" + bssid.substring(12, 14)) * 7 + 500;
                long interDel = (long) Integer.decode("0x" + bssid.substring(15, 17)) * 7 + 500;
                playSound(determineSoundFile(idCalc), startDel, interDel, vol);
                wifis.add("sound_" + (determineSoundFile(idCalc) - 2131427328) + " " + startDel + " " + interDel);
            }
            adapter.notifyDataSetChanged();
            Handler scanDelay = new Handler();
            scanDelay.postDelayed(new Runnable() {
                public void run() {
                    try {
                        wifiManager.startScan();
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 4000);
        }
    }
}

