package com.reticentmonolith;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XmlParser {
    Stack<XmlObject> processing = new Stack<>();

    public XmlObject parse(File file) throws FileNotFoundException {
        // Add xml from xml file to array
        Scanner input = new Scanner(file);
        StringBuilder xmlBuilder = new StringBuilder();
        while (input.hasNext()) {xmlBuilder.append(input.nextLine());}
        String xml = xmlBuilder.toString();
        // find tags and create hierarchy
        Pattern HIERARCHY_PATTERN = Pattern.compile(
                "<(?<opener>[^?>/]+)>|" +
                "(?<closer></[^>]+>)|" +
                "<(?<selfclosing>[^?>/]+\s?/>)|"
        );
        Matcher tag = HIERARCHY_PATTERN.matcher(xml);
        while (tag.find()) {
            XmlObject obj = new XmlObject();
            String opener = tag.group("opener");
            String closer = tag.group("closer");
            String selfCloser = tag.group("selfclosing");
            if (opener != null) {
                addHeaderAndAttributes(opener, obj);
                processing.push(obj);
            } else if (selfCloser != null) {
                addHeaderAndAttributes(selfCloser, obj);
                processing.peek().addChild(obj);
            } else if (closer != null && processing.size() > 1) {
                XmlObject closed = processing.pop();
                processing.peek().addChild(closed);
            }
        }
        return processing.pop();
    }

    private void addHeaderAndAttributes(String tag, XmlObject obj) {
        Pattern ATTRIBUTE_PATTERN = Pattern.compile(
                "\s?(?<attribute>[a-zA-Z0-9_]+=\"[a-zA-Z0-9._\\s]+\")\s?|"
        );
        Matcher attributeMatcher = ATTRIBUTE_PATTERN.matcher(tag);
        while (attributeMatcher.find()) {
            String attribute = attributeMatcher.group("attribute");
            if (attribute != null) {
                obj.addAttribute(attribute);
            }
        }
        StringBuilder headerBuilder = new StringBuilder(tag);
        if (tag.contains(" ")) {
            int spaceIndex = tag.indexOf(" ");
            if (spaceIndex < 0) spaceIndex = tag.length();
            obj.setHeader(headerBuilder.substring(0, spaceIndex));
        } else if (tag.contains("/")) {
            int slashIndex = tag.indexOf("/");
            if (slashIndex < 0) slashIndex = tag.length();
            obj.setHeader(headerBuilder.substring(0, slashIndex));
        }
    }
}
