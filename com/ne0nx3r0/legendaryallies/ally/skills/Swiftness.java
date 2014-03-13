package com.ne0nx3r0.legendaryallies.ally.skills;

import com.ne0nx3r0.legendaryallies.ally.Ally;
import com.ne0nx3r0.legendaryallies.ally.AllyClassType;
import org.bukkit.event.player.PlayerInteractEvent;

public class Swiftness extends AllySkill {
    public Swiftness() {
        super(AllySkillType.Swiftness,AllyClassType.Wild,"Swiftness","Causes ally to move at an increased speed (bonus is kept while riding)",30);
    }

    @Override
    public boolean onInteract(PlayerInteractEvent e, Ally ally) {
        return false;
    }
}
