package me.LynSnow.ParkourNyan.Listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent.Cause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import me.LynSnow.ParkourNyan.Main.ParkourLevel;
import me.LynSnow.ParkourNyan.Main.ParkourLevel.TipoPos;
import me.LynSnow.ParkourNyan.Main.ParkourNyan;
import me.LynSnow.ParkourNyan.Main.ParkourPlayer;

public class ActionBlocker implements Listener{
	
	ParkourNyan plugin;
	
	public ActionBlocker(ParkourNyan plugin) {
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
	public void onBlockBreak(BlockBreakEvent ev) {
		Block block = ev.getBlock();
		ParkourLevel nivel;
		if(block.getType() == Material.LIGHT_WEIGHTED_PRESSURE_PLATE){
			nivel = encuentraPos(block.getLocation(), TipoPos.START);
			if(nivel != null) {
				Player p = ev.getPlayer();
				if(p.hasPermission("parkournyan.admin")) {
					if(p.isSneaking()) {
						nivel.setInicio(null);
						nivel.disable();
						p.sendMessage(ChatColor.GREEN + "Se removió el inicio del parkour " + nivel.getNombre() + " exitosamente!");
					} else {
						p.sendMessage(ChatColor.AQUA + "" + ChatColor.ITALIC + "Para desactivar el inicio, rompe el bloque shifteando");
						ev.setCancelled(true);
					}
				}else {
					p.sendMessage(ChatColor.RED + "No tienes permiso de quitar este bloque!");
					ev.setCancelled(true);
					return;
				}
			}
		}
		if(block.getType() == Material.HEAVY_WEIGHTED_PRESSURE_PLATE){	
			nivel = encuentraPos(block.getLocation(), TipoPos.END);
			if(nivel != null) {
				Player p = ev.getPlayer();
				if(p.hasPermission("parkournyan.admin")) {
					if(p.isSneaking()) {
						nivel.setMeta(null);
						nivel.disable();
						p.sendMessage(ChatColor.GREEN + "Se removió el final del parkour " + nivel.getNombre() + " exitosamente!");
					} else {
						p.sendMessage(ChatColor.AQUA + "" + ChatColor.ITALIC + "Para desactivar el final, rompe el bloque shifteando");
						ev.setCancelled(true);
					}
				}else {
					p.sendMessage(ChatColor.RED + "No tienes permiso de quitar este bloque!");
					ev.setCancelled(true);
					return;
				}
			}
		}
		if(block.getType() == Material.BIRCH_PRESSURE_PLATE){	
			nivel = encuentraPos(block.getLocation(), TipoPos.CHECK);
			if(nivel != null) {
				Player p = ev.getPlayer();
				if(p.hasPermission("parkournyan.admin")) {
					for(int i = 0; i < nivel.getChecks().size(); i++) {
						if(nivel.getCheckB(i).equals(block.getLocation())) {
							if(p.isSneaking()) {
								nivel.getChecks().remove(i);
								p.sendMessage(ChatColor.GREEN + "Se removió un checkpoint del parkour " + nivel.getNombre() + " exitosamente!");
							} else {
								p.sendMessage(ChatColor.AQUA + "" + ChatColor.ITALIC + "Para desactivar el checkpoint, rompe el bloque shifteando");
								ev.setCancelled(true);
							}
						}
					}
				}else {
					p.sendMessage(ChatColor.RED + "No tienes permiso de quitar este bloque!");
					ev.setCancelled(true);
					return;
				}
			}
		}
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent ev) {
		if (ev.getClickedInventory() == null) return;
		if(plugin.getJugando().containsKey(ev.getWhoClicked().getUniqueId())){
			ev.setCancelled(true);
			return;
		}
	}

	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent ev) {
		if(plugin.getJugando().containsKey(ev.getPlayer().getUniqueId()) && !ev.getPlayer().hasPermission("parkournyan.admin")) {
			ev.getPlayer().sendMessage(ChatColor.RED + "No puedes usar comandos mientras estas en el parkour!");
			ev.setCancelled(true);
		}
	}
	
	@EventHandler 
	public void onDroppedItem(PlayerDropItemEvent ev){
		if(plugin.getJugando().containsKey(ev.getPlayer().getUniqueId())) {
			ev.setCancelled(true);
		}
	}
	
	@EventHandler 
	public void onSwapHands(PlayerSwapHandItemsEvent ev){
		if(plugin.getJugando().containsKey(ev.getPlayer().getUniqueId())) {
			ev.setCancelled(true);
		}
	}
	
	@EventHandler 
	public void onPickupItem(EntityPickupItemEvent ev){
		if(ev.getEntityType() == EntityType.PLAYER) {
			if(plugin.getJugando().containsKey(ev.getEntity().getUniqueId())) {
				ev.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onFoodChange(FoodLevelChangeEvent ev) {
		if(plugin.getJugando().containsKey(ev.getEntity().getUniqueId())) {
			ev.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent ev) {
		if(!(ev.getEntity() instanceof Player)) return;
		Player p = (Player) ev.getEntity();
		if(plugin.getJugando().containsKey(p.getUniqueId())) {
			ev.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onEffect(EntityPotionEffectEvent ev) {
		if(!(ev.getEntity() instanceof Player)) return;
		if(!plugin.getJugando().containsKey(((Player) ev.getEntity()).getUniqueId())) return;
		if(ev.getCause() == Cause.PLUGIN) return;
		ev.setCancelled(true);
	}
	
	@EventHandler
	public void onTeleport(PlayerTeleportEvent ev) {
		Player p = ev.getPlayer();
		if(!plugin.getJugando().containsKey(p.getUniqueId())) return;
		ParkourPlayer pp = plugin.getJugando().get(p.getUniqueId());
		if(!ev.getTo().equals(pp.getExit()) && !ev.getTo().equals(pp.getLastCP()) && !ev.getTo().equals(plugin.getNiveles().get(pp.getLevel()).getInicio())){
			p.sendMessage(ChatColor.RED + "No te puedes teletransportar en el parkour!");
			ev.setCancelled(true);
		}
	}
	
}
