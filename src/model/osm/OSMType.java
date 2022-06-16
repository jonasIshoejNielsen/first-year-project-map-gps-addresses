package model.osm;

import model.helpers.drawing.Strokes;

import java.awt.*;
import java.util.ArrayList;

public enum OSMType {
    //Unused types:
    UNKNOWN         (null, false, -1, false, false, false),

    //Toplevel drawing
    COASTLINE       (OSMTypeParent.NATURAL, true, Integer.MAX_VALUE,false, false, true),
    VILLAGE         (OSMTypeParent.PLACE, true, 9, true, false, true),
    CONSTRUCTION_LA (OSMTypeParent.LANDUSE, true, 12, true, false, true),
    RESIDENTIAL_LA  (OSMTypeParent.LANDUSE, true, 12, false, false, true),
    PARK            (OSMTypeParent.LEISURE, true, 8, false, false, true),
    WATER_NA        (OSMTypeParent.NATURAL, true, 13, false, false, true),

    CANAL           (OSMTypeParent.WATERWAY, true, 3, false, true, false, Strokes.NORMALSTROKE),
    DAM             (OSMTypeParent.WATERWAY, true, 4, false, false, true),
    DITCH_WA        (OSMTypeParent.WATERWAY, true, 4, false, true, false),
    DOCK            (OSMTypeParent.WATERWAY, true, 4, false, false, true),
    DRAIN           (OSMTypeParent.WATERWAY, true, 8, false, true, false),
    DRY_DOCK        (OSMTypeParent.WATERWAY, true, 4, false, false, true),
    FARMS           (OSMTypeParent.WATERWAY, true, 4, false, false, true),
    FISH_PASS       (OSMTypeParent.WATERWAY, true, 4, false, true, false),
    LAKE            (OSMTypeParent.WATERWAY, true, 3, false, false, true),
    POND_WA         (OSMTypeParent.WATERWAY, true, 4, false, true, false),
    RIVER           (OSMTypeParent.WATERWAY, true, 8, false, true, false),
    RIVERBANK       (OSMTypeParent.WATERWAY, true, 4, false, false, true),
    STREAM          (OSMTypeParent.WATERWAY, true, 8, false, true, false, Strokes.MEDIUMSTROKE),
    WATER           (OSMTypeParent.WATERWAY, true, 4, false, false, true),
    WATERFALL       (OSMTypeParent.WATERWAY, true, 4, false, false, false),
    WEIR            (OSMTypeParent.WATERWAY, true, 4, false, true, false),
    YES_WA          (OSMTypeParent.WATERWAY, true, 4, false, false, true),

    AERODROME       (OSMTypeParent.AEROWAY, true, 4, true, false, true),
    APRON           (OSMTypeParent.AEROWAY, true, 4, false, false, true),
    HANGAR_AE       (OSMTypeParent.AEROWAY, true, 4, false, false, true),
    HELIPAD         (OSMTypeParent.AEROWAY, true, 4, false, false, true),
    MARKING         (OSMTypeParent.AEROWAY, true, 4, false, false, true),
    PARKING_POSITION(OSMTypeParent.AEROWAY, true, 4, false, false, false),
    RUNWAY          (OSMTypeParent.AEROWAY, true, 4, false, true, false, Strokes.FATSTROKE),
    TAXIWAY         (OSMTypeParent.AEROWAY, true, 4, false, true, false, Strokes.MEDIUMSTROKE),
    TERMINAL_AE     (OSMTypeParent.AEROWAY, true, 8, false, true, true, Strokes.BORDERSTROKE),

    ADMINISTRATION  (OSMTypeParent.AMENITY, true, 4, true, false, false),
    AMBULANCE_STATION(OSMTypeParent.AMENITY, true, 4, true, false, false),
    ANIMAL_BOARDING (OSMTypeParent.AMENITY, true, 4, true, false, false),
    ANIMAL_BREEDING (OSMTypeParent.AMENITY, true, 4, true, false, false),
    ANIMAL_SHELTER  (OSMTypeParent.AMENITY, true, 4, true, false, false),
    ANIMAL_TRAINING_AM(OSMTypeParent.AMENITY, true, 4, true, false, false),
    ARTS_CENTRE     (OSMTypeParent.AMENITY, true, 4, true, false, false),
    BANK_AM         (OSMTypeParent.AMENITY, true, 4, true, false, false),
    BAR             (OSMTypeParent.AMENITY, true, 4, true, false, false),
    BBQ             (OSMTypeParent.AMENITY, true, 4, true, false, false),
    BENCH           (OSMTypeParent.AMENITY, true, 4, true, false, false),
    BICYCLE_PARKING (OSMTypeParent.AMENITY, true, 4, true, false, false),
    BICYCLE_RENTAL  (OSMTypeParent.AMENITY, true, 4, true, false, false),
    BIK             (OSMTypeParent.AMENITY, true, 4, true, false, false),
    BING            (OSMTypeParent.AMENITY, true, 4, true, false, false),
    BOAT_STORAGE    (OSMTypeParent.AMENITY, true, 4, true, false, false),
    BREWERY_AM      (OSMTypeParent.AMENITY, true, 4, true, false, false),
    BUS_STATION     (OSMTypeParent.AMENITY, true, 4, true, false, false),
    CABINS          (OSMTypeParent.AMENITY, true, 4, true, false, false),
    CAFE            (OSMTypeParent.AMENITY, true, 4, true, false, false),
    CANOE_HIRE      (OSMTypeParent.AMENITY, true, 4, true, false, false),
    CAR_RENTAL      (OSMTypeParent.AMENITY, true, 4, true, false, false),
    CAR_SHARING     (OSMTypeParent.AMENITY, true, 4, true, false, false),
    CAR_WASH        (OSMTypeParent.AMENITY, true, 4, true, false, false),
    CHARGING_STATION(OSMTypeParent.AMENITY, true, 4, true, false, false),
    CHILDCARE       (OSMTypeParent.AMENITY, true, 4, true, false, false),
    CINEMA          (OSMTypeParent.AMENITY, true, 4, true, false, false),
    CLINIC          (OSMTypeParent.AMENITY, true, 4, true, false, false),
    CLUB            (OSMTypeParent.AMENITY, true, 4, true, false, false),
    CLUBHOUSE       (OSMTypeParent.AMENITY, true, 4, true, false, false),
    COAST_RADAR_STATION(OSMTypeParent.AMENITY, true, 4, true, false, false),
    COLLEGE         (OSMTypeParent.AMENITY, true, 4, true, false, false),
    COMMUNITY_CENTRE(OSMTypeParent.AMENITY, true, 4, true, false, false),
    COMPOSTING_SITE (OSMTypeParent.AMENITY, true, 4, true, false, false),
    CONCERT_HALL    (OSMTypeParent.AMENITY, true, 4, true, false, false),
    CONFERENCE_CENTRE(OSMTypeParent.AMENITY, true, 4, true, false, false),
    CONSTRUCTION_AM (OSMTypeParent.AMENITY, true, 4, true, false, false),
    COURTHOUSE      (OSMTypeParent.AMENITY, true, 4, true, false, false),
    COWORKING_SPACE (OSMTypeParent.AMENITY, true, 4, true, false, false),
    CREMATORIUM     (OSMTypeParent.AMENITY, true, 4, true, false, false),
    CRYPT           (OSMTypeParent.AMENITY, true, 4, true, false, false),
    DANCING_SCHOOL  (OSMTypeParent.AMENITY, true, 4, true, false, false),
    DAYCARE         (OSMTypeParent.AMENITY, true, 4, true, false, false),
    DENTIST         (OSMTypeParent.AMENITY, true, 4, true, false, false),
    DIVE_CENTRE     (OSMTypeParent.AMENITY, true, 4, true, false, false),
    DOCTORS         (OSMTypeParent.AMENITY, true, 4, true, false, false),
    DOG_TRAINING    (OSMTypeParent.AMENITY, true, 4, true, false, false),
    DRINKING_WATER  (OSMTypeParent.AMENITY, true, 4, true, false, false),
    DRIVING_SCHOOL  (OSMTypeParent.AMENITY, true, 4, true, false, false),
    EMBASSY         (OSMTypeParent.AMENITY, true, 4, true, false, false),
    EQUESTRIAN_FIELD(OSMTypeParent.AMENITY, true, 4, true, false, false),
    EVENTS_VENUE    (OSMTypeParent.AMENITY, true, 4, true, false, false),
    EXHIBITION_CENTRE(OSMTypeParent.AMENITY, true, 4, true, false, false),
    FAST_FOOD       (OSMTypeParent.AMENITY, true, 4, true, false, false),
    FERRY_TERMINAL  (OSMTypeParent.AMENITY, true, 4, true, false, false),
    FESTIVAL_GROUNDS(OSMTypeParent.AMENITY, true, 4, true, false, false),
    FIRE_STATION    (OSMTypeParent.AMENITY, true, 4, true, false, false),
    FLIGHT_SCHOOL   (OSMTypeParent.AMENITY, true, 4, true, false, false),
    FOUNTAIN        (OSMTypeParent.AMENITY, true, 4, true, false, false),
    FUEL_AM         (OSMTypeParent.AMENITY, true, 4, true, false, false),
    GAMBLING        (OSMTypeParent.AMENITY, true, 4, true, false, false),
    GRAVE_YARD      (OSMTypeParent.AMENITY, true, 4, true, false, false),
    HOSPICE         (OSMTypeParent.AMENITY, true, 4, true, false, false),
    HOSPITAL_AM     (OSMTypeParent.AMENITY, true, 4, true, false, false),
    ICE_CREAM_AM    (OSMTypeParent.AMENITY, true, 4, true, false, false),
    INTERNET_CAFE   (OSMTypeParent.AMENITY, true, 4, true, false, false),
    JOBCENTRE       (OSMTypeParent.AMENITY, true, 4, true, false, false),
    KINDERGARTEN_AM (OSMTypeParent.AMENITY, true, 4, true, false, false),
    LIBRARY         (OSMTypeParent.AMENITY, true, 4, true, false, false),
    MARKETPLACE     (OSMTypeParent.AMENITY, true, 4, true, false, false),
    MONASTERY_AM    (OSMTypeParent.AMENITY, true, 4, true, false, false),
    MORTUARY        (OSMTypeParent.AMENITY, true, 4, true, false, false),
    MOTORCYCLE_PARKING(OSMTypeParent.AMENITY, true, 4, true, false, false),
    MUSIC_SCHOOL    (OSMTypeParent.AMENITY, true, 4, true, false, false),
    MUSIC_VENUE     (OSMTypeParent.AMENITY, true, 4, true, false, false),
    NIGHTCLUB       (OSMTypeParent.AMENITY, true, 4, true, false, false),
    NURSING_HOME    (OSMTypeParent.AMENITY, true, 4, true, false, false),
    PARING          (OSMTypeParent.AMENITY, true, 4, true, false, false),
    PARKING_AM      (OSMTypeParent.AMENITY, true, 4, true, false, false),
    PARKING_ENTRANCE(OSMTypeParent.AMENITY, true, 4, true, false, false),
    PARKING_SPACE   (OSMTypeParent.AMENITY, true, 4, true, false, false),
    PARKING_SPACE_GROUP(OSMTypeParent.AMENITY, true, 4, true, false, false),
    PAVILION_AM     (OSMTypeParent.AMENITY, true, 4, true, false, false),
    PHARMACY        (OSMTypeParent.AMENITY, true, 4, true, false, false),
    PLACE_OF_WORSHIP(OSMTypeParent.AMENITY, true, 4, true, false, false),
    POLICE          (OSMTypeParent.AMENITY, true, 4, true, false, false),
    POST_BOX        (OSMTypeParent.AMENITY, true, 4, true, false, false),
    POST_OFFICE     (OSMTypeParent.AMENITY, true, 4, true, false, false),
    PRESCHOOL       (OSMTypeParent.AMENITY, true, 4, true, false, false),
    PRISON          (OSMTypeParent.AMENITY, true, 4, true, false, false),
    PUB             (OSMTypeParent.AMENITY, true, 4, true, false, false),
    PUBLIC_BATH     (OSMTypeParent.AMENITY, true, 4, true, false, false),
    PUBLIC_BUILDING (OSMTypeParent.AMENITY, true, 4, true, false, false),
    RECYCLING       (OSMTypeParent.AMENITY, true, 4, true, false, false),
    REFUGEE_HOUSING (OSMTypeParent.AMENITY, true, 4, true, false, false),
    REST_AREA_AM    (OSMTypeParent.AMENITY, true, 4, true, false, false),
    RESTAURANT      (OSMTypeParent.AMENITY, true, 4, true, false, false),
    RETIREMENT_HOME (OSMTypeParent.AMENITY, true, 4, true, false, false),
    RIDING_STABLES  (OSMTypeParent.AMENITY, true, 4, true, false, false),
    SCHOOL_AM       (OSMTypeParent.AMENITY, true, 4, true, false, false),
    SCIENCE_PARK    (OSMTypeParent.AMENITY, true, 4, true, false, false),
    SCOUT_CAMP      (OSMTypeParent.AMENITY, true, 4, true, false, false),
    SCOUT_HUT       (OSMTypeParent.AMENITY, true, 4, true, false, false),
    SCRAPYARD       (OSMTypeParent.AMENITY, true, 4, true, false, false),
    SEATS           (OSMTypeParent.AMENITY, true, 4, true, false, false),
    SHELTER         (OSMTypeParent.AMENITY, true, 4, true, false, false),
    SHOWER          (OSMTypeParent.AMENITY, true, 4, true, false, false),
    SOCIAL_CENTRE   (OSMTypeParent.AMENITY, true, 4, true, false, false),
    SOCIAL_FACILITY (OSMTypeParent.AMENITY, true, 4, true, false, false),
    SPORT           (OSMTypeParent.AMENITY, true, 4, true, false, false),
    SQUARE_AM       (OSMTypeParent.AMENITY, true, 4, true, false, false),
    STUDENT_ACCOMMODATION(OSMTypeParent.AMENITY, true, 4, true, false, false),
    STUDIO          (OSMTypeParent.AMENITY, true, 4, true, false, false),
    SWIMMING_POOL   (OSMTypeParent.AMENITY, true, 4, true, false, false),
    SWINGERCLUB     (OSMTypeParent.AMENITY, true, 4, true, false, false),
    TAXI            (OSMTypeParent.AMENITY, true, 4, true, false, false),
    THEATRE         (OSMTypeParent.AMENITY, true, 4, true, false, false),
    TOILETS         (OSMTypeParent.AMENITY, true, 4, true, false, false),
    TOWNHALL        (OSMTypeParent.AMENITY, true, 4, true, false, false),
    TRAINING        (OSMTypeParent.AMENITY, true, 4, true, false, false),
    UNIVERSITY_AM   (OSMTypeParent.AMENITY, true, 8, true, false, false),
    VEHICLE_INSPECTION(OSMTypeParent.AMENITY, true, 4, true, false, false),
    VETERINARY      (OSMTypeParent.AMENITY, true, 4, true, false, false),
    WASTE_BASKET    (OSMTypeParent.AMENITY, true, 4, true, false, false),
    WASTE_DISPOSAL  (OSMTypeParent.AMENITY, true, 4, true, false, false),
    WASTE_TRANSFER_STATION(OSMTypeParent.AMENITY, true, 4, true, false, false),
    WEIGHBRIDGE     (OSMTypeParent.AMENITY, true, 4, true, false, false),

    DITCH_BA        (OSMTypeParent.BARRIER, true, 4, false, true, false),
    EMBANKMENT_BA   (OSMTypeParent.BARRIER, true, 4, false, true, false),
    FENCE           (OSMTypeParent.BARRIER, true, 3, false, true, false, Strokes.LOWERSTROKE),
    GUARD_RAIL      (OSMTypeParent.BARRIER, true, 4, false, true, false),
    HEDGE           (OSMTypeParent.BARRIER, true, 8, false, true, false, Strokes.BORDERSTROKE),
    KERB            (OSMTypeParent.BARRIER, true, 4, false, true, false, Strokes.BORDERSTROKE),
    RETAINING_WALL  (OSMTypeParent.BARRIER, true, 4, false, true, false),
    WALL            (OSMTypeParent.BARRIER, true, 4, false, true, false, Strokes.LOWERSTROKE),
    YES_BA          (OSMTypeParent.BARRIER, true, 4, false, true, false, Strokes.LOWERSTROKE),

    ADMINISTRATIVE  (OSMTypeParent.BOUNDARY, true, 4, false, false, false, Strokes.DOTTEDSTROKE),
    PROTECTED_AREA  (OSMTypeParent.BOUNDARY, true, 4, false, true, false, Strokes.SMALLSTROKE),

    ARBORIST        (OSMTypeParent.CRAFT, true, 4, true, false, false),
    BEEKEEPER       (OSMTypeParent.CRAFT, true, 4, true, false, false),
    BLACKSMITH      (OSMTypeParent.CRAFT, true, 4, true, false, false),
    BOATBUILDER     (OSMTypeParent.CRAFT, true, 4, true, false, false),
    BREWERY_CR      (OSMTypeParent.CRAFT, true, 4, true, false, false),
    BRUSH_MAKER     (OSMTypeParent.CRAFT, true, 4, true, false, false),
    BUILDER         (OSMTypeParent.CRAFT, true, 4, true, false, false),
    CARPENTER       (OSMTypeParent.CRAFT, true, 4, true, false, false),
    CATERER         (OSMTypeParent.CRAFT, true, 4, true, false, false),
    CATERING        (OSMTypeParent.CRAFT, true, 4, true, false, false),
    CLOCKMAKER      (OSMTypeParent.CRAFT, true, 4, true, false, false),
    CONFECTIONERY_CR(OSMTypeParent.CRAFT, true, 4, true, false, false),
    DAIRY           (OSMTypeParent.CRAFT, true, 4, true, false, false),
    ELECTRICIAN     (OSMTypeParent.CRAFT, true, 4, true, false, false),
    ELECTRONICS_REPAIR(OSMTypeParent.CRAFT, true, 4, true, false, false),
    GARDENER        (OSMTypeParent.CRAFT, true, 4, true, false, false),
    HANDICRAFT      (OSMTypeParent.CRAFT, true, 4, true, false, false),
    HVAC            (OSMTypeParent.CRAFT, true, 4, true, false, false),
    JEWELLER        (OSMTypeParent.CRAFT, true, 4, true, false, false),
    LOCKSMITH_CR    (OSMTypeParent.CRAFT, true, 4, true, false, false),
    METAL_CONSTRUCTION(OSMTypeParent.CRAFT, true, 4, true, false, false),
    PHOTOGRAPHER    (OSMTypeParent.CRAFT, true, 4, true, false, false),
    PLUMBER         (OSMTypeParent.CRAFT, true, 4,true, false, false),
    POTTERY         (OSMTypeParent.CRAFT, true, 4,true, false, false),
    PRINTING        (OSMTypeParent.CRAFT, true, 4, true, false, false),
    ROOFER          (OSMTypeParent.CRAFT, true, 4, true, false, false),
    SAWMILL         (OSMTypeParent.CRAFT, true, 4, true, false, false),
    SCULPTOR        (OSMTypeParent.CRAFT, true, 4, true, false, false),
    STONEMASON      (OSMTypeParent.CRAFT, true, 4, true, false, false),

    FOREST          (OSMTypeParent.LANDUSE, true, 11, true, false, true),
    ARCHAEOLOGICAL_SITE(OSMTypeParent.HISTORIC, true, 4, true, true, true),
    GRAVE           (OSMTypeParent.HISTORIC, true, 4, true, false, false),
    HERITAGE        (OSMTypeParent.HISTORIC, true, 4, true, false, false),
    MEMORIAL        (OSMTypeParent.HISTORIC, true, 4, true, false, false),
    MONUMENT        (OSMTypeParent.HISTORIC, true, 4, true, false, false),
    TOMB            (OSMTypeParent.HISTORIC, true, 4, true, false, false),
    YES_HI          (OSMTypeParent.HISTORIC, true, 4, true, true, true),

    ALLOTMENTS_LA   (OSMTypeParent.LANDUSE, true, 8, false, false, true),
    ANIMAL_KEEPING  (OSMTypeParent.LANDUSE, true, 3, false, false, true),
    AQUACULTURE     (OSMTypeParent.LANDUSE, true, 3, false, false, true),
    BASIN           (OSMTypeParent.LANDUSE, true, 3, false, false, true),
    BROWNFIELD      (OSMTypeParent.LANDUSE, true, 3, false, false, true),
    CEMETERY        (OSMTypeParent.LANDUSE, true, 8, false, false, true),
    COAL_STORAGE    (OSMTypeParent.LANDUSE, true, 3, false, false, true),
    COMMERCIAL_LA   (OSMTypeParent.LANDUSE, true, 3, false, false, true),
    CONSERVATION    (OSMTypeParent.LANDUSE, true, 3, false, false, true),
    COURTYARD       (OSMTypeParent.LANDUSE, true, 11, false, false, true),
    DEPOT           (OSMTypeParent.LANDUSE, true, 3, false, false, true),
    DUNE            (OSMTypeParent.LANDUSE, true, 3, false, false, true),
    FARMLAND        (OSMTypeParent.LANDUSE, true, 6, false, false, true),
    FARMYARD        (OSMTypeParent.LANDUSE, true, 6, false, false, true),
    FERRY_TERMINAL_LA(OSMTypeParent.LANDUSE, true, 3, false, false, true),
    FISHFARM        (OSMTypeParent.LANDUSE, true, 3, false, false, true),
    GARDEN_LA       (OSMTypeParent.LANDUSE, true, 10, false, false, true),
    GRASS           (OSMTypeParent.LANDUSE, true, 9, false, false, true),
    GRASSLAND_LA    (OSMTypeParent.LANDUSE, true, 8, false, false, true),
    GREEN           (OSMTypeParent.LANDUSE, true, 8, false, false, true),
    GREENFIELD      (OSMTypeParent.LANDUSE, true, 10, false, false, true),
    GREENHOUSE_LA   (OSMTypeParent.LANDUSE, true, 3, false, false, true),
    GREENHOUSE_HORTICULTURE(OSMTypeParent.LANDUSE, true, 3, false, false, true),
    HARBOUR         (OSMTypeParent.LANDUSE, true, 11, false, false, true),
    HEATH_LA        (OSMTypeParent.LANDUSE, true, 8, false, false, true),
    INDUSTRIAL_LA   (OSMTypeParent.LANDUSE, false, 11, false, false, true),
    LANDFILL        (OSMTypeParent.LANDUSE, true, 8, false, false, true),
    MEADOW          (OSMTypeParent.LANDUSE, true, 10, false, false, true),
    ORCHARD         (OSMTypeParent.LANDUSE, true, 8, false, false, true),
    PASTURE         (OSMTypeParent.LANDUSE, true, 3, false, false, true),
    PLANT_NURSERY   (OSMTypeParent.LANDUSE, true, 8, false, false, true),
    QUARRY          (OSMTypeParent.LANDUSE, true, 3, false, false, true),
    RAILWAY_LA      (OSMTypeParent.LANDUSE, true, 12, false, false, true),
    RECREATION_GROUND_LA(OSMTypeParent.LANDUSE, true, 8, false, false, true),
    RELIGIOUS_LA    (OSMTypeParent.LANDUSE, true, 3, false, false, true),
    RETAIL_LA       (OSMTypeParent.LANDUSE, true, 3, false, false, true),
    SCHOOLYARD_LA   (OSMTypeParent.LANDUSE, true, 3, false, false, true),
    SHIPYARD        (OSMTypeParent.LANDUSE, true, 3, false, false, true),
    TRAFFIC_ISLAND  (OSMTypeParent.LANDUSE, true, 3, false, false, true),
    VILLAGE_GREEN   (OSMTypeParent.LANDUSE, true, 8, false, false, true),
    VINEYARD        (OSMTypeParent.LANDUSE, true, 8, false, false, true),
    WORKYARD        (OSMTypeParent.LANDUSE, true, 3, false, false, true),
    YES_LA          (OSMTypeParent.LANDUSE, true, 12, false, false, true),

    AMUSEMENT_ARCADE(OSMTypeParent.LEISURE, true, 4, true, false, true),
    ANIMAL_TRAINING_LE(OSMTypeParent.LEISURE, true, 4, true, false, false),
    BANDSTAND       (OSMTypeParent.LEISURE, true, 4, true, false, true),
    BEACH_LE        (OSMTypeParent.LEISURE, true, 8, true, false, true),
    BIRD_HIDE       (OSMTypeParent.LEISURE, true, 4, true, false, true),
    BOWLING_ALLEY   (OSMTypeParent.LEISURE, true, 4, true, false, true),
    CLUB_LE         (OSMTypeParent.LEISURE, true, 4, true, true, true),
    COMMON          (OSMTypeParent.LEISURE, true, 10, true, false, true),
    DANCE           (OSMTypeParent.LEISURE, true, 4, true, false, true),
    DISC_GOLF_COURSE(OSMTypeParent.LEISURE, true, 8, true, false, true),
    DOG_PARK        (OSMTypeParent.LEISURE, true, 8, true, false, true),
    EQUESTRIAN      (OSMTypeParent.LEISURE, true, 4, true, false, true),
    FAIRGROUND      (OSMTypeParent.LEISURE, true, 4, true, false, true),
    FISHING_LE      (OSMTypeParent.LEISURE, true, 4, true, false, true),
    FITNESS_CENTRE  (OSMTypeParent.LEISURE, true, 4, true, false, true),
    FITNESS_STATION (OSMTypeParent.LEISURE, true, 8, true, true, true, Strokes.BORDERSTROKE),
    GARDEN          (OSMTypeParent.LEISURE, true, 8, true, false, true),
    GOLF_COURSE     (OSMTypeParent.LEISURE, true, 8, true, false, true),
    HACKERSPACE     (OSMTypeParent.LEISURE, true, 4, true, false, true),
    HORSE_RIDING    (OSMTypeParent.LEISURE, true, 4, true, false, false),
    ICE_RINK        (OSMTypeParent.LEISURE, true, 4, true, false, true),
    MARINA          (OSMTypeParent.LEISURE, true, 4, true, true, false),
    MAZE            (OSMTypeParent.LEISURE, true, 4, true, true, false),
    MINIATURE_GOLF  (OSMTypeParent.LEISURE, true, 8, true, false, true),
    MINI_SOCCER     (OSMTypeParent.LEISURE, true, 4, true, false, true),
    NATURE_RESERVE  (OSMTypeParent.LEISURE, true, 8, false, true, false),
    OUTDOOR_SEATING (OSMTypeParent.LEISURE, true, 4, false, false, true),
    PAINTBALL       (OSMTypeParent.LEISURE, true, 4, false, false, true),
    PETANQUE        (OSMTypeParent.LEISURE, true, 4, true, false, true),
    PICNIC_AREA     (OSMTypeParent.LEISURE, true, 4, true, false, true),
    PITCH           (OSMTypeParent.LEISURE, true, 6, false, true, true, Strokes.BORDERSTROKE),
    PLAYGROUND      (OSMTypeParent.LEISURE, true, 8, true, true, true, Strokes.BORDERSTROKE),
    POOL            (OSMTypeParent.LEISURE, true, 4, true, true, true),
    RACETRACK       (OSMTypeParent.LEISURE, true, 4, true, false, false),
    RECREATION_GROUND_LE(OSMTypeParent.LEISURE, true, 8, true, false, true),
    SCHOOLYARD      (OSMTypeParent.LEISURE, true, 4, true, false, true),
    SCOUT           (OSMTypeParent.LEISURE, true, 4, true, false, true),
    SHOOTING_GROUND (OSMTypeParent.LEISURE, true, 4, true, false, true),
    SLIPWAY         (OSMTypeParent.LEISURE, true, 4, true, false, false),
    SOCCER          (OSMTypeParent.LEISURE, true, 4, true, false, true),
    SOCCER_GOLF     (OSMTypeParent.LEISURE, true, 4, true, false, true),
    SOCIAL_CLUB     (OSMTypeParent.LEISURE, true, 4, true, false, true),
    SPORTS_CENTRE   (OSMTypeParent.LEISURE, true, 4, true, true, true, Strokes.BORDERSTROKE),
    STADIUM_LE      (OSMTypeParent.LEISURE, true, 8, true, false, true),
    STAGE           (OSMTypeParent.LEISURE, true, 4, true, false, true),
    SUMMER_CAMP     (OSMTypeParent.LEISURE, true, 4, true, true, true),
    SWIMMING_AREA   (OSMTypeParent.LEISURE, true, 8, true, false, true),
    SWIMMING_POOL_LE(OSMTypeParent.LEISURE, true, 8, true, false, true),
    TRACK_LE        (OSMTypeParent.LEISURE, true, 4, false, false, false),
    WILDLIFE_HIDE   (OSMTypeParent.LEISURE, true, 4, true, false, true),
    YES_LE          (OSMTypeParent.LEISURE, true, 8, false, false, true),

    BREAKWATER      (OSMTypeParent.MAN_MADE, true, 4, false, true, false),
    BRIDGE_MA       (OSMTypeParent.MAN_MADE, true, 8, false, false, true),
    BUNKER_SILO     (OSMTypeParent.MAN_MADE, true, 4, false, true, true, Strokes.BORDERSTROKE),
    CLEARCUT        (OSMTypeParent.MAN_MADE, true, 4, false, true, false),
    CUTLINE         (OSMTypeParent.MAN_MADE, true, 4, false, true, false),
    DYKE            (OSMTypeParent.MAN_MADE, true, 4, false, false, false),
    EARTHWORK       (OSMTypeParent.MAN_MADE, true, 4, false, false, true),
    EMBANKMENT      (OSMTypeParent.MAN_MADE, true, 8, false, true, false),
    MIDDEN          (OSMTypeParent.MAN_MADE, true, 4, true, false, true),
    PIER            (OSMTypeParent.MAN_MADE, true, 4, false, true, true),
    PIPELINE_MA     (OSMTypeParent.MAN_MADE, true, 4, false, true, false),
    QUAY            (OSMTypeParent.MAN_MADE, true, 4, false, false, true),
    RETAINING_WALL_MA(OSMTypeParent.MAN_MADE, true, 4, false, false, true),
    SILO            (OSMTypeParent.MAN_MADE, true, 4, false, true, true, Strokes.BORDERSTROKE),
    TUNNEL          (OSMTypeParent.MAN_MADE, true, 4, false, true, false),
    WASTEWATER_PLANT_MA(OSMTypeParent.MAN_MADE, true, 4, true, false, true),
    WINDMILL_MA     (OSMTypeParent.MAN_MADE, true, 4, true, false, true),
    WRACK           (OSMTypeParent.MAN_MADE, true, 4, true, false, true),
    YES_MA          (OSMTypeParent.MAN_MADE, true, 4, true, false, true),

    AIRFIELD        (OSMTypeParent.MILITARY, true, 4, true, false, true),
    BUNKER_MI       (OSMTypeParent.MILITARY, true, 4, true, true, true, Strokes.BORDERSTROKE),
    DANGER_AREA     (OSMTypeParent.MILITARY, true, 4, true, false, true),
    NAVAL_ACADEMY       (OSMTypeParent.MILITARY, true, 4, true, true, true),
    NAVAL_BASE      (OSMTypeParent.MILITARY, true, 4, true, false, true),
    OBSTACLE_COURSE_MI(OSMTypeParent.MILITARY, true, 4, false, true, true),
    OFFICE_MI       (OSMTypeParent.MILITARY, true, 4, true, false, true),
    RANGE           (OSMTypeParent.MILITARY, true, 4, true, false, true),
    TRENCH          (OSMTypeParent.MILITARY, true, 4, true, true, false),

    ALLOTMENTS_PL   (OSMTypeParent.PLACE, true, 4, true, false, true),
    ARCHIPELAGO     (OSMTypeParent.PLACE, true, 4, false, false, false),
    HAMLET          (OSMTypeParent.PLACE, true, 4, true, false, true),
    ISLAND          (OSMTypeParent.PLACE, true, 4, true, false, false),
    ISLET           (OSMTypeParent.PLACE, true, 4, true, false, true),
    ISOLATED_DWELLING(OSMTypeParent.PLACE, true, 4, true, false, true),
    SQUARE          (OSMTypeParent.PLACE, true, 4, true, false, true),

    BAY_NA          (OSMTypeParent.NATURAL, true, 4, false, false, false),
    BEACH           (OSMTypeParent.NATURAL, true, 4, true, false, true),
    CAPE            (OSMTypeParent.NATURAL, true, 4, true, true, true),
    CLIFF           (OSMTypeParent.NATURAL, true, 4, true, false, false),
    FELL            (OSMTypeParent.NATURAL, true, 4, true, false, true),
    FIELD           (OSMTypeParent.NATURAL, true, 4, true, false, true),
    GRASS_NA        (OSMTypeParent.NATURAL, true, 4, false, false, true),
    GRASSLAND       (OSMTypeParent.NATURAL, true, 4, false, false, true),
    HEATH           (OSMTypeParent.NATURAL, true, 4, false, false, true),
    ISLAND_NA       (OSMTypeParent.NATURAL, true, 4, false, false, true),
    MUD             (OSMTypeParent.NATURAL, true, 4, false, false, true),
    PASTURE_NA      (OSMTypeParent.NATURAL, true, 4, false, false, true),
    PEAK            (OSMTypeParent.NATURAL, true, 4, true, false, false),
    RIDGE           (OSMTypeParent.NATURAL, true, 4, false, true, false),
    ROCK            (OSMTypeParent.NATURAL, true, 8, true, false, true),
    SAND            (OSMTypeParent.NATURAL, true, 8, false, false, true),
    SCREE           (OSMTypeParent.NATURAL, true, 4, false, false, true),
    SCRUB           (OSMTypeParent.NATURAL, true, 8, true, false, true),
    SHINGLE         (OSMTypeParent.NATURAL, true, 4, false, false, true),
    SLOANE          (OSMTypeParent.NATURAL, true, 4, true, false, true),
    SPRING          (OSMTypeParent.NATURAL, true, 4, true, false, false),
    THICKET         (OSMTypeParent.NATURAL, true, 4, false, false, false),
    TREE            (OSMTypeParent.NATURAL, true, 1, false, false, true),
    VALLEY          (OSMTypeParent.NATURAL, true, 4, false, true, false),
    WETLAND         (OSMTypeParent.NATURAL, true, 4, false, false, true),
    WOOD            (OSMTypeParent.NATURAL, true, 8, false, false, true),
    YES_NA          (OSMTypeParent.NATURAL, true, 3, false, false, true),
    RESERVOIR       (OSMTypeParent.LANDUSE, true, 12, true, false, true),
    GROYNE          (OSMTypeParent.MAN_MADE, true, 8, false, true, false, Strokes.BORDERSTROKE),

    APARTMENTS      (OSMTypeParent.BUILDING, true, 5, false, true, true, Strokes.BORDERSTROKE),
    BOAT            (OSMTypeParent.BUILDING, true, 5, false, true, true, Strokes.BORDERSTROKE),
    BUNKER_BU       (OSMTypeParent.BUILDING, true, 5, false, true, true, Strokes.BORDERSTROKE),
    CABIN           (OSMTypeParent.BUILDING, true, 5, false, true, true, Strokes.BORDERSTROKE),
    CASTLE_BU       (OSMTypeParent.BUILDING, true, 5, false, true, true, Strokes.BORDERSTROKE),
    CHURCH_BU       (OSMTypeParent.BUILDING, true, 6, false, true, true, Strokes.BORDERSTROKE),
    COLLEGE_BU      (OSMTypeParent.BUILDING, true, 6, false, true, true, Strokes.BORDERSTROKE),
    COMMERCIAL_BU   (OSMTypeParent.BUILDING, true, 5, false, true, true, Strokes.BORDERSTROKE),
    CONSTRUCTION_BU (OSMTypeParent.BUILDING, true, 6, false, false, true),
    DETACHED        (OSMTypeParent.BUILDING, true, 6, false, true, true, Strokes.BORDERSTROKE),
    DORMITORY       (OSMTypeParent.BUILDING, true, 5, false, true, true, Strokes.BORDERSTROKE),
    FARM_BU         (OSMTypeParent.BUILDING, true, 5, false, true, true, Strokes.BORDERSTROKE),
    FARM_AUXILIARY  (OSMTypeParent.BUILDING, true, 5, false, true, true, Strokes.BORDERSTROKE),
    GREENHOUSE      (OSMTypeParent.BUILDING, true, 5, false, true, true, Strokes.BORDERSTROKE),
    HOSPITAL_BU     (OSMTypeParent.BUILDING, true, 6, false, true, true, Strokes.BORDERSTROKE),
    HOUSE           (OSMTypeParent.BUILDING, true, 5, false, true, true, Strokes.BORDERSTROKE),
    HUT             (OSMTypeParent.BUILDING, true, 5, false, true, true, Strokes.BORDERSTROKE),
    INDUSTRIAL_BU   (OSMTypeParent.BUILDING, true, 5, false, true, true, Strokes.BORDERSTROKE),
    KINDERGARTEN_BU (OSMTypeParent.BUILDING, true, 6, false, true, true, Strokes.BORDERSTROKE),
    MANOR_BU        (OSMTypeParent.BUILDING, true, 6, false, true, true, Strokes.BORDERSTROKE),
    OFFICE_BU       (OSMTypeParent.BUILDING, true, 5, false, true, true, Strokes.BORDERSTROKE),
    OPEN_AIR        (OSMTypeParent.BUILDING, true, 5, false, false, true),
    PARKING_BU      (OSMTypeParent.BUILDING, true, 6, false, true, true, Strokes.BORDERSTROKE),
    PLATFORM        (OSMTypeParent.BUILDING, true, 8, false, false, true),
    PUBLIC          (OSMTypeParent.BUILDING, true, 6, false, true, true, Strokes.BORDERSTROKE),
    RESIDENTIAL_BU  (OSMTypeParent.BUILDING, true, 5, false, true, true, Strokes.BORDERSTROKE),
    RETAIL_BU       (OSMTypeParent.BUILDING, true, 6, false, true, true, Strokes.BORDERSTROKE),
    ROOF            (OSMTypeParent.BUILDING, true, 5, false, true, true, Strokes.BORDERSTROKE),
    SCHOOL_BU       (OSMTypeParent.BUILDING, true, 6, false, true, true, Strokes.BORDERSTROKE),
    SHED            (OSMTypeParent.BUILDING, true, 5, false, true, true, Strokes.BORDERSTROKE),
    SILO_BU         (OSMTypeParent.BUILDING, true, 5, false, true, true, Strokes.BORDERSTROKE),
    STABLE          (OSMTypeParent.BUILDING, true, 5, false, true, true, Strokes.BORDERSTROKE),
    STATION         (OSMTypeParent.BUILDING, true, 5, false, true, true, Strokes.BORDERSTROKE),
    SUPERMARKET     (OSMTypeParent.BUILDING, true, 6, false, true, true, Strokes.BORDERSTROKE),
    TANK            (OSMTypeParent.BUILDING, true, 5, false, true, true, Strokes.BORDERSTROKE),
    TOWER           (OSMTypeParent.BUILDING, true, 8, false, true, true, Strokes.BORDERSTROKE),
    UNIVERSITY_BU   (OSMTypeParent.BUILDING, true, 6, false, true, true, Strokes.BORDERSTROKE),
    WAREHOUSE       (OSMTypeParent.BUILDING, true, 5, false, true, true, Strokes.BORDERSTROKE),
    YES_BU          (OSMTypeParent.BUILDING, true, 5, false, true, true, Strokes.BORDERSTROKE),

    DISUSED         (OSMTypeParent.RAILWAY, true, 4, false, true, false, Strokes.DOTTEDSTROKE),
    LIGHT_RAIL_RA   (OSMTypeParent.RAILWAY, true, 4, false, true, false, Strokes.DASHEDSTROKE),
    MINIATURE       (OSMTypeParent.RAILWAY, true, 4, false, true, false, Strokes.BORDERSTROKE),
    MONORAIL        (OSMTypeParent.RAILWAY, true, 4, false, true, false, Strokes.DOTTEDSTROKE),
    PLATFORM_RA     (OSMTypeParent.RAILWAY, true, 4, false, true, true),
    PRESERVED       (OSMTypeParent.RAILWAY, true, 4, false, true, false),
    RAIL            (OSMTypeParent.RAILWAY, true, 8, false, true, false, Strokes.DASHEDSTROKE),
    STATION_RA      (OSMTypeParent.RAILWAY, true, 4, true, false, true),
    SUBWAY          (OSMTypeParent.RAILWAY, false, 4, false, true, false),
    TRAM_RA         (OSMTypeParent.RAILWAY, true, 4, false, true, false),
    TRAVERSER       (OSMTypeParent.RAILWAY, true, 4, true, false, true),
    TURNTABLE       (OSMTypeParent.RAILWAY, true, 4, true, false, true),
    YES_RA          (OSMTypeParent.RAILWAY, true, 4, true, false, true),

    BRIDLEWAY       (OSMTypeParent.HIGHWAY, true, 5, false, true, false, Strokes.DOTTEDSTROKE,  -1, true, true),
    CYCLEWAY_HIG    (OSMTypeParent.HIGHWAY, true, 3, false, true, false, Strokes.DOTTEDSTROKE,  -1, false, true),
    FOOTWAY         (OSMTypeParent.HIGHWAY, true, 3, false, true, false, Strokes.DOTTEDSTROKE,  -1, true, false),
    LIVING_STREET   (OSMTypeParent.HIGHWAY, true, 5, false, true, false, Strokes.SMALLSTROKE,   30, true, true),
    MOTORWAY        (OSMTypeParent.HIGHWAY, true, Integer.MAX_VALUE, false, true, false, Strokes.BIGSTROKE, 130, false, false),
    PATH            (OSMTypeParent.HIGHWAY, true, 3, false, true, false, Strokes.DOTTEDSTROKE,  -1, true, true),
    PEDESTRIAN      (OSMTypeParent.HIGHWAY, true, 3, false, true, false, Strokes.SMALLSTROKE,   -1, true, false),
    PRIMARY         (OSMTypeParent.HIGHWAY, true, 10, false, true, false, Strokes.MEDIUMSTROKE,  80, true, true),
    RACEWAY         (OSMTypeParent.HIGHWAY, true, 5, false, true, false, Strokes.NORMALSTROKE,  130, false, false),
    RESIDENTIAL_HIG (OSMTypeParent.HIGHWAY, true, 5, false, true, false, Strokes.NORMALSTROKE,  50, true, true),
    ROAD            (OSMTypeParent.HIGHWAY, true, 5, false, true, false, Strokes.FATSTROKE,     50, true, true),
    SECONDARY       (OSMTypeParent.HIGHWAY, true, 5, false, true, false, Strokes.FATSTROKE,     50, true, true),
    SERVICE_HIG     (OSMTypeParent.HIGHWAY, true, 5, false, true, false, Strokes.SMALLSTROKE,   -1, true, true),
    STEPS           (OSMTypeParent.HIGHWAY, true, 2, false, true, false, Strokes.DOTTEDSTROKE,  -1, true, false),
    TERTIARY        (OSMTypeParent.HIGHWAY, true, 4, false, true, false, Strokes.FATSTROKE,    30, true, true),
    TRACK_HIG       (OSMTypeParent.HIGHWAY, true, 3, false, true, false, Strokes.DOTTEDSTROKE,  -1, true, false),
    TRUNK           (OSMTypeParent.HIGHWAY, true, 4, false, true, false, Strokes.SMALLSTROKE,   80, true, true),
    TRUNK_LINK      (OSMTypeParent.HIGHWAY, true, 4, false, true, false, Strokes.SMALLSTROKE,   80, true, true),

    BARRACKS        (OSMTypeParent.MILITARY, true, 4, false, false, true),
    MILITARY_LA     (OSMTypeParent.LANDUSE, true, 10, false, false, true),
    TRAINING_AREA   (OSMTypeParent.MILITARY, true, 4, false, false, true);


    private final OSMTypeParent parent;
    private boolean currentStatus;
    private final int detailLevel;

    private final boolean node;
    private final boolean line;
    private final boolean areal;
    private final boolean walkable;
    private final boolean cyckleable;

    private final Strokes stroke;
    private Color color;
    private final int speed;

    OSMType(OSMTypeParent parent, boolean status, int detailLevel, boolean node, boolean line, boolean areal) {
        this.parent = parent;
        this.currentStatus = status;
        this.detailLevel = detailLevel;
        this.stroke = Strokes.SMALLSTROKE;
        this.node = node;
        this.line = line;
        this.areal = areal;
        this.speed = 0;
        this.walkable = false;
        this.cyckleable = false;
    }

    OSMType(OSMTypeParent parent, boolean status, int detailLevel, boolean node, boolean line, boolean areal, Strokes stroke) {
        this.parent = parent;
        this.currentStatus = status;
        this.detailLevel = detailLevel;
        this.stroke = stroke;
        this.node = node;
        this.line = line;
        this.areal = areal;
        this.speed = 0;
        this.walkable = false;
        this.cyckleable = false;
    }

    OSMType(OSMTypeParent parent, boolean status, int detailLevel, boolean node, boolean line, boolean areal, Strokes stroke, int speed, boolean walkable, boolean cyckleable) {
        this.parent = parent;
        this.currentStatus = status;
        this.detailLevel = detailLevel;
        this.stroke = stroke;
        this.node = node;
        this.line = line;
        this.areal = areal;
        this.speed = speed;
        this.walkable = walkable;
        this.cyckleable = cyckleable;
    }


    public void toggle() {
        this.currentStatus = !this.currentStatus;
    }

    public boolean isEnabled(int zoomLevel) {
        if (detailLevel >= zoomLevel) return currentStatus;
        return false;
    }

    public void changeColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        if(color == null)   return Color.black;
        else                return color;
    }

    public Stroke getStroke() {
        try {
            return this.stroke.getStroke();
        } catch (NullPointerException e) {
            return null;
        }
    }

    public boolean isNode() {
        return node;
    }

    public boolean isLine() {
        return line;
    }

    public boolean isArea() {
        return areal;
    }

    public static ArrayList<OSMType> getChildren(OSMTypeParent parent) {
        ArrayList<OSMType> list = new ArrayList<>();
        for (OSMType type : OSMType.values()){
            if(type.parent == parent){
                list.add(type);
            }
        }
        return list;
    }

    public OSMTypeParent getParent() {
        return this.parent;
    }

    //TODO move to the top, when types are cleared
    private String[] postfixs = new String[] {"_AE", "_AM", "_BA", "_BU", "_CR", "_HIG", "_HI",
            "_LA", "_LE", "_MA", "_MI", "_NA", "_PL", "_RA", "_WA"};

    public String toString() {
        String name = this.name();
        if (!name.contains("_")) return name;
        for (String post : postfixs) {
            if (this.name().endsWith(post)) {
                return name.substring(0, name.length() - post.length());
            }
        }
        return name;
    }

    public int getSpeed(){
        return speed;
    }

    public int getDetailLevel() {
        return detailLevel;
    }

    public Stroke getModifiedStroke(float drawFactor) {
        try {
            return this.stroke.getModifiedStroke(drawFactor);
        } catch (NullPointerException e) {
            return null;
        }
    }

    public boolean isWalkingAllowed() {
        return walkable;
    }

    public boolean isBicycleAllowed() {
        return cyckleable;
    }
}