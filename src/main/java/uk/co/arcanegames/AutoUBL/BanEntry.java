package uk.co.arcanegames.AutoUBL;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import uk.co.arcanegames.AutoUBL.utils.CSVReader;

/**
 * Represents a single entry in the ban-list
 *
 * @author XHawk87
 */
public class BanEntry {

    private Map<String, String> data = new HashMap<>();
    private String ign;
    private UUID uuid;

    /**
     * Creates a new BanEntry from a list of pre-parsed field names, used to
     * store record values by field name, and a raw CSV record
     *
     * @param fieldNames A pre-parsed array of field names
     * @param rawCSV A raw CSV record
     */
    public BanEntry(String[] fieldNames, String rawCSV) {
        String[] parts = CSVReader.parseLine(rawCSV);
        if (parts.length != fieldNames.length) {
            throw new IllegalArgumentException("Expected " + fieldNames.length + " columns: " + rawCSV);
        }
        for (int i = 0; i < fieldNames.length; i++) {
            data.put(fieldNames[i], parts[i]);
        }
    }

    /**
     * Set the value of the in-game name for this player
     *
     * @param ign The player's in-game name
     */
    public void setIgn(String ign) {
        this.ign = ign;
    }

    /**
     * Set the value of the universally unique identifier for this player
     *
     * @param uuid The player's UUID
     */
    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    /**
     * @return The player's in-game name
     */
    public String getIgn() {
        return ign;
    }

    /**
     * @return The player's universally unique identifier
     */
    public UUID getUUID() {
        return uuid;
    }

    /**
     * @param fieldName The field name to retrieve a value of
     * @return The value of the given field
     */
    public String getData(String fieldName) {
        return data.get(fieldName);
    }

    /**
     * Sets the value of a given field
     * 
     * @param fieldName The field name to set a value for
     * @param value The value to set for this field
     */
    public void setData(String fieldName, String value) {
        data.put(fieldName, value);
    }

    /**
     * @return A map of all data in this ban entry
     */
    public Map<String, String> getData() {
        return data;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof BanEntry) {
            BanEntry other = (BanEntry) obj;
            if (other.uuid != null && uuid != null) {
                return other.uuid.equals(uuid);
            }
            return other.ign.equalsIgnoreCase(ign);
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (uuid != null) {
            return uuid.hashCode();
        } else {
            return ign.hashCode();
        }
    }
}
