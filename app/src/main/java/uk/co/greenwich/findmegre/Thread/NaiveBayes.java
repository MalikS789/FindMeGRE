package uk.co.greenwich.findmegre.Thread;

import java.util.ArrayList;
import java.util.List;

import uk.co.greenwich.findmegre.Entity.AccessPoint;

import static uk.co.greenwich.findmegre.MainActivity.AllReadings;
import static uk.co.greenwich.findmegre.MainActivity.locations;

public class NaiveBayes extends Thread {

    private static List<ArrayList> Unknown;
    public boolean running = true;
    private static String result = "not found";

    public static String getResult() {
        return result;
    }

    public NaiveBayes(List<ArrayList> Unknown) {
        this.Unknown = Unknown;
    }

    @Override
    public void run() {
        result = NaiveBayes(Unknown);
        running = false;
    }

    private static String NaiveBayes(List<ArrayList> Unknown) {
        long startTime = System.nanoTime();
        String BestMatch = "Not found";
        float BestProbability = 0;
        float TempProbability = 0;
        for (String location : locations) {
            TempProbability = 0;
            int numberOfReadingsAtLocation = 0;
            int numberOfTimesAPappearsAtLocation = 0;
            for (ArrayList ReadingUnknown : Unknown) {
                AccessPoint UnknownAP = (AccessPoint) ReadingUnknown.get(0);
                for (ArrayList Fingerprint : AllReadings) {
                    String RoomName = (String) Fingerprint.get(0);
                    if (RoomName.equals(location)) {
                        numberOfReadingsAtLocation++;
                        ArrayList RSSIObject = (ArrayList) (Fingerprint.get(1));
                        for (Object ReadingKnown : RSSIObject) {
                            AccessPoint knownAP = (AccessPoint) ((ArrayList) ReadingKnown).get(0);
                            if (knownAP.getBSSID().equals(UnknownAP.getBSSID())) {
                                numberOfTimesAPappearsAtLocation++;
                                //break;
                            }
                        }
                    }
                }
                float probabilityThatAPisAtLabel = ((float) numberOfTimesAPappearsAtLocation / numberOfReadingsAtLocation);
                TempProbability += probabilityThatAPisAtLabel;
            }

            if (TempProbability > BestProbability) {
                BestProbability = TempProbability;
                BestMatch = location;
            }
        }
        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000;  //divide by 1000000 to get milliseconds.
        return "Naive Bayes: " + BestMatch + " (" + duration + " µs)";
    }

}
