package me.LynSnow.ParkourNyan.Hooks;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import me.LynSnow.ParkourNyan.Main.ParkourLevel;
import me.LynSnow.ParkourNyan.Main.ParkourNyan;

@Deprecated
public class HDHook {

	private ParkourNyan pl;
	private boolean hd;
	
	public HDHook(ParkourNyan pl) {
		this.pl = pl;
		if(Bukkit.getPluginManager().getPlugin("HolographicDisplays") != null)
            hd = true;
		else {
			pl.getLogger().log(Level.WARNING, "No se encontro el plugin HolographicDisplays, algunas opciones no estaran disponibles.");
			hd = false;
		}
	}
	
	public boolean hasHD() {
		return this.hd;
	}
	
	public void createHD(ParkourLevel lvl, Location loc) {
		if(!hd) return;
		
		Hologram hol = HologramsAPI.createHologram(pl, loc);
		hol.appendTextLine("%pnyan_" + lvl.getNombre() + "_disp%");
		hol.appendTextLine("");
		hol.appendTextLine(ChatColor.DARK_AQUA + "Mejores Tiempos:");
		hol.appendTextLine(ChatColor.AQUA + "%pnyan_" + lvl.getNombre() + "_time_0%");
		hol.appendTextLine(ChatColor.AQUA + "%pnyan_" + lvl.getNombre() + "_time_1%");
		hol.appendTextLine(ChatColor.AQUA + "%pnyan_" + lvl.getNombre() + "_time_2%");
		hol.appendTextLine(ChatColor.AQUA + "%pnyan_" + lvl.getNombre() + "_time_3%");
		hol.appendTextLine(ChatColor.AQUA + "%pnyan_" + lvl.getNombre() + "_time_4%");
		
		lvl.getTables().add(loc);
	}
	
}
