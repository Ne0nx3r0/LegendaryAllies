package com.ne0nx3r0.legendaryallies.ally.skills;

import com.ne0nx3r0.legendaryallies.ally.Ally;
import com.ne0nx3r0.legendaryallies.ally.AllyClassType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class VoidShift extends AllySkill {
    public VoidShift(int dropProbability) {
        super(
                AllySkillType.VoidShift,
                AllyClassType.Void,
                "Void Shift","Shift into or from The Void",
                30,
                dropProbability);
    }

    @Override
    public boolean onInteract(PlayerInteractEvent e, Ally ally) {
        return this.shiftPlayer(e.getPlayer(), ally);
    }
    
    @Override
    public boolean onDamageOther(EntityDamageByEntityEvent e, Ally ally) {
        return this.shiftPlayer(((Player) e.getDamager()), ally);
    }
    
    @Override
    public boolean onInteractEntity(PlayerInteractEntityEvent e, Ally ally) {
        return this.shiftPlayer(e.getPlayer(), ally);
    }
    
    public boolean shiftPlayer(Player player, Ally ally) {
        String sCurrentWorld = player.getLocation().getWorld().getName();
        
        Location teleportTo = player.getLocation();
        
        if(sCurrentWorld.contains("_the_end")) {
            teleportTo.setWorld(Bukkit.getWorld(sCurrentWorld.substring(0, sCurrentWorld.indexOf("_the_end"))));
            teleportTo.setX(teleportTo.getBlockX());
            teleportTo.setZ(teleportTo.getBlockZ());
            
            this.send(player,ally,"shifted you out of The Void!");
        }
        else {
            teleportTo.setWorld(Bukkit.getWorld(sCurrentWorld+"_the_end"));
            teleportTo.setX(teleportTo.getBlockX());
            teleportTo.setZ(teleportTo.getBlockZ());
            
            this.send(player,ally,"shifted you into The Void!");
        }
        
        player.teleport(teleportTo);
        
        return true;
    }
    
    @Override
    public int getCooldownSeconds(Ally ally) {
        return ally.getPrimaryClass() == this.getClassType() ? this.cooldown / 2 : this.cooldown;
    }
}
