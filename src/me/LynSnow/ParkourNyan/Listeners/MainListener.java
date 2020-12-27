package me.LynSnow.ParkourNyan.Listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.LynSnow.ParkourNyan.FileManagers.LevelFile;
import me.LynSnow.ParkourNyan.FileManagers.PlayerFile2;
import me.LynSnow.ParkourNyan.Main.ParkourLevel;
import me.LynSnow.ParkourNyan.Main.ParkourLevel.Medal;
import me.LynSnow.ParkourNyan.Main.ParkourNyan;
import me.LynSnow.ParkourNyan.Main.ParkourPlayer;

public class MainListener implements Listener{
	
	ParkourNyan plugin;
	
	public MainListener(ParkourNyan plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent ev) {
		if(ev.getAction().equals(Action.RIGHT_CLICK_AIR) || ev.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			Player p = ev.getPlayer();
			ItemStack item = p.getInventory().getItemInMainHand();
			if(item == null) return;
			if(item.getItemMeta() == null) return;
			if(item.getItemMeta().getDisplayName() == null) return;
			if(!plugin.getJugando().containsKey(p.getUniqueId())) return;
			ev.setCancelled(true);
			switch(item.getType()) {
			case CYAN_DYE:
				if((item.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "" + ChatColor.BOLD + "Volver al Inicio") ||
				   item.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "" + ChatColor.BOLD + "Ultimo CheckPoint"))) {
					Long[] cd = {0L, 0L};
					if(plugin.getItemCooldowns().containsKey(p.getUniqueId())) {
						cd = plugin.getItemCooldowns().get(p.getUniqueId());
						if(cd[0] != 0) {
							Long ms = (cd[0]+1000 - System.currentTimeMillis());
							if(ms > 0) return;
						}
					}
					ParkourPlayer pp = plugin.getJugando().get(p.getUniqueId());
					ParkourLevel nivel = plugin.getNiveles().get(pp.getLevel());
					p.teleport(pp.getLastCP(), TeleportCause.PLUGIN);
					if(pp.getLastCP().equals(nivel.getInicio())) {
						p.sendMessage(ChatColor.GRAY + "" +ChatColor.ITALIC + "Volviste al inicio");
						pp.resetTimer();
					} else {
						p.sendMessage(ChatColor.GRAY + "" +ChatColor.ITALIC + "Volviste al ultimo CheckPoint");
					}
					
					cd[0] = System.currentTimeMillis();
					plugin.getItemCooldowns().put(p.getUniqueId(), cd);
				}
				break;
			case SLIME_BALL:
			case MAGMA_CREAM:
				if((item.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "" + ChatColor.BOLD + "Ocultar Jugadores")
				 || item.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "" + ChatColor.BOLD + "Mostrar Jugadores"))) {
					Long[] cd = {0L, 0L};
					if(plugin.getItemCooldowns().containsKey(p.getUniqueId())) {
						cd = plugin.getItemCooldowns().get(p.getUniqueId());
						if(cd[1] != 0) {
							Long ms = (cd[1]+1000 - System.currentTimeMillis());
							if(ms > 0) return;
						}
					}
					if(item.getType() == Material.SLIME_BALL) {
						item.setType(Material.MAGMA_CREAM);
						ItemMeta meta = item.getItemMeta();
						meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Mostrar Jugadores");
						item.setItemMeta(meta);
						for(Player pl : p.getWorld().getPlayers()) {
							p.hidePlayer(plugin, pl);
						}
					} else {
						item.setType(Material.SLIME_BALL);
						ItemMeta meta = item.getItemMeta();
						meta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Ocultar Jugadores");
						item.setItemMeta(meta);
						for(Player pl : p.getWorld().getPlayers()) {
							p.showPlayer(plugin, pl);
						}
					}
					cd[1] = System.currentTimeMillis();
					plugin.getItemCooldowns().put(p.getUniqueId(), cd);
				}
				break;
			case RED_DYE:
				if(item.getItemMeta().getDisplayName().equals(ChatColor.RED + "" + ChatColor.BOLD + "Salir del Parkour")) {
					plugin.kickPlayer(p.getUniqueId(), "&cSaliste del parkour");
				}
				break;
			default:
				break;
			}
		}
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent ev) {
		if (ev.getClickedInventory() == null) return;
		
		if(!ev.getView().getTitle().contains(ChatColor.translateAlternateColorCodes('&', "&d&9Premios del parkour "))) return;
		if(ev.getInventory().getSize() != 54) return;
		if(ev.getCurrentItem() == null) return;
		
		if((ev.getSlot() < 9 || (ev.getSlot() > 17 && ev.getSlot() < 28) ||
				(ev.getSlot() > 31 && ev.getSlot() < 37) ||
				(ev.getSlot() > 40 && ev.getSlot() < 46) ||
				(ev.getSlot() > 49)) &&
				!(ev.getClickedInventory().getHolder() instanceof Player))
			ev.setCancelled(true);
		
		if(plugin.getEco().hasEco()) {
			if(ev.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', "&4&l-10$"))){
				ItemStack item = ev.getClickedInventory().getItem(ev.getSlot() + 1);
				ItemMeta meta = item.getItemMeta();
				int num = Integer.parseInt(ChatColor.stripColor(meta.getDisplayName().replace("$", "")));
				if(num > 0) num -= 10;
				meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&f&l$" + num));
				item.setItemMeta(meta);
			}
			if(ev.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', "&2&l+10$"))){
				ItemStack item = ev.getClickedInventory().getItem(ev.getSlot() - 1);
				ItemMeta meta = item.getItemMeta();
				int num = Integer.parseInt(ChatColor.stripColor(meta.getDisplayName().replace("$", "")));
				num +=10;
				meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&f&l$" + num));
				item.setItemMeta(meta);
			}
		}
	}
	
	@EventHandler
	public void onFall(PlayerMoveEvent ev) {
		final Player p = ev.getPlayer();
		if(p.getFallDistance() > 10F) {
			if(plugin.getJugando().containsKey(p.getUniqueId())) {
				
			}
		}
	}

	@EventHandler
	public void onGUIClose(InventoryCloseEvent ev) {
		if(!ev.getView().getTitle().contains(ChatColor.translateAlternateColorCodes('&', "&d&9Premios del parkour "))) return;
		if(!ev.getPlayer().hasPermission("parkournyan.admin")) return;
		String nom = ev.getView().getTitle().replace(ChatColor.translateAlternateColorCodes('&', "&d&9Premios del parkour &f&l"), "");
		if(!plugin.getNiveles().containsKey(nom)) return;
		Inventory inv = ev.getInventory();
		ParkourLevel nivel = plugin.getNiveles().get(nom);
		//general
		ItemStack[] prize = new ItemStack[9];
		for(int i = 9; i < 18; i++) {
			if(inv.getItem(i) != null)
				prize[i-9] = inv.getItem(i);
		}
		nivel.setPrize(prize, Medal.GENERAL);
		if(plugin.getEco().hasEco()) nivel.setPrizeM(translateMoney(inv.getItem(4)), Medal.GENERAL);
		
		//gold
		prize = new ItemStack[4];
		for(int i = 28; i < 32; i++) {
			if(inv.getItem(i) != null)
				prize[i-28] = inv.getItem(i);
		}
		nivel.setPrize(prize, Medal.GOLD);
		if(plugin.getEco().hasEco()) nivel.setPrizeM(translateMoney(inv.getItem(34)), Medal.GOLD);
		
		//silver
		prize = new ItemStack[4];
		for(int i = 37; i < 41; i++) {
			if(inv.getItem(i) != null)
				prize[i-37] = inv.getItem(i);
		}
		nivel.setPrize(prize, Medal.SILVER);
		if(plugin.getEco().hasEco()) nivel.setPrizeM(translateMoney(inv.getItem(43)), Medal.SILVER);
		
		//bronze
		prize = new ItemStack[4];
		for(int i = 46; i < 50; i++) {
			if(inv.getItem(i) != null)
				prize[i-46] = inv.getItem(i);
		}
		nivel.setPrize(prize, Medal.BRONZE);
		if(plugin.getEco().hasEco()) nivel.setPrizeM(translateMoney(inv.getItem(52)), Medal.BRONZE);
		
		
		new LevelFile(plugin.getNiveles()).save();
		
		ev.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&dSe cerró el menu del nivel &f" + plugin.getNiveles().get(nom).getNombre()));
		
	}
	
	private int translateMoney(ItemStack item) {
		ItemMeta meta = item.getItemMeta();
		try {
			return Integer.parseInt(ChatColor.stripColor(meta.getDisplayName().replace("$", "")));
		} catch(NumberFormatException nfe) {
			return 0;
		}
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent ev) {
		Player p = ev.getPlayer();
		ItemStack[] inv = PlayerFile2.load(p.getUniqueId());
		if(inv != null)
			p.getInventory().setContents(inv);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent ev) {
		final Player p = ev.getPlayer();
		if(plugin.getJugando().containsKey(p.getUniqueId())) {
			plugin.kickPlayer(p.getUniqueId(), "Desconectado");
		}
	}
	
}
