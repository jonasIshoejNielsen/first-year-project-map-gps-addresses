package model.osm;

import java.util.ArrayList;
import java.util.List;

public class OSMRelation implements IOSMObject{
    private OSMType type;
    private List<OSMRelationNode> nodeList = new ArrayList<>();

    public void addNode(IOSMObject object, String type){
        nodeList.add(new OSMRelationNode(object, findType(type)));
    }

    public void addNode(long id, String type){
        nodeList.add(new OSMRelationNode(id, findType(type)));
    }

    public OSMType getType() {
        return type;
    }

    public void setType(OSMType type){
        this.type = type;
    }

    public List<OSMRelationNode> getNodeList(){
        return nodeList;
    }

    protected void setNodeList(List<OSMRelationNode> nodeList){
        this.nodeList = nodeList;
    }

    private OSMRelationType findType(String type){
        switch (type){
            case "inner":
                return OSMRelationType.INNER;
            case "outer":
                return OSMRelationType.OUTER;
            default:
                return OSMRelationType.UNKNOWN;
        }
    }
}
