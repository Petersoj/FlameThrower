package me.petersoj;

import me.petersoj.controller.FlameController;
import org.bukkit.plugin.java.JavaPlugin;

public class FlameThrowerPlugin extends JavaPlugin {

    private FlameController flameController;

    @Override
    public void onEnable() {
        this.flameController = new FlameController(this);
    }

    public FlameController getFlameController(){
        return flameController;
    }
}