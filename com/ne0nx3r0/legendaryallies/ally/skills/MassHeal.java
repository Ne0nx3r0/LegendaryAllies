package com.ne0nx3r0.legendaryallies.ally.skills;

import com.ne0nx3r0.legendaryallies.ally.Ally;
import com.ne0nx3r0.legendaryallies.ally.AllyClassType;
import org.bukkit.Effect;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class MassHeal extends AllySkill {
    public MassHeal(int dropProbability) {
        super(
            AllySkillType.MassHeal,
            AllyClassType.Support,
            "Mass Heal",
            "Self and nearby creatures are\n instantly healed. Healing has \n a negative effect on undead\nharming them instead",
            60*5,
            dropProbability
        );
    }

    @Override
    public boolean onInteract(PlayerInteractEvent e, Ally ally) {
        return this.massHeal(e.getPlayer(), ally);
    }
    
    @Override
    public boolean onDamageOther(EntityDamageByEntityEvent e, Ally ally) {
        return this.massHeal((Player) e.getDamager(), ally);
    }
    
    @Override
    public boolean onInteractEntity(PlayerInteractEntityEvent e, Ally ally) {
        return this.massHeal(e.getPlayer(), ally);
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
    
    private boolean massHeal(Player caster, Ally ally) {
        int distance = (ally.getLevel() + 5) * (ally.getPrimaryClass() == this.getClassType() ? 2 : 1);
        int maxHeals = (ally.getLevel() + 3) * (ally.getPrimaryClass() == this.getClassType() ? 2 : 1);
        
        int healed = 0;
        int harmed = 0;
        
        for(Entity entity : ally.getPet().getCraftPet().getNearbyEntities(distance, distance, distance)) {
            if(entity instanceof LivingEntity) {
                LivingEntity le = (LivingEntity) entity;
                
                if(this.isUndead(le)) {
                    this.heal(caster, ally, (LivingEntity) entity);
                    harmed++;
                }
                else {
                    this.heal(caster, ally, (LivingEntity) entity);
                    healed++;
                }
                
                if(harmed + healed > maxHeals) {
                    break;
                }
            }
        }
        
        if(healed > 0) {
            this.send(caster,ally,"healed "+healed+" targets!");
        }
        if(harmed > 0) {
            this.send(caster,ally,"harmed "+harmed+" targets!");  
        }
        
        if(healed == 0 && harmed == 0) {
            this.sendError(caster,ally,"didn't see any targets to heal!");
        }
        
        return true;
    }
    
    private boolean heal(Player caster, Ally ally, LivingEntity target) {
        if(target.getHealth() >= target.getMaxHealth() && !this.isUndead(target)) {
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
        }
        else {
            double damage = target.getHealth() - healAmount;
            
            target.damage(damage,ally.getPet().getCraftPet());
            
            target.getLocation().getWorld()
                    .playEffect(target.getLocation(), Effect.MAGIC_CRIT, 1, 1);
            
            this.send(caster, ally, "harmed a target with healing energy!");
        }

        return true;
    }
}
