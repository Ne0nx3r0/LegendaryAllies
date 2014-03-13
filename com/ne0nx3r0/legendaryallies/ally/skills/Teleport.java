package com.ne0nx3r0.legendaryallies.ally.skills;

import com.ne0nx3r0.legendaryallies.ally.Ally;
import com.ne0nx3r0.legendaryallies.ally.AllyClassType;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class Teleport extends AllySkill {
    public Teleport() {
        super(AllySkillType.Teleport,AllyClassType.Mage,"Teleport","Teleport to a ",10);
    }

    @Override
    public boolean onInteract(PlayerInteractEvent e, Ally ally) {
        if(e.hasBlock()) {
            Location teleportTo = e.getClickedBlock().getLocation();

            this.teleportTo(e.getPlayer(), teleportTo);

            e.getPlayer().sendMessage(ally.getName()+" teleported you!");

            return true;
        }
        e.getPlayer().sendMessage(ChatColor.RED+"You need to target a block!");
        
        return false;
    }
    
    @Override
    public boolean onDamageOther(EntityDamageByEntityEvent e, Ally ally) {
        Location teleportTo = e.getEntity().getLocation();
        
        this.teleportTo(((Player) e.getDamager()), teleportTo);
        
        //TODO: Add entity name if applicable
        ((Player) e.getDamager()).sendMessage(ally.getName()+" teleported you!");
        
        return true;
    }
    
    @Override
    public boolean onInteractEntity(PlayerInteractEntityEvent e, Ally ally) {
        Location teleportTo = e.getRightClicked().getLocation();
        
        this.teleportTo(e.getPlayer(), teleportTo);
        
        //TODO: Add entity name if applicable
        e.getPlayer().sendMessage(ally.getName()+" teleported you!");
        
        return true;
    }
    
    public void teleportTo(Player player, Location teleportTo) {
        while(!teleportTo.getBlock().getType().equals(Material.AIR) && teleportTo.getBlockY() < teleportTo.getWorld().getMaxHeight()) {
            teleportTo.add(0, 1, 0);
        }
        
        teleportTo.setPitch(player.getLocation().getPitch());
        teleportTo.setDirection(player.getLocation().getDirection());
        
        player.teleport(teleportTo);
    }
}
