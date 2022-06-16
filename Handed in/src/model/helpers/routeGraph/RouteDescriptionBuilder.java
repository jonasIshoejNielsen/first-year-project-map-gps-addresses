package model.helpers.routeGraph;

import view.components.RouteDirection;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class RouteDescriptionBuilder {
    private final static int BIKE_SPEED = 15, WALK_SPEED = 4, KILOMETER_CONVERT = 1000, KILOMETER = 1000;
    private String currentRoad = "Destination", distanceType = "";
    private float totalDistance = 0, timeTaken = 0, currentDistance = 0,  distance = 0;
    private DecimalFormat decimalFormat = new DecimalFormat("#.##");
    private GraphNode previousNode = null;
    private RouteDescription routeDescription;
    private DirectionFinder directionFinder;
    private RouteType routeType;

    public RouteDescriptionBuilder() {
        directionFinder = new DirectionFinder();
    }

    public void createRoute(RouteType routeType) {
        this.routeType = routeType;
        routeDescription = new RouteDescription();
        decimalFormat.setRoundingMode(RoundingMode.CEILING);
    }

    public void setRouteTime(){
        routeDescription.setDistanceInMeter(totalDistance);
        if(routeType == RouteType.BIKE) timeTaken = (totalDistance / KILOMETER_CONVERT) / BIKE_SPEED;
        else if (routeType == RouteType.WALK) timeTaken = (totalDistance / KILOMETER_CONVERT) / WALK_SPEED;
        routeDescription.setSpeedHours(timeTaken);
    }

    public void updateRoute(GraphNode newNode) {
        if(newNode == null) return;
        String roadName = newNode.getName();
        StringBuilder stringBuilder = new StringBuilder();

        if(roadName.equals("Start")) {
            routeDescription.setRoadFrom(currentRoad);
            setDistanceType();
            if(routeType == RouteType.WALK) stringBuilder.append("\nGå ad ").append(currentRoad);
            else stringBuilder.append("\nKør ad ").append(currentRoad);
            routeDescription.addStep(stringBuilder.toString(), RouteDirection.NONE, decimalFormat.format(distance) + distanceType);
            return;
        }

        if(currentRoad.equals("Destination")) {
            routeDescription.setRoadTo(roadName);
            currentRoad     = roadName;
            previousNode    = newNode;
        } else if(!currentRoad.equals(roadName)) {
            setCurrentDistance(previousNode, newNode);
            RouteDirection direction = directionFinder.getDirection(previousNode, newNode, newNode.getBestNodeToThis());

            if(roadName.isEmpty() && direction == RouteDirection.FORWARD){
                previousNode = newNode;
                return;
            }

            setDistanceType();

            stringBuilder.append(getDirectionText(direction, newNode));

            stringBuilder.append(currentRoad);
            routeDescription.addStep(stringBuilder.toString(), direction, decimalFormat.format(distance) + distanceType);
            if(roadName.isEmpty()) roadName = "Unavngivet vej";
            currentRoad         = roadName;
            timeTaken           += currentDistance / KILOMETER_CONVERT / previousNode.getEdgeToThis(routeType).getSpeed();
            totalDistance       += currentDistance;
            currentDistance     = 0;
            previousNode        = newNode;
        } else {
            setCurrentDistance(previousNode, newNode);
            previousNode = newNode;
        }
    }

    private String findHeading(GraphNode previous, GraphNode node) {
        //TODO: calculate heading north, south.. etc.
        return "Fortsæt i ";
    }

    private void setCurrentDistance(GraphNode previousNode, GraphNode currentNode) {
        currentDistance += MathConstants.getDistanceInMetersSimple(previousNode, currentNode);
    }

    private void setDistanceType(){
        distance = currentDistance;
        if(distance > KILOMETER) {
            distance /= KILOMETER_CONVERT;
            distanceType = " km";
            decimalFormat = new DecimalFormat("#.##");
        } else {
            distanceType = " m";
            decimalFormat = new DecimalFormat("#");
        }
    }

    public void noRouteFound() {
        routeDescription = new RouteDescription();
        routeDescription.addStep("Ingen rute kunne findes", RouteDirection.NONE, "");
    }

    public RouteDescription getRouteDescription() {
        return routeDescription;
    }


    private String getDirectionText(RouteDirection direction, GraphNode currentNode){
        switch (direction){
            case FORWARD:
                return "Fortsæt ligeud af ";
            case LEFT:
                return  "Drej til venstre af ";
            case RIGHT:
                return "Drej til højre af ";
            case NONE:
                return findHeading(previousNode, currentNode);
        }
        return null;
    }
}
