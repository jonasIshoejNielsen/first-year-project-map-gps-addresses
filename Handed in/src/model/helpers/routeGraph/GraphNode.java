package model.helpers.routeGraph;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GraphNode implements Serializable {
    private Point2D coordinate;
    private GraphNode next;
    private List<GraphEdge> edges;
    private float distance, traveledDistance;
    private GraphNode bestNodeToThis;

    GraphNode(Point2D coordinate, GraphNode next) {
        this.coordinate = coordinate;
        this.next = next;
        distance = Float.MAX_VALUE;
        traveledDistance = Float.MAX_VALUE;
        edges = new ArrayList<>();
    }

    public void addEdge(GraphEdge edge){
        edges.add(edge);
    }

    public GraphNode getNext() {
        return next;
    }

    public Point2D getCoordinate() {
        return coordinate;
    }

    public List<GraphEdge> getEdges() {
        return edges;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public GraphNode getBestNodeToThis() {
        return bestNodeToThis;
    }

    public void setBestNodeToThis(GraphNode bestNodeToThis) {
        this.bestNodeToThis = bestNodeToThis;
    }

    public float getTraveledDistance() {
        return traveledDistance;
    }

    public void setTraveledDistance(float traveledDistance) {
        this.traveledDistance = traveledDistance;
    }

    public float getModifiedDistance() {
        return traveledDistance + distance;
    }

    public String getName() {
        if(bestNodeToThis == null) return "Start";
        for(GraphEdge edge : getEdges() ){
            if(edge.getTo().equals(bestNodeToThis) || edge.getFrom().equals(bestNodeToThis)){
                if(edge.getRoadName().equals(""))continue;
                return edge.getRoadName();
            }
        }
        return "";
    }

    public GraphEdge getEdgeToThis(RouteType searchType) {
        if(bestNodeToThis == null) throw new RuntimeException("Not in route");
        for(GraphEdge edge : getEdges()){
            if (!edge.getTo().equals(bestNodeToThis) && !edge.getFrom().equals(bestNodeToThis)) continue;
            if(searchType == RouteType.CAR && edge.getSpeed() < 1) continue;
            return edge;
        }
        throw new RuntimeException("No edge leading to this, should never happen");
    }
}
