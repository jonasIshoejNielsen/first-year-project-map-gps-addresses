package model.helpers.parsers;

import model.helpers.AddressSearch.HousePlacement;
import model.osm.OSMAddress;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.List;

public class PostcodeAndCityCenter implements Serializable {
    private String postcode;
    private String city;
    private Point2D center;

    public PostcodeAndCityCenter(String postcode, String city, List<Point2D> points) {
        this.postcode = postcode;
        this.city = city;

        double lon = 0;
        double lat = 0;
        if(points == null){
            System.out.println(postcode + "   "+city);
        }
        for(Point2D point : points){
            if(point == null){
                System.out.println(postcode + "   "+city);
            }
            lon+=point.getX();
            lat+=point.getY();
        }

        this.center = new Point2D.Float(new Double(lon/points.size()).floatValue(), new Double(lat/points.size()).floatValue());
    }

    public String getPostcode() {
        return postcode;
    }

    public String getCity() {
        return city;
    }

    public Point2D getCenter() {
        return center;
    }

    public OSMAddress toOSMAddress() {
        HousePlacement placement = new HousePlacement(null, new Double(center.getX()).floatValue(),new Double(center.getY()).floatValue());
        return new OSMAddress(city,placement,postcode,null);
    }
}
