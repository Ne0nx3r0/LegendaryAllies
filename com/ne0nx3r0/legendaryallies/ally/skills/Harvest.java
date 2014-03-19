

package com.ne0nx3r0.legendaryallies.ally.skills;

import com.ne0nx3r0.legendaryallies.ally.Ally;
import com.ne0nx3r0.legendaryallies.ally.AllyClassType;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class Harvest extends AllySkill {
    public Harvest(int dropProbability) {
        super(
            AllySkillType.Harvest,
            AllyClassType.Farmer,
            "Harvest","Harvests all nearby resources \nthat are ready",
            30,
            dropProbability
        );
    }

    @Override
    public boolean onInteract(PlayerInteractEvent e, Ally ally) {
        return this.mineCrops(e.getPlayer(), ally);
    }
    
    @Override
    public boolean onDamageOther(EntityDamageByEntityEvent e, Ally ally) {
        return this.mineCrops((Player) e.getDamager(), ally);
    }
    
    @Override
    public boolean onInteractEntity(PlayerInteractEntityEvent e, Ally ally) {
        return this.mineCrops(e.getPlayer(), ally);
    }
    
    public boolean mineCrops(Player player, Ally ally) {
        Location lAlly = ally.getPet().getCraftPet().getLocation();
        World world = lAlly.getWorld();
        
        int distance = 5;
        
        int harvested = 0;
        
        for(int x = lAlly.getBlockX()-distance;x<lAlly.getBlockX()+distance;x++) {
            for(int y = lAlly.getBlockY()-distance;y<lAlly.getBlockY()+distance;y++) {
                for(int z = lAlly.getBlockZ()-distance;z<lAlly.getBlockZ()+distance;z++) {
                    Block block = lAlly.getWorld().getBlockAt(x,y,z);
                    
                    if(this.isFarmResource(block)) {
                        harvested++;
                        
                        ItemStack[] is = this.getMinedResource(block);

                        if(is != null && !player.getInventory().addItem(is).isEmpty()) {                            
                            for(ItemStack is1 : is) {
                                player.getWorld().dropItemNaturally(player.getLocation(), is1);
                            }
                        }
                        
                        world.getBlockAt(x, y, z).setType(Material.AIR);
                    }
                }
            }
        }
        
        this.send(player,ally,"harvested "+harvested+" crops!");
        
        return true;
    }

    private boolean isFarmResource(Block block) {
        if(block.getType() == Material.CROPS) {
            if(block.getData() == (byte) 6 || block.getData() == (byte) 7) {

                return true;
            }
        }
        
        return false;
    }

    private ItemStack[] getMinedResource(Block block) {
        switch(block.getType()) {
            default:
                return null;
            case CROPS:
                return new ItemStack[]{
                    new ItemStack(Material.WHEAT),
                    new ItemStack(Material.SEEDS)
                };
        }
    }
}
