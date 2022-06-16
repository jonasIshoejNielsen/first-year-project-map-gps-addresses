package model;

import model.helpers.drawing.EnhancedRoadShape;
import model.helpers.drawing.EnhancedShape;
import model.helpers.KdTree;
import model.helpers.routeGraph.GraphMap;
import model.helpers.routeGraph.RouteType;
import model.osm.OSMType;
import model.osm.OSMTypeParent;

import java.awt.geom.*;
import java.io.Serializable;
import java.util.*;

public class CanvasModel implements Serializable{
    private HashMap<OSMType, KdTree> mapTree;
    private List<EnhancedShape> coastline;
    private List<OSMType> kdTreeTypes;
    private float lonFactor;

    private float[] bounds;

    public CanvasModel(Map<OSMType, List<EnhancedShape>> map, float[] bounds, float lonFactor){
        mapTree         = new HashMap<>();
        kdTreeTypes     = new ArrayList<>();
        createKDTrees(map);
        this.bounds = bounds;
        this.lonFactor = lonFactor;
    }

    private void createKDTrees(Map<OSMType, List<EnhancedShape>> map) {
        for(OSMType type: map.keySet()) {
            if(type == OSMType.UNKNOWN)                     continue;
            else if(type == OSMType.COASTLINE) {
                coastline = map.get(type);
                continue;
            } else if (map.get(type).size() <= 0)           continue;
              else if (!type.isArea() && !type.isLine())    continue;

            KdTree tree = new KdTree(map.get(type));
            mapTree.put(type, tree);
            kdTreeTypes.add(type);
        }
    }

    public List<OSMType> getKdTreeTypes() {
        return kdTreeTypes;
    }

    public KdTree getTree(OSMType type) {
        return mapTree.get(type);
    }

    public float[] getBounds() {
        return bounds;
    }

    public List<EnhancedShape> getCoastline(Rectangle2D viewRect) {
        List<EnhancedShape> coastlineToReturn = new ArrayList<>();
        for(EnhancedShape enhancedShape : coastline){
            if(enhancedShape.getShape().intersects(viewRect))   coastlineToReturn.add(enhancedShape);
        }
        return coastlineToReturn;
    }
    public boolean noCoastlines(){
        return coastline.size() == 0;
    }

    public float getLonFactor() {
        return lonFactor;
    }

    public Point2D findNearestNode(Point2D point, RouteType routeType, GraphMap graphMap) {
        Map<OSMType, EnhancedShape> shapes = new HashMap<>();

        for(OSMType type : OSMTypeParent.HIGHWAY.getChildren()) {

            KdTree tree = mapTree.get(type);
            if(tree == null) continue;
            float x = (float) point.getX();
            float y = (float) point.getY();
            EnhancedShape shape = tree.getNearestNeighbor(x-0.005F, y-0.005F, x+0.005F, y+0.005F);

            if(shape == null) {
                continue;
            }

            if(routeType != RouteType.NOT_A_ROUTE) {
                if (type.getParent() == null ||
                        type.getParent() != OSMTypeParent.HIGHWAY) continue;
                if (routeType == RouteType.CAR && (type.getSpeed() < 1 || type.getSpeed() > 80))
                    continue;

                if(!(shape instanceof EnhancedRoadShape)) continue;
                EnhancedRoadShape enhancedRoadShape = (EnhancedRoadShape) shape;
                if (routeType == RouteType.WALK && !enhancedRoadShape.isWalkingAllowed()) continue;
                if (routeType == RouteType.BIKE && !enhancedRoadShape.isBicycleAllowed()) continue;

            }

            shapes.put(type, shape);
        }

        float dist = Float.MAX_VALUE;
        Point2D.Float closestPoint = null;
        for(OSMType type : shapes.keySet()) {
            EnhancedShape shape = shapes.get(type);
            PathIterator pathIterator = shape.getShape().getPathIterator(null);
            while(!pathIterator.isDone()) {
                float[] path = new float[6];
                pathIterator.currentSegment(path);
                Point2D.Float newPoint = new Point2D.Float(path[0], path[1]);
                if(graphMap == null || graphMap.contains(newPoint)) {
                    if (dist > point.distance(newPoint)) {
                        dist = (float) point.distance(newPoint);
                        closestPoint = newPoint;
                    }
                }
                pathIterator.next();
            }
        }
        return closestPoint;
    }

    public HashMap<OSMType, KdTree> getMapTree() {
        return mapTree;
    }
}