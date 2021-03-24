package com.reticentmonolith;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

class XmlParserTest {

    @Test
    void parseBasicXmlPrintsHeader_root() throws FileNotFoundException, MalformedTokenException,
            UnbalancedHierarchyException {
        File file = new File("C:\\Users\\bjone\\OneDrive\\Documents\\Development\\Sample_Files" +
                "\\sample.xml");
        XmlParser parser = new XmlParser();

    }
}