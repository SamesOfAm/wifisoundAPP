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
    private WifiInfo wifiInfo;
    private ArrayList<String> wifis;

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
    }

    public int determineSoundFile(int idCalc){
        double soundVal = Math.ceil(idCalc / 10) + 2131427329;
        return (int) soundVal;
    }

    private void playSound(final int soundNum, int vol) {
        int maxVolume = 55;
        final float volume = (1 - (float) (Math.log(maxVolume - vol) / Math.log(maxVolume)));
        final MediaPlayer sound = MediaPlayer.create(this, soundNum);
        sound.setVolume(volume, volume);
        sound.start();
    }

    class MyBroadCastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            wifis.clear();
            List<ScanResult> results = wifiManager.getScanResults();
            Collections.sort(results, new ScanResultComparator());
            int numberOfNetworks = results.size();
                String bssid = results.get(0).BSSID;
                int vol = results.get(0).level + 100;
                int idCalc = Math.abs(Integer.decode("0x" + bssid.substring(0, 2))
                        - Integer.decode("0x" + bssid.substring(3, 5))
                        + Integer.decode("0x" + bssid.substring(6, 8))
                        - Integer.decode("0x" + bssid.substring(9, 11))
                        + Integer.decode("0x" + bssid.substring(12, 14))
                        - Integer.decode("0x" + bssid.substring(15, 17)));
                playSound(determineSoundFile(idCalc), vol);
                wifis.add(numberOfNetworks + " sound_" + (determineSoundFile(idCalc) - 2131427328) + " " + vol + " " + (1 - (float) (Math.log(55 - vol) / Math.log(55))));
                adapter.notifyDataSetChanged();
                wifiManager.startScan();
        }
    }
}

