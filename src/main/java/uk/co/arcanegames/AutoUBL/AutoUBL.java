package uk.co.arcanegames.AutoUBL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import uk.co.arcanegames.AutoUBL.commands.UBLCommand;
import uk.co.arcanegames.AutoUBL.listeners.LoginListener;
import uk.co.arcanegames.AutoUBL.tasks.BanlistUpdater;
import uk.co.arcanegames.AutoUBL.utils.CSVReader;
import uk.co.arcanegames.AutoUBL.utils.StringTemplate;

/**
 * An automatic UBL plugin for the /r/ultrahardcore community
 *
 * @author XHawk87
 */
public class AutoUBL extends MultiThreadedJavaPlugin {

    private BanlistUpdater banlistUpdater;
    private Map<String, BanEntry> banlistByIGN;
    private Map<UUID, BanEntry> banlistByUUID;
    private Set<String> exempt;
    private StringTemplate banMessageTemplate;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        banlistUpdater = new BanlistUpdater(this);
        reload();
        getCommand("ubl").setExecutor(new UBLCommand(this));
        new LoginListener().registerEvents(this);
    }

    /**
     * Reload configuration settings and update the banlist
     */
    public void reload() {
        if (banlistUpdater != null) {
            banlistUpdater.cancel();
        }
        reloadConfigAsync(new BukkitRunnable() {
            @Override
            public void run() {
                getLogger().info("Configuration reloaded, checking UBL for updates");
                FileConfiguration config = getConfig();
                try {
                    String banMessage = config.getString("ban-message", "UBL - {Reason} - {Courtroom Post}");
                    banMessageTemplate = StringTemplate.getStringTemplate(banMessage);
                } catch (IllegalArgumentException ex) {
                    new InvalidConfigurationException("Invalid ban-message", ex).printStackTrace();
                }
                exempt = new HashSet<>(config.getStringList("exempt"));
                int autoCheckInterval = config.getInt("auto-check-interval", 60);
                if (autoCheckInterval > 0) {
                    banlistUpdater.schedule(autoCheckInterval);
                }
                updateBanlist();
            }
        });
    }

    /**
     * Attempt to update the banlist immediately
     */
    public void updateBanlist() {
        banlistUpdater.download();
    }

    /**
     * Add a player to the exempt list
     *
     * @param name The player to add
     * @return False, if the player was already exempt, otherwise true
     */
    public boolean exempt(String name) {
        String lname = name.toLowerCase();
        if (!exempt.contains(lname)) {
            exempt.add(lname);
            getConfig().set("exempt", new ArrayList<>(exempt));
            saveConfig();
            return true;
        }
        return false;
    }

    /**
     * Removes a player from the exempt list
     *
     * @param name The player to remove
     * @return False, if the player was not exempt, otherwise true
     */
    public boolean unexempt(String name) {
        String lname = name.toLowerCase();
        if (exempt.contains(lname)) {
            exempt.remove(lname);
            getConfig().set("exempt", new ArrayList<>(exempt));
            saveConfig();
            return true;
        }
        return false;
    }

    /**
     * Check if the given player is banned on the UBL and is not exempt on this
     * server
     *
     * @param ign The in-game name of the player to check
     * @return True, if the player is banned and not exempt, otherwise false
     */
    public boolean isBanned(String ign) {
        String lname = ign.toLowerCase();
        return banlistByIGN.containsKey(lname) && !exempt.contains(lname);
    }

    /**
     * Check if the given player is banned on the UBL and is not exempt on this
     * server
     *
     * @param ign The in-game name of the player to check against the exemptions
     * @param uuid The universally unique identifier of the player to check
     * @return True, if the player is banned and not exempt, otherwise false
     */
    public boolean isBanned(String ign, UUID uuid) {
        return banlistByUUID.containsKey(uuid) && !exempt.contains(ign.toLowerCase());
    }

    /**
     * @param ign The in-game name of the banned player
     * @return A personalised ban message for this player
     */
    public String getBanMessage(String ign) {
        BanEntry banEntry = banlistByIGN.get(ign.toLowerCase());
        if (banEntry == null) {
            return "Not on the UBL";
        }
        return banMessageTemplate.format(banEntry.getData());
    }

    /**
     * @param uuid The universally unique identifier of the banned player
     * @return A personalised ban message for this player
     */
    public String getBanMessage(UUID uuid) {
        BanEntry banEntry = banlistByUUID.get(uuid);
        if (banEntry == null) {
            return "Not on the UBL";
        }
        return banMessageTemplate.format(banEntry.getData());
    }

    /**
     * Check if the ban-list is ready. This will only be the case if a ban-list
     * has been downloaded or read from backup and contains either an IGN field
     * or a UUID field from which to check players
     *
     * @return True, if the banlist can be checked, otherwise false
     */
    public boolean isReady() {
        return banlistByIGN != null && banlistByUUID != null
                && (!banlistByIGN.isEmpty() || !banlistByUUID.isEmpty());
    }

    /**
     * Check if the ban-list is ready and can be queried using player UUIDs
     * instead of IGNs
     *
     * @return True, if the ban-list can be checked using UUIDs, otherwise false
     */
    public boolean isUUIDReady() {
        return banlistByUUID != null && !banlistByUUID.isEmpty();
    }

    /**
     * Update the entire ban-list using raw CSV lines, overwriting any previous
     * settings
     *
     * @param banlist The new ban-list
     */
    public void setBanList(String fieldNamesCSV, List<String> banlist) {
        String[] fieldNames = CSVReader.parseLine(fieldNamesCSV);
        if (!Arrays.asList(fieldNames).contains(getIGNFieldName())) {
            getLogger().warning("There is no matching IGN field (" + getIGNFieldName() + ") in the ban-list data. Please check the UBL spreadsheet and set 'fields.ign' in your config.yml to the correct field name");
            getServer().broadcast("[AutoUBL] No IGN field found in the ban-list data. If you also have no UUID field then your server will be locked to non-ops for your protection. Please see your server logs for details in how to fix this issue", "bukkit.op");
        }
        if (!Arrays.asList(fieldNames).contains(getUUIDFieldName())) {
            getLogger().warning("There is no matching UUID field (" + getUUIDFieldName() + ") in the ban-list data. Please check the UBL spreadsheet and set 'fields.uuid' in your config.yml to the correct field name");
            getServer().broadcast("[AutoUBL] No UUID field found in the ban-list data. If Mojang has not yet allowed name-changing, this is not a problem. Otherwise, please check your server logs for details on how to fix this issue", "bukkit.op");
        }
        this.banlistByIGN = new HashMap<>();
        this.banlistByUUID = new HashMap<>();
        for (String rawCSV : banlist) {
            BanEntry banEntry = new BanEntry(fieldNames, rawCSV);
            String ign = banEntry.getData(getIGNFieldName());
            if (ign != null) {
                this.banlistByIGN.put(ign.toLowerCase(), banEntry);
                banEntry.setIgn(ign);
            }
            String uuidString = banEntry.getData(getUUIDFieldName()).trim();
            if (uuidString != null) {
                if (uuidString.length() == 32) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(uuidString.substring(0, 8)).append('-');
                    sb.append(uuidString.substring(8, 12)).append('-');
                    sb.append(uuidString.substring(12, 16)).append('-');
                    sb.append(uuidString.substring(16, 20)).append('-');
                    sb.append(uuidString.substring(20, 32));
                    uuidString = sb.toString();
                }
                if (uuidString.length() == 36) {
                    UUID uuid = UUID.fromString(uuidString);
                    this.banlistByUUID.put(uuid, banEntry);
                    banEntry.setUUID(uuid);
                } else {
                    getLogger().warning("Invalid UUID in ban-list for " + ign + ": " + uuidString);
                }
            }
        }
    }

    /**
     * @return The field name to check for the player's in-game name
     */
    public String getIGNFieldName() {
        String ignFieldName = getConfig().getString("fields.ign", null);
        if (ignFieldName == null || ignFieldName.isEmpty()) {
            ignFieldName = "IGN";
            getConfig().set("fields.ign", "IGN");
            saveConfig();
        }
        return ignFieldName;
    }

    /**
     * @return The field name to check for the player's universally unique
     * identifier
     */
    public String getUUIDFieldName() {
        String uuidFieldName = getConfig().getString("fields.uuid", null);
        if (uuidFieldName == null || uuidFieldName.isEmpty()) {
            uuidFieldName = "UUID";
            getConfig().set("fields.uuid", "UUID");
            saveConfig();
        }
        return uuidFieldName;
    }
}
