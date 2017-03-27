/*
 * Copyright 2014 William Seemann
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package wseemann.media.jplaylistparser.playlist;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * A multi-valued metadata container.
 */
public class PlaylistEntry {
//        Message, MSOffice, ClimateForcast, TIFF, TikaMetadataKeys, TikaMimeKeys,
//        Serializable {

    /**
     * A map of all metadata attributes.
     */
    private Map<String, String[]> metadata = null;

    /**
     * The common delimiter used between the namespace abbreviation and the property name
     */
    public static final String NAMESPACE_PREFIX_DELIMITER = ":";

    @Deprecated public static final String FORMAT = "format";
    @Deprecated public static final String IDENTIFIER = "identifier";
    @Deprecated public static final String MODIFIED = "modified";
    @Deprecated public static final String CONTRIBUTOR = "contributor";
    @Deprecated public static final String COVERAGE = "coverage";
    @Deprecated public static final String CREATOR = "creator";
    @Deprecated public static final String DESCRIPTION = "description";
    @Deprecated public static final String LANGUAGE = "language";
    @Deprecated public static final String PUBLISHER = "publisher";
    @Deprecated public static final String RELATION = "relation";
    @Deprecated public static final String RIGHTS = "rights";
    @Deprecated public static final String SOURCE = "source";
    @Deprecated public static final String SUBJECT = "subject";
    public static final String TITLE = "title";
    @Deprecated public static final String TYPE = "type";
    public static final String TRACK = "track";
    public static final String URI = "uri";
    public static final String PLAYLIST_METADATA = "playlist_metadata";

    /**
     * Constructs a new, empty playlist entry.
     */
    public PlaylistEntry() {
        metadata = new HashMap<String, String[]>();
    }

    /**
     * Returns true if named value is multivalued.
     * 
     * @param name
     *          name of metadata
     * @return true is named value is multivalued, false if single value or null
     */
    public boolean isMultiValued(final String name) {
        return metadata.get(name) != null && metadata.get(name).length > 1;
    }

    /**
     * Returns an array of the names contained in the metadata.
     * 
     * @return Metadata names
     */
    public String[] names() {
        return metadata.keySet().toArray(new String[metadata.keySet().size()]);
    }

    /**
     * Get the value associated to a metadata name. If many values are assiociated
     * to the specified name, then the first one is returned.
     * 
     * @param name
     *          of the metadata.
     * @return the value associated to the specified metadata name.
     */
    public String get(final String name) {
        String[] values = metadata.get(name);
        if (values == null) {
            return null;
        } else {
            return values[0];
        }
    }

    /**
     * Get the values associated to a metadata name.
     * 
     * @param name
     *          of the metadata.
     * @return the values associated to a metadata name.
     */
    public String[] getValues(final String name) {
        return _getValues(name);
    }

    private String[] _getValues(final String name) {
        String[] values = metadata.get(name);
        if (values == null) {
            values = new String[0];
        }
        return values;
    }
    
    private String[] appendedValues(String[] values, final String value) {
        String[] newValues = new String[values.length + 1];
        System.arraycopy(values, 0, newValues, 0, values.length);
        newValues[newValues.length - 1] = value;
        return newValues;
    }

    /**
     * Add a metadata name/value mapping. Add the specified value to the list of
     * values associated to the specified metadata name.
     * 
     * @param name
     *          the metadata name.
     * @param value
     *          the metadata value.
     */
    public void add(final String name, final String value) {
        String[] values = metadata.get(name);
        if (values == null) {
            set(name, value);
        } else {
            metadata.put(name, appendedValues(values, value));
        }
    }
    
    /**
     * Copy All key-value pairs from properties.
     * 
     * @param properties
     *          properties to copy from
     */
    @SuppressWarnings("unchecked")
    public void setAll(Properties properties) {
        Enumeration<String> names =
            (Enumeration<String>) properties.propertyNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            metadata.put(name, new String[] { properties.getProperty(name) });
        }
    }

    /**
     * Set metadata name/value. Associate the specified value to the specified
     * metadata name. If some previous values were associated to this name, they
     * are removed.
     * 
     * @param name
     *          the metadata name.
     * @param value
     *          the metadata value.
     */
    public void set(String name, String value) {
        metadata.put(name, new String[] { value });
    }

    /**
     * Remove a metadata and all its associated values.
     * 
     * @param name
     *          metadata name to remove
     */
    public void remove(String name) {
        metadata.remove(name);
    }

    /**
     * Returns the number of metadata names in this metadata.
     * 
     * @return number of metadata names
     */
    public int size() {
        return metadata.size();
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }

        PlaylistEntry other = null;
        try {
            other = (PlaylistEntry) o;
        } catch (ClassCastException cce) {
            return false;
        }

        if (other.size() != size()) {
            return false;
        }

        String[] names = names();
        for (int i = 0; i < names.length; i++) {
            String[] otherValues = other._getValues(names[i]);
            String[] thisValues = _getValues(names[i]);
            if (otherValues.length != thisValues.length) {
                return false;
            }
            for (int j = 0; j < otherValues.length; j++) {
                if (!otherValues[j].equals(thisValues[j])) {
                    return false;
                }
            }
        }
        return true;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        String[] names = names();
        for (int i = 0; i < names.length; i++) {
            String[] values = _getValues(names[i]);
            for (int j = 0; j < values.length; j++) {
                buf.append(names[i]).append("=").append(values[j]).append(" ");
            }
        }
        return buf.toString();
    }

}
