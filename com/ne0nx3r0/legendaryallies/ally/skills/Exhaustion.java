

package com.ne0nx3r0.legendaryallies.ally.skills;

import com.ne0nx3r0.legendaryallies.ally.Ally;
import com.ne0nx3r0.legendaryallies.ally.AllyClassType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class Exhaustion extends AllySkill {
    public Exhaustion(int dropProbability) {
        super(
            AllySkillType.Exhaustion,
            AllyClassType.Nether,
            "Exhaustion","Makes nearby players low\non food",
            45,
            dropProbability
        );
    }
    
    @Override
    public boolean onDamageOther(EntityDamageByEntityEvent e, Ally ally) {           
        return this.exhaust((Player) e.getDamager(),ally,e.getEntity());
    }
    
    @Override
    public boolean onInteractEntity(PlayerInteractEntityEvent e, Ally ally) {
        return this.exhaust(e.getPlayer(),ally,e.getRightClicked());
    }
    
    private boolean exhaust(Player owner, Ally ally,Entity eClicked) {
        if(!(eClicked instanceof Player)) {
            this.send(owner,ally,"can't exhaust that target!");
            
            return false;
        }
        
        Player target = (Player) eClicked;
        
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
