package model;

import java.awt.geom.Point2D;
import java.io.Serializable;

public class UserInputPoint extends Point2D.Float implements Serializable{
    private String userInput;

    public UserInputPoint(Point2D point, String userInput){
        super();
        setLocation(point);
        this.userInput = userInput;
    }

    public String getUserInput() {
        return userInput;
    }
}
