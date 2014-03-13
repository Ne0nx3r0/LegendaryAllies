package com.ne0nx3r0.legendaryallies.ally.skills;

import com.ne0nx3r0.legendaryallies.ally.Ally;
import com.ne0nx3r0.legendaryallies.ally.AllyClassType;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class Fireball extends AllySkill {
    public Fireball() {
        super(AllySkillType.Fireball,AllyClassType.Fire,"Fireball","Ally shoots a fireball at the target",10);
    }

    @Override
    public boolean onInteract(PlayerInteractEvent e, Ally ally) {
        if(e.hasBlock()) {
            Location sender = ally.getPet().getCraftPet().getLocation();
            Location receiver = e.getClickedBlock().getLocation();

            return this.shootFireballFromTo(e.getPlayer(), ally, sender, receiver);
        }
        return false;
    }
    
    @Override
    public boolean onDamageOther(EntityDamageByEntityEvent e, Ally ally) {
        Location sender = ally.getPet().getCraftPet().getLocation();
        Location receiver = e.getEntity().getLocation();
        
        return this.shootFireballFromTo((Player) e.getDamager(), ally,sender, receiver);
    }
    
    @Override
    public boolean onInteractEntity(PlayerInteractEntityEvent e, Ally ally) {
        Location sender = ally.getPet().getCraftPet().getLocation();
        Location receiver = e.getRightClicked().getLocation();
        
        return this.shootFireballFromTo(e.getPlayer(), ally, sender, receiver);
    }
    
    public boolean shootFireballFromTo(Player pOwner,Ally ally,Location sender, Location receiver) {
        Location lStart = sender.add(sender.subtract(receiver).multiply(0.3));
        
        org.bukkit.entity.Fireball fireball = lStart.getWorld().spawn(lStart, org.bukkit.entity.Fireball.class);
        
        fireball.setVelocity(lStart.toVector().subtract(receiver.toVector()).normalize());
        
        //TODO: Add entity name if applicable
        pOwner.sendMessage(ally.getName()+" shot a fireball!");
        
        return true;
    }
}
