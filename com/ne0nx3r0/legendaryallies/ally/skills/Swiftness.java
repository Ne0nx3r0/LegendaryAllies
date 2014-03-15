package com.ne0nx3r0.legendaryallies.ally.skills;

import com.ne0nx3r0.legendaryallies.ally.Ally;
import com.ne0nx3r0.legendaryallies.ally.AllyClassType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Swiftness extends AllySkill {
    public Swiftness(int dropProbability) {
        super(
            AllySkillType.Swiftness,
            AllyClassType.Wild,
            "Swiftness",
            "Ally moves faster, applies to riding as well",
            30,
            dropProbability
        );
    }

    @Override
    public boolean onInteract(PlayerInteractEvent e, Ally ally) {
        return this.castSwiftness(e.getPlayer(), ally, e.getPlayer());
    }
    
    @Override
    public boolean onDamageOther(EntityDamageByEntityEvent e, Ally ally) {
        if(e.getEntity() instanceof LivingEntity) {
            return this.castSwiftness((Player) e.getDamager(), ally, (LivingEntity) e.getEntity());
        }
        
        return false;
    }
    
    @Override
    public boolean onInteractEntity(PlayerInteractEntityEvent e, Ally ally) {
        if(e.getRightClicked() instanceof LivingEntity) {
            return this.castSwiftness(e.getPlayer(), ally, (LivingEntity) e.getRightClicked());
        }
        
        return false;
    }

    private boolean castSwiftness(Player caster, Ally ally, LivingEntity target) {
        int duration = 20 * 10 * (ally.getLevel() + 1);
        
        if(ally.getPrimaryClass() == this.getClassType()) {
            duration = duration * 2;
        }
        
        int level = ally.getLevel() + 1;
        
        if(!target.hasPotionEffect(PotionEffectType.SPEED)) {
            target.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,duration,level));
            
            if(caster.equals(target)) {
                this.send(caster,ally,"used "+this.getName()+" on you!");
            }
            else {
                if(target instanceof Player) {
                    Player player = (Player) target;
                    
                    this.send(player, ally, "used "+this.getName()+" on you!");
                    this.send(caster, ally, "used "+this.getName()+" on "+player.getDisplayName()+"!");
                }
                else {
                    this.send(caster, ally, "used "+this.getName()+" on the target!");
                }
            }
            
            return true;
        }

        if(caster.equals(target)) {
            this.sendError(caster, ally, "thinks you are fast already!");
        }
        else{
            this.sendError(caster, ally, "thinks that target is fast already!");
        }
        
        return false;
    }
}
