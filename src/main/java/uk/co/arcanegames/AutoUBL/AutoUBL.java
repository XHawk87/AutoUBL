package uk.co.arcanegames.AutoUBL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import uk.co.arcanegames.AutoUBL.commands.UBLCommand;
import uk.co.arcanegames.AutoUBL.listeners.LoginListener;
import uk.co.arcanegames.AutoUBL.tasks.BanlistUpdater;

/**
 * An automatic UBL plugin for the /r/ultrahardcore community
 *
 * @author XHawk87
 */
public class AutoUBL extends MultiThreadedJavaPlugin {

    private BanlistUpdater banlistUpdater;
    private Map<String, BanEntry> banlist;
    private Set<String> exempt;

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
     * @param name The player to check
     * @return True, if the player is banned and not exempt, otherwise false
     */
    public boolean isBanned(String name) {
        String lname = name.toLowerCase();
        return banlist.containsKey(lname) && !exempt.contains(lname);
    }

    public String getBanMessage(String name) {
        BanEntry banEntry = banlist.get(name.toLowerCase());
        if (banEntry == null) {
            return "Not on the UBL";
        }
        return "UBL - " + banEntry.getReason() + " - " + banEntry.getCourtPost();
    }
    
    /**
     * Check if the banlist is ready
     *
     * @return True, if the banlist can be checked, otherwise false
     */
    public boolean isReady() {
        return banlist != null;
    }

    /**
     * Update the entire ban-list using raw CSV lines, overwriting any previous
     * settings
     *
     * @param banlist The new ban-list
     */
    public void setBanList(List<String> banlist) {
        this.banlist = new HashMap<>();
        for (String rawCSV : banlist) {
            BanEntry banEntry = new BanEntry(rawCSV);
            this.banlist.put(banEntry.getIgn(), banEntry);
        }
    }
}
