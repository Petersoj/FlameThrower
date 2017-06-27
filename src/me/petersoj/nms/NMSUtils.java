package me.petersoj.nms;

import me.petersoj.nms.actionbars.ActionBarv1_11_2;
import me.petersoj.nms.actionbars.ActionBarv1_12;
import me.petersoj.nms.actionbars.ActionBarv1_8_8;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class NMSUtils {
// I know that you can simply call player.spigot().sendMessage(), but I don't think that method exists for 1.8.8

    private static ActionBarSender actionBarSender;

    public static void setupNMS() {
        String version = Bukkit.getVersion();

        if (version.contains("1.8.8")) {
            actionBarSender = new ActionBarv1_8_8();
        } else if (version.contains("1.11.2")) {
            actionBarSender = new ActionBarv1_11_2();
        } else if (version.contains("1.12")) {
            actionBarSender = new ActionBarv1_12();
        }
    }

    public static void sendActionBar(Player player, String message) {
        actionBarSender.sendActionBar(player, message);
    }
}
