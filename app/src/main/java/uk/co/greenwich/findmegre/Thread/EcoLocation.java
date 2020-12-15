package uk.co.greenwich.findmegre.Thread;

import java.util.ArrayList;
import java.util.List;

import uk.co.greenwich.findmegre.Entity.AccessPoint;

import static uk.co.greenwich.findmegre.MainActivity.AllReadings;
import static uk.co.greenwich.findmegre.MainActivity.locations;

public class EcoLocation extends Thread {

    private static List<ArrayList> Unknown;
    public boolean running = true;
    private static String result = "not found";

    public static String getResult() {
        return result;
    }

    public EcoLocation(List<ArrayList> Unknown) {
        this.Unknown = Unknown;
    }

    @Override
    public void run() {
        result = Ecolocation(Unknown);
        running = false;
    }

    private static String Ecolocation(List<ArrayList> Unknown) {
        long startTime = System.nanoTime();
        String BestMatch = "Not found";
        ArrayList<String> PreviousBest = new ArrayList<>();
        int maxConstrMatch = 0;
        ArrayList Constraints = new ArrayList<>();
        //First make a copy of all the readings, but all the readings sorted in order.
        for (int i = 0; i < AllReadings.size(); i++) {
            ArrayList RSSIObject = (ArrayList) (AllReadings.get(i).get(1));
            QuickSort(RSSIObject, 0, RSSIObject.size() - 1);
            ArrayList locationReading = new ArrayList<>();
            locationReading.add((String) AllReadings.get(i).get(0));
            locationReading.add(RSSIObject);
            Constraints.add(locationReading);
        }
        QuickSort(Unknown, 0, Unknown.size() - 1);
        //next, we need to find the reading that has the most matched constraints
        for (Object Fingerprint : Constraints) {
            int ConstrMatch = 100;
            String RoomName = (String) ((ArrayList) Fingerprint).get(0);
            ArrayList RSSIObject = (ArrayList) ((ArrayList) Fingerprint).get(1);
            for (int j = 1; j < Unknown.size(); j++) {
                if (RSSIObject.size() < j) {
                } else {
                    AccessPoint UnknownAP = (AccessPoint) ((ArrayList) Unknown.get(Unknown.size() - j)).get(0);
                    AccessPoint knownAP = (AccessPoint) ((ArrayList) RSSIObject.get(RSSIObject.size() - j)).get(0);
                    if (knownAP.getBSSID().equals(UnknownAP.getBSSID())) {
                        ConstrMatch += 1;
                    } else {
                        ConstrMatch -= 1;
                    }
                }
            }
            if (maxConstrMatch < ConstrMatch) {
                maxConstrMatch = ConstrMatch;
                BestMatch = RoomName;
                PreviousBest.clear();
                PreviousBest.add(RoomName);
            } else if (maxConstrMatch == ConstrMatch) {
                if (!PreviousBest.contains(RoomName)) {
                    BestMatch += " or " + RoomName;
                    PreviousBest.add(RoomName);
                }
            }
        }

        if (PreviousBest.size() == locations.size()) {
            //basically it thinks it can be any room, which is useless
            BestMatch = "not found";
        }
        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000;
        return "Error controlled localization : " + BestMatch + " (" + duration + " µs)";
    }

    private static int partition(List<ArrayList> arr, int low, int high) {
        int pivot = (int) arr.get(high).get(1);
        int i = (low - 1); // index of smaller element
        for (int j = low; j < high; j++) {
            if ((int) arr.get(j).get(1) <= pivot) {
                i++;
                ArrayList temp = arr.get(i);
                arr.set(i, arr.get(j));
                arr.set(j, temp);
            }
        }
        ArrayList temp = arr.get(i + 1);
        arr.set(i + 1, arr.get(high));
        arr.set(high, temp);
        return i + 1;
    }

    private static void QuickSort(List<ArrayList> arr, int low, int high) {
        /* The main function that implements QuickSort()
      arr[] --> Array to be sorted,
      low  --> Starting index,
      high  --> Ending index */
        if (low < high) {
            int pi = partition(arr, low, high);
            QuickSort(arr, low, pi - 1);
            QuickSort(arr, pi + 1, high);
        }
    }

}
