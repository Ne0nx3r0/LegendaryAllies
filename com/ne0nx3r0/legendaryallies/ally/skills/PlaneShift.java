package com.ne0nx3r0.legendaryallies.ally.skills;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.protection.FlagPermissions;
import com.ne0nx3r0.legendaryallies.ally.Ally;
import com.ne0nx3r0.legendaryallies.ally.AllyClassType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlaneShift extends AllySkill {
    public PlaneShift(int dropProbability) {
        super(
                AllySkillType.PlaneShift,
                AllyClassType.Nether,
                "Plane Shift","Shift into or from The Nether",
                20,
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
        
        if(sCurrentWorld.contains("_nether")) {
            teleportTo.setWorld(Bukkit.getWorld(sCurrentWorld.substring(0, sCurrentWorld.indexOf("_nether"))));
            teleportTo.setX(teleportTo.getBlockX()*8);
            teleportTo.setZ(teleportTo.getBlockZ()*8);
            
            this.send(player,ally,"shifted you out of The Nether!");
        }
        else {
            teleportTo.setWorld(Bukkit.getWorld(sCurrentWorld+"_nether"));
            teleportTo.setX(teleportTo.getBlockX()/8);
            teleportTo.setZ(teleportTo.getBlockZ()/8);
            
            this.send(player,ally,"shifted you into The Nether!");
        }
        
        FlagPermissions permsByLoc = Residence.getPermsByLocForPlayer(teleportTo, player);

        if(!permsByLoc.playerHas(player.getName(), sCurrentWorld, "move", true)) {
            this.send(player, ally, "was unable to shift you! "+ChatColor.RED+"(no move permission in other world)");
            
            return false;
        }
        
        player.teleport(teleportTo);
        
        return true;
    }
    
    @Override
    public int getCooldownSeconds(Ally ally) {
        return ally.getPrimaryClass() == this.getClassType() ? this.cooldown / 2 : this.cooldown;
    }
}
