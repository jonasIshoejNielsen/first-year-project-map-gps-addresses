package model.helpers.parsers;

import model.helpers.drawing.EnhancedShape;
import model.osm.OSMNode;
import model.osm.OSMType;
import model.osm.OSMWay;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CoastlineParserTest {
    private EnumMap<OSMType, List<EnhancedShape>> enumMap;
    private CoastlineParser coastlineParser;
    private ArrayList<OSMNode> nodes;

    @BeforeEach
    void setUp() {
        nodes = new ArrayList<>();
        int lon = 0;
        int lat = -75;
        for(int i = 0; i < 41; i++){
            nodes.add(new OSMNode(lon++, lat));
        }
        lon = 10;
        lat = -50;
        for(int i = 0; i < 41; i++){
            nodes.add(new OSMNode(lon, lat--));
        }
        enumMap = new EnumMap<>(OSMType.class);
        for (OSMType type: OSMType.values()) {
            enumMap.put(type, new ArrayList<>());
        }
        coastlineParser = new CoastlineParser(enumMap);
        float[] bounds = { -70, 5, -80, 15 };
        coastlineParser.setBounds(bounds);
    }

    @Test
    @DisplayName("insertCoastline given coastline pieces merges them all to one")
    void insertCoastlineGivenTenCoastlinePiecesMergesThemAllToOne(){
        ArrayList<OSMWay> coastlines = new ArrayList<>();
        int k = 0;
        for(int i = 0; i < 10; i++){
            coastlines.add(new OSMWay());
            for(int j = 0; j < 5; j++)
                coastlines.get(i).add(nodes.get(k++));
            k--;
        }
        for(OSMWay coastline : coastlines) {
            coastlineParser.insertCoastline(coastline);
        }
        Map<OSMNode, OSMWay> result = coastlineParser.getCoastlines();
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("insertCoastline given crossing coastline pieces merges them into two coastlines")
    void insertCoastlineGivenTenCoastlinePiecesMergesThemIntoTwoCoastlines(){
        ArrayList<OSMWay> coastlines = new ArrayList<>();
        int k = 0;
        for(int i = 0; i < 20; i++){
            coastlines.add(new OSMWay());
            for(int j = 0; j < 5; j++)
                coastlines.get(i).add(nodes.get(k++));
            k--;
            if(k == 40) k++;

        }
        for(OSMWay coastline : coastlines) {
            coastlineParser.insertCoastline(coastline);
        }
        Map<OSMNode, OSMWay> result = coastlineParser.getCoastlines();
        assertEquals(4, result.size());
    }

    @Test
    @DisplayName("addCoastlinesToMap closes map correctly")
    void addCoastlinesToMapClosesMapCorrectly(){
        ArrayList<OSMWay> coastlines = new ArrayList<>();
        int k = 0;
        for(int i = 0; i < 20; i++){
            coastlines.add(new OSMWay());
            for(int j = 0; j < 5; j++)
                coastlines.get(i).add(nodes.get(k++));
            k--;
            if(k == 40) k++;

        }
        for(OSMWay coastline : coastlines) {
            coastlineParser.insertCoastline(coastline);
        }
        coastlineParser.addCoastlinesToMap();
        assertEquals(1, enumMap.get(OSMType.COASTLINE).size());
    }
}
