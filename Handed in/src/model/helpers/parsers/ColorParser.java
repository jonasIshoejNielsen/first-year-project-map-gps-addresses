package model.helpers.parsers;

import model.osm.OSMType;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ColorParser {
    private XMLStreamReader streamReader;
    private int red, green, blue, alpha;
    private OSMType type;
    private TypeParser typeParser;

    public boolean parseColor(InputStreamReader inputStreamReader) {
        try {
            typeParser = new TypeParser();
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            BufferedReader xmlReader = new BufferedReader(inputStreamReader);
            streamReader = inputFactory.createXMLStreamReader(xmlReader);

            if(!validateColorXML()) return false;
            return parse();

        } catch (XMLStreamException e) { return false; }
    }

    private boolean validateColorXML() throws XMLStreamException {
        while (streamReader.hasNext()) {
            streamReader.next();
            if(streamReader.isStartElement()) {
                String localName = streamReader.getLocalName();
                if(localName.equals("colorXML")) return true;
            }
        }
        return false;
    }

    private boolean parse() throws XMLStreamException {
        while (streamReader.hasNext()) {
            streamReader.nextTag();
            if      (streamReader.isEndElement() && streamReader.getLocalName().equals("color"))    continue;
            else if (streamReader.isEndElement() && streamReader.getLocalName().equals("colorXML")) return true;
            else if (streamReader.getAttributeCount() == 5) loadRGBTexture(false);
            else if (streamReader.getAttributeCount() == 6) loadRGBTexture(true);
            else if (streamReader.getAttributeCount() == 2) loadDefaultTexture();
        }
        return false;
    }

    private void loadDefaultTexture() {
        readOSMType();
        type.changeColor(new Color(217,208,201));
    }

    private void loadRGBTexture(boolean alphaOn) {
        if(readOSMType())   return;

        readColors(alphaOn);
        if(!colorCheck(alphaOn)) {
            loadDefaultTexture();
            return;
        }

        if(!alphaOn) type.changeColor(new Color(red,green,blue));
        else         type.changeColor(new Color(red,green,blue,alpha));
    }

    private boolean readOSMType() {
        type = typeParser.setType(streamReader);
        return type == OSMType.UNKNOWN;
    }

    private void readColors(boolean alphaOn) {
        red   = Integer.parseInt(streamReader.getAttributeValue(2));
        green = Integer.parseInt(streamReader.getAttributeValue(3));
        blue  = Integer.parseInt(streamReader.getAttributeValue(4));
        if(alphaOn) alpha = Integer.parseInt(streamReader.getAttributeValue(5));
    }

    private boolean colorCheck(boolean alphaOn) {
        if(red > 255 || green > 255 || blue > 255)  return false;
        else if(red < 0 || green < 0 || blue < 0)   return false;
        if(alphaOn) if(alpha > 255 || alpha < 0)    return false;
        return true;
    }
}
