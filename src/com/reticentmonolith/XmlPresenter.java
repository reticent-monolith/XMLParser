package com.reticentmonolith;

import java.util.ArrayList;

public class XmlPresenter {
    XmlObject object;

    XmlPresenter(XmlObject object) {
        this.object = object;
    }

    public void display() {
        // presentation string
        StringBuilder output = new StringBuilder("\n");
        // recursively go through children and build presentation string
        build(output, 0, object);
        // print out the display
        System.out.println(output.toString());
    }
    private void build(StringBuilder output, int depth, XmlObject object) {
        output.append("\n");
        output.append(generateHeader(depth, object.getHeader())).append("\n");
        object.getAttributes().forEach((key, value) -> output.append(generateAttribute(depth, key,
                value)).append("\n"));
        if (object.getText().size() > 0) {
            output.append(generateText(depth, object.getText()));
        }
        object.getChildren().forEach(child -> build(output, depth+1, child));
    }

    private String generateHeader(int depth, String text) {
        return  "\t|".repeat(depth) +
                "\u001b[31m" +
                text +
                "\u001b[0m";
    }

    private String generateAttribute(int depth, String key, String value) {
        return  "\t|".repeat(depth) +
                "\u001b[33m" +
                key +
                "\u001b[0m" +
                ": " +
                "\u001b[32m" +
                value +
                "\u001b[0m";
    }

    private String generateText(int depth, ArrayList<String> text) {
        StringBuilder output = new StringBuilder();
        text.forEach(t -> output.append("\t|".repeat(depth))
                .append("\u001b[36m")
                .append(t)
                .append("\u001b[0m"));
        return  output.toString();
    }
}
