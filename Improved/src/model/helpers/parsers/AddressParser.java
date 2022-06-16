package model.helpers.parsers;

import model.helpers.AddressSearch.HousePlacement;
import model.helpers.TernarySearchTries;
import model.osm.OSMAddress;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class AddressParser {
    private int numberOfAddresses = 0;
    private TernarySearchTries<OSMAddress> addresses;
    private TernarySearchTries<PostcodeAndCityCenter> postcodes;
    private TernarySearchTries<List<PostcodeAndCityCenter>> cities;
    private HashMap<String, List<Point2D>> postcodeToCoordinates;
    private HashMap<String, List<Point2D>> cityToCoordinates;


    public AddressParser() {
        addresses               = new TernarySearchTries<>();
        postcodes               = new TernarySearchTries<>();
        cities                  = new TernarySearchTries<>();

        postcodeToCoordinates   = new HashMap<>();
        cityToCoordinates       = new HashMap<>();
        loadPostcodesAndCity();
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

                    if(city == null && postcode == null) return;

                    if(postcode != null && city == null){
                        city = findCityOrPostcode(postcode, true);
                    }
                    else if(postcode == null && city != null){
                        postcode = findCityOrPostcode(city, false);
                    }
                    else {
                        if(!postcodeToCityMap.containsKey(postcode))
                            postcodeToCityMap.put(postcode, city);
                        if(!cityToPostcoeMap.containsKey(city))
                            postcodeToCityMap.put(city, postcode);
                    }
                    if(city == null || postcode == null) return;

                    addToHashMap(postcodeToCoordinates, postcode, new Point2D.Double(lon, lat));
                    addToHashMap(cityToCoordinates, city, new Point2D.Double(lon, lat));

                    HousePlacement placement = new HousePlacement(housenumber, lon, lat);
                    saveAddress(city, placement, street, postcode);
                    numberOfAddresses ++;
                    return;
                }
            }
            stream.next();
        }
    }

    private void addToHashMap(HashMap<String, List<Point2D>> hashMap, String key, Point2D.Double point) {
        List<Point2D> points = hashMap.get(key);
        if(points == null) points = new ArrayList<>();
        points.add(point);
        hashMap.put(key, points);
    }

    private void saveAddress(String city, HousePlacement housePlacement, String street, String postcode) {
        OSMAddress addressFound = addresses.get(street);
        if(addressFound == null || addressFound.getCity() == null) {
            addresses.put(street, new OSMAddress(city, housePlacement, postcode, street));
            return;
        }
        if(addressFound.getCity() == null) return;
        addressFound.addHousePlacement(housePlacement, postcode);

    }



    HashMap<String, String> postcodeToCityMap;
    HashMap<String, String> cityToPostcoeMap;

    private String findCityOrPostcode(String searchName, boolean findCity) {
        boolean test = postcodeToCityMap.containsKey(searchName);
        String val = postcodeToCityMap.get(searchName);
        Set<String> all = postcodeToCityMap.keySet();
        List<String> content = new ArrayList<>();
        content.addAll(all);
        Collections.sort(content);
        if(findCity) return postcodeToCityMap.get(searchName);
        return cityToPostcoeMap.get(searchName);

    }

    private void loadPostcodesAndCity(){
        postcodeToCityMap = new HashMap<>();
        cityToPostcoeMap = new HashMap<>();
        try {
            InputStream inputStream = AddressParser.class.getResourceAsStream("/postcodeToCity.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

            //Going through the list and looks for a city to match the postcode.
            String line;
            while ((line = reader.readLine()) != null) {
                String[] split = line.split(" ", 2);
                postcodeToCityMap.put(split[0].trim(), split[1].trim());
                cityToPostcoeMap.put(split[1].trim(), split[0].trim());
            }
            reader.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void resolveAddressWaitlist() {
        for (String currPostCode : postcodeToCoordinates.keySet()) {
            List<Point2D> points = postcodeToCoordinates.get(currPostCode);
            String cityForPostcode = findCityOrPostcode(currPostCode, true);
            if(points == null){
                System.out.println(currPostCode);
            }
            PostcodeAndCityCenter postcodeAndCityCenter = new PostcodeAndCityCenter(currPostCode, cityForPostcode, points);
            postcodes.put(currPostCode, postcodeAndCityCenter);

            if(cityForPostcode == null){
                cityForPostcode = findCityOrPostcode(currPostCode, true);
            }

            List<PostcodeAndCityCenter> citiesForPostcode = cities.get(cityForPostcode);
            if(citiesForPostcode == null) citiesForPostcode = new ArrayList<>();
            citiesForPostcode.add(postcodeAndCityCenter);
            cities.put(cityForPostcode, citiesForPostcode);
        }
    }

    public TernarySearchTries<OSMAddress> getAddresses() {
        return addresses;
    }
    public TernarySearchTries<PostcodeAndCityCenter> getPostcodes() {
        return postcodes;
    }
    public TernarySearchTries<List<PostcodeAndCityCenter>> getCities() {
        return cities;
    }

    public int numberOfAddresses() {
        return numberOfAddresses;
    }
}
