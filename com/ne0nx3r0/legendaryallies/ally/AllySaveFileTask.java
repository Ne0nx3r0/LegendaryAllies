/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ne0nx3r0.legendaryallies.ally;

import com.ne0nx3r0.legendaryallies.LegendaryAlliesPlugin;
import io.github.dsh105.echopet.entity.PetData;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.configuration.file.YamlConfiguration;

public class AllySaveFileTask implements Runnable {
    private final LegendaryAlliesPlugin plugin;

    public AllySaveFileTask(LegendaryAlliesPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        List<Map<String,Object>> tempAllies = new ArrayList<>();

        for(Ally ally : plugin.allyManager.getAllAllies().values()) {
            Map<String,Object> tempAlly = new HashMap<>();

            tempAlly.put("id",ally.getAllyID());
            tempAlly.put("echoPetType",ally.getPetType().name());
            tempAlly.put("name",ally.getName());
            tempAlly.put("xp",ally.getXP());
            tempAlly.put("hp",ally.getHP());
            tempAlly.put("attackPower",ally.getAttackPower());
            tempAlly.put("defense",ally.getDefense());

            if(ally.getPrimarySkill() != null) {
                tempAlly.put("primarySkill",ally.getPrimarySkill().getType().toString());
            }

            if(ally.getSecondarySkill() != null) {
                tempAlly.put("secondarySkill",ally.getSecondarySkill().getType().toString());
            }
            
            if(ally.getPetData() != null) {
                ArrayList<String> tempData = new ArrayList<>();
                
                for(PetData petData : ally.getPetData()) {
                    tempData.add(petData.name());
                }
                
                tempAlly.put("petData",tempData);
            }
            
            tempAllies.add(tempAlly);
        }

        File saveFile = new File(plugin.getDataFolder(),"allies.yml");

        YamlConfiguration saveYml = YamlConfiguration.loadConfiguration(saveFile);

        saveYml.set("cardinality",plugin.allyManager.getCardinality());
        saveYml.set("allies", tempAllies);

        try
        {
            saveYml.save(new File(plugin.getDataFolder(),"allies.yml"));
        }
        catch (IOException ex)
        {
            plugin.getLogger().log(Level.SEVERE, null, ex);
            plugin.getLogger().log(Level.SEVERE,"Unable to write to save.yml!");
        }
    }
}
