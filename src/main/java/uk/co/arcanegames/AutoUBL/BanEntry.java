/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.co.arcanegames.AutoUBL;

import java.util.Objects;
import uk.co.arcanegames.AutoUBL.utils.CSVReader;

/**
 *
 * @author XHawk87
 */
public class BanEntry {

    private String ign;
    private String reason;
    private String banDate;
    private String banLength;
    private String banExpiry;
    private String courtPost;

    public BanEntry() {
    }

    public BanEntry(String rawCSV) {
        String[] parts = CSVReader.parseLine(rawCSV);
        if (parts.length != 6) {
            throw new IllegalArgumentException("Expected 6 columns: " + rawCSV);
        }
        this.ign = parts[0];
        this.reason = parts[1];
        this.banDate = parts[2];
        this.banLength = parts[3];
        this.banExpiry = parts[4];
        this.courtPost = parts[5];
    }

    public BanEntry(String ign, String reason, String banDate, String banLength, String banExpiry, String courtPost) {
        this.ign = ign;
        this.reason = reason;
        this.banDate = banDate;
        this.banLength = banLength;
        this.banExpiry = banExpiry;
        this.courtPost = courtPost;
    }

    public String getBanDate() {
        return banDate;
    }

    public String getBanExpiry() {
        return banExpiry;
    }

    public String getBanLength() {
        return banLength;
    }

    public String getCourtPost() {
        return courtPost;
    }

    public String getIgn() {
        return ign;
    }

    public String getReason() {
        return reason;
    }

    public void setBanDate(String banDate) {
        this.banDate = banDate;
    }

    public void setBanExpiry(String banExpiry) {
        this.banExpiry = banExpiry;
    }

    public void setBanLength(String banLength) {
        this.banLength = banLength;
    }

    public void setCourtPost(String courtPost) {
        this.courtPost = courtPost;
    }

    public void setIgn(String ign) {
        this.ign = ign;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof BanEntry) {
            BanEntry other = (BanEntry) obj;
            return other.ign.equalsIgnoreCase(ign);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.ign);
        return hash;
    }
}
