package me.LynSnow.ParkourNyan.Main;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitScheduler;

import me.LynSnow.ParkourNyan.Commands.MyCommandExecutor;
import me.LynSnow.ParkourNyan.FileManagers.LevelFile;
import me.LynSnow.ParkourNyan.FileManagers.PlayerFile;
import me.LynSnow.ParkourNyan.Listeners.ActionBlocker;
import me.LynSnow.ParkourNyan.Listeners.MainListener;
import me.LynSnow.ParkourNyan.Listeners.PlateListener;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;


public class ParkourNyan extends JavaPlugin {
	
	private ParkourEconomy eco = new ParkourEconomy(this);

	private short t = 0;
	
	private HashMap<UUID, ParkourPlayer> jugando = new HashMap<UUID, ParkourPlayer>();
	private HashMap<String, ParkourLevel> niveles = new HashMap<String, ParkourLevel>();
	private HashMap<UUID, Long[]> itemCooldowns = new HashMap<UUID, Long[]>();
	private HashMap<UUID, HashMap<String, Long>> levelCooldowns = new HashMap<UUID, HashMap<String, Long>>();
	
	@Override
	public void onEnable() {
		this.getServer().getPluginManager().registerEvents(new PlateListener(this), this);
		this.getServer().getPluginManager().registerEvents(new MainListener(this), this);
		this.getServer().getPluginManager().registerEvents(new ActionBlocker(this), this);
		
		this.getCommand("parkournyan").setTabCompleter(new ParkourTab(this));
		
		this.getCommand("parkournyan").setExecutor(new MyCommandExecutor(this));
		
		getEco().init();
		
		//detectar caidas cada 10 ticks (1/2 segundo)
        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                for(Map.Entry<UUID, ParkourPlayer> entry : getJugando().entrySet()) {
                	Player p = getServer().getPlayer(entry.getKey());
                	if(p != null) {
                		ParkourPlayer pp = entry.getValue();
                		ParkourLevel l = getNiveles().get(entry.getValue().getLevel());
                		pp.updateTimer();
                		p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', pp.getTimeString(l))));
                		if(t % 5 == 0) {
		                	if(p.getLocation().getY() < l.getLow()) {
		                		p.teleport(entry.getValue().getLastCP());
		                	}
	                	}
                	}
                }
                t++;
                if(t >= 120) { // cada minuto 
                	for(Map.Entry<UUID, HashMap<String, Long>> entry : getLevelCooldowns().entrySet()) {
                		if(entry.getValue().isEmpty())
                			getLevelCooldowns().remove(entry.getKey());
                	}
                	for(ParkourLevel nivel : getNiveles().values()) {
                		nivel.updateTimeTable();
                	}
                	t = 0;
                }
            }
        }, 0L, 2L);
        
		//cargar parkours
		if(new File("plugins/ParkourNyan/niveles.data").exists()) {
			niveles = LevelFile.load();
			Bukkit.getLogger().log(Level.INFO, "[ParkourNyan] Se cargo correctamente el archivo de niveles");
		}
		
		//cargar si hay gente en la db de inventarios y si hay, devolverlos
		if(new File("plugins/ParkourNyan/jugadores.data").exists()) {
			jugando = PlayerFile.load();
			if(!getJugando().isEmpty()) {
				Bukkit.getLogger().log(Level.INFO, "[ParkourNyan] Se encontro un archivo de jugadores, el plugin se cerro como es debido?");
			}
		}
	}
	
	@Override
	public void onDisable() {
		//devolver todos los inventarios
		
		for(Map.Entry<UUID, ParkourPlayer> entry : getJugando().entrySet()) {
			kickPlayer(entry.getKey(), "&cEl plugin ha sido desactivado.");
		}
		new PlayerFile(getJugando()).save();
	}
	
	public boolean existeNivel(CommandSender sender, String nom) {
		boolean enc = getNiveles().containsKey(nom);
		if(!enc) {
			sender.sendMessage(ChatColor.RED + "Ese nivel no existe, asegurate de haber escrito bien el nombre.");
		}
		return enc;
	}
	
	public boolean kickPlayer(UUID id, String razon) {
		if(getJugando().containsKey(id)) {
			ParkourPlayer pp = getJugando().get(id);
			Player p = Bukkit.getPlayer(id);
			if(p == null) {
				return false;
			}
			for(Player pl : p.getPlayer().getWorld().getPlayers()) {
				p.getPlayer().showPlayer(this, pl);
			}
			p.teleport(pp.getExit(), TeleportCause.PLUGIN);
			p.getInventory().setContents(pp.getInventory());
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', razon));
			p.setGameMode(GameMode.SURVIVAL);
			p.setFoodLevel(pp.getFood());
			p.setExhaustion(pp.getExhaust());
			for(PotionEffect effect : pp.getEffects()) {
				p.addPotionEffect(effect);
			}
			getJugando().remove(id);
			getItemCooldowns().remove(id);
		}
		return true;
	}


	public HashMap<String, ParkourLevel> getNiveles() {
		return niveles;
	}

	public ParkourEconomy getEco() {
		return eco;
	}

	public HashMap<UUID, ParkourPlayer> getJugando() {
		return jugando;
	}

	public HashMap<UUID, Long[]> getItemCooldowns() {
		return itemCooldowns;
	}

	public HashMap<UUID, HashMap<String, Long>> getLevelCooldowns() {
		return levelCooldowns;
	}
	
}
