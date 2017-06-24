package me.petersoj.controller;

import me.petersoj.FlameThrowerPlugin;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class FlameController {

    private FlameThrowerPlugin plugin;

    private int flameThrowerID;
    private int flameThrowerTime;

    public FlameController(FlameThrowerPlugin plugin) {
        this.plugin = plugin;

        plugin.saveDefaultConfig(); // Copies the default config if it doesn't exist.

        FileConfiguration config = plugin.getConfig();
        this.flameThrowerID = config.getInt("item");
        this.flameThrowerTime = config.getInt("flame-thrower-time");
    }

    public ItemStack getNewFlameThrower(int refuels, boolean setsFire) {
        ItemStack flameThrower = new ItemStack(flameThrowerID);
        this.setFlameThrowerLore(flameThrower, 100, refuels, setsFire);
        return flameThrower;
    }

    private void setFlameThrowerLore(ItemStack flameThrower, int fuelPercentage, int refuels, boolean setsFire) {
        ItemMeta itemMeta = flameThrower.getItemMeta();
        itemMeta.setDisplayName(ChatColor.RED + "Flame Thrower");

        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.DARK_RED + "Fuel: " + ChatColor.WHITE + fuelPercentage);
        lore.add(ChatColor.GRAY + "Re-fuels: " + ChatColor.WHITE + (refuels > -1 ? refuels : "Infinite"));
        if (setsFire) {
            lore.add(ChatColor.GOLD + "Sets fire to blocks");
        }
        itemMeta.setLore(lore);
        flameThrower.setItemMeta(itemMeta);
    }

    public void useFlameThrower(Player player, ItemStack flameThrower) {
        new BukkitRunnable() {

            List<String> lore = flameThrower.getItemMeta().getLore();

            int fuelPercentage = getFuel(lore);
            int refuels = getReFuels(lore);
            boolean setsFire = setsFire(lore);

            int index = 0;

            @Override
            public void run() {



                if(index >= 4){
                    setFlameThrowerLore(flameThrower, fuelPercentage, refuels, setsFire);

                    this.cancel();
                }

                index++;
            }
        }.runTaskTimer(plugin, 0, 1);
    }

    private int getFuel(List<String> lore) {
        return Integer.valueOf(lore.get(0).substring(6)); // get the first line and substring at 6th position
    }

    private int getReFuels(List<String> lore) { // Return -1 for unbreakable
        String number = lore.get(1).substring(10); // get the second line and substring at 10th position

        if (number.startsWith("Inf")) { // Will be infinite
            return -1;
        } else {
            return Integer.valueOf(number);
        }
    }

    private boolean setsFire(List<String> lore) {
        return lore.size() == 3; // Will be 3 lines long if lore contains "Sets fire to blocks"
    }
}
