package model.helpers.parsers;

import model.helpers.maps.OSMNodeMap;
import model.helpers.maps.OSMWayMap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class OSMParserTest {
    private Path resourceDirectory = Paths.get("testResources");

    @Test
    @DisplayName("Given legal OSM file returns true")
    void legalOSMReturnsTrue()
    {
        boolean success = false;
        try {
            FileInputStream inputStream = new FileInputStream(resourceDirectory + "/osmParsering/goodOSM.osm");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            OSMParser parser = new OSMParser(inputStream.getChannel().size(), false);
            success = parser.parseOSM(inputStreamReader);

        } catch (IOException e) {
            e.printStackTrace();
        }
        assertTrue(success);
    }

    @Test
    @DisplayName("Given bad generator OSM file returns false")
    void badGeneratorOSMReturnsFalse()
    {
        boolean success = true;
        try {
            FileInputStream inputStream = new FileInputStream(resourceDirectory + "/osmParsering/badTypeOSM.osm");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            OSMParser parser = new OSMParser(inputStream.getChannel().size(), false);
            success = parser.parseOSM(inputStreamReader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertFalse(success);
    }

    @Test
    @DisplayName("Given bad type OSM file returns false")
    void badTypeOSMReturnsFalse()
    {
        boolean success = true;
        try {
            FileInputStream inputStream = new FileInputStream(resourceDirectory + "/osmParsering/badGeneratorOSM.osm");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            OSMParser parser = new OSMParser(inputStream.getChannel().size(), false);
            success = parser.parseOSM(inputStreamReader);

        } catch (IOException e) {
            e.printStackTrace();
        }
        assertFalse(success);
    }

    @Test
    @DisplayName("Given bounds expected bounds are returned")
    void boundsAreSetCorrectly()
    {
        float[] expectedArray = { -55.6688300f, 7.0937371924493f, -55.67034f, 7.096308870556075f };
        try {
            FileInputStream inputStream = new FileInputStream(resourceDirectory + "/osmParsering/bounds.osm");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            OSMParser parser = new OSMParser(inputStream.getChannel().size(), false);
            parser.parseOSM(inputStreamReader);
            float[] resultArray = parser.getBounds();

            for (int i = 0; i < expectedArray.length; i++) {
                assertEquals(expectedArray[i], resultArray[i], 0.000001);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    @DisplayName("Given expected nodes, correct nodes can be pulled from nodeMap")
    void nodesAreReadCorrectly()
    {
        long[] expectedIDArray = {125399, 8079932, 18011548};

        try {
            FileInputStream inputStream = new FileInputStream(resourceDirectory + "/osmParsering/nodeTest.osm");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            OSMParser parser = new OSMParser(inputStream.getChannel().size(), false);
            parser.parseOSM(inputStreamReader);
            OSMNodeMap nodeMap = parser.getNodeMap();

            for(long id : expectedIDArray) assertTrue(nodeMap.contains(id));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //TODO: When more tags are added, add new test for checking tagging logic
    @Test
    @DisplayName("Given expected ways, correct ways can be pulled from wayMap")
    void waysAreReadCorrectly()
    {
        long[] expectedIDArray = {530983733, 53083733, 53733};

        try {
            FileInputStream inputStream = new FileInputStream(resourceDirectory + "/osmParsering/wayTest.osm");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            OSMParser parser = new OSMParser(inputStream.getChannel().size(), false);
            parser.parseOSM(inputStreamReader);
            OSMWayMap wayMap = parser.getWayMap();

            for(long id : expectedIDArray) assertTrue(wayMap.contains(id));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
