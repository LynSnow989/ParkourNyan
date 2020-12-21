package me.LynSnow.ParkourNyan.FileManagers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.bukkit.Bukkit;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import me.LynSnow.ParkourNyan.Main.ParkourLevel;

public class LevelFile implements Serializable{
	private static transient final long serialVersionUID = -1681012206529286338L;
	
	private HashMap<String,ParkourLevel> levels = new HashMap<>();
	
	public LevelFile(HashMap<String,ParkourLevel> levels) {
		this.levels = levels;
	}
	

	public boolean save() {
		File pl = new File("plugins/ParkourNyan/niveles.data");
		File bkp = new File("plugins/ParkourNyan/niveles.backup");
		pl.renameTo(bkp);
		
		pl = new File("plugins/ParkourNyan");
		if(!pl.exists()) {
			pl.mkdir();
		}
		
		pl = new File("plugins/ParkourNyan/niveles.data");
		if(!pl.exists()) {
			try {
			pl.createNewFile();
			} catch (IOException e) {
				Bukkit.getServer().getLogger().log(Level.WARNING, "No se pudo crear un archivo de niveles");
				return false;
			}
		}
		
		try {
			BukkitObjectOutputStream out = new BukkitObjectOutputStream(new GZIPOutputStream(new FileOutputStream("plugins/ParkourNyan/niveles.data")));
			out.writeObject(this);
			out.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	public static HashMap<String,ParkourLevel> load() {

        try {
            BukkitObjectInputStream in = new BukkitObjectInputStream(new GZIPInputStream(new FileInputStream("plugins/ParkourNyan/niveles.data")));
            LevelFile file = (LevelFile) in.readObject();
            in.close();
            return file.levels;
        } catch (ClassNotFoundException | IOException e1) {
        	Bukkit.getServer().getLogger().log(Level.WARNING, "No se pudo cargar el archivo de niveles, intentando backup...");
            try {
                BukkitObjectInputStream in = new BukkitObjectInputStream(new GZIPInputStream(new FileInputStream("plugins/ParkourNyan/niveles.backup")));
                LevelFile file = (LevelFile) in.readObject();
                in.close();
                return file.levels;
            } catch (ClassNotFoundException | IOException e2) {
            	Bukkit.getServer().getLogger().log(Level.WARNING, "No se pudo cargar el backup de niveles");
                return new HashMap<String, ParkourLevel>();
            }
        }
	}
}
