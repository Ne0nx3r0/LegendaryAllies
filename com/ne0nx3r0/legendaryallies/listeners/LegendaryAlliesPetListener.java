package com.ne0nx3r0.legendaryallies.listeners;

import com.dsh105.echopet.compat.api.event.*;
import com.ne0nx3r0.legendaryallies.LegendaryAlliesPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class LegendaryAlliesPetListener implements Listener {

    // doesn't really matter as none of these events work.
    
    public LegendaryAlliesPetListener(LegendaryAlliesPlugin plugin) {

    }
    
    // Called before a Pet spawns.
    @EventHandler
    public void onPetSpawn(PetPreSpawnEvent e) {
        //e.getPet().getOwner().sendMessage(e.toString());
    }	
    
    //Called when a Pet attempts to damage a target. Cancelling this event will not cancel the animation, only the damage dealt.
    @EventHandler
    public void onPetAttacks(PetAttackEvent e) {
       //e.getPet().getOwner().sendMessage(e.toString());
    }
    // Called when a Pet is damaged.
    @EventHandler
    public void onPetDamaged(PetDamageEvent e) {
        //e.getPet().getOwner().sendMessage(e.toString());
    }	
            
    // Called when a Player interacts (right or left click) with a Pet.
    @EventHandler
    public void onPetInteract(PetInteractEvent e) {
        //e.getPet().getOwner().sendMessage(e.toString());
    }
    
    /*
    // Called when a Pet's Owner opens the PetMenu for their Pet.
    @EventHandler
    public void onPetMenuOpen(PetMenuOpenEvent e) {
        e.getViewer().sendMessage(e.toString());
    }	
    */

    // Called when a Pet moves while following it's Owner or riding.
    /*
    @EventHandler
    public void onPetMove(PetMoveEvent e) {
        e.getPet().getOwner().sendMessage(e.toString());
    }	
    */
            
    // Called when a Player attempts to jump while riding their Pet.
    /*
    @EventHandler
    public void onPetRidingAndJump(PetRideJumpEvent e) {
        e.getPet().getOwner().sendMessage(e.toString());
    }
    */
    
    // Called when a Player moves while riding their Pet.
    /*
    @EventHandler
    public void onPetRideMove(PetRideMoveEvent e) {
        e.getPet().getOwner().sendMessage(e.toString());
    }	
    */
    
    /*
    // Called when a Pet teleports.
    @EventHandler
    public void onPetTeleport(PetTeleportEvent e) {
        e.getPet().getOwner().sendMessage(e.toString());
    }	
    */
}
