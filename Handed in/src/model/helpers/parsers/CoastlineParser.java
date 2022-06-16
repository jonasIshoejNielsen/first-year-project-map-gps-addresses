package model.helpers.parsers;

import model.helpers.drawing.EnhancedShape;
import model.osm.OSMNode;
import model.osm.OSMType;
import model.osm.OSMWay;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;

class CoastlineParser {
    private ArrayList<OSMWay> unclosedCoastlines = new ArrayList<>();
    private Map<OSMNode, OSMWay> coastlines;
    private Map<OSMType, List<EnhancedShape>> enumMap;
    private float minLat, minLon, maxLat, maxLon;
    private float factor;
    private ArrayList<OSMWay> closedCoastlines = new ArrayList<>();

    CoastlineParser(Map<OSMType, List<EnhancedShape>> enumMap){
        coastlines = new HashMap<>();
        this.enumMap = enumMap;
    }

    void insertCoastline(OSMWay way){
        // rank map for coastlines that can be merged with current way. Prepare them for merge and remove them from map.
        OSMWay coastlineBeforeWay = coastlines.remove(way.get(0));
        OSMWay coastlineAfterWay = coastlines.remove(way.get(way.size() - 1));
        OSMWay merged = new OSMWay();

        if (coastlineBeforeWay != null) {
            merged.addAll(coastlineBeforeWay.subList(0, coastlineBeforeWay.size() - 1));
        }
        merged.addAll(way);
        if (coastlineAfterWay != null && coastlineAfterWay != coastlineBeforeWay) {
            merged.addAll(coastlineAfterWay.subList(1, coastlineAfterWay.size()));
        }

        coastlines.put(merged.get(merged.size() - 1), merged);
        coastlines.put(merged.get(0), merged);
    }


    void addCoastlinesToMap() {
        Path2D path;
        OSMNode node;
        for (Map.Entry<OSMNode, OSMWay> coastline : coastlines.entrySet()) {
            OSMWay way = coastline.getValue();
            if(way.get(0) != way.get(way.size()-1)) {
                if (!unclosedCoastlines.contains(way))
                    unclosedCoastlines.add(way);
            } else {
                path = new Path2D.Float();
                path.setWindingRule(Path2D.WIND_EVEN_ODD);
                node = way.get(0);
                path.moveTo(node.getLon(), node.getLat());
                for (int i = 1; i < way.size(); i++) {
                    node = way.get(i);
                    path.lineTo(node.getLon(), node.getLat());
                }
                enumMap.get(OSMType.COASTLINE).add(new EnhancedShape(path));
            }
        }
        if(unclosedCoastlines.size() > 0) closeCoastlines();
        for(OSMWay way : closedCoastlines) {
            path = new Path2D.Float();
            path.setWindingRule(Path2D.WIND_EVEN_ODD);
            node = way.get(0);
            path.moveTo(node.getLon(), node.getLat());
            for (int i = 1; i < way.size(); i++) {
                node = way.get(i);
                path.lineTo(node.getLon(), node.getLat());
            }
            enumMap.get(OSMType.COASTLINE).add(new EnhancedShape(path));
        }
    }

    private void closeCoastlines() {
        int bestMatch = -1;
        int closed = -1;
        for(int i = 0; i < unclosedCoastlines.size(); i++){
            OSMWay coastline = unclosedCoastlines.get(i);
            Float bestDist = Float.MAX_VALUE;
            OSMNode endNode = coastline.get(coastline.size()-1);
            int side = checkSide(endNode); //down = 0, right = 1, up = 2, left = 3
            for (int j = 0; j < unclosedCoastlines.size(); j++){
                OSMNode startNode = unclosedCoastlines.get(j).get(0);
                int s = checkSide(startNode);
                if(side == s){
                    if(checkCorrectPlacement(s, startNode, endNode)) {
                        float dist = (float) (new Point2D.Float(endNode.getLon(), endNode.getLat()).distance(new Point2D.Float(startNode.getLon(), startNode.getLat())));
                        if (dist < bestDist) {
                            bestDist = dist;
                            bestMatch = j;
                        }
                    }
                }
            }
            if(bestMatch == -1) coastline.add(extendWithCorner(side));
            else {
                OSMWay way = unclosedCoastlines.get(bestMatch);
                if(coastline.equals(way)) {
                    closed = i;
                    bestMatch = -1;
                }
                else {
                    coastline.addAll(way);
                    OSMNode start = coastline.get(0);
                    OSMNode end = coastline.get(coastline.size() - 1);
                    if (start.getLat() == end.getLat() && start.getLon() == end.getLon()) closed = i;
                }
                break;
            }
        }
        if(bestMatch > -1)unclosedCoastlines.remove(bestMatch);
        if(closed > -1) {
            closedCoastlines.add(unclosedCoastlines.get(closed));
            unclosedCoastlines.remove(closed);
        }
        if(unclosedCoastlines.size() > 0) closeCoastlines();
    }

    private OSMNode extendWithCorner(int side) {
        switch (side) { //down = 0, right = 1, up = 2, left = 3
            case 0:
                return new OSMNode(maxLon+factor, minLat+factor);
            case 1:
                return new OSMNode(maxLon+factor, maxLat-factor);
            case 2:
                return new OSMNode(minLon-factor, maxLat-factor);
            default:
                return new OSMNode(minLon-factor, minLat+factor);
        }
    }

    private int checkSide(OSMNode node) {
        if(node.getLat() > minLat)
            if(node.getLon() < maxLon) return 0;
            else return 1;

        else if(node.getLon() > maxLon)
                if(node.getLat() > maxLat) return 1;
                else return 2;

        else if(node.getLat() < maxLat)
            if(node.getLon() > minLon) return 2;
            else return 3;

        else return 3;
    }

    private boolean checkCorrectPlacement(int side, OSMNode start, OSMNode end){
        if(side == 0){ //down = 0, right = 1, up = 2, left = 3
            return end.getLon() < start.getLon();
        } else if(side == 1){
            return end.getLat() > start.getLat();
        } else if(side == 2){
            return end.getLon() > start.getLon();
        } else {
            return end.getLat() < start.getLat();
        }
    }

    void setBounds(float[] bounds) {
        minLat = bounds[0];
        minLon = bounds[1];
        maxLat = bounds[2];
        maxLon = bounds[3];
        float latDif = minLat - maxLat;
        float lonDif = maxLon - minLon;
        if(latDif < lonDif) factor = lonDif/2;
        else factor = latDif/2;
    }


    public Map<OSMNode, OSMWay> getCoastlines(){
        return coastlines;
    }

}

