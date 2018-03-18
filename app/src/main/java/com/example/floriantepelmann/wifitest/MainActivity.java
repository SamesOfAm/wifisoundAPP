package com.example.floriantepelmann.wifitest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

public class MainActivity extends AppCompatActivity {

    private ArrayAdapter adapter;
    private WifiManager wifiManager;
    private ArrayList<String> wifis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.floriantepelmann.wifiwithstabledrone.R.layout.activity_main);
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
        double soundVal = Math.ceil(idCalc / 10) + 2131427329;
        return (int) soundVal;
    }

    private void playSound(final int soundNum, long startDelay, long interDelay, int vol) {
        // int maxVolume = 55;
        final MediaPlayer sound = MediaPlayer.create(this, soundNum);
        sound.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int k) {
                System.out.println("ZZZ Error what= " + i + " extra= " + k);
                return false;
            }
        });
        // final float volume = (float) ((Math.log(maxVolume - vol) / Math.log(maxVolume)));
        // sound.setVolume(1 - volume, 1- volume);
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
            int numberOfNetworks = results.size();
            int iterations = 5;
            if (numberOfNetworks < 5) {
                iterations = numberOfNetworks;
            }
            for (int i = 0; i < iterations; i++) {
                String bssid = results.get(i).BSSID;
                int vol = results.get(i).level + 100;
                int idCalc = Math.abs(
                        Integer.decode("0x" + bssid.substring(0, 2)) -
                        Integer.decode("0x" + bssid.substring(3, 5)) +
                        Integer.decode("0x" + bssid.substring(6, 8)) -
                        Integer.decode("0x" + bssid.substring(9, 11)) +
                        Integer.decode("0x" + bssid.substring(12, 14)) -
                        Integer.decode("0x" + bssid.substring(15, 17))
                );
                long startDel = (long) Integer.decode("0x" + bssid.substring(12, 14)) * 7 + 500;
                long interDel = (long) Integer.decode("0x" + bssid.substring(15, 17)) * 7 + 500;
                playSound(determineSoundFile(idCalc), startDel, interDel, vol);
                wifis.add(numberOfNetworks + " sound_" + (determineSoundFile(idCalc) - 2131427328) + " " + vol + " " + (1 - (float) (Math.log(55 - vol) / Math.log(55))));
            }
            adapter.notifyDataSetChanged();
            wifiManager.startScan();
        }
    }
}