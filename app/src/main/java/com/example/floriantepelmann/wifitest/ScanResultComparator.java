package com.example.floriantepelmann.wifitest;

import android.net.wifi.ScanResult;

import java.util.Comparator;

/**
 * Created by thomaskessler on 15.01.18.
 */

public class ScanResultComparator implements Comparator<ScanResult> {

    @Override
    public int compare(ScanResult b, ScanResult a){
        if(a.level < b.level){
            return -1;
        } else if(a.level > b.level){
            return 1;
        }
        return 0;
    }
}
