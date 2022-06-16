package model.helpers.routeGraph;

import view.components.RouteDirection;

import java.awt.geom.Point2D;

class DirectionFinder {
    private Vector from;
    private Vector to;

    RouteDirection getDirection(GraphNode previous, GraphNode current, GraphNode next) {
        Point2D previousPoint = previous.getCoordinate();
        Point2D currentPoint  = current.getCoordinate();
        Point2D nextPoint     = next.getCoordinate();

        if(continueForward(previousPoint, currentPoint, nextPoint)) return RouteDirection.FORWARD;

        double value = (currentPoint.getX() - previousPoint.getX()) * (nextPoint.getY() - previousPoint.getY()) - (nextPoint.getX() - previousPoint.getX()) * (currentPoint.getY() - previousPoint.getY());

        if (value > 0)  return RouteDirection.LEFT;
        return RouteDirection.RIGHT;
    }

    private boolean continueForward(Point2D p0, Point2D p1, Point2D p2) {
        from = new Vector(p0, p1);
        to   = new Vector(p1, p2);
        return calculateAngle() < 30 ;
    }

    private double calculateAngle() {
        return Math.toDegrees(Math.acos(calculateScalar() / (from.getLength() * to.getLength())));
    }

    private double calculateScalar() {
        return from.x * to.x + from.y * to.y;
    }

    private class Vector {
        private double x;
        private double y;

        Vector(Point2D start, Point2D end) {
            this.x = start.getX() - end.getX();
            this.y = start.getY() - end.getY();
        }

        private double getLength() {
            return Math.abs(Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)));
        }
    }
}
