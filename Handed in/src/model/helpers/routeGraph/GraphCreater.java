package model.helpers.routeGraph;

import model.helpers.drawing.EnhancedRoadShape;
import model.helpers.drawing.EnhancedShape;
import model.helpers.KdTree;
import model.osm.OSMType;
import model.osm.OSMTypeParent;

import java.awt.*;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphCreater {
    private GraphMap graphMap;
    private int graphNodes = 0;

    public GraphCreater(Map<OSMType, List<EnhancedShape>> enumMap, int mapSize, float lonFactor, boolean useTestMethods) {
        graphMap = new GraphMap(mapSize, lonFactor, useTestMethods);
        for(OSMType type : enumMap.keySet()){
            if(type.getParent() != null && type.getParent() == OSMTypeParent.HIGHWAY) {
                List<EnhancedShape> list = enumMap.get(type);
                for(int i = 0; i < list.size(); i++) {
                    EnhancedShape shape = list.get(i);
                    if(shape instanceof EnhancedRoadShape)
                        addToGraphMap((EnhancedRoadShape) shape);
                }
            }
        }
    }

    public GraphCreater(HashMap<OSMType, KdTree> map, int mapSize, float lonFactor) {
        graphMap = new GraphMap(mapSize, lonFactor, false);
        for(OSMType type : map.keySet()){
            if(type.getParent() != null && type.getParent() == OSMTypeParent.HIGHWAY) {
                for(EnhancedShape shape : map.get(type).getAllShapes()){
                    if(shape instanceof EnhancedRoadShape)
                        addToGraphMap((EnhancedRoadShape) shape);
                }
            }
        }
    }

    private void addToGraphMap(EnhancedRoadShape enhancedRoadShape) {
        Shape shape = enhancedRoadShape.getShape();
        PathIterator pathIterator = shape.getPathIterator(null);
        GraphNode previousGraphNode = null;
        while (!pathIterator.isDone()){
            float[] coordinates = new float[6];
            pathIterator.currentSegment(coordinates);
            Point2D coordinate = new Point2D.Float(coordinates[0], coordinates[1]);
            graphMap.put(coordinate);
            graphNodes++;
            if(previousGraphNode == null){
                previousGraphNode = graphMap.getGraphNode(coordinate);
                continue;
            }
            GraphNode currentGraphNode = graphMap.getGraphNode(coordinate);
            if(enhancedRoadShape.isOneWay()){
                if(enhancedRoadShape.isReversed())
                    graphMap.addEdge(currentGraphNode, previousGraphNode, enhancedRoadShape.getMaxSpeed(), enhancedRoadShape.isWalkingAllowed(), enhancedRoadShape.isBicycleAllowed(), enhancedRoadShape.getName(), -1);
                else
                    graphMap.addEdge(previousGraphNode, currentGraphNode, enhancedRoadShape.getMaxSpeed(), enhancedRoadShape.isWalkingAllowed(), enhancedRoadShape.isBicycleAllowed(), enhancedRoadShape.getName(), 1);
            } else {
                graphMap.addEdge(currentGraphNode, previousGraphNode, enhancedRoadShape.getMaxSpeed(), enhancedRoadShape.isWalkingAllowed(), enhancedRoadShape.isBicycleAllowed(), enhancedRoadShape.getName(), 0);
            }
            previousGraphNode = currentGraphNode;
            pathIterator.next();
        }
    }

    public int getGraphNodes() {
        return graphNodes;
    }

    public GraphMap getGraphMap() {
        return graphMap;
    }
}
