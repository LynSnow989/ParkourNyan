package me.LynSnow.ParkourNyan.Main;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;

import me.LynSnow.ParkourNyan.Main.ParkourLevel.Medal;
import net.milkbowl.vault.economy.Economy;

public class ParkourEconomy {
	
	private ParkourNyan plugin;

	private Economy eco;
	private boolean hasEco;
	
	public ParkourEconomy(ParkourNyan plugin) {
		this.plugin = plugin;
	}
	
	public void init() {
		
		if(Bukkit.getPluginManager().getPlugin("Vault") != null) {
			RegisteredServiceProvider<Economy> economy = plugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
			if(economy != null) {
				eco = economy.getProvider();
			} else {
				plugin.getLogger().log(Level.WARNING, "No se encontro un plugin de economia, algunas opciones no estaran disponibles.");
			}
			
			hasEco = (eco != null);
		}else {
			plugin.getLogger().log(Level.WARNING, "No se encontro el plugin Vault, algunas opciones no estaran disponibles.");
			hasEco = false;
		}
	}
	
	public void givePrize(Player p, int mon) {
		if(hasEco && mon != 0) {
			eco.depositPlayer((OfflinePlayer) p, mon);
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&oRecibiste &f$" + mon + "&b&o!"));
		}
	}
	
	public boolean hasEco() {
		return this.hasEco;
	}
	
	public static Inventory fillInv(Inventory inventory, ParkourLevel nivel) {
		
		Inventory inv = inventory;
			
		ItemStack item = new ItemStack(Material.PAPER);
		ItemMeta meta = item.getItemMeta();
		meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, false);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&f&l$" + nivel.getPrizeM(Medal.GENERAL)));
		item.setItemMeta(meta);
		inv.setItem(4, item);
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&f&l$" + nivel.getPrizeM(Medal.GOLD)));
		item.setItemMeta(meta);
		inv.setItem(34, item);
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&f&l$" + nivel.getPrizeM(Medal.SILVER)));
		item.setItemMeta(meta);
		inv.setItem(43, item);
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&f&l$" + nivel.getPrizeM(Medal.BRONZE)));
		item.setItemMeta(meta);
		inv.setItem(52, item);
		
		item = new ItemStack(Material.RED_WOOL);
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&4&l-10$"));
		item.setItemMeta(meta);
		inv.setItem(3, item);
		inv.setItem(33, item);
		inv.setItem(42, item);
		inv.setItem(51, item);
		
		item = new ItemStack(Material.LIME_WOOL);
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&2&l+10$"));
		item.setItemMeta(meta);
		inv.setItem(5, item);
		inv.setItem(35, item);
		inv.setItem(44, item);
		inv.setItem(53, item);
		
		return inv;
	}
	
}
