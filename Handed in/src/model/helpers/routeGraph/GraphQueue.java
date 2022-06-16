package model.helpers.routeGraph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class GraphQueue implements Serializable{
    private Node root;
    private HashMap<GraphNode, Node> nodeMap;


    public GraphQueue(){
        nodeMap = new HashMap<>();
    }

    private boolean isDistanceBetter(GraphNode oldNode, GraphNode newNode) {
        return oldNode.getModifiedDistance() > newNode.getModifiedDistance();
    }

    public boolean isEmpty() {
        return root == null;
    }

    private Iterator<Node> iterator() {
        return new Iterator<Node>() {
            Node currentNode = root;

            @Override
            public boolean hasNext() {
                return currentNode != null;
            }

            @Override
            public Node next() {
                Node node = currentNode;
                currentNode = currentNode.next;
                return node;
            }
        };
    }

    public boolean add(GraphNode graphNode) {
        Node newNode;
        if(root == null || isDistanceBetter(root.graphNode, graphNode)) {
            newNode = new Node(root, null, graphNode);
            root = newNode;
            nodeMap.put(graphNode, newNode);
        }
        else {
            if(nodeMap.containsKey(graphNode)) remove(graphNode);
            Node previousNode = root;
            for (Iterator<Node> it = iterator(); it.hasNext(); ) {
                Node node = it.next();
                if(isDistanceBetter(node.graphNode, graphNode)){
                    newNode = new Node(node, previousNode, graphNode);
                    previousNode.next = newNode;
                    node.previous = previousNode.next;
                    nodeMap.put(graphNode, newNode);
                    return true;
                }
                previousNode = node;
            }
            newNode = new Node(null, previousNode, graphNode);
            previousNode.next = newNode;
            nodeMap.put(graphNode, newNode);
        }
        return true;
    }

    public boolean remove(GraphNode graphNodeToRemove) {
        if(!nodeMap.containsKey(graphNodeToRemove)) return false;
        Node node = nodeMap.remove(graphNodeToRemove);
        if(node.previous != null) node.previous.next = node.next;
        if(node.next != null) node.next.previous = node.previous;
        return true;
    }

    public GraphNode poll() {
        Node returnNode = nodeMap.remove(root.graphNode);
        while (returnNode == null) {
            root = root.next;
            returnNode = nodeMap.remove(root.graphNode);
        }
        root = returnNode.next;
        return returnNode.graphNode;
    }

    public GraphNode peek() {
        return root.graphNode;
    }

    public List<GraphNode> getAllNodes(){
        List<GraphNode> nodes = new ArrayList<>();
        Node node = root;
        while (node!=null){
            nodes.add(node.graphNode);
            node = node.next;
        }
        return nodes;
    }

    private class Node implements Serializable{
        Node previous;
        Node next;
        GraphNode graphNode;

        private Node(Node next, Node previous, GraphNode graphNode){
            this.next = next;
            this.previous = previous;
            this.graphNode = graphNode;
        }
    }
}
