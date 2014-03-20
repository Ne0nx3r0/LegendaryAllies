package com.ne0nx3r0.legendaryallies.listeners;

import com.ne0nx3r0.legendaryallies.LegendaryAlliesPlugin;
import com.ne0nx3r0.legendaryallies.ally.Ally;
import com.ne0nx3r0.legendaryallies.ally.items.CommonCandy;
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
                if(!plugin.allyManager.hasActiveAlly(e.getPlayer()) || plugin.allyManager.getActiveAlly(e.getPlayer().getName()) != ally) {
                    e.getPlayer().sendMessage(ChatColor.GRAY+"Summoning "+ChatColor.GREEN+ally.getName()+ChatColor.GRAY+"!");
                    
                    if(plugin.allyManager.itemStackHasBeenRenamed(e.getItem(), ally)) {
                        String newName = ChatColor.stripColor(e.getItem().getItemMeta().getDisplayName());
                        
                        e.getPlayer().sendMessage(ChatColor.GRAY+"Updating "+ChatColor.GREEN+ally.getName()+ChatColor.GRAY+"'s name to "+ChatColor.GREEN+newName+"!");
                        
                        ally.setName(newName);

                        plugin.allyManager.setAllyItemMetaData(e.getItem(), ally);
                    }
                    
                    plugin.allyManager.summonAlly(e.getPlayer(),ally);
                }
                else if(e.getPlayer().isSneaking()) {
                    e.getPlayer().sendMessage(ChatColor.GRAY+"Unsummoning "+ChatColor.GREEN+ally.getName()+ChatColor.GRAY+"!");
                    
                    // Save pet data
                    ally.setPetData(ally.getPet().getPetData());
                    
                    plugin.allyManager.unSummonAlly(e.getPlayer());
                }
                else if((e.getAction().equals(Action.RIGHT_CLICK_AIR) 
                    ||   e.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {
                    if(ally.getSecondarySkill() != null) {
                        // activate alternate skill
                        AllySkill skill = ally.getSecondarySkill();

                        long remainingTime = plugin.skillsManager.getCooldownSecondsRemaining(e.getPlayer().getName(),skill);

                        if(remainingTime == 0) {
                            if(!e.isCancelled() && skill.onInteract(e,ally) && !e.isCancelled()) {
                                plugin.skillsManager.setCooldown(e.getPlayer().getName(), ally, skill);
                            }
                        }
                        else {
                            e.getPlayer().sendMessage(String.format(ChatColor.RED+"You must wait %s more seconds!",remainingTime));
                        }
                    }
                }
                else if(ally.getPrimarySkill() != null){
                    // activate primary skill
                    AllySkill skill = ally.getPrimarySkill();
                    
                    long remainingTime = plugin.skillsManager.getCooldownSecondsRemaining(e.getPlayer().getName(),skill);
                    
                    if(remainingTime == 0) {
                        if(!e.isCancelled() && skill.onInteract(e,ally)) {
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
                
                e.setCancelled(true);
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
        if(!e.isCancelled() && e.getPlayer().getItemInHand() != null) {
            Ally ally = plugin.allyManager.getAllyFromSummoningItem(e.getPlayer().getItemInHand());

            if(ally != null && plugin.allyManager.getActiveAlly(e.getPlayer().getName()) == ally) {
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
                
                e.setCancelled(true);
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
            else if(ally.getPrimarySkill() == null) {               
                ally.setPrimarySkill(skill);

                Player player = ((Player) e.getWhoClicked()); 

                player.sendMessage(ChatColor.GREEN+ally.getName()+ChatColor.GRAY+" learned "+ChatColor.WHITE+skill.getName()+ChatColor.GRAY+"! (left click to use)");

                plugin.allyManager.setAllyItemMetaData(e.getCurrentItem(), ally);

                e.setCursor(null);

                e.setCancelled(true);
            }
            else if(ally.getSecondarySkill() == null) {
                ally.setSecondarySkill(skill);

                Player player = ((Player) e.getWhoClicked()); 

                player.sendMessage(ally.getName()+" learned "+skill.getName()+"! (right click to use)");

                plugin.allyManager.setAllyItemMetaData(e.getCurrentItem(), ally);

                e.setCursor(null);

                e.setCancelled(true);
            }
            else {
                ((Player) e.getWhoClicked()).sendMessage(ChatColor.RED+ally.getName()+" cannot learn anymore skills!");
            }
        }
        else if(ally != null && CommonCandy.isCommonCandy(e.getCursor())) {
            Player player = ((Player) e.getWhoClicked()); 

            if(ally.getPrimarySkill() != null || ally.getSecondarySkill() != null ) {
                player.sendMessage(ChatColor.GREEN+ally.getName()+ChatColor.GRAY+" ate the "+ChatColor.GREEN+"Common Candy"+ChatColor.GRAY+"!");
                    
                if(ally.getPrimarySkill() != null) {
                    player.sendMessage(ChatColor.GREEN+ally.getName()+ChatColor.GRAY+" forgot "+ChatColor.GREEN+ally.getPrimarySkill().getName()+ChatColor.GRAY+"!");
                    
                    ally.setPrimarySkill(null);
                }
                if(ally.getSecondarySkill() != null) {
                    player.sendMessage(ChatColor.GREEN+ally.getName()+ChatColor.GRAY+" forgot "+ChatColor.GREEN+ally.getSecondarySkill().getName()+ChatColor.GRAY+"!");
                    
                    ally.setSecondarySkill(null);
                }

                plugin.allyManager.setAllyItemMetaData(e.getCurrentItem(), ally);

                e.setCursor(null);
            }
            else {
                player.sendMessage(ChatColor.RED+ally.getName()+" doesn't have any skills to forget!");
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
