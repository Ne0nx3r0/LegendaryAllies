package com.ne0nx3r0.legendaryallies.loot;

import com.ne0nx3r0.legendaryallies.LegendaryAlliesPlugin;
import com.ne0nx3r0.legendaryallies.ally.skills.AllySkill;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import java.util.logging.Level;
import lib.PatPeter.SQLibrary.SQLite;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class LootManager {
    private final LegendaryAlliesPlugin plugin;
    private final SQLite sqlite;
    private int skillsTotalProbability;

    public LootManager(LegendaryAlliesPlugin plugin) {
        this.plugin = plugin;
        
        this.sqlite = new SQLite(
            plugin.getLogger(),
            "LegendaryAllies",
            "LegendaryAllies",
            plugin.getDataFolder().getAbsolutePath()
        );
        
        try {
            sqlite.open();
        } 
        catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, null, ex);
            
            return;
        }
               
        if(!sqlite.checkTable("megachunks"))
        {
            sqlite.query("pragma journal_mode=wal");
            
            sqlite.query("CREATE TABLE megachunks("
                + "x INT,"             
                + "z INT"
            + ");"); 
            
            sqlite.query("CREATE UNIQUE INDEX megachunksIndex ON megachunks (x, z);");

            plugin.getLogger().log(Level.INFO, "megachunks table created");
        }
        
        // Probability test
        this.skillsTotalProbability = 0;
        
        for (AllySkill skill : plugin.skillsManager.getAllSkills()) {
            this.skillsTotalProbability += skill.getDropProbability();
        }
    }
    
    public AllySkill getWeightedRandomSkill() {
        
        int p = new Random().nextInt(this.skillsTotalProbability+1);
        int cumulativeProbability = 0;
        
        for (AllySkill skill : plugin.skillsManager.getAllSkills()) {
            
            cumulativeProbability += skill.getDropProbability();
            
            if (p <= cumulativeProbability) {
                return skill;
            }
        }
        
        return null;
    }
    
    public boolean getHasMegaChunkDroppedLoot(Location location) {        
        return this.getHasMegaChunkDroppedLootDB(
                location.getChunk().getX()/5,
                location.getChunk().getZ()/5
        );
    }
    
    public boolean setMegaChunkHasDroppedLoot(Location location, boolean droppedLoot) {
        
        int megaChunkX = location.getChunk().getX()/5;
        int megaChunkZ = location.getChunk().getZ()/5;
        
// no change
        if(droppedLoot == this.getHasMegaChunkDroppedLootDB(megaChunkX,megaChunkZ)) {
            return true;
        }
        
        if(droppedLoot) {
// set to true / add row
            try {
                PreparedStatement statement = sqlite.prepare("INSERT INTO megachunks(x,z) VALUES(?,?);");

                statement.setInt(1, megaChunkX);
                statement.setInt(2, megaChunkZ);
                statement.execute();
            }
            catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, null, ex);

                plugin.getServer().broadcastMessage("[LegendaryAllies:LootManager] A loot error occurred! Check the logs!");

                // operation failed
                return false;
            }
        }
        else {
// set to false / delete row
            try {
                PreparedStatement statement = sqlite.prepare("DELETE FROM megachunks WHERE x=?,z=? LIMIT 1;");
                statement.setInt(1, megaChunkX);
                statement.setInt(2, megaChunkZ);
                statement.execute();
            }
            catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, null, ex);

                plugin.getServer().broadcastMessage("[LegendaryAllies:LootManager] A loot error occurred! Check the logs!");

                // operation failed
                return false;
            }
        }
        
        return true;
    }
    
    private boolean getHasMegaChunkDroppedLootDB(int megaChunkX,int megaChunkZ) { 
        try
        {
            PreparedStatement statement = sqlite.prepare("SELECT x,z FROM megachunks WHERE x=? AND z=? LIMIT 1;");

            statement.setInt(1, megaChunkX);
            statement.setInt(2, megaChunkZ);

            ResultSet result = statement.executeQuery();

            if(result.next())
            {
                return true;
            }
        } 
        catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, null, ex);
            
            plugin.getServer().broadcastMessage("[LegendaryAllies:LootManager] A loot error occurred! Check the logs!");
            
            // DB errors result in no loot!
            return true;
        }
        
        return false;
    }

    public ItemStack chanceToDropRandomSkillDisk(EntityType entityType) {
        int chanceToDrop = 0;
   
        switch(entityType) {
            case ZOMBIE:
                chanceToDrop = 125;//1.25%
                break;
            case SKELETON:
                chanceToDrop = 125;
                break;
            case CREEPER:
                chanceToDrop = 90;
                break;
            case SPIDER:
                    chanceToDrop = 150;
                    break;
            case GIANT:
                    chanceToDrop = 200;
                    break;
            case SLIME:
                    chanceToDrop = 50;
                    break;
            case GHAST:
                    chanceToDrop = 100;
                    break;
            case PIG_ZOMBIE:
                    chanceToDrop = 75;
                    break;
            case ENDERMAN:
                    chanceToDrop = 110;
                    break;
            case CAVE_SPIDER:
                    chanceToDrop = 125;
                    break;
            case SILVERFISH:
                    chanceToDrop = 100;
                    break;
            case BLAZE:
                    chanceToDrop = 200;
                    break;
            case MAGMA_CUBE:
                    chanceToDrop = 150;
                    break;
            case ENDER_DRAGON:
                    chanceToDrop = 2000;
                    break;
            case WITHER:
                    chanceToDrop = 1200;
                    break;
            case WITCH:
                    chanceToDrop = 150;
                    break;
            case VILLAGER:
                    chanceToDrop = 75;
                    break;
        }
        
        if(chanceToDrop == 0) {
            return null;
        }
        
        int roll = new Random().nextInt(10000);//100.00%

        plugin.getLogger().log(Level.INFO, "{0} chance rolled {1}", new Object[]{
            chanceToDrop, 
            roll
        });

        if(roll < chanceToDrop) {
            AllySkill skill = this.getWeightedRandomSkill();
            
            plugin.getLogger().log(Level.INFO, "{0} was killed and dropped a {1} skill disk", new Object[]{
                entityType, 
                skill.getName()
            });
            
            return plugin.skillsManager.createSkillDiskItem(skill);
        }
        
        return null;
    }
}
