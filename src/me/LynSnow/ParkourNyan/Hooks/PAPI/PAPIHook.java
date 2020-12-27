package me.LynSnow.ParkourNyan.Hooks.PAPI;

import org.bukkit.Bukkit;

import me.LynSnow.ParkourNyan.Main.ParkourNyan;

public class PAPIHook {
	
	private ParkourNyan pl;
	private boolean hasPAPI;
	
	public PAPIHook(ParkourNyan pl) {
		this.pl = pl;
		if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
            hasPAPI = true;
		else hasPAPI = false;
	}
	
	public void register() {
		if(hasPAPI) {
			new PAPIExpansion(pl).register();
		}
	}
	
}
