package com.ne0nx3r0.legendaryallies.ally.skills;

import com.ne0nx3r0.legendaryallies.ally.Ally;
import com.ne0nx3r0.legendaryallies.ally.AllyClassType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlaneShift extends AllySkill {
    public PlaneShift() {
        super(
                AllySkillType.PlaneShift,
                AllyClassType.Nether,
                "Plane Shift","Shift into or from The Nether",
                20);
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
        
        if(sCurrentWorld.contains("_nether")) {
            teleportTo.setWorld(Bukkit.getWorld(sCurrentWorld.substring(0, sCurrentWorld.indexOf("_nether"))));
            teleportTo.setX(teleportTo.getBlockX()*8);
            teleportTo.setZ(teleportTo.getBlockZ()*8);
            
            this.send(player,ally,"Shifted you out of The Nether!");
        }
        else {
            teleportTo.setWorld(Bukkit.getWorld(sCurrentWorld+"_nether"));
            teleportTo.setX(teleportTo.getBlockX()/8);
            teleportTo.setZ(teleportTo.getBlockZ()/8);
            
            this.send(player,ally,"Shifted you into The Nether!");
        }
        
        player.teleport(teleportTo);
        
        return true;
    }
    
    @Override
    public int getCooldownSeconds(Ally ally) {
        return ally.getPrimaryClass() == this.getClassType() ? this.cooldown / 2 : this.cooldown;
    }
}