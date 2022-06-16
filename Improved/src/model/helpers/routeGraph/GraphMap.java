package model.helpers.routeGraph;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.*;

public class GraphMap implements Serializable {
    private GraphNodeForMap[] table;
    private final int MASK = 0x7fffffff;

    private boolean useTestMethods;
    private HashMap<Integer, Boolean> test;
    private int collisions;

    public GraphMap(int capacity, float lonFactor, boolean useTestMethods){
        this.useTestMethods = useTestMethods;
        if(useTestMethods){
            test = new HashMap<>();
            collisions = 0;
        }
        MathConstants.setLonFactor(lonFactor);
        table = new GraphNodeForMap[capacity];
    }

    public void put(Point2D coordinates) {
        int position = (coordinates.hashCode() & MASK) % (table.length - 1);

        if(!contains(coordinates)) {
            table[position] = new GraphNodeForMap(coordinates, table[position]);
            if(useTestMethods){
                if(test.containsKey(position)) collisions++;
                else test.put(position, true);
            }
        }
    }

    public GraphNode getGraphNode(Point2D coordinate) {
        if(table == null || coordinate == null ) return null;
        int position = (coordinate.hashCode() & MASK) % (table.length - 1);
        for (GraphNodeForMap n = table[position]; n != null; n = n.getNext()) {
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
        for (GraphNodeForMap n = table[position]; n != null; n = n.getNext()) {
            if (n.getCoordinate().equals(coordinate)) {
                return true;
            }
        }
        return false;
    }

    public int getCollisions(){
        return collisions;
    }

    private class GraphNodeForMap extends GraphNode implements Serializable{
        GraphNodeForMap next;
        GraphNodeForMap(Point2D coordinate, GraphNodeForMap next) {
            super(coordinate);
            this.next = next;
        }

        public GraphNodeForMap getNext() {
            return next;
        }
    }

}
