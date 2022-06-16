package model.osm;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OSMTypeParentTest {

    @Test
    @DisplayName("Testing the number of children is correct")
    void getChidrenTest() {
        assertEquals(17, OSMTypeParent.WATERWAY.getChildren().size());
        assertEquals(9, OSMTypeParent.BARRIER.getChildren().size());
        assertEquals(2, OSMTypeParent.BOUNDARY.getChildren().size());

        assertTrue(OSMTypeParent.BUILDING.getChildren().contains(OSMType.YES_BU));
        assertFalse(OSMTypeParent.AMENITY.getChildren().contains(OSMType.TOWER));
    }

}