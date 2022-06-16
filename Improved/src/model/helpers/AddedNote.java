package model.helpers;

import java.awt.geom.Point2D;
import java.io.Serializable;

public class AddedNote implements Serializable{
    private Point2D coords;
    private String name;

    public AddedNote(Point2D coords, String name) {
        this.coords = coords;
        this.name = name;
    }

    public Point2D getCoords() {
        return coords;
    }

    public String getName() {
        return name;
    }
}
