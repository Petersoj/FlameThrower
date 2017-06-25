package me.petersoj.listener;

import me.petersoj.FlameThrowerPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Commands {

    private FlameThrowerPlugin plugin;

    public Commands(FlameThrowerPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getLabel().equals("flamethrower")) {

            if (args.length == 3) {
                Player giveTo = Bukkit.getPlayer(args[0]);
                int refuels = Integer.valueOf(args[1]);
                boolean setsFire = Boolean.valueOf(args[2]);

                ItemStack flameThrower = plugin.getFlameController().getNewFlameThrower(refuels, setsFire);

                if (giveTo.getInventory().addItem(flameThrower).size() > 0) { // If greater than 0, inventory was full.
                    giveTo.getWorld().dropItem(giveTo.getLocation(), flameThrower);
                }

                if (sender instanceof Player) {
                    sender.sendMessage(ChatColor.GREEN + "Successfully gave a flamethrower to " + giveTo.getName());
                }
            } else {
                sender.sendMessage(ChatColor.RED + "To few arguments!");
                sender.sendMessage(ChatColor.RED + "Usage: " + ChatColor.WHITE + command.getUsage());
            }
        }
        return true;
    }
}
