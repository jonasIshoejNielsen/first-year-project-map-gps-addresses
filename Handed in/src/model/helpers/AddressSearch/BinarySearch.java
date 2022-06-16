package model.helpers.AddressSearch;

import model.osm.OSMAddress;
import java.util.List;

public class BinarySearch {

    public int rank(List<OSMAddress> list, String key, SearchID id, List<Integer> indexes) {
        if(id == null || id == SearchID.HOUSENUMBER)    return -1;

        int lo = 0;
        int hi = list.size() - 1;
        while (lo <= hi) {
            int mid         = lo + (hi - lo) / 2;
            OSMAddress searchHit  = list.get(mid);

            if(id == SearchID.POSTCODE) {
                if(containsSearch(key, searchHit, id)) return mid;
                if      (key.compareTo(searchHit.getPostCode()) < 0) hi = mid - 1;
                else if (key.compareTo(searchHit.getPostCode()) > 0) lo = mid + 1;
                else return mid;
            } else if (id == SearchID.CITY) {
                if(!indexes.contains(mid) && containsSearch(key, searchHit, id)) return mid;
                if      (key.compareTo(searchHit.getCity().toLowerCase()) < 0) hi = mid - 1;
                else if (key.compareTo(searchHit.getCity().toLowerCase()) > 0) lo = mid + 1;
                else {
                    if(indexes.contains(mid)) return -1;
                    else return mid;
                }
            } else if (id == SearchID.ADDRESS) {
                if(!indexes.contains(mid) && containsSearch(key, searchHit, id)) return mid;
                if      (key.compareTo(searchHit.toString()) < 0) hi = mid - 1;
                else if (key.compareTo(searchHit.toString()) > 0) lo = mid + 1;
                else {
                    if(indexes.contains(mid)) return -1;
                    else return mid;
                }
            }
        }
        return -1;
    }

    private boolean containsSearch(String key, OSMAddress searchHit, SearchID id) {
        if(id == SearchID.ADDRESS)        return searchHit.getStreet().toLowerCase().trim().contains(key);
        else if (id == SearchID.CITY)     return searchHit.getCity().toLowerCase().trim().contains(key);
        else if (id == SearchID.POSTCODE) return searchHit.getPostCode().trim().startsWith(key);
        else                              return false;

    }
}