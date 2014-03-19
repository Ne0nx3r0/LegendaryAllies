

package com.ne0nx3r0.legendaryallies.ally.skills;

import com.ne0nx3r0.legendaryallies.ally.Ally;
import com.ne0nx3r0.legendaryallies.ally.AllyClassType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class Midnight extends AllySkill {
    public Midnight(int dropProbability) {
        super(
            AllySkillType.Midnight,
            AllyClassType.Mage,
            "Midnight","Sets the time to midnight",
            60*60*12,
            dropProbability
        );
    }

    @Override
    public boolean onInteract(PlayerInteractEvent e, Ally ally) {
        return this.setToMidnight(e.getPlayer(), ally);
    }
    
    @Override
    public boolean onDamageOther(EntityDamageByEntityEvent e, Ally ally) {
        return this.setToMidnight((Player) e.getDamager(), ally);
    }
    
    @Override
    public boolean onInteractEntity(PlayerInteractEntityEvent e, Ally ally) {
        return this.setToMidnight(e.getPlayer(), ally);
    }
    
    public boolean setToMidnight(Player player, Ally ally) {
        player.getWorld().setTime(18000);
        
        for(Player p : player.getWorld().getPlayers()) {
            p.sendMessage(ChatColor.GRAY+p.getName()+"'s ally "+ally.getName()+" has set the time to midnight!");
        }
        
        this.send(player,ally,"set the time to midnight!");
        
        return true;
    }
}
