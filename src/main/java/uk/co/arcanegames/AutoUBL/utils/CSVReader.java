/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.co.arcanegames.AutoUBL.utils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author XHawk87
 */
public class CSVReader {

    public static String[] parseLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == ',') {
                fields.add(sb.toString());
                sb = new StringBuilder();
            } else if (c == '"') {
                int ends = line.indexOf('"', i + 1);
                if (ends == -1) {
                    throw new IllegalArgumentException("Expected double-quote to terminate (" + i + "): " + line);
                }
                sb.append(line.substring(i + 1, ends - 1));
                i = ends;
            } else {
                sb.append(c);
            }
        }
        if (sb.length() != 0) {
            fields.add(sb.toString());
        }
        return fields.toArray(new String[fields.size()]);
    }
}
