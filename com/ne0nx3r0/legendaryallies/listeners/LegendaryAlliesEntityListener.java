package com.ne0nx3r0.legendaryallies.listeners;

import com.earth2me.essentials.User;
import com.ne0nx3r0.legendaryallies.LegendaryAlliesPlugin;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class LegendaryAlliesEntityListener implements Listener {
    private final LegendaryAlliesPlugin plugin;

    public LegendaryAlliesEntityListener(LegendaryAlliesPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent e) {
        if(e.getEntity().getKiller() != null) {
            Player killer = e.getEntity().getKiller();

            // disable drops if player was in creative or god mode
            if(killer.getGameMode() == GameMode.CREATIVE) {
                return;
            }
            
            if(plugin.essentials != null) {
                User user = plugin.essentials.getUser(killer);

                if(user.isGodModeEnabled()) {
                    return;
                }
            }
            
            Location lDeathAt = e.getEntity().getLocation();

            if(!plugin.lootManager.getHasMegaChunkDroppedLoot(lDeathAt)) {
                ItemStack is = plugin.lootManager.chanceToDropRandomSkillDisk(e.getEntity().getType());

                if(is != null) {
                    plugin.lootManager.setMegaChunkHasDroppedLoot(lDeathAt, true);

                    e.getEntity().getWorld().dropItemNaturally(e.getEntity().getLocation(), is);
                }
            }
        }
    }
}
