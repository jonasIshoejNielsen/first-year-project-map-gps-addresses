package model.helpers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoadTest {
    private Load load;

    @BeforeEach
    void setUp() {
        load = new Load();
    }

    @Test
    @DisplayName("Loading of a .tgm file / .bin file")
    void testLoadFilenameBin() {
        assertTrue(load.load("testResources/load/test.tgm"));
    }

    @Test
    @DisplayName("Loading of a .zip file")
    void testLoadFilenameZip() {
        assertTrue(load.load("testResources/load/test.zip"));
    }

    @Test
    @DisplayName("Loading of a .osm file")
    void testLoadFilenameOSM() {
        assertTrue(load.load("testResources/load/test.osm"));
    }

    @Test
    @DisplayName("Loading of an unsupported file")
    void testUnsupportedLoad() {
        assertFalse(load.load("testResources/load/test.txt"));
    }

}