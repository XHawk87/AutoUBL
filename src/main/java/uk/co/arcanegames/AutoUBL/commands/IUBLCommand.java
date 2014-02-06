package uk.co.arcanegames.AutoUBL.commands;

import org.bukkit.command.CommandExecutor;

/**
 * Provides permissions and usage message support for UBL sub-commands
 *
 * @author XHawk87
 */
public interface IUBLCommand extends CommandExecutor {

    /**
     * @return The usage message for this subcommand
     */
    public String getUsage();

    /**
     * @return The permission-node required to use this subcommand, or null if
     * none is required
     */
    public String getPermission();
}
