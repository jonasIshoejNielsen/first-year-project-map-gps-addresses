package controller;

import model.helpers.AddressSearch.SearchResult;
import model.helpers.routeGraph.RouteDescription;
import model.helpers.routeGraph.RouteType;
import model.osm.OSMAddress;
import model.osm.OSMType;
import view.components.InputFieldType;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import java.awt.geom.Point2D;
import java.util.List;

public class InteractionController {
    private ViewController viewController;
    private ModelController modelController;

    public void setViewController(ViewController viewController, ModelController modelController) {
        this.viewController = viewController;
        this.modelController = modelController;
    }

    public void zoom(int zoomDirection) {
        viewController.zoomToCenterCanvasView(zoomDirection);
    }


    public void resetPosition() {
        viewController.resetPosition();
    }

    public int getMaxZoomlevel() {
        return viewController.getMaxZoomlevel();
    }

    public int getCurrentZoomlevel() {
        return viewController.getCurrentZoomLevel();
    }

    public void toggleFilter(OSMType type) {
        type.toggle();
        viewController.repaintCanvas();
    }

    public void goTo(OSMAddress address, String postcode) {
        viewController.goTo(address, postcode);
    }

    public RouteDescription findRoute(OSMAddress from, String postcodeFrom, OSMAddress to, String postcodeTo, RouteType routeType, boolean calculateSpeed, float routePannelRightX){
        int adresses = 1;
        float lon = from.getHousePlacement(postcodeFrom).getLon();
        float lat = from.getHousePlacement(postcodeFrom).getLat();
        if(to != null){
            adresses++;
            lon += to.getHousePlacement(postcodeTo).getLon();
            lat += to.getHousePlacement(postcodeTo).getLat();
        }
        float routePanelScreenWidht = routePannelRightX/2;
        Point2D screenLeftSide      = viewController.toModelCoords(new Point2D.Float(0,0));
        Point2D routePanelRightSide = viewController.toModelCoords(new Point2D.Float(routePanelScreenWidht,0));
        float routeModelWidth       = new Double(Math.abs(Math.abs(screenLeftSide.getX()) - Math.abs(routePanelRightSide.getX()))).floatValue();
        viewController.goTo((lon/adresses)-routeModelWidth, lat/adresses);
        viewController.resetZoomLevel();

        viewController.setRoute(from, postcodeFrom, to, postcodeTo);
        return modelController.findRoute(from, postcodeFrom, to, postcodeTo, routeType, calculateSpeed);
    }

    public void setDefaultSearch() {
        viewController.setDefaultSearch();
    }

    public void setSelectedAddress(SearchResult result, JTextField inputField, InputFieldType type) {
        viewController.setSelectedAddress(result, inputField, type);
    }

    public void appendSuggestions(List<SearchResult> results, String inputText, JTextField inputField, InputFieldType type) {
        viewController.appendSuggestions(results, inputText, inputField, type);
    }


    public DocumentListener inputDocumentListener(JTextField searchInput, InputFieldType type) {
        return viewController.inputDocumentListener(searchInput, type);
    }

    public void clearSuggestions() {
        viewController.clearSuggestions();
    }

    public void clearLocations() {
        viewController.clearLocations();
    }

    public void toggleShowNearestRoad() {
        viewController.toggleShowNearestRoad();
    }

    public void setAddNode(boolean state) {
        viewController.setAddNode(state);
    }

    public void setClickToFindRoute(boolean state) {
        viewController.setClickToFindRoute(state);
    }

    public void clearSelectedAddress() {
        viewController.clearSelectedAddress();
    }

    public void switchSearchButtonIcon(boolean setDefault) {
        viewController.switchSearchButtonIcon(setDefault);
    }

    public List<SearchResult> findAddresses(String input, int maxNumberOfSearchResults) {
        return modelController.findAddresses(input, maxNumberOfSearchResults);
    }
}
