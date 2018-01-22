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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import android.media.MediaPlayer;
// import android.os.SystemClock;

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
        // wifis.add("-" + SystemClock.elapsedRealtime());
        wifis = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, wifis);
        list.setAdapter(adapter);
        MediaPlayer drone = MediaPlayer.create(this, R.raw.drone2);
        drone.start();
        drone.setLooping(true);
        wifiManager.startScan();
    }

    // public long timeDifference = 0;
    public static boolean isBetween(int x, int lower, int upper) {
        return lower <= x && x <= upper;
    }

    public int getMaxNum(List<ScanResult> results) {
        int maxNum = 15;
        if(results.size() < 15){
          maxNum = results.size();
        }
        return maxNum;
    }

    public int determineSoundFile(int idCalc){
        double soundVal = Math.ceil(idCalc / 10) + 2131427329;
        int soundNum = (int) soundVal;
        return soundNum;
    }

    private void playSound(final int soundNum, long startDelay, long interDelay, int vol) {
        int MAX_VOLUME = 100;
        final MediaPlayer sound_01 = MediaPlayer.create(this, soundNum);
        final float volume = (float) (1 - (Math.log(MAX_VOLUME + vol / Math.log(MAX_VOLUME))));
        sound_01.setVolume(volume, volume);
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            public void run() {
                try {
                    sound_01.start();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }, startDelay);
        Handler nHandler = new Handler();
        nHandler.postDelayed(new Runnable() {
            public void run() {
                try {
                    sound_01.start();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }, (startDelay) + (interDelay));
        Handler oHandler = new Handler();
        oHandler.postDelayed(new Runnable() {
            public void run() {
                try {
                    sound_01.start();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }, (startDelay) + (interDelay) + (interDelay));
        Handler pHandler = new Handler();
        pHandler.postDelayed(new Runnable() {
            public void run() {
                try {
                    sound_01.start();
                    sound_01.release();
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
            // timeDifference = SystemClock.elapsedRealtime() - timeDifference;
            // wifis.add(timeDifference + " ");
            // timeDifference = SystemClock.elapsedRealtime();
            List<ScanResult> results = wifiManager.getScanResults();
            Collections.sort(results, new ScanResultComparator());

            for (int i = 0; i < getMaxNum(results); i++) {
                String bssid = results.get(i).BSSID;
                int rssi = results.get(i).level;
                int vol = rssi + 50;
                int idCalc = Math.abs(Integer.decode("0x" + bssid.substring(0, 2))
                        - Integer.decode("0x" + bssid.substring(3, 5))
                        + Integer.decode("0x" + bssid.substring(6, 8))
                        - Integer.decode("0x" + bssid.substring(9, 11))
                        + Integer.decode("0x" + bssid.substring(12, 14))
                        - Integer.decode("0x" + bssid.substring(15, 17)));
                long startDel = (long) Integer.decode("0x" + bssid.substring(12, 14)) * 5;
                long interDel = (long) Integer.decode("0x" + bssid.substring(15, 17)) * 5;


                playSound(determineSoundFile(idCalc), startDel, interDel, vol);

                /* if (isBetween(idCalc, 0, 9)) {
                    playSound(R.raw.sound_01, startDel, interDel, vol);
                } else if (isBetween(idCalc, 10, 19)) {
                    playSound(R.raw.sound_02, startDel, interDel, vol);
                } else if (isBetween(idCalc, 20, 29)) {
                    playSound(R.raw.sound_03, startDel, interDel, vol);
                } else if (isBetween(idCalc, 30, 39)) {
                    playSound(R.raw.sound_04, startDel, interDel, vol);
                } else if (isBetween(idCalc, 40, 49)) {
                    playSound(R.raw.sound_05, startDel, interDel, vol);
                } else if (isBetween(idCalc, 50, 59)) {
                    playSound(R.raw.sound_06, startDel, interDel, vol);
                } else if (isBetween(idCalc, 60, 69)) {
                    playSound(R.raw.sound_09, startDel, interDel, vol);
                } else if (isBetween(idCalc, 70, 79)) {
                    playSound(R.raw.sound_10, startDel, interDel, vol);
                } else if (isBetween(idCalc, 80, 89)) {
                    playSound(R.raw.sound_11, startDel, interDel, vol);
                } else if (isBetween(idCalc, 90, 99)) {
                    playSound(R.raw.sound_12, startDel, interDel, vol);
                } else if (isBetween(idCalc, 100, 109)) {
                    playSound(R.raw.sound_13, startDel, interDel, vol);
                } else if (isBetween(idCalc, 110, 119)) {
                    playSound(R.raw.sound_14, startDel, interDel, vol);
                } else if (isBetween(idCalc, 120, 129)) {
                    playSound(R.raw.sound_15, startDel, interDel, vol);
                } else if (isBetween(idCalc, 130, 139)) {
                    playSound(R.raw.sound_16, startDel, interDel, vol);
                } else if (isBetween(idCalc, 140, 149)) {
                    playSound(R.raw.sound_17, startDel, interDel, vol);
                } else if (isBetween(idCalc, 150, 159)) {
                    playSound(R.raw.sound_18, startDel, interDel, vol);
                } else if (isBetween(idCalc, 160, 169)) {
                    playSound(R.raw.sound_20, startDel, interDel, vol);
                } else if (isBetween(idCalc, 170, 179)) {
                    playSound(R.raw.sound_21, startDel, interDel, vol);
                } else if (isBetween(idCalc, 180, 189)) {
                    playSound(R.raw.sound_22, startDel, interDel, vol);
                } else if (isBetween(idCalc, 190, 199)) {
                    playSound(R.raw.sound_23, startDel, interDel, vol);
                } else if (isBetween(idCalc, 200, 209)) {
                    playSound(R.raw.sound_24, startDel, interDel, vol);
                } else if (isBetween(idCalc, 210, 219)) {
                    playSound(R.raw.sound_25, startDel, interDel, vol);
                } else if (isBetween(idCalc, 220, 229)) {
                    playSound(R.raw.sound_26, startDel, interDel, vol);
                } else if (isBetween(idCalc, 230, 239)) {
                    playSound(R.raw.sound_27, startDel, interDel, vol);
                } else if (isBetween(idCalc, 240, 249)) {
                    playSound(R.raw.sound_28, startDel, interDel, vol);
                } else if (isBetween(idCalc, 250, 259)) {
                    playSound(R.raw.sound_29, startDel, interDel, vol);
                } else if (isBetween(idCalc, 260, 269)) {
                    playSound(R.raw.sound_30, startDel, interDel, vol);
                } else if (isBetween(idCalc, 270, 279)) {
                    playSound(R.raw.sound_31, startDel, interDel, vol);
                } else if (isBetween(idCalc, 280, 289)) {
                    playSound(R.raw.sound_32, startDel, interDel, vol);
                } else if (isBetween(idCalc, 290, 299)) {
                    playSound(R.raw.sound_33, startDel, interDel, vol);
                } else if (isBetween(idCalc, 300, 309)) {
                    playSound(R.raw.sound_35, startDel, interDel, vol);
                } else if (isBetween(idCalc, 310, 319)) {
                    playSound(R.raw.sound_36, startDel, interDel, vol);
                } else if (isBetween(idCalc, 320, 329)) {
                    playSound(R.raw.sound_37, startDel, interDel, vol);
                } else if (isBetween(idCalc, 330, 339)) {
                    playSound(R.raw.sound_38, startDel, interDel, vol);
                } else if (isBetween(idCalc, 340, 349)) {
                    playSound(R.raw.sound_39, startDel, interDel, vol);
                } else if (isBetween(idCalc, 350, 359)) {
                    playSound(R.raw.sound_40, startDel, interDel, vol);
                } else if (isBetween(idCalc, 360, 369)) {
                    playSound(R.raw.sound_41, startDel, interDel, vol);
                } */

                wifis.add(bssid + " " + vol + " " + determineSoundFile(idCalc));
            }

            adapter.notifyDataSetChanged();

            Handler qHandler = new Handler();
            qHandler.postDelayed(new Runnable() {
                public void run() {
                    try {
                        wifiManager.startScan();
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 3000);
        }
    }
}

