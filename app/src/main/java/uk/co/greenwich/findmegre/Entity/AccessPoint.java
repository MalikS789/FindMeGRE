package uk.co.greenwich.findmegre.Entity;

import java.util.List;

import static uk.co.greenwich.findmegre.Entity.EntityType.ACCESSPOINT;

public class AccessPoint extends Entity {

    private String ssid;
    private String bssid;
    private int frequency;

    public AccessPoint(List<double[]> v, String ssid, String bssid, int frequency) {
        super(v, 0, 162, 232, 5, ACCESSPOINT);
        this.ssid = ssid;
        this.bssid = bssid;
        this.frequency = frequency;
    }

    public double getWidth() {
        return (double) this.vertices.get(1)[0];
    }

    public String getSSID() {
        return ssid;
    }

    public String getBSSID() {
        return bssid;
    }

    public int getFrequency() {
        return frequency;
    }
}