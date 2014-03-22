package com.ne0nx3r0.legendaryallies.ally;

import com.ne0nx3r0.legendaryallies.LegendaryAlliesPlugin;
import com.ne0nx3r0.legendaryallies.ally.skills.AllySkill;
import com.ne0nx3r0.legendaryallies.ally.skills.AllySkillType;
import io.github.dsh105.echopet.entity.Pet;
import io.github.dsh105.echopet.entity.PetData;
import io.github.dsh105.echopet.entity.PetType;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class AllyManager {
    private final LegendaryAlliesPlugin plugin;
    private int cardinality;
    private final Map<Integer,Ally> allies;
    private Map<String,Ally> activeAllies;

    public AllyManager(LegendaryAlliesPlugin plugin) {
        this.plugin = plugin;
        
        this.activeAllies = new HashMap<>();
        
        this.allies = this.loadAllies();
        
        this.cardinality = 0;
            
        if(this.allies.size()> 0) {
            for(int id : this.allies.keySet()) {
                if(id > this.cardinality) {
                    this.cardinality = id;
                }
            }
        }
        
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new AllySaveFileTask(plugin), 1200, 1200);
    }
    
    public Ally createAlly(PetType type) {
        this.cardinality++;
        
        Ally ally = new Ally(this.cardinality,type);
        
        this.allies.put(ally.getAllyID(), ally);
        
        return ally;
    }
    
    private final String SUMMONER_DISPLAY_FORMAT      = ChatColor.GREEN+"%s";
    private final String SUMMONER_LORE_ID_FORMAT      = ChatColor.GRAY+"LMC ID: "+ChatColor.WHITE+"%s";
    private final String SUMMONER_LORE_LEVEL_FORMAT   = ChatColor.GRAY+"Level: "+ChatColor.WHITE+"%s "+ChatColor.GRAY+" - "+ChatColor.WHITE+"%sXP"+ChatColor.GRAY+" to level";
    private final String SUMMONER_LORE_TYPE_FORMAT    = ChatColor.GRAY+"Type: "+ChatColor.WHITE+"%s"+ChatColor.GRAY+", "+ChatColor.WHITE+"%s";
    private final String SUMMONER_LORE_SKILLS_FORMAT  = ChatColor.GRAY+"Skills: "+ChatColor.WHITE+"%s"+ChatColor.GRAY+", "+ChatColor.WHITE+"%s";
    private final String SUMMONER_LORE_HATCHED_FORMAT  = ChatColor.GRAY+"Hatched by: "+ChatColor.WHITE+"%s";
    
    public ItemStack createSummoningItem(Ally ally) {
        ItemStack is = new ItemStack(this.getAllyMaterial(ally.getPetType()),1,(short) 0,this.getEggId(ally.getPetType()));
 
        this.setAllyItemMetaData(is, ally);
        
        return is;
    }
    
    public boolean itemStackHasBeenRenamed(ItemStack is,Ally ally) {
        return !is.getItemMeta().getDisplayName().equals(String.format(SUMMONER_DISPLAY_FORMAT,new Object[]{ally.getName()}));
    }
    
    public void setAllyItemMetaData(ItemStack is,Ally ally) {
        ItemMeta meta = is.getItemMeta();

        meta.setDisplayName(String.format(SUMMONER_DISPLAY_FORMAT,new Object[]{
            ally.getName()
        }));
        
        List<String> lore = new ArrayList<>();
        
        lore.add(String.format(SUMMONER_LORE_ID_FORMAT,new Object[]{
            ally.getAllyID()
        }));
        
        lore.add(String.format(SUMMONER_LORE_LEVEL_FORMAT,new Object[]{
            ally.getXP(),
            ally.getLevel()
        }));
        
        lore.add(String.format(SUMMONER_LORE_TYPE_FORMAT,new Object[]{
            ally.getPrimaryClass(),
            ally.getSecondaryClass()
        }));
        
        lore.add(String.format(SUMMONER_LORE_SKILLS_FORMAT,new Object[]{
            ally.getPrimarySkill() != null ? ally.getPrimarySkill().getName() : "<empty>",
            ally.getSecondarySkill() != null ? ally.getSecondarySkill().getName() : "<empty>",
        }));
        
        if(ally.getHatchedBy() != null) {
            lore.add(String.format(SUMMONER_LORE_HATCHED_FORMAT,ally.getHatchedBy()));
        }
        
        meta.setLore(lore);
        
        is.setItemMeta(meta);
    }
    
    public Ally getAllyFromSummoningItem(ItemStack is) {
        if(is != null && is.hasItemMeta()) {
            ItemMeta meta = is.getItemMeta();
            
            if(meta.getLore() != null 
            && !meta.getLore().isEmpty()
            && meta.getLore().get(0).startsWith(String.format(SUMMONER_LORE_ID_FORMAT,""))) {
                String loreId = meta.getLore().get(0);

                String sID = loreId.substring(loreId.lastIndexOf(ChatColor.WHITE.toString()) + 2);
                
                int id = -1;
                
                try {
                    id = Integer.parseInt(sID);
                }
                catch(NumberFormatException ex) {
                    return null;
                }
                
                return this.allies.get(id);
            }
        }
        
        return null;
    }

    private Material getAllyMaterial(PetType petType) {
        // TODO: Something more creative for different allies after testing?
        return Material.MONSTER_EGG;
    }

    public int getCardinality() {
        return this.cardinality;
    }

    Map<Integer, Ally> getAllAllies() {
        return this.allies;
    }

    public void saveAllies() {
        Runnable run = new AllySaveFileTask(plugin);
        
        run.run();
    }

    public void setCardinality(int cardinality) {
        this.cardinality = cardinality;
    }

    public void summonAlly(Player player, Ally ally) {      
        if(this.getActiveAlly(player.getName()) != null) {
            this.unSummonAlly(player);
        }
        
        Pet pet = plugin.petAPI.givePet(player, ally.getPetType(), false);

        pet.setPetName(ally.getName());
        
        if(ally.getPetData() != null) {
            for(PetData pd : ally.getPetData()) {
                plugin.petAPI.addData(pet, pd);
            }

            // in case any ordering messed things up
            for(PetData pd : ally.getPetData()) {
                plugin.petAPI.addData(pet, pd);
            }
        }
        
        ally.setPet(pet);
        
        if(ally.getHatchedBy() == null) {
            plugin.getLogger().log(Level.INFO, player.getName()+" hatched LMCID"+ally.getAllyID());
            
            ally.setHatchedBy(player.getName());
        }
        
        ally.setLastSummonedBy(player.getName());

        this.activeAllies.put(player.getName(), ally);
        
        if(ally.getPrimarySkill() != null) {
            plugin.skillsManager.setCooldown(player.getName(), ally, ally.getPrimarySkill(),2);
        }
        if(ally.getSecondarySkill() != null) {
            plugin.skillsManager.setCooldown(player.getName(), ally, ally.getSecondarySkill(),2);
        }
        
        plugin.getLogger().log(Level.INFO, "{0} summoned {1} LMCID{2}", new Object[]{player.getName(), ally.getName(), ally.getAllyID()});
    }

    public Ally getActiveAlly(String playerName) {
        return this.activeAllies.get(playerName);
    }

    public void unSummonAlly(Player player) {
        Ally ally = this.activeAllies.remove(player.getName());
        
        if(ally != null) {
            ally.setPet(null);
        }
        
        plugin.petAPI.removePet(player, false, true);        
    }

    public boolean hasActiveAlly(Player player) {
        return plugin.petAPI.hasPet(player);
    }

    public Map<Integer, Ally> loadAllies() {
        Map<Integer, Ally> loadedAllies = new HashMap<>();
        
        File saveFile = new File(plugin.getDataFolder(),"allies.yml");
        
        YamlConfiguration saveYml = YamlConfiguration.loadConfiguration(saveFile);
        
        if(saveYml.isSet("allies"))
        {     
            for(Map<String,Object> tempAlly : (List<Map<String,Object>>) saveYml.get("allies"))
            {    
                int allyId = (int) tempAlly.get("id");
                PetType petType = PetType.valueOf((String) tempAlly.get("echoPetType"));
                String name = (String) tempAlly.get("name");
                int xp = (int) tempAlly.get("xp");
                int hp = (int) tempAlly.get("hp");
                int attackPower = (int) tempAlly.get("attackPower");
                int defense = (int) tempAlly.get("defense");
                
                AllySkill primarySkill = null; 
                try {
                    if(tempAlly.containsKey("primarySkill")) {
                        primarySkill = plugin.skillsManager.getSkillFromType(AllySkillType.valueOf((int) tempAlly.get("primarySkill")));
                    }
                }
                catch(Exception e) {
                    System.out.println("Invalid ally skill:"+(String) tempAlly.get("primarySkill"));
                }
                
                AllySkill secondarySkill = null;
                
                try {
                    if(tempAlly.containsKey("secondarySkill")) {
                        secondarySkill = plugin.skillsManager.getSkillFromType(AllySkillType.valueOf((int) tempAlly.get("secondarySkill")));
                    }
                }
                catch(Exception e) {
                    System.out.println("Invalid ally skill:"+(String) tempAlly.get("secondarySkill"));
                }
                
                ArrayList<PetData> petData = null;
                if(tempAlly.containsKey("petData")) {
                    ArrayList<String> tempPetData = (ArrayList<String>) tempAlly.get("petData");
                    petData = new ArrayList<>();
                    
                    for(String sPetData : tempPetData) {
                        petData.add(PetData.valueOf(sPetData));
                    }
                }
                
                String hatchedBy = null;
                
                if(tempAlly.containsKey("hatchedBy")) {
                    hatchedBy = (String) tempAlly.get("hatchedBy");
                }
                
                String lastSummonedby = null;
                
                if(tempAlly.containsKey("lastSummonedby")) {
                    lastSummonedby = (String) tempAlly.get("lastSummonedby");
                }

                loadedAllies.put(allyId,new Ally(allyId,petType,name,xp,hp,attackPower,defense,primarySkill,secondarySkill,petData,hatchedBy,lastSummonedby));
            }
        }
        
        if(saveYml.isSet("cardinality")) {
            this.setCardinality(Integer.parseInt(saveYml.getString("cardinality")));
        }
        
        return loadedAllies;
    }

    public Ally getAlly(int allyId) {
        return this.allies.get(allyId);
    }

    public void removeAlly(Ally ally) {
        this.allies.remove(ally.getAllyID());
    }
    
    public byte getEggId(PetType type) {
        switch(type) {
            default: 
                return 0;
            case BAT: 
                return 65;
            case BLAZE: 
                return 61;
            case CAVESPIDER: 
                return 59;
            case CHICKEN: 
                return 93;
            case COW: 
                return 92;
            case CREEPER: 
                return 50;
            case ENDERMAN: 
                return 58;
            case ENDERDRAGON: 
                return 58;
            case GHAST: 
                return 56;
            case IRONGOLEM: 
                return 95;
            case MAGMACUBE: 
                return 62;
            case MUSHROOMCOW: 
                return 96;
            case OCELOT: 
                return 98;
            case PIG: 
                return 90;
            case PIGZOMBIE: 
                return 57;
            case SHEEP: 
                return 91;
            case SILVERFISH: 
                return 60;
            case SKELETON: 
                return 51;
            case SLIME: 
                return 55;
            case SNOWMAN: 
                return 56;
            case SPIDER: 
                return 52;
            case SQUID: 
                return 94;
            case VILLAGER: 
                return 120;
            case WITCH: 
                return 66;
            case WITHER: 
                return 58;
            case WOLF: 
                return 95;
            case ZOMBIE: 
                return 54;
            case HORSE:
                return 100;
        }
    }
}
