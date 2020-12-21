package me.LynSnow.ParkourNyan.Commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.LynSnow.ParkourNyan.FileManagers.LevelFile;
import me.LynSnow.ParkourNyan.Main.ParkourEconomy;
import me.LynSnow.ParkourNyan.Main.ParkourLevel;
import me.LynSnow.ParkourNyan.Main.ParkourNyan;
import me.LynSnow.ParkourNyan.Main.ParkourLevel.Medal;

public class ConfigCommands {
	
	ParkourNyan plugin;
	
	public ConfigCommands(ParkourNyan plugin) {
		this.plugin = plugin;
	}
	
	private void eliminaNivel(ParkourLevel nivel) {
		
		if(nivel.getInicio() != null) 
			if(nivel.getInicio().getBlock() != null)
				nivel.getInicioB().getBlock().setType(Material.AIR);
		
		if(nivel.getMeta() != null) 
			if(nivel.getMeta().getBlock() != null)
				nivel.getMeta().getBlock().setType(Material.AIR);
		
		for(Location loc : nivel.getChecks()) {
			if(loc.getBlock() != null)
			loc.getBlock().setType(Material.AIR);
		}
		nivel.delTimeTable();
	}
	
	//setlow [parkour] (altura)
	public void comandoSetLow(CommandSender sender, String[] args) {
		int low;
		
		if(args.length < 2) {
			sender.sendMessage(ChatColor.RED + "Uso: /parkournyan setlow [parkour] (altura)");
			return;
		}
		String nom = args[1].toLowerCase();
		
		if(!plugin.existeNivel(sender, nom)) return;
		ParkourLevel nivel = plugin.getNiveles().get(nom);
		
		if(args.length < 3) {
			low = nivel.getInicioB().getBlockY() - 10;
			nivel.setLow(low);
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aSe cambió a la altura default (&f" + low + "&a) la altura minima del nivel " + nom + "&a."));
		}
		else {
			try {
				low  = Integer.parseInt(args[2]);
			}catch(NumberFormatException nfe) {
				sender.sendMessage(ChatColor.RED + "Uso: /parkournyan setlow [parkour] (altura)");
				return;
			}
			if(low > nivel.getInicio().getBlockY() || low > nivel.getMeta().getBlockY()) {
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cLa altura mínima debe estar por debajo del punto de inicio y la meta."));
				return;
			}
			nivel.setLow(low);
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aSe cambió a &f" + low + "&a la altura minima del nivel " + nom + "&a."));
		}
		new LevelFile(plugin.getNiveles()).save();
	}
	
	//display [parkour]
	public void comandoDisplay(CommandSender sender, String[] args) {
		if(args.length < 2) {
			sender.sendMessage(ChatColor.RED + "Uso: /parkournyan display [parkour] (display)");
			return;
		}
		String nom = args[1].toLowerCase();
		
		if(!plugin.existeNivel(sender, nom)) return;
		ParkourLevel nivel = plugin.getNiveles().get(nom);
		
		if(args.length < 3) {
			nivel.setDisplay(nivel.getNombre());
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aSe reinició el display del nivel " + nom + "&a correctamente."));
		}
		else {
			nivel.setDisplay(args[2]);
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aSe cambió el display del nivel " + nom + "&a a " + args[2] + "&a."));
		}
		
		if(nivel.getTimeTable() != null) {
			if(!nivel.updateTimeTable()) {
				sender.sendMessage(ChatColor.RED + "[!] La tabla del nivel no esta cargada, espera a que se actualice sola o actuallízala con /pn table update " + nom);
			}
		}
		
	}
	
	//prize [parkour]
	public void comandoPrize(CommandSender sender, String[] args) {
		if(args.length < 2) {
			sender.sendMessage(ChatColor.RED + "Uso: /parkournyan prize [parkour]");
			return;
		}
		String nom = args[1].toLowerCase();
		Player player = (Player) sender;
		if(!plugin.existeNivel(sender, nom)) return;
		
		
		Inventory inv = Bukkit.createInventory(null, 54, ChatColor.translateAlternateColorCodes('&', "&d&9Premios del parkour &f&l" + nom));
		
		//rellenado de la GUI General
		
		ItemStack relleno = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
		ItemMeta rellMeta = relleno.getItemMeta();
		rellMeta.setDisplayName(ChatColor.AQUA + "");
		relleno.setItemMeta(rellMeta);
		for(int i = 0; i < 27; i++) {
			inv.setItem(i, relleno);
		}
		for(int i = 9; i < 18; i++) {
			inv.setItem(i, plugin.getNiveles().get(nom).getPrize(Medal.GENERAL, i-9));
		}
		//gold
		relleno = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);
		rellMeta = relleno.getItemMeta();
		rellMeta.setDisplayName(ChatColor.YELLOW + "Medalla de oro:");
		relleno.setItemMeta(rellMeta);
		for(int i = 27; i < 36; i++) {
			inv.setItem(i, relleno);
		}
		for(int i = 28; i < 32; i++) {
			inv.setItem(i, plugin.getNiveles().get(nom).getPrize(Medal.GOLD, i-28));
		}
		
		//silver
		relleno = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
		rellMeta = relleno.getItemMeta();
		rellMeta.setDisplayName(ChatColor.WHITE + "Medalla de Plata:");
		relleno.setItemMeta(rellMeta);
		for(int i = 36; i < 45; i++) {
			inv.setItem(i, relleno);
		}
		for(int i = 37; i < 41; i++) {
			inv.setItem(i, plugin.getNiveles().get(nom).getPrize(Medal.SILVER, i-37));
		}
		
		//bronze
		relleno = new ItemStack(Material.BROWN_STAINED_GLASS_PANE);
		rellMeta = relleno.getItemMeta();
		rellMeta.setDisplayName(ChatColor.GOLD + "Medalla de Bronce:");
		relleno.setItemMeta(rellMeta);
		for(int i = 45; i < 54; i++) {
			inv.setItem(i, relleno);
		}
		for(int i = 46; i < 50; i++) {
			inv.setItem(i, plugin.getNiveles().get(nom).getPrize(Medal.BRONZE, i-46));
		}
		
		if(plugin.getEco().hasEco())
			inv = ParkourEconomy.fillInv(inv, plugin.getNiveles().get(nom));
		
		player.openInventory(inv);
	}
	
	//enable [nombre/all]
	public void comandoEnable(CommandSender sender, String[] args) {
		if(args.length < 2) {
			sender.sendMessage(ChatColor.RED + "Uso: /parkournyan enable [nombre/all]");
			return;
		}
		String nom = args[1].toLowerCase();
		
		ParkourLevel nivel;
		if(nom.equals("all")) {
			List<String> keys = new ArrayList<String>(plugin.getNiveles().keySet());
			
			for(int i = 0; i < keys.size(); i++) {
				nivel = plugin.getNiveles().get(keys.get(i));
				nom = nivel.getNombre();
				if(!nivel.isEnabled()) {
					if(nivel.enableIfComplete()) {
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aEl nivel " + nom + "&a fue &9activado&a correctamente."));
					} else {
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cEl nivel " + nom + "&c no se pudo activar."));
					}
				}
			}
		} else {
			if(!plugin.existeNivel(sender, nom)) return;
			
			nivel = plugin.getNiveles().get(nom);
			if(!nivel.isEnabled()) {
				if(nivel.enableIfComplete()) {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aEl nivel " + nom + "&a fue &9activado&a correctamente."));
				} else {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cEl nivel " + nom + "&c no se pudo activar, asegúrate de que tenga el 'start', 'end' y 'exit' definidos."));
				}
			} else {
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cEl nivel " + nom + "&c ya esta activado!"));
			}
		}
		new LevelFile(plugin.getNiveles()).save();
	}
	
	//disable [nombre/all]
	public void comandoDisable(CommandSender sender, String[] args) {
		if(args.length < 2) {
			sender.sendMessage(ChatColor.RED + "Uso: /parkournyan disable [nombre/all]");
			return;
		}
		String nom = args[1].toLowerCase();
		
		ParkourLevel nivel;
		List<UUID> keysJ = new ArrayList<>(plugin.getJugando().keySet());
		
		if(nom.equals("all")) {
			List<String> keys = new ArrayList<String>(plugin.getNiveles().keySet());
			
			for(int j = 0; j < keysJ.size(); j++) {
				plugin.kickPlayer(keysJ.get(j), "&cEl parkour en el que estabas fue desactivado por un admin.");
			}
			
			for(int i = 0; i < keys.size(); i++) {
				nivel = plugin.getNiveles().get(keys.get(i));
				nom = nivel.getNombre();
				nivel.disable();
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aEl nivel " + nom + "&a fue &cdesactivado &acorrectamente."));
			}
		} else {
			if(!plugin.existeNivel(sender, nom)) return;
			
			for(int j = 0; j < keysJ.size(); j++) {
				if(plugin.getJugando().get(keysJ.get(j)).getLevel().equals(nom))
					plugin.kickPlayer(keysJ.get(j), "&cEl nivel fue desactivado por un admin.");
			}
			nivel = plugin.getNiveles().get(nom);
			nivel.disable();
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aEl nivel " + nom + "&a fue &cdesactivado &acorrectamente."));
		}
		new LevelFile(plugin.getNiveles()).save();
	}
	
	//delete [nombre/all]
	public void comandoDelete(CommandSender sender, String[] args) {
		if(args.length < 2) {
			sender.sendMessage(ChatColor.RED + "Uso: /parkournyan delete [nombre/all]");
			return;
		}
		
		comandoDisable(sender, args);
		
		String nom = args[1].toLowerCase();
		
		ParkourLevel nivel;
		
		if(nom.equals("all")) {
			List<String> keys = new ArrayList<String>(plugin.getNiveles().keySet());
			for(int i = 0; i < keys.size(); i++) {
				nivel = plugin.getNiveles().get(keys.get(i));
				eliminaNivel(nivel);
				plugin.getNiveles().remove(keys.get(i));
			}
		} else {
			if(!plugin.getNiveles().containsKey(nom)) return;
			nivel = plugin.getNiveles().get(nom);
			eliminaNivel(nivel);
			plugin.getNiveles().remove(nom);
		}
		new LevelFile(plugin.getNiveles()).save();
	}	
	
	//cd [parkour] (cd)
	public void comandoCD(CommandSender sender, String[] args) {
		int cd = 0;
		
		if(args.length < 2) {
			sender.sendMessage(ChatColor.RED + "Uso: /parkournyan cd [parkour] (segundos)");
			return;
		}
		String nom = args[1].toLowerCase();
		
		if(!plugin.existeNivel(sender, nom)) return;
		ParkourLevel nivel = plugin.getNiveles().get(nom);
		
		if(args.length < 3) {
			nivel.setCooldown(cd);
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aSe cambió al cooldown default (&f" + cd + "&a) el nivel " + nom + "&a."));
		}
		else {
			try {
				cd  = Integer.parseInt(args[2]);
			}catch(NumberFormatException nfe) {
				sender.sendMessage(ChatColor.RED + "Uso: /parkournyan setlow [parkour] (segundos)");
				return;
			}
			nivel.setCooldown(cd*1000);
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aSe cambió a &f" + cd + "&a segundos el cooldown del nivel " + nom + "&a."));
		}
		for(Map.Entry<UUID, HashMap<String, Long>> e1 : plugin.getLevelCooldowns().entrySet()) {
			if(e1.getValue().containsKey(nom)) {
				e1.getValue().remove(nom);
			}
		}
		new LevelFile(plugin.getNiveles()).save();
	}
	
	//medal [tipo] [parkour] (tiempo)
	public void comandoMedal(CommandSender sender, String[] args) {
		if(args.length < 3) {
			sender.sendMessage(ChatColor.RED + "Uso: /parkournyan medal [tipo] [parkour] (segundos)");
			return;
		}
		
		Medal tipo;
		
		switch(args[1].toLowerCase()) {
		case "gold":
			tipo = Medal.GOLD;
			break;
		case "silver":
			tipo = Medal.SILVER;
			break;
		case "bronze":
			tipo = Medal.BRONZE;
			break;
		default:
			sender.sendMessage(ChatColor.RED + "Los tipos de medalla son: \"gold\", \"silver\" o \"bronze\".");
			return;
		}
		
		String nom = args[2].toLowerCase();
		
		if(!plugin.existeNivel(sender, nom)) return;
		ParkourLevel nivel = plugin.getNiveles().get(nom);
		
		if(args.length < 4) {
			nivel.setMedal(tipo, -1);
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aSe desactivó la medalla &f" + tipo + "&a del parkour &f" + nom + "&a."));
			
		} else {
			int time;
			try {
				time  = Integer.parseInt(args[3]);
			}catch(NumberFormatException nfe) {
				sender.sendMessage(ChatColor.RED + "El tiempo debe ser un número de segundos.");
				return;
			}
			nivel.setMedal(tipo, time);
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aSe cambió la medalla &f" + tipo + "&a del parkour &f" + nom + "&a a &f" + time + "&a segundos."));
		}
		new LevelFile(plugin.getNiveles()).save();
	}
	
	
	
}
