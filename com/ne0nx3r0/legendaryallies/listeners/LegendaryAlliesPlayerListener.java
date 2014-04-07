package com.ne0nx3r0.legendaryallies.listeners;

import com.ne0nx3r0.legendaryallies.LegendaryAlliesPlugin;
import com.ne0nx3r0.legendaryallies.ally.Ally;
import com.ne0nx3r0.legendaryallies.ally.items.CommonCandy;
import com.ne0nx3r0.legendaryallies.ally.skills.AllySkill;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.inventory.ItemStack;

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
                            if(!e.isCancelled() && skill.onInteract(e,ally)) {
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
            
            // Check if they are breaking an end portal frame with an eye of ender and break it if they are
            else if(!e.isCancelled() 
                  && e.getItem().getType().equals(Material.EYE_OF_ENDER) 
                  && e.getClickedBlock().getType().equals(Material.ENDER_PORTAL_FRAME))
            {
                Block endPortalFrame = e.getClickedBlock();
                byte data = endPortalFrame.getData();
                byte[] activeBfs = new byte[]{4,5,6,7};
                
                if(e.getAction().equals(Action.LEFT_CLICK_BLOCK) && e.getPlayer().getGameMode().equals(GameMode.SURVIVAL)) {
                    for(byte b : activeBfs) {
                        if(data == b) {
                            endPortalFrame.getWorld().dropItemNaturally(endPortalFrame.getLocation(), new ItemStack(Material.EYE_OF_ENDER));
                        }
                    }

                    endPortalFrame.getWorld().dropItemNaturally(endPortalFrame.getLocation(), new ItemStack(Material.ENDER_PORTAL_FRAME));

                    endPortalFrame.breakNaturally();

                    Location lCornerPiece = this.getPortalCorner(endPortalFrame.getLocation(),true);
                    
                    if(lCornerPiece != null) {
                        this.setEndPortal(lCornerPiece, false);
                    }

                    e.setCancelled(true);
                }
                else if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                    boolean activating_end_portal = true;
                    
                    for(byte b : activeBfs) {
                        if(data == b) {
                            activating_end_portal = false;
                        }
                    }
                    
                    if(activating_end_portal){
                        Location lCornerPiece = this.getPortalCorner(endPortalFrame.getLocation(),false);

                        if(lCornerPiece != null) {
                            this.setEndPortal(lCornerPiece, true);
                        }
                    }
                }
            }
        }
    }
    
    private Location getPortalCorner(Location lFramePiece,boolean activePortal) {
        int[][] values = new int[12][2];
        values[0]  = new int[]{0,-1};
        values[1]  = new int[]{0,-2};
        values[2]  = new int[]{0,-3};
        values[3]  = new int[]{-1,0};
        values[4]  = new int[]{-2,0};
        values[5]  = new int[]{-3,0};
        values[6]  = new int[]{-4,-1};
        values[7]  = new int[]{-4,-2};
        values[8]  = new int[]{-4,-3};
        values[9]  = new int[]{-1,-4};
        values[10] = new int[]{-2,-4};
        values[11] = new int[]{-3,-4};
        
        for(int v=0; v<values.length; v++) {
            Location lStart = lFramePiece.clone().add(values[v][0], 0, values[v][1]);

            if(this.isPortalCorner(lStart,lFramePiece,activePortal)){
                return lStart;
            }
        }
        
        return null;
    }
    
    private boolean isPortalCorner(Location lCorner,Location lClickedPiece,boolean portalIsActive) {
        byte[] activeDatas = new byte[]{4,5,6,7};
        
        Material m[][] = new Material[][] {
            new Material[]{null,Material.ENDER_PORTAL_FRAME,Material.ENDER_PORTAL_FRAME,Material.ENDER_PORTAL_FRAME,null},
            new Material[]{Material.ENDER_PORTAL_FRAME,Material.AIR,Material.AIR,Material.AIR,Material.ENDER_PORTAL_FRAME},
            new Material[]{Material.ENDER_PORTAL_FRAME,Material.AIR,Material.AIR,Material.AIR,Material.ENDER_PORTAL_FRAME},
            new Material[]{Material.ENDER_PORTAL_FRAME,Material.AIR,Material.AIR,Material.AIR,Material.ENDER_PORTAL_FRAME},
            new Material[]{null,Material.ENDER_PORTAL_FRAME,Material.ENDER_PORTAL_FRAME,Material.ENDER_PORTAL_FRAME,null}
        };
        
        for(int x=0;x<m.length;x++){
            for(int z=0;z<m[0].length;z++){                
                Location lCurrent = lCorner.clone().add(x, 0, z);
                Material mCurrent = lCurrent.getBlock().getType();

                if(m[x][z] != null){
                    // looking for an active portal, so could be air or ender_portal
                    if(portalIsActive && m[x][z].equals(Material.AIR) && mCurrent.equals(Material.ENDER_PORTAL)){
                        // continue on
                    }
                    // type doesn't match
                    else if(!mCurrent.equals(m[x][z])){
                        // if it's air and it's the clicked block let it go 
                        if(!(lCurrent.equals(lClickedPiece) && mCurrent.equals(Material.AIR))) {
                            return false;
                        }
                    }
                    // Type matched, but we need to check if the data is correct
                    else if(mCurrent.equals(Material.ENDER_PORTAL_FRAME)){
                        boolean isActivePiece = false;
                        
                        for(byte data : activeDatas){
                            if(lCurrent.getBlock().getData() == data){
                                isActivePiece = true;
                            }
                        }
                        
                        if(!portalIsActive && !isActivePiece && !lCurrent.equals(lClickedPiece)){
                            return false;
                        }
                    }
                }
            }
        }
        
        return true;
    }
    
    private void setEndPortal(Location lCornerPiece,boolean activated) {
        World world = lCornerPiece.getWorld();
        
        for(int x=1;x<4;x++){
            for(int z=1;z<4;z++){
                Block b = world.getBlockAt(lCornerPiece.getBlockX()+x, lCornerPiece.getBlockY(), lCornerPiece.getBlockZ()+z);
                if(activated) {
                    b.setType(Material.ENDER_PORTAL);
                }
                else {
                    b.setType(Material.AIR);
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
        if(!e.isCancelled() && e.getPlayer().getItemInHand() != null) {
            Ally ally = plugin.allyManager.getAllyFromSummoningItem(e.getPlayer().getItemInHand());

            if(ally != null && plugin.allyManager.getActiveAlly(e.getPlayer().getName()) == ally) {
                if(ally.getSecondarySkill() != null) {
                    AllySkill skill = ally.getSecondarySkill();
                    
                    long remainingTime = plugin.skillsManager.getCooldownSecondsRemaining(e.getPlayer().getName(),skill);
                    
                    if(remainingTime == 0) {
                        if(skill.onInteractEntity(e, ally)) {
                            plugin.skillsManager.setCooldown(e.getPlayer().getName(), ally, skill);
                
                            e.setCancelled(true);
                        }
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
    
    @EventHandler
    public static void onPortalTravel(PlayerPortalEvent e)
    {
        if(e.getTo().getWorld().getEnvironment().equals(Environment.THE_END))
        {            
            Location newTo = e.getFrom();
            
            newTo.setWorld(e.getTo().getWorld());
            
            e.setTo(newTo);
            
            e.setCancelled(true);
            
            e.getPlayer().teleport(newTo);
        }
    }
}
