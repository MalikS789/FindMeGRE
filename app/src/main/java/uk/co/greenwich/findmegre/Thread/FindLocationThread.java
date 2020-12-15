package uk.co.greenwich.findmegre.Thread;

import java.util.ArrayList;
import java.util.List;

import uk.co.greenwich.findmegre.DrawingPanel;
import uk.co.greenwich.findmegre.Entity.Entity;
import uk.co.greenwich.findmegre.Entity.Text;

public class FindLocationThread extends Thread {

    private List<ArrayList> Unknown;
    public static int Ecolabelindex, nearestneighbourindex, naivebayesindex, svmindex;
    int Delay = 250;

    public FindLocationThread(List<ArrayList> Unknown) {
        this.Unknown = Unknown;
    }

    @Override
    public void run() {
        EcoLocation ecolocation = new EcoLocation(Unknown);
        NaiveBayes naivebayes = new NaiveBayes(Unknown);
        NearestNeighbour nn = new NearestNeighbour(Unknown);
        SupportVectorMachine svm = new SupportVectorMachine(Unknown);
        ecolocation.run();
        naivebayes.run();
        nn.run();
        svm.run();
        String loading = "";
        int k = 0;
        while (ecolocation.running || naivebayes.running || nn.running || svm.running) {
            k++;
            if (k > 3) {
                k = 0;
            }
            switch (k) {
                case 0:
                    loading = "";
                    break;
                case 1:
                    loading = ".";
                    break;
                case 2:
                    loading = "..";
                    break;
                case 3:
                    loading = "...";
                    break;
            }

            int j = -1;
            for (int i = 0; i < DrawingPanel.getShapes().size(); i++) {
                Entity shape = (Entity) DrawingPanel.getShapes().get(i);
                if (shape instanceof Text) {
                    ++j;
                    if (j == 0) {
                        Ecolabelindex = i;
                    } else if (j == 1) {
                        naivebayesindex = i;
                    } else if (j == 2) {
                        nearestneighbourindex = i;
                    } else if (j == 3) {
                        svmindex = i;
                    }
                }
            }

            if (ecolocation.running) {
                ((Text) (DrawingPanel.getShapes().get(Ecolabelindex))).setText(loading);
            }
            if (naivebayes.running) {
                ((Text) (DrawingPanel.getShapes().get(naivebayesindex))).setText(loading);
            }
            if (nn.running) {
                ((Text) (DrawingPanel.getShapes().get(nearestneighbourindex))).setText(loading);
            }
            if (svm.running) {
                ((Text) (DrawingPanel.getShapes().get(svmindex))).setText(loading);
            }

            if (!ecolocation.running) {
                ((Text) (DrawingPanel.getShapes().get(Ecolabelindex))).setText(ecolocation.getResult());
            }
            if (!naivebayes.running) {
                ((Text) (DrawingPanel.getShapes().get(naivebayesindex))).setText(naivebayes.getResult());
            }
            if (!nn.running) {
                ((Text) (DrawingPanel.getShapes().get(nearestneighbourindex))).setText(nn.getResult());
            }
            if (!svm.running) {
                ((Text) (DrawingPanel.getShapes().get(svmindex))).setText(svm.getResult());
            }
            try {
                Thread.sleep(Delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        int j = -1;
        for (int i = 0; i < DrawingPanel.getShapes().size(); i++) {
            Entity shape = (Entity) DrawingPanel.getShapes().get(i);
            if (shape instanceof Text) {
                ++j;
                if (j == 0) {
                    Ecolabelindex = i;
                } else if (j == 1) {
                    naivebayesindex = i;
                } else if (j == 2) {
                    nearestneighbourindex = i;
                } else if (j == 3) {
                    svmindex = i;
                }
            }
        }
        ((Text) (DrawingPanel.getShapes().get(Ecolabelindex))).setText(ecolocation.getResult());
        ((Text) (DrawingPanel.getShapes().get(naivebayesindex))).setText(naivebayes.getResult());
        ((Text) (DrawingPanel.getShapes().get(nearestneighbourindex))).setText(nn.getResult());
        ((Text) (DrawingPanel.getShapes().get(svmindex))).setText(svm.getResult());
        GUI.AddButton();
    }
}
