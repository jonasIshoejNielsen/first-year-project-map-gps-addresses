package view;

import model.CanvasModel;
import model.Model;
import model.UserInputPoint;
import model.UserInputList;
import model.helpers.AddedNote;
import model.helpers.drawing.EnhancedAddressShape;
import model.helpers.drawing.EnhancedRoadShape;
import model.helpers.drawing.EnhancedShape;
import model.helpers.KdTree;
import model.osm.OSMAddress;
import model.osm.OSMType;
import model.osm.OSMTypeParent;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.List;

import static java.lang.Math.pow;


public class CanvasView extends JComponent{
    private CanvasModel canvasModel;
    private Model model;

    private AffineTransform transform = new AffineTransform();

    private Rectangle2D viewRect;
    private Graphics2D g;

    private int maxZoomLevel;
    private int zoomLevel       = 0;
    private final int OFFSET    = 10;
    private float fps           = 0f;
    private float xMin, yMin, xMax, yMax, minLat, minLon, maxLat, maxLon, nearestDist;
    private final float ZOOMFACTOR = 1.5f, BASIC_STROKE_SIZE_5 = 0.00005f;

    private List<Point2D> route, visited;
    private Point2D min, max, mouseModelCoords;
    private KdTree kdTreeHouseNumbers;
    private List<OSMAddress> addresses;
    private OSMAddress selectedAddress;
    private List<AddedNote> addedNotes;
    private EnhancedRoadShape nearestRoad;
    private EnhancedShape nearestShape;
    private OSMType nearestShapeType;
    private UserInputList userInputList;

    private boolean drawKdTree = false, drawNearest = false, drawAddresses = false;
    private boolean useAntiAliasing = true, showNearestRoad = false, grayScaleOn = false;

    public CanvasView(Model model){
        this.model  = model;
        canvasModel = model.getCanvasModel();
        addresses   = model.getAddresses();
        route       = model.getRoute();
        visited     = model.getVisited();
    }

    @Override
    public void paint(Graphics _g){
        long fpsTimeStart = System.nanoTime();
        g = (Graphics2D) _g;
        g.transform(transform);

        nearestShape    = null;
        nearestDist     = (float) Double.MAX_VALUE;

        useAntiAliasing();
        viewRect = new Rectangle2D.Float(0, 0, getWidth(), getHeight());

        int OFFSET = 5;
        if(drawKdTree) OFFSET = -100;

        Point2D min = toModelCoords(new Point(-OFFSET, -OFFSET));
        Point2D max = toModelCoords(new Point(getWidth() + OFFSET, getHeight() + OFFSET * 2));

        xMin = (float) min.getX();
        yMin = (float) min.getY();
        xMax = (float) max.getX();
        yMax = (float) max.getY();

        try { viewRect = transform.createInverse().createTransformedShape(viewRect).getBounds2D(); }
        catch (NoninvertibleTransformException e) { viewRect = new Rectangle2D.Float(); }

        if(canvasModel == null) return;
        setBackground(viewRect, new Color(88, 189, 255));

        if(grayScaleOn) setBackground(viewRect, calculateGrayscale(new Color(88, 189, 255)));
        drawMap();

        //Draw nearest shape to mouse cursor.

        if(drawKdTree) outLineKDTree(g);

        calculateFPS(fpsTimeStart);

        //Draw nearest shape to mouse cursor.
        if(drawNearest && nearestShape != null) drawNearestShape();
        paintRoute();
        if(drawAddresses) if (zoomLevel <= 1) printHouseNumbers();

        if(userInputList != null) {
            for (UserInputPoint point : userInputList) {
                drawPoint(point,Color.BLACK);
                drawString(point.getUserInput(), point);
            }
        }

        if(showNearestRoad && nearestRoad != null) {
            g.setColor(new Color(8, 82, 78, 150));
            g.draw(nearestRoad.getShape());
            drawString(nearestRoad.getName(), nearestRoad.getMedian());
        }
        if(selectedAddress != null) {
            Point2D p = new Point2D.Float(selectedAddress.getHousePlacement().getLon(), selectedAddress.getHousePlacement().getLat());
            drawPoint(p, new Color(8, 82, 78, 200));
            drawString(selectedAddress.getStreet(), p);
        }
        drawAddedNotes();
    }
    private void drawAddedNotes(){
        float ELLIPSE_DIAMETER = 0.00003f;
        if(addedNotes == null) return;
        Ellipse2D ellipse2D = new Ellipse2D.Float();
        for(AddedNote note : addedNotes){
            g.setColor(Color.cyan);
            drawEllipse(ellipse2D,note.getCoords(), ELLIPSE_DIAMETER,true, Color.cyan);
            drawString( note.getName(), note.getCoords());
        }
    }

    private void paintRoute() {
        if(route != null && route.size()<1) return;

        float STROKE_WIDTH      = 0.00001f;
        float ELLIPSE_DIAMETER  = 0.000003f;
        Path2D path2D = new Path2D.Float();

        g.setStroke(new BasicStroke(STROKE_WIDTH * dynamicScaleFactor(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        Ellipse2D nodeEllipse = new Ellipse2D.Double();
        g.setColor(Color.magenta);

        for(Point2D point2D : visited) drawEllipse(nodeEllipse, point2D, ELLIPSE_DIAMETER * dynamicScaleFactor(), true, Color.magenta);

        Point2D first = null, last  = null;
        for(Point2D point2D : route ) {
            if (point2D == null) continue;
            if (last == null) {
                last = point2D;
                path2D.moveTo(last.getX(), last.getY());
            }
            path2D.lineTo(point2D.getX(), point2D.getY());
        }
        first = route.get(route.size()-1);

        g.setColor(new Color(8, 82, 78, 200));
        g.draw(path2D);
        if(first == null) return;
        drawPoint(first, new Color(255,255,255, 150));
        drawPoint(last, new Color(214, 62, 48, 150));
    }
    private void drawPoint(Point2D point2D, Color color){
        g.setColor(color);
        Ellipse2D nodeEllipse = new Ellipse2D.Double();
        drawEllipse(nodeEllipse, point2D, 0.00002f * dynamicScaleFactor(), true, color);
    }

    private float dynamicScaleFactor() {
        return new Double(Math.pow(ZOOMFACTOR, Math.max(zoomLevel+1,2))).floatValue();
    }

    private void drawEllipse(Ellipse2D nodeEllipse, Point2D point2D, float diameter, boolean fill, Color color){
        nodeEllipse.setFrameFromCenter(point2D.getX(), point2D.getY(), point2D.getX() + diameter, point2D.getY() + diameter);
        if(fill) {
            nodeEllipse.setFrameFromCenter(point2D.getX(), point2D.getY(), point2D.getX() + (diameter*1.4f), point2D.getY() + (diameter*1.4f));
            g.setColor(new Color(0, 0, 0, 150));
            g.fill(nodeEllipse);

            nodeEllipse.setFrameFromCenter(point2D.getX(), point2D.getY(), point2D.getX() + diameter, point2D.getY() + diameter);
            g.setColor(color);
            g.fill(nodeEllipse);

            nodeEllipse.setFrameFromCenter(point2D.getX(), point2D.getY(), point2D.getX() + (diameter*0.4f), point2D.getY() + (diameter*0.4f));
            g.setColor(new Color(0, 0, 0, 150));
            g.fill(nodeEllipse);
        }
        else g.draw(nodeEllipse);
    }

    private void printHouseNumbers() {
        if(addresses == null) return;
        for(EnhancedShape enhancedShape : kdTreeHouseNumbers.rangeSearch(xMin, yMin, xMax, yMax)) {
            EnhancedAddressShape enhancedAddressShape = (EnhancedAddressShape) enhancedShape;
            drawString(enhancedAddressShape.getStringValue(), enhancedAddressShape.getPoint2D());
        }
    }

    private  void drawString(String string, Point2D point2D){
        g.setPaint(new Color(214, 62, 48));
        Font font = new Font("Arial", Font.BOLD, 12);
        g.setFont(font.deriveFont(AffineTransform.getScaleInstance(1.2 / transform.getScaleX(), 1.2 / transform.getScaleX())));
        if(!viewRect.intersects(point2D.getX(), point2D.getY(), 0.000001,0.000001)) return;
        if(string != null) g.drawString(string, (float) point2D.getX(), (float) point2D.getY());
    }

    private void useAntiAliasing(){
        if (useAntiAliasing) g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }

    private void outLineKDTree(Graphics2D g) {
        Path2D path = new Path2D.Float();
        path.moveTo(xMin,yMin);
        path.lineTo(xMin, yMax);
        path.lineTo(xMax, yMax);
        path.lineTo(xMax, yMin);
        path.lineTo(xMin, yMin);
        g.setPaint(new Color(255, 24, 34, 100));
        g.fill(path);
    }

    private void drawMap(){
        fillArea(OSMType.COASTLINE);
        for(OSMType type : canvasModel.getKdTreeTypes()) {
            if(!type.isEnabled(zoomLevel))  continue;
            if(type.isLine()) drawLine(type);
            if(type.isArea()) fillArea(type);
        }
    }

    private void fillArea(OSMType type){
        g.setPaint(type.getColor());
        if(grayScaleOn) g.setPaint(calculateGrayscale(type.getColor()));
        List<EnhancedShape> shapes = (type.equals(OSMType.COASTLINE)) ? canvasModel.getCoastline(viewRect) : canvasModel.getTree(type).rangeSearch(xMin, yMin, xMax, yMax);
        for (EnhancedShape shape: shapes) {
            g.fill(shape.getShape());
            if(type == OSMType.COASTLINE) continue;
            validateNearestNeighbor(shape, type);
        }
    }

    public Color calculateGrayscale(Color color) {
        int factor = (color.getRed() + color.getBlue() + color.getGreen()) / 3;
        return new Color(factor, factor, factor, color.getAlpha());
    }

    private void validateNearestNeighbor(EnhancedShape shape, OSMType type) {
        if(mouseModelCoords == null) return;
        if(shape == null) return;
        if(nearestShape == null) nearestShape = shape;
        float distanceToCenter = shape.getDistanceToCenter(mouseModelCoords);

        if(distanceToCenter < nearestDist) {
            nearestShape        = shape;
            nearestDist         = distanceToCenter;
            nearestShapeType    = type;
        }
    }

    private void drawNearestShape() {
        g.setPaint(Color.BLUE);
        g.fill(nearestShape.getShape());
    }

    private void drawLine(OSMType type){
        if(type.getStroke() == null) return;
        if(zoomLevel>5 && type.getParent().equals(OSMTypeParent.HIGHWAY)) g.setStroke(type.getModifiedStroke(dynamicScaleFactor()));
        else    g.setStroke(type.getStroke());

        if(type.isArea()) g.setPaint(type.getColor().darker());
        else {
            g.setPaint(type.getColor());
            if(grayScaleOn) {
                g.setPaint(calculateGrayscale(type.getColor()));
            }
        }
        for (EnhancedShape line: canvasModel.getTree(type).rangeSearch(xMin, yMin, xMax, yMax)) {
            g.draw(line.getShape());
        }
    }
    private void setBackground(Rectangle2D viewRect, Color color){
        if(canvasModel.noCoastlines()){color=OSMType.COASTLINE.getColor();}
        g.setColor(color);
        g.fill(viewRect);
    }

    public void setAntiAliasing(Boolean useAntiAliasing) {
        this.useAntiAliasing = useAntiAliasing;
    }

    public void toggleAntiAliasing() { useAntiAliasing =! useAntiAliasing; }

    public void zoomToCenter(int zoomDirection){
        zoom(zoomDirection, -getWidth() /2, -getHeight() / 2);
    }

    public void resetZoomLevel(){
        while (zoomLevel < maxZoomLevel) zoomToCenter(-1);
    }

    public void zoom(int zoomDirection, float x, float y) {
        if (validateZoom(zoomDirection)) {
            float zoomFactor = (float) pow(ZOOMFACTOR, zoomDirection);
            pan(x, y);
            transform.preConcatenate(AffineTransform.getScaleInstance(zoomFactor, zoomFactor));
            pan(-x, -y);
        }
    }

    public boolean validateZoom(int zoomDirection) {
        if(zoomDirection == -1) {
            if(zoomLevel < maxZoomLevel) {
                zoomLevel++;
                return true;
            }
        }
        else if(zoomDirection == 1) {
            if(zoomLevel > 0) {
                zoomLevel--;
                return true;
            }
        }
        return false;
    }

    private void calculateMaxZoomLevel() {
        double FUNCTION_OF_X     = 350000;
        double INITIAL_QUANTITY  = getWidth() / (maxLon - minLon);
        maxZoomLevel = (int) Math.abs(Math.round((Math.log(FUNCTION_OF_X) - Math.log(INITIAL_QUANTITY)) / Math.log(ZOOMFACTOR))) + 1;
    }

    public void resetPosition() {
        transform = new AffineTransform();
        pan(-minLon, -maxLat);
        float resetFactor = getWidth() / (maxLon - minLon);
        transform.preConcatenate(AffineTransform.getScaleInstance(resetFactor, resetFactor));
        zoomLevel = maxZoomLevel - 1;
    }


    public void goToPosition(float x, float y) {
        float scale     = (float) transform.getScaleX();
        transform       = new AffineTransform();
        pan(-x, -y);
        float factor    = (float) (scale / transform.getScaleX());
        transform.preConcatenate(AffineTransform.getScaleInstance(factor,factor));
        pan(getWidth() / 2, getHeight() / 2);
    }

    public void pan(float dx, float dy) {
        transform.preConcatenate(AffineTransform.getTranslateInstance(dx, dy));
    }

    public void update(){
        CanvasModel oldCanvasModel  = canvasModel;
        canvasModel                 = model.getCanvasModel();
        addresses                   = model.getAddresses();
        route                       = model.getRoute();
        visited                     = model.getVisited();
        userInputList               = model.getUserInputList();
        addedNotes                  = model.getAddedNotes();
        kdTreeHouseNumbers          = model.getKdTreeHouseNumbers();

        if(canvasModel == null) return;

        minLat      = canvasModel.getBounds()[0];
        minLon      = canvasModel.getBounds()[1];
        maxLat      = canvasModel.getBounds()[2];
        maxLon      = canvasModel.getBounds()[3];
        nearestRoad = model.getNearestRoad();

        calculateMaxZoomLevel();
        if(oldCanvasModel != canvasModel) resetPosition();
        repaint();
    }


    public Point2D mouseToModelCoords(Point2D mouseCoords) {
        mouseModelCoords = toModelCoords(mouseCoords);
        return mouseModelCoords;
    }

    public void drawSelectedAddress(OSMAddress address){
        if(address == null) return;
        if(address.equals(this.selectedAddress)) return;
        this.selectedAddress = address;
    }

    public void clearSelectedAddress() {
        this.selectedAddress = null;
        update();
    }

    public Point2D toModelCoords(Point2D p){
        try { return transform.inverseTransform(p,null); }
        catch (NoninvertibleTransformException e) { return new Point2D.Float(0,0);}
    }

    private void calculateFPS(long fpsTimeStart) {
        long fpsTimeEnd = System.nanoTime();
        fps = (fps + 1e9f/ (fpsTimeEnd - fpsTimeStart)) / 2f;
    }

    public float getFPS(){
        return fps;
    }

    public int getCurrentZoomlevel() {
        return zoomLevel;
    }

    public void setDrawKdTree() {
        drawKdTree = !drawKdTree;
    }

    public void setDrawAdresses() {
        drawAddresses = !drawAddresses;
    }

    public void toggleDrawNearest(){
        drawNearest = !drawNearest;
    }

    public void toggleGrayscale() {
        grayScaleOn = !grayScaleOn;
        repaint();
    }

    public void setShowNearestRoad(boolean showNearestRoad) {
        this.showNearestRoad = showNearestRoad;
    }

    public int getMaxZoomLevel() {
        return maxZoomLevel;
    }

    public boolean isKdTreeOn() {
        return drawKdTree;
    }

    public boolean isHouseNumbersOn() {
        return drawAddresses;
    }

    public boolean isNearestFigurOn() {
        return drawNearest;
    }

    public boolean isGrayscaleOn() {
        return grayScaleOn;
    }
}
