package model.helpers;

import model.CanvasModel;
import model.helpers.AddressSearch.SearchID;
import model.helpers.parsers.PostcodeAndCityCenter;
import model.osm.OSMAddress;
import model.helpers.parsers.ColorParser;
import model.helpers.parsers.OSMParser;
import model.helpers.routeGraph.GraphCreater;
import model.helpers.routeGraph.GraphMap;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Load {
    private CanvasModel canvasModel;
    private List<AddedNote> addedNotes;
    private String errorMessage;
    private OSMParser osmParser;
    private GraphMap graphMap;
    private KdTree kdTreeHouseNumbers;
    private boolean testLoading = false;
    private final static int GRAPHMAP_ARRAY_SIZE = 5574368;

    private TernarySearchTries<OSMAddress> addresses;
    private TernarySearchTries<PostcodeAndCityCenter> postcodes;
    private TernarySearchTries<List<PostcodeAndCityCenter>> cities;

    public boolean load(String filename) {
        FileInputStream inputStream;
        try {
            inputStream = new FileInputStream(filename);
        } catch (FileNotFoundException e) {
            errorMessage = "Filen kunne ikke findes.";
            return false;
        }
        try {
        if(filename.endsWith(".tgm")) {
            return loadFromTGM(inputStream, inputStream.getChannel().size());
        }
        else if (filename.endsWith(".zip")) return loadFromZip(filename);
        else if (filename.endsWith(".tmc")) return loadFromTMC(inputStream);
        else return filename.endsWith(".osm") && loadFromOSM(inputStream, inputStream.getChannel().size());
        } catch (IOException e) {
            errorMessage = "Filen kunne ikke læses korrekt.";
            return false;
        }
    }


    private boolean loadFromTGM(InputStream inputStream, long size) {
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(new BufferedInputStream(inputStream));
            canvasModel = (CanvasModel) objectInputStream.readObject();
            kdTreeHouseNumbers = (KdTree) objectInputStream.readObject();
            addresses = (TernarySearchTries<OSMAddress>) objectInputStream.readObject();
            postcodes = (TernarySearchTries<PostcodeAndCityCenter>) objectInputStream.readObject();
            cities = (TernarySearchTries<List<PostcodeAndCityCenter>>) objectInputStream.readObject();
            addedNotes = (List<AddedNote>) objectInputStream.readObject();
            GraphCreater graphCreater = new GraphCreater(canvasModel.getMapTree(), new Long(size/100).intValue(), canvasModel.getLonFactor());
            graphMap = graphCreater.getGraphMap();
            inputStream.close();
            return true;
        } catch (FileNotFoundException e) {
            errorMessage = "Filen kunne ikke findes.";
            return false;
        } catch (IOException e) {
            errorMessage = "TGM fil er korrupt.";
            return false;
        } catch (ClassNotFoundException e) {
            errorMessage = "TGM fil indholder ikke understøttede elementer.";
            return false;
        }
    }

    private boolean loadFromZip(String filename) {
        ZipInputStream zipInputStream;
        FileInputStream inputStream;

        try {
            inputStream = new FileInputStream(filename);
            zipInputStream = new ZipInputStream(inputStream);
        } catch (FileNotFoundException e) {
            errorMessage = "Filen kunne ikke findes";
            return false;
        }

        try {
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            if(zipEntry == null) {
                errorMessage = "Zip filen er tom.";
                return false;
            }

            File file = new File(zipEntry.getName());

            if (file.getName().endsWith(".osm")){
                return loadFromOSM(zipInputStream, inputStream.getChannel().size());
            } else if (!file.getName().endsWith(".tgm")) {
                return loadFromTGM(zipInputStream, inputStream.getChannel().size());
            } else {
                errorMessage = "Zip fil indeholder ikke en understøttet fil.\nFørste fil i zip skal være .osm eller .tgm.";
                return false;
            }
        } catch (IOException e) {
            errorMessage = "Zip filen er korrupt.";
            return false;
        } finally {
            try {
                zipInputStream.close();
            } catch (IOException ignored) {
            }
        }
    }

    private boolean loadFromOSM(InputStream inputStream, long fileSize) {
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            if(testLoading){
                osmParser = new OSMParser(fileSize, true);
            } else {
                osmParser = new OSMParser(fileSize, false);
            }
            boolean result = osmParser.parseOSM(inputStreamReader);
            if(!result) {
                errorMessage = osmParser.getErrorMessage();
                osmParser = null;
                return false;
            }
            canvasModel = osmParser.getCanvasModel();
            addresses   = osmParser.getAddresses();
            postcodes   = osmParser.getPostcodes();
            cities      = osmParser.getCities();
            kdTreeHouseNumbers = osmParser.getKdTreeHouseNumbers();
            graphMap = osmParser.getGraphMap();
            addedNotes = new ArrayList<>();

            osmParser = null;
            return true;
        } catch (UnsupportedEncodingException e) {
            errorMessage = "OSM filen er korrupt.";
            osmParser = null;
            return false;
        }
    }

    private boolean loadFromTMC(InputStream inputStream) {
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream,"UTF-8");
            ColorParser parser = new ColorParser();
            return parser.parseColor(inputStreamReader);
        } catch (UnsupportedEncodingException e) {
            errorMessage = "TCM filen er korrupt.";
            return false;
        }
    }

    public CanvasModel getCanvasModel() {
        return canvasModel;
    }

    public String getErrorMessage(){
        return errorMessage;
    }

    public OSMParser getOsmParser(){
        return osmParser;
    }

    public void loadMapFromResource() {
        InputStream mapStream = Load.class.getResourceAsStream("/start.tgm");
        loadFromTGM(mapStream, GRAPHMAP_ARRAY_SIZE);
    }

    public void loadThemeFromResource() {
        InputStream colorStream = Load.class.getResourceAsStream("/default.tmc");
        loadFromTMC(colorStream);
    }

    public GraphMap getGraphMap() {
        return graphMap;
    }

    public void toggleLoadWithTestInfo() {
        this.testLoading = !testLoading;
    }

    public List<AddedNote> getAddedNotes() {
        return addedNotes;
    }

    public boolean isTestInfoOn() {
        return testLoading;
    }

    public KdTree getKdTreeHouseNumbers() {
        return kdTreeHouseNumbers;
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
}
