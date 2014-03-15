package com.ne0nx3r0.legendaryallies.loot;

import com.ne0nx3r0.legendaryallies.LegendaryAlliesPlugin;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import lib.PatPeter.SQLibrary.SQLite;
import org.bukkit.Location;

public class LootManager {
    private final LegendaryAlliesPlugin plugin;
    private final SQLite sqlite;

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
            PreparedStatement statement = sqlite.prepare("SELECT EXISTS(SELECT 1 FROM megachunks WHERE x=? AND z=? LIMIT 1);");

            statement.setInt(1, megaChunkX);
            statement.setInt(1, megaChunkZ);

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
}
