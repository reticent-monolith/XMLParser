package com.reticentmonolith;
import java.util.ArrayList;
import java.util.HashMap;

public class XmlObject {
    private String header;
    private final HashMap<String, String> attributes = new HashMap<>();
    private final ArrayList<XmlObject> children = new ArrayList<>();
    private String text = "No Text";

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
    public String getText() {
        return this.text;
    }

    public void addChild(XmlObject child) {
        this.children.add(child);
    }

    public void addAttribute(String string) {
        StringBuilder s = new StringBuilder(string);
        int equalsIndex = s.indexOf("=");
        String key = s.substring(0, equalsIndex);
        String value = s.substring(equalsIndex+2, s.length()-1);
        attributes.put(key, value);
    }

    public HashMap<String, String> getAttributes() {
        return attributes;
    }
    public ArrayList<XmlObject> getChildren() {
        return children;
    }
}
