package me.petersoj;

import me.petersoj.controller.FlameController;
import me.petersoj.listener.Commands;
import me.petersoj.listener.Listeners;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class FlameThrowerPlugin extends JavaPlugin {

    private FlameController flameController;
    private Commands commands;
    private Listeners listeners;

    @Override
    public void onEnable() {
        this.flameController = new FlameController(this);
        this.commands = new Commands(this);
        this.listeners = new Listeners(this);

        this.listeners.listen();
    }

    public FlameController getFlameController() {
        return flameController;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return commands.onCommand(sender, command, label, args);
    }
}