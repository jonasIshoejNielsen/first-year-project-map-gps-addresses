package model.helpers.drawing;

import java.awt.*;
import java.awt.geom.Point2D;

public class EnhancedAddressShape extends EnhancedShape {
    private String stringValue;
    private Point2D point2D;

    public EnhancedAddressShape(Shape shape, Point2D point2D, String name) {
        super(shape);
        this.stringValue = name.intern();
        this.point2D = point2D;
    }

    public String getStringValue() {
        return stringValue;
    }

    public Point2D getPoint2D() {
        return point2D;
    }

}
