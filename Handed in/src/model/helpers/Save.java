package model.helpers;

import model.CanvasModel;
import model.helpers.AddressSearch.SearchID;
import model.helpers.routeGraph.GraphMap;
import model.osm.OSMAddress;
import model.osm.OSMType;
import model.osm.OSMTypeParent;

import java.io.*;
import java.awt.Color;
import java.util.HashMap;
import java.util.List;

public class Save {
    private String errorMessage;

    public boolean saveTGMFile(String fileName, CanvasModel canvasModel, KdTree kdTreeHouseNumbers, HashMap<SearchID, List<OSMAddress>> searchList, List<AddedNote> addedNotes) {
        if(!fileName.endsWith(".tgm")) fileName = fileName + ".tgm";
        try {
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(fileName));
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(bufferedOutputStream);
            objectOutputStream.writeObject(canvasModel);
            objectOutputStream.writeObject(kdTreeHouseNumbers);
            objectOutputStream.writeObject(searchList);
            objectOutputStream.writeObject(addedNotes);
            objectOutputStream.close();
        } catch (FileNotFoundException e) {
            errorMessage = "File not found";
            return false;
        } catch (IOException e) {
            errorMessage = "IOException";
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean saveTMCFile(String path) {
        if(!path.endsWith(".tmc")) path = path + ".tmc";
        try {
            FileWriter writer = new FileWriter(path);
            writer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\n");
            writer.append("<colorXML version=\"1.0\">" + "\n");
            for (OSMType type : OSMType.values()) {
                if (type.getParent() == null) continue;
                if (type.getParent() == OSMTypeParent.AMENITY) continue;
                if (type.getParent() == OSMTypeParent.CRAFT) continue;
                writer.append(getLine(type));
            }
            writer.append("</colorXML>");
            writer.close();
        } catch (FileNotFoundException e) {
            errorMessage = "File not found";
            return false;
        } catch (IOException exception) {
            errorMessage = "IOException";
            return false;
        }
        return true;
    }

    private String getLine(OSMType type) {
        StringBuilder s = new StringBuilder();
        s.append("    <color type=\"");
        s.append(type.getParent());
        s.append("\" att=\"");
        s.append(type.toString());
        s.append("\" ");
        s.append(getColorString(type.getColor()));
        s.append("/>");
        s.append("\n");
        return s.toString();
    }

    private String getColorString(Color color) {
        StringBuilder s = new StringBuilder();
        s.append("r=\"");
        s.append(color.getRed());
        s.append("\" g=\"");
        s.append(color.getGreen());
        s.append("\" b=\"");
        s.append(color.getBlue());
        s.append("\" a=\"");
        s.append(color.getAlpha());
        s.append("\"");
        return s.toString();
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
