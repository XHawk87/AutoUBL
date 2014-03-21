package uk.co.arcanegames.AutoUBL.listeners;

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
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "Server not ready");
            return;
        }
        if (plugin.isBanned(event.getName())) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, plugin.getBanMessage(event.getName()));
        }
    }
}
