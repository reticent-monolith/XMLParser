package com.reticentmonolith;
import java.util.ArrayList;
import java.util.HashMap;


public class XmlObject {
    private String header;
    private HashMap<String, String> attributes = new HashMap<>();
    private ArrayList<XmlObject> children = new ArrayList<>();
    private String text = "";

    String RESET = "\u001b[0m";
    String RED = "\u001b[31m";


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

    @Override
    public String toString() {
        StringBuffer output = new StringBuffer();
        output.append("Root name: ")
                .append(RED)
                .append(this.header)
                .append(RESET)
                .append("\n");
        return output.toString();
    }

}
