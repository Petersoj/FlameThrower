package me.petersoj.listener;

import me.petersoj.FlameThrowerPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.ItemMeta;

public class Listeners implements Listener {

    private FlameThrowerPlugin plugin;

    public Listeners(FlameThrowerPlugin flameThrower) {
        this.plugin = flameThrower;
    }

    public void listen() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // Happens last
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK && e.hasItem()) {
            ItemMeta itemMeta = e.getItem().getItemMeta();

            if (itemMeta.hasLore() && itemMeta.hasDisplayName()) {
                if (ChatColor.stripColor(itemMeta.getDisplayName()).equals("Flame Thrower")) {

                    plugin.getFlameController().useFlameThrower(e.getPlayer(), e.getItem());
                    e.setCancelled(true);
                }
            }
        }
    }
}