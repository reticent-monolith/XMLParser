package com.reticentmonolith;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

class XmlParserTest {

    @Test
    void parseBasicXmlPrintsHeader_root() throws FileNotFoundException {
        File file = new File("res/basic.xml");
        XmlParser parser = new XmlParser();
        XmlObject result;
//        result = parser.parse(file);
//        assertEquals(result.getHeader(), "root");
    }
}