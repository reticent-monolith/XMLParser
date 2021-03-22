package com.reticentmonolith;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XmlParser {
    Stack<XmlObject> processing = new Stack<>();

    /* Main parsing operation */
    public XmlObject parse(File file) throws FileNotFoundException {
        var scrapedString = scrapeFromXml(file);
        var strings = cleanStrings(scrapedString);
        var tokens = tokenise(strings);
        // TODO create exception for this
        // TODO improve validation
        if (!validate(tokens)) System.err.println("Openers != Closers");
        process(tokens);
        return processing.pop();
    }

    /*--------------------------------- Private Methods -------------------------------------*/
    private String scrapeFromXml(File file) throws FileNotFoundException {
        Scanner input = new Scanner(file);
        input.useDelimiter("");
        StringBuilder xmlString = new StringBuilder();
        ArrayList<String> notAllowed = new ArrayList<>(Arrays.asList("\n", "\r", "\t"));
        while (input.hasNext()) {xmlString.append(input.next());}
        input.close();
        return xmlString.toString().replaceAll("[\\n\\t\\r]", "").replaceAll("[\\s]{2,}", " ");
    }
    private ArrayList<String> cleanStrings(String inputString) {
        Scanner string = new Scanner(inputString);
        string.useDelimiter(">");
        ArrayList<String> tokens = new ArrayList<>();
        while (string.hasNext()) {
            String token = string.next() + ">";
            token = token.trim();
            if (token.charAt(0) != '<' && token.contains("<")) {
                String[] split = token.split("<");
                split[0] = split[0].trim();
                split[1] = "<" + split[1];
                tokens.addAll(Arrays.asList(split));
            } else tokens.add(token);
        }
        return tokens;
    }
    private ArrayList<XmlElement> tokenise(ArrayList<String> array) {
        ArrayList<XmlElement> tokens = new ArrayList<>();
        array.forEach(string -> {
            XmlElement token = new XmlElement(string);
            tokens.add(token);
        });

        return tokens;
    }
    private boolean validate(ArrayList<XmlElement> tokens) {
        // check for equals numbers of closers and openers
        int openers = (int) tokens.stream().filter(token -> token.type().equals(TYPES.Opener)).count();
        int closers =
                (int) tokens.stream().filter(token -> token.type().equals(TYPES.Closer)).count();
        return openers == closers;

    }
    private void process(ArrayList<XmlElement> tokens) {
        tokens.forEach(token -> {
            switch (token.type()) {
                case Opener -> {
                    XmlObject obj = new XmlObject();
                    addHeaderAndAttributes(token.getContent(), obj);
                    processing.push(obj);
                }
                case Closer -> {
                    if (processing.size() > 1) {
                        XmlObject closed = processing.pop();
                        processing.peek().addChild(closed);
                    }
                }
                case Single -> {
                    XmlObject obj = new XmlObject();
                    addHeaderAndAttributes(token.getContent(), obj);
                    processing.peek().addChild(obj);
                }
                case Text -> {
                    processing.peek().addText(token.getContent());
                }
                case Malformed -> {
                    System.err.println("Malformed token: " + token.getContent());
                }
            }
        });
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
            obj.setHeader(tag.substring(1, spaceIndex));
        } else if (tag.contains("/")) {
            int slashIndex = tag.indexOf("/");
            if (slashIndex < 0) slashIndex = tag.length();
            obj.setHeader(tag.substring(0, slashIndex));
        } else {
            obj.setHeader(tag.substring(1, tag.length()-1));
        }
    }
}
