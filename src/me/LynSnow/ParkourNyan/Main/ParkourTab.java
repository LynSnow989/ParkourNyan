package me.LynSnow.ParkourNyan.Main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class ParkourTab implements TabCompleter{
	
	private ParkourNyan plugin;

	private final List<String> general = Arrays.asList(new String[] {
			"cd", "checkpoint", "cooldown", "cp", "create", "delete", "disable", "display", "enable", "end",
			"exit", "help", "info", "kick", "list", "medal", "prize", "setlow" , "start", "table", "tp"
	});
	
	public ParkourTab(ParkourNyan plugin) {
		this.plugin = plugin;
	}
	
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args){
		
		List<String> res = new ArrayList<>();
		
		if(sender.hasPermission("parkournyan.admin")) {
			if(args.length > 0) {
				if(args.length == 1) {
					for(String a : general) {
						if(a.toLowerCase().startsWith(args[0].toLowerCase()))
							res.add(a);
					}
				} else {
					switch(args[0].toLowerCase()) {
					case "enable":
					case "disable":
					case "delete":
						if(args.length == 2)
							if("all".startsWith(args[1].toLowerCase()))
								res.add("ALL");
					case "cd":
					case "cooldown":
					case "create":
					case "start":
					case "end":
					case "exit":
					case "checkpoint":
					case "cp":
					case "setlow":
					case "display":
					case "prize":
					case "info":
					case "tp":
						if(args.length == 2)
							res.addAll(completarNiveles(args[1]));
						break;
					case "medal":
						if(args.length == 2) {
							List<String> temp = Arrays.asList(new String[] {"GOLD", "SILVER", "BRONZE" });
							for(String t : temp) {
								if(t.toLowerCase().startsWith(args[1].toLowerCase())) {
									res.add(t);
								}
							}
						}
						if(args.length == 3) {
							res.addAll(completarNiveles(args[2]));
						}
						break;
					case "help":
						if(args.length == 2) {
							List<String> temp = Arrays.asList(new String[] {"creation", "table", "config", "misc" });
							for(String t : temp) {
								if(t.toLowerCase().startsWith(args[1].toLowerCase())) {
									res.add(t);
								}
							}
						}
						break;
					case "table":
						if(args.length == 2) {
							List<String> temp = Arrays.asList(new String[] {"create", "delete", "remove", "reset", "update"});
							for(String t : temp) {
								if(t.toLowerCase().startsWith(args[1].toLowerCase())) {
									res.add(t);
								}
							}
						}
						if(args.length == 3) {
							res.addAll(completarNiveles(args[2]));
						}
						break;
					}
				}
			}
			
		} else if(sender.hasPermission("parkournyan.mod")) {
			if(args.length == 1) {
				res.add("kick");
			}
		}
		return res;
	}
	
	private List<String> completarNiveles(String argument){
		List<String> res = new ArrayList<>();
		for(String nom : plugin.getNiveles().keySet()) {
			if(nom.toLowerCase().startsWith(argument.toLowerCase()))
				res.add(nom);
		}
		return res;
	}
	
}
