package model.helpers.parsers;

import model.osm.OSMType;
import org.junit.jupiter.api.*;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class TypeParserTest {
    private TypeParser parser;
    private XMLStreamReader streamReader;

    @BeforeEach
    void setUp() {
        parser = new TypeParser();
    }

    @Test
    @DisplayName("Testing typeparser for OSMType.UNKNOWN")
    void testUnknown() throws XMLStreamException {
        updateStreamReader("unknown");
        while (streamReader.hasNext()) {
            streamReader.nextTag();
            if(streamReader.getLocalName().equals("testXML")) {
                if(streamReader.isEndElement()) break;
                if(streamReader.isStartElement()) continue;
            }
            if(streamReader.isEndElement()) continue;
            assertEquals(OSMType.UNKNOWN, parser.setType(streamReader));
        }
    }

    @Test
    @DisplayName("Testing typeparser for OSMType.YES_BU")
    void testBuildingYes() throws XMLStreamException {
        updateStreamReader("buildingYes");
        while (streamReader.hasNext()) {
            streamReader.nextTag();
            if(streamReader.getLocalName().equals("testXML")) {
                if(streamReader.isEndElement()) break;
                if(streamReader.isStartElement()) continue;
            }
            if(streamReader.isEndElement()) continue;
            assertEquals(OSMType.YES_BU, parser.setType(streamReader));
        }
    }

    @Test
    @DisplayName("Testing false inputs")
    void testFailedInput() throws XMLStreamException {
        updateStreamReader("fail");
        while (streamReader.hasNext()) {
            streamReader.nextTag();
            if(streamReader.getLocalName().equals("testXML")) {
                if(streamReader.isEndElement()) break;
                if(streamReader.isStartElement()) continue;
            }
            if(streamReader.isEndElement()) continue;
            assertNull(parser.setType(streamReader));
        }
    }

    private void updateStreamReader(String path) {
        String fullPath = "testResources/typeParsing/" + path + ".xml";

        try {
            FileInputStream testStream = new FileInputStream(fullPath);
            InputStreamReader testStreamReader = new InputStreamReader(testStream, "UTF-8");
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            BufferedReader xmlReader = new BufferedReader(testStreamReader);
            streamReader = inputFactory.createXMLStreamReader(xmlReader);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }
}