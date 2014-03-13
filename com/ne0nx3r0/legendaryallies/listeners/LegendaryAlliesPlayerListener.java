package com.ne0nx3r0.legendaryallies.listeners;

import com.ne0nx3r0.legendaryallies.LegendaryAlliesPlugin;
import com.ne0nx3r0.legendaryallies.ally.Ally;
import com.ne0nx3r0.legendaryallies.ally.skills.AllySkill;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class LegendaryAlliesPlayerListener implements Listener {
    private final LegendaryAlliesPlugin plugin;

    public LegendaryAlliesPlayerListener(LegendaryAlliesPlugin plugin) {
        this.plugin = plugin;
    }
    
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if(e.hasItem()) {
            Ally ally = plugin.allyManager.getAllyFromSummoningItem(e.getItem());

            if(ally != null) {
                e.setCancelled(true);

                if(!plugin.allyManager.hasActiveAlly(e.getPlayer()) || plugin.allyManager.getActiveAlly(e.getPlayer().getName()) != ally) {
                    e.getPlayer().sendMessage(ChatColor.GRAY+"Summoning "+ChatColor.GREEN+ally.getName()+ChatColor.GRAY+"!");
                    
                    plugin.allyManager.summonAlly(e.getPlayer(),ally);
                }
                else if(e.getPlayer().isSneaking()) {
                    e.getPlayer().sendMessage(ChatColor.GRAY+"Unsummoning "+ChatColor.GREEN+ally.getName()+ChatColor.GRAY+"!");
                    
                    plugin.allyManager.unSummonAlly(e.getPlayer());
                }
                else if((e.getAction().equals(Action.RIGHT_CLICK_AIR) 
                     || e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
                     && ally.getSecondarySkill() != null) {
                    // activate alternate skill
                    AllySkill skill = ally.getSecondarySkill();

                    long remainingTime = plugin.skillsManager.getCooldownSecondsRemaining(e.getPlayer().getName(),skill);

                    if(remainingTime == 0) {
                        if(skill.onInteract(e,ally)) {
                            plugin.skillsManager.setCooldown(e.getPlayer().getName(), ally, skill);
                        }
                    }
                    else {
                        e.getPlayer().sendMessage(String.format(ChatColor.RED+"You must wait %s more seconds!",remainingTime));
                    }
                }
                else if(ally.getPrimarySkill() != null){
                    // activate primary skill
                    AllySkill skill = ally.getPrimarySkill();
                    
                    long remainingTime = plugin.skillsManager.getCooldownSecondsRemaining(e.getPlayer().getName(),skill);
                    
                    if(remainingTime == 0) {
                        if(skill.onInteract(e,ally)) {
                            plugin.skillsManager.setCooldown(e.getPlayer().getName(), ally,  skill);
                        }
                    }
                    else {
                        e.getPlayer().sendMessage(String.format(ChatColor.RED+"You must wait %s more seconds!",remainingTime));
                    }
                }
                else {
                    e.getPlayer().sendMessage(ChatColor.RED+"No skill available");
                }
            }
        }
    }
    
    @EventHandler
    public void onPlayerAttack(EntityDamageByEntityEvent e) {
        if(e.getDamager() instanceof Player) {
            Player player = (Player) e.getDamager();
            
            if(player.getItemInHand() != null) {
                Ally ally = plugin.allyManager.getAllyFromSummoningItem(player.getItemInHand());

                if(ally != null && plugin.allyManager.getActiveAlly(player.getName()) == ally) {
                    e.setCancelled(true);
                    
                    if(ally.getPrimarySkill() != null) {
                        AllySkill skill = ally.getPrimarySkill();

                        long remainingTime = plugin.skillsManager.getCooldownSecondsRemaining(player.getName(),skill);

                        if(remainingTime == 0) {
                            if(skill.onDamageOther(e, ally)) { 
                                plugin.skillsManager.setCooldown(player.getName(), ally, skill);
                            }
                        }
                        else {
                            player.sendMessage(String.format(ChatColor.RED+"You must wait %s more seconds!",remainingTime));
                        }
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onPlayerInteractWithEntity(PlayerInteractEntityEvent e) {
        if(e.getPlayer().getItemInHand() != null) {
            Ally ally = plugin.allyManager.getAllyFromSummoningItem(e.getPlayer().getItemInHand());

            if(ally != null && plugin.allyManager.getActiveAlly(e.getPlayer().getName()) == ally) {
                e.setCancelled(true);

                if(ally.getSecondarySkill() != null) {
                    AllySkill skill = ally.getSecondarySkill();
                    
                    long remainingTime = plugin.skillsManager.getCooldownSecondsRemaining(e.getPlayer().getName(),skill);
                    
                    if(remainingTime == 0) {
                        plugin.skillsManager.setCooldown(e.getPlayer().getName(), ally, skill);
                        
                        skill.onInteractEntity(e, ally);
                    }
                    else {
                        e.getPlayer().sendMessage(String.format(ChatColor.RED+"You must wait %s more seconds!",remainingTime));
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onPlayerClick(InventoryClickEvent e) {
        AllySkill skill = plugin.skillsManager.getSkillFromSkillDisk(e.getCursor());
        Ally ally = plugin.allyManager.getAllyFromSummoningItem(e.getCurrentItem());
        
        if(skill != null && ally != null){
            if(!skill.getClassType().equals(ally.getPrimaryClass()) 
            && !skill.getClassType().equals(ally.getSecondaryClass())) {
                ((Player) e.getWhoClicked()).sendMessage(ChatColor.RED+ally.getName()+" cannot learn "+skill.getName()+"!");
            }
            else if(e.getClick().isLeftClick()) {
                if(ally.getPrimarySkill() != null) {
                    ((Player) e.getWhoClicked()).sendMessage(ChatColor.RED+ally.getName()+" already has a primary skill!");
                }
                else {                    
                    ally.setPrimarySkill(skill);

                    Player player = ((Player) e.getWhoClicked()); 
                    
                    player.sendMessage(ally.getName()+" learned "+skill.getName()+"!");

                    plugin.allyManager.setAllyItemStackLoreValues(e.getCurrentItem(), ally);
                    
                    e.setCursor(null);
                }
            }
            else if(e.getClick().isRightClick()) {
                if(ally.getSecondarySkill() != null) {
                    ((Player) e.getWhoClicked()).sendMessage(ChatColor.RED+ally.getName()+" already has a secondary skill!");
                }
                else {
                    ally.setSecondarySkill(skill);

                    Player player = ((Player) e.getWhoClicked()); 
                    
                    player.sendMessage(ally.getName()+" learned "+skill.getName()+"!");

                    plugin.allyManager.setAllyItemStackLoreValues(e.getCurrentItem(), ally);
                    
                    e.setCursor(null);
                }
            }
        }
    }
    
    
    @EventHandler
    public void onPlayerDropAlly(PlayerDropItemEvent e) {
        Ally ally = plugin.allyManager.getAllyFromSummoningItem(e.getItemDrop().getItemStack());
        
        if(ally != null) {
            Ally activeAlly = plugin.allyManager.getActiveAlly(e.getPlayer().getName());
            
            if(activeAlly != null && activeAlly == ally) {
                e.getPlayer().sendMessage("Unsummoning "+activeAlly.getName()+"!");
                
                plugin.allyManager.unSummonAlly(e.getPlayer());
            }
        }
    }
}
