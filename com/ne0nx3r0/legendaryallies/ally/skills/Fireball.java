package com.ne0nx3r0.legendaryallies.ally.skills;

import com.ne0nx3r0.legendaryallies.ally.Ally;
import com.ne0nx3r0.legendaryallies.ally.AllyClassType;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class Fireball extends AllySkill {
    public Fireball(int dropProbability) {
        super(
                AllySkillType.Fireball,
                AllyClassType.Fire,
                "Fireball",
                "Ally shoots a fireball at the target",
                10,
                dropProbability
        );
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
        
        Vector direction = lStart.toVector().subtract(receiver.toVector()).normalize();
        
        Projectile projectile;
        projectile = (Projectile) sender.getWorld().spawn(lStart, org.bukkit.entity.Fireball.class);
        projectile.setShooter(ally.getPet().getCraftPet());
        projectile.setVelocity(direction);
        
        //TODO: Add entity name if applicable
        this.send(pOwner,ally,"shot a fireball!");
        
        return true;
    }
}
