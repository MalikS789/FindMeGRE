package uk.co.greenwich.findmegre.Thread;

import java.util.ArrayList;
import java.util.List;

import uk.co.greenwich.findmegre.DrawingPanel;
import uk.co.greenwich.findmegre.Entity.Entity;
import uk.co.greenwich.findmegre.Entity.LoadingBar;

import static uk.co.greenwich.findmegre.Entity.EntityType.LOADINGBAR;

public class LoadingBarAnimator extends Thread {

    private int Delay = 16, PercentageOfArc = 0, StartingAngle = 90;
    private DrawingPanel DrawingPanel;
    public static double xCentre, yCentre, Radius;

    public LoadingBarAnimator(DrawingPanel drawingpanel) {
        this.DrawingPanel = drawingpanel;
    }

    @Override
    public void run() {
        Step1(); //Create Loading Bar
        Step2(); //Animate Loading Bar
        Step3(); //Delete Loading Bar
    }

    private void Step1() {
        xCentre = DrawingPanel.Width() / 2;
        yCentre = DrawingPanel.Height() / 2;
        Radius = DrawingPanel.Width() / 5;
        List<double[]> v = new ArrayList<>();
        double[] v0 = {xCentre - Radius, yCentre - Radius};
        v.add(v0);
        double[] v1 = {2 * Radius, 2 * Radius};
        v.add(v1);
        DrawingPanel.getShapes().add(new LoadingBar(v, (int) (DrawingPanel.Width() * 1.5 / (double) 30), StartingAngle, 10));
    }

    public void Step2() {
        while (!GUI.ShouldWeShowTheLoadingBar()) {
            try {
                Thread.sleep(Delay); //Wait
            } catch (InterruptedException ex) {
            }
        }
       // while (1 == 1) {
        while (GUI.ShouldWeShowTheLoadingBar()) {
            PercentageOfArc++;
            if (StartingAngle < 0) {
                StartingAngle = 360;
            }
            StartingAngle = StartingAngle - 4;
            if (PercentageOfArc > 100) {
                PercentageOfArc = 0;
            }
            for (int i = 0; i < DrawingPanel.getShapes().size(); i++) {
                if ((DrawingPanel.getShapes().get(i)) instanceof LoadingBar) {
                    ((LoadingBar) DrawingPanel.getShapes().get(i)).setAngle((int) (PercentageOfArc * ((double) 360 / (double) 100)));
                    ((LoadingBar) DrawingPanel.getShapes().get(i)).setOffset(StartingAngle);
                    break;
                }
            }
            if (GUI.getLoadingObjective() == null) {
                GUI.ShouldWeShowTheLoadingBar(false);
            }
            try {
                Thread.sleep(Delay);
            } catch (InterruptedException ex) {
            }
        }
    }

    private void Step3() {
        for (int i = 0; i < DrawingPanel.getShapes().size(); i++) {
            if (((Entity) (DrawingPanel.getShapes().get(i))).getEntiteType() == LOADINGBAR) {
                    DrawingPanel.getShapes().remove(i);
                    break;
                }
            }
        }
}
