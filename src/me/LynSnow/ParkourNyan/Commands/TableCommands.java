package me.LynSnow.ParkourNyan.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.LynSnow.ParkourNyan.FileManagers.LevelFile;
import me.LynSnow.ParkourNyan.Main.ParkourLevel;
import me.LynSnow.ParkourNyan.Main.ParkourNyan;

public class TableCommands {
	
	ParkourNyan plugin;
	
	public TableCommands(ParkourNyan plugin) {
		this.plugin = plugin;
	}
	
	
	
	//table [operacion] [parkour]
	public void comandoTable(CommandSender sender, String[] args) {
		if(args.length < 3) {
			sender.sendMessage(ChatColor.RED + "Uso: /parkournyan table [operacion] [parkour]"); // redir a /pn help table
			return;
		}
		String op = args[1].toLowerCase();
		String nom = args[2].toLowerCase();
		
		if(!plugin.existeNivel(sender, nom)) return;
		ParkourLevel nivel = plugin.getNiveles().get(nom);
		
		
		switch(op) {
			case "create":
				if(!nivel.newTimeTable((Player) sender)) {
					sender.sendMessage(ChatColor.RED + "Asegurate de que la tabla anterior esté cargada o eliminala primero.");
					return;
				}
				//plugin.getHDHook().createHD(nivel, ((Player) sender).getLocation());
				sender.sendMessage(ChatColor.GREEN + "Tabla creada!");
				break;
			case "delete":
				if(!nivel.delTimeTable()) {
					sender.sendMessage(ChatColor.RED + "Asegurate de que la tabla esté cargada.");
					return;
				}
				sender.sendMessage(ChatColor.GREEN + "Tabla eliminada!");
				break;
			case "update":
				if(!nivel.updateTimeTable()) {
					sender.sendMessage(ChatColor.RED + "La tabla no existe o no está cargada.");
					return;
				}
				sender.sendMessage(ChatColor.GREEN + "Tabla actualizada!");
				break;
			case "reset":
				nivel.resetBest();
				if(nivel.getTimeTable() != null) {
					if(!nivel.updateTimeTable()) {
						sender.sendMessage(ChatColor.RED + "[!] La tabla no está cargada, para actualizarla usa /pn table update " + nom);
						return;
					}
				}
				sender.sendMessage(ChatColor.GREEN + "Tiempos reiniciados!");
				break;
			case "remove":
				if(args.length < 4) {
					sender.sendMessage(ChatColor.RED + "Uso: /parkournyan table remove [parkour] [nick]");
					return;
				}
				if(!nivel.hasBest(args[3].toLowerCase())) {
					sender.sendMessage(ChatColor.RED + "Escoge un nick de la tabla.");
					return;
				}
				nivel.removeFromTimeTable(args[3].toLowerCase());
				if(nivel.getTimeTable() != null) {
					if(!nivel.updateTimeTable()) {
						sender.sendMessage(ChatColor.RED + "[!] La tabla no está cargada, para actualizarla usa /pn table update " + nom);
						return;
					}
				}
				sender.sendMessage(ChatColor.GREEN + "Tiempo eliminado de la tabla!");
				break;
				
			default:
				sender.sendMessage(ChatColor.RED + "No reconozco esa operación ;w;");
				return;
		}
		new LevelFile(plugin.getNiveles()).save();
	}
	
}
