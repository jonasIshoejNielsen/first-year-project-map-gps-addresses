package model.helpers.parsers;

import model.helpers.AddressSearch.HousePlacement;
import model.osm.OSMAddress;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddressParser {
    private HashMap<String, OSMAddress[]> postMap;
    private List<OSMAddress> waitlist;
    private HashMap<String, OSMAddress> addresses;

    public AddressParser() {
        postMap         = new HashMap<>();
        waitlist        = new ArrayList<>();
        addresses = new HashMap<>();
    }


    public void parseAdress(XMLStreamReader stream, float lat, float lon) throws XMLStreamException {
        String city = null;
        String housenumber = null;
        String postcode = null;
        String street = null;

        while (stream.hasNext()) {
            if (stream.isStartElement()) {
                switch (stream.getLocalName()) {
                    case "tag":
                        switch (stream.getAttributeValue(0)) {
                            case "addr:city":
                                city = stream.getAttributeValue(1).intern();
                                break;

                            case "addr:housenumber":
                                housenumber = stream.getAttributeValue(1).intern();
                                break;

                            case "addr:postcode":
                                postcode = stream.getAttributeValue(1).intern();
                                break;

                            case "addr:street":
                                street = stream.getAttributeValue(1).intern();
                                break;

                            default:
                                break;
                        }
                    default:
                        break;
                }
            }
            if (stream.isEndElement()) {
                if (stream.getLocalName().equals("node")) {
                    if (street == null) return;

                    if (postcode == null) {
                        if (city == null) {
                            //Saving the addresses for finding postcode later
                            HousePlacement placement = new HousePlacement(housenumber, lon, lat);
                            waitlist.add(new OSMAddress(null, placement, null, street));
                            return;
                        }
                        postcode = findCityOrPostcode(city, false);

                    } else if (city == null) {
                        city = findCityOrPostcode(postcode, true);
                    }

                    if(postcode == null || city == null) return;

                    HousePlacement placement = new HousePlacement(housenumber, lon, lat);
                    saveAddress(new OSMAddress(city, placement, postcode, street));
                    return;
                }
            }
            stream.next();
        }
    }

    private void saveAddress(OSMAddress address) {
        if(address.getHousePlacement().getHouseNumber() == null) return;
        if(!isAddressSaved(address)) {
            addAddressToPostMap(address);
            addresses.put(buildKey(address), address);
        } else {
            addHousePlacement(address);
        }

    }

    private String buildKey(OSMAddress address) {
        StringBuilder builder = new StringBuilder();
        builder.append(address.getStreet());
        builder.append(address.getCity());
        return builder.toString();
    }

    private boolean isAddressSaved(OSMAddress addressCheck) {
        OSMAddress addressFound = addresses.get(buildKey(addressCheck));
        if(addressFound == null)  return false;
        if(addressFound.getCity() == null && addressFound.getPostCode() == null) return false;

        if(addressFound.getCity() == null || addressFound.getPostCode() == null) {
            if(addressFound.getCity() == null && addressFound.getPostCode().equals(addressCheck.getPostCode())) return true;
            if(addressFound.getPostCode() == null && addressFound.getCity().equals(addressCheck.getCity()))     return true;
            return false;
        }

        if(addressFound.getCity().equals(addressCheck.getCity())) {
            if(addressFound.getPostCode().equals(addressCheck.getPostCode()))   return true;
        }

        return false;
    }

    private void addHousePlacement(OSMAddress address) {
        addresses.get(buildKey(address)).addHousePlacement(address.getHousePlacement());
    }

    private void addAddressToPostMap(OSMAddress address) {
        OSMAddress[] addressList = postMap.get(address.getStreet());
        if (addressList == null) { //If the street is not in the map
            createNewList(address);
        } else {
            if(address.getPostCode() == null)   return;
            boolean found = findPostcodeInList(addressList, address.getPostCode());
            if (found) return;
            addPostcodeToList(addressList, address);
        }
    }

    private void createNewList(OSMAddress address) {
        OSMAddress[] addressList = new OSMAddress[]{address};
        postMap.put(address.getStreet(), addressList);
    }

    private boolean findPostcodeInList(OSMAddress[] addressList, String postcode) {
        for (OSMAddress address : addressList) {
            if (address.getPostCode().equals(postcode)) { //If this postcode is already in the map, stop!
                return true;
            }
        }
        return false;
    }

    private void addPostcodeToList(OSMAddress[] oldAddressList, OSMAddress address) {
        OSMAddress[] newAddressList = new OSMAddress[oldAddressList.length + 1];
        System.arraycopy(oldAddressList, 0, newAddressList, 0, oldAddressList.length);
        newAddressList[newAddressList.length - 1] = address;
        postMap.put(address.getStreet(), newAddressList);
    }

    private String findCityOrPostcode(String searchName, boolean findCity) {
        String result = null;
        try {
            InputStream inputStream = AddressParser.class.getResourceAsStream("/postcodeToCity.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

            //Going through the list and looks for a city to match the postcode.
            String line;
            while ((line = reader.readLine()) != null) {
                String[] split = line.split(" ", 2);

                result=(findCity)? matchOneReturnOther(searchName, split[0],split[1]) : matchOneReturnOther(searchName, split[1],split[0]);
                if(result!=null) break;
            }

            reader.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private String matchOneReturnOther(String searchString, String stringOne, String stringOther){
        if (stringOne.equals(searchString)) return stringOther;
        return null;
    }

    public void resolveAddressWaitlist() {
        runWaitlist();
        waitlist.clear();
    }

    private void runWaitlist() {
        for (OSMAddress address : waitlist) {
            OSMAddress[] addressList = postMap.get(address.getStreet());
            if (addressList == null) {
                continue;
            }

            float lon = address.getHousePlacement().getLon();
            float lat = address.getHousePlacement().getLat();

            Point2D addressPoint = new Point2D.Float(lon, lat);
            float bestDistance = Float.MAX_VALUE;
            String bestPostcode = null;

            for (OSMAddress addr : addressList) { //Find the point, that is closest to waitlist point:
                float addrLon = addr.getHousePlacement().getLon();
                float addrLat = addr.getHousePlacement().getLat();

                Point2D mapPoint = new Point2D.Float(addrLon, addrLat);
                float distance = Math.abs((float) mapPoint.distance(addressPoint));

                if (distance < bestDistance) {
                    bestDistance = distance;
                    bestPostcode = addr.getPostCode();
                }
            }

            address.setPostCode(bestPostcode);
            address.setCity(findCityOrPostcode(bestPostcode, true));

            saveAddress(address);
        }
    }

    public List<OSMAddress> getAddresses() {
        return new ArrayList<>(addresses.values());
    }
}
