package uk.co.arcanegames.AutoUBL.commands.ubl;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import uk.co.arcanegames.AutoUBL.AutoUBL;
import uk.co.arcanegames.AutoUBL.commands.IUBLCommand;

/**
 * This command reloads the plugin configuration
 *
 * @author XHawk87
 */
public class ReloadCommand implements IUBLCommand {

    private AutoUBL plugin;

    public ReloadCommand(AutoUBL plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getUsage() {
        return "/ubl reload - Reload AutoUBL settings from the config.yml and update the banlist";
    }

    @Override
    public String getPermission() {
        return "autoubl.commands.reload";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 0) {
            return false; // Expected no args
        }
        plugin.reload();
        sender.sendMessage("AutoUBL config is reloading");
        return true;
    }
}
