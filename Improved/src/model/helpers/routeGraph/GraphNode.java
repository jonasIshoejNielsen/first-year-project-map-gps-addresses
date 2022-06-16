package model.helpers.routeGraph;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GraphNode implements Serializable {
    private Point2D coordinate;
    private List<GraphEdge> edges;

    GraphNode(Point2D coordinate) {
        this.coordinate = coordinate;
        edges = new ArrayList<>();
    }

    public void addEdge(GraphEdge edge){
        edges.add(edge);
    }


    public Point2D getCoordinate() {
        return coordinate;
    }

    public List<GraphEdge> getEdges() {
        return edges;
    }


    public String getName(GraphNode bestNodeToThis) {
        if(bestNodeToThis == null) return "Start";
        for(GraphEdge edge : getEdges() ){
            if(edge.getTo().equals(bestNodeToThis) || edge.getFrom().equals(bestNodeToThis)){
                if(edge.getRoadName().equals(""))continue;
                return edge.getRoadName();
            }
        }
        return "";
    }

    public GraphEdge getEdgeToThis(GraphNode bestNodeToThis, RouteType searchType) {
        if(bestNodeToThis == null) throw new RuntimeException("Not in route");
        for(GraphEdge edge : getEdges()){
            if (!edge.getTo().equals(bestNodeToThis) && !edge.getFrom().equals(bestNodeToThis)) continue;
            if(searchType == RouteType.CAR && edge.getSpeed() < 1) continue;
            return edge;
        }
        throw new RuntimeException("No edge leading to this, should never happen");
    }

}
