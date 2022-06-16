package model;

import model.helpers.AddressSearch.SearchID;
import model.helpers.AddressSearch.SearchResult;
import model.osm.OSMAddress;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ModelTest {
    private Model model;
    private List<OSMAddress> testAddresses;

    @BeforeAll
    void setUp() {
        model = new Model();
        testAddresses = new ArrayList<>();
        fillTestAddressList();
    }

    private void fillTestAddressList() {
        testAddresses.add(new OSMAddress("København", "2", "1234", "gade", 0,0));
        testAddresses.add(new OSMAddress("Helsingør", "3B", "5623", "havnegade", 50,30));
        testAddresses.add(new OSMAddress("Struer", "978", "3276", "havnegade", 20,15));
        testAddresses.add(new OSMAddress("Hillerød", "15", "6277", "købmagergade", 786,20));
        testAddresses.add(new OSMAddress("Silkeborg", "65Q", "6820", "Lille havnegade", 50,90));
        testAddresses.add(new OSMAddress("Struer", "54", "3276", "Store havnegade", 45,95));
        testAddresses.add(new OSMAddress("Struer", "32", "3276", "Store havnegade", 44,94));
    }

    @Test
    @DisplayName("Test loading")
    void testLoad() {
        assertTrue(model.load("testResources/load/test.osm"));
        assertTrue(model.load("testResources/load/test.tgm"));
        assertTrue(model.load("testResources/load/test.zip"));
        assertTrue(model.load("testResources/load/test.tmc"));
        assertFalse(model.load("testResources/load/test.txt"));
        assertThrows(NullPointerException.class, () -> model.load(null));
    }

    @Test
    @DisplayName("Testing of default resources loading")
    void testDefaultLoad() {
        model.loadMapResource();
        model.loadThemeResource();
    }

    @Test
    @DisplayName("Testing that you can get addresses from the searchlist")
    void testAddressesFromSearchlist() {
        model.load("testResources/load/test.tgm");
        List<OSMAddress> testList = model.getAddresses();
        assertEquals(232, testList.size());
    }

    @Test
    @DisplayName("Testing of the error message system")
    void testErrorMessage() {
        model.loadMapResource();
        assertNull(model.getLoadErrorMessage());
        model.load("testResources/load/test.tgm");
        assertNull(model.getLoadErrorMessage());
        model.load("cake.cake");
        assertEquals("Filen kunne ikke findes.", model.getLoadErrorMessage());
    }

    @Test
    @DisplayName("Testing of the save method")
    void testSave() {
        assertTrue(model.saveTGMFile("saveTestTGM"));
        assertTrue(model.saveTMCFile("saveTestTMC"));
    }

    @Test
    @DisplayName("Testing of the postcode search")
    void postcodeSearchTest() {
        model.load("testResources/load/test.tgm");
        List<SearchResult> testList;
        testList = model.postcodeSearch("1050");
        assertEquals(6, testList.size());
        assertEquals("1050", testList.get(0).getAddress().getPostCode());
        assertEquals("1051", testList.get(1).getAddress().getPostCode());

        testList = model.postcodeSearch("117");
        assertEquals(6, testList.size());
        assertEquals("1170", testList.get(0).getAddress().getPostCode());
        assertEquals("1171", testList.get(1).getAddress().getPostCode());

        testList = model.postcodeSearch("999");
        assertEquals(0, testList.size());

        testList = model.postcodeSearch("112");
        assertEquals(0, testList.size());
    }

    @Test
    @DisplayName("Testing of city search")
    void citySearchTest() {
        model.load("testResources/load/test.tgm");
        List<SearchResult> testList;
        testList = model.search("køb", SearchID.CITY);
        assertEquals(2, testList.size());
        assertEquals("København", testList.get(0).getAddress().getCity());
        assertEquals("København K", testList.get(1).getAddress().getCity());

        testList = model.search("kobenhavn", SearchID.CITY);
        assertEquals(0, testList.size());

        testList = model.search("havn", SearchID.CITY);
        assertEquals(2, testList.size());
        assertEquals("København", testList.get(0).getAddress().getCity());
        assertEquals("København K", testList.get(1).getAddress().getCity());
    }

    @Test
    @DisplayName("Testing of address search")
    void addressSearchTest() {
        model.load("testResources/load/test.tgm");
        List<SearchResult> testList;
        testList = model.search("hav", SearchID.ADDRESS);
        assertEquals(1, testList.size());
        assertEquals("Havnegade", testList.get(0).getAddress().getStreet());

        testList = model.search("lille", SearchID.ADDRESS);
        assertEquals(4, testList.size());
        assertEquals("Lille Kirkestræde", testList.get(1).getAddress().getStreet());
        assertEquals("Lille Søndervoldstræde", testList.get(3).getAddress().getStreet());

        testList = model.search("gade", SearchID.ADDRESS);
        assertEquals(4, testList.size());
        assertEquals("H.C. Hansens Gade", testList.get(0).getAddress().getStreet());
        assertEquals("Hambrosgade", testList.get(3).getAddress().getStreet());
    }
}
