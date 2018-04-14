package com.example.floriantepelmann.wifitest;

import java.lang.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
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
import android.media.MediaPlayer;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.example.floriantepelmann.wifiwithstabledrone.R;

public class MainActivity extends AppCompatActivity {

    private ArrayAdapter adapter;
    private AudioManager audioManager;
    private WifiManager wifiManager;
    private ArrayList<String> wifis;
    private SoundPool sound = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
    private int chapterFactor = 0;
    private MediaPlayer drone;
    int numberOfNetworks = 0;
    float droneVol = 0;
    boolean stop = true;
    boolean stopping = false;
    boolean starting = false;
    boolean muted = false;
    Handler nextScan = new Handler();
    Handler firstHit = new Handler();
    Handler secondHit = new Handler();
    Handler finish = new Handler();
    Handler stopDrone = new Handler();
    Handler fadeDrone = new Handler();
    private long lastTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        super.onCreate(savedInstanceState);
        setContentView(com.example.floriantepelmann.wifiwithstabledrone.R.layout.activity_main);
        ListView list = findViewById(com.example.floriantepelmann.wifiwithstabledrone.R.id.listView);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        MyBroadCastReceiver myBroadCastReceiver = new MyBroadCastReceiver();
        registerReceiver(myBroadCastReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifis = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, wifis);
        list.setAdapter(adapter);
        drone = MediaPlayer.create(this, R.raw.dronesame);
        final Button chapter = findViewById(com.example.floriantepelmann.wifiwithstabledrone.R.id.chapter);
        chapter.setSoundEffectsEnabled(false);
        chapter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!stop && !stopping && !starting){
                    chapterFactor++;
                    if (chapterFactor > 3) {
                        chapterFactor = 0;
                    }
                    lastTime = System.currentTimeMillis();
                    chapter.setText(R.string.changing);
                    Handler resetButtonText = new Handler();
                    resetButtonText.postDelayed(new Runnable() {
                        public void run() {
                            try {
                                chapter.setText(R.string.next_chapter);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, 2500);
                }
            }
        });
        final Button mute = findViewById(com.example.floriantepelmann.wifiwithstabledrone.R.id.mute);
        mute.setSoundEffectsEnabled(false);
        mute.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!stop) {
                    if (!muted) {
                        mute.setText(R.string.unmute);
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                        muted = true;
                    } else {
                        mute.setText(R.string.mute);
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 15, 0);
                        muted = false;
                    }
                }
            }
        });
        final Button start = findViewById(com.example.floriantepelmann.wifiwithstabledrone.R.id.start);
        start.setSoundEffectsEnabled(false);
        start.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(stop){
                    stop = false;
                    starting = true;
                    lastTime = System.currentTimeMillis();
                    Handler firstScan = new Handler();
                    firstScan.postDelayed(new Runnable() {
                        public void run() {
                            try {
                                wifiManager.startScan();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, 5000);
                    Handler startingIsOver = new Handler();
                    startingIsOver.postDelayed(new Runnable() {
                        public void run() {
                            try {
                                starting = false;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, 9000);
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 15, 0);
                    drone.setVolume(0, 0);
                    drone.start();
                    fadeVolume(1);
                    drone.setLooping(true);
                }
            }
        });
        final Button end = findViewById(com.example.floriantepelmann.wifiwithstabledrone.R.id.end);
        end.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!stop && !stopping && !starting){
                    stopping = true;
                    nextScan.removeCallbacksAndMessages(null);
                    firstHit.removeCallbacksAndMessages(null);
                    secondHit.removeCallbacksAndMessages(null);
                    fadeDrone.postDelayed(new Runnable() {
                        public void run() {
                            try {
                                fadeVolume(0);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, 10000);
                    stopDrone.postDelayed(new Runnable() {
                        public void run() {
                            try {
                                drone.setVolume(0, 0);
                                droneVol = 0;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, 19000);
                    end.setText(R.string.ending);
                    finish.postDelayed(new Runnable() {
                        public void run() {
                            try {
                                end.setText(R.string.end);
                                stopping = false;
                                stop = true;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, 18000);
                }
            }
        });
        final Button restart = findViewById(com.example.floriantepelmann.wifiwithstabledrone.R.id.restart);
        restart.setSoundEffectsEnabled(false);
        restart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(stop) stop = false;
                if(starting) starting = false;
                if(stopping) {
                    stopping = false;
                    finish.removeCallbacksAndMessages(null);
                    stopDrone.removeCallbacksAndMessages(null);
                    fadeDrone.removeCallbacksAndMessages(null);
                    end.setText(R.string.end);
                }
                if(muted) {
                    muted = false;
                    mute.setText(R.string.mute);
                }
                wifiManager.startScan();
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 15, 0);
                if (drone.isPlaying()){
                    drone.setVolume(1,1);
                    droneVol = 1;
                }
                else {
                    drone.start();
                    drone.setVolume(1, 1);
                    droneVol = 1;
                }
            }
        });
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
        final int fadeDuration = 8000;
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
                if((isFadingUp && (droneVol >= destVolume)) || (!isFadingUp && (droneVol <= destVolume))){
                    timer.cancel();
                    timer.purge();
                }
            }
        };
        timer.schedule(timerTask,fadeInterval,fadeInterval);
    }

    private void fadeStep(float volumeStep, boolean fadeUp){
        final float deltaVolume;
        if(stopping || starting) {
            if (fadeUp) {
                deltaVolume = droneVol + volumeStep;
            } else {
                deltaVolume = droneVol - volumeStep;
            }
            drone.setVolume(deltaVolume, deltaVolume);
            droneVol = deltaVolume;
        }
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
            if(!stop) {
                wifis.clear();
                List<ScanResult> results = wifiManager.getScanResults();
                Collections.sort(results, new ScanResultComparator());
                List<Integer> playingSounds = new ArrayList<>();
                numberOfNetworks = results.size();
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
                    if (playingSounds.contains(soundCalcPre)) {
                        soundCalcPre = avoidDuplicateSounds(playingSounds, soundCalcPre);
                    }
                    playingSounds.add(soundCalcPre);
                    int soundCalc = soundCalcPre + chapterFactor * 28;
                    if (soundCalc > 115) {
                        soundCalc = 115;
                    }
                    int randTime = 100 + (int) (Math.random() * ((1000 - 100) + 1));
                    long startDel = (long) Math.abs(Integer.decode("0x" + bssid.substring(12, 14)) * 7 + randTime);
                    long interDel = (long) Math.abs(Integer.decode("0x" + bssid.substring(15, 17)) * 7 + 1000);
                    if (System.currentTimeMillis() - lastTime >= 240000) {
                        chapterFactor++;
                        if (chapterFactor > 3) {
                            chapterFactor = 3;
                        }
                        lastTime = System.currentTimeMillis();
                    }
                    playSound(determineSoundFile(soundCalc), startDel, interDel);
                    wifis.add(soundCalc + " " + randTime + " " + startDel + " " + interDel);
                }
                wifis.add("Current Chapter: " + chapterFactor);
                wifis.add("Found Networks: " + numberOfNetworks);
                wifis.add("All Sounds: " + playingSounds);
                adapter.notifyDataSetChanged();
                if (!stopping) {
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
}