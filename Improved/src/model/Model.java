package model;

import model.helpers.AddressSearch.HousePlacement;
import model.helpers.AddressSearch.SearchID;
import model.helpers.AddressSearch.SearchResult;
import model.helpers.drawing.EnhancedRoadShape;
import model.helpers.parsers.PostcodeAndCityCenter;
import model.helpers.routeGraph.Dijkstra;
import model.helpers.routeGraph.RouteDescription;
import model.helpers.routeGraph.RouteType;
import model.osm.OSMAddress;
import model.helpers.*;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Model extends Observable {
    private CanvasModel canvasModel;
    private List<Point2D> route;
    private List<Point2D> visited;
    private List<AddedNote> addedNotes;
    private EnhancedRoadShape nearestRoad;
    private Point2D startPoint;
    private GraphMap graphMap;
    private Dijkstra dijkstra;
    private Load load;
    private Save save;
    private KdTree kdTreeHouseNumbers;
    private RouteType routeType     = RouteType.CAR;
    private boolean calculateSpeed  = true;

    private TernarySearchTries<OSMAddress> addresses;
    private TernarySearchTries<PostcodeAndCityCenter> postcodes;
    private TernarySearchTries<List<PostcodeAndCityCenter>> cities;

    public Model(){
        load            = new Load();
        save            = new Save();
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
        addresses = load.getAddresses();
        postcodes = load.getPostcodes();
        cities = load.getCities();
        graphMap = load.getGraphMap();

        route = new ArrayList<>();
        visited = new ArrayList<>();
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


    public float getLonFactor(){
        if(canvasModel == null) return -999; //used as error code to avoid null pointer on program startup
        return canvasModel.getLonFactor();
    }
    public KdTree getKdTreeHouseNumbers() {
        return kdTreeHouseNumbers;
    }

    public boolean saveTGMFile(String fileName) {
        return save.saveTGMFile(fileName, canvasModel, kdTreeHouseNumbers, addresses, postcodes, cities, addedNotes);
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


    public void toggleDrawVisited() {
        dijkstra.toggleDrawVisited();
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



    public RouteDescription findRoute(OSMAddress from, String postcodeFrom, OSMAddress to, String postcodeTo, RouteType routeType, boolean calculateSpeed){
        float fromLon = from.getHousePlacement(postcodeFrom).getLon();
        float fromLat = from.getHousePlacement(postcodeFrom).getLat();
        float toLon = to.getHousePlacement(postcodeTo).getLon();
        float toLat = to.getHousePlacement(postcodeTo).getLat();

        return findRoute(new Point2D.Float(fromLon, fromLat), new Point2D.Float(toLon, toLat), routeType, calculateSpeed);
    }

    public RouteDescription findRoute(Point2D from, Point2D to, RouteType routeType, boolean calculateSpeed){
        this.routeType      = routeType;
        this.calculateSpeed = calculateSpeed;
        startPoint = from;
        dijkstra = new Dijkstra();
        if(from != null && to != null) {
            Point2D startRoad = getClosestPoint(from, routeType);
            Point2D destinationRoad = getClosestPoint(to, routeType);
            if(startRoad == null || destinationRoad == null) return new RouteDescription();
            route   = new ArrayList<>();
            visited = new ArrayList<>();
            route.add(to);
            route.addAll(dijkstra.findRoute(graphMap, startRoad, destinationRoad, routeType, calculateSpeed));
            route.add(from);
            visited.addAll(dijkstra.getVisited());
        } else {
            if(from != null) {
                Point2D startRoad = getClosestPoint(from, routeType);
                if(startRoad == null) return new RouteDescription();
                route   = new ArrayList<>();
                visited = new ArrayList<>();
                route.add(from);
                route.add(startRoad);
                dijkstra.findRoute(graphMap, startRoad, startRoad, routeType, calculateSpeed);
                dirty();
                return new RouteDescription();
            }
        }
        dirty();
        return dijkstra.getRouteDescription();
    }

    public RouteDescription findRoute(Point2D from, Point2D to) {
        return findRoute(from, to, routeType, calculateSpeed);
    }

    public void updateRoute(Point2D newEndCoords) {
        route   = new ArrayList<>();
        visited = new ArrayList<>();
        Point2D destinationNode = getClosestPoint(newEndCoords, routeType);

        route.add(newEndCoords);
        route.addAll(dijkstra.updateRoute(destinationNode));
        route.add(startPoint);
        visited.addAll(dijkstra.getVisited());
        dirty();
    }

    public RouteDescription getRouteDescription() {
        return dijkstra.getRouteDescription();
    }

    public void updateNearestRoad(Point2D modelCoordinate) {
        HashMap<OSMType, KdTree> typeToTreeMap = canvasModel.getMapTree();
        ArrayList<EnhancedRoadShape> closestShapes = new ArrayList<>();
        for(OSMType type : typeToTreeMap.keySet()){
            EnhancedRoadShape enhancedRoadShape;
            if(type.getParent() == null || !type.getParent().equals(OSMTypeParent.HIGHWAY)) continue;
            EnhancedShape enhancedShape = typeToTreeMap.get(type).getNearestNeighbor(modelCoordinate);
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
            return dijkstra.isDrawVisitedOn();
        } catch (NullPointerException e) {
            return false;
        }
    }

    public boolean isUsingSpeedCalculation() {
        return dijkstra.isUsingSpeedCalculation();
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

    public List<SearchResult> findAddresses(String input, int maxNumberOfSearchResults) {
        if(addresses == null || input == null)     return null;
        //((?<street>((\d+?.+?)?[a-zA-ZæøåÆØÅ.\-éäüöÿèëËÈÖÜÄ,'\/ ]+?))\s+(?<house>(\d+))\s?((?<side>[a-zA-ZæøåÆØÅ]?(?![a-zA-ZæøåÆØÅ]))\s*(?<floor>\d)?(?!\d))?(\s+|,|)(,?))?\s*(?<postcode>\d{4})?\s*(?<city>(\d+?.+?)?[a-zA-ZæøåÆØÅ.\-éäüÿèëËÈöÖÜÄ,'\/() ]+)?(?<postcodeNotDone>\d{1,3})?
        final String regex = "((?<street>((\\d+?.+?)?[a-zA-ZæøåÆØÅ.\\-éäüöÿèëËÈÖÜÄ,'\\/ ]+?))\\s+(?<house>(\\d+))\\s?((?<side>[a-zA-ZæøåÆØÅ]?(?![a-zA-ZæøåÆØÅ]))\\s*(?<floor>\\d)?(?!\\d))?(\\s+|,|)(,?))?\\s*(?<postcode>\\d{4})?\\s*(?<city>(\\d+?.+?)?[a-zA-ZæøåÆØÅ.\\-éäüÿèëËÈöÖÜÄ,'\\/() ]+)?(?<postcodeNotDone>\\d{1,3})?";
        Pattern pattern=Pattern.compile(regex);
        Matcher matcher=pattern.matcher(input.trim());
        OSMAddress osmAddress;
        String postcode = null;
        String housenumber;
        if(matcher.matches()) {
            postcode = matcher.group("postcode");
            String cityName = matcher.group("city");
            String streetName = matcher.group("street");
            housenumber = matcher.group("house");
            String postcodeNotDone = matcher.group("postcodeNotDone");
            if(streetName==null) streetName=cityName;
            if(postcodeNotDone != null && postcode == null) postcode = postcodeNotDone;
            if(postcode == null && housenumber != null) postcode = housenumber;
            streetName=removeCommaAtEnd(streetName);
            cityName=removeCommaAtEnd(cityName);
            osmAddress=new OSMAddress(cityName, housenumber, postcode, streetName, 0,0);
        }
        else return null;

        List<SearchResult> searchResults = new ArrayList<>();
        findExactAddress(osmAddress, postcode, housenumber, searchResults, maxNumberOfSearchResults);
        findStartWith(osmAddress, postcode, searchResults, maxNumberOfSearchResults);
        findContains(osmAddress, postcode, searchResults, maxNumberOfSearchResults);
        return searchResults;
    }
    private void findExactAddress(OSMAddress osmAddress, String postcode, String housenumber, List<SearchResult> searchResults, int maxNumberOfSearchResults){
        if(osmAddress.getStreet() != null) {
            OSMAddress foundAddress = addresses.get(osmAddress.getStreet());
            if (foundAddress != null) {
                if (foundAddress.getHousePlacement(postcode) != null) {
                    addExactAddress(foundAddress, postcode, housenumber, searchResults, maxNumberOfSearchResults);
                }
                else{
                    for(String foundPostcode : foundAddress.getPostcodes()){
                        addExactAddress(foundAddress, foundPostcode, housenumber, searchResults, maxNumberOfSearchResults);
                    }
                }
            }
        }
        if(postcode != null) {
            PostcodeAndCityCenter postcodeCenter = postcodes.get(postcode);
            if (postcodeCenter != null){
                searchResults.add(new SearchResult(postcodeCenter.toOSMAddress(), postcode, SearchID.POSTCODE));
                if(searchResults.size() >= maxNumberOfSearchResults) return;
            }
        }
        if(osmAddress.getCity() != null) {
            List<PostcodeAndCityCenter> citiesCenter = cities.get(osmAddress.getCity());
            if (citiesCenter != null) {
                for (PostcodeAndCityCenter cityCenter : citiesCenter) {
                    if (cityCenter != null){
                        searchResults.add(new SearchResult(cityCenter.toOSMAddress(), cityCenter.getPostcode(), SearchID.CITY));
                        if(searchResults.size() >= maxNumberOfSearchResults) return;
                    }
                }
            }
        }
    }
    private void addExactAddress(OSMAddress foundAddress, String foundPostcode, String housenumber, List<SearchResult> searchResults, int maxNumberOfSearchResults){
        if (housenumber != null) {
            List<HousePlacement> housePlacements = new ArrayList<>();
            for (HousePlacement housePlacement : foundAddress.getHousePlacements(foundPostcode)) {
                if (housePlacement.getHouseNumber().startsWith(housenumber)) {
                    housePlacements.add(housePlacement);
                    if (searchResults.size() == maxNumberOfSearchResults) return;
                }
            }
            for (HousePlacement housePlacement : housePlacements) {
                searchResults.add(new SearchResult(new OSMAddress(foundAddress.getCity(), housePlacement, foundPostcode, foundAddress.getStreet()), foundPostcode, SearchID.HOUSENUMBER));
                if (searchResults.size() == maxNumberOfSearchResults) return;
            }
        }
        else {
            for (HousePlacement housePlacement : foundAddress.getHousePlacements(foundPostcode)) {
                searchResults.add(new SearchResult(new OSMAddress(foundAddress.getCity(), housePlacement, foundPostcode, foundAddress.getStreet()), foundPostcode, SearchID.HOUSENUMBER));
                if (searchResults.size() >= maxNumberOfSearchResults) return;
            }
        }
    }

    private void findStartWith(OSMAddress osmAddress, String postcode, List<SearchResult> searchResults, int maxNumberOfSearchResults){
        if(osmAddress.getStreet() != null){
            List<String> list = addresses.getKeySetWithprefix(osmAddress.getStreet());
            addListOfAddresses(list, searchResults, maxNumberOfSearchResults);
            if (searchResults.size() == maxNumberOfSearchResults) return;
        }
        if(postcode != null){
            List<String> validPostcodes = postcodes.getKeySetWithprefix(postcode);
            for(String postcodeFound : validPostcodes) {
                PostcodeAndCityCenter cityCenter = postcodes.get(postcodeFound);
                searchResults.add(new SearchResult(cityCenter.toOSMAddress(), postcodeFound, SearchID.POSTCODE));
                if (searchResults.size() == maxNumberOfSearchResults) return;

            }
        }
        if(osmAddress.getCity() != null){
            List<String> validCities = cities.getKeySetWithprefix(osmAddress.getCity().toLowerCase());
            addListOfCities(validCities, searchResults, maxNumberOfSearchResults);
            if (searchResults.size() == maxNumberOfSearchResults) return;
        }
    }
    private void findContains(OSMAddress osmAddress, String postcode, List<SearchResult> searchResults, int maxNumberOfSearchResults){
        if(osmAddress.getStreet() != null){
            List<String> list = addresses.keysetContaining(osmAddress.getStreet());
            addListOfAddresses(list, searchResults, maxNumberOfSearchResults);
        }
        if(osmAddress.getCity() != null){
            List<String> validCities = cities.keysetContaining(osmAddress.getCity().toLowerCase());
            addListOfCities(validCities, searchResults, maxNumberOfSearchResults);
        }
    }
    private void addListOfAddresses(List<String> list, List<SearchResult>searchResults, int maxNumberOfSearchResults){
        for(String address : list){
            OSMAddress currOSMAddress = addresses.get(address);
            for(String currPostcode : currOSMAddress.getPostcodes()) {
                searchResults.add(new SearchResult(currOSMAddress, currPostcode, SearchID.ADDRESS));
                if (searchResults.size() == maxNumberOfSearchResults) return;
            }
        }
    }
    private void addListOfCities(List<String> validCities, List<SearchResult>searchResults, int maxNumberOfSearchResults){
        for(String cityFound : validCities) {
            List<PostcodeAndCityCenter> cityCenterList = cities.get(cityFound);
            for(PostcodeAndCityCenter cityCenter : cityCenterList) {
                searchResults.add(new SearchResult(cityCenter.toOSMAddress(), cityCenter.getPostcode(), SearchID.CITY));
                if(searchResults.size()==maxNumberOfSearchResults) return;
            }
        }
    }


    private String removeCommaAtEnd(String s){
        if(s!=null && s.charAt(s.length()-1)==','){
            s= s.substring(0, s.length() - 1);
            s=s.trim();
        }
        return s;
    }
}
