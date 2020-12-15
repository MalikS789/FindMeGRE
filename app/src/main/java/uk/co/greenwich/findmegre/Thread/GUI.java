package uk.co.greenwich.findmegre.Thread;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import uk.co.greenwich.findmegre.Entity.AccessPoint;
import uk.co.greenwich.findmegre.Entity.Button;
import uk.co.greenwich.findmegre.DrawingPanel;
import uk.co.greenwich.findmegre.Entity.Entity;
import uk.co.greenwich.findmegre.MainActivity;
import uk.co.greenwich.findmegre.PHP;
import uk.co.greenwich.findmegre.Entity.Text;

public class GUI extends Thread {

    private static DrawingPanel DrawingPanel = null;
    private static MainActivity MA = null;
    private static Context context;
    private int Delay = 8;
    private static boolean busy = false, ShowLoadingBar = true;
    private static String LoadingObjective = "Loading"; //This will be whatever is being loaded..

    public GUI(DrawingPanel drawingpanel, Context context, MainActivity MA) {
        this.DrawingPanel = drawingpanel;
        this.context = context;
        this.MA = MA;
    }

    public static boolean ShouldWeShowTheLoadingBar() {
        return ShowLoadingBar;
    }

    public static void ShouldWeShowTheLoadingBar(boolean value) {
        ShowLoadingBar = value;
    }

    public static String getLoadingObjective() {
        return LoadingObjective;
    }

    public static void setLoadingObjective(String Objective) {
        LoadingObjective = Objective;
    }

    @Override
    public void run() {
        try {
            PHP php = new PHP(context);
            php.getTrainingData();
            AddButton();
            AddResultLabels();
            LoadingObjective = null;
            long timer = System.currentTimeMillis();
            while (MainActivity.IsAppRunning()) {
                long temp = System.currentTimeMillis();
                if ((temp - timer) >= Delay) {
                    Update();
                    timer += (temp - timer);
                }
            }
        } catch (Exception ex) {
            LoadingObjective = "ERROR: Connect to eduroam Wi-Fi";
        }
    }

    private void AddResultLabels() {
        List<double[]> v = new ArrayList<>();
        v.add(new double[]{(double) DrawingPanel.Width() / 9 * 1.5,(double)DrawingPanel.Height() / 16 * 1.5});
        DrawingPanel.addShape(new Text(v, ""));
        List<double[]> v1 = new ArrayList<>();
        v1.add(new double[]{(double) DrawingPanel.Width() / 9 * 1.5,(double)DrawingPanel.Height() / 16 * 3.5});
        DrawingPanel.addShape(new Text(v1, ""));
        List<double[]> v2 = new ArrayList<>();
        v2.add(new double[]{(double) DrawingPanel.Width() / 9 * 1.5,(double)DrawingPanel.Height() / 16 * 5.5});
        DrawingPanel.addShape(new Text(v2, ""));
        List<double[]> v3 = new ArrayList<>();
        v3.add(new double[]{(double) DrawingPanel.Width() / 9 * 1.5,(double)DrawingPanel.Height() / 16 * 7.5});
        DrawingPanel.addShape(new Text(v3, ""));
        List<double[]> v4 = new ArrayList<>();
        v4.add(new double[]{(double) DrawingPanel.Width() / 9 * 1.5,(double)DrawingPanel.Height() / 16 * 9.5});
        DrawingPanel.addShape(new Text(v4, ""));
    }

    public static void AddButton() {
        List<double[]> v = new ArrayList<>();
        double[] v0 = new double[]{(double) DrawingPanel.Width() / 9 * 3,(double)DrawingPanel.Height() / 16 * 12};
        double[] v1 = new double[]{(double) DrawingPanel.Width() / 9 * 6,(double)DrawingPanel.Height() / 16 * 12};
        double[] v2 = new double[]{(double)DrawingPanel.Width() / 9 * 6,(double)DrawingPanel.Height() / 16 * 14};
        double[] v3 = new double[]{(double)DrawingPanel.Width() / 9 * 3,(double)DrawingPanel.Height() / 16 * 14};
        v.add(v0);
        v.add(v1);
        v.add(v2);
        v.add(v3);
        Button button = new Button(v,"Find Me");
        DrawingPanel.addShape(button);
    }

    private void Update() {
        if (!busy) {
            busy = true;
            for (int i = 0; i < DrawingPanel.getShapes().size(); i++) {
                Entity Shape = (Entity) DrawingPanel.getShapes().get(i);
                if (Shape instanceof Button) {
                    if (IsRectangleClicked(Shape.getVertices())) {
                        for (int j = 0; j < DrawingPanel.getShapes().size(); j++) {
                            Entity shape = (Entity) DrawingPanel.getShapes().get(j);
                            if (shape instanceof AccessPoint || shape instanceof Button) {
                                DrawingPanel.getShapes().remove(shape);
                            }
                        }
                        FindLocationThread fl = new FindLocationThread(MA.doScan());
                        fl.run();
                        DrawingPanel.setLastClickLocationX(-1000);
                        DrawingPanel.setLastClickLocationY(-1000);
                    }
                }
            }
            busy = false;
        }
    }

    private static float FindAreaOfRectangle(float x1, float y1, float x2, float y2, float x3, float y3) {
        return (float) Math.abs((x1 * (y2 - y3) + x2 * (y3 - y1) + x3 * (y1 - y2)) / 2.0);
    }

    private static boolean IsRectangleClicked(List<double[]> v) {
        float A = FindAreaOfRectangle((float) v.get(0)[0],(float)  v.get(0)[1],(float)  v.get(1)[0],(float)  v.get(1)[1],(float)  v.get(2)[0],(float)  v.get(2)[1])
                + FindAreaOfRectangle((float) v.get(0)[0],(float)  v.get(0)[1],(float)  v.get(3)[0], (float) v.get(3)[1],(float)  v.get(2)[0], (float) v.get(2)[1]);
        float A1 = FindAreaOfRectangle(DrawingPanel.getLastClickLocationX(), DrawingPanel.getLastClickLocationY(),(float)  v.get(0)[0], (float) v.get(0)[1], (float) v.get(1)[0], (float) v.get(1)[1]);
        float A2 = FindAreaOfRectangle(DrawingPanel.getLastClickLocationX(), DrawingPanel.getLastClickLocationY(), (float) v.get(1)[0],(float)  v.get(1)[1],(float)  v.get(2)[0], (float) v.get(2)[1]);
        float A3 = FindAreaOfRectangle(DrawingPanel.getLastClickLocationX(), DrawingPanel.getLastClickLocationY(), (float) v.get(2)[0], (float) v.get(2)[1],(float)  v.get(3)[0], (float) v.get(3)[1]);
        float A4 = FindAreaOfRectangle(DrawingPanel.getLastClickLocationX(), DrawingPanel.getLastClickLocationY(), (float) v.get(0)[0],(float)  v.get(0)[1],(float)  v.get(3)[0], (float) v.get(3)[1]);
        return (A == A1 + A2 + A3 + A4);
    }
}
