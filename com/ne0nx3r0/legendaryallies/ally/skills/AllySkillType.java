package com.ne0nx3r0.legendaryallies.ally.skills;

import java.util.HashMap;
import java.util.Map;

public enum AllySkillType {
    Fireball(0),
    Swiftness(1),
    Blink(2),
    Replenish(3),
    NightVision(4),
    Heal(5),
    PlaneShift(6),
    VoidSight(7),
    VoidWall(8),
    Twilight(9),
    Midnight(10),
    Harvest(11),
    Sprout(12),
    VoidShift(13),
    MassHeal(14),
    VoidMirage(15);
    
    private final int id;   
    
    private static final Map<Integer, AllySkillType> map = new HashMap<>();

    static {
        for (AllySkillType skillType : AllySkillType.values()) {
            map.put(skillType.id, skillType);
        }
    }
    
    AllySkillType(int id) {
        this.id = id;
    }

    public int getSkillId() { 
        return id; 
    }

    public static AllySkillType valueOf(int skillTypeId) {
        return map.get(skillTypeId);
    }
}
