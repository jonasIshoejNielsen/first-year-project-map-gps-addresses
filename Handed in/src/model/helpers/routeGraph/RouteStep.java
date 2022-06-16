package model.helpers.routeGraph;

import view.components.RouteDirection;

public class RouteStep{
    String description, distance;
    RouteDirection direction;
    RouteStep(String description, RouteDirection direction, String distance){
        this.description    = description;
        this.direction      = direction;
        this.distance       = distance;
    }

    public String getDescription() {
        return description;
    }

    public String getDistance() {
        return distance;
    }

    public RouteDirection getDirection() {
        return direction;
    }
}
