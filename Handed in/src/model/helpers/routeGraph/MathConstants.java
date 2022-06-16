package model.helpers.routeGraph;

public class MathConstants {
    private static float lonFactor;
    private static final int earthsRadius = 6378137;

    // Haversine’ Formula (assumes the earth to be completely round, even though it's a spire)
    public static float getDistanceInMeters(GraphNode start, GraphNode end) {
        double latDistance = Math.toRadians(getNodeCoordinateY(end) - getNodeCoordinateY(start));
        double lonDistance = Math.toRadians(getNodeCoordinateX(end) - getNodeCoordinateX(start));

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(getNodeCoordinateY(start))) * Math.cos(Math.toRadians(getNodeCoordinateY(end)))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = earthsRadius * c;

        distance = Math.pow(distance, 2);

        return new Double(Math.sqrt(distance)).floatValue();

    }

    // Equirectangular approximation (Pythagoras’ theorem) Use if performance is an issue and accuracy less important, for small distances.
    public static float getDistanceInMetersSimple(GraphNode node1, GraphNode node2){
        double lat1             = toRadians(getNodeCoordinateY(node1));
        double lat2             = toRadians(getNodeCoordinateY(node2));

        double x = toRadians(differenceLon(node1,node2) * Math.cos((lat1 + lat2) / 2));
        double y = toRadians(differenceLat(node1, node2));
        float distance = new Double(Math.sqrt( x*x + y*y ) * earthsRadius).floatValue();

        return distance;
    }

    private static double toRadians(double coordinateXorY) {
        return coordinateXorY * Math.PI / 180;
    }

    private static double getNodeCoordinateX(GraphNode node){
        double lon = node.getCoordinate().getX() / lonFactor; // /longFactor converts from canvas-coordinates to realworld-coordinates.
        return lon;
    }

    private static double getNodeCoordinateY(GraphNode node){
        double lat = node.getCoordinate().getY() * (-1); // *(-1) converts from canvas-coordinates to realworld-coordinates.
        return lat;
    }

    private static double differenceLon(GraphNode node1, GraphNode node2){
        double lon1 = getNodeCoordinateX(node1);
        double lon2 = getNodeCoordinateX(node2);
        return Math.abs(lon2 - lon1);
    }

    private static double differenceLat(GraphNode node1, GraphNode node2){
        double lat1 = getNodeCoordinateY(node1);
        double lat2 = getNodeCoordinateY(node2);
        return Math.abs(lat2 - lat1);
    }

    public static void setLonFactor(float lonFactor) {
        MathConstants.lonFactor = lonFactor;
    }

    public static float getLonFactor() {
        return lonFactor;
    }
}
