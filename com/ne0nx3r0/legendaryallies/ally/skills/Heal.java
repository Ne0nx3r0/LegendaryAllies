package com.ne0nx3r0.legendaryallies.ally.skills;

import com.ne0nx3r0.legendaryallies.ally.Ally;
import com.ne0nx3r0.legendaryallies.ally.AllyClassType;
import org.bukkit.Effect;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class Heal extends AllySkill {
    public Heal() {
        super(
            AllySkillType.Heal,
            AllyClassType.Support,
            "Heal",
            "Self or targetted creature is\n instantly healed. Healing has \n a negative effect on undead\nharming them instead",
            30
        );
    }

    @Override
    public boolean onInteract(PlayerInteractEvent e, Ally ally) {
        Player player = e.getPlayer();
        
        return this.heal(player, ally, player);
    }
    
    @Override
    public boolean onDamageOther(EntityDamageByEntityEvent e, Ally ally) {
        if(e.getEntity() instanceof LivingEntity) {
            return this.heal((Player) e.getDamager(), ally, (LivingEntity) e.getEntity());
        }
        
        return false;
    }
    
    @Override
    public boolean onInteractEntity(PlayerInteractEntityEvent e, Ally ally) {
        if(e.getRightClicked() instanceof LivingEntity) {
            return this.heal(e.getPlayer(), ally, (LivingEntity) e.getRightClicked());
        }
        
        return false;
    }

    private boolean isUndead(LivingEntity entity) {
        switch(entity.getType()) {
            case ZOMBIE:
            case SKELETON:
            case GIANT:
            case PIG_ZOMBIE:
                return true;
            default:
                return false;
        }
    }
    
    private boolean heal(Player caster, Ally ally, LivingEntity target) {
        if(target.getHealth() >= target.getMaxHealth() && !this.isUndead(target)) {
            if(caster.equals(target)) {
                this.sendError(caster, ally, "thinks you are healthy enough!");
            }
            else {
                this.sendError(caster, ally, "thinks that target is healthy enough!");
            }
            
            return false;
        }

        int healAmount = 5;
        
        healAmount = healAmount + ally.getLevel();
        
        if(ally.getPrimaryClass() == this.getClassType()) {
            healAmount = (int) (healAmount * 1.5);
        }
        
        if(!this.isUndead(target)){
            double newHealth = healAmount + target.getHealth();
            
            if(newHealth > target.getMaxHealth()) {
                newHealth = target.getMaxHealth();
            }
            
            target.setHealth(newHealth);     
            
            if(caster.equals(target)) {
                this.send(caster, ally, "healed you!");
            }
            else{
                if(target instanceof Player) {
                    this.send((Player) target, ally, "healed you!");
                }

                this.send(caster, ally, "healed the target!");
            }
        }
        else {
            double damage = target.getHealth() - healAmount;
            
            target.damage(damage,ally.getPet().getCraftPet());
            
            target.getLocation().getWorld()
                    .playEffect(target.getLocation(), Effect.MAGIC_CRIT, 1, 1);
            
            this.send(caster, ally, "harmed the target with healing energy!");
        }

        return true;
    }
}
