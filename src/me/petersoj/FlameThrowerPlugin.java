package me.petersoj;

import me.petersoj.controller.FlameController;
import me.petersoj.listener.Listeners;
import org.bukkit.plugin.java.JavaPlugin;

public class FlameThrowerPlugin extends JavaPlugin {

    private FlameController flameController;
    private Listeners listeners;

    @Override
    public void onEnable() {
        this.flameController = new FlameController(this);
        this.listeners = new Listeners(this);

        this.listeners.listen();;
    }

    public FlameController getFlameController(){
        return flameController;
    }
}