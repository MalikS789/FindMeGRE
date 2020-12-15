package uk.co.greenwich.findmegre.Entity;

import java.util.List;

public class Entity {

    protected int R;
    protected int G;
    protected int B;
    public int thickness;
    protected EntityType entiteType;
    protected List<double[]> vertices; //Each verticie is an arraylist of 3 values (X,Y,Z)

    public Entity(List<double[]> v, int r, int g, int b, int t, EntityType ss) {
        R = r;
        G = g;
        B = b;
        thickness = t;
        entiteType = ss;
        vertices = v;
    }

    public List<double[]> getVertices() {
        return vertices;
    }

    public double getXStart() {
        return this.vertices.get(0)[0];
    }

    public double getYStart() {
        return this.vertices.get(0)[1];
    }

    public int getThickness() {
        return thickness;
    }

    public EntityType getEntiteType() {
        return entiteType;
    }

}