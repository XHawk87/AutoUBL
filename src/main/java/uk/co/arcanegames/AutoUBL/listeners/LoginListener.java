package uk.co.arcanegames.AutoUBL.listeners;

import java.util.UUID;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import uk.co.arcanegames.AutoUBL.AutoUBL;

/**
 * This listens for players attempting to connect to the server and checks them
 * against the ban-list asynchronously
 *
 * Nobody will be allowed onto the server until the ban-list has had a chance to
 * load
 *
 * @author XHawk87
 */
public class LoginListener implements Listener {

    public AutoUBL plugin;

    public void registerEvents(AutoUBL plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onLogin(AsyncPlayerPreLoginEvent event) {
        if (!plugin.isReady()) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "AutoUBL is not ready");
            return;
        }
        if (plugin.isUUIDReady()) {
            try {
                if (plugin.isBanned(event.getName(), event.getUniqueId())) {
                    event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, plugin.getBanMessage(event.getUniqueId()));
                }
                return;
            } catch (NoSuchMethodError ex) { // In case the server does not yet have AsyncPlayerPreLoginEvent.getUniqueId() method
                plugin.getLogger().warning("This server is outdated and not capable of detecting player UUIDs before they join. Falling back on IGNs for now, please consider updating to at least the latest CB 1.7.5-R0.1-SNAPSHOT or later");
            }
        }
        if (plugin.isBanned(event.getName())) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, plugin.getBanMessage(event.getName()));
        }
    }
}
