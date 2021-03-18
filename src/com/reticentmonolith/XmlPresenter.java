package com.reticentmonolith;

import java.util.HashMap;

public class XmlPresenter {
    XmlObject object;
    private HashMap<String, String> COLOR = new HashMap<>();
    int depth = 0;

    XmlPresenter(XmlObject object) {
        this.object = object;
    }

    public void display() {
        StringBuilder output = new StringBuilder();
        output.append(generateHeader(0, object.getHeader())).append("\n");
        object.getAttributes().forEach((key, value) -> {
            output.append(generateAttribute(0, key, value)).append("\n");
        });
        output.append(generateText(0, object.getText())).append("\n");
        object.getChildren().forEach(child -> {
            output.append(generateHeader(1, child.getHeader())).append("\n");
            child.getAttributes().forEach((key, value) -> {
                output.append(generateAttribute(1, key, value)).append("\n");
            });
            output.append(generateText(1, child.getText())).append("\n");
            child.getChildren().forEach(subchild -> {
                output.append(generateHeader(2, subchild.getHeader())).append("\n");
                subchild.getAttributes().forEach((key, value) -> {
                    output.append(generateAttribute(2, key, value)).append("\n");
                });
                output.append(generateText(2, subchild.getText())).append("\n");
                subchild.getChildren().forEach(subsubchild -> {
                    output.append(generateHeader(3, subsubchild.getHeader())).append("\n");
                    subsubchild.getAttributes().forEach((key, value) -> {
                        output.append(generateAttribute(3, key, value)).append("\n");
                    });
                    output.append(generateText(3, subsubchild.getText())).append("\n");
                });
            });
        });

        System.out.println(output.toString());
    }

    private String generateHeader(int depth, String text) {
        StringBuilder output = new StringBuilder();
        output
                .append("+")
                .append("-------".repeat(depth))
                .append('[')
                .append("\u001b[31m")
                .append(text.toUpperCase())
                .append("\u001b[0m")
                .append("]")
        ;
        return output.toString();
    }

    private String generateAttribute(int depth, String key, String value) {
        StringBuilder output = new StringBuilder();
        output  .append("|")
                .append("\t\t".repeat(depth))
                .append("|")
                .append("\u001b[33m")
                .append(key)
                .append("\u001b[0m")
                .append(": ")
                .append("\u001b[32m")
                .append(value)
                .append("\u001b[0m")
        ;
        return output.toString();
    }

    private String generateText(int depth, String text) {
        StringBuilder output = new StringBuilder();
        output  .append("|")
                .append("\t\t".repeat(depth))
                .append("|")
                .append(text.equals("No Text")? "\u001b[37m" : "\u001b[36m")
                .append(text)
                .append("\u001b[0m")
        ;
        return output.toString();
    }
}
