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
import java.util.Timer;
import java.util.TimerTask;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import android.media.MediaPlayer;

public class MainActivity extends AppCompatActivity {

    private ArrayAdapter adapter;
    private WifiManager wifiManager;
    private WifiInfo wifiInfo;
    private ArrayList<String> wifis;
    private MediaPlayer drone;
    private float currentDroneVolume = (float) 0.2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView list = findViewById(R.id.listView);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        MyBroadCastReceiver myBroadCastReceiver = new MyBroadCastReceiver();
        wifiInfo = wifiManager.getConnectionInfo();
        registerReceiver(myBroadCastReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
        wifis = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, wifis);
        list.setAdapter(adapter);
        wifiManager.startScan();
        drone = MediaPlayer.create(this, R.raw.drone);
        drone.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int k) {
                System.out.println("Error what= " + i + " extra= " + k);
                return false;
            }
        });
        drone.start();
        drone.setVolume(currentDroneVolume, currentDroneVolume);
        drone.setLooping(true);
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
        int maxVolume = 55;
        final MediaPlayer sound = MediaPlayer.create(this, soundNum);
        sound.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int k) {
                System.out.println("Error what= " + i + " extra= " + k);
                return false;
            }
        });
        final float volume = (float) ((Math.log(maxVolume - vol) / Math.log(maxVolume)));
        sound.setVolume(1 - volume, 1- volume);
        Handler firstHit = new Handler();
        firstHit.postDelayed(new Runnable() {
            public void run() {
                try {
                    sound.start();
                    sound.release();
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
                    sound.release();
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
                    sound.release();
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

    private void fadeVolume(final float currVol, final float destVolume){
        final int fadeDuration = 1000;
        final int fadeInterval = 80;
        final int numberOfSteps = fadeDuration / fadeInterval;
        final float volumeDifference = destVolume - currVol;
        final float deltaVolume;

        final float volumeStep = volumeDifference / (float) numberOfSteps;
        if(volumeDifference > 0) {
            deltaVolume = volumeStep + currentDroneVolume;
        } else {
            deltaVolume = volumeStep - currentDroneVolume;
        }

        final Timer timer = new Timer(true);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                fadeStep(deltaVolume);
                if(currentDroneVolume >= destVolume){
                    timer.cancel();
                    timer.purge();
                }
            }
        };
        timer.schedule(timerTask,fadeInterval,fadeInterval);
    }

    private void fadeStep(float deltaVolume){
        drone.setVolume(deltaVolume, deltaVolume);
        currentDroneVolume = deltaVolume;
        System.out.println("ZZZ " + currentDroneVolume);
    }

    class MyBroadCastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            wifis.clear();
            List<ScanResult> results = wifiManager.getScanResults();
            Collections.sort(results, new ScanResultComparator());
            int maxDroneVolume = 14;
            int numberOfNetworks = getMaxNum(results);
            final float nextDroneVolume = (float) ((Math.log(maxDroneVolume - numberOfNetworks) / Math.log(maxDroneVolume)));
            System.out.println("ZZZ " + numberOfNetworks + " : " + (1 - nextDroneVolume));
            fadeVolume(currentDroneVolume,1 - nextDroneVolume);
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
                wifis.add(currentDroneVolume + " " + " sound_" + (determineSoundFile(idCalc) - 2131427328) + " " + vol);
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
            }, 1000);
        }
    }
}

