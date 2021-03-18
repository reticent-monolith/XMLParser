package com.reticentmonolith;
import java.util.ArrayList;
import java.util.HashMap;


public class XmlObject {
    private String header;
    private HashMap<String, String> attributes = new HashMap<>();
    private ArrayList<XmlObject> children = new ArrayList<>();
    private String text = "";

    /* Getters and setters */
    public void setHeader(String header) {
        this.header = header;
    }

    public String getHeader() {
        return this.header;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void addChild(XmlObject child) {
        this.children.add(child);
    }

}
