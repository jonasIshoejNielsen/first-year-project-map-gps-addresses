package model.osm;

public class OSMNode implements IOSMObject{
    private float lon, lat;

    public OSMNode(float lon, float lat) {
        this.lon = lon;
        this.lat = lat;
    }

    public float getLon() {
        return lon;
    }

    public float getLat() {
        return lat;
    }
}
