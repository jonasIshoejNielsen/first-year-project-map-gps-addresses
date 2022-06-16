package model.osm;

import java.util.ArrayList;

public enum OSMTypeParent {
    AEROWAY, AMENITY, BARRIER, BOUNDARY, BUILDING, CRAFT, HIGHWAY, HISTORIC,
    LANDUSE, LEISURE, MAN_MADE, MILITARY, NATURAL, PLACE, RAILWAY, WATERWAY;

    public ArrayList<OSMType> getChildren() {
        return OSMType.getChildren(this);
    }
}
