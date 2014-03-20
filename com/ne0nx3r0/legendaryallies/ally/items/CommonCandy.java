package com.ne0nx3r0.legendaryallies.ally.items;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CommonCandy extends ItemStack{
    public CommonCandy() {
        this(1);
    }
    
    private static String DISPLAY_NAME = ChatColor.GREEN+"Common Candy";
    private static String LORE_1 = ChatColor.GRAY+"When dropped on an ally egg";
    private static String LORE_2 = ChatColor.GRAY+"will remove all skills the ally";
    private static String LORE_3 = ChatColor.GRAY+"currently knows";
    
    public CommonCandy(int amount) {
        super(175,amount);
        
        ItemMeta meta = this.getItemMeta();
        
        meta.setDisplayName(DISPLAY_NAME);
        
        List<String> lore = new ArrayList<>();
        
        lore.add(LORE_1);
        lore.add(LORE_2);
        lore.add(LORE_3);
        
        meta.setLore(lore);
        
        this.setItemMeta(meta);
    }
    
    public static boolean isCommonCandy(ItemStack is) {
        if(is.hasItemMeta()) {
            ItemMeta meta = is.getItemMeta();
            
            if(meta.hasLore()) {
                return meta.getLore().get(0).equals(LORE_1);
            }
        }
        
        return false;
    }
}
