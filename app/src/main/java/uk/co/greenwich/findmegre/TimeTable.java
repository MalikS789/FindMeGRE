package uk.co.greenwich.findmegre;

import android.content.Context;
import android.content.SharedPreferences;

public class TimeTable {

    private final Context ctx;
    private String[][] timetable; //7 days, 23 hours available

    public TimeTable(Context ctx) {
        this.ctx = ctx;
        timetable = new String[7][24];
        LoadTimeTableFromFile(ctx);
    }

    public String ReceiveFromTimeTable(int day, int Hour24) {
        day = day - 1;
        if (day < 0) {
            day = 0;
        }
  //      System.out.println("Requested day :" + day);
   //     System.out.println("Requested hour : " + Hour24);
   //     System.out.println(Arrays.deepToString(timetable).replace("], ", "]\n").replace("[[", "[").replace("]]", "]"));
        return timetable[day][Hour24];
    }

    public void AddEntryToTimeTable(int day, int Hour24, String location) {
        timetable[day][Hour24] = location;
    }

    public void SaveTimeTableToFile() {
        for (int i = 0; i < 7; i++) {
            if (timetable[i].length < 1) {
                timetable[i] = new String[]{null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null};
            }
        }
        saveArray(timetable[0], "Sunday", ctx);
        saveArray(timetable[1], "Monday", ctx);
        saveArray(timetable[2], "Tuesday", ctx);
        saveArray(timetable[3], "Wednesday", ctx);
        saveArray(timetable[4], "Thursday", ctx);
        saveArray(timetable[5], "Friday", ctx);
        saveArray(timetable[6], "Saturday", ctx);
        saveArray(new String[]{"Yes"}, "Setup", ctx);
    }

    public void LoadTimeTableFromFile(Context ctx) {
        timetable[0] = loadArray("Sunday", ctx);
        timetable[1] = loadArray("Monday", ctx);
        timetable[2] = loadArray("Tuesday", ctx);
        timetable[3] = loadArray("Wednesday", ctx);
        timetable[4] = loadArray("Thursday", ctx);
        timetable[5] = loadArray("Friday", ctx);
        timetable[6] = loadArray("Saturday", ctx);
        for (int i = 0; i < 7; i++) {
            if (timetable[i].length < 1) {
                timetable[i] = new String[]{null, null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null};
            }
        }
    }

    public boolean isSetupComplete() {
        String[] array = loadArray("Setup", ctx);
        try {
            if (array[0].equals("Yes")) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }


    private boolean saveArray(String[] array, String arrayName, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("preferencename", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(arrayName + "_size", array.length);
        for (int i = 0; i < array.length; i++) {
            editor.putString(arrayName + "_" + i, array[i]);
        }
        return editor.commit();
    }

    private String[] loadArray(String arrayName, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("preferencename", 0);
        int size = prefs.getInt(arrayName + "_size", 0);
        String array[] = new String[size];
        for (int i = 0; i < size; i++) {
            array[i] = prefs.getString(arrayName + "_" + i, null);
        }
        return array;
    }

}


