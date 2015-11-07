package uk.co.arcanegames.AutoUBL.commands.ubl;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import uk.co.arcanegames.AutoUBL.AutoUBL;
import uk.co.arcanegames.AutoUBL.commands.IUBLCommand;

/**
 * This command removes players from the exempt list
 *
 * @author XHawk87
 */
public class UnexemptCommand implements IUBLCommand {

    private AutoUBL plugin;

    public UnexemptCommand(AutoUBL plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getUsage() {
        return "/unexempt [player] - Removes a player to the exempt list so that they cannot join the server if they are on the UBL";
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
        if (plugin.unexempt(playerName)) {
            sender.sendMessage(playerName + " is no longer exempt from the UBL on this server");
        } else {
            sender.sendMessage(playerName + " is not exempt from the UBL on this server");
        }
        return true;
    }
}
