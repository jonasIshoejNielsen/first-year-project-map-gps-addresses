package model.helpers.AddressSearch;

import model.osm.OSMAddress;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchListBuilder {
    private HashMap<SearchID, List<OSMAddress>> searchLists;
    private List<OSMAddress> addresses;

    public SearchListBuilder(List<OSMAddress> addresses) {
        searchLists = new HashMap<>();
        this.addresses = addresses;
        sortAddressLists();
    }

    private void sortAddressLists() {
        findCenter(SearchID.POSTCODE);
        findCenter(SearchID.CITY);
        addresses.sort((addressSource, addressToCompare) -> compareAddresses(addressSource, addressToCompare));
        searchLists.put(SearchID.ADDRESS, addresses);
    }

    private void findCenter(SearchID id) {
        sortLists(id);

        List<OSMAddress> results = new ArrayList<>();
        List<OSMAddress> matches = new ArrayList<>();
        OSMAddress temp = addresses.get(0);

        String value = (id == SearchID.POSTCODE) ? temp.getPostCode() : temp.getCity();
        for(OSMAddress address : addresses) {
            if(id == SearchID.POSTCODE && address.getPostCode() == null)  continue;
            if(id == SearchID.CITY && address.getCity() == null)          continue;

            if(id == SearchID.POSTCODE && value.equals(address.getPostCode())) {
                matches.add(address);
            } else if (id == SearchID.CITY && value.equals(address.getCity())) {
                matches.add(address);
            } else if (!matches.isEmpty()) {
                results.add(calculateCenter(matches));
                matches.clear();
                matches.add(address);
                value = (id == SearchID.POSTCODE) ? address.getPostCode() : address.getCity();
            }
        }

        if(!matches.isEmpty()) {
            results.add(calculateCenter(matches));
            matches.clear();
        }

        addListToSearchLists(results, id);
    }

    void sortLists(SearchID id) {
        switch (id) {
            case POSTCODE:
                addresses.sort((source, compareTo) -> source.getPostCode().compareToIgnoreCase(compareTo.getPostCode()));
                break;

            case CITY:
                addresses.sort(((source, compareTo) -> source.getCity().compareToIgnoreCase(compareTo.getCity())));
                break;

            default:
                throw new RuntimeException("Illigal SearchID in findCenter");
        }
    }

    OSMAddress calculateCenter(List<OSMAddress> collection) {
        float lat = 0, lon = 0;
        String postCode = collection.get(0).getPostCode();
        String city = collection.get(0).getCity();
        int numberOfHouses = 0;

        for(OSMAddress address : collection) {
            for(HousePlacement placement : address.getHousePlacements()) {
                lat += placement.getLat();
                lon += placement.getLon();
                numberOfHouses++;
            }
        }

        lat /= numberOfHouses;
        lon /= numberOfHouses;
        return new OSMAddress(city, null, postCode, null, lon, lat);
    }

    private void addListToSearchLists(List<OSMAddress> results, SearchID id) {
        switch (id) {
            case POSTCODE:
                results.sort((postcodeSource, postcodeToCompare) -> postcodeSource.getPostCode().compareToIgnoreCase(postcodeToCompare.getPostCode()));
                searchLists.put(SearchID.POSTCODE, results);
                break;

            case CITY:
                results.sort((citySource, cityToCompare) -> citySource.getCity().compareToIgnoreCase(cityToCompare.getCity()));
                searchLists.put(SearchID.CITY, results);
                break;
        }
    }

    int compareAddresses(OSMAddress addressSource, OSMAddress addressToCompare) {
        if (addressSource.getStreet() != null && addressToCompare.getStreet() != null) {
            if (addressSource.getStreet().compareToIgnoreCase(addressToCompare.getStreet()) < 0) return -1;
            if (addressSource.getStreet().compareToIgnoreCase(addressToCompare.getStreet()) > 0) return 1;
        }
        if (addressSource.getPostCode() != null && addressToCompare.getPostCode() != null) {
            if (addressSource.getPostCode().compareToIgnoreCase(addressToCompare.getPostCode()) < 0) return -1;
            if (addressSource.getPostCode().compareToIgnoreCase(addressToCompare.getPostCode()) > 0) return 1;
        }
        if (addressSource.getCity() != null && addressToCompare.getCity() != null) {
            if (addressSource.getCity().compareToIgnoreCase(addressToCompare.getCity()) < 0) return -1;
            if (addressSource.getCity().compareToIgnoreCase(addressToCompare.getCity()) > 0) return 1;
        }
        return 0;
    }

    public HashMap<SearchID, List<OSMAddress>> getSearchList() {
        return searchLists;
    }
}
