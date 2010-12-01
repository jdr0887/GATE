package org.renci.gate;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Condor ClassAd representation
 */
public class ClassAd extends HashMap<String, String> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor
     */
    public ClassAd() {
    }

    public void parseLine(String line) {
        
        if (line.indexOf(',') != -1) {
            String[] keyValuePairArray = line.split(",");
            for (String keyValuePair : keyValuePairArray) {
                parseLine(keyValuePair);
            }
        }
        
        int idx = line.indexOf('=');

        if (idx <= 0) {
            return;
        }

        String key = line.substring(0, idx);
        String value = line.substring(idx + 1);
        put(key.trim(), value.trim());
    }

    /**
     * Copy constructor
     */
    public ClassAd(ClassAd from) {
        putAll(from);
    }

    /**
     * Gets an attribute from the ad
     * 
     * @param key
     *            attribute key
     * @return attribyte value
     */
    public String get(String key) {
        return (String) get(key);
    }

    /**
     * Gets an attribute as a bool (converts from string representation to boolean)
     * 
     * @param key
     *            attribute key
     * @return attribute value (true/false)
     */
    public boolean getAsBoolean(String key) {
        String val = getReallyTrimmed(key);
        boolean b = false;
        if (val != null
                && (val.toLowerCase().equals("true") || val.toLowerCase().equals("t")
                        || val.toLowerCase().equals("yes") || val.toLowerCase().equals("y") || val.equals("1"))) {
            b = true;
        }
        return b;
    }

    /**
     * Retrieves a string attribute with qoutes stripped off
     * 
     * @param key
     *            attribute key
     * @return attribute value, no quotes
     */
    public String getReallyTrimmed(String key) {
        return reallyTrimmed(get(key));
    }

    /**
     * Retrieves a int attribute
     * 
     * @param key
     *            attribute key
     * @return attribute value as int
     */
    public int getAsInt(String key) {
        String s = reallyTrimmed(get(key));
        int i = 0;
        try {
            i = Integer.parseInt(s);
        } catch (Exception e) {
            // /ignore
        }
        return i;
    }

    /**
     * Retrives the full ad as a string
     * 
     * @return all attributes, as string
     */
    public String getFullAd() {
        StringBuilder out = new StringBuilder();
        Iterator<String> i = keySet().iterator();
        while (i.hasNext()) {
            String key = (String) i.next();
            String value = (String) get(key);
            out.append(key).append(" = ").append(value).append("\n");
        }
        return out.toString();
    }

    /**
     * Sets an attribute
     * 
     * @param key
     *            attribute key
     * @param value
     *            attribute value
     */
    public void set(String key, String value) {
        key = key.trim();
        put(key, value);
    }

    /**
     * Sets an string attribute
     * 
     * @param key
     *            attribute key
     * @param value
     *            attribute value
     */
    public void setString(String key, String value) {
        key = key.trim();
        put(key, "\"" + value + "\"");
    }

    /**
     * Sets a raw line, that is one with "key = value"
     * 
     * @param line
     *            key=value line
     */
    public void setRawLine(String line) {
        int equal = line.indexOf('=');

        if (equal <= 0) {
            return;
        }

        String key = line.substring(0, equal - 1);
        String value = line.substring(equal + 1);

        key = key.trim();
        put(key, value);
    }

    /**
     * Writes full ad to file
     * 
     * @param fileName
     *            the file to write to
     */
    public void writeToFile(String fileName) {
        try {
            FileWriter fstream = new FileWriter(fileName);
            BufferedWriter out = new BufferedWriter(fstream);

            Iterator<String> i = keySet().iterator();
            while (i.hasNext()) {
                String key = (String) i.next();
                String value = (String) get(key);
                out.write(key + " = " + value + "\n");
            }

            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Trims a string, including qoutes
     * 
     * @param s
     *            string to trim
     * @return the trimmed string
     */
    private String reallyTrimmed(String s) {

        if (s == null) {
            return null;
        }

        s = s.trim();
        if (s.charAt(0) == '"' && s.charAt(s.length() - 1) == '"') {
            s = s.substring(1, s.length() - 1);
        }

        return s;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        Set<String> keySet = keySet();

        for (String key : keySet) {
            sb.append(key).append(" = ").append(get(key));
        }

        return sb.toString();
    }

}
