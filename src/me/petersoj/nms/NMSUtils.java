package me.petersoj.nms;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class NMSUtils {
// I know that you can simply call player.spigot().sendMessage(), but I don't think that method exists for 1.8.8

    private static NMSVersion nmsVersion;

    public static void setupNMS() {
        String version = Bukkit.getVersion();

        if (version.contains("1.8.8")) {
            nmsVersion = NMSVersion.v1_8_8;
        } else if (version.contains("1.11.2")) {
            nmsVersion = NMSVersion.v1_11_2;
        } else if (version.contains("1.12")) {
            nmsVersion = NMSVersion.v1_12;
        }
    }

    public static void sendActionBar(Player player, String message) {
        switch (nmsVersion) {
            case v1_8_8:
                sendActionBarv1_12(player, message);
                break;
            case v1_11_2:
                sendActionBarv1_12(player, message);
                break;
            case v1_12:
                sendActionBarv1_12(player, message);
                break;
        }
    }


//    private static void sendActionBarv1_8_8(Player player, String message) {
//        net.minecraft.server.v1_8_R3.IChatBaseComponent chatBaseComponent = net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + message + "\"}");
//
//        net.minecraft.server.v1_8_R3.PacketPlayOutChat packetPlayOutChat = new net.minecraft.server.v1_8_R3.PacketPlayOutChat(chatBaseComponent, (byte) 2);
//
//        ((org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(packetPlayOutChat);
//    }

//    private static void sendActionBarv1_11_2(Player player, String message) {
//        net.minecraft.server.v1_11_R1.IChatBaseComponent chatBaseComponent = net.minecraft.server.v1_11_R1.IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + message + "\"}");
//
//        net.minecraft.server.v1_11_R1.PacketPlayOutChat packetPlayOutChat = new net.minecraft.server.v1_11_R1.PacketPlayOutChat(chatBaseComponent, (byte) 2);
//
//        ((org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(packetPlayOutChat);
//    }

    private static void sendActionBarv1_12(Player player, String message) {
        net.minecraft.server.v1_12_R1.IChatBaseComponent chatBaseComponent = net.minecraft.server.v1_12_R1.IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + message + "\"}");

        net.minecraft.server.v1_12_R1.PacketPlayOutChat packetPlayOutChat = new net.minecraft.server.v1_12_R1.PacketPlayOutChat(chatBaseComponent, net.minecraft.server.v1_12_R1.ChatMessageType.GAME_INFO);

        ((org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(packetPlayOutChat);
    }
}
