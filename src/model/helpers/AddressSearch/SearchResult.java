package model.helpers.AddressSearch;

import model.osm.OSMAddress;

public class SearchResult {
    private OSMAddress address;
    private SearchID id;

    public SearchResult(OSMAddress address, SearchID id) {
        this.address = address;
        this.id = id;
    }

    public OSMAddress getAddress() {
        return address;
    }

    public SearchID getId() {
        return id;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        switch (id) {
            case ADDRESS:
                sb.append(address.getStreet()).append(", ");
                sb.append(address.getPostCode()).append(" ");
                sb.append(address.getCity());
                break;

            case POSTCODE:
                sb.append(address.getPostCode()).append(" ");
                sb.append(address.getCity());
                break;

            case CITY:
                sb.append(address.getCity()).append(" ");
                sb.append(address.getPostCode());
                break;

            case HOUSENUMBER:
                sb.append(address.getStreet()).append(" ");
                sb.append(address.getHousePlacement().getHouseNumber()).append(", ");
                sb.append(address.getPostCode()).append(" ");
                sb.append(address.getCity());
                break;
        }
        return sb.toString();
    }
}
