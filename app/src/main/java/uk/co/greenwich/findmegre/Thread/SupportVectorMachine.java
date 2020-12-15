package uk.co.greenwich.findmegre.Thread;

import java.util.ArrayList;
import java.util.List;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;
import uk.co.greenwich.findmegre.Entity.AccessPoint;

import static uk.co.greenwich.findmegre.MainActivity.AP;
import static uk.co.greenwich.findmegre.MainActivity.AllReadings;
import static uk.co.greenwich.findmegre.MainActivity.locations;

public class SupportVectorMachine extends Thread {

    private static List<ArrayList> Unknown;
    public boolean running = true;
    private static String result = "not found";

    public static String getResult() {
        return result;
    }

    public SupportVectorMachine(List<ArrayList> Unknown) {
        this.Unknown = Unknown;
    }

    @Override
    public void run() {
        if (AP.size() > 0) {
            result = SupportVectorMachine(Unknown);
        } else {
            result = "not found";
        }
        running = false;
    }

    private static String SupportVectorMachine(List<ArrayList> Unknown) {
        long startTime = System.nanoTime();
        String label = "not found";
        double[][] trainingData;
        for (int i = 0; i < locations.size(); i++) {
            String location = locations.get(i);
            //for this location, create trainingdata (trainingData[j][0] is the target)
            trainingData = new double[AllReadings.size()][AP.size() + 1];
            for (int j = 0; j < AllReadings.size(); j++) {
                if (AllReadings.get(j).get(0).equals(location)) {
                    trainingData[j][0] = 1.0;
                } else {
                    trainingData[j][0] = -1.0;
                }
                ArrayList RSSIObject = (ArrayList) AllReadings.get(j).get(1);
                for (int k = 0; k < AP.size(); k++) {
                    for (Object EachRSSI : RSSIObject) {
                        AccessPoint AccessPointID = (AccessPoint) ((ArrayList) EachRSSI).get(0);
                        int RSSI = (int) ((ArrayList) EachRSSI).get(1);
                        if (AccessPointID.getBSSID().equals(AP.get(k).getBSSID())) {
                            trainingData[j][k + 1] = (double) RSSI;
                            break;
                        }
                    }
                }
            }
            //train a svm model with this training data
            svm_model m = svmTrain(trainingData);
            //now create normalised dataset for the unknown reading.
            double[] normalisedUnknown = new double[AP.size() + 1];
            for (int n = 0; n < Unknown.size(); n++) {
                AccessPoint UnknownAP = (AccessPoint) ((ArrayList) Unknown.get(n)).get(0);
                int RSSI = (int) ((ArrayList) Unknown.get(n)).get(1);
                for (int j = 0; j < AP.size(); j++) {
                    if (UnknownAP.getBSSID().equals(AP.get(j).getBSSID())) {
                        normalisedUnknown[j + 1] = (double) RSSI;
                        break;
                    }
                }
            }

            //find out if it is a match!
            if (evaluate(normalisedUnknown, m) == 1.0) {
                label = locations.get(i);
            }
        }
        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000;  //divide by 1000000 to get milliseconds.
        return "Support Vector Machine : " + label + " (" + duration + " µs)";
    }

    private static svm_model svmTrain(double[][] train) {
        svm_problem prob = new svm_problem();
        int dataCount = train.length;
        prob.y = new double[dataCount];
        prob.l = dataCount;
        prob.x = new svm_node[dataCount][];

        for (int i = 0; i < dataCount; i++) {
            double[] features = train[i];
            prob.x[i] = new svm_node[features.length - 1];
            for (int j = 1; j < features.length; j++) {
                svm_node node = new svm_node();
                node.index = j;
                node.value = features[j];
                prob.x[i][j - 1] = node;
            }
            prob.y[i] = features[0];
        }

        svm_parameter param = new svm_parameter();
        param.probability = 1;
        param.gamma = 0.5;
        param.nu = 0.5;
        param.C = 1;
        param.svm_type = svm_parameter.C_SVC;
        param.kernel_type = svm_parameter.POLY;
        param.cache_size = 20000;
        param.eps = 0.001;
        param.degree = 2;

        // Disables svm output
        svm.svm_set_print_string_function(s -> {
        });

        svm_model model = svm.svm_train(prob, param);
        return model;
    }

    private static double evaluate(double[] features, svm_model model) {
        svm_node[] nodes = new svm_node[features.length - 1];
        for (int i = 1; i < features.length; i++) {
            svm_node node = new svm_node();
            node.index = i;
            node.value = features[i];

            nodes[i - 1] = node;
        }

        int totalClasses = 2;
        int[] labels = new int[totalClasses];
        svm.svm_get_labels(model, labels);

        double[] prob_estimates = new double[totalClasses];
        double v = svm.svm_predict_probability(model, nodes, prob_estimates);

        return v;
    }
}
