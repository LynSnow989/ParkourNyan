package me.LynSnow.ParkourNyan.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.LynSnow.ParkourNyan.Main.ParkourNyan;

public class MyCommandExecutor implements CommandExecutor{
	
	ParkourNyan plugin;
	
	CreationCommands creation;
	ConfigCommands config;
	MiscCommands misc;
	TableCommands table;
	
	public MyCommandExecutor(ParkourNyan plugin) {
		this.plugin = plugin;
		
		creation = new CreationCommands(plugin);
		config = new ConfigCommands(plugin);
		misc = new MiscCommands(plugin);
		table = new TableCommands(plugin);
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		//	/parkournyan /pn
		if(label.equalsIgnoreCase("parkournyan") || label.equalsIgnoreCase("pn")) {
			if(args.length <= 0) {
				// solo /pn
				misc.comandoInfoPl(sender);
				return true;
			}
			if(sender instanceof Player) {
				//kick
				if(!sender.hasPermission("parkournyan.admin")) {
					sender.sendMessage(ChatColor.RED + "Nyo tienes permiso de usar ese comando~");
					return true;
				}
				//comandos
				switch(args[0].toLowerCase()) {
				
					case "help":
						misc.comandoHelp(sender, args);
						break;
					case "create":
						creation.comandoCreate(sender,args);
						break;
					case "list":
						misc.comandoList(sender,args);
						break;
					case "start":
						creation.comandoStart(sender,args);
						break;
					case "end":
						creation.comandoEnd(sender,args);
						break;
					case "exit":
						creation.comandoExit(sender,args);
						break;
					case "checkpoint":
					case "cp":
						creation.comandoCheckPoint(sender, args);
						break;
					case "setlow":
						config.comandoSetLow(sender, args);
						break;
					case "enable":
						config.comandoEnable(sender, args);
						break;
					case "disable":
						config.comandoDisable(sender, args);
						break;
					case "display":
						config.comandoDisplay(sender, args);
						break;
					case "prize":
						config.comandoPrize(sender, args);
						break;
					case "delete":
						config.comandoDelete(sender, args);
						break;
					case "kick":
						misc.comandoKick(sender, args);
						break;
					case "tp":
						misc.comandoTp(sender, args);
						break;
					case "info":
						misc.comandoInfo(sender, args);
						break;
					case "cooldown":
					case "cd":
						config.comandoCD(sender, args);
						break;
					case "table":
						table.comandoTable(sender, args);
						break;
					case "medal":
						config.comandoMedal(sender, args);
						break;
					default:
						sender.sendMessage(ChatColor.RED + "Sorry nya, no reconozco ese comando ;w;");
				}
				return true;
			}
			else {
				sender.sendMessage("*** Este plugin solo se puede manejar como un jugador! ***");
				return true;
			}
		}
		
		return false;
	}
	
	
}
