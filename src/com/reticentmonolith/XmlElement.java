package com.reticentmonolith;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XmlElement {
    private String content;
    private TYPES type;

    XmlElement(String input) {
        Matcher opener = Pattern.compile("<[^?>/]+>").matcher(input);
        Matcher closer = Pattern.compile("</[^>]+>").matcher(input);
        Matcher single = Pattern.compile("<[^?>/]+\s?/>").matcher(input);
        // match input string against regex
        if (opener.find()) this.type = TYPES.Opener;
        else if (closer.find()) this.type = TYPES.Closer;
        else if (single.find()) this.type = TYPES.Single;
        else if (!(input.contains("<") || input.contains(">"))) this.type = TYPES.Text;
        else this.type = TYPES.Malformed;
        this.content = input;
    }

    public String getContent() {return this.content;}
    public TYPES type() {return this.type;}
}
