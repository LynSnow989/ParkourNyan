package me.LynSnow.ParkourNyan.Commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.LynSnow.ParkourNyan.Main.HelpCommands;
import me.LynSnow.ParkourNyan.Main.ParkourLevel;
import me.LynSnow.ParkourNyan.Main.ParkourNyan;

public class MiscCommands {
	
	ParkourNyan plugin;
	
	public MiscCommands(ParkourNyan plugin) {
		this.plugin = plugin;
	}
	
	//plinfo
	public void comandoInfoPl(CommandSender sender) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&lParkour&d&lNyan &bv") + plugin.getDescription().getVersion());
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7&oCreado por LynSnow nya~"));
			p.sendMessage("");
			if(p.hasPermission("parkournyan.admin")) p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&9Para una lista de comandos, usa &f/parkournyan help"));
		} else {
			sender.sendMessage("ParkourNyan v" + plugin.getDescription().getVersion() + " por: LynSnow");
		}
	}
	
	//help
	public void comandoHelp(CommandSender sender, String[] args) {
		Player p = (Player) sender;
		String[] m;
		if(args.length < 2) {
			m = HelpCommands.general;
		} else {
			switch(args[1].toLowerCase()) {
			case "creation":
				m = HelpCommands.creation;
				break;
			case "config":
				m = HelpCommands.config;
				break;
			case "table":
				m = HelpCommands.table;
				break;
			case "misc":
				m = HelpCommands.misc;
				break;
			default:
				m = new String[]{"&c/parkournyan help (creation|config|table|misc)"};
			}
		}
		for(String s : m) {
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
		}
	}
	

	
	 //list
	public void comandoList(CommandSender sender, String[] args) {
		String color;
		char sym;
		
		
		sender.sendMessage(ChatColor.LIGHT_PURPLE + "Lista de Niveles de parkour:");
		sender.sendMessage("");
		
		if(plugin.getNiveles().isEmpty()) {
			sender.sendMessage(ChatColor.RED + "> Nada owo");
			return;
		}
		
		List<String> keys = new ArrayList<String>(plugin.getNiveles().keySet());
		ParkourLevel nivel;
		
		for(int i = 0; i < keys.size(); i++) {
			nivel = plugin.getNiveles().get(keys.get(i));
			if(nivel.isEnabled()) {
				color = "&a";
				sym = '+';
			}
			else {
				color = "&c";
				sym = '-';
			}
			
			sender.sendMessage(ChatColor.translateAlternateColorCodes
					('&', color + "<" + sym + "> " + nivel.getNombre() + color+" (" + nivel.getDisplay() + color+")"));
		}
		sender.sendMessage("");
	}
	
	//kick [nick]
	public void comandoKick(CommandSender sender, String[] args) {
		if(args.length < 2) {
			sender.sendMessage(ChatColor.RED + "Uso: /parkournyan kick [nickname]");
			return;
		}
		Player player = plugin.getServer().getPlayer(args[1]);
		if(player == null) {
			sender.sendMessage(ChatColor.RED + "Ese usuario no existe o no esta conectado.");
			return;
		}
		if(!plugin.getJugando().containsKey(player.getUniqueId())) {
			sender.sendMessage(ChatColor.RED + "Ese usuario no está jugando en un parkour.");
			return;
		}
		plugin.kickPlayer(player.getUniqueId(), "Un staff te expulsó del parkour.");
	}

	//tp [parkour]
	public void comandoTp(CommandSender sender, String[] args) {
		if(args.length < 2) {
			sender.sendMessage(ChatColor.RED + "Uso: /parkournyan tp [parkour]");
			return;
		}
		String nom = args[1].toLowerCase();
		
		if(!plugin.existeNivel(sender, nom)) return;
		ParkourLevel nivel = plugin.getNiveles().get(nom);
		
		Player player = (Player) sender;
		
		if(player.teleport(nivel.getExit())) {
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aFuiste teletransportado al parkour " + nivel.getDisplay() + " &acon éxito."));
		}
		
	}

	//info [parkour]
	public void comandoInfo(CommandSender sender, String[] args) {
		if(args.length < 2) {
			sender.sendMessage(ChatColor.RED + "Uso: /parkournyan info [parkour]");
			return;
		}
		String nom = args[1].toLowerCase();
		
		if(!plugin.existeNivel(sender, nom)) return;
		ParkourLevel nivel = plugin.getNiveles().get(nom);
		
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&l>>Info del nivel " + nom));
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b+Display: " + nivel.getDisplay()));
		if(nivel.isEnabled())
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b+Activado &a: SI"));
		else
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b+Activado &c: NO"));
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b+Cooldown: &a" + new Double(nivel.getCooldown()/1000).intValue() + " &bsegundos"));
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b+Altura min &a: " + nivel.getLow()));
		for(int i = 0; i < 3; i++) {
			Location loc = null;
			String nomL = null;
			switch(i){
			case 0:
				loc = nivel.getInicioB();
				nomL = "Inicio: ";
				break;
			case 1:
				loc = nivel.getMeta();
				nomL = "Meta: ";
				break;
			case 2:
				loc = nivel.getExit();
				nomL = "Salida: ";
				break;
			}
			if(loc == null)
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b+" + nomL + ": &c(**,**,**)"));
			else
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b+" + nomL + ": &f("
																								+ loc.getBlockX() + ","
																								+ loc.getBlockY() + ","
																								+ loc.getBlockZ() + ")"));
		}
		
	}
	
}
