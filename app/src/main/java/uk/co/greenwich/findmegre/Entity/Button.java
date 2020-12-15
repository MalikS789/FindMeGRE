package uk.co.greenwich.findmegre.Entity;

import java.util.List;

import static uk.co.greenwich.findmegre.Entity.EntityType.BUTTON;

public class Button extends Entity {

    private String Label = "";

    public Button(List<double[]> v, String Label) {
        super(v,0,0,0,5,BUTTON);
        this.Label = Label;
    }

    public String getLabel() {
        return Label;
    }

    public void setLabel(String temp) {
        Label = temp;
    }

}
