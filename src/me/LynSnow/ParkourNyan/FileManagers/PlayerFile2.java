package me.LynSnow.ParkourNyan.FileManagers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.UUID;
import java.util.logging.Level;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

public class PlayerFile2 implements Serializable{
	private static transient final long serialVersionUID = -1681012206529286338L;
	
	private ItemStack[] inv;
	
	public PlayerFile2(ItemStack[] inv) {
		this.inv = inv;
	}
	

	public boolean save(UUID id) {
		
		File pl = new File("plugins/ParkourNyan/Players");
		if(!pl.exists()) {
			pl.mkdir();
		}
		
		pl = new File("plugins/ParkourNyan/Players/" + id + ".nyan");
		if(!pl.exists()) {
			try {
			pl.createNewFile();
			} catch (IOException e) {
				Bukkit.getServer().getLogger().log(Level.WARNING, "No se pudo crear un archivo de jugador!");
				return false;
			}
		}
		
		try {
			BukkitObjectOutputStream out = new BukkitObjectOutputStream(new GZIPOutputStream(new FileOutputStream("plugins/ParkourNyan/Players/" + id + ".nyan")));
			out.writeObject(this);
			out.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	

	public static ItemStack[] load(UUID id) {

		File pl = new File("plugins/ParkourNyan/Players/" + id + ".nyan");
		if(!pl.exists())
			return null;
		
        try {
            BukkitObjectInputStream in = new BukkitObjectInputStream(new GZIPInputStream(new FileInputStream("plugins/ParkourNyan/Players/" + id + ".nyan")));
            PlayerFile2 file = (PlayerFile2) in.readObject();
            in.close();
            pl.delete();
            return file.inv;	
        } catch (ClassNotFoundException | IOException e1) {
        	Bukkit.getServer().getLogger().log(Level.WARNING, "No se pudo cargar el archivo del jugador " + Bukkit.getPlayer(id).getName() + "!");
            return null;
        }
	}
	
}
