package com.reticentmonolith;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
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
        var scrapedString = scrapeFromXml(file);
        var strings = cleanStrings(scrapedString);
        var tokens = tokenise(strings);
        // TODO create exception for this
        if (!validate(tokens)) System.err.println("Openers != Closers");
        process(tokens);
        return processing.pop();
    }
    private String scrapeFromXml(File file) throws FileNotFoundException {
        Scanner input = new Scanner(file);
        input.useDelimiter("");
        StringBuilder xmlString = new StringBuilder();
        ArrayList<String> notAllowed = new ArrayList<>(Arrays.asList("\n", "\r", "\t"));
        while (input.hasNext()) {
            String ch = input.next();
            if (notAllowed.contains(ch)) {
                String next = input.next();
                while (!notAllowed.contains(next)) {
                    next = input.next();
                }
                xmlString.append(" ");
            }
            else if (ch.equals(" ")) {
                String next = input.next();
                if (!next.equals(" ")) xmlString.append(" ").append(next);
            }
            else xmlString.append(ch);
        }
        input.close();

        return xmlString.toString();
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
