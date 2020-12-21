package me.LynSnow.ParkourNyan.Main;

import java.io.Serializable;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class ParkourDisplay implements Serializable{
	private static transient final long serialVersionUID = -1681012206529286338L;
	
	private UUID[] ids = new UUID[7];
	private Location loc;
	
	public ParkourDisplay(Player p, ParkourLevel level) {
		double sp = 0.23;
		ArmorStand stand;
		loc = p.getLocation();
		loc.setPitch(0);
		loc.setYaw(0);
		for(int i = 0; i < 7; i++) {
			Location loc2 = loc.clone();
			loc2.setY(loc.getY() - sp*(i+1));
			if(i == 0) {
				loc2.setY(loc.getY() + sp);
				stand = (ArmorStand) p.getWorld().spawnEntity(loc2, EntityType.ARMOR_STAND);
				stand.setCustomName(ChatColor.translateAlternateColorCodes('&', level.getDisplay()));
			} else {
				stand = (ArmorStand) p.getWorld().spawnEntity(loc2, EntityType.ARMOR_STAND);
				switch(i) {
				case 1:
					stand.setCustomName(ChatColor.translateAlternateColorCodes('&', "&3Mejores Tiempos: "));
					break;
				default:
					stand.setCustomName(ChatColor.translateAlternateColorCodes('&', "&b" + level.getBest(i-2)));
					break;
				}
			}
			stand.setInvulnerable(true);
			stand.setGravity(false);
			stand.setVisible(false);
			stand.setCustomNameVisible(true);
			stand.setSmall(true);
			
			ids[i] = stand.getUniqueId();
		}
	}
	
	public void update(ParkourLevel level) {
		for(int i = 0; i < 7; i++) {
			ArmorStand stand = (ArmorStand) Bukkit.getEntity(ids[i]);
			
			switch(i) {
			case 0:
				stand.setCustomName(ChatColor.translateAlternateColorCodes('&', level.getDisplay()));
				break;
			case 1:
				stand.setCustomName(ChatColor.translateAlternateColorCodes('&', "&3Mejores Tiempos: "));
				break;
			default:
				stand.setCustomName(ChatColor.translateAlternateColorCodes('&', "&b" + level.getBest(i-2)));
				break;
			}
		}
	}
	
	public boolean isLoaded() {
		for(int i = 0; i < 7; i++) {
			ArmorStand stand = (ArmorStand) Bukkit.getEntity(ids[i]);
			if(stand == null) return false;
		}
		return true;
	}
	
	public void delete() {
		for(int i = 0; i < 7; i++) {
			ArmorStand stand = (ArmorStand) Bukkit.getEntity(ids[i]);
			stand.remove();
		}
	}
	
	
}
