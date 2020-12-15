package uk.co.greenwich.findmegre.Thread;

import java.util.Calendar;

import uk.co.greenwich.findmegre.MainActivity;
import uk.co.greenwich.findmegre.PHP;
import uk.co.greenwich.findmegre.TimeTable;

public class UploadTrainingSample extends Thread {

    private static MainActivity ctx;
    private static TimeTable timetable;

    public UploadTrainingSample(MainActivity ctx) {
        this.ctx = ctx;
    }

    @Override
    public void run() {
        System.out.println("Thread started");
        PHP server = new PHP(ctx);
        timetable = new TimeTable(ctx);
        while (true) {
            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_WEEK);
            int currentHourIn24Format = calendar.get(Calendar.HOUR_OF_DAY);
            if (timetable.ReceiveFromTimeTable(day, currentHourIn24Format) != null) {
                try {
                    server.sendTrainingSample(ctx.doScan(), timetable.ReceiveFromTimeTable(day, currentHourIn24Format));
                } catch (Exception ex) {
                    System.out.println("Couldn't send training sample : " + ex);
                }
            } else {
                System.out.println("Allocated session for this time is null, sample not sent.");
            }
            try {
                Thread.sleep(10000); //wait 10 seconds
            } catch (InterruptedException e) {
                System.out.println("Thread interrupted : " + e);
            }
        }
    }
}
