package com.ne0nx3r0.legendaryallies.commands;

import com.ne0nx3r0.legendaryallies.LegendaryAlliesPlugin;
import com.ne0nx3r0.legendaryallies.ally.Ally;
import com.ne0nx3r0.legendaryallies.ally.skills.AllySkill;
import com.ne0nx3r0.legendaryallies.ally.skills.AllySkillType;
import io.github.dsh105.echopet.entity.PetType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class LegendaryAlliesCommands implements CommandExecutor {
    private final LegendaryAlliesPlugin plugin;

    public LegendaryAlliesCommands(LegendaryAlliesPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String label, String[] args) {
        if(args.length > 0) {
            switch(args[0]) {
                case "createAlly":
                case "ca":
                    return this._createAlly(cs,args);
                case "createDuplicateAlly":
                case "cda":
                    return this._createDuplicateAlly(cs,args);
                case "removeAlly":
                case "aa":
                    return this._deleteAlly(cs,args);
                case "createSkillDisk":
                case "csd":
                    return this._createSkillDisk(cs,args);
            }
        }
        
        return false;
    }

    private boolean _createAlly(CommandSender cs, String[] args) {
        if(cs instanceof Player && !this.hasCommandPermission(cs, "admin", "Manage legendary allies")) {
            return true;
        }
        
        // /la create <PETTYPE> [username]
        if(args.length < 2) {
            this.send(cs,"Create",
                "Usage:",
                "/la createAlly <TYPE> [username] [name]",
                "/la ca <TYPE> [username] [name]",
                "Hint: ZOMBIE, CAVESPIDER, ENDERMAN"
            );
        }
        else {
            if(!(cs instanceof Player) && args.length < 3) {
                cs.sendMessage(ChatColor.RED+"You must specify a player!");
                
                return true;
            }
            
            Player player;
            
            if(args.length == 2) {
                player = (Player) cs;
            }
            else {
                player = Bukkit.getPlayer(args[2]);
                
                if(player == null) {
                    cs.sendMessage(ChatColor.RED+"Invalid player!");

                    return true;
                }
            }
            
            PetType petType;
            
            try {
                petType = PetType.valueOf(args[1].toUpperCase().replace("_",""));
            }
            catch(Exception ex) {
                cs.sendMessage(ChatColor.RED+"Invalid type!");

                return true;
            }
            
            Ally ally = plugin.allyManager.createAlly(petType);
            
            if(args.length > 3) {
                String name = args[3];
                
                for(int i=4;i<args.length;i++) {
                    name = name + " " + args[i];
                }
                
                ally.setName(name);
            }
            else {
                ally.setName("Unnamed "+ally.getPetType().name().toLowerCase());
            }
            
            ItemStack is = plugin.allyManager.createSummoningItem(ally);

            if(!player.getInventory().addItem(is).isEmpty()) {
                player.getWorld().dropItemNaturally(player.getLocation(), is);
            }

            player.sendMessage("You received a legendary ally! ("+ally.getName()+" #LMC"+ally.getAllyID()+")");
            
            if(!cs.equals(player)) {
                this.send(cs,"Create","Gave "+player.getName()+" a legendary ally! ("+ally.getName()+" #LMC"+ally.getAllyID()+")");
            }
        }
        
        return true;
    }

    private boolean _createSkillDisk(CommandSender cs, String[] args) {
        if(!this.hasCommandPermission(cs, "admin", "Create a skill disk")) {
            return true;
        }
        
        // /la createSkillDisk <SKILLTYPE> [username]
        if(args.length < 2) {
            this.send(cs,"Create",
                "Usage:",
                "/la createSkillDisk <SKILL> [username]",
                "/la csd <SKILL> [username]"
            );
        }
        else {
            if(!(cs instanceof Player) && args.length < 3) {
                cs.sendMessage(ChatColor.RED+"You must specify a player!");
                
                return true;
            }
            
            Player player;
            
            if(args.length == 2) {
                player = (Player) cs;
            }
            else {
                player = Bukkit.getPlayer(args[2]);
                
                if(player == null) {
                    cs.sendMessage(ChatColor.RED+"Invalid player!");

                    return true;
                }
            }
            
            AllySkillType skillType;
            
            try {
                skillType = AllySkillType.valueOf(args[1]);
            }
            catch(Exception ex) {
                cs.sendMessage(ChatColor.RED+"Invalid skill!");

                return true;
            }
            
            AllySkill skill = plugin.skillsManager.getSkillFromType(skillType);
            
            if(skill == null) {
                cs.sendMessage(ChatColor.RED+"Invalid skill!");

                return true;
            }
            
            ItemStack is = plugin.skillsManager.createSkillDiskItem(skill);

            if(!player.getInventory().addItem(is).isEmpty()) {
                player.getWorld().dropItemNaturally(player.getLocation(), is);
            }

            player.sendMessage("You received a "+skill.getName()+" skill disk!");
            
            if(!cs.equals(player)) {
                this.send(cs,"Create","Gave "+player.getName()+" a "+skill.getName()+" skill disk!");
            }
        }
        
        return true;
    }
    
    private boolean hasCommandPermission(CommandSender cs, String sPerm,String sAction)
    {
        if(cs.hasPermission("legendaryallies."+sPerm))
        {
            return true;
        }
        
        cs.sendMessage(ChatColor.RED+"You do not have permission to use "+ChatColor.WHITE+sAction+ChatColor.RED+".");
        cs.sendMessage(ChatColor.RED+"Permission node: "+ChatColor.WHITE+"legendaryallies."+sPerm);
        
        return false;
    }
    
    private void send(CommandSender cs, String... args) {
        cs.sendMessage(ChatColor.GREEN+"--- "+ChatColor.WHITE+args[0]+ChatColor.GREEN+" ---");

        for(int i=1;i<args.length;i++) {
            cs.sendMessage(args[i]);
        }

        cs.sendMessage("");
    }

    private boolean _createDuplicateAlly(CommandSender cs, String[] args) {
        if(!this.hasCommandPermission(cs, "admin", "Manage legendary allies")) {
            return true;
        }
        
        // /la create <PETTYPE> [username]
        if(args.length < 2) {
            this.send(cs,"Create Duplicate",
                "Usage:",
                "/la createDuplicateAlly <ID> [username]",
                "/la cda <ID> [username]"
            );
        }
        else {
            if(!(cs instanceof Player) && args.length < 2) {
                cs.sendMessage(ChatColor.RED+"You must specify a player!");
                
                return true;
            }
            
            Player player;
            
            if(args.length == 2) {
                player = (Player) cs;
            }
            else {
                player = Bukkit.getPlayer(args[2]);
                
                if(player == null) {
                    cs.sendMessage(ChatColor.RED+"Invalid player!");

                    return true;
                }
            }
            
            int allyId;
            
            try {
                allyId = Integer.parseInt(args[1]);
            }
            catch(Exception ex) {
                cs.sendMessage(ChatColor.RED+"Invalid ID!");

                return true;
            }
            
            Ally ally = plugin.allyManager.getAlly(allyId);
            
            if(ally == null) {
                cs.sendMessage(ChatColor.RED+"Invalid ID!");

                return true;
            }
            
            ItemStack is = plugin.allyManager.createSummoningItem(ally);

            if(!player.getInventory().addItem(is).isEmpty()) {
                player.getWorld().dropItemNaturally(player.getLocation(), is);
            }

            player.sendMessage("You received a legendary ally copy! ("+ally.getName()+" #LMC"+ally.getAllyID()+")");
            
            if(!cs.equals(player)) {
                this.send(cs,"Create","Gave "+player.getName()+" a legendary ally copy! ("+ally.getName()+" #LMC"+ally.getAllyID()+")");
            }
        }
        
        return true;
    }

    private boolean _deleteAlly(CommandSender cs, String[] args) {
        if(!this.hasCommandPermission(cs, "admin", "Manage legendary allies")) {
            return true;
        }
        
        if(args.length < 2) {
            this.send(cs,"Remove Ally",
                "Usage:",
                "/la removeAlly <ID>",
                "/la ra <ID>"
            );
        }
        else {
            int allyId;
            
            try {
                allyId = Integer.parseInt(args[1]);
            }
            catch(Exception ex) {
                cs.sendMessage(ChatColor.RED+"Invalid ID!");

                return true;
            }
            
            Ally ally = plugin.allyManager.getAlly(allyId);
            
            if(ally == null) {
                cs.sendMessage(ChatColor.RED+"Invalid ID!");

                return true;
            }
            
            String allyName = ally.getName();
            
            plugin.allyManager.removeAlly(ally);
            
            cs.sendMessage("Deleted "+allyName+" (LMC#"+allyId+")");  
        }
        
        return true;
    }
}
