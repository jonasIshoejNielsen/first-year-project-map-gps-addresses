package model.helpers.routeGraph;

import java.awt.geom.Point2D;
import java.util.*;

public class Dijkstra {
    private GraphMap graphMap;

    private HashMap<Point2D, VisitedNodeInformation> markedMapToDistanceTraveled = new HashMap<>();
    private PriorityQueue<VisitedNodeInformation> routeQueue;
    private GraphNode startNode;
    private VisitedNodeInformation destinationNodeInformation;

    private boolean useSpeedCalculation = false;
    private static final float HEURISTIC_SPEED = 130;
    private static final float HEURISTIC_FACTOR = 1f;
    private final static int METER_PER_KILOMETER = 1000;

    private boolean drawVisited = false;
    private RouteDescription routeDescription;
    private RouteType searchType;

    public List<Point2D> findRoute(GraphMap _graphMap, Point2D startPoint, Point2D destinationPoint, RouteType routeType, boolean calculateSpeed) {
        graphMap = _graphMap;
        useSpeedCalculation = calculateSpeed;
        searchType          = routeType;
        routeDescription = null;
        RouteDescriptionBuilder routeDescriptionBuilder = new RouteDescriptionBuilder();
        List<Point2D> routePoints                       = new ArrayList<>();

        GraphNode destinationNode = graphMap.getGraphNode(destinationPoint);
        startNode       = graphMap.getGraphNode(startPoint);
        //bestFactor();     //for finding best factor to the route

        routeQueue      = new PriorityQueue<>();
        destinationNodeInformation    = new VisitedNodeInformation(Float.MAX_VALUE, Float.MAX_VALUE, null, destinationNode);
        VisitedNodeInformation startNodeInformation = new VisitedNodeInformation(0, 0, null, startNode);
        routeQueue.add(startNodeInformation);
        markedMapToDistanceTraveled = new HashMap<>();
        markedMapToDistanceTraveled.put(startNode.getCoordinate(), startNodeInformation);
        markedMapToDistanceTraveled.put(destinationNodeInformation.getNode().getCoordinate(), destinationNodeInformation);

        if(startPoint == destinationPoint) return routePoints;
        if(pollRouteQueue()) {
            buildRoutePoints(routePoints);
            buildRouteDescription(routeDescriptionBuilder);
        } else {
            routeDescriptionBuilder.createRoute(searchType);
            routeDescriptionBuilder.noRouteFound();
            routeDescriptionBuilder.setRouteTime();
            routeDescription = routeDescriptionBuilder.getRouteDescription();
            routePoints.add(destinationNode.getCoordinate());
            routePoints.add(startNode.getCoordinate());
        }
        return routePoints;
    }

    private void checkEdges(VisitedNodeInformation oldNodeInformation){
        GraphNode oldNode = oldNodeInformation.getNode();
        if(oldNode.equals(destinationNodeInformation.getNode())) return;
        for(GraphEdge edge : oldNode.getEdges()){
            if(!edgeIsTraversable(edge, oldNode)) continue;
            GraphNode nextNode;
            if(oldNode.equals(edge.getTo())) nextNode = edge.getFrom();
            else nextNode = edge.getTo();

            VisitedNodeInformation nextNodeInformation =  markedMapToDistanceTraveled.get(nextNode.getCoordinate());
            if(nextNode.equals(oldNode)) continue;

            float edgeDistance = MathConstants.getDistanceInMetersSimple(oldNode, nextNode);
            if(useSpeedCalculation) edgeDistance /= METER_PER_KILOMETER * edge.getSpeed();


            float distanceTraveled = oldNodeInformation.getTraveledDistance() + edgeDistance;

            float distanceToDestination = MathConstants.getDistanceInMetersSimple(destinationNodeInformation.getNode(), nextNode);
            if (useSpeedCalculation) distanceToDestination /= METER_PER_KILOMETER * HEURISTIC_SPEED;

            distanceToDestination *= HEURISTIC_FACTOR;

            if(nextNodeInformation == null) nextNodeInformation = new VisitedNodeInformation(Float.MAX_VALUE, Float.MAX_VALUE, null, nextNode);
            if(nextNodeInformation.getTraveledDistance() > distanceTraveled) {
                nextNodeInformation.setTraveledDistance(distanceTraveled);
                nextNodeInformation.setBestNodeToThis(oldNode);
                nextNodeInformation.setDistance(distanceToDestination);
                routeQueue.remove(nextNodeInformation);
                markedMapToDistanceTraveled.put(nextNode.getCoordinate(), nextNodeInformation);
                routeQueue.add(nextNodeInformation);
            }
        }
    }

    private boolean edgeIsTraversable(GraphEdge edge, GraphNode node) {
        if(searchType == RouteType.CAR) {
            if(edge.getTo().equals(node) && edge.getOneWay() == 1) return false;
            if(edge.getFrom().equals(node) && edge.getOneWay() == -1) return false;
            if(edge.getSpeed() < 1) return false;
        }
        else {
            if (edge.getSpeed() >= 80) return false;
            if(searchType == RouteType.WALK && !edge.getIsWalking()) return false;
            if(searchType == RouteType.BIKE && !edge.getIsBickingAllowed()) return false;
        }
        return true;
    }

    private boolean pollRouteQueue() {
        List<GraphNode> nodesToDestination = new ArrayList<>();
        while (checkIfDone()) {
            VisitedNodeInformation nodeInformation = routeQueue.poll();
            if (nodeInformation.getNode() == destinationNodeInformation.getNode() && destinationNodeInformation.getTraveledDistance() > nodeInformation.getTraveledDistance()) {
                nodesToDestination.add(nodeInformation.getNode());
                destinationNodeInformation.setTraveledDistance(nodeInformation.getTraveledDistance());
            } else {
                checkEdges(nodeInformation);
            }
        }
        for(GraphNode graphNode : nodesToDestination){
            routeQueue.add(markedMapToDistanceTraveled.get(graphNode.getCoordinate()));
        }
        return destinationNodeInformation.getBestNodeToThis() != null;
    }

    private boolean checkIfDone() {
        boolean empty = routeQueue.isEmpty();
        if(empty) return false;
        boolean dist = routeQueue.peek().getTraveledDistance() > destinationNodeInformation.getTraveledDistance();
        if(dist) return false;
        return true;
    }

    private void buildRoutePoints(List<Point2D> routePoints) {
        VisitedNodeInformation information = markedMapToDistanceTraveled.get(destinationNodeInformation.getNode().getCoordinate());

        if(information == null) return;
        GraphNode node = information.getBestNodeToThis();
        while (node != null && node != startNode){
            routePoints.add(node.getCoordinate());
            information = markedMapToDistanceTraveled.get(node.getCoordinate());
            node = information.getBestNodeToThis();
        }
    }
    private void buildRouteDescription(RouteDescriptionBuilder routeDescriptionBuilder) {

        if(destinationNodeInformation.getBestNodeToThis() == null) return;
        GraphNode node = destinationNodeInformation.getBestNodeToThis();
        routeDescriptionBuilder.createRoute(searchType);
        VisitedNodeInformation nodeInformation;
        while (node != null && node != startNode){
            nodeInformation = markedMapToDistanceTraveled.get(node.getCoordinate());
            node = nodeInformation.getBestNodeToThis();
            routeDescriptionBuilder.updateRoute(nodeInformation);
        }
        routeDescriptionBuilder.setRouteTime();
        routeDescription = routeDescriptionBuilder.getRouteDescription();
    }

    public Collection<? extends Point2D> getVisited() {
        ArrayList<Point2D> visited = new ArrayList<>();
        for(Point2D point2D : markedMapToDistanceTraveled.keySet()){
            if(drawVisited) visited.add(point2D);
        }
        return visited;
    }

    public List<Point2D> updateRoute(Point2D newEndCoords) {
        routeDescription = null;
        if(destinationNodeInformation.getBestNodeToThis() != null) routeQueue.add(destinationNodeInformation);
        GraphNode newDestination = graphMap.getGraphNode(newEndCoords);
        if(newEndCoords != null) {
            VisitedNodeInformation newDestinationInfo = markedMapToDistanceTraveled.get(newDestination.getCoordinate());
            if (newDestinationInfo == null) {
                List<VisitedNodeInformation> graphNodesInformation = new ArrayList<>();
                for(VisitedNodeInformation visitedInformation : routeQueue){
                    graphNodesInformation.add(visitedInformation);
                }
                destinationNodeInformation    = new VisitedNodeInformation(Float.MAX_VALUE, Float.MAX_VALUE, null, newDestination);
                markedMapToDistanceTraveled.put(destinationNodeInformation.getNode().getCoordinate(), destinationNodeInformation);
                routeQueue = new PriorityQueue<>();
                addGraphNodesToQueue(graphNodesInformation);
            }
            else{
                destinationNodeInformation = newDestinationInfo;
            }
        }
        List<Point2D> routePoints = new ArrayList<>();

        routePoints.add(destinationNodeInformation.getNode().getCoordinate());
        if(destinationNodeInformation.getBestNodeToThis() != null || pollRouteQueue()){
            buildRoutePoints(routePoints);
        } else {
            RouteDescriptionBuilder routeDescriptionBuilder = new RouteDescriptionBuilder();
            routeDescriptionBuilder.createRoute(searchType);
            routeDescriptionBuilder.noRouteFound();
            routeDescriptionBuilder.setRouteTime();
            routePoints.add(destinationNodeInformation.getNode().getCoordinate());
            routePoints.add(startNode.getCoordinate());
        }
        routePoints.add(startNode.getCoordinate());

        return routePoints;
    }

    private void addGraphNodesToQueue(List<VisitedNodeInformation> graphNodesInformation) {
        for(VisitedNodeInformation nodeInformationToAdd: graphNodesInformation){
            float distanceToDestination = MathConstants.getDistanceInMetersSimple(destinationNodeInformation.getNode(), nodeInformationToAdd.getNode());
            if (useSpeedCalculation) distanceToDestination /= HEURISTIC_SPEED;
            distanceToDestination *= HEURISTIC_FACTOR;
            nodeInformationToAdd.setDistance(distanceToDestination);
            routeQueue.add(nodeInformationToAdd);
        }
    }

    public RouteDescription getRouteDescription() {
        if(routeDescription == null) {
            if(destinationNodeInformation.getBestNodeToThis() != null){
                RouteDescriptionBuilder routeDescriptionBuilder = new RouteDescriptionBuilder();
                buildRouteDescription(routeDescriptionBuilder);
            }
        }
        RouteDescription returnRoute = routeDescription;
        routeDescription = null;
        return returnRoute;
    }

    public void toggleDrawVisited() {
        drawVisited = !drawVisited;
    }

    public RouteType getRouteSearchType() {
        return searchType;
    }


    public boolean isDrawVisitedOn() {
        return drawVisited;
    }

    public boolean isUsingSpeedCalculation() {
        return useSpeedCalculation;
    }
}
