package me.LynSnow.ParkourNyan.Listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

import me.LynSnow.ParkourNyan.FileManagers.LevelFile;
import me.LynSnow.ParkourNyan.FileManagers.PlayerFile2;
import me.LynSnow.ParkourNyan.Main.ParkourLevel;
import me.LynSnow.ParkourNyan.Main.ParkourLevel.Medal;
import me.LynSnow.ParkourNyan.Main.ParkourLevel.TipoPos;
import me.LynSnow.ParkourNyan.Main.ParkourNyan;
import me.LynSnow.ParkourNyan.Main.ParkourPlayer;
import me.LynSnow.ParkourNyan.Main.ParkourTime;

public class PlateListener implements Listener{
	
	ParkourNyan plugin;
	
	public PlateListener(ParkourNyan plugin) {
		this.plugin = plugin;
	}
	
	private ParkourLevel encuentraPos(Location loc, TipoPos tipo) {
		List<String> keys = new ArrayList<String>(plugin.getNiveles().keySet());
		ParkourLevel nivel;
		
		for(int i = 0; i < keys.size(); i++) {
			nivel = plugin.getNiveles().get(keys.get(i));
			if(nivel.isEnabled()) {
				switch(tipo) {
				case START:
					if(nivel.getInicioB().equals(loc))
						return nivel;
					break;
				case END:
					if(nivel.getMeta().equals(loc))
						return nivel;
					break;
				case EXIT:
					if(nivel.getExit().equals(loc))
						return nivel;
					break;
				case CHECK:
					for(int j = 0; j < nivel.getChecks().size(); j++) {
						Location cp = nivel.getCheckB(j);
						if(cp.equals(loc)) {
							return nivel;
						}
					}
					break;
				}
			}
		}
		return null;
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent ev) {
		if(ev.getAction().equals(Action.PHYSICAL)) {
			if(ev.getClickedBlock().getType() == Material.LIGHT_WEIGHTED_PRESSURE_PLATE) {
				ParkourLevel nivel = encuentraPos(ev.getClickedBlock().getLocation(), TipoPos.START);
				if(nivel != null) {
					startParkour(ev.getPlayer(), nivel);
				}
			}
			if(ev.getClickedBlock().getType() == Material.HEAVY_WEIGHTED_PRESSURE_PLATE) {
				ParkourLevel nivel = encuentraPos(ev.getClickedBlock().getLocation(), TipoPos.END);
				if(nivel != null) {
					endParkour(ev.getPlayer(),nivel);
				}
			}
			if(ev.getClickedBlock().getType() == Material.BIRCH_PRESSURE_PLATE) {
				ParkourLevel nivel = encuentraPos(ev.getClickedBlock().getLocation(), TipoPos.CHECK);
				if(nivel != null) {
					checkpointGet(ev.getPlayer(), nivel, ev.getClickedBlock().getLocation());
				}
			}
		}
	}
	
	private void startParkour(Player p, ParkourLevel nivel) {
		
		boolean onCD = false;
		if(!plugin.getJugando().containsKey(p.getUniqueId())) {
			
			if(plugin.getLevelCooldowns().containsKey(p.getUniqueId())) {
				if(plugin.getLevelCooldowns().get(p.getUniqueId()).containsKey(nivel.getNombre())){
					Long tiempo = plugin.getLevelCooldowns().get(p.getUniqueId()).get(nivel.getNombre()) + nivel.getCooldown() - System.currentTimeMillis();
					if(tiempo > 0) {
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', tiempoRestante(new Long(tiempo/1000).intValue())));
						onCD = true;
					}
				}
			}
			
			ParkourPlayer tmpPlayer = new ParkourPlayer(p.getFoodLevel(), 
														p.getExhaustion(),
														p.getHealth(),
														p.getActivePotionEffects(),
														nivel,
														onCD);
			plugin.getJugando().put(p.getUniqueId(), tmpPlayer);
			//new PlayerFile(plugin.getJugando()).save();
			new PlayerFile2(p.getInventory().getContents()).save(p.getUniqueId());
			setInvParkour(p);
			p.setFoodLevel(20);
			p.setExhaustion(0);
			for(PotionEffect effect : p.getActivePotionEffects()) {
				p.removePotionEffect(effect.getType());
			}
			p.setGameMode(GameMode.ADVENTURE);
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aIniciaste el nivel " + nivel.getDisplay() + "&a, suerte nya~!"));
		}
	}
	
	private void endParkour(Player p, ParkourLevel nivel) {
		
		if(plugin.getJugando().containsKey(p.getUniqueId())) {
			ParkourPlayer pp = plugin.getJugando().get(p.getUniqueId()); 
			if(pp.getLevel().equals(nivel.getNombre())) {
				//p.getInventory().setContents(pp.getInventory());
				final ItemStack[] inv = PlayerFile2.load(p.getUniqueId());
				if(inv == null)
					p.getInventory().clear();
				else
					p.getInventory().setContents(inv);
				p.setFoodLevel(pp.getFood());
				p.setHealth(Math.max(20.0,pp.getHP()));
				p.setExhaustion(pp.getExhaust());
				for(PotionEffect effect : pp.getEffects()) {
					p.addPotionEffect(effect);
				}
				p.setGameMode(GameMode.SURVIVAL);
				if(!pp.isOnCD()) {
					boolean lleno = false;
					Medal tipo = nivel.compareMedal(pp.getStartTime());								
					
					List<ItemStack> items = new ArrayList<>();
					int mon = 0;
					items.addAll(Arrays.asList(nivel.getPrize(Medal.GENERAL)));
					if(plugin.getEco().hasEco() && nivel.getPrizeM(Medal.GENERAL) != 0) mon += nivel.getPrizeM(Medal.GENERAL);
					
					switch(tipo) {
					default:
						break;
					case GOLD:
						if(nivel.hasMedal(Medal.GOLD)) items.addAll(Arrays.asList(nivel.getPrize(Medal.GOLD)));
						mon += nivel.getPrizeM(Medal.GOLD);
					case SILVER:
						if(nivel.hasMedal(Medal.SILVER)) items.addAll(Arrays.asList(nivel.getPrize(Medal.SILVER)));
						mon +=nivel.getPrizeM(Medal.SILVER);
					case BRONZE:
						if(nivel.hasMedal(Medal.BRONZE)) items.addAll(Arrays.asList(nivel.getPrize(Medal.BRONZE)));
						mon +=nivel.getPrizeM(Medal.BRONZE);
					}
					
					plugin.getEco().givePrize(p, mon);
					
					for(ItemStack item : items) {
						if(item != null) {
							if(p.getInventory().firstEmpty() == -1) {
								if(!lleno && item != null) {
									p.sendMessage(ChatColor.RED + "Tu inventario está lleno, se soltaron algunos items en el suelo.");
									lleno = true;
								}
								p.getWorld().dropItem(p.getLocation(), item);
							} else {
								p.getInventory().addItem(item);
							}
						}
					}
					String m;
					switch(tipo) {
					case GOLD:
						m = " &eha conseguido la medalla de &6&l&oORO&e en el parkour " + nivel.getDisplay() + "&e!";
						break;
					case SILVER:
						m = " &7ha conseguido la medalla de &f&l&oPLATA&7 en el parkour " + nivel.getDisplay() + "&7!";
						break;
					case BRONZE:
						m = " &9ha conseguido la medalla de &6&l&oBRONCE&9 en el parkour " + nivel.getDisplay() + "&9!";
						break;
					default:
						m = " &aha completado el parkour " + nivel.getDisplay() + "&6!";
						break;
					}
					plugin.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&',
							"&f[&bParkour&f] &a&l" +p.getName() + m ));
					if(plugin.getLevelCooldowns().containsKey(p.getUniqueId())) {
						plugin.getLevelCooldowns().get(p.getUniqueId()).put(nivel.getNombre(), System.currentTimeMillis());
					} else {
						HashMap<String, Long> cd = new HashMap<>();
						cd.put(nivel.getNombre(), System.currentTimeMillis());
						plugin.getLevelCooldowns().put(p.getUniqueId(), cd);
					}
				} else {
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', 
							"&f[&bParkour&f] &bCompletaste el nivel " + nivel.getDisplay() + "&b!"));
				}
				for(Player pl : p.getWorld().getPlayers()) {
					p.showPlayer(plugin, pl);
				}
				
				int puesto = nivel.addTime(new ParkourTime(p.getName(), (int) (System.currentTimeMillis() - pp.getStartTime())));
				if(puesto != 0) {
					plugin.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', 
							"&f[&bParkour&f] &a&l" + p.getName()  + "&b ha conseguido el puesto &a&lN°"+ puesto + "&b en el parkour " + nivel.getDisplay() + "&b!"));
				}
				nivel.updateTimeTable();
				new LevelFile(plugin.getNiveles()).save();
				
				plugin.getJugando().remove(p.getUniqueId());
				plugin.getItemCooldowns().remove(p.getUniqueId());

				//new PlayerFile(plugin.getJugando()).save();
			}
		}
	}
	
	private void checkpointGet(Player p, ParkourLevel nivel, Location cpLoc) {
		
		if(plugin.getJugando().containsKey(p.getUniqueId())) {
			ParkourPlayer pp = plugin.getJugando().get(p.getUniqueId()); 
			if(pp.getLevel().equals(nivel.getNombre())) {
				Location cp = null;
				for(int i = 0; i < nivel.getChecks().size(); i++) {
					if(nivel.getCheckB(i).equals(cpLoc)) {
						cp = nivel.getCheck(i);
					}
				}
				
				ItemStack item = p.getInventory().getItem(1);
				if(item != null) {
					ItemMeta meta = item.getItemMeta();
					if(meta != null) {
						if(meta.getDisplayName().equals(ChatColor.AQUA + "" + ChatColor.BOLD + "Volver al Inicio")) {
							meta.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "Ultimo CheckPoint");
							item.setItemMeta(meta);
						}
					}
				}
				
				if(!cp.equals(pp.getLastCP())) {
					pp.setLastCP(cp);
					p.sendMessage(ChatColor.AQUA + "> CheckPoint adquirido!");
				}
			}
		}
	}
	
	private String tiempoRestante(int segs) {
		int h = 0;
		int m = 0;
		int s = segs;
		if(s > 3600) {
			h = new Double(Math.floor(s/3600)).intValue();
			s = new Double(Math.floorMod(s, 3600)).intValue();
		}
		if(s > 60) {
			m = new Double(Math.floor(s/60)).intValue();
			s = new Double(Math.floorMod(s, 60)).intValue();
		}
		String msg = "&cAún te faltan &f&l";
		if(h != 0)
			msg = msg + h + "h&c, &f&l";
		if(m != 0)
			msg = msg + m + "m&c, &f&l";
		msg = msg + s + "s&c para poder recibir los premios del parkour nuevamente.";
		return msg;
	}
	
	private void setInvParkour(Player p) {
		PlayerInventory i = p.getInventory();
		i.clear();
		i.setArmorContents(new ItemStack[4]);
		
		//dar items del parkour
		//reiniciar
		ItemStack restart = new ItemStack(Material.CYAN_DYE);
		ItemMeta meta = restart.getItemMeta();
		meta.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "Volver al Inicio");
		meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		restart.setItemMeta(meta);
		i.setItem(1, restart);
		
		//ver jugadores
		ItemStack see = new ItemStack(Material.SLIME_BALL);
		meta = see.getItemMeta();
		meta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Ocultar Jugadores");
		meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		see.setItemMeta(meta);
		i.setItem(4, see);
		
		//salir
		ItemStack exit = new ItemStack(Material.RED_DYE);
		meta = exit.getItemMeta();
		meta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Salir del Parkour");
		meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		exit.setItemMeta(meta);
		i.setItem(7, exit);
		
	}
	
}
