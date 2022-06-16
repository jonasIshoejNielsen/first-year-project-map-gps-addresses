package model.helpers.AddressSearch;

import model.osm.OSMAddress;

public class SearchResult {
    private OSMAddress address;
    private String postcode;
    private SearchID id;

    public SearchResult(OSMAddress address, String postcode, SearchID id) {
        this.address = address;
        this.postcode = postcode;
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
                sb.append(postcode).append(" ");
                sb.append(address.getCity());
                break;

            case POSTCODE:
                sb.append(postcode).append(" ");
                sb.append(address.getCity());
                break;

            case CITY:
                sb.append(address.getCity()).append(" ");
                sb.append(postcode);
                break;

            case HOUSENUMBER:
                sb.append(address.getStreet()).append(" ");
                sb.append(address.getHousePlacement(postcode).getHouseNumber()).append(", ");
                sb.append(postcode).append(" ");
                sb.append(address.getCity());
                break;
        }
        return sb.toString();
    }

    public String getPostcode() {
        return postcode;
    }
}
