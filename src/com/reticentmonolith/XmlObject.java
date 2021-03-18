package com.reticentmonolith;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

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

    public void addAttribute(String string) {
        StringBuilder s = new StringBuilder(string);
        int equalsIndex = s.indexOf("=");
        String key = s.substring(0, equalsIndex);
        String value = s.substring(equalsIndex+2, s.length()-1);
        attributes.put(key, value);
    }

    @Override
    public String toString() {
        String RESET = "\u001b[0m";
        String RED = "\u001b[31m";
        StringBuffer output = new StringBuffer();
        AtomicInteger tabs = new AtomicInteger();
        output  .append("Header: ")
                .append(RED)
                .append(this.header)
                .append(RESET)
                .append("\n");
        this.children.forEach(child -> {
            output.append("\t".repeat(tabs.getAndIncrement()));
            output.append(child.toString());
        });
        return output.toString();
    }

}
