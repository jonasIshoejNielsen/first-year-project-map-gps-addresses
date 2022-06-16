package model.helpers.parsers;

import model.helpers.drawing.EnhancedShape;
import model.helpers.maps.OSMRelationMap;
import model.osm.*;

import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class RelationShapeBuilder {

    static void createShapes(Map<OSMType, List<EnhancedShape>> enumMap, OSMRelationMap relationMap){
        for (OSMRelation relation : relationMap){
                if(relation != null) {
                    enumMap.get(relation.getType()).add(new EnhancedShape(createRelationShape(relation, relationMap)));
            }
        }
    }


    private static Shape createRelationShape(OSMRelation relation, OSMRelationMap relationMap) {
        Path2D path = new Path2D.Float();
        boolean connect = false;
        OSMRelationType previousType = OSMRelationType.NOTSET;
        ArrayList<OSMRelationNode> pathList = new ArrayList<>();
        path.setWindingRule(Path2D.WIND_EVEN_ODD);
        List<OSMRelationNode> nodeList = relation.getNodeList();
        for (OSMRelationNode node : nodeList) {
            if (node.getObject() != null) {
                if (connect) {
                    if (previousType == node.getType()) {
                        pathList.add(node);
                    } else {
                        if (!pathList.isEmpty()) {
                            pathList = sortPathList(pathList, path);
                            for (OSMRelationNode pathNode : pathList) {
                                connect = buildPath(pathNode, connect, relationMap, path);
                            }
                        }
                        pathList.clear();
                        connect = false;
                        buildPath(node, connect, relationMap, path);
                    }
                } else {
                    buildPath(node, connect, relationMap, path);
                }
                previousType = node.getType();
                connect = true;
            }
        }
        if (!pathList.isEmpty()) {
            pathList = sortPathList(pathList, path);
            for (OSMRelationNode pathNode : pathList) {
                connect = buildPath(pathNode, connect, relationMap, path);
            }
        }
        return path;
    }

    private static ArrayList<OSMRelationNode> sortPathList(ArrayList<OSMRelationNode> pathList, Path2D path) {
        ArrayList<OSMRelationNode> oldList = new ArrayList<>(pathList);
        for(OSMRelationNode node : pathList){
            IOSMObject osmObject = node.getObject();
            if(!(osmObject instanceof OSMWay)) {
                return oldList;
            }
        }
        Point2D startPoint = path.getCurrentPoint();
        ArrayList<OSMRelationNode> sortedPathList = new ArrayList<>();
        OSMWay currentWay;
        OSMNode endNode = new OSMNode(new Double(startPoint.getX()).floatValue(), new Double(startPoint.getY()).floatValue());
        int counter = pathList.size();
        for(int i = 0; i < counter; i++){
            OSMRelationNode node = findNextWay(pathList, endNode);
            if(node == null) {
                return oldList;
            }
            sortedPathList.add(node);
            currentWay = (OSMWay) node.getObject();
            endNode = currentWay.get(currentWay.size()-1);
        }
        return sortedPathList;
    }

    private static OSMRelationNode findNextWay(ArrayList<OSMRelationNode> pathList, OSMNode endNode){
        //Check for matches
        for(int i = 0; i < pathList.size(); i++){
            OSMWay way = (OSMWay) pathList.get(i).getObject();
            OSMNode node = way.get(0);
            if(node.getLat() == endNode.getLat() && node.getLon() == endNode.getLon())
                return pathList.remove(i);
        }

        //Check for reverse matches
        for(int i = 0; i < pathList.size(); i++){
            OSMWay way = (OSMWay) pathList.get(i).getObject();
            OSMNode node = way.get(way.size()-1);
            if(node.getLat() == endNode.getLat() && node.getLon() == endNode.getLon()) {
                OSMWay reversedWay = new OSMWay();
                for(int j = way.size()-1; j >= 0; j--){
                    reversedWay.add(way.get(j));
                }
                return new OSMRelationNode(reversedWay, pathList.remove(i).getType());
            }
        }
        return null;
    }

    private static boolean buildPath(OSMRelationNode node, boolean connect, OSMRelationMap relationMap, Path2D path){
        IOSMObject osmObject = node.getObject();
        OSMNode osmNode;
        if (osmObject instanceof OSMWay){
            OSMWay way = (OSMWay) osmObject;
            OSMNode startNode = way.get(0);
            OSMNode endNode = way.get(way.size()-1);

            if(startNode.getLat() == endNode.getLat() && startNode.getLon() == endNode.getLon()) connect = false;
            int i = 0;
            if (!connect) {
                path.moveTo(startNode.getLon(), startNode.getLat());
                i++;
            }
            while (i < way.size()) {
                osmNode = way.get(i);
                path.lineTo(osmNode.getLon(), osmNode.getLat());
                i++;
            }
        } else if (osmObject instanceof OSMRelation){
            path.append(createRelationShape(relationMap.get(node.getId()), relationMap), connect);
        } else if (osmObject instanceof OSMNode){
            osmNode = (OSMNode) osmObject;
            if (connect) {
                path.lineTo(osmNode.getLon(), osmNode.getLat());
            } else
                path.moveTo(osmNode.getLon(), osmNode.getLat());

        }
        return connect;
    }
}