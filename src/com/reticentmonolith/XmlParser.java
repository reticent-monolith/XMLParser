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
    Pattern HIERARCHY_PATTERN = Pattern.compile(
            "<(?<opener>[^?>/]+)>|" +
            "(?<closer></[^>]+>)|" +
            "(?<selfclosing><[^?>/]+\s?/>)");
    Pattern ATTRIBUTE_PATTERN = Pattern.compile(
            "\s+(?<attribute>[a-zA-Z0-9]+=\"[a-zA-Z0-9]+\")\s?|"
    );
    Pattern TEXT_PATTERN = Pattern.compile(
            "(?<text>>\\w*<)|"
    );

    public XmlObject parse(File file) throws FileNotFoundException {
        // Add lines from xml file to array
        Scanner input = new Scanner(file);
        ArrayList<String> lines = new ArrayList<>();
        while (input.hasNext()) {
            lines.add(input.nextLine());
        }

        // find tags and create hierarchy
        lines.forEach(line -> {
            Matcher tag = HIERARCHY_PATTERN.matcher(line);
            while (tag.find()) {
                XmlObject obj = new XmlObject();
                String opener = tag.group("opener");
                String closer = tag.group("closer");
                String selfCloser = tag.group("selfclosing");

                if (opener != null) {
                    obj.setHeader(opener);
                    processing.push(obj);
                } else if (selfCloser != null) {
                    obj.setHeader(selfCloser);
                    processing.peek().addChild(obj);
                } else if (closer != null && processing.size() != 1) {
                    XmlObject closed = processing.pop();
                    processing.peek().addChild(closed);
                }
            }
        });
        return processing.pop();
    }
}
