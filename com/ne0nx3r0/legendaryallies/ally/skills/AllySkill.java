package com.ne0nx3r0.legendaryallies.ally.skills;

import com.ne0nx3r0.legendaryallies.ally.Ally;
import com.ne0nx3r0.legendaryallies.ally.AllyClassType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class AllySkill {
    private final AllySkillType skillType;
    private final String name;
    private final String description;
    final int cooldown;
    private final AllyClassType classType;
    private final int dropProbability;

    public AllySkill(AllySkillType skillType,AllyClassType classType,String name,String description,int cooldownSeconds,int dropProbability) {
        this.skillType = skillType;
        this.classType = classType;
        this.name = name;
        this.description = description;
        this.cooldown = cooldownSeconds;
        this.dropProbability = dropProbability;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public int getCooldownSeconds(Ally ally) {
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
    
    private final String MESSAGE_FORMAT = ChatColor.GREEN+"%s "+ChatColor.GRAY+"%s";
    
    void send(Player player,Ally ally,String message) {
        player.sendMessage(String.format(MESSAGE_FORMAT,new Object[]{
            ally.getName(),
            message
        }));
    }
    
    void sendError(Player player,Ally ally,String message) {
        this.send(player,ally,ChatColor.RED+message);
    }
    
    public int getDropProbability() {
        return this.dropProbability;
    }

    public int getSkillID() {
        return this.skillType.getSkillId();
    }
}
