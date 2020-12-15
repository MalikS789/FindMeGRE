package uk.co.greenwich.findmegre.Entity;

import java.util.List;

import static uk.co.greenwich.findmegre.Entity.EntityType.LOADINGBAR;

public class LoadingBar extends Entity {

    private int angle;
    private int offset;

    public LoadingBar(List<double[]> v, int Thickness, int Offset, int Angle) {
        super(v, 0,0,0, Thickness, LOADINGBAR);
        angle = Angle;
        offset = Offset;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int p) {
        offset = p;
    }

    public int getAngle() {
        return angle;
    }

    public void setAngle(int p) {
        angle = p;
    }

    public double getWidth() {
        return this.vertices.get(1)[0];
    }
}