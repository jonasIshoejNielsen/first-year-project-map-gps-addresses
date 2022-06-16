package model.helpers.routeGraph;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.*;

public class GraphMap implements Serializable {
    private GraphNode[] table;
    private final int MASK = 0x7fffffff;
    private HashSet<Point2D> markedMapToDistanceTraveled = new HashSet<>();
    private GraphQueue routeQueue;
    private GraphNode startNode, destinationNode;
    private float bestDistance;
    private boolean useSpeedCalculation = false;
    private static final float HEURISTIC_SPEED = 130;
    private static final float HEURISTIC_FACTOR = 1f;

    private boolean useTestMethods;
    private HashMap<Integer, Boolean> test;
    private int collisions;
    private boolean drawVisited = false;
    private RouteDescription routeDescription;
    private RouteType searchType;
    private final static int METER_PER_KILOMETER = 1000;

    public GraphMap(int capacity, float lonFactor, boolean useTestMethods){
        this.useTestMethods = useTestMethods;
        if(useTestMethods){
            test = new HashMap<>();
            collisions = 0;
        }
        MathConstants.setLonFactor(lonFactor);
        table = new GraphNode[capacity];
    }

    public void put(Point2D coordinates) {
        int position = (coordinates.hashCode() & MASK) % (table.length - 1);

        if(!contains(coordinates)) {
            table[position] = new GraphNode(coordinates, table[position]);
            if(useTestMethods){
                if(test.containsKey(position)) collisions++;
                else test.put(position, true);
            }
        }
    }

    public GraphNode getGraphNode(Point2D coordinate) {
        if(table == null || coordinate == null ) return null;
        int position = (coordinate.hashCode() & MASK) % (table.length - 1);
        for (GraphNode n = table[position]; n != null; n = n.getNext()) {
            if (n.getCoordinate().equals(coordinate)) {
                return n;
            }
        }
        return null;
    }

    public void addEdge(GraphNode fromNode, GraphNode toNode, float speed, boolean bicycleAllowed, boolean walkingAllowed, String roadName, int oneWay){
        GraphEdge edge = new GraphEdge(fromNode, toNode, speed, bicycleAllowed, walkingAllowed, roadName, oneWay);
        fromNode.addEdge(edge);
        toNode.addEdge(edge);
    }

    public boolean contains(Point2D coordinate) {
        int position = (coordinate.hashCode() & MASK) % (table.length - 1);
        for (GraphNode n = table[position]; n != null; n = n.getNext()) {
            if (n.getCoordinate().equals(coordinate)) {
                return true;
            }
        }
        return false;
    }

    public int getCollisions(){
        return collisions;
    }

    public List<Point2D> findRoute(Point2D startPoint, Point2D destinationPoint, RouteType routeType, boolean calculateSpeed) {
        useSpeedCalculation = calculateSpeed;
        searchType          = routeType;
        routeDescription = null;
        resetVisitedGraphNodes();
        RouteDescriptionBuilder routeDescriptionBuilder = new RouteDescriptionBuilder();
        List<Point2D> routePoints                       = new ArrayList<>();

        destinationNode = getGraphNode(destinationPoint);
        startNode = getGraphNode(startPoint);
        //bestFactor();     //for finding best factor to the route

        routeQueue      = new GraphQueue();
        bestDistance    = Float.MAX_VALUE;
        startNode.setTraveledDistance(0f);
        routeQueue.add(startNode);
        markedMapToDistanceTraveled = new HashSet<>();
        markedMapToDistanceTraveled.add(startNode.getCoordinate());
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

    private void checkEdges(GraphNode currentNode){
        if(currentNode.equals(destinationNode)) return;
        for(GraphEdge edge : currentNode.getEdges()){
            if(!edgeIsTraversable(edge, currentNode)) continue;
            GraphNode node;
            if(currentNode.equals(edge.getTo())) node = edge.getFrom();
            else node = edge.getTo();

            if(node.equals(currentNode) || node.equals(currentNode.getBestNodeToThis())) continue;

            float edgeDistance = MathConstants.getDistanceInMetersSimple(currentNode, node);
            if(useSpeedCalculation) edgeDistance /= METER_PER_KILOMETER * edge.getSpeed();


            float distanceTraveled = currentNode.getTraveledDistance() + edgeDistance;

            float distanceToDestination = MathConstants.getDistanceInMetersSimple(destinationNode, node);
            if (useSpeedCalculation) distanceToDestination /= METER_PER_KILOMETER * HEURISTIC_SPEED;

            distanceToDestination *= HEURISTIC_FACTOR;

            if(node.getTraveledDistance() > distanceTraveled) {
                markedMapToDistanceTraveled.add(node.getCoordinate());
                node.setDistance(distanceToDestination);
                node.setTraveledDistance(distanceTraveled);
                node.setBestNodeToThis(currentNode);
                routeQueue.remove(node);
                routeQueue.add(node);
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
            GraphNode node = routeQueue.poll();
            if (node == destinationNode && bestDistance > node.getTraveledDistance()) {
                nodesToDestination.add(node);
                bestDistance = node.getTraveledDistance();
            } else {
                checkEdges(node);
            }
        }
        for(GraphNode graphNode : nodesToDestination){
            routeQueue.add(graphNode);
        }
        return destinationNode.getBestNodeToThis() != null;
    }

    private boolean checkIfDone() {
        boolean empty = routeQueue.isEmpty();
        if(empty) return false;
        boolean dist = routeQueue.peek().getTraveledDistance() > bestDistance;
        if(dist) return false;
        return true;
    }

    private void buildRoutePoints(List<Point2D> routePoints) {
        GraphNode node = destinationNode.getBestNodeToThis();
        while (node != null && node != startNode){
            routePoints.add(node.getCoordinate());
            node = node.getBestNodeToThis();
        }
    }
    private void buildRouteDescription(RouteDescriptionBuilder routeDescriptionBuilder) {
        GraphNode node = destinationNode.getBestNodeToThis();
        routeDescriptionBuilder.createRoute(searchType);

        while (node != null && node != startNode){
            node = node.getBestNodeToThis();
            routeDescriptionBuilder.updateRoute(node);
        }
        routeDescriptionBuilder.setRouteTime();
        routeDescription = routeDescriptionBuilder.getRouteDescription();
    }

    public void resetVisitedGraphNodes() {
        for(Point2D point2D : markedMapToDistanceTraveled){
            GraphNode graphNode = getGraphNode(point2D);
            resetGraphNode(graphNode);
        }
    }
    private void resetGraphNode(GraphNode graphNode){
        graphNode.setDistance(Float.MAX_VALUE);
        graphNode.setTraveledDistance(Float.MAX_VALUE);
        graphNode.setBestNodeToThis(null);
    }
    public Collection<? extends Point2D> getVisited() {
        ArrayList<Point2D> visited = new ArrayList<>();
        for(Point2D point2D : markedMapToDistanceTraveled){
            if(drawVisited) visited.add(point2D);
        }
        return visited;
    }

    public List<Point2D> updateRoute(Point2D newEndCoords) {
        routeDescription = null;
        routeQueue.add(destinationNode);
        GraphNode newDestination = getGraphNode(newEndCoords);
        if(newEndCoords != null) {
            destinationNode = newDestination;
            if (destinationNode.getBestNodeToThis() == null) {
                List<GraphNode> graphNodes = routeQueue.getAllNodes();
                bestDistance = Float.MAX_VALUE;
                routeQueue = new GraphQueue();
                addGraphNodesToQueue(graphNodes);
            }
        }
        List<Point2D> routePoints = new ArrayList<>();

        routePoints.add(destinationNode.getCoordinate());
        if(destinationNode.getBestNodeToThis() != null || pollRouteQueue()){
            buildRoutePoints(routePoints);
        } else {
            RouteDescriptionBuilder routeDescriptionBuilder = new RouteDescriptionBuilder();
            routeDescriptionBuilder.createRoute(searchType);
            routeDescriptionBuilder.noRouteFound();
            routeDescriptionBuilder.setRouteTime();
            routePoints.add(destinationNode.getCoordinate());
            routePoints.add(startNode.getCoordinate());
        }
        routePoints.add(startNode.getCoordinate());

        return routePoints;
    }

    private void addGraphNodesToQueue(List<GraphNode> graphNodes) {
        for(GraphNode nodeToAdd: graphNodes){

            float distanceToDestination = MathConstants.getDistanceInMetersSimple(destinationNode, nodeToAdd);
            if (useSpeedCalculation) distanceToDestination /= HEURISTIC_SPEED;
            distanceToDestination *= HEURISTIC_FACTOR;
            nodeToAdd.setDistance(distanceToDestination);
            routeQueue.add(nodeToAdd);
        }
    }

    public RouteDescription getRouteDescription() {
        if(routeDescription == null) {
            if(destinationNode.getBestNodeToThis() != null){
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
