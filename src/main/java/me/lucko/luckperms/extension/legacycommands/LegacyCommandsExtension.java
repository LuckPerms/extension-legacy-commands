package me.lucko.luckperms.extension.legacycommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class LegacyCommandsExtension extends JavaPlugin implements CommandExecutor {
    private PluginCommand luckPermsCommand;
    private CommandExecutor executor;

    @Override
    public void onEnable() {
        this.luckPermsCommand = getServer().getPluginCommand("luckperms");
        Objects.requireNonNull(this.luckPermsCommand, "luckPermsCommand");
        this.executor = this.luckPermsCommand.getExecutor();
        this.luckPermsCommand.setExecutor(this);
    }

    @Override
    public void onDisable() {
        this.luckPermsCommand.setExecutor(this.executor);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        List<String> arguments = new ArrayList<>(Arrays.asList(args));
        handleRewrites(arguments, true);
        return this.executor.onCommand(sender, command, label, arguments.toArray(new String[0]));
    }

    /* https://github.com/lucko/LuckPerms/blob/5577e3f59068a10d80201a6e07e60f620e5c654f/common/src/main/java/me/lucko/luckperms/common/command/CommandManager.java#L335 */
    private static void handleRewrites(List<String> args, boolean rewriteLastArgument) {
        // Provide aliases
        if (args.size() >= 1 && (rewriteLastArgument || args.size() >= 2)) {
            String arg0 = args.get(0);
            if (arg0.equalsIgnoreCase("u") || arg0.equalsIgnoreCase("player") || arg0.equalsIgnoreCase("p")) {
                args.remove(0);
                args.add(0, "user");
            } else if (arg0.equalsIgnoreCase("g")) {
                args.remove(0);
                args.add(0, "group");
            } else if (arg0.equalsIgnoreCase("t")) {
                args.remove(0);
                args.add(0, "track");
            } else if (arg0.equalsIgnoreCase("i")) {
                args.remove(0);
                args.add(0, "info");
            }
        }

        if (args.size() >= 3 && (rewriteLastArgument || args.size() >= 4)) {
            if (!args.get(0).equalsIgnoreCase("user") && !args.get(0).equalsIgnoreCase("group")) {
                return;
            }

            String s = args.get(2).toLowerCase();
            switch (s) {
                // Provide aliases
                case "p":
                case "perm":
                case "perms":
                    args.remove(2);
                    args.add(2, "permission");
                    break;
                case "chat":
                case "m":
                    args.remove(2);
                    args.add(2, "meta");
                    break;
                case "i":
                case "about":
                case "list":
                    args.remove(2);
                    args.add(2, "info");
                    break;
                case "inherit":
                case "inheritances":
                case "group":
                case "groups":
                case "g":
                case "rank":
                case "ranks":
                case "parents":
                    args.remove(2);
                    args.add(2, "parent");
                    break;
                case "e":
                    args.remove(2);
                    args.add(2, "editor");
                    break;

                // Provide backwards compatibility
                case "setprimarygroup":
                case "switchprimarygroup":
                    args.remove(2);
                    args.add(2, "parent");
                    args.add(3, "switchprimarygroup");
                    break;
                case "listnodes":
                    args.remove(2);
                    args.add(2, "permission");
                    args.add(3, "info");
                    break;
                case "set":
                case "unset":
                case "settemp":
                case "unsettemp":
                    args.add(2, "permission");
                    break;
                case "haspermission":
                    args.remove(2);
                    args.add(2, "permission");
                    args.add(3, "check");
                    break;
                case "inheritspermission":
                    args.remove(2);
                    args.add(2, "permission");
                    args.add(3, "checkinherits");
                    break;
                case "listgroups":
                    args.remove(2);
                    args.add(2, "parent");
                    args.add(3, "info");
                    break;
                case "addgroup":
                case "setinherit":
                    args.remove(2);
                    args.add(2, "parent");
                    args.add(3, "add");
                    break;
                case "setgroup":
                    args.remove(2);
                    args.add(2, "parent");
                    args.add(3, "set");
                    break;
                case "removegroup":
                case "unsetinherit":
                    args.remove(2);
                    args.add(2, "parent");
                    args.add(3, "remove");
                    break;
                case "addtempgroup":
                case "settempinherit":
                    args.remove(2);
                    args.add(2, "parent");
                    args.add(3, "addtemp");
                    break;
                case "removetempgroup":
                case "unsettempinherit":
                    args.remove(2);
                    args.add(2, "parent");
                    args.add(3, "removetemp");
                    break;
                case "chatmeta":
                    args.remove(2);
                    args.add(2, "meta");
                    args.add(3, "info");
                    break;
                case "addprefix":
                case "addsuffix":
                case "removeprefix":
                case "removesuffix":
                case "addtempprefix":
                case "addtempsuffix":
                case "removetempprefix":
                case "removetempsuffix":
                    args.add(2, "meta");
                    break;
                default:
                    break;
            }

            // provide lazy info
            boolean lazyInfo = (
                    args.size() >= 4 && (rewriteLastArgument || args.size() >= 5) &&
                    (args.get(2).equalsIgnoreCase("permission") || args.get(2).equalsIgnoreCase("parent") || args.get(2).equalsIgnoreCase("meta")) &&
                    (args.get(3).equalsIgnoreCase("i") || args.get(3).equalsIgnoreCase("about") || args.get(3).equalsIgnoreCase("list"))
            );

            if (lazyInfo) {
                args.remove(3);
                args.add(3, "info");
            }

            // Provide lazy set rewrite
            boolean lazySet = (
                    args.size() >= 6 && (rewriteLastArgument || args.size() >= 7) &&
                    args.get(2).equalsIgnoreCase("permission") &&
                    args.get(3).toLowerCase().startsWith("set") &&
                    (args.get(5).equalsIgnoreCase("none") || args.get(5).equalsIgnoreCase("0"))
            );

            if (lazySet) {
                args.remove(5);
                args.remove(3);
                args.add(3, "unset");
            }
        }
    }

}
