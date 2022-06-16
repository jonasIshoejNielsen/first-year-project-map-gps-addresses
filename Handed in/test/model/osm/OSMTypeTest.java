package model.osm;

import model.helpers.drawing.Strokes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class OSMTypeTest {

    @Test
    @DisplayName("Test of the IsEnabled method")
    void testOSMTypeIsEnabled() {
        assertTrue(OSMType.APARTMENTS.isEnabled(4));
        assertFalse(OSMType.LAKE.isEnabled(30));
    }

    @Test
    @DisplayName("Test of the toggle method")
    void testToggle() {
        assertTrue(OSMType.AERODROME.isEnabled(4));
        OSMType.AERODROME.toggle();
        assertFalse(OSMType.AERODROME.isEnabled(4));
        OSMType.AERODROME.toggle();
        assertTrue(OSMType.AERODROME.isEnabled(4));
    }

    @Test
    @DisplayName("Test of the method to change a types color")
    void testChangeColor() {
        OSMType.FARMLAND.changeColor(new Color(200,200,200));
        assertEquals(new Color(200,200,200),OSMType.FARMLAND.getColor());
        OSMType.FARMLAND.changeColor(new Color(100,150,200));
        assertEquals(new Color(100,150,200),OSMType.FARMLAND.getColor());
        OSMType.VINEYARD.changeColor(new Color(200,100,50,200));
        assertEquals(new Color(200,100,50,200),OSMType.VINEYARD.getColor());
    }

    @Test
    @DisplayName("Test of the method to get the strokes")
    void testGetStrokes() {
        assertEquals(Strokes.DOTTEDSTROKE.getStroke(),OSMType.BRIDLEWAY.getStroke());
        assertEquals(Strokes.FATSTROKE.getStroke(),OSMType.SECONDARY.getStroke());
        assertEquals(Strokes.SMALLSTROKE.getStroke(),OSMType.PEDESTRIAN.getStroke());
    }

    @Test
    @DisplayName("Test of the method to get a parent of a type")
    void testGetParent() {
        assertEquals(OSMTypeParent.BUILDING, OSMType.HOUSE.getParent());
        assertEquals(OSMTypeParent.BARRIER, OSMType.HEDGE.getParent());
    }
}