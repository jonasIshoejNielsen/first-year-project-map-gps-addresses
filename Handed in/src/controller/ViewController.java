package controller;

import model.Model;
import model.helpers.AddressSearch.SearchResult;
import model.helpers.routeGraph.RouteDescription;

import model.osm.OSMAddress;
import view.CanvasView;
import view.InteractionView;
import view.components.ButtonType;
import view.components.InputFieldType;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class ViewController implements Observer{
    private static final int DISTANCE_TO_DRAG = 60;
    private InteractionView interactionView;
    private CanvasView canvasView;
    private Model model;
    private Point2D lastCoords,  currentCoords,  mouseCoords, lastPanningMouseCoords;
    private boolean isAddNodeToggled = false, isFindRouteToggled = false, showNearestRoad = false;
    private boolean draggingRoute, moveStartRoutePoint;

    public ViewController(InteractionView interactionView, CanvasView canvasView, Model model) {
        this.interactionView = interactionView;
        this.canvasView = canvasView;
        this.model = model;
        model.addObserver(this);
    }

    public void addMouseListeners(MouseController mouseController) {
        interactionView.addMouseListener(mouseController);
        canvasView.addMouseListener(mouseController);
        interactionView.addMouseMotionListener(mouseController);
        canvasView.addMouseMotionListener(mouseController);
        interactionView.addMouseWheelListener(mouseController);
        canvasView.addMouseWheelListener(mouseController);
    }

    @Override
    public void update(Observable o, Object arg) {
        canvasView.update();
        interactionView.update();
    }

    /*/////////////////////////////////////////
    CanvasView methods.
    /*////////////////////////////////////////

    public void panCanvasView(float dx, float dy) {
        canvasView.pan(dx,dy);
        updateCoordinateLabel();
        setAntiAliasing(false);
        repaintCanvas();
    }

    public void setAntiAliasing(Boolean useAntiAliasing){
        canvasView.setAntiAliasing(useAntiAliasing);
    }

    public void zoomCanvasView(int zoomDirection, int x, int y) {
        canvasView.zoom(zoomDirection, x, y);
        updateCoordinateLabel();
        repaintCanvas();
    }

    public void zoomToCenterCanvasView(int zoomDirection) {
        canvasView.zoomToCenter(zoomDirection);
        updateCoordinateLabel();
        repaintCanvas();
    }

    public void toggleAntiAliasingCanvasView() {
        canvasView.toggleAntiAliasing();
        repaintCanvas();
    }

    public void  updateCoordinateLabel(){
        Point2D modelCoords = canvasView.mouseToModelCoords(mouseCoords);
        interactionView.updateCoordinateLabel(modelCoords, model.getLonFactor(), canvasView.getFPS(), canvasView.getCurrentZoomlevel());
    }

    public void resetPosition() {
        canvasView.resetPosition();
        repaintCanvas();
    }

    public void repaintCanvas() {
        canvasView.repaint();
        interactionView.updateZoomSliderValue();
    }

    public void toggleGrayscale() {
        canvasView.toggleGrayscale();
    }

    /*/////////////////////////////////////////
    InteractionView methods.
    /*////////////////////////////////////////

    public void updateCoordinate(Point2D point) {
        mouseCoords = point;
    }

    public void toggleCoordinateType() {
        interactionView.toggleCoordinateType();
        updateCoordinateLabel();
    }

    public void toggleFPS() {
        interactionView.toggleFPSCounter();
        updateCoordinateLabel();
    }

    public void toggleZoomLevels() {
        interactionView.toggleZoomLevels();
        updateCoordinateLabel();
    }

    public void toggleKdTreeView() {
        canvasView.setDrawKdTree();
        canvasView.repaint();
    }

    public void toggleAdressView() {
        canvasView.setDrawAdresses();
        canvasView.repaint();
    }

    public List<SearchResult> addressSearch(String s) {
        return model.addressSearch(s);
    }

    public void goTo(OSMAddress address) {
        canvasView.drawSelectedAddress(address);
        canvasView.goToPosition(address.getHousePlacement().getLon(), address.getHousePlacement().getLat());
        canvasView.repaint();
    }

    public void goTo(float lon, float lat) {
        canvasView.goToPosition(lon, lat);
        canvasView.repaint();
    }

    public void leftMouseClick(){
        Point2D modelCoords = canvasView.mouseToModelCoords(mouseCoords);
        if(!isAddNodeToggled) toggleMouseDraggedCursor(true);
        if(isFindRouteToggled) startDraggingRoute(modelCoords);
        else if (isAddNodeToggled){
            return;
        }
        else {
            toggleMouseDraggedCursor(false);
            lastPanningMouseCoords      = mouseCoords;
        }
    }
    private void startDraggingRoute( Point2D modelCoords){
        if (lastCoords == null){
            lastCoords      = modelCoords;
            model.findRoute(lastCoords, null);
            moveStartRoutePoint = false;
            draggingRoute       = false;
            return;
        }
        double distanceToCurrent    = (currentCoords == null) ? Float.MAX_VALUE : currentCoords.distance(modelCoords);
        double distanceToLast       = lastCoords.distance(modelCoords);
        if(currentCoords == null || distanceToCurrent < distanceToLast){
            moveStartRoutePoint = false;
            if(currentCoords == null) model.findRoute(lastCoords, modelCoords);
            currentCoords       = modelCoords;
            model.changeEndPoint(modelCoords);
            draggingRoute = true;
        }
        else {
            moveStartRoutePoint    = true;
            lastCoords             = modelCoords;
            model.changeStartPoint(modelCoords);
            draggingRoute          = true;
        }
    }
    public void leftMouseDragged(Point2D mouseCoords) {
        if(mouseCoords == null) return;
        
        Point2D modelCoords = canvasView.mouseToModelCoords(mouseCoords);
        if(isFindRouteToggled) dragRoute(modelCoords);
        else if(isAddNodeToggled) return;
        else {
            if(lastPanningMouseCoords == null){
                lastPanningMouseCoords = mouseCoords;
                return;
            }
            float dx = (float) mouseCoords.getX() - (float) lastPanningMouseCoords.getX();
            float dy = (float) mouseCoords.getY() - (float) lastPanningMouseCoords.getY();
            panCanvasView(dx, dy);
            lastPanningMouseCoords = mouseCoords;
            toggleMouseDraggedCursor(true);
        }
    }

    private void dragRoute(Point2D modelCoords){
        if (currentCoords == null){
            lastCoords  = modelCoords;
            model.findRoute(lastCoords, null);
            return;
        }
        if(draggingRoute){
            if(!moveStartRoutePoint){
                currentCoords = modelCoords;
                model.changeEndPoint(modelCoords);
            }
            else {
                lastCoords = modelCoords;
                model.changeStartPoint(modelCoords);
            }
        }
    }
    public void leftMouseReleased() {
        if(isFindRouteToggled) {
            if(currentCoords == null) return;
            if(!draggingRoute) return;
            if(moveStartRoutePoint) model.findRoute(lastCoords, currentCoords);
            else model.updateRoute(currentCoords);
            RouteDescription route  = model.getRouteDescription();
            printRoute(route);
        }
        else if(isAddNodeToggled) {
            model.addNode(canvasView.toModelCoords( mouseCoords));
        }
        if(!isAddNodeToggled){
            toggleMouseDraggedCursor(false);
            lastPanningMouseCoords = null;
        }
        repaintCanvas();
    }
    private void printRoute(RouteDescription routeDescription){
        interactionView.setRouteDescription(routeDescription, model.isUsingSpeedCalculation());
    }

    public void toggleDrawNearest() {
        canvasView.toggleDrawNearest();
        canvasView.repaint();
    }

    public void setDefaultSearch() {
        interactionView.setDefaultSearch();
    }

    public void setSelectedAddress(SearchResult result, JTextField inputField, InputFieldType type) {
        interactionView.setSelectedAddress(result, inputField, type);
    }

    public void appendSuggestions(List<SearchResult> results, String inputText, JTextField inputField, InputFieldType type) {
        interactionView.appendSuggestions(results, inputText,inputField, type);
    }

    public DocumentListener inputDocumentListener(JTextField searchInput, InputFieldType type) {
        return interactionView.inputDocumentListener(searchInput, type);
    }

    public void clearSuggestions() {
        interactionView.clearSuggestions();
    }

    public void toggleShowNearestRoad() {
        showNearestRoad = !showNearestRoad;
        canvasView.setShowNearestRoad(showNearestRoad);
        canvasView.repaint();
    }

    public void setClickToFindRoute(boolean state) {
        isFindRouteToggled = state;
        isAddNodeToggled    = false;
    }

    public int getMaxZoomlevel() {
        return canvasView.getMaxZoomLevel();
    }

    public int getCurrentZoomLevel() {
        return canvasView.getCurrentZoomlevel();

    }

    public void clearLocations() {
        model.clearLocations();
        interactionView.setButtonBackground(ButtonType.ADD_ROUTE, false);
    }

    public void setAddNode(boolean state) {
        isAddNodeToggled    = state;
        isFindRouteToggled = false;
    }

    public void toggleMouseDraggedCursor(boolean isDragging) {
        interactionView.toggleMouseDraggedCursor(isDragging);
    }

    public void updateNearestRoad() {
        if (!showNearestRoad) return;
        model.updateNearestRoad(canvasView.mouseToModelCoords(mouseCoords));
    }

    public void clearSelectedAddress() {
        canvasView.clearSelectedAddress();
    }

    public void switchSearchButtonIcon(boolean setDefault) {
        interactionView.switchSearchButtonIcon(setDefault);
    }
    public void setRoute(OSMAddress from, OSMAddress to) {
        lastCoords      = from.getHousePlacement().getCoordinate();
        currentCoords   = to.getHousePlacement().getCoordinate();
    }

    public boolean isUseRealLifeCoordinatesOn() {
        return interactionView.isUseRealLifeCoordinatesOn();
    }

    public boolean isFPSCounterOn() {
        return interactionView.isFPSCounterOn();
    }

    public boolean isZoomLevelOn() {
        return interactionView.isZoomLevelOn();
    }

    public boolean isKdTreeOn() {
        return canvasView.isKdTreeOn();
    }

    public boolean isHouseNumbersOn() {
        return canvasView.isHouseNumbersOn();
    }

    public boolean isNearestFigurOn() {
        return canvasView.isNearestFigurOn();
    }

    public boolean isNearestRoadOn() {
        return showNearestRoad;
    }

    public boolean isGrayscaleOn() {
        return canvasView.isGrayscaleOn();
    }

    public Point2D toModelCoords(Point2D screenPoint){
        return canvasView.toModelCoords(screenPoint);

    }

    public void resetZoomLevel() {
        canvasView.resetZoomLevel();
    }
}
