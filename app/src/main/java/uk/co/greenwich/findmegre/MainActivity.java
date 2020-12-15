package uk.co.greenwich.findmegre;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import uk.co.greenwich.findmegre.Entity.AccessPoint;
import uk.co.greenwich.findmegre.Thread.APAnimator;
import uk.co.greenwich.findmegre.Thread.GUI;
import uk.co.greenwich.findmegre.Thread.LoadingBarAnimator;
import uk.co.greenwich.findmegre.Thread.UploadTrainingSample;

import static android.graphics.Color.rgb;

public class MainActivity extends AppCompatActivity {

    private static DrawingPanel panDrawingArea;
    private static boolean running = false; //By default, the program isn't running
    public static int red = 233, green = 226, blue = 255;
    public static List<ArrayList> AllReadings = new ArrayList<>();    //Holds all training samples! [[KW303,[[AP1, -55],[AP2, -60]]],[..],[...]]
    public static ArrayList<String> locations = new ArrayList<>();    //The following are used by Naive Bayes and ecolocation.
    public static ArrayList<AccessPoint> AP = new ArrayList<>();
    private WifiManager wifiMgr;

    public static boolean IsAppRunning() {
        return running;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        wifiMgr = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifiMgr.isWifiEnabled()) {
            wifiMgr.setWifiEnabled(true);
        }/* if disabled, enabled. however, return the state which is saved inside wasEnabled */
        panDrawingArea = new DrawingPanel(this);
        panDrawingArea.setBackgroundColor(rgb(red, green, blue));
        setContentView(panDrawingArea);
        running = true;
        LoadingBarAnimator loadingbar = new LoadingBarAnimator(panDrawingArea);
        APAnimator apanimator = new APAnimator(panDrawingArea);
        GUI Map = new GUI(panDrawingArea, this, this);
        loadingbar.start();
        apanimator.start();
        Map.start();
        if (!(new TimeTable(this).isSetupComplete())) {
            startActivity(new Intent(MainActivity.this, SetupActivity.class));
        } else {
            UploadTrainingSample UPS = new UploadTrainingSample(this);
            UPS.start();
        }
    }

    public List<ArrayList> doScan() {
        List<ScanResult> apList = getCurrentWiFiSituation(); // Returns a <list> of scanResults
        List<ArrayList> Unknown = new ArrayList<>();
        for (ScanResult ap : apList) {
            ArrayList Reading = new ArrayList<>();
            AccessPoint appppp = AP.get(getRouterID(ap.SSID, ap.BSSID, ap.frequency));
            Reading.add(appppp);
            Reading.add(ap.level);
            Unknown.add(Reading);
            panDrawingArea.addShape(appppp);
        }
        return Unknown;
    }

    public List<ScanResult> getCurrentWiFiSituation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 0x12345);
            }
        }
        wifiMgr.startScan();    // manually starting a scan
        List<ScanResult> apList = wifiMgr.getScanResults(); // Returns a <list> of scanResults
        return apList;
    }

    public static int getRouterID(String SSID, String BSSID, int Freq) {
        for (int i = 0; i < AP.size(); i++) {
            if (AP.get(i).getBSSID().equals(BSSID)) {
                return i;
            }
        }
        List<double[]> v = new ArrayList<>();
        int min = 0;
        int max = panDrawingArea.Width();
        int max2 = panDrawingArea.Height();
        Random r = new Random();
        int i1 = r.nextInt(max - min + 1) + min;
        int i12 = r.nextInt(max2 - min + 1) + min;
        v.add(new double[]{i1, i12});
        v.add(new double[]{20, 20});
        AP.add(new AccessPoint(v, SSID, BSSID, Freq));
        return AP.size() - 1;
    }
}
