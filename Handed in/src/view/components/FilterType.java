package view.components;

import model.osm.OSMType;
import model.osm.OSMTypeParent;
import java.util.ArrayList;

public enum FilterType {
    // OSM Parents
    HIGHWAY         ("Veje", "highway.png",         OSMTypeParent.HIGHWAY.getChildren(),    true),
    BUILDING        ("Bygning", "building.png",     OSMTypeParent.BUILDING.getChildren(),   true),
    NATURAL         ("Natur", "natural.png",        OSMTypeParent.NATURAL.getChildren(),    true),
    AEROWAY	    	("Luftfart", "aeroway.png", 	OSMTypeParent.AEROWAY.getChildren(), 	true),
    AMENITY	    	("Fasiliteter", "amenity.png", 	OSMTypeParent.AMENITY.getChildren(), 	true),
    BARRIER	    	("Barriere", "barrier.png", 	OSMTypeParent.BARRIER.getChildren(), 	true),
    BOUNDARY	    ("Grænser", "boundary.png", 	OSMTypeParent.BOUNDARY.getChildren(), 	true),
    CRAFT	    	("Håndværk", "craft.png", 		OSMTypeParent.CRAFT.getChildren(), 		true),
    HISTORIC	    ("Historisk", "historic.png", 	OSMTypeParent.HISTORIC.getChildren(), 	true),
    LANDUSE	    	("Områder", "landuse.png", 		OSMTypeParent.LANDUSE.getChildren(), 	true),
    LEISURE	    	("Fritidsområde", "leisure.png",OSMTypeParent.LEISURE.getChildren(), 	true),
    MAN_MADE	    ("Menneskeskabt", "man_made.png",OSMTypeParent.MAN_MADE.getChildren(), 	true),
    MILITARY	    ("Militær", "military.png",     OSMTypeParent.MILITARY.getChildren(), 	true),
    PLACE	    	("Boligområde", "place.png", 	OSMTypeParent.PLACE.getChildren(), 		true),
    RAILWAY	    	("Togbane", "railway.png", 		OSMTypeParent.RAILWAY.getChildren(), 	true),
    WATERWAY	    ("Søvej", "waterway.png", 		OSMTypeParent.WATERWAY.getChildren(), 	true),

    // OSM Children
    PARK	                ("Park", "park.png", 		    				OSMType.PARK, 				true),
    WATER_NA                ("Vand", "water_na.png", 		    			OSMType.WATER_NA, 			true),
    CONSTRUCTION_LA         ("Byggeplads", "construction_la.png",		    OSMType.CONSTRUCTION_LA,	true),
    RESIDENTIAL_LA          ("Bolig", "residential_la.png",		            OSMType.RESIDENTIAL_LA,		true),
    DAM                     ("Dam", "dam.png",								OSMType.DAM,			  	true),
    DOCK                    ("Havnemole", "dock.png",						OSMType.DOCK,			  	true),
    FARMS                   ("Farm", "farms.png",							OSMType.FARMS,			  	true),
    LAKE                    ("Sø", "lake.png",							    OSMType.LAKE,			  	true),
    AMBULANCE_STATION       ("Ambulance Station", "ambulance_station.png",	OSMType.AMBULANCE_STATION, 	true),
    ARTS_CENTRE             ("Kunst", "arts_centre.png",				    OSMType.ARTS_CENTRE,		true),
    BANK_AM                 ("Bank", "bank_am.png",						    OSMType.BANK_AM,			true),
    BAR                     ("Bar", "bar.png",								OSMType.BAR,			  	true),
    CAFE                    ("Café", "cafe.png",							OSMType.CAFE,			  	true),
    CAR_WASH                ("Bil vask", "car_wash.png",					OSMType.CAR_WASH,			true),
    CINEMA                  ("Biograf", "cinema.png",						OSMType.CINEMA,			  	true),
    COLLEGE                 ("Gymnasium", "college.png",					OSMType.COLLEGE,			true),
    COURTHOUSE              ("Retssal", "courthouse.png",				    OSMType.COURTHOUSE,			true),
    DENTIST                 ("Tandlæge", "dentist.png",						OSMType.DENTIST,			true),
    DOCTORS                 ("Læge", "doctors.png",						    OSMType.DOCTORS,			true),
    FAST_FOOD               ("Fast food", "fast_food.png",					OSMType.FAST_FOOD,			true),
    GAMBLING                ("Gambling", "gambling.png",					OSMType.GAMBLING,			true),
    GRAVE_YARD              ("Kirkegård", "grave_yard.png",				    OSMType.GRAVE_YARD,			true),
    HOSPITAL_AM             ("Hospital", "hospital_am.png",				    OSMType.HOSPITAL_AM,		true),
    LIBRARY                 ("Biblotek", "library.png",						OSMType.LIBRARY,			true),
    PHARMACY                ("apotek", "pharmacy.png",					    OSMType.PHARMACY,			true),
    POLICE                  ("Politi", "police.png",						OSMType.POLICE,			  	true),
    POST_OFFICE             ("Post Kontor", "post_office.png",				OSMType.POST_OFFICE,		true),
    PRISON                  ("Fængsel", "prison.png",						OSMType.PRISON,			  	true),
    RESTAURANT              ("Restaurant", "restaurant.png",				OSMType.RESTAURANT,			true),
    SWIMMING_POOL           ("Swimming pool", "swimming_pool.png",			OSMType.SWIMMING_POOL,		true),
    THEATRE                 ("Teater", "theatre.png",						OSMType.THEATRE,			true),
    TOILETS                 ("Toiletter", "toilets.png",					OSMType.TOILETS,			true);


    String text;
    String iconPath;
    boolean isVisible;
    ArrayList<OSMType> children;

    //OSM Parants
    FilterType(String text, String iconPath, ArrayList<OSMType> children, boolean isVisible) {
        this.text       = text;
        this.iconPath   = iconPath;
        this.isVisible  = isVisible;
        this.children   = children;
    }

    // OSM Children
    FilterType(String text, String iconPath, OSMType child, boolean isVisible) {
        this.text       = text;
        this.iconPath   = iconPath;
        this.isVisible  = isVisible;
        this.children = new ArrayList<>();
        children.add(child);
    }

    public ArrayList<OSMType> getChildren() {
        return children;
    }

    public String getText() {
        return text;
    }

    public String getIconPath() {
        return iconPath;
    }

    public boolean isVisible() {
        return isVisible;
    }

}
