

package com.ne0nx3r0.legendaryallies.ally.skills;

import com.ne0nx3r0.legendaryallies.ally.Ally;
import com.ne0nx3r0.legendaryallies.ally.AllyClassType;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class Sprout extends AllySkill {
    public Sprout(int dropProbability) {
        super(
            AllySkillType.Sprout,
            AllyClassType.Farmer,
            "Sprout","Causes saplings and other\nplants to grow",
            45,
            dropProbability
        );
    }

    @Override
    public boolean onInteract(PlayerInteractEvent e, Ally ally) {
        return this.growSprout(e.getPlayer(), ally, e.getClickedBlock());
    }
    
    public boolean growSprout(Player player, Ally ally, Block block) {
        BlockFace[] adjacentBlockFaces = new BlockFace[]{
            BlockFace.EAST,
            BlockFace.WEST,
            BlockFace.NORTH,
            BlockFace.SOUTH
        };
        
        Block growAt = null;
        
        switch(block.getType()) {
            default:
                this.sendError(player,ally,"cannot grow this");
                
                return false;
            case CROPS:
            case CARROT:
            case POTATO:
                block.setData((byte) 7);
                
                break;
                
            case MELON_STEM:
                for(BlockFace bf : adjacentBlockFaces) {
                    if(block.getRelative(bf).getType() == Material.AIR) {
                        growAt = block.getRelative(bf);
                        
                        growAt.setType(Material.MELON_BLOCK);
                        
                        break;
                    }
                }
            
                if(growAt == null) {
                    this.sendError(player,ally,"cannot grow a melon there!");
                    
                    return false;
                }
                
                break;
                
            case PUMPKIN_STEM:
                for(BlockFace bf : adjacentBlockFaces) {
                    if(block.getRelative(bf).getType() == Material.AIR) {
                        growAt = block.getRelative(bf);
                        
                        growAt.setType(Material.PUMPKIN);
                        
                        break;
                    }
                }
            
                if(growAt == null) {
                    this.sendError(player,ally,"cannot grow a pumpkin there!");
                    
                    return false;
                }
            
                break;
                
            case SAPLING: 
                block.getWorld().generateTree(block.getLocation(), this.getTreeType(block));
                
                break;
        }
        
        ally.getPet().teleport(block.getLocation());
        
        this.send(player,ally,"grew the plant for you!!");
        
        return true;
    }
    
    public TreeType getTreeType(Block sappling) {
        switch (sappling.getData())
        {
            case 0:
                if((int)(Math.random() * 100.0D) > 90)
                {
                    return TreeType.TREE;
                }
                return TreeType.BIG_TREE;
            case 1:
                if((int)(Math.random() * 100.0D) > 90){
                    return TreeType.REDWOOD;
                }
                return TreeType.TALL_REDWOOD;
            case 2:
                return TreeType.BIRCH;
        }
        return TreeType.TREE;
    }
}
