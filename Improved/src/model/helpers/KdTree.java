package model.helpers;

import model.helpers.drawing.EnhancedShape;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public class KdTree implements Serializable {
    private Node root;
    private static final int maxLeafSize = 1000;

    public KdTree(List<EnhancedShape> shapes) {
        sortNodes(shapes, root = new Node(true));
    }

    private void sort(List<EnhancedShape> shapes, boolean vertical) {
        EnhancedShape.setVertical(vertical);
        Collections.sort(shapes);
    }

    public void sortNodes(List<EnhancedShape> shapes, Node node) {
        if(shapes.size() <= maxLeafSize) {
            node.leaf = new Leaf(shapes);
            return;
        }

        //Sort by axis
        sort(shapes, node.vertical);

        //New split
        int mid = shapes.size() / 2;
        node.split = shapes.get(mid);
        node.left  = new Node(!node.vertical);
        node.right = new Node(!node.vertical);

        //Split list into into sublists
        List<EnhancedShape> leftList  = new ArrayList<>(mid);
        List<EnhancedShape> rightList = new ArrayList<>(mid);

        //Copy into subarrays
        float splitValue   = (node.vertical) ? (float) node.split.getMedian().getX() : (float) node.split.getMedian().getY();
        node.boundsMax = splitValue;
        node.boundsMin = splitValue;
        for (int i = 0; i < mid; i++) {
            setMaxBounds(node, shapes.get(i));
            leftList.add(shapes.get(i));
        }
        for (int i = mid; i < shapes.size(); i++) {
            setMinBounds(node, shapes.get(i));
            rightList.add(shapes.get(i));
        }

        //Recursive sorting
        sortNodes(leftList, node.left);
        sortNodes(rightList, node.right);
    }
    private void setMaxBounds(Node node, EnhancedShape shape){
        float valueMax = (node.vertical) ? (float) shape.getBounds().getMaxX() : (float) shape.getBounds().getMaxY();
        if (node.boundsMax < valueMax) {
            node.boundsMax = valueMax;
        }
    }

    private void setMinBounds(Node node, EnhancedShape shape){
        float valueMin = (node.vertical) ? (float) shape.getBounds().getMinX(): (float) shape.getBounds().getMinY();
        if(node.boundsMin > valueMin){
            node.boundsMin = valueMin;
        }
    }

    private class Node implements Serializable {
        float boundsMin, boundsMax;
        EnhancedShape split;
        Node left, right;
        Leaf leaf;
        boolean vertical;

        public Node(boolean vertical){
            this.vertical = vertical;
        }
    }

    private class Leaf implements Serializable {
        List<EnhancedShape> shapes;
        public Leaf(List<EnhancedShape> shapes) {
            this.shapes = shapes;
        }
    }

    public List<EnhancedShape> rangeSearch( Rectangle2D rect){
        List<EnhancedShape> shapes = new ArrayList<>();
        getShapes(shapes, root, rect);
        return shapes;
    }

    public List<EnhancedShape> getAllShapes(){
        return getLeafs(root);
    }

    private List<EnhancedShape> getLeafs(Node node){
        List<EnhancedShape> shapes = new ArrayList<>();
        if(node != null){
            shapes = (getLeafs(node.left));
            shapes.addAll(getLeafs(node.right));
            if(node.leaf != null) shapes.addAll(node.leaf.shapes);
        }
        return shapes;
    }

    public void getShapes(List<EnhancedShape> shapes, Node node, Rectangle2D rect) {
        if (shapes == null) return;
        if (node.leaf != null) {
            for (EnhancedShape shape : node.leaf.shapes) {
                if(shape.getBounds().intersects(rect)) shapes.add(shape);
            }
            return;
        }


        double minValue     = (node.vertical)? rect.getMinX() : rect.getMinY();
        double maxValue     = (node.vertical)? rect.getMaxX() : rect.getMaxY();
        double centerValue  = (node.vertical)? node.split.getBounds().getCenterX() : node.split.getBounds().getCenterY();
        boolean left = false, right = false;

        if (minValue < centerValue) {
            getShapes(shapes, node.left, rect);
            left = true;
            if(maxValue>node.boundsMin && maxValue < centerValue) {
                getShapes(shapes, node.right, rect);
                right = true;
            }
        }
        if (maxValue > centerValue) {
            if(!right) getShapes(shapes, node.right, rect);
            if (minValue < node.boundsMax && minValue > centerValue && ! left)
                getShapes(shapes, node.left, rect);
        }

    }


    public EnhancedShape getNearestNeighbor(Point2D point) {
        List<EnhancedShape> closest = new ArrayList<>();
        findClosestShape(point, root, closest);
        return closest.get(0);
    }

    private void findClosestShape(Point2D point2D, Node node, List<EnhancedShape> best) {
        if(node.split != null){
            if(best.size() == 0) best.add(node.split);
            else if(node.split.getDistanceToCenter(point2D) < best.get(0).getDistanceToCenter(point2D));
        }
        if(node.leaf != null){
            float bestDist = Float.MAX_VALUE;
            if(best.size() != 0){
                bestDist = best.get(0).getDistanceToCenter(point2D);
            }
            for(EnhancedShape enhancedShape : node.leaf.shapes){
                float newDist = enhancedShape.getDistanceToCenter(point2D);
                if(newDist < bestDist) {
                    best.clear();
                    best.add(enhancedShape);
                    bestDist = newDist;
                }
            }
            return ;
        }

        double pointVal     = (node.vertical)? point2D.getX() : point2D.getY();
        double medianVal    = (node.vertical)? node.split.getMedian().getX() : node.split.getMedian().getY();

        boolean leftChecked = false, rightChecked = false;
        if(pointVal < medianVal){
            findClosestShape(point2D, node.left, best);
            leftChecked = true;
            if(pointVal > medianVal + node.boundsMax){
                findClosestShape(point2D, node.right, best);
                rightChecked = true;
            }
        }
        if(!rightChecked){
            findClosestShape(point2D, node.right, best);
            if(!leftChecked && pointVal < medianVal - node.boundsMin){
                findClosestShape(point2D, node.left, best);
            }
        }
    }
}
