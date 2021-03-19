package com.reticentmonolith;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XmlParser {
    Stack<XmlObject> processing = new Stack<>();

    // Patterns
    private final Pattern OPENING_TAG = Pattern.compile("<[^?>/]+");
    private Pattern CLOSING_TAG = Pattern.compile("(?<closer>/[^>]+>)");
    private Pattern SINGLE_TAG = Pattern.compile("<(?<single>[^?>/]+\s?/>)");
    private Pattern ATTRIBUTE_PATTERN = Pattern.compile("\s?(?<attribute>[\\p{L}0-9_]+=\"[\\p{L}0-9._\\s]+\")\s?");

    public XmlObject parseOld(File file) throws FileNotFoundException {
        // Add xml from xml file to array
        Scanner input = new Scanner(file);
        StringBuilder xmlBuilder = new StringBuilder();
        while (input.hasNext()) {xmlBuilder.append(input.nextLine());}
        String xml = xmlBuilder.toString();
        // find tags and create hierarchy
        // TODO get swedish characters too
        // TODO get text before children too
        Pattern HIERARCHY_PATTERN = Pattern.compile(
                "<(?<opener>[^?>/]+)>|" +
                "(?<endText>[^>]*)<(?<closer>/[^>]+>)|" +
                "<(?<selfclosing>[^?>/]+\s?/>)|"
        );
        Matcher tag = HIERARCHY_PATTERN.matcher(xml);
        while (tag.find()) {
            XmlObject obj = new XmlObject();
            String opener = tag.group("opener");
            String endText = tag.group("endText");
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
                if (endText != null && !endText.isBlank()) {
                    closed.addText(endText);
                }
                processing.peek().addChild(closed);
            }
        }
        return processing.pop();
    }

    public XmlObject parse(File file) throws FileNotFoundException {
        // Add xml from xml file to array
        Scanner input = new Scanner(file);
        ArrayList<String> xmlArray = new ArrayList<String>();
        while (input.hasNext()) {
            xmlArray.add(
                    input.nextLine()
                        .replace(" ", "")
                        .replace("\n", ""));
        }
        input.close();
        StringBuilder xmlString = new StringBuilder();
        xmlArray.forEach(x -> {
            xmlString.append(x);
        });

        System.out.println(xmlString);

        return new XmlObject();
    }

    private void addHeaderAndAttributes(String tag, XmlObject obj) {
        Pattern ATTRIBUTE_PATTERN = Pattern.compile(
                "\s?(?<attribute>[\\p{L}0-9_]+=\"[\\p{L}0-9._\\s]+\")\s?|"
        );
        Matcher attributeMatcher = ATTRIBUTE_PATTERN.matcher(tag);
        while (attributeMatcher.find()) {
            String attribute = attributeMatcher.group("attribute");
            if (attribute != null) {
                obj.addAttribute(attribute);
            }
        }
        if (tag.contains(" ")) {
            int spaceIndex = tag.indexOf(" ");
            if (spaceIndex < 0) spaceIndex = tag.length();
            obj.setHeader(tag.substring(0, spaceIndex));
        } else if (tag.contains("/")) {
            int slashIndex = tag.indexOf("/");
            if (slashIndex < 0) slashIndex = tag.length();
            obj.setHeader(tag.substring(0, slashIndex));
        } else {
            obj.setHeader(tag);
        }
    }
}
