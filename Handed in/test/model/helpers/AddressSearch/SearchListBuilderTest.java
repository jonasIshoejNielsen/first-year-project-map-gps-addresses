package model.helpers.AddressSearch;

import model.osm.OSMAddress;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SearchListBuilderTest {
    private SearchListBuilder searchListBuilder;
    private List<OSMAddress> testAddresses;

    @BeforeAll
    void setUp() {
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
    @DisplayName("Testing of list sorting of Postcodes")
    void testListSortingPostcode() {
        searchListBuilder = new SearchListBuilder(testAddresses);
        searchListBuilder.sortLists(SearchID.POSTCODE);
        String postcode = "";
        for(OSMAddress address : testAddresses) {
            assertTrue(address.getPostCode().compareTo(postcode) >= 0);
            postcode = address.getPostCode();
        }
    }

    @Test
    @DisplayName("Testing of list sorting of cities")
    void testListSortingCities() {
        searchListBuilder = new SearchListBuilder(testAddresses);
        searchListBuilder.sortLists(SearchID.CITY);
        String city = "";
        for(OSMAddress address : testAddresses) {
            assertTrue(address.getCity().compareTo(city) >= 0);
            city = address.getCity();
        }
    }

    @Test
    @DisplayName("Testing of list sorting with wrong id")
    void testFalseListSorting() {
        searchListBuilder = new SearchListBuilder(testAddresses);
        assertThrows(RuntimeException.class, () -> searchListBuilder.sortLists(SearchID.ADDRESS));
        assertThrows(RuntimeException.class, () -> searchListBuilder.sortLists(SearchID.HOUSENUMBER));
        assertThrows(RuntimeException.class, () -> searchListBuilder.sortLists(null));
    }

    @Test
    @DisplayName("Testing the calculation of city center")
    void testFindCityCenter() {
        List<OSMAddress> testAddresses = new ArrayList<>();
        testAddresses.add(new OSMAddress("Test", null, "1234", null, 10, 10));
        testAddresses.add(new OSMAddress("Test", null, "1234", null, 20, -20));
        testAddresses.add(new OSMAddress("Test", null, "1234", null, 15, 20));
        testAddresses.add(new OSMAddress("Test", null, "1234", null, -20, 30));

        searchListBuilder = new SearchListBuilder(testAddresses);

        OSMAddress testFinal = searchListBuilder.calculateCenter(testAddresses);
        assertEquals(6.25f, testFinal.getHousePlacement().getLon());
        assertEquals(10.0f, testFinal.getHousePlacement().getLat());
    }

    @Test
    @DisplayName("Testing city center calculation of single address")
    void testFindSingleCityCenter() {
        List<OSMAddress> testAddresses = new ArrayList<>();
        testAddresses.add(new OSMAddress("Test", null, "1234", null, 10, -10));

        searchListBuilder = new SearchListBuilder(testAddresses);

        OSMAddress testFinal = searchListBuilder.calculateCenter(testAddresses);
        assertEquals(10f, testFinal.getHousePlacement().getLon());
        assertEquals(-10f, testFinal.getHousePlacement().getLat());
    }

    @Test
    @DisplayName("Testing of failing city center calculations")
    void testFailedFindCityCenter() {
        searchListBuilder = new SearchListBuilder(testAddresses);
        assertThrows(NullPointerException.class, () -> searchListBuilder.calculateCenter(null));
        assertThrows(IndexOutOfBoundsException.class, () -> searchListBuilder.calculateCenter(new ArrayList<>()));
    }

    @Test
    @DisplayName("Test of the address compare method")
    void testAddressCompare() {
        searchListBuilder = new SearchListBuilder(testAddresses);
        assertEquals(-1, searchListBuilder.compareAddresses(testAddresses.get(0), testAddresses.get(1)));
        assertEquals(1, searchListBuilder.compareAddresses(testAddresses.get(1), testAddresses.get(0)));
        assertEquals(-1, searchListBuilder.compareAddresses(testAddresses.get(1), testAddresses.get(5)));
        assertEquals(-1, searchListBuilder.compareAddresses(testAddresses.get(1), testAddresses.get(2)));
        assertEquals(1, searchListBuilder.compareAddresses(testAddresses.get(2), testAddresses.get(1)));
        assertEquals(-1, searchListBuilder.compareAddresses(testAddresses.get(2), testAddresses.get(5)));
        assertEquals(0, searchListBuilder.compareAddresses(testAddresses.get(5), testAddresses.get(6)));
        assertEquals(0, searchListBuilder.compareAddresses(testAddresses.get(6), testAddresses.get(5)));
    }

    @Test
    @DisplayName("Test of false input for the compare method")
    void testAddressCompareNegative() {
        searchListBuilder = new SearchListBuilder(testAddresses);
        assertThrows(NullPointerException.class, () -> searchListBuilder.compareAddresses(null, null));
        assertThrows(NullPointerException.class, () -> searchListBuilder.compareAddresses(testAddresses.get(0), null));
        assertThrows(NullPointerException.class, () -> searchListBuilder.compareAddresses(null, testAddresses.get(4)));

        OSMAddress testAddress = new OSMAddress("Test", "4", "1234", null, 0, 0);
        OSMAddress compareAddress = new OSMAddress("Hagebro", "5", "2345", "Havnegade", 0, 0);
        assertEquals(-1, searchListBuilder.compareAddresses(testAddress, compareAddress));

        testAddress = new OSMAddress("A", "1", null, null, 0, 0);
        assertEquals(-1, searchListBuilder.compareAddresses(testAddress, compareAddress));
        assertEquals(1, searchListBuilder.compareAddresses(compareAddress, testAddress));
    }

}