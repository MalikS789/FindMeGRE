package uk.co.greenwich.findmegre;

import android.content.Context;

import org.json.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import uk.co.greenwich.findmegre.Entity.AccessPoint;
import uk.co.greenwich.findmegre.Thread.GUI;

import static uk.co.greenwich.findmegre.MainActivity.AP;
import static uk.co.greenwich.findmegre.MainActivity.AllReadings;
import static uk.co.greenwich.findmegre.MainActivity.locations;

public class PHP {

    private static Context context;
    private static String getAllAPDataurl = "http://stuiis.cms.gre.ac.uk/ms2721o/getAllKnownAPs.php";
    private static String getAllTrainingDataurl = "http://stuiis.cms.gre.ac.uk/ms2721o/getTrainingData.php";
    private static String addTrainingDataurl = "http://stuiis.cms.gre.ac.uk/ms2721o/InsertIntoDatabase.php";

    public PHP(Context context) {
        this.context = context;
    }

    public void sendTrainingSample(List<ArrayList> Unknown, String Room) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(addTrainingDataurl); // here is your URL path
            JSONObject installedPackage;
            installedPackage = new JSONObject();
            installedPackage.put("apNum", Unknown.size());
            installedPackage.put("Zone", Room);
            org.json.JSONArray jSSID = new org.json.JSONArray();
            org.json.JSONArray jBSSID = new org.json.JSONArray();
            org.json.JSONArray jFrequency = new org.json.JSONArray();
            org.json.JSONArray jRSSI = new org.json.JSONArray();
            System.out.println("Creating 4 JSONArrays");
            for (int i = 0; i < Unknown.size(); i++) {
                jSSID.put(((AccessPoint) (Unknown.get(i).get(0))).getSSID());
                jBSSID.put(((AccessPoint) (Unknown.get(i).get(0))).getBSSID());
                jFrequency.put(((AccessPoint) (Unknown.get(i).get(0))).getFrequency());
                jRSSI.put(Unknown.get(i).get(1));
            }
            installedPackage.put("SSID", jSSID);
            installedPackage.put("BSSID", jBSSID);
            installedPackage.put("Frequency", jFrequency);
            installedPackage.put("RSSI", jRSSI);
            String dataToSend = installedPackage.toString();
            System.out.println(dataToSend);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(dataToSend);
            writer.flush();
            writer.close();
            os.close();
            int responseCode = conn.getResponseCode();
            System.out.println("Response code : " + responseCode);
        } catch (Exception e) {
            System.out.println("ERROR in auto training sample upload: " + e);
            GUI.setLoadingObjective("Exception: " + e.getMessage());
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    public void getTrainingData() {
        getAllAPData();
        JSONArray AllTrainingData = null;
        try {
            GUI.setLoadingObjective("Downloading training data from the database...");
            URL website = new URL(getAllTrainingDataurl);
            ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            FileOutputStream outputStream = context.openFileOutput("TrainingData.json", Context.MODE_PRIVATE);
            outputStream.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            File file = context.getFileStreamPath("TrainingData.json");
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(new FileReader(file.getAbsolutePath()));
            AllTrainingData = (JSONArray) obj;//convert Object to JSONObject

            for (Object EachFingeprint : AllTrainingData) {
                ArrayList Fingerprint = new ArrayList<>();
                JSONArray array = (JSONArray) EachFingeprint;
                String RoomName = (String) array.get(0);
                if (!locations.contains(RoomName)) {
                    locations.add(RoomName);
                }
                ArrayList RSSIObject = (JSONArray) array.get(1);
                Fingerprint.add(RoomName);
                Fingerprint.add(new ArrayList<>());
                for (int i = 0; i < RSSIObject.size(); i++) {
                    ArrayList Reading = new ArrayList();
                    int APID = Integer.parseInt((String) ((JSONArray) RSSIObject.get(i)).get(0));
                    int RSSI = Integer.parseInt((String) ((JSONArray) RSSIObject.get(i)).get(1));
                    Reading.add(AP.get(APID - 2));
                    Reading.add(RSSI);
                    ((ArrayList) Fingerprint.get(1)).add(Reading);
                }
                AllReadings.add(Fingerprint);
            }
        } catch (Exception e) {
            System.out.println("ERROR: " + e);
            GUI.setLoadingObjective(e + "");
        }
    }

    public void getAllAPData() {
        GUI.setLoadingObjective("Downloading all known access points from the database...");
        try {
            URL website = new URL(getAllAPDataurl);
            ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            FileOutputStream outputStream = context.openFileOutput("AP.json", Context.MODE_PRIVATE);
            outputStream.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            File file = context.getFileStreamPath("AP.json");
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(new FileReader(file.getAbsolutePath()));
            JSONArray AllAPs = (JSONArray) obj;//convert Object to JSONObject
            for (Object EachAPArray : AllAPs) {
                JSONArray EachAP = (JSONArray) EachAPArray;
                List<double[]> v = new ArrayList<>();
                int min = 0;
                int max = 1440;
                int max2 = 2560;
                Random r = new Random();
                int i1 = r.nextInt(max - min + 1) + min;
                int i12 = r.nextInt(max2 - min + 1) + min;
                v.add(new double[]{i1, i12});
                v.add(new double[]{20, 20});
                AP.add(new AccessPoint(v, (String) EachAP.get(0), (String) EachAP.get(1), Math.toIntExact((long) EachAP.get(2))));
            }
        } catch (IOException | ParseException ex) {
            System.out.println("ERROR: " + ex);
            GUI.setLoadingObjective("Couldn't find the file? (" + ex + ")");
        }
    }
}