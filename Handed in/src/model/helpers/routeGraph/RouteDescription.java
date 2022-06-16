package model.helpers.routeGraph;

import view.components.RouteDirection;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RouteDescription {
    private float distanceInMeter, speedHours;
    private List<RouteStep> steps;
    private final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.#");
    private String roadFrom,  roadTo;

    public RouteDescription( ) {
        steps = new ArrayList<>();
    }

    public void setDistanceInMeter(float distanceInMeter) {
        this.distanceInMeter = distanceInMeter;
    }

    public void setSpeedHours(float speed) {
        this.speedHours = speed;
    }

    public List<RouteStep> getSteps() {
        Collections.reverse(steps);
        return steps;
    }

    public void addStep(String description, RouteDirection direction, String distance){
        steps.add(new RouteStep(description, direction, distance));
    }

    public String distanceToString() {
        return (distanceInMeter > 1000) ? DECIMAL_FORMAT.format((distanceInMeter / 1000)) + " km": DECIMAL_FORMAT.format(Math.round(distanceInMeter)) + " m";
    }

    public String speedToString() {
        float rest  = speedHours % 1;
        int hours   = Math.round(speedHours - rest);
        int minuts  = Math.round(rest * 60);
        if(hours != 0)  return new StringBuilder().append(hours).append(" timer, ").append(minuts).append(" min ").toString();
        else            return new StringBuilder().append(minuts).append(" min ").toString();
    }
    public String getRoadFrom() {
        return roadFrom;
    }

    public String getRoadTo() {
        return roadTo;
    }

    public void setRoadFrom(String roadFrom) {
        this.roadFrom = roadFrom;
    }

    public void setRoadTo(String roadTo) {
        this.roadTo = roadTo;
    }
}
