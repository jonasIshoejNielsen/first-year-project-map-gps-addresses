package model.helpers.drawing;

import java.awt.*;

public class EnhancedRoadShape extends EnhancedShape {
    private String name;
    private boolean isOneWay, isReverseDirection, isBicycleAllowed, isWalkingAllowed;
    private int maxSpeed;

    public EnhancedRoadShape(Shape shape, String name, boolean isOneWay, boolean isReverseDirection, boolean isBicycleAllowed, boolean isWalkingAllowed, int maxSpeed) {
        super(shape);
        this.name = name.intern();
        this.isOneWay = isOneWay;
        this.isReverseDirection = isReverseDirection;
        this.isBicycleAllowed = isBicycleAllowed;
        this.isWalkingAllowed = isWalkingAllowed;
        this.maxSpeed = maxSpeed;
    }

    public String getName() {
        return name;
    }

    public boolean isOneWay() {
        return isOneWay;
    }

    public boolean isBicycleAllowed() {
        return isBicycleAllowed;
    }

    public boolean isWalkingAllowed() {
        return isWalkingAllowed;
    }

    public int getMaxSpeed() {
        return maxSpeed;
    }

    public boolean isReversed() {
        return isReverseDirection;
    }
}
