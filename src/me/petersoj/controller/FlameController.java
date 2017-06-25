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

    private int flameThrowerID;
    private int flameThrowerTime;

    public FlameController(FlameThrowerPlugin plugin) {
        this.plugin = plugin;

        plugin.saveDefaultConfig(); // Copies the default config if it doesn't exist.

        FileConfiguration config = plugin.getConfig();
        this.flameThrowerID = config.getInt("item");
        this.flameThrowerTime = config.getInt("flame-thrower-time");

        this.addRecipe();
    }

    private void addRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(getNewFlameThrower(5, true));

        recipe.shape("B  ", "DT ", "C  ");

        recipe.setIngredient('B', Material.BLAZE_POWDER);
        recipe.setIngredient('D', Material.DISPENSER);
        recipe.setIngredient('T', Material.TORCH);
        recipe.setIngredient('C', Material.COAL);

        Bukkit.addRecipe(recipe);
    }

    public ItemStack getNewFlameThrower(int refuels, boolean setsFire) {
        ItemStack flameThrower = new ItemStack(flameThrowerID);
        this.setFlameThrowerLore(flameThrower, 100.0, refuels, setsFire);
        return flameThrower;
    }

    private void setFlameThrowerLore(ItemStack flameThrower, double fuelPercentage, int refuels, boolean setsFire) {
        ItemMeta itemMeta = flameThrower.getItemMeta();
        itemMeta.setDisplayName(ChatColor.RED + "Flame " + ChatColor.GOLD + "Thrower");

        ArrayList<String> lore = new ArrayList<>();

        DecimalFormat df = new DecimalFormat("###.##");
        df.setRoundingMode(RoundingMode.DOWN);

        lore.add(ChatColor.DARK_RED + "Fuel: " + ChatColor.WHITE + df.format(fuelPercentage));
        lore.add(ChatColor.GRAY + "Re-fuels: " + ChatColor.WHITE + (refuels > -1 ? refuels : "Infinite"));
        if (setsFire) {
            lore.add(ChatColor.GOLD + "Sets fire to blocks");
        }
        itemMeta.setLore(lore);
        flameThrower.setItemMeta(itemMeta);
    }

    // This method is VERY messy, because I was trying to make it as optimized as possible :D
    // I could have done better still...
    public void useFlameThrower(Player player, ItemStack flameThrower) {
        new BukkitRunnable() {

            List<String> lore = flameThrower.getItemMeta().getLore();

            double fuelPercentage = getFuel(lore) / 100;
            int refuels = getReFuels(lore);
            boolean setsFire = setsFire(lore);

            int index = 0;

            @Override
            public void run() {

                if (fuelPercentage > 0) {

                    this.shootFlames();

                    this.fuelPercentage = fuelPercentage - (fuelPercentage / (flameThrowerTime * 20));

                    System.out.println(fuelPercentage);

                    this.sendActionBar();

                    if (setsFire && Math.random() >= 0.8) { // Light fire to block one fifth of the time
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
                } else if (index == 0) { // Make sure this is the first time through
                    if (refuels > 0) {
                        int itemIndex = player.getInventory().first(Material.COAL);
                        if (itemIndex >= 0) {
                            ItemStack coal = player.getInventory().getItem(itemIndex);
                            coal.setAmount(coal.getAmount() - 1);
                        } else {
                            NMSUtils.sendActionBar(player, ChatColor.RED + "You need coal in order to refuel this Flame Thrower!");
                            player.playSound(player.getLocation(), Sound.ITEM_FLINTANDSTEEL_USE, 1f, 1f);
                        }

                        this.fuelPercentage = 100;

                        refuels--;
                    } else {
                        NMSUtils.sendActionBar(player, ChatColor.RED + "You've run out of uses for this Flame Thrower!");
                    }
                }

                if (index >= 4) {
                    if (fuelPercentage > 0 || refuels > 0) {
                        setFlameThrowerLore(flameThrower, fuelPercentage * 100, refuels, setsFire);
                    }
                    this.cancel();
                }

                index++;
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
            }

            // Separate method for efficiency
            private void shootSingleFlame(Vector playerDirection, Location particleLocation) {
                Vector particlePath = playerDirection.clone(); // clone to prevent extra math calculations

                particlePath.add(new Vector(Math.random() - Math.random(), Math.random() - Math.random(), Math.random() - Math.random())); // Add some randomness

                Location offsetLocation = particlePath.toLocation(player.getWorld());

                player.getWorld().spawnParticle(Particle.FLAME, particleLocation, 0, offsetLocation.getX(), offsetLocation.getY(), offsetLocation.getZ(), 0.1);
            }

            private void sendActionBar() {

            }

        }.runTaskTimer(plugin, 0, 1);
    }

    private double getFuel(List<String> lore) {
        return Double.valueOf(ChatColor.stripColor(lore.get(0)).substring(6)); // get the first line and substring at 6th position
    }

    private int getReFuels(List<String> lore) { // Return -1 for unbreakable
        String number = ChatColor.stripColor(lore.get(1)).substring(10); // get the second line and substring at 10th position

        if (number.startsWith("Inf")) { // Will be infinite
            return -1;
        } else {
            return Integer.valueOf(number);
        }
    }

    private boolean setsFire(List<String> lore) {
        return lore.size() == 3; // Will be 3 lines long if lore contains "Sets fire to blocks"
    }
}
