package uk.co.arcanegames.AutoUBL.commands.ubl;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import uk.co.arcanegames.AutoUBL.AutoUBL;
import uk.co.arcanegames.AutoUBL.commands.IUBLCommand;

/**
 * This command adds players to the exempt list
 * 
 * @author XHawk87
 */
public class ExemptCommand implements IUBLCommand {

    private AutoUBL plugin;

    public ExemptCommand(AutoUBL plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getUsage() {
        return "/exempt [player] - Adds a player to the exempt list so that they can join the server even if they are on the UBL";
    }

    @Override
    public String getPermission() {
        return "autoubl.commands.exemption";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // If there are no args or the "help" keyword is used, display the usage message
        if (args.length == 0 || args.length == 1 && args[0].equalsIgnoreCase("help")) {
            sender.sendMessage(getUsage());
            return true;
        }

        if (args.length > 1) {
            return false; // Too many arguments
        }
        
        String playerName = args[0];
        if (plugin.exempt(playerName)) {
            sender.sendMessage(playerName + " is now exempt from the UBL on this server");
        } else {
            sender.sendMessage(playerName + " is already exempt from the UBL on this server");
        }
        return true;
    }
}
