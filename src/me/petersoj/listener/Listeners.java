package me.petersoj.listener;

import me.petersoj.FlameThrowerPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public class Listeners implements Listener {

    private FlameThrowerPlugin plugin;

    public Listeners(FlameThrowerPlugin flameThrower) {
        this.plugin = flameThrower;
    }

    public void listen(){
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
}