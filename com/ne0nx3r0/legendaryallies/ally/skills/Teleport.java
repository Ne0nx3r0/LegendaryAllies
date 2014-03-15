

package com.ne0nx3r0.legendaryallies.ally.skills;

import com.ne0nx3r0.legendaryallies.ally.Ally;
import com.ne0nx3r0.legendaryallies.ally.AllyClassType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class Teleport extends AllySkill {
    public Teleport(int dropProbability) {
        super(
            AllySkillType.Teleport,
            AllyClassType.Mage,
            "Teleport","Teleport to a target location",
            10,
            dropProbability
        );
    }

    @Override
    public boolean onInteract(PlayerInteractEvent e, Ally ally) {
        if(e.hasBlock()) {
            return this.teleportTo(e.getPlayer(), ally, e.getClickedBlock().getLocation());
        }

        int distance = ally.getPrimaryClass() == this.getClassType() ? 40 : 20;
        
        return this.teleportTo(e.getPlayer(), ally, e.getPlayer().getTargetBlock(null, distance).getLocation());
    }
    
    @Override
    public boolean onDamageOther(EntityDamageByEntityEvent e, Ally ally) {
        Location teleportTo = e.getEntity().getLocation();
        
        this.teleportTo(((Player) e.getDamager()), ally, teleportTo);
        
        //TODO: Add entity name if applicable
        ((Player) e.getDamager()).sendMessage(ally.getName()+" teleported you!");
        
        return true;
    }
    
    @Override
    public boolean onInteractEntity(PlayerInteractEntityEvent e, Ally ally) {
        Location teleportTo = e.getRightClicked().getLocation();
        
        return this.teleportTo(e.getPlayer(), ally, teleportTo);
    }
    
    public boolean teleportTo(Player player, Ally ally, Location teleportTo) {
        while(!teleportTo.getBlock().getType().equals(Material.AIR) && teleportTo.getBlockY() < teleportTo.getWorld().getMaxHeight()) {
            teleportTo.add(0, 1, 0);
        }
        
        teleportTo.setPitch(player.getLocation().getPitch());
        teleportTo.setDirection(player.getLocation().getDirection());
        
        player.teleport(teleportTo);
        
        this.send(player,ally,"teleported you!");
        
        return true;
    }
}
