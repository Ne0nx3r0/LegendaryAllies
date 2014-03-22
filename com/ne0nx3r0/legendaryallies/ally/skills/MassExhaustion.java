

package com.ne0nx3r0.legendaryallies.ally.skills;

import com.ne0nx3r0.legendaryallies.ally.Ally;
import com.ne0nx3r0.legendaryallies.ally.AllyClassType;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class MassExhaustion extends AllySkill {
    public MassExhaustion(int dropProbability) {
        super(
            AllySkillType.MassExhaustion,
            AllyClassType.Nether,
            "Mass Exhaustion","Makes nearby players low\non food",
            120,
            dropProbability
        );
    }
    
    @Override
    public boolean onInteract(PlayerInteractEvent e, Ally ally) {
        return this.exhaustArea(e.getPlayer(), ally);
    }
    
    @Override
    public boolean onDamageOther(EntityDamageByEntityEvent e, Ally ally) {           
        return this.exhaustArea((Player) e.getDamager(),ally);
    }
    
    @Override
    public boolean onInteractEntity(PlayerInteractEntityEvent e, Ally ally) {
        return this.exhaustArea(e.getPlayer(),ally);
    }

    private boolean exhaustArea(Player caster, Ally ally) {
        int affectedPlayersDistance = 10;

        int targetsAffected = 0;
        
        Location lAlly = ally.getPet().getCraftPet().getLocation();
        
        for(Player pNearby : caster.getWorld().getPlayers()) {
            if(pNearby.getLocation().distance(lAlly) < affectedPlayersDistance) {
                if(this.exhaust(caster, ally, pNearby)) {
                    targetsAffected++;
                }
            }
        }
        
        if(targetsAffected > 0) {
            return true;
        }
        else {
            this.send(caster,ally,"didn't find any valid targets!");
            
            return false;
        }
    }
    
    private boolean exhaust(Player owner, Ally ally,Player target) {        
        int currentFoodLevel = target.getFoodLevel();
        
        if(currentFoodLevel == 0) {
            this.send(owner,ally,"thinks "+target.getName()+" is already exhausted!");
            
            return false;
        }
        
        int foodDamageAmount = (5 + ally.getLevel() * 2) * (ally.getPrimaryClass() == this.getClassType() ? 2 : 1);
        
        int newFoodLevel = currentFoodLevel - foodDamageAmount;
        
        if(newFoodLevel < 0) {
            newFoodLevel = 0;
        }
        
        target.setFoodLevel(newFoodLevel);

        this.send(owner,ally,"exhausted "+target.getName()+"!");

        this.send(target,ally,"exhausted you!");

        return true;
    }
}
