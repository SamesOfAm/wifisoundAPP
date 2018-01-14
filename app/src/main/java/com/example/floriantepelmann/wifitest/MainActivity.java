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
        wifis.add("lädt...");
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, wifis);
        list.setAdapter(adapter);
        MediaPlayer drone = MediaPlayer.create(this, R.raw.drone2);
        drone.start();
        drone.setLooping(true);
        wifiManager.startScan();
    }

    public static boolean isBetween(int x, int lower, int upper) {
        return lower <= x && x <= upper;
    }

    private void playSound(final int soundFile, long startDelay, long interDelay, int vols) {
        int MAX_VOLUME = 100;
        int vol = vols;
        final MediaPlayer sound_01 = MediaPlayer.create(this, soundFile);
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
        }, startDelay * 100);
        Handler nHandler = new Handler();
        nHandler.postDelayed(new Runnable() {
            public void run() {
                try {
                    sound_01.start();
                    sound_01.release();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }, (startDelay * 100) + (interDelay * 100));
        Handler oHandler = new Handler();
        oHandler.postDelayed(new Runnable() {
            public void run() {
                try {
                    sound_01.start();
                    sound_01.release();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }, (startDelay * 100) + (interDelay * 100) + (interDelay * 100));
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
        }, (startDelay * 100) + (interDelay * 100) + (interDelay * 100) + (interDelay * 100));
    }
    class MyBroadCastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            List<ScanResult> list = wifiManager.getScanResults();
            wifis.clear();
            for (int i = 0; i < list.size(); i++) {
                String bssid =  list.get(i).BSSID;
                int rssi =  list.get(i).level;
                int vol = rssi + 50;
                int idCalc = Math.abs(Integer.decode("0x" + bssid.substring(0, 2))
                        - Integer.decode("0x" + bssid.substring(3, 5))
                        + Integer.decode("0x" + bssid.substring(6, 8))
                        - Integer.decode("0x" + bssid.substring(9, 11))
                        + Integer.decode("0x" + bssid.substring(12, 14))
                        - Integer.decode("0x" + bssid.substring(15, 17)));
                if(isBetween(idCalc, 0, 9)){
                    playSound(R.raw.sound_01, (long)(Math.random() * 40) + 1, (long)(Math.random() * 40) + 1, vol);
                }
                else if(isBetween(idCalc, 10, 19)) {
                    playSound(R.raw.sound_02, (long)(Math.random() * 40) + 1, (long)(Math.random() * 40) + 1, vol);
                }
                else if(isBetween(idCalc, 20, 29)) {
                    playSound(R.raw.sound_03, (long)(Math.random() * 40) + 1, (long)(Math.random() * 40) + 1, vol);
                }
                else if(isBetween(idCalc, 30, 39)) {
                    playSound(R.raw.sound_04, (long)(Math.random() * 40) + 1, (long)(Math.random() * 40) + 1, vol);
                }
                else if(isBetween(idCalc, 40, 49)) {
                    playSound(R.raw.sound_05, (long)(Math.random() * 40) + 1, (long)(Math.random() * 40) + 1, vol);
                }
                else if(isBetween(idCalc, 50, 59)) {
                    playSound(R.raw.sound_06, (long)(Math.random() * 40) + 1, (long)(Math.random() * 40) + 1, vol);
                }
                else if(isBetween(idCalc, 60, 69)) {
                    playSound(R.raw.sound_09, (long)(Math.random() * 40) + 1, (long)(Math.random() * 40) + 1, vol);
                }
                else if(isBetween(idCalc, 70, 79)) {
                    playSound(R.raw.sound_10, (long)(Math.random() * 40) + 1, (long)(Math.random() * 40) + 1, vol);
                }
                else if(isBetween(idCalc, 80, 89)) {
                    playSound(R.raw.sound_11, (long)(Math.random() * 40) + 1, (long)(Math.random() * 40) + 1, vol);
                }
                else if(isBetween(idCalc, 90, 99)) {
                    playSound(R.raw.sound_12, (long)(Math.random() * 40) + 1, (long)(Math.random() * 40) + 1, vol);
                }
                else if(isBetween(idCalc, 100, 109)) {
                    playSound(R.raw.sound_13, (long)(Math.random() * 40) + 1, (long)(Math.random() * 40) + 1, vol);
                }
                else if(isBetween(idCalc, 110, 119)) {
                    playSound(R.raw.sound_14, (long)(Math.random() * 40) + 1, (long)(Math.random() * 40) + 1, vol);
                }
                else if(isBetween(idCalc, 120, 129)) {
                    playSound(R.raw.sound_15, (long)(Math.random() * 40) + 1, (long)(Math.random() * 40) + 1, vol);
                }
                else if(isBetween(idCalc, 130, 139)) {
                    playSound(R.raw.sound_16, (long)(Math.random() * 40) + 1, (long)(Math.random() * 40) + 1, vol);
                }
                else if(isBetween(idCalc, 140, 149)) {
                    playSound(R.raw.sound_17, (long)(Math.random() * 40) + 1, (long)(Math.random() * 40) + 1, vol);
                }
                else if(isBetween(idCalc, 150, 159)) {
                    playSound(R.raw.sound_18, (long)(Math.random() * 40) + 1, (long)(Math.random() * 40) + 1, vol);
                }
                else if(isBetween(idCalc, 160, 169)) {
                    playSound(R.raw.sound_20, (long)(Math.random() * 40) + 1, (long)(Math.random() * 40) + 1, vol);
                }
                else if(isBetween(idCalc, 170, 179)) {
                    playSound(R.raw.sound_21, (long)(Math.random() * 40) + 1, (long)(Math.random() * 40) + 1, vol);
                }
                else if(isBetween(idCalc, 180, 189)) {
                    playSound(R.raw.sound_22, (long)(Math.random() * 40) + 1, (long)(Math.random() * 40) + 1, vol);
                }
                else if(isBetween(idCalc, 190, 199)) {
                    playSound(R.raw.sound_23, (long)(Math.random() * 40) + 1, (long)(Math.random() * 40) + 1, vol);
                }
                else if(isBetween(idCalc, 200, 209)) {
                    playSound(R.raw.sound_24, (long)(Math.random() * 40) + 1, (long)(Math.random() * 40) + 1, vol);
                }
                else if(isBetween(idCalc, 210, 219)) {
                    playSound(R.raw.sound_25, (long)(Math.random() * 40) + 1, (long)(Math.random() * 40) + 1, vol);
                }
                else if(isBetween(idCalc, 220, 229)) {
                    playSound(R.raw.sound_26, (long)(Math.random() * 40) + 1, (long)(Math.random() * 40) + 1, vol);
                }
                else if(isBetween(idCalc, 230, 239)) {
                    playSound(R.raw.sound_27, (long)(Math.random() * 40) + 1, (long)(Math.random() * 40) + 1, vol);
                }
                else if(isBetween(idCalc, 240, 249)) {
                    playSound(R.raw.sound_28, (long)(Math.random() * 40) + 1, (long)(Math.random() * 40) + 1, vol);
                }
                else if(isBetween(idCalc, 250, 259)) {
                    playSound(R.raw.sound_29, (long)(Math.random() * 40) + 1, (long)(Math.random() * 40) + 1, vol);
                }
                else if(isBetween(idCalc, 260, 269)) {
                    playSound(R.raw.sound_30, (long)(Math.random() * 40) + 1, (long)(Math.random() * 40) + 1, vol);
                }
                else if(isBetween(idCalc, 270, 279)) {
                    playSound(R.raw.sound_31, (long)(Math.random() * 40) + 1, (long)(Math.random() * 40) + 1, vol);
                }
                else if(isBetween(idCalc, 280, 289)) {
                    playSound(R.raw.sound_32, (long)(Math.random() * 40) + 1, (long)(Math.random() * 40) + 1, vol);
                }
                else if(isBetween(idCalc, 290, 299)) {
                    playSound(R.raw.sound_33, (long)(Math.random() * 40) + 1, (long)(Math.random() * 40) + 1, vol);
                }
                else if(isBetween(idCalc, 300, 309)) {
                    playSound(R.raw.sound_35, (long)(Math.random() * 40) + 1, (long)(Math.random() * 40) + 1, vol);
                }
                else if(isBetween(idCalc, 310, 319)) {
                    playSound(R.raw.sound_36, (long)(Math.random() * 40) + 1, (long)(Math.random() * 40) + 1, vol);
                }
                else if(isBetween(idCalc, 320, 329)) {
                    playSound(R.raw.sound_37, (long)(Math.random() * 40) + 1, (long)(Math.random() * 40) + 1, vol);
                }
                else if(isBetween(idCalc, 330, 339)) {
                    playSound(R.raw.sound_38, (long)(Math.random() * 40) + 1, (long)(Math.random() * 40) + 1, vol);
                }
                else if(isBetween(idCalc, 340, 349)) {
                    playSound(R.raw.sound_39, (long)(Math.random() * 40) + 1, (long)(Math.random() * 40) + 1, vol);
                }
                else if(isBetween(idCalc, 350, 359)) {
                    playSound(R.raw.sound_40, (long)(Math.random() * 40) + 1, (long)(Math.random() * 40) + 1, vol);
                }
                else if(isBetween(idCalc, 360, 369)) {
                    playSound(R.raw.sound_41, (long)(Math.random() * 40) + 1, (long)(Math.random() * 40) + 1, vol);
                }

                wifis.add(bssid + " " + vol + " " + idCalc);

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
            }, 2000);
        }
    }
}

