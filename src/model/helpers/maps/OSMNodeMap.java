package model.helpers.maps;

import model.osm.OSMNode;

import java.util.HashMap;

public class OSMNodeMap {
    private Node[] table;
    private int MASK = 0x7fffffff;
    private int loadFactor = 1;

    private boolean useTestMethods;
    private HashMap<Integer, Boolean> test;
    private int collisions;

    public OSMNodeMap(int capacity, boolean useTestMethods) {
        this.useTestMethods = useTestMethods;
        if(useTestMethods){
            test = new HashMap<>();
            collisions = 0;
        }
        table = new Node[capacity / loadFactor];
    }

    public void put(long id, float lon, float lat) {
        int position = (Long.hashCode(id) & MASK) % (table.length - 1);
        if(useTestMethods){
            if(test.containsKey(position)) collisions++;
            else test.put(position, true);
        }
        table[position] = new Node(id, lon, lat, table[position]);
    }

    public Node get(long id) {
        int position = (Long.hashCode(id) & MASK) % (table.length - 1);
        for (Node n = table[position]; n != null; n = n.next) {
            if (n.id == id) {
                return n;
            }
        }
        return null;
    }

    public boolean contains(long id) {
        int position = (Long.hashCode(id) & MASK) % (table.length - 1);
        for (Node n = table[position]; n != null; n = n.next) {
            if (n.id == id) {
                return true;
            }
        }
        return false;
    }

    public int getCollisions(){
        return collisions;
    }

    private class Node extends OSMNode {
        private long id;
        private Node next;

        private Node(long id, float lon, float lat, Node next) {
            super(lon, lat);
            this.id = id;
            this.next = next;
        }

    }
}
