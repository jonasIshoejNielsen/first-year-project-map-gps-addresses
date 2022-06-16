package model;

import model.helpers.AddressSearch.*;
import model.helpers.drawing.EnhancedRoadShape;
import model.helpers.routeGraph.RouteDescription;
import model.helpers.routeGraph.RouteType;
import model.osm.OSMAddress;
import model.helpers.*;
import model.helpers.drawing.EnhancedAddressShape;
import model.helpers.drawing.EnhancedShape;
import model.helpers.parsers.OSMParser;
import model.helpers.routeGraph.GraphMap;
import model.osm.OSMType;
import model.osm.OSMTypeParent;

import javax.swing.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.List;

public class Model extends Observable {
    private CanvasModel canvasModel;
    private HashMap<SearchID, List<OSMAddress>> searchLists;

    private UserInputList userInputList;
    private List<Point2D> route;
    private List<Point2D> visited;
    private List<AddedNote> addedNotes;
    private EnhancedRoadShape nearestRoad;
    private Point2D startPoint;
    private GraphMap graphMap;
    private Load load;
    private Save save;
    private KdTree kdTreeHouseNumbers;
    private BinarySearch binarySearch;
    private RouteType routeType     = RouteType.CAR;
    private boolean calculateSpeed  = true;

    private final int CITIES = 2;
    private final int ADDRESSES = 6;

    public Model(){
        load            = new Load();
        save            = new Save();
        searchLists     = new HashMap<>();
        binarySearch    = new BinarySearch();
        addedNotes      = new ArrayList<>();
        nearestRoad     = null;
    }

    public void loadMapResource(){
        load.loadMapFromResource();
        buildModel();
    }

    public void loadThemeResource(){
        load.loadThemeFromResource();
        dirty();
    }

    public boolean load(String filename){
        if(load.load(filename)) {
            buildModel();
            return true;
        }
        return false;
    }
    private void buildModel() {
        canvasModel = load.getCanvasModel();
        kdTreeHouseNumbers = load.getKdTreeHouseNumbers();
        searchLists = load.getSearchLists();
        graphMap = load.getGraphMap();

        route = new ArrayList<>();
        visited = new ArrayList<>();
        userInputList = new UserInputList();
        addedNotes = load.getAddedNotes();
        dirty();
    }

    private void dirty() {
        setChanged();
        notifyObservers();
    }

    public CanvasModel getCanvasModel() {
        return canvasModel;
    }

    public List<OSMAddress> getAddresses() {
        return searchLists.get(SearchID.ADDRESS);
    }

    public String getLoadErrorMessage() {
        return load.getErrorMessage();
    }

    public OSMParser getOsmParser() {
        return load.getOsmParser();
    }

    public Point2D getClosestPoint(Point2D point, RouteType routeType) {
        if(canvasModel == null || graphMap == null) return null;
        return canvasModel.findNearestNode(point, routeType, graphMap);
    }

    public List<SearchResult> houseSearch(String src, String houseNumber) {
        OSMAddress address;
        //Fix for preventing unknown exception when searching for addresses
        try {
            address = addressSearch(src.trim()).get(0).getAddress();
        } catch (IndexOutOfBoundsException e){
            return new ArrayList<>();
        }
        address.sortHousePlacements();
        List<SearchResult> results = new ArrayList<>();

        int counter = 0;
        for(HousePlacement placement : address.getHousePlacements()) {
            String number = placement.getHouseNumber();
            if(number.startsWith(houseNumber)) {
                OSMAddress temp = new OSMAddress(address, placement);
                SearchResult result = new SearchResult(temp, SearchID.HOUSENUMBER);
                results.add(result);
                counter++;
            }

            if(counter >= ADDRESSES + CITIES) break;
        }

        return results;
    }

    public List<SearchResult> addressSearch(String src) {
        src = src.toLowerCase();

        if(src.matches("[0-9]{3,4}"))   return postcodeSearch(src);
        else {
            List<SearchResult> list = search(src, SearchID.CITY);
            list.addAll(search(src, SearchID.ADDRESS));
            return list;
        }
    }

    List<SearchResult> search(String src, SearchID id) {
        List<Integer> indexes = new ArrayList<>();
        findIndexes(src, indexes, id);
        return findSearchResult(indexes, id);
    }

    private List<Integer> findIndexes(String src, List<Integer> indexes, SearchID id) {
        List<OSMAddress> list = searchLists.get(id);
        int index =  binarySearch.rank(list, src, id, indexes);

        if(index < 0) return indexes;

        while (index > 0 && contains(src, list.get(index - 1), id))  index -= 1;

        int max = (id == SearchID.ADDRESS) ? ADDRESSES : CITIES;
        for(int i = 0; i < max; i++) {
            if(list.size() > (index + i) && !indexes.contains(index + i)) {
                if(contains(src, list.get(index + i), id)) {
                    if(indexes.size() >= max)    break;
                    indexes.add(index + i);
                }
            }
        }

        if(indexes.size() < max)  return findIndexes(src, indexes, id);
        return indexes;
    }

    private boolean contains(String key, OSMAddress hit, SearchID id) {
        if(id == SearchID.ADDRESS)   return hit.getStreet().toLowerCase().contains(key);
        else if(id == SearchID.CITY) return hit.getCity().toLowerCase().contains(key);
        return false;
    }

    private List<SearchResult> findSearchResult(List<Integer> indexes, SearchID id) {
        indexes.sort(Comparator.naturalOrder());
        List<SearchResult> results = new ArrayList<>();
        for(Integer integer : indexes) {
            SearchResult result = new SearchResult(searchLists.get(id).get(integer), id);
            results.add(result);
        }
        return results;
    }

    List<SearchResult> postcodeSearch(String src) {
        List<OSMAddress> temp = searchLists.get(SearchID.POSTCODE);
        List<SearchResult> results = new ArrayList<>();
        int index = binarySearch.rank(temp, src, SearchID.POSTCODE, null);

        for(int i = 0; i < ADDRESSES; i++) {
            if(index < 0)   break;
            if(index + i >= searchLists.get(SearchID.POSTCODE).size()) break;
            SearchResult result = new SearchResult(searchLists.get(SearchID.POSTCODE).get(index + i), SearchID.POSTCODE);
            results.add(result);
        }
        return results;
    }

    public float getLonFactor(){
        if(canvasModel == null) return -999; //used as error code to avoid null pointer on program startup
        return canvasModel.getLonFactor();
    }
    public KdTree getKdTreeHouseNumbers() {
        return kdTreeHouseNumbers;
    }

    public boolean saveTGMFile(String fileName) {
        return save.saveTGMFile(fileName, canvasModel, kdTreeHouseNumbers, searchLists, addedNotes);
    }

    public boolean saveTMCFile(String fileName) {
        return save.saveTMCFile(fileName);
    }

    public String getSaveErrorMessage() {
        return save.getErrorMessage();
    }

    public List<Point2D> getRoute() {
        return route;
    }

    public List<Point2D> getVisited() {
        return visited;
    }

    public void toggleLoadWithTestInfo() {
        load.toggleLoadWithTestInfo();
    }

    public UserInputList getUserInputList(){
        return userInputList;
    }

    public void toggleDrawVisited() {
        graphMap.toggleDrawVisited();
    }

    public void clearLocations() {
        route.clear();
        visited.clear();
        dirty();
    }

    public void addNode(Point2D modelCoords) {
        Point2D point = canvasModel.findNearestNode(modelCoords, RouteType.NOT_A_ROUTE, graphMap);

        String nodeName     = getUserInputDialogBOx("Write node name:");
        for(int i = 0; i < addedNotes.size(); i++){
            if(addedNotes.get(i).getCoords().equals(point)){
                addedNotes.remove(i);
                break;
            }
        }
        if(nodeName == null) return;
        if(nodeName.equals("")) return;
        AddedNote addedNote = new AddedNote(point, nodeName);
        addedNotes.add(addedNote);
        dirty();
    }

    public List<AddedNote> getAddedNotes() {
        return addedNotes;
    }

    private String getUserInputDialogBOx(String message){
        return JOptionPane.showInputDialog(null, message);
    }



    public RouteDescription findRoute(OSMAddress from, OSMAddress to, RouteType routeType, boolean calculateSpeed){
        float fromLon = from.getHousePlacement().getLon();
        float fromLat = from.getHousePlacement().getLat();
        float toLon = to.getHousePlacement().getLon();
        float toLat = to.getHousePlacement().getLat();

        return findRoute(new Point2D.Float(fromLon, fromLat), new Point2D.Float(toLon, toLat), routeType, calculateSpeed);
    }

    public RouteDescription findRoute(Point2D from, Point2D to, RouteType routeType, boolean calculateSpeed){
        this.routeType      = routeType;
        this.calculateSpeed = calculateSpeed;
        startPoint = from;
        if(from != null && to != null) {
            Point2D startRoad = getClosestPoint(from, routeType);
            Point2D destinationRoad = getClosestPoint(to, routeType);
            if(startRoad == null || destinationRoad == null) return new RouteDescription();
            route   = new ArrayList<>();
            visited = new ArrayList<>();
            route.add(to);
            route.addAll(graphMap.findRoute(startRoad, destinationRoad, routeType, calculateSpeed));
            route.add(from);
            visited.addAll(graphMap.getVisited());
        } else {
            if(from != null) {
                Point2D startRoad = getClosestPoint(from, routeType);
                if(startRoad == null) return new RouteDescription();
                route   = new ArrayList<>();
                visited = new ArrayList<>();
                route.add(from);
                route.add(startRoad);
                graphMap.findRoute(startRoad, startRoad, routeType, calculateSpeed);
                dirty();
                return new RouteDescription();
            }
        }
        dirty();
        return graphMap.getRouteDescription();
    }

    public RouteDescription findRoute(Point2D from, Point2D to) {
        return findRoute(from, to, routeType, calculateSpeed);
    }

    public void updateRoute(Point2D newEndCoords) {
        route   = new ArrayList<>();
        visited = new ArrayList<>();
        Point2D destinationNode = getClosestPoint(newEndCoords, routeType);

        route.add(newEndCoords);
        route.addAll(graphMap.updateRoute(destinationNode));
        route.add(startPoint);
        visited.addAll(graphMap.getVisited());
        dirty();
    }

    public RouteDescription getRouteDescription() {
        return graphMap.getRouteDescription();
    }

    public void updateNearestRoad(Point2D modelCoordinate) {
        HashMap<OSMType, KdTree> typeToTreeMap = canvasModel.getMapTree();
        ArrayList<EnhancedRoadShape> closestShapes = new ArrayList<>();
        float SEARCH_AREA_SIZE = 0.0001f;
        for(OSMType type : typeToTreeMap.keySet()){
            EnhancedRoadShape enhancedRoadShape;
            if(type.getParent() == null || !type.getParent().equals(OSMTypeParent.HIGHWAY)) continue;
            EnhancedShape enhancedShape = typeToTreeMap.get(type).getNearestNeighbor(
                    new Double(modelCoordinate.getX()).floatValue()-SEARCH_AREA_SIZE,
                    new Double(modelCoordinate.getY()).floatValue()-SEARCH_AREA_SIZE,
                    new Double(modelCoordinate.getX()).floatValue()+SEARCH_AREA_SIZE,
                    new Double(modelCoordinate.getY()).floatValue()+SEARCH_AREA_SIZE);
            if(enhancedShape instanceof EnhancedRoadShape)
                enhancedRoadShape = (EnhancedRoadShape) enhancedShape;
            else continue;
            closestShapes.add(enhancedRoadShape);
        }

        EnhancedRoadShape closestShape = null;

        for(EnhancedRoadShape enhancedRoadShape : closestShapes){
            if(enhancedRoadShape.getShape().contains(modelCoordinate)){
                closestShape = enhancedRoadShape;
                break;
            }
            if(closestShape == null) {
                closestShape = enhancedRoadShape;
                continue;
            }
            if(closestShape.getDistanceToCenter(modelCoordinate) > enhancedRoadShape.getDistanceToCenter(modelCoordinate)) closestShape = enhancedRoadShape;
        }
        nearestRoad = closestShape;
        dirty();
    }

    public EnhancedRoadShape getNearestRoad() {
        return nearestRoad;
    }


    public boolean isTestInfoOn() {
        return load.isTestInfoOn();
    }

    public boolean isDrawVisitedOn() {
        try {
            return graphMap.isDrawVisitedOn();
        } catch (NullPointerException e) {
            return false;
        }
    }

    public boolean isUsingSpeedCalculation() {
        return graphMap.isUsingSpeedCalculation();
    }

    public void changeStartPoint(Point2D modelCoords) {
        changePoint(route.size()-1, modelCoords);
    }
    public void changeEndPoint(Point2D modelCoords) {
        changePoint(0, modelCoords);
    }
    private void changePoint(int index, Point2D modelCoords){
        if(route == null) return;
        route.set(index,modelCoords);
        dirty();
    }
}
