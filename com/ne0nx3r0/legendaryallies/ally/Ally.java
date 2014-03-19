package com.ne0nx3r0.legendaryallies.ally;

import com.ne0nx3r0.legendaryallies.ally.skills.AllySkill;
import io.github.dsh105.echopet.entity.Pet;
import io.github.dsh105.echopet.entity.PetData;
import io.github.dsh105.echopet.entity.PetType;
import java.util.ArrayList;

public class Ally {
    private final int allyId;
    private final PetType petType;
    private String name = "Unnamed";
    private int xp = 0;
    private int hp = 1;
    private int attackPower = 0;
    private int defense = 0; 
    private AllySkill primarySkill;   
    private AllySkill secondarySkill;
    private Pet pet;
    private ArrayList<PetData> petData;
    
    public Ally(int id,PetType petType) {
        this.allyId = id;
        this.petType = petType;
    }

    // Generally used to load a saved ally to memory
    Ally(int allyId, PetType petType, String name, int xp,int hp,int attackPower,int defense,AllySkill primarySkill,AllySkill secondarySkill, ArrayList<PetData> petData) {
        this.allyId = allyId;
        this.petType = petType;
        this.name = name;
        this.xp = xp;
        this.hp = hp;
        this.attackPower = attackPower;
        this.defense = defense;
        this.primarySkill = primarySkill;
        this.secondarySkill = secondarySkill;
        this.petData = petData;
    }

    public int getAllyID() {
        return this.allyId;
    }

    public PetType getPetType() {
        return this.petType;
    }

    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public int getXP() {
        return this.xp;
    }
    
    public void setXP(int xp) {
        this.xp = xp;
    }

    public int getHP() {
        return this.hp;
    }
    
    public void setHP(int hp) {
        this.hp = hp;
    }

    public int getAttackPower() {
        return this.attackPower;
    }
    
    public void setAttackPower(int attackPower) {
        this.attackPower = attackPower;
    }

    public int getDefense() {
        return this.defense;
    }
    
    public void setDefense(int defense) {
        this.defense = defense;
    }

    public AllyClassType getPrimaryClass() {
        switch(this.petType) {
            default: return AllyClassType.Unknown;
            case BAT: return AllyClassType.Flying;
            case BLAZE: return AllyClassType.Fire;
            case CAVESPIDER: return AllyClassType.Wild;
            case CHICKEN: return AllyClassType.Support;
            case COW: return AllyClassType.Farmer;
            case CREEPER: return AllyClassType.Mage;
            case ENDERMAN: return AllyClassType.Void;
            case ENDERDRAGON: return AllyClassType.Void;
            case GHAST: return AllyClassType.Flying;
            case IRONGOLEM: return AllyClassType.Warrior;
            case MAGMACUBE: return AllyClassType.Mage;
            case MUSHROOMCOW: return AllyClassType.Support;
            case OCELOT: return AllyClassType.Wild;
            case PIG: return AllyClassType.Support;
            case PIGZOMBIE: return AllyClassType.Nether;
            case SHEEP: return AllyClassType.Support;
            case SILVERFISH: return AllyClassType.Wild;
            case SKELETON: return AllyClassType.Undead;
            case SLIME: return AllyClassType.Support;
            case SNOWMAN: return AllyClassType.Cold;
            case SPIDER: return AllyClassType.Warrior;
            case SQUID: return AllyClassType.Aqua;
            case VILLAGER: return AllyClassType.Carpenter;
            case WITCH: return AllyClassType.Mage;
            case WITHER: return AllyClassType.Undead;
            case WOLF: return AllyClassType.Warrior;
            case ZOMBIE: return AllyClassType.Warrior;
            case HORSE: return AllyClassType.Wild;
        }
    }

    public AllyClassType getSecondaryClass() {
        switch(this.petType) {
            default: return AllyClassType.Unknown;
            case BAT: return AllyClassType.Mage;
            case BLAZE: return AllyClassType.Flying;
            case CAVESPIDER: return AllyClassType.Support;
            case CHICKEN: return AllyClassType.Farmer;
            case COW: return AllyClassType.Support;
            case CREEPER: return AllyClassType.Wild;
            case ENDERMAN: return AllyClassType.Carpenter;
            case ENDERDRAGON: return AllyClassType.Flying;
            case GHAST: return AllyClassType.Nether;
            case IRONGOLEM: return AllyClassType.Support;
            case MAGMACUBE: return AllyClassType.Fire;
            case MUSHROOMCOW: return AllyClassType.Wild;
            case OCELOT: return AllyClassType.Farmer;
            case PIG: return AllyClassType.Farmer;
            case PIGZOMBIE: return AllyClassType.Undead;
            case SHEEP: return AllyClassType.Farmer;
            case SILVERFISH: return AllyClassType.Warrior;
            case SKELETON: return AllyClassType.Warrior;
            case SLIME: return AllyClassType.Mage;
            case SNOWMAN: return AllyClassType.Carpenter;
            case SPIDER: return AllyClassType.Wild;
            case SQUID: return AllyClassType.Flying;
            case VILLAGER: return AllyClassType.Support;
            case WITCH: return AllyClassType.Alchemist;
            case WITHER: return AllyClassType.Mage;
            case WOLF: return AllyClassType.Wild;
            case ZOMBIE: return AllyClassType.Undead;   
            case HORSE: return AllyClassType.Support;
        }
    }


    public void setSecondarySkill(AllySkill skill) {
        this.secondarySkill = skill;
    }
    
    public AllySkill getSecondarySkill() {
        return this.secondarySkill;
    }

    public void setPrimarySkill(AllySkill skill) {
        this.primarySkill = skill;
    }
    
    public AllySkill getPrimarySkill() {
       return this.primarySkill;
    }

    public int getLevel() {
        return this.xp / 1000;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }
    
    public Pet getPet() {
        return this.pet;
    }

    public ArrayList<PetData> getPetData() {
        return this.petData;
    }
    
    public void setPetData(ArrayList<PetData> petData) {
        this.petData = petData;
    }
}
