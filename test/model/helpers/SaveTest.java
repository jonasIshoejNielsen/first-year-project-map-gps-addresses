package model.helpers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SaveTest {
    private Save save;

    @BeforeEach
    void setUp() {
        save = new Save();
        Load load = new Load();
        load.loadMapFromResource();
    }

    @Test
    @DisplayName("Test of default save operation for colors")
    void testSaveTMC() {
        assertTrue(save.saveTMCFile("testColorSave"));
    }
}