package uk.co.arcanegames.AutoUBL.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * A simple text template parser and formatter
 *
 * @author XHawk87
 */
public class StringTemplate {

    private String[] text;
    private String[] fieldNames;

    private StringTemplate(String[] text, String[] fieldNames) {
        this.text = text;
        this.fieldNames = fieldNames;
    }

    /**
     * Build the message from the template and the given field data
     *
     * @param data The field data to use for the dynamic text
     * @return The formatted text
     */
    public String format(Map<String, String> data) {
        StringBuilder sb = new StringBuilder(text[0]);
        for (int i = 0; i < fieldNames.length; i++) {
            sb.append(data.get(fieldNames[i]));
            sb.append(text[i + 1]);
        }
        return sb.toString();
    }

    /**
     * @return A list of all plain text parts of the template
     */
    public List<String> getText() {
        return Arrays.asList(text);
    }

    /**
     * @return A list of all field names to be used for dynamic text in the
     * template
     */
    public List<String> getFieldNames() {
        return Arrays.asList(fieldNames);
    }

    /**
     * Build a new StringTemplate from template text. The template must be
     * plain text with field names surrounded by {curly braces}.
     *
     * @param template The template
     * @return A prepared StringTemplate
     */
    public static StringTemplate getStringTemplate(String template) {
        List<String> textList = new ArrayList<>();
        List<String> fieldNameList = new ArrayList<>();
        StringBuilder textBuilder = new StringBuilder();
        StringBuilder fieldNameBuilder = null;
        for (int i = 0; i < template.length(); i++) {
            char c = template.charAt(i);
            if (textBuilder != null) {
                if (c == '{') {
                    textList.add(textBuilder.toString());
                    textBuilder = null;
                    fieldNameBuilder = new StringBuilder();
                } else if (c == '}') {
                    throw new IllegalArgumentException("Invalid template. Missing open curly brace '{' in: " + template);
                } else {
                    textBuilder.append(c);
                }
            } else {
                if (c == '}') {
                    fieldNameList.add(fieldNameBuilder.toString());
                    fieldNameBuilder = null;
                    textBuilder = new StringBuilder();
                } else if (c == '}') {
                    throw new IllegalArgumentException("Invalid template. Missing closing curly brace '}' in: " + template);
                } else {
                    fieldNameBuilder.append(c);
                }
            }
        }
        if (fieldNameBuilder != null) {
            throw new IllegalArgumentException("Invalid template. Missing closing curly brace '}' in: " + template);
        }
        textList.add(textBuilder.toString());
        return new StringTemplate(textList.toArray(new String[textList.size()]), fieldNameList.toArray(new String[fieldNameList.size()]));
    }
}
