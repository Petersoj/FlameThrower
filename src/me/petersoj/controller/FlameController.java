package me.petersoj.controller;

import me.petersoj.FlameThrowerPlugin;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class FlameController {

    private FlameThrowerPlugin plugin;

    private int flameThrowerID;
    private int flameThrowerTime;

    public FlameController(FlameThrowerPlugin plugin){
        this.plugin = plugin;

        plugin.saveDefaultConfig(); // Copies the default config if it doesn't exist.

        FileConfiguration config = plugin.getConfig();
        this.flameThrowerID = config.getInt("item");
        this.flameThrowerTime = config.getInt("flame-thrower-time");
    }

    public ItemStack getNewFlameThrower(int percentFueled, int uses, boolean setsFire){
        ItemStack flameThrower = new ItemStack(flameThrowerID);

        ItemMeta itemMeta = flameThrower.getItemMeta();
        itemMeta.setDisplayName(ChatColor.RED + "Flame Thrower");

        ArrayList<String> lore = new ArrayList<String>();
        lore.add(ChatColor.DARK_RED + "Fuel: " + ChatColor.WHITE + percentFueled);
        if(uses < 0){
            lore.add(ChatColor.GRAY + "Unbreakable");
        }else{
            lore.add(ChatColor.GRAY + "Uses left: " + ChatColor.WHITE + uses);
        }
        if(setsFire){
            lore.add(ChatColor.GOLD + "Sets fire to blocks");
        }
        itemMeta.setLore(lore);
        flameThrower.setItemMeta(itemMeta);

        return flameThrower;
    }

    public void useFlameThrower(Player player, ItemStack flameThrower){

    }

    private int getFlameThrowerFuel(ArrayList<String> lore){
        return 0;
    }

}
