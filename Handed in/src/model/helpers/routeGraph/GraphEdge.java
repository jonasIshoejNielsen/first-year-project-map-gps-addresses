package model.helpers.routeGraph;

import java.io.Serializable;

public class GraphEdge implements Serializable {
    private GraphNode to;
    private GraphNode from;

    private float speed;

    private boolean isWalkingAllowed, isBicycleAllowed;
    private String roadName;

    private int oneWay;

    GraphEdge(GraphNode from, GraphNode to, float speed, boolean isWalkingAllowed, boolean isBicycleAllowed, String roadName, int oneWay) {
        this.from = from;
        this.to = to;
        this.speed = speed;
        this.isWalkingAllowed = isWalkingAllowed;
        this.isBicycleAllowed = isBicycleAllowed;
        this.roadName = roadName;
        this.oneWay = oneWay;
    }

    public GraphNode getFrom(){
        return from;
    }

    public GraphNode getTo(){
        return to;
    }

    public String getRoadName() {
        return roadName;
    }

    public float getSpeed() {
        return speed;
    }

    public boolean getIsWalking() {
        return isWalkingAllowed;
    }
    public boolean getIsBickingAllowed(){return isBicycleAllowed;}

    public int getOneWay() {
        return oneWay;
    }
}
