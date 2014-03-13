package com.ne0nx3r0.legendaryallies;

import com.ne0nx3r0.legendaryallies.ally.AllyManager;
import com.ne0nx3r0.legendaryallies.ally.skills.AllySkillsManager;
import com.ne0nx3r0.legendaryallies.commands.LegendaryAlliesCommands;
import com.ne0nx3r0.legendaryallies.listeners.LegendaryAlliesPlayerListener;
import com.ne0nx3r0.legendaryallies.listeners.LegendaryAlliesPetListener;
import org.bukkit.plugin.java.JavaPlugin;
import io.github.dsh105.echopet.api.EchoPetAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
        
public class LegendaryAlliesPlugin extends JavaPlugin {
    public EchoPetAPI petAPI;
    public AllyManager allyManager;
    public AllySkillsManager skillsManager;
    
    @Override
    public void onEnable() {
        this.petAPI = EchoPetAPI.getAPI();

        this.skillsManager = new AllySkillsManager(this);
        
        this.allyManager = new AllyManager(this);
        
        this.getServer().getPluginManager().registerEvents(new LegendaryAlliesPetListener(this), this);
        this.getServer().getPluginManager().registerEvents(new LegendaryAlliesPlayerListener(this), this);
        
        this.getCommand("la").setExecutor(new LegendaryAlliesCommands(this));
    }
    
    @Override
    public void onDisable() {
        this.allyManager.saveAllies();
        
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(this.allyManager.hasActiveAlly(player)) {
                player.sendMessage("Plugin is beind disabled, unsummoning your pet");
                
                this.allyManager.unSummonAlly(player);
            }
        }
    }
}
