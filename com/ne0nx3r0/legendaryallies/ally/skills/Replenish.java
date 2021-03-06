package com.ne0nx3r0.legendaryallies.ally.skills;

import com.ne0nx3r0.legendaryallies.ally.Ally;
import com.ne0nx3r0.legendaryallies.ally.AllyClassType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class Replenish extends AllySkill {
    public Replenish(int dropProbability) {
        super(
            AllySkillType.Replenish,
            AllyClassType.Support,
            "Replenish",
            "Self or targetted player is\ninstantly satiated.",
            45,
            dropProbability
        );
    }

    @Override
    public boolean onInteract(PlayerInteractEvent e, Ally ally) {
        Player player = e.getPlayer();
        
        return this.replenishPlayer(player, ally, player);
    }
    
    @Override
    public boolean onDamageOther(EntityDamageByEntityEvent e, Ally ally) {
        if(e.getEntity() instanceof Player) {
            return this.replenishPlayer((Player) e.getDamager(), ally, (Player) e.getEntity());
        }
        
        return false;
    }
    
    @Override
    public boolean onInteractEntity(PlayerInteractEntityEvent e, Ally ally) {
        if(e.getRightClicked() instanceof Player) {
            return this.replenishPlayer(e.getPlayer(), ally, (Player) e.getRightClicked());
        }
        
        return false;
    }

    private boolean replenishPlayer(Player caster, Ally ally, Player target) {
        if(target.getFoodLevel() < 20) {
            int newFoodAmount = 10;
            
            // Double base food amount if primary skill
            if(ally.getPrimaryClass() == this.getClassType()) {
                newFoodAmount = newFoodAmount * 2;
            }
            
            // Add the target's current food level and level modifier
            newFoodAmount += target.getFoodLevel() + ally.getLevel();
            
            if(newFoodAmount > 20) {
                target.setFoodLevel(20);
            }
            else {
                target.setFoodLevel(newFoodAmount);
            }
            
            if(caster.equals(target)) {
                this.send(caster, ally, "used "+this.getName()+" on you!");
            }
            else {
                this.send(caster, ally, "used "+this.getName()+" on "+target.getName()+"!");
                this.send(target, ally, "used "+this.getName()+" on you!");
            }
            
            return true;
        }
        
        if(caster.equals(target)) {
            this.sendError(caster, ally, "thinks you are already full!");
        }
        else {
            this.sendError(caster, ally, "thinks "+target.getName()+" is already full!");
        }
        
        return false;
    }
}
