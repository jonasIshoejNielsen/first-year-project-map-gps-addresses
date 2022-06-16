package model.helpers.routeGraph;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.geom.Point2D;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GraphMapTest {
    private Point2D[] points;
    private GraphMap graphMap;

        /* Graph:
            0 -> 1 -> 2 -> 3
                 ^    |
                 |    |
                 6    4 -> 7 -> 8
                 ^    |
                 |    |
                 5 <--|
        */

    @BeforeEach
    public void createGraphMap(){
        points  = new Point2D[] {
                new Point2D.Float(0, 0), //0
                new Point2D.Float(0, 1), //1
                new Point2D.Float(0, 2), //2
                new Point2D.Float(0, 3), //3
                new Point2D.Float(1, 2), //4
                new Point2D.Float(2, 1), //5
                new Point2D.Float(1, 1), //6
                new Point2D.Float(3, 3), //7
                new Point2D.Float(3, 4)  //8
        };

        graphMap = new GraphMap(15,1, false);

        GraphNode previous = null;
        Point2D[][] ways = {
                { points[0], points[1], points[2], points[3] },
                { points[2], points[4], points[5], points[6], points[1] },
                { points[4], points[7], points[8] }
        };

        for(Point2D[] way: ways) {
            for (Point2D point : way) {
                graphMap.put(point);
                if (previous == null) {
                    previous = graphMap.getGraphNode(point);
                    continue;
                }
                GraphNode current = graphMap.getGraphNode(point);
                graphMap.addEdge(current, previous, 50, false,false, point + "", 0);
                previous = current;
            }
            previous = null;
        }
    }

    @Test
    @DisplayName("given specific Nodes and Edges expected Graph is generated")
    public void givenSpecificNodesAndEdgesExpectedGraphIsGenerated(){
        Point2D[][] result = {
                {points[1]},                          //0
                {points[0], points[2], points[6]},    //1
                {points[1], points[3], points[4]},    //2
                {points[2]},                          //3
                {points[2], points[5], points[7]},    //4
                {points[4], points[6]},               //5
                {points[5], points[1]},               //6
                {points[4], points[8]},               //7
                {points[7]},                          //8
        };

        for (int i = 0; i < points.length; i++) {
            List<GraphEdge> edgeList = graphMap.getGraphNode(points[i]).getEdges();
            for(int j = 0; j < result[i].length; j++){
                assertEquals(result[i][j], edgeList.get(j).getTo().getCoordinate());
            }
        }
    }
}