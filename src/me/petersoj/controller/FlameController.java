package me.petersoj.controller;

import me.petersoj.FlameThrowerPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

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

    public void giveFlameThrower(Player player, int percentFuel){

    }

}
