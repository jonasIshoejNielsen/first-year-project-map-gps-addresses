package model.osm;

import model.helpers.AddressSearch.HousePlacement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class OSMAddress implements Serializable {

    private String city, street;
    private HashMap<String,List<HousePlacement>> postcodeToHousePlacements;

    public OSMAddress(String city, HousePlacement placement, String postcode, String street) {
        setUp(city,placement,postcode, street);
    }

    public OSMAddress(String city, String houseNumber, String postcode, String street, float lon, float lat) {
        setUp(city, new HousePlacement(houseNumber, lon, lat), postcode, street);
    }

    private void setUp(String city, HousePlacement placement, String postcode, String street){
        List<HousePlacement> housePlacements = new ArrayList<>();
        housePlacements.add(placement);
        postcodeToHousePlacements = new HashMap<>();
        postcodeToHousePlacements.put(postcode, housePlacements);

        this.city = city;
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public List<HousePlacement> getHousePlacements(String postcode) {
        return postcodeToHousePlacements.get(postcode);
    }

    public HousePlacement getHousePlacement(String postcode) {
        List<HousePlacement> housePlacementList = postcodeToHousePlacements.get(postcode);
        if(housePlacementList == null) return null;
        return housePlacementList.get(0);
    }

    public HousePlacement getHousePlacement(String postcode, int index) {
        return postcodeToHousePlacements.get(postcode).get(index);
    }

    public String getStreet() {
        return street;
    }

    public void addHousePlacement(HousePlacement placement, String postcode) {
        List<HousePlacement> list = postcodeToHousePlacements.get(postcode);
        if(list == null) list = new ArrayList<>();
        list.add(placement);
        postcodeToHousePlacements.put(postcode, list);
    }

    public Set<String> getPostcodes() {
        return postcodeToHousePlacements.keySet();
    }
}
