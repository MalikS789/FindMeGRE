package uk.co.greenwich.findmegre.Entity;

import java.util.List;

import static uk.co.greenwich.findmegre.Entity.EntityType.BUTTON;

public class Text extends Entity {

    private String Label = "";

    public Text(List<double[]> v, String Label) {
        super(v,0,0,0,5,BUTTON);
        this.Label = Label;
    }

    public String getText() {
        return Label;
    }

    public void setText(String temp) {
        Label = temp;
    }

}
