package com.reticentmonolith;

import java.io.File;
import java.io.FileNotFoundException;

public class Main {
    public static String RESET = "\u001b[0m";
    public static String RED = "\u001b[31m";
    public static void main(String[] args) {
        File file = new File("res/basic.xml");
        XmlParser parser = new XmlParser();
        XmlObject result = new XmlObject();
        try {
            result = parser.parse(file);
        } catch (FileNotFoundException err) {
            System.err.println("Cannot find " + file.toString());
        }

        System.out.println(result);

    }
}
