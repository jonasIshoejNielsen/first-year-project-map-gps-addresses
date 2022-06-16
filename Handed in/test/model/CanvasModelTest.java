package model;

import model.osm.OSMType;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CanvasModelTest {
    private CanvasModel canvasModel;
    private Model model;

    @BeforeEach
    void setUp() {
        model = new Model();
        model.load("testResources/load/test.osm");
        canvasModel = model.getCanvasModel();
    }

    @Test
    @DisplayName("Test of the kdtree types on the map")
    void kdTreeTypesTest() {
        List<OSMType> testList = canvasModel.getKdTreeTypes();
        assertEquals(68, testList.size());
        assertEquals(OSMType.VILLAGE, testList.get(0));
        assertEquals(OSMType.YES_BA, testList.get(10));
        assertEquals(OSMType.RESIDENTIAL_BU, testList.get(44));
        assertEquals(OSMType.MILITARY_LA, testList.get(67));
    }

    @Test
    @DisplayName("Testing that the correct bounds are given")
    void boundsTest() {
        float[] test = canvasModel.getBounds();
        assertEquals(-55.6631f, test[0]);
        assertEquals(7.090333f, test[1]);
        assertEquals(-55.6804f, test[2]);
        assertEquals(7.107307f, test[3]);
    }

    @Test
    @DisplayName("Testing bounds for smaller map")
    void smallBoundsTest() {
        model.load("testResources/load/smallMap.osm");
        float[] test = canvasModel.getBounds();
        assertEquals(-55.6631f, test[0]);
        assertEquals(7.090333f, test[1]);
        assertEquals(-55.6804f, test[2]);
        assertEquals(7.107307f, test[3]);
    }

    @Test
    @DisplayName("Testing the lonFactor is correct")
    void lonfactorTest() {
        assertEquals(0.56393325f, canvasModel.getLonFactor());
        model.load("testResources/load/smallMap.osm");
        assertEquals(0.56393325f, canvasModel.getLonFactor());
    }

    @Test
    @Disabled
    @DisplayName("Testing of coastline detection")
    void noCoastlinesTest() {
        assertFalse(canvasModel.noCoastlines());
        model.load("testResources/load/smallMap.osm");
        assertTrue(canvasModel.noCoastlines()); //There is always 7 coastlines?
    }
}