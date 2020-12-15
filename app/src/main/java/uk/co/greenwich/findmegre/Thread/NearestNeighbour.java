package uk.co.greenwich.findmegre.Thread;

import java.util.ArrayList;
import java.util.List;

import uk.co.greenwich.findmegre.Entity.AccessPoint;

import static java.lang.Math.sqrt;
import static uk.co.greenwich.findmegre.MainActivity.AllReadings;

public class NearestNeighbour extends Thread {

    private static List<ArrayList> Unknown;
    public boolean running = true;
    private static String result = "not found";

    public static String getResult() {
        return result;
    }

    public NearestNeighbour(List<ArrayList> Unknown) {
        this.Unknown = Unknown;
    }

    @Override
    public void run() {
        result = NearestNeighbour(Unknown);
        running = false;
    }

    public static String NearestNeighbour(List<ArrayList> Unknown) {
        long startTime = System.nanoTime();
        String BestMatch = "Not found";
        float LowestScore = 99;
        float Score = 0;
        for (ArrayList Fingerprint : AllReadings) {
            String RoomName = (String) Fingerprint.get(0);
            ArrayList RSSIObject = (ArrayList) (Fingerprint.get(1));
            for (ArrayList ReadingUnknown : Unknown) {
                AccessPoint UnknownAP = (AccessPoint) ReadingUnknown.get(0);
                int UnknownRSSI = (int) ReadingUnknown.get(1);
                boolean found = false;
                for (Object ReadingKnown : RSSIObject) {
                    AccessPoint knownAP = (AccessPoint) ((ArrayList) ReadingKnown).get(0);
                    int knownRSSI = (int) ((ArrayList) ReadingKnown).get(1);
                    if (knownAP.getBSSID().equals(UnknownAP.getBSSID())) {
                        Score += Math.pow(Math.abs(knownRSSI) - Math.abs(UnknownRSSI), 2);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    Score += 100;
                }
            }
            Score = (float) sqrt(Score);
            if (Score < LowestScore) {
                LowestScore = Score;
                BestMatch = RoomName;
            }
        }
        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000;  //divide by 1000000 to get milliseconds.
        return "Nearest Neighbour: " + BestMatch + " (" + duration + " µs)";
    }

}
