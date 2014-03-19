package com.ne0nx3r0.legendaryallies.ally.skills;

import com.ne0nx3r0.legendaryallies.ally.Ally;
import com.ne0nx3r0.legendaryallies.ally.AllyClassType;
import io.github.dsh105.echopet.entity.CraftPet;
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

        int distance = ally.getPrimaryClass() == this.getClassType() ? 40 : 20;
        
        return this.shootFireballFromTo(e.getPlayer(), ally, ally.getPet().getLocation(),e.getPlayer().getTargetBlock(null, distance).getLocation());
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

        Vector direction = receiver.toVector().subtract(sender.toVector()).normalize();

        CraftPet allyShooter = ally.getPet().getEntityPet().getBukkitEntity();

        Projectile proj = allyShooter.launchProjectile(org.bukkit.entity.Fireball.class);
        
        proj.setShooter(allyShooter);
        
        proj.setBounce(false);
        
        proj.setVelocity(direction);
        
        //TODO: Add entity name if applicable
        this.send(pOwner,ally,"shot a fireball!");
        
        return true;
    }
}
