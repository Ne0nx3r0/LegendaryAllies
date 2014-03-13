package com.ne0nx3r0.legendaryallies.ally.skills;

import com.ne0nx3r0.legendaryallies.LegendaryAlliesPlugin;
import com.ne0nx3r0.legendaryallies.ally.Ally;
import com.ne0nx3r0.legendaryallies.ally.AllyClassType;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class AllySkillsManager {
    private final LegendaryAlliesPlugin plugin;
    private final EnumMap<AllySkillType,AllySkill> skills;
    private final HashMap<String, Map<AllySkillType, Long>> cooldowns;

    public AllySkillsManager(LegendaryAlliesPlugin plugin) {
        this.plugin = plugin;
        
        this.skills = new EnumMap<>(AllySkillType.class);
        this.addSkill(new Swiftness());
        this.addSkill(new Fireball());
        this.addSkill(new Teleport());
        
        this.cooldowns = new HashMap<>();
        
        final AllySkillsManager skillsManager = this;
        
        // remove old cooldowns periodically
        plugin.getServer().getScheduler().runTaskTimer(plugin,new Runnable(){
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();

                Iterator<Map.Entry<String, Map<AllySkillType, Long>>> iter = skillsManager.cooldowns.entrySet().iterator();
                
                while (iter.hasNext()) {
                    Map.Entry<String, Map<AllySkillType, Long>> entry = iter.next();
                    
                    Iterator<Map.Entry<AllySkillType, Long>> playerIter = entry.getValue().entrySet().iterator();
                    
                    while (playerIter.hasNext()) {
                        Map.Entry<AllySkillType, Long> playerEntry = playerIter.next();
                        
                        if(playerEntry.getValue() < currentTime) {
                            playerIter.remove();
                        }
                    }
                    
                    if(entry.getValue().isEmpty()) {
                        iter.remove();
                    }
                }
            }
        },20*60,20*60);
    }
    
    public void setCooldown(String playerName,AllySkill skill) {
        Map<AllySkillType,Long> playerCooldowns = this.cooldowns.get(playerName);
        
        if(playerCooldowns == null) {
            playerCooldowns = new EnumMap<>(AllySkillType.class);
            
            this.cooldowns.put(playerName,playerCooldowns);
        }
        
        playerCooldowns.put(skill.getType(), System.currentTimeMillis() + skill.getCooldownSeconds()*1000);
    }
    
    public boolean hasCooldown(String playerName,AllySkill skill) {
        Map<AllySkillType,Long> playerCooldowns = this.cooldowns.get(playerName);
        
        if(playerCooldowns == null) {
            return false;
        }
        
        Long cooldown = playerCooldowns.get(skill.getType());
        
        return cooldown != null && cooldown > System.currentTimeMillis();
    }

    public long getCooldownSecondsRemaining(String playerName, AllySkill skill) {
        Map<AllySkillType,Long> playerCooldowns = this.cooldowns.get(playerName);
        
        if(playerCooldowns == null) {
            return 0;
        }
        
        Long cooldown = playerCooldowns.get(skill.getType());
        
        long currenTime = System.currentTimeMillis();
        
        if(cooldown == null || cooldown < currenTime) {
            return 0;
        }
        
        return (cooldown - currenTime)/1000;
    }
    
    public AllySkill getSkillFromType(AllySkillType type) {
        return this.skills.get(type);
    }

    private void addSkill(AllySkill skill) {
        AllySkillType type = skill.getType();
        
        this.skills.put(type, skill);
    }

    private final String SKILL_DISPLAY_NAME          = ChatColor.GREEN+"%s";
    private final String SKILL_LORE_NAME          = ChatColor.GRAY+"Skill Disk: "+ChatColor.WHITE+"%s";
    private final String SKILL_LORE_DESCRIPTION   = ChatColor.WHITE+"%s";
    
    public ItemStack createSkillDiskItem(AllySkill skill) {
        ItemStack is = new ItemStack(this.getSkillMaterial(skill.getClassType()));
        
        ItemMeta meta = is.getItemMeta();
        
        meta.setDisplayName(String.format(SKILL_DISPLAY_NAME,new Object[]{
            skill.getName()
        }));
        
        List<String> lore = new ArrayList<>();
        
        lore.add(String.format(SKILL_LORE_NAME,new Object[]{
            skill.getName()
        }));
        
        for(String descriptionLine : SKILL_LORE_DESCRIPTION.split("\n")) {
            lore.add(String.format(descriptionLine,new Object[]{
                skill.getDescription()
            }));
        }
        
        meta.setLore(lore);
        
        is.setItemMeta(meta);
        
        return is;
    }

    private Material getSkillMaterial(AllyClassType type) {
        switch(type) {
            default:
            case Unknown: 
                return Material.RECORD_3;
            case Warrior: 
                return Material.RECORD_10;
            case Nether: 
                return Material.RECORD_6;
            case Fire: 
                return Material.RECORD_4;
            case Mage: 
                return Material.RECORD_7;
            case Undead: 
                return Material.RECORD_8;
            case Support: 
                return Material.RECORD_9;
            case Carpenter: 
                return Material.RECORD_3;
            case Aqua: 
                return Material.RECORD_12;
            case Alchemist: 
                return Material.RECORD_5;
            case Farmer: 
                return Material.RECORD_3;
            case Wild: 
                return Material.RECORD_10;
            case Flying: 
                return Material.RECORD_12;
        }
    }

    public AllySkill getSkillFromSkillDisk(ItemStack is) {
        if(is.hasItemMeta()) {
            ItemMeta meta = is.getItemMeta();
            
            if(meta.getLore() != null && !meta.getLore().isEmpty()) {
                String loreId = meta.getLore().get(0);

                if(loreId.startsWith(String.format(SKILL_LORE_NAME,""))) {
                    String sID = loreId.substring(loreId.lastIndexOf(ChatColor.WHITE.toString()) + 2);
                    
                    return this.skills.get(AllySkillType.valueOf(sID));
                }
            }
        }
        
        return null;
    }
}
