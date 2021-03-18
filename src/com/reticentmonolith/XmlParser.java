package com.reticentmonolith;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Stack;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.ArrayList;

public class XmlParser {
    Stack<XmlObject> processing = new Stack<>();
    public XmlObject parse(File file) throws FileNotFoundException {
        // Add lines from xml file to array
        Scanner input = new Scanner(file);
        ArrayList<String> lines = new ArrayList<>();
        while (input.hasNext()) {
            lines.add(input.nextLine());
        }

        // find tags and create hierarchy
        Pattern HIERARCHY_PATTERN = Pattern.compile(
                "<(?<opener>[^?>/]+)>|" +
                "(?<closer></[^>]+>)|" +
                "<(?<selfclosing>[^?>/]+\s?/>)|"
        );

        // TODO clean up the lines array so that newlines in text are one element

        lines.forEach(line -> {
            Matcher tag = HIERARCHY_PATTERN.matcher(line);
            while (tag.find()) {
                XmlObject obj = new XmlObject();
                String opener = tag.group("opener");
                String closer = tag.group("closer");
                String selfCloser = tag.group("selfclosing");

                if (opener != null) {
                    addHeaderAndAttributes(opener, obj);
                    setText(line, obj);
                    processing.push(obj);
                } else if (selfCloser != null) {
                    addHeaderAndAttributes(selfCloser, obj);
                    processing.peek().addChild(obj);
                } else if (closer != null && processing.size() > 1) {
                    XmlObject closed = processing.pop();
                    processing.peek().addChild(closed);
                }
            }
        });
        return processing.pop();
    }
    private void addHeaderAndAttributes(String tag, XmlObject obj) {
        Pattern ATTRIBUTE_PATTERN = Pattern.compile(
                "\s?(?<attribute>[a-zA-Z0-9]+=\"[a-zA-Z0-9]+\")\s?|"
        );
        Matcher attributeMatcher = ATTRIBUTE_PATTERN.matcher(tag);
        while (attributeMatcher.find()) {
            String attribute = attributeMatcher.group("attribute");
            if (attribute != null) {
                obj.addAttribute(attribute);
            }
        }
        StringBuilder headerBuilder = new StringBuilder(tag);
        int spaceIndex = tag.indexOf(" ");
        if (spaceIndex < 0) spaceIndex = tag.length();
        obj.setHeader(headerBuilder.substring(0, spaceIndex));
    }
    private void setText(String line, XmlObject obj) {
        Matcher textMatcher = Pattern.compile(
                "<(?<header>[^?>/]+)\s?.?>(?<text>[^<]+.?)<", Pattern.DOTALL
        ).matcher(line);
        while (textMatcher.find()) {
            String text = textMatcher.group("text");
            if (text != null) {
                obj.setText(text);
            }
        }
    }
}
