package model.helpers.AddressSearch;

import java.awt.geom.Point2D;
import java.io.Serializable;

public class HousePlacement implements Serializable {
    private String houseNumber;
    private float lon, lat;

    public HousePlacement(String houseNumber, float lon, float lat) {
        this.houseNumber = houseNumber;
        this.lon = lon;
        this.lat = lat;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public float getLon() {
        return lon;
    }

    public float getLat() {
        return lat;
    }

    public Point2D getCoordinate() {
        return new Point2D.Float(lon, lat);
    }
}
