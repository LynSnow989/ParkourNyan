package me.LynSnow.ParkourNyan.Commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.LynSnow.ParkourNyan.FileManagers.LevelFile;
import me.LynSnow.ParkourNyan.Main.ParkourLevel;
import me.LynSnow.ParkourNyan.Main.ParkourNyan;

public class CreationCommands {
	
	ParkourNyan plugin;
	
	public CreationCommands(ParkourNyan plugin) {
		this.plugin = plugin;
	}
	
	//create [parkour] (display)
	public void comandoCreate(CommandSender sender, String[] args) {
		String disp;
		if(args.length < 2) {
			sender.sendMessage(ChatColor.RED + "Uso: /parkournyan create [parkour] (display)");
		}
		else {
			String nom = args[1].toLowerCase();
			if(nom.equals("all")) {
				sender.sendMessage(ChatColor.RED + "Para evitar confusiones, no uses ese nombre porfi ;w;");
			} else if(!plugin.getNiveles().containsKey(nom)) {
				if(args.length >= 3) disp = args[2];
				else disp = args[1];
				ParkourLevel nivel = new ParkourLevel(nom, disp);
				plugin.getNiveles().put(nom,nivel);
				new LevelFile(plugin.getNiveles()).save();
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aEl parkour " + nom + "&a fue creado correctamente."));
			}else {
				sender.sendMessage(ChatColor.RED + "Ese nivel ya existe!");
			}
		}
	}
	
	//start [parkour]
	public void comandoStart(CommandSender sender, String[] args) {
		
		if(args.length < 2) {
			sender.sendMessage(ChatColor.RED + "Uso: /parkournyan start [parkour]");
			return;
		}
		
		String nom = args[1].toLowerCase();
		
		if(!plugin.existeNivel(sender, nom)) return;
		ParkourLevel nivel = plugin.getNiveles().get(nom);
		
		Player p = (Player) sender;
		Location loc = p.getLocation();
		loc.setPitch(0);
		
		if(!posSegura(sender, p.getLocation())) return;
		boolean autoEnable = (nivel.getInicioB() == null);
		
		nivel.setInicio(loc);
		loc.getBlock().setType(Material.LIGHT_WEIGHTED_PRESSURE_PLATE);
		sender.sendMessage(ChatColor.GREEN + "El inicio fue añadido correctamente");
		
		if(autoEnable) {
			if(nivel.enableIfComplete()) {
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&9El parkour &f" + nom + "&9 fue activado automaticamente, para desactivarlo usa &a/parkournyan disable " + nom));
			}
		}
		new LevelFile(plugin.getNiveles()).save();
	}
	
	//end [parkour]
	public void comandoEnd(CommandSender sender, String[] args) {
		
		if(args.length < 2) {
			sender.sendMessage(ChatColor.RED + "Uso: /parkournyan end [parkour]");
			return;
		}
		
		String nom = args[1].toLowerCase();
		
		if(!plugin.existeNivel(sender, nom)) return;
		ParkourLevel nivel = plugin.getNiveles().get(nom);
		
		Player p = (Player) sender;
		Location loc = p.getLocation();
		
		if(!posSegura(sender, p.getLocation())) return;
		boolean autoEnable = (nivel.getMeta() == null);

		nivel.setMeta(loc);
		loc.getBlock().setType(Material.HEAVY_WEIGHTED_PRESSURE_PLATE);
		sender.sendMessage(ChatColor.GREEN + "La meta fue añadida correctamente");
		
		if(autoEnable) {
			if(nivel.enableIfComplete()) {
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&9El nivel &f" + nom + "&9 fue activado automaticamente, para desactivarlo usa &a/parkournyan disable " + nom));
			}
		}
		new LevelFile(plugin.getNiveles()).save();
	}
	
	//exit [parkour]
	public void comandoExit(CommandSender sender, String[] args) {

		if(args.length < 2) {
			sender.sendMessage(ChatColor.RED + "Uso: /parkournyan exit [parkour]");
			return;
		}
		
		String nom = args[1].toLowerCase();
		
		if(!plugin.existeNivel(sender, nom)) return;
		ParkourLevel nivel = plugin.getNiveles().get(nom);
		
		Player p = (Player) sender;
		Location loc = p.getLocation();
		
		if(!posSegura(sender, p.getLocation())) return;
		boolean autoEnable = (nivel.getExit() == null);

		nivel.setExit(loc.clone());
		loc.setY(loc.getY() - 1);
		loc.getBlock().setType(Material.DIAMOND_BLOCK);
		sender.sendMessage(ChatColor.GREEN + "La salida fue añadida correctamente");
		
		if(autoEnable) {
			if(nivel.enableIfComplete()) {
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&9El nivel &f" + nom + "&9 fue activado automaticamente, para desactivarlo usa &a/parkournyan disable " + nom));
			}
		}
		new LevelFile(plugin.getNiveles()).save();
	}
	
	//checkpoint [parkour]
	public void comandoCheckPoint(CommandSender sender, String[] args) {
		
		if(args.length < 2) {
			sender.sendMessage(ChatColor.RED + "Uso: /parkournyan checkpoint [parkour]");
			return;
		}
		
		String nom = args[1].toLowerCase();
		
		if(!plugin.existeNivel(sender, nom)) return;
		ParkourLevel nivel = plugin.getNiveles().get(nom);
		
		Player p = (Player) sender;
		Location loc = p.getLocation();
		loc.setPitch(0);
		
		if(!posSegura(sender, p.getLocation())) return;
		
		if (!nivel.addCheck(loc)){
			sender.sendMessage(ChatColor.RED + "El nivel ya tiene mas de 5 checkpoints, rompe alguno primero");
			return;
		}
		loc.getBlock().setType(Material.BIRCH_PRESSURE_PLATE);
		sender.sendMessage(ChatColor.GREEN + "El checkpoint fue añadido correctamente");
		
		new LevelFile(plugin.getNiveles()).save();
	}
	
	private boolean posSegura(CommandSender sender, Location loc) {
		Location loc1 = loc.clone();
		loc1.setY(loc1.getY() - 1);
		
		if(loc.getBlock().getType() != Material.AIR || loc1.getBlock().getType() == Material.AIR) {
			sender.sendMessage(ChatColor.RED + "La posición tiene que ser segura.");
			return false;
		}
		return true;
	}
	

}
