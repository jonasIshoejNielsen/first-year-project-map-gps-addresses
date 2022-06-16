package model.osm;

import model.helpers.AddressSearch.HousePlacement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class OSMAddress implements Serializable {

    private String city, postCode, street;
    private List<HousePlacement> housePlacements;
    private String fullAddress;
    private boolean isSorted;

    public OSMAddress(String city, HousePlacement placement, String postcode, String street) {
        housePlacements = new ArrayList<>();
        housePlacements.add(placement);

        this.city = city;
        this.postCode = postcode;
        this.street = street;
        this.isSorted = false;
        buildFullAddress();
    }

    public OSMAddress(OSMAddress address, HousePlacement place) {
        this.city = address.getCity();
        this.postCode = address.getPostCode();
        this.street = address.getStreet();

        housePlacements = new ArrayList<>();
        housePlacements.add(place);
        buildFullAddress();
    }

    public OSMAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public OSMAddress(String city, String houseNumber, String postcode, String street, float lon, float lat) {
        this.city = city;
        this.postCode = postcode;
        this.street = street;

        housePlacements = new ArrayList<>();
        HousePlacement placement = new HousePlacement(houseNumber, lon, lat);
        housePlacements.add(placement);
        buildFullAddress();
    }

    public String getCity() {
        return city;
    }

    public List<HousePlacement> getHousePlacements() {
        return housePlacements;
    }

    public HousePlacement getHousePlacement() {
        return housePlacements.get(0);
    }

    public HousePlacement getHousePlacement(int index) {
        return housePlacements.get(index);
    }

    public String getPostCode() {
        return postCode;
    }

    public String getStreet() {
        return street;
    }

    public String toString() {
        return fullAddress;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void addHousePlacement(HousePlacement placement) {
        housePlacements.add(placement);
    }

    public void sortHousePlacements() {
        if(isSorted)    return;
        housePlacements.sort((placeSource, placeToCompare) -> {
            if (placeSource.getHouseNumber() != null && placeToCompare.getHouseNumber() != null) {
                int length1 = placeSource.getHouseNumber().length();
                int length2 = placeToCompare.getHouseNumber().length();

                if(placeSource.getHouseNumber().matches("[0-9]+[A-Za-zæøåÆØÅ]")) length1 -= 1;
                if(placeToCompare.getHouseNumber().matches("[0-9]+[A-Za-zæøåÆØÅ]")) length2 -= 1;

                if (length1 > length2) return 1;
                if (length1 < length2) return -1;
                if (placeSource.getHouseNumber().compareToIgnoreCase(placeToCompare.getHouseNumber()) < 0) return -1;
                if (placeSource.getHouseNumber().compareToIgnoreCase(placeToCompare.getHouseNumber()) > 0) return 1; }
                return 0;
            });

        isSorted = true;
    }

    private void buildFullAddress() {
        StringBuilder sb = new StringBuilder();
        if(this.street != null) {
            sb.append(this.street);
            sb.append(", ");
        }
        if(this.postCode != null) {
            sb.append(this.postCode);
            sb.append(" ");
        }
        if(this.city != null) {
            sb.append(this.city);
            sb.append(" ");
        }
        this.fullAddress = sb.toString().toLowerCase();
    }
}
