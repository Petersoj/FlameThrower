package me.petersoj.controller;

import me.petersoj.FlameThrowerPlugin;
import me.petersoj.nms.NMSUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FlameController {

    private FlameThrowerPlugin plugin;

    private final int flameThrowerID;
    private final double fireChance;

    public FlameController(FlameThrowerPlugin plugin) {
        this.plugin = plugin;

        plugin.saveDefaultConfig(); // Copies the default config if it doesn't exist.

        FileConfiguration config = plugin.getConfig();
        this.flameThrowerID = config.getInt("item");
        this.fireChance = ((double) config.getInt("chance-of-fire")) / 100;

        if (config.getBoolean("craft-flame-thrower")) {
            this.addRecipe();
        }
    }

    private void addRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(getNewFlameThrower(30, 20));

        recipe.shape("B  ", "DT ", "C  ");

        recipe.setIngredient('B', Material.BLAZE_POWDER);
        recipe.setIngredient('D', Material.DISPENSER);
        recipe.setIngredient('T', Material.TORCH);
        recipe.setIngredient('C', Material.COAL);

        Bukkit.addRecipe(recipe);
    }

    public ItemStack getNewFlameThrower(int fullFuelSeconds, int refuels) {
        ItemStack flameThrower = new ItemStack(flameThrowerID);
        this.setFlameThrowerLore(flameThrower, fullFuelSeconds, fullFuelSeconds, refuels);
        return flameThrower;
    }

    private void setFlameThrowerLore(ItemStack flameThrower, double fuelTime, int fullFuelSeconds, int refuels) {
        ItemMeta itemMeta = flameThrower.getItemMeta();
        itemMeta.setDisplayName(ChatColor.RED + "Flame " + ChatColor.GOLD + "Thrower");

        ArrayList<String> lore = new ArrayList<>();

        DecimalFormat df = new DecimalFormat("###.##");
        df.setRoundingMode(RoundingMode.DOWN);

        lore.add(ChatColor.DARK_RED + "Fuel: " + ChatColor.WHITE + (fuelTime > -1 ? df.format(fuelTime) + "/" + fullFuelSeconds : "Infinite"));
        lore.add(ChatColor.GRAY + "Re-fuels: " + ChatColor.WHITE + (refuels > -1 ? refuels : "Infinite"));

        itemMeta.setLore(lore);
        flameThrower.setItemMeta(itemMeta);
    }

    // This method is VERY messy, because I was trying to make it as optimized as possible :D
    // I could have done better still... :P
    public void useFlameThrower(Player player, ItemStack flameThrower) {
        new BukkitRunnable() {

            List<String> lore = flameThrower.getItemMeta().getLore(); // Gosh darn getItemMeta gets cloned

            double fuelTime = getFuel(lore);
            int fullFuelSeconds = getFullFuelTime(lore);
            int refuels = getReFuels(lore);

            int index = 0;

            @Override
            public void run() {

                fuelTime = getFuel(flameThrower.getItemMeta().getLore()); // Needed to prevent overlapping bukkit tasks

                if (fuelTime > 0 || fuelTime == -1) { // -1 for Infinite

                    this.shootFlames();

                    if (fuelTime != -1) {
                        this.fuelTime -= 0.05; // Each tick decrement is equal to 1/20 of a second

                        this.sendActionBar();
                    }

                } else if (index == 0) { // Make sure this is the first time through
                    if (refuels > 0 || refuels == -1) { // -1 for Infinite
                        int itemIndex = player.getInventory().first(Material.COAL);
                        if (itemIndex >= 0) {
                            ItemStack coal = player.getInventory().getItem(itemIndex);
                            if (coal.getAmount() <= 1) {
                                player.getInventory().clear(itemIndex);
                            } else {
                                coal.setAmount(coal.getAmount() - 1);
                            }

                            this.fuelTime = fullFuelSeconds;

                            if (refuels != -1) {
                                refuels--;
                            }

                            NMSUtils.sendActionBar(player, ChatColor.GREEN + "Flame Thrower re-fueled!");
                            player.playSound(player.getLocation(), Sound.ITEM_FLINTANDSTEEL_USE, 1f, 1f);
                        } else {
                            NMSUtils.sendActionBar(player, ChatColor.RED + "You need coal in order to refuel this Flame Thrower!");
                            player.playSound(player.getLocation(), Sound.ITEM_FLINTANDSTEEL_USE, 1f, 0f);

                            this.fuelTime = 0;
                        }

                        this.index = 3; // To cancel the task
                    } else {
                        NMSUtils.sendActionBar(player, ChatColor.RED + "You've run out of uses for this Flame Thrower!");

                        this.index = 3; // To cancel the task

                        this.fuelTime = 0;
                    }
                }

                setFlameThrowerLore(flameThrower, fuelTime, fullFuelSeconds, refuels);

                if (index >= 3) {
                    this.cancel();
                } else {
                    index++;
                }
            }

            private void shootFlames() {

                Vector playerDirection = player.getLocation().getDirection();
                Vector particleVector = playerDirection.clone();

                playerDirection.multiply(8); // Set length to 8 blocks out

                // rotate particle location 90 degrees
                double x = particleVector.getX();
                particleVector.setX(-particleVector.getZ());
                particleVector.setZ(x);
                particleVector.divide(new Vector(3, 3, 3)); // Divide it by 2 to shorten length

                Location particleLocation = particleVector.toLocation(player.getWorld()).add(player.getLocation()).add(0, 1.05, 0);

                for (int i = 0; i < 4; i++) { // Shoot 4 times for more flames! Change here to shoot more flames!
                    shootSingleFlame(playerDirection, particleLocation);
                }

                if (Math.random() < fireChance) { // Light fire to block one fifth of the time
                    Block lookingBlock = player.getTargetBlock((Set<Material>) null, 15); // Get target block in 15 block range
                    if (lookingBlock != null && lookingBlock.getType().isBlock()) {
                        Block upBlock = lookingBlock.getRelative(BlockFace.UP);
                        if (upBlock != null && upBlock.getType() == Material.AIR) {
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    upBlock.setType(Material.FIRE);
                                }
                            }.runTaskLater(plugin, 10); // run half a second later for a more realistic effect.
                        }
                    }
                }
            }

            // Separate method for efficiency
            private void shootSingleFlame(Vector playerDirection, Location particleLocation) {
                Vector particlePath = playerDirection.clone(); // clone to prevent extra math calculations

                particlePath.add(new Vector(Math.random() - Math.random(), Math.random() - Math.random(), Math.random() - Math.random())); // Add some randomness

                Location offsetLocation = particlePath.toLocation(player.getWorld());

                player.getWorld().spawnParticle(Particle.FLAME, particleLocation, 0, offsetLocation.getX(), offsetLocation.getY(), offsetLocation.getZ(), 0.1); // Count of zero, to respect 'speed'
            }

            private void sendActionBar() {
                String block = "\u2588"; // Block character unicode
                double fuelRatio = (fuelTime / (double) fullFuelSeconds) * 10;

                System.out.println(fuelRatio);

                StringBuilder builder = new StringBuilder(ChatColor.RED + "Fuel: ");
                for (int i = 0; i < 10; i++) { // For ten block characters
                    if (i < fuelRatio) {
                        builder.append(ChatColor.GREEN + block);
                    } else {
                        builder.append(ChatColor.GRAY + block);
                    }
                }

                NMSUtils.sendActionBar(player, builder.toString());
            }
        }.runTaskTimer(plugin, 0, 0);
    }

    private double getFuel(List<String> lore) {
        String line = ChatColor.stripColor(lore.get(0));
        try {
            return Double.parseDouble(line.substring(6, line.indexOf("/"))); // get the first line and substring at 6th position to the "/"
        } catch (Exception e) {
            return -1;
        }
    }

    private int getFullFuelTime(List<String> lore) {
        String line = ChatColor.stripColor(lore.get(0));
        try {
            return Integer.parseInt(line.substring(line.indexOf("/") + 1)); // get past the "/" to the end.
        } catch (Exception e) { // Catch for infinite
            return -1;
        }
    }

    private int getReFuels(List<String> lore) { // Return -1 for infinite
        String number = ChatColor.stripColor(lore.get(1)).substring(10); // get the second line and substring at 10th position

        if (number.startsWith("Inf")) { // Will be infinite
            return -1;
        } else {
            return Integer.parseInt(number);
        }
    }
}
