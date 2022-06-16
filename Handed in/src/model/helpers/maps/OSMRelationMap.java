package model.helpers.maps;

import model.osm.OSMRelation;

import java.util.HashMap;
import java.util.Iterator;

public class OSMRelationMap implements Iterable<OSMRelation> {
    private Node[] table;
    private int MASK = 0x7fffffff;
    private int loadFactor = 1;

    private boolean useTestMethods;
    private HashMap<Integer, Boolean> test;
    private int collisions;

    public OSMRelationMap(int capacity, boolean useTestMethods) {
        this.useTestMethods = useTestMethods;
        if(useTestMethods){
            test = new HashMap<>();
            collisions = 0;
        }
        table = new Node[capacity / loadFactor];
    }

    public void put(long id, OSMRelation relation) {
        int position = (Long.hashCode(id) & MASK) % (table.length - 1);
        if(useTestMethods){
            if(test.containsKey(position)) collisions++;
            else test.put(position, true);
        }
        table[position] = new Node(id, relation, table[position]);
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

    @Override
    public Iterator<OSMRelation> iterator() {
        return new Iterator<OSMRelation>() {
            private int tablePosition = 0;
            private int chainPosition = 0;
            @Override
            public boolean hasNext() {
                return tablePosition < table.length;
            }

            @Override
            public OSMRelation next() {
                if(table[tablePosition] != null) {
                    int i = 0;
                    for (Node n = table[tablePosition]; n != null; n = n.next) {
                        if (i == chainPosition) {
                            chainPosition++;
                            return n;
                        } else i++;
                    }
                    chainPosition = 0;
                    tablePosition++;
                } else{
                    tablePosition++;
                }
                return null;
            }
        };
    }

    public int getCollisions() {
        return collisions;
    }

    private class Node extends OSMRelation {
        private long id;
        private Node next;

        private Node(long id, OSMRelation relation, Node next){
            setNodeList(relation.getNodeList());
            setType(relation.getType());
            this.id = id;
            this.next = next;
        }
    }
}
