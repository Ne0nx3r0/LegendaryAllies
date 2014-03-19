

package com.ne0nx3r0.legendaryallies.ally.skills;

import com.ne0nx3r0.legendaryallies.ally.Ally;
import com.ne0nx3r0.legendaryallies.ally.AllyClassType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class Twilight extends AllySkill {
    public Twilight(int dropProbability) {
        super(
            AllySkillType.Twilight,
            AllyClassType.Mage,
            "Twilight","Sets the time to early morning",
            60*60*6,
            dropProbability
        );
    }

    @Override
    public boolean onInteract(PlayerInteractEvent e, Ally ally) {
        return this.setToMorning(e.getPlayer(), ally);
    }
    
    @Override
    public boolean onDamageOther(EntityDamageByEntityEvent e, Ally ally) {
        return this.setToMorning((Player) e.getDamager(), ally);
    }
    
    @Override
    public boolean onInteractEntity(PlayerInteractEntityEvent e, Ally ally) {
        return this.setToMorning(e.getPlayer(), ally);
    }
    
    public boolean setToMorning(Player player, Ally ally) {
        player.getWorld().setTime(22000);
        
        for(Player p : player.getWorld().getPlayers()) {
            p.sendMessage(ChatColor.GRAY+p.getName()+"'s ally "+ally.getName()+" has set the time to early morning!");
        }
        
        this.send(player,ally,"set the time to morning!");
        
        return true;
    }
}
