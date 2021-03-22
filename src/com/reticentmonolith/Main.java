package com.reticentmonolith;

import java.io.File;
import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) {
        File file = new File(args[0]);
        XmlParser parser = new XmlParser();
        XmlObject result = new XmlObject();
        try {
            result = parser.parse(file);
            XmlPresenter presenter = new XmlPresenter(result);
            presenter.display();
        } catch (FileNotFoundException err) {
            System.err.println("Cannot find " + file.toString());
        } catch (MalformedTokenException err) {
            System.err.println("Malformed token found (" + err + "), exiting...");
        }


    }
}
