

package com.ne0nx3r0.legendaryallies.ally.skills;

import com.ne0nx3r0.legendaryallies.LegendaryAlliesPlugin;
import com.ne0nx3r0.legendaryallies.ally.Ally;
import com.ne0nx3r0.legendaryallies.ally.AllyClassType;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class VoidMirage extends AllySkill {
    public VoidMirage(int dropProbability) {
        super(
            AllySkillType.VoidMirage,
            AllyClassType.Void,
            "Void Mirage","Creates the illusion of \na mirrored world",
            60,
            dropProbability
        );
    }
    
    @Override
    public boolean onInteract(PlayerInteractEvent e, Ally ally) {
        return this.voidMirage(e.getPlayer(), ally);
    }
    
    @Override
    public boolean onDamageOther(EntityDamageByEntityEvent e, Ally ally) {           
        return this.voidMirage((Player) e.getDamager(),ally);
    }
    
    @Override
    public boolean onInteractEntity(PlayerInteractEntityEvent e, Ally ally) {
        return this.voidMirage(e.getPlayer(),ally);
    }
    
    private boolean voidMirage(Player caster, Ally ally) {
            int affectedPlayersDistance = 30;
            final int mirageSize = 10;

            final Location lCaster = caster.getLocation();
            final List<Player> affectedPlayers = new ArrayList<>();
            
            for(Player pNearby : caster.getWorld().getPlayers()) {
                if(pNearby.getLocation().distance(lCaster) < affectedPlayersDistance) {
                    affectedPlayers.add(pNearby);
                }
            }
            
            LegendaryAlliesPlugin plugin = (LegendaryAlliesPlugin) Bukkit.getServer().getPluginManager().getPlugin("LegendaryAllies");
            
            Bukkit.getServer().getScheduler().runTaskLater(plugin, new Runnable(){
                @Override
                public void run() {
                    Location location = lCaster;
                    World casterWorld = location.getWorld();
                    World mirrorFromWorld; 
                    
                    if(casterWorld.getName().contains("_the_end")) {
                       mirrorFromWorld = Bukkit.getWorld(casterWorld.getName().substring(0, casterWorld.getName().indexOf("_the_end")));
                    }
                    else {
                        mirrorFromWorld = Bukkit.getWorld(casterWorld.getName()+"_the_end");
                    }

                    int x = location.getBlockX();
                    int y = location.getBlockY();
                    int z = location.getBlockZ();

                    for(int i=x-mirageSize;i<x+mirageSize;i++) { 
                        for(int j=y-1;j<y+mirageSize;j++) { 
                            for(int k=z-mirageSize;k<z+mirageSize;k++) {
                                Block b = mirrorFromWorld.getBlockAt(i, j, k);
                                
                                Location lSend = b.getLocation();
                                
                                lSend.setWorld(casterWorld);
                                
                                for(Player pAffected : affectedPlayers) {
                                    if(pAffected.isOnline()) {
                                        pAffected.sendBlockChange(lSend,b.getTypeId(), b.getData());
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
                    World world = lCaster.getWorld();

                    int x = lCaster.getBlockX();
                    int y = lCaster.getBlockY();
                    int z = lCaster.getBlockZ();

                    for(int i=x-mirageSize;i<x+mirageSize;i++) { 
                        for(int j=y-1;j<y+mirageSize;j++) { 
                            for(int k=z-mirageSize;k<z+mirageSize;k++) {
                                Block b = world.getBlockAt(i, j, k);
                                
                                for(Player pAffected : affectedPlayers) {
                                    if(pAffected.isOnline() && pAffected.getWorld().equals(world)) {
                                        pAffected.sendBlockChange(b.getLocation(), b.getType(), b.getData());
                                    }
                                }
                            }
                        }
                    }
                }
            }, 30 + (15*20+ally.getLevel()*20) * (ally.getPrimaryClass() == this.getClassType() ? 2 : 1) );

            this.send(caster,ally,"cast void mirage!");
            
            for(Player pAffected : affectedPlayers) {
                this.send(pAffected,ally,"cast void mirage on you!");
            }
                
            return true;
    }
}
