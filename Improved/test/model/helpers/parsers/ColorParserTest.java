package model.helpers.parsers;

import model.helpers.Load;
import model.osm.OSMType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class ColorParserTest {

    @BeforeEach
    void setUp() {
        Load load = new Load();
        load.load("testResources/colorParsing/testXML.tmc");
    }

    @Test
    @DisplayName("Testing that normal coloring works")
    void testColorParsing() {
        assertEquals(new Color(233,231, 226), OSMType.AERODROME.getColor());
        assertEquals(new Color(238,207, 179), OSMType.ALLOTMENTS_LA.getColor());
        assertEquals(new Color(82,167, 80), OSMType.BRIDLEWAY.getColor());
        assertEquals(new Color(217, 208, 201), OSMType.YES_BU.getColor());
    }

    @Test
    @DisplayName("Testing that alpha coloring works")
    void testAlphaParsing() {
        assertEquals(new Color(255,0,0,50),OSMType.TRAINING_AREA.getColor());
    }

    @Test
    @DisplayName("Testing that uncolored types get default color")
    void testDefaultColoring() {
        assertEquals(new Color(217,208,201), OSMType.BAY_NA.getColor());
        assertEquals(new Color(217,208,201), OSMType.CYCLEWAY_HIG.getColor());
    }

    @Test
    @DisplayName("Testing that wrong inputs get default color")
    void testWrongRGBA() {
        assertEquals(new Color(217,208,201), OSMType.TERTIARY.getColor());
        assertEquals(new Color(217,208,201), OSMType.CONSTRUCTION_LA.getColor());
        assertEquals(new Color(217,208,201,255), OSMType.HANGAR_AE.getColor());
    }

    @Test
    @DisplayName("Testing coloring around the RGB limit")
    void testBorderColors() {
        assertEquals(new Color(255, 255, 255), OSMType.POOL.getColor());
        assertEquals(new Color(217, 208, 201), OSMType.PIER.getColor());
        assertEquals(new Color(0,0,0), OSMType.AIRFIELD.getColor());
        assertEquals(new Color(217,208,201), OSMType.TREE.getColor());
    }
}