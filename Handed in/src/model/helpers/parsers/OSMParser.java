package model.helpers.parsers;

import model.CanvasModel;
import model.helpers.AddressSearch.HousePlacement;
import model.helpers.AddressSearch.SearchID;
import model.helpers.AddressSearch.SearchListBuilder;
import model.helpers.KdTree;
import model.helpers.drawing.EnhancedAddressShape;
import model.helpers.drawing.EnhancedRoadShape;
import model.helpers.drawing.EnhancedShape;
import model.helpers.maps.OSMNodeMap;
import model.helpers.maps.OSMRelationMap;
import model.helpers.maps.OSMWayMap;
import model.helpers.routeGraph.GraphCreater;
import model.helpers.routeGraph.GraphMap;
import model.osm.*;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

import static java.lang.Math.incrementExact;
import static java.lang.Math.max;
import static java.lang.Math.toIntExact;

public class OSMParser {
    private CanvasModel canvasModel;
    private Map<OSMType, List<EnhancedShape>> enumMap;
    private OSMNodeMap nodeMap;
    private OSMWayMap wayMap;
    private OSMRelationMap relationMap;

    private XMLStreamReader streamReader;
    private String errorMessage;

    private float minLat, minLon, maxLat, maxLon, lonFactor;

    private int nodeCount = 0, wayCount = 0, relCount = 0, graphNodes = 0, mapProgres = 0;
    private int nodeCountExpected, wayCountExpected, relCountExpected, fileSize;
    private boolean isBuildingMap = false;
    private int latIndex, lonIndex;

    private CoastlineParser coastlineParser;
    private TypeParser typeParser;
    private AddressParser addressParser = new AddressParser();
    private ArrayList<String> missingTypes = new ArrayList<>();
    private GraphMap graphMap;
    private boolean useTestMethods;
    private KdTree kdTreeHouseNumbers;
    private SearchListBuilder searchListBuilder;


    public OSMParser(long _fileSize, boolean useTestMethods) {
        this.useTestMethods = useTestMethods;
        typeParser = new TypeParser();
        fileSize = new Long(_fileSize / 1000).intValue();
        if (fileSize < 100) fileSize = 100;
        nodeMap = new OSMNodeMap(new Double(fileSize * 4).intValue(), useTestMethods);
        wayMap = new OSMWayMap(new Double(fileSize * 0.5).intValue(), useTestMethods);
        relationMap = new OSMRelationMap(new Double(fileSize * 0.005).intValue(), useTestMethods);
        enumMap = Collections.synchronizedMap(new EnumMap<>(OSMType.class));
        for (OSMType type : OSMType.values())   enumMap.put(type, new ArrayList<>());
        coastlineParser = new CoastlineParser(enumMap);
        nodeCountExpected = toIntExact(fileSize*4);
        wayCountExpected = toIntExact(fileSize/2);
        relCountExpected = toIntExact(fileSize/100);
    }

    public boolean parseOSM(InputStreamReader inputStreamReader) {
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        BufferedReader xmlReader = new BufferedReader(inputStreamReader);
        try {
            streamReader = inputFactory.createXMLStreamReader(xmlReader);
            return parse();
        } catch (XMLStreamException e) {
            errorMessage =  "OSM fil er korrupt.";
            return false;
        }
    }

    private boolean parse() throws XMLStreamException{
        double startTime = System.currentTimeMillis();
        if(!validateOSM()) {
            errorMessage = "OSM filen er ikke skrevet\ni en generator der er understøttet.";
            return false;
        }
        if(useTestMethods) System.out.println("OSM validated");
        parseBounds();
        if(useTestMethods) System.out.println("Bounds found");
        parseNodes();
        parseWays();
        parseRelations();
        isBuildingMap = true;

        List<Thread> threads = new ArrayList<>();
        Thread thread = new Thread(() -> {
            GraphCreater graphCreater;
            if(useTestMethods) graphCreater = new GraphCreater(enumMap, fileSize, lonFactor, true);
            else graphCreater = new GraphCreater(enumMap, fileSize, lonFactor, false);
            graphMap = graphCreater.getGraphMap();
            graphNodes = graphCreater.getGraphNodes();
            mapProgres++;
        });
        threads.add(thread);
        thread.start();

        threads = new ArrayList<>();
        thread = new Thread(() -> {
            RelationShapeBuilder.createShapes(enumMap, relationMap);
            mapProgres++;
        });
        threads.add(thread);
        thread.start();

        thread = new Thread(() -> {
            coastlineParser.setBounds(getBounds());
            coastlineParser.addCoastlinesToMap();
            mapProgres++;
        });
        threads.add(thread);
        thread.start();

        thread = new Thread(() -> {
            addressParser.resolveAddressWaitlist(); //Finish off the last waiting addresses
            mapProgres++;
            searchListBuilder = new SearchListBuilder(addressParser.getAddresses());
            buildKDtreeNumbers();
            mapProgres++;
        });
        threads.add(thread);
        thread.start();

        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        canvasModel = new CanvasModel(enumMap, getBounds(), lonFactor);



        //****
        //Test and debug code.
        //****
        if(useTestMethods) {
            double time = System.currentTimeMillis();
            time -= startTime;
            System.out.println("osm loaded in " + time / 1000 + " seconds");
            DecimalFormat df = new DecimalFormat("#.####");
            df.setRoundingMode(RoundingMode.CEILING);
            float elements = nodeCount + wayCount + relCount;
            float nodeProcent = (nodeCount / elements) * 100;
            float wayProcent = (wayCount / elements) * 100;
            float relProcent = (relCount / elements) * 100;
            System.out.println(fileSize + "kb");
            System.out.println("Nodes: " + nodeCount + ". Ways: " + wayCount + ". Relations: " + relCount + ". In all: " + elements);
            System.out.println("Nodes: " + df.format(nodeProcent) + "%. Ways: " + df.format(wayProcent) + "%. Relations: " + df.format(relProcent) + "%");
            System.out.println("Nodes per kb: " + df.format((float) nodeCount / fileSize) + ". Ways per kb: " + df.format((float) wayCount / fileSize) + ". Relations per kb: " + df.format((float) relCount / fileSize));
            System.out.println("Nodemap collisions: " + nodeMap.getCollisions() + " of " + nodeCount);
            System.out.println("WayMap collisions: " + wayMap.getCollisions() + " of " + wayCount);
            System.out.println("RelMap collisions: " + relationMap.getCollisions() + " of " + relCount);
            System.out.println("Graphmap collisions: " + graphMap.getCollisions() + " of " + graphNodes);
            System.out.println("Nodemap size: " + fileSize * 3);
            System.out.println("Number of adresses: " + addressParser.getAddresses().size());

            System.out.println("Missing types:");
            for (String type : missingTypes)
                System.out.println(type);
        }
        return true;
    }

    private boolean validateOSM() throws XMLStreamException {
        while (streamReader.hasNext()) {
            streamReader.next();
            if (streamReader.isStartElement()) {
                String localName = streamReader.getLocalName();
                if (localName.equals("osm")) {
                    //index 0 should be osm version, index 1 generator type
                    if ("CGImap".equals(streamReader.getAttributeValue(1).substring(0, 6))) {
                        latIndex = 7;
                        lonIndex = 8;
                        return true;
                    }
                    if ("osmconvert".equals(streamReader.getAttributeValue(1).substring(0, 10))) {
                        latIndex = 1;
                        lonIndex = 2;
                        return true;
                    }
                    if ("Overpass".equals(streamReader.getAttributeValue(1).substring(0, 8))) {
                        latIndex = 1;
                        lonIndex = 2;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void parseBounds() throws XMLStreamException {
        while (streamReader.hasNext()) {
            streamReader.next();
            if (streamReader.isStartElement()) {
                String localName = streamReader.getLocalName();
                if(localName.equals("bounds")){
                    minLat = Float.parseFloat(streamReader.getAttributeValue(0));
                    minLon = Float.parseFloat(streamReader.getAttributeValue(1));
                    maxLat = Float.parseFloat(streamReader.getAttributeValue(2));
                    maxLon = Float.parseFloat(streamReader.getAttributeValue(3));
                    float avgLat = minLat + (maxLat - minLat) / 2;
                    lonFactor = (float) Math.cos(avgLat / 180 * Math.PI);
                    minLon *= lonFactor;
                    maxLon *= lonFactor;
                    maxLat = -maxLat;
                    minLat = -minLat;
                    break;
                }
            }
        }
    }

    private void parseNodes() throws XMLStreamException {
        while (streamReader.hasNext()) {
            if (streamReader.isStartElement()) {
                String localName = streamReader.getLocalName();
                if (localName.equals("node")) {
                    nodeCount++;
                    long id = Long.parseLong(streamReader.getAttributeValue(0));
                    float lat = -Float.parseFloat(streamReader.getAttributeValue(latIndex));
                    float lon = lonFactor * Float.parseFloat(streamReader.getAttributeValue(lonIndex));
                    nodeMap.put(id, lon, lat);
                    addressParser.parseAdress(streamReader, lat, lon);
                }
                if (localName.equals("way")) break;
            }
            streamReader.next();
        }
    }

    private void parseWays() throws XMLStreamException {
        while (streamReader.hasNext()) {
            if (streamReader.isStartElement()) {
                String localName = streamReader.getLocalName();
                if (localName.equals("way")) {
                    wayCount++;
                    OSMWay way = new OSMWay();
                    long id = Long.parseLong(streamReader.getAttributeValue(0));
                    parseInnerWay(id, way);
                }
                if (localName.equals("relation")) break;
            }
            streamReader.next();
        }
    }

    private void parseInnerWay(long id, OSMWay way) throws XMLStreamException {
        OSMType type = OSMType.UNKNOWN;
        String name = "";
        boolean isBicycleAllowed = true, isWalkingAllowed = true, isOneway = false, isReverseDirection = false;
        int maxSpeed = -1;

        while (streamReader.hasNext()) {
            if (streamReader.isStartElement()) {
                switch (streamReader.getLocalName()) {
                    case "nd":
                        way.add(nodeMap.get(Long.parseLong(streamReader.getAttributeValue(0))));
                        break;
                    case "tag":
                        String tag = streamReader.getAttributeValue(0).toLowerCase();
                        switch (tag) {
                            case "maxspeed":
                                String speed = streamReader.getAttributeValue(1);
                                speed = speed.replaceAll("\\D", "");
                                if(speed.equals("")) break;
                                maxSpeed = Integer.parseInt(speed);
                                //TODO: handle non integers
                                break;
                            case "oneway":
                                if(streamReader.getAttributeValue(1).equals("yes")) isOneway = true;
                                if(streamReader.getAttributeValue(1).equals("-1")) {
                                    isOneway = true;
                                    isReverseDirection = true;
                                }
                                break;
                            case "bicycle":
                                String value = streamReader.getAttributeValue(1);
                                if(value.equals("yes")) isBicycleAllowed = true; isWalkingAllowed = true;
                                break;
                            case "name":
                                name = streamReader.getAttributeValue(1).intern();
                                break;
                            case "junction":
                                isOneway = true;
                                break;
                            default:
                                OSMType newType = typeParser.setType(streamReader);
                                if(useTestMethods) { //Method for finding missing types
                                    if (newType == null) {
                                        String missingType = streamReader.getAttributeValue(0).toLowerCase() + " / " + streamReader.getAttributeValue(1).toLowerCase();
                                        if (!missingTypes.contains(missingType))
                                            missingTypes.add(missingType);
                                    }
                                }
                                if (newType != null && !newType.equals(OSMType.UNKNOWN)) {
                                    if (type != OSMType.UNKNOWN) {
                                        createWayShape(type, way, name, isOneway, isReverseDirection, isBicycleAllowed, isWalkingAllowed, maxSpeed);
                                    }
                                    type = newType;
                                }
                        }
                        break;
                    default:
                        break;
                }
            }
            if (streamReader.isEndElement()) {
                if (streamReader.getLocalName().equals("way")) {
                    wayMap.put(id, way);
                    createWayShape(type, way, name, isOneway, isReverseDirection, isBicycleAllowed, isWalkingAllowed, maxSpeed);
                    return;
                }
            }
            streamReader.next();
        }
    }

    private void createWayShape(OSMType type, OSMWay way, String name, boolean isOneway, boolean isReverseDirection, boolean isBicycleAllowed, boolean isWalkingAllowed, int maxSpeed) {
        Path2D path = new Path2D.Float();
        if (type == null) return;
        if (type == OSMType.COASTLINE) {
            coastlineParser.insertCoastline(way);
        } else if (type.getParent() != null && type.getParent() == OSMTypeParent.HIGHWAY) {
            if(maxSpeed == -1 || maxSpeed == 0) maxSpeed = type.getSpeed();
            OSMNode node = way.get(0);
            path.moveTo(node.getLon(), node.getLat());
            for (int i = 1; i < way.size(); i++) {
                node = way.get(i);
                path.lineTo(node.getLon(), node.getLat());
            }
            if(!type.isBicycleAllowed()) isBicycleAllowed = false;
            if(!type.isWalkingAllowed()) isWalkingAllowed = false;
            enumMap.get(type).add(new EnhancedRoadShape(path, name, isOneway, isReverseDirection, isBicycleAllowed, isWalkingAllowed, maxSpeed));
        } else {
            OSMNode node = way.get(0);
            path.moveTo(node.getLon(), node.getLat());
            for (int i = 1; i < way.size(); i++) {
                node = way.get(i);
                path.lineTo(node.getLon(), node.getLat());
            }
            enumMap.get(type).add(new EnhancedShape(path));
        }
    }

    private void parseRelations() throws XMLStreamException {
        while (streamReader.hasNext()) {
            if (streamReader.isStartElement()) {
                String localName = streamReader.getLocalName();
                if (localName.equals("relation")) {
                    relCount++;
                    OSMRelation relation = new OSMRelation();
                    parseInnerRelations(relation, Long.parseLong(streamReader.getAttributeValue(0)));
                }
            }
            streamReader.next();
        }
    }

    private void parseInnerRelations(OSMRelation relation, long id) throws XMLStreamException {
        OSMType type = OSMType.UNKNOWN;
        while (streamReader.hasNext()) {
            if (streamReader.isStartElement()) {
                switch (streamReader.getLocalName()) {
                    case "member":
                        if (streamReader.getAttributeValue(0).equals("way"))
                            relation.addNode(wayMap.get(Long.parseLong(streamReader.getAttributeValue(1))), streamReader.getAttributeValue(2));
                        else if (streamReader.getAttributeValue(0).equals("node")) {
                            relation.addNode(nodeMap.get(Long.parseLong(streamReader.getAttributeValue(1))), streamReader.getAttributeValue(2));
                        } else if (streamReader.getAttributeValue(0).equals("relation"))
                            relation.addNode(Long.parseLong(streamReader.getAttributeValue(1)), streamReader.getAttributeValue(2));
                        break;
                    case "tag":
                        OSMType newType = typeParser.setType(streamReader);
                        if (newType == null) {
                            String missingType = streamReader.getAttributeValue(0).toLowerCase() + " / " + streamReader.getAttributeValue(1).toLowerCase();
                            if (!missingTypes.contains(missingType))
                                missingTypes.add(missingType);
                        }
                        if (newType != null && !newType.equals(OSMType.UNKNOWN)) type = newType;
                        break;
                    default:
                        break;
                }
            }
            if (streamReader.isEndElement()) {
                if (streamReader.getLocalName().equals("relation")) {
                    relation.setType(type);
                    relationMap.put(id, relation);
                    return;
                }
            }
            streamReader.next();
        }
    }

    private void buildKDtreeNumbers() {
        List<EnhancedShape> numbers = new ArrayList<>();
        for(OSMAddress osmAddress: searchListBuilder.getSearchList().get(SearchID.ADDRESS)) {
            for(HousePlacement placement : osmAddress.getHousePlacements()) {
                if(placement.getHouseNumber() == null) continue;
                float lon = placement.getLon();
                float lat = placement.getLat();

                Rectangle2D rectangle2D = new Rectangle2D.Float(lon, lat, 0.000001f, 0.000001f);
                Point2D point2D = new Point2D.Float(lon, lat);
                numbers.add(new EnhancedAddressShape(rectangle2D, point2D, placement.getHouseNumber()));
            }
        }
        kdTreeHouseNumbers = new KdTree(numbers);
    }

    public CanvasModel getCanvasModel() {
        return canvasModel;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public OSMNodeMap getNodeMap() {
        return nodeMap;
    }

    public OSMWayMap getWayMap() {
        return wayMap;
    }

    public float[] getBounds(){
        return new float[]{ minLat, minLon, maxLat, maxLon };
    }

    public int[] getCountersCurrent(){
        return new int[]{nodeCount, wayCount, relCount};
    }

    public int[] getCountersExpected(){
        return new int[]{nodeCountExpected, wayCountExpected, relCountExpected};
    }

    public GraphMap getGraphMap() {
        return graphMap;
    }

    public boolean getIsBuildingMap() {
        return isBuildingMap;
    }

    public int getMapProgress() {
        return mapProgres;
    }

    public KdTree getKdTreeHouseNumbers() {
        return kdTreeHouseNumbers;
    }

    public HashMap<SearchID,List<OSMAddress>> getSearchLists() {
        return searchListBuilder.getSearchList();
    }
}