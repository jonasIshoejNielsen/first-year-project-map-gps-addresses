package controller;

import model.Model;
import model.helpers.AddressSearch.SearchResult;
import model.helpers.parsers.OSMParser;
import model.helpers.routeGraph.RouteDescription;
import model.helpers.routeGraph.RouteStep;
import model.helpers.routeGraph.RouteType;
import model.osm.OSMAddress;

import java.util.List;

public class ModelController {
    private Model model;

    public ModelController(Model model){
        this.model = model;
    }

    public boolean load(String fileName) {
        return model.load(fileName);
    }

    public boolean saveMap(String fileName) {
        return model.saveTGMFile(fileName);
    }

    public boolean saveColorTheme(String fileName) {
        return model.saveTMCFile(fileName);
    }

    public String getLoadErrorMessage() {
        return model.getLoadErrorMessage();
    }

    public String getSaveErrorMessage() {
        return model.getSaveErrorMessage();
    }

    public OSMParser getOSMParser(){
        return model.getOsmParser();
    }

    public void loadMapFromResource() {
        model.loadMapResource();
    }

    public void loadThemeFromResource() {
        model.loadThemeResource();
    }

    public RouteDescription findRoute(OSMAddress from, OSMAddress to, RouteType routeType, boolean calculateSpeed) {
        return model.findRoute(from, to, routeType, calculateSpeed);
    }

    public void toggleLoadWithTestInfo() {
        model.toggleLoadWithTestInfo();
    }

    public void toggleDrawVisited(){
        model.toggleDrawVisited();
    }

    public List<SearchResult> houseSearch(String src, String houseNumber) {
        return model.houseSearch(src, houseNumber);
    }

    public boolean isTestInfoOn() {
        return model.isTestInfoOn();
    }

    public boolean isDrawVisitedOn() {
        return model.isDrawVisitedOn();
    }
}
