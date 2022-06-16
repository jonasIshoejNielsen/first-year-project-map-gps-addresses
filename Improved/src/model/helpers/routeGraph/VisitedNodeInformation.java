package model.helpers.routeGraph;

public class VisitedNodeInformation implements Comparable<VisitedNodeInformation> {
    private float distance, traveledDistance;
    private GraphNode bestNodeToThis;
    private GraphNode node;
    public VisitedNodeInformation(float distance, float traveledDistance, GraphNode bestNodeToThis, GraphNode node) {
        this.distance = distance;
        this.traveledDistance = traveledDistance;
        this.bestNodeToThis = bestNodeToThis;
        this.node = node;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public void setTraveledDistance(float traveledDistance) {
        this.traveledDistance = traveledDistance;
    }

    public void setBestNodeToThis(GraphNode bestNodeToThis) {
        this.bestNodeToThis = bestNodeToThis;
    }

    public void setNode(GraphNode node) {
        this.node = node;
    }

    public float getDistance() {
        return distance;
    }

    public float getTraveledDistance() {
        return traveledDistance;
    }

    public GraphNode getBestNodeToThis() {
        return bestNodeToThis;
    }

    public GraphNode getNode() {
        return node;
    }

    public Float getModifiedDistance(){
        return traveledDistance + distance;
    }

    @Override
    public int compareTo(VisitedNodeInformation o) {
        return getModifiedDistance().compareTo(o.getModifiedDistance());
    }
}
