package uk.co.arcanegames.AutoUBL.commands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import uk.co.arcanegames.AutoUBL.AutoUBL;
import uk.co.arcanegames.AutoUBL.commands.ubl.ExemptCommand;
import uk.co.arcanegames.AutoUBL.commands.ubl.ReloadCommand;
import uk.co.arcanegames.AutoUBL.commands.ubl.UnexemptCommand;
import uk.co.arcanegames.AutoUBL.commands.ubl.UpdateCommand;

/**
 * The base for all UBL commands
 *
 * @author XHawk87
 */
public class UBLCommand implements CommandExecutor {

    /**
     * All subcommands of the UBL command, stored by their name
     */
    private Map<String, IUBLCommand> subCommands = new HashMap<>();

    public UBLCommand(AutoUBL plugin) {
        subCommands.put("exempt", new ExemptCommand(plugin));
        subCommands.put("unexempt", new UnexemptCommand(plugin));
        subCommands.put("update", new UpdateCommand(plugin));
        subCommands.put("reload", new ReloadCommand(plugin));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // It is assumed that entering the menu command without parameters is an
        // attempt to get information about it. So let's give it to them.
        if (args.length == 0) {
            for (IUBLCommand menuCommand : subCommands.values()) {
                String permission = menuCommand.getPermission();
                if (permission != null && sender.hasPermission(permission)) {
                    sender.sendMessage(menuCommand.getUsage());
                }
            }
            return true;
        }

        String subCommandName = args[0].toLowerCase();
        IUBLCommand ublCommand = subCommands.get(subCommandName);
        if (ublCommand == null) {
            return false; // They mistyped or entered an invalid subcommand
        }
        // Handle the permissions check
        String permission = ublCommand.getPermission();
        if (permission != null && !sender.hasPermission(permission)) {
            sender.sendMessage("You do not have permission to use this command");
            return true;
        }
        // Remove the sub-command from the args list and pass along the rest
        if (!ublCommand.onCommand(sender, command, label, Arrays.copyOfRange(args, 1, args.length))) {
            // A sub-command returning false should display the usage information for that sub-command
            sender.sendMessage(ublCommand.getUsage());
        }
        return true;
    }
}
