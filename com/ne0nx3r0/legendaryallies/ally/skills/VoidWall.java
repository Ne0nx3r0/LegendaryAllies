

package com.ne0nx3r0.legendaryallies.ally.skills;

import com.ne0nx3r0.legendaryallies.LegendaryAlliesPlugin;
import com.ne0nx3r0.legendaryallies.ally.Ally;
import com.ne0nx3r0.legendaryallies.ally.AllyClassType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class VoidWall extends AllySkill {
    public VoidWall(int dropProbability) {
        super(
            AllySkillType.VoidWall,
            AllyClassType.Void,
            "Void Wall","Creates a fake wall around a player",
            10,
            dropProbability
        );
    }
    
    @Override
    public boolean onDamageOther(EntityDamageByEntityEvent e, Ally ally) {
        if(e.getEntity() instanceof Player) {
            Player target = (Player) e.getEntity();
            
            return this.voidWall((Player) e.getDamager(),ally,target);
        }
        return false;
    }
    
    @Override
    public boolean onInteractEntity(PlayerInteractEntityEvent e, Ally ally) {
        if(e.getRightClicked() instanceof Player){
            Player target = (Player) e.getRightClicked();

            return this.voidWall(e.getPlayer(),ally,target);
        }
        return false;
    }
    
    private boolean voidWall(Player sender, Ally ally, Player receiver) {
            final Player player = sender;
            final Player target = receiver;
            
            LegendaryAlliesPlugin plugin = (LegendaryAlliesPlugin) Bukkit.getServer().getPluginManager().getPlugin("LegendaryAllies");
            
            Bukkit.getServer().getScheduler().runTaskLater(plugin, new Runnable(){
                @Override
                public void run() {
                    Location location = target.getLocation();
                    World world = location.getWorld();

                    int size = 3;

                    int x = location.getBlockX();
                    int y = location.getBlockY();
                    int z = location.getBlockZ();

                    for(int i=x-size;i<x+size;i++) { 
                        for(int j=y-1;j<y+size;j++) { 
                            for(int k=z-size;k<z+size;k++) {
                                if(i == x - size || j == y - size || k == z - size || i == x + size - 1 || j == y + size - 1 || k == z + size - 1) {
                                    Block b = world.getBlockAt(i, j, k);

                                    if(b.getType().equals(Material.AIR)) {
                                        target.sendBlockChange(b.getLocation(), Material.ENDER_STONE, (byte) 0);
                                    }
                                }
                            }
                        }
                    }
                }
            
            }, 30);
            
            Bukkit.getServer().getScheduler().runTaskLater(plugin, new Runnable(){
                @Override
                public void run() {
                    Location location = target.getLocation();
                    World world = location.getWorld();

                    int size = 3;

                    int x = location.getBlockX();
                    int y = location.getBlockY();
                    int z = location.getBlockZ();

                    for(int i=x-size;i<x+size;i++) { 
                        for(int j=y-1;j<y+size;j++) { 
                            for(int k=z-size;k<z+size;k++) {
                                if(i == x - size || j == y - size || k == z - size || i == x + size - 1 || j == y + size - 1 || k == z + size - 1) {
                                    Block b = world.getBlockAt(i, j, k);

                                    target.sendBlockChange(b.getLocation(), b.getType(), b.getData());
                                }
                            }
                        }
                    }
                }
            }, 30 + (5*20+ally.getLevel()*20) * (ally.getPrimaryClass() == this.getClassType() ? 2 : 1) );

            this.send(player,ally,"cast void wall on "+target.getName()+"!");
            this.send(target,ally,"cast void wall on you!");
                
            return true;
    }
}
