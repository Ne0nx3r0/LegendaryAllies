

package com.ne0nx3r0.legendaryallies.ally.skills;

import com.ne0nx3r0.legendaryallies.LegendaryAlliesPlugin;
import com.ne0nx3r0.legendaryallies.ally.Ally;
import com.ne0nx3r0.legendaryallies.ally.AllyClassType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class VoidSight extends AllySkill {
    public VoidSight(int dropProbability) {
        super(
            AllySkillType.VoidSight,
            AllyClassType.Void,
            "Void Sight","Allows the owner to see through blocks",
            60,
            dropProbability
        );
    }

    @Override
    public boolean onInteract(PlayerInteractEvent e, Ally ally) {
        if(e.hasBlock()) {
            final Block block = e.getClickedBlock();
            final Player player = e.getPlayer();
            
            LegendaryAlliesPlugin plugin = (LegendaryAlliesPlugin) Bukkit.getServer().getPluginManager().getPlugin("LegendaryAllies");
            
            Bukkit.getServer().getScheduler().runTaskLater(plugin, new Runnable(){
                @Override
                public void run() {
                    World world = block.getWorld();

                    int size = 3;

                    int x = block.getX();
                    int y = block.getY();
                    int z = block.getZ();

                    for(int i=x-size;i<x+size;i++) { 
                        for(int j=y;j<y+size;j++) { 
                            for(int k=z-size;k<z+size;k++) {
                                Block b = world.getBlockAt(i, j, k);

                                if(!b.getType().equals(Material.AIR) && !b.getType().equals(Material.GLASS)) {
                                    player.sendBlockChange(b.getLocation(), Material.GLASS, (byte) 0);
                                }
                            }
                        }
                    }
                }
            }, 30);
            
            Bukkit.getServer().getScheduler().runTaskLater(plugin, new Runnable(){
                @Override
                public void run() {
                    World world = block.getWorld();

                    int size = 3;

                    int x = block.getX();
                    int y = block.getY();
                    int z = block.getZ();

                    for(int i=x-size;i<x+size;i++) { 
                        for(int j=y;j<y+size;j++) { 
                            for(int k=z-size;k<z+size;k++) {
                                Block b = world.getBlockAt(i, j, k);

                                if(!b.getType().equals(Material.AIR) && !b.getType().equals(Material.GLASS)) {
                                    player.sendBlockChange(b.getLocation(), b.getType(), b.getData());
                                }
                            }
                        }
                    }
                }
            }, 30 + (5*20+ally.getLevel()*20) * (ally.getPrimaryClass() == this.getClassType() ? 2 : 1) );

            this.send(player,ally,"cast void sight on that area!");

            return true;
        }
        
        return false;
    }
}
