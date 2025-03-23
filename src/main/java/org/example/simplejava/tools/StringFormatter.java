package org.example.simplejava.tools;

import java.util.ArrayList;
/**
 * {@code @Author:} Thomas Lu
 */
public class StringFormatter {

    /**
     * Adds a tab character at the beginning of each line in the given string.
     * @param input The input string containing multiple lines.
     * @return A new string with each line prefixed by a tab character.
     */
    public static String addTabToEachLine(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        String[] lines = input.split("\n");
        StringBuilder formattedString = new StringBuilder();

        for (String line : lines) {
            formattedString.append("\t").append(line).append("\n");
        }

        return formattedString.toString();
    }
    public static ArrayList<String> addTabToEachLine(ArrayList<String> input) {
        ArrayList<String> output= new ArrayList<>();
        if (input == null || input.isEmpty()) {
            return input;
        }
        for (String line : input) {
            output.add('\t'+line);
        }
        return output;
    }
}
