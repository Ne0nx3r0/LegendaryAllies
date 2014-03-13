package com.ne0nx3r0.legendaryallies.ally.skills;

import com.ne0nx3r0.legendaryallies.ally.Ally;
import com.ne0nx3r0.legendaryallies.ally.AllyClassType;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class AllySkill {
    private final AllySkillType skillType;
    private final String name;
    private final String description;
    private final int cooldown;
    private final AllyClassType classType;

    public AllySkill(AllySkillType skillType,AllyClassType classType,String name,String description,int cooldownSeconds) {
        this.skillType = skillType;
        this.classType = classType;
        this.name = name;
        this.description = description;
        this.cooldown = cooldownSeconds;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public int getCooldownSeconds() {
        return this.cooldown;
    }
    
    public AllySkillType getType() {
        return this.skillType;
    }
    
    public AllyClassType getClassType() {
        return this.classType;
    }

    public boolean onInteract(PlayerInteractEvent e, Ally ally) {return false;}
    
    public boolean onInteractEntity(PlayerInteractEntityEvent e, Ally ally) {return false;}

    public boolean onDamageOther(EntityDamageByEntityEvent e, Ally ally) {return false;}
    
    private final String MESSAGE_FORMAT = ChatColor.GRAY+"["+ChatColor.RESET+"%s"+ChatColor.GRAY+"]"+ChatColor.RESET+" %s";
    
    void send(Player player,String message) {
        player.sendMessage(String.format(MESSAGE_FORMAT,new Object[]{
            this.getName(),
            message
        }));
    }
    
    void sendError(Player player,String message) {
        this.send(player,ChatColor.RED+message);
    }
}
