package model.osm;

public class OSMRelationNode {
    private long id;
    private OSMRelationType type;
    private IOSMObject object;

    public OSMRelationNode(long id, OSMRelationType type){
        this.id = id;
        this.type = type;
    }

    public OSMRelationNode(IOSMObject object, OSMRelationType type){
        this.object = object;
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public IOSMObject getObject() {
        return object;
    }

    public OSMRelationType getType() {
        return type;
    }
}
