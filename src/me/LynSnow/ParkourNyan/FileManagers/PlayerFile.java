package me.LynSnow.ParkourNyan.FileManagers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.bukkit.Bukkit;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import me.LynSnow.ParkourNyan.Main.ParkourPlayer;

@Deprecated
public class PlayerFile implements Serializable{
	private static transient final long serialVersionUID = -1681012206529286338L;
	
	private HashMap<UUID,ParkourPlayer> players = new HashMap<>();
	
	public PlayerFile(HashMap<UUID,ParkourPlayer> players) {
		this.players = players;
	}
	

	public boolean save() {
		File pl = new File("plugins/ParkourNyan/jugadores.data");
		File bkp = new File("plugins/ParkourNyan/jugadores.backup");
		pl.renameTo(bkp);
		
		pl = new File("plugins/ParkourNyan");
		if(!pl.exists()) {
			pl.mkdir();
		}
		
		pl = new File("plugins/ParkourNyan/jugadores.data");
		if(!pl.exists()) {
			try {
			pl.createNewFile();
			} catch (IOException e) {
				Bukkit.getServer().getLogger().log(Level.WARNING, "No se pudo crear un archivo de jugadores");
				return false;
			}
		}
		
		try {
			BukkitObjectOutputStream out = new BukkitObjectOutputStream(new GZIPOutputStream(new FileOutputStream("plugins/ParkourNyan/jugadores.data")));
			out.writeObject(this);
			out.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	

	public static HashMap<UUID,ParkourPlayer> load() {

        try {
            BukkitObjectInputStream in = new BukkitObjectInputStream(new GZIPInputStream(new FileInputStream("plugins/ParkourNyan/jugadores.data")));
            PlayerFile file = (PlayerFile) in.readObject();
            in.close();
            return file.players;
        } catch (ClassNotFoundException | IOException e1) {
        	Bukkit.getServer().getLogger().log(Level.WARNING, "No se pudo cargar el archivo de jugadores, intentando backup...");
            try {
                BukkitObjectInputStream in = new BukkitObjectInputStream(new GZIPInputStream(new FileInputStream("plugins/ParkourNyan/jugadores.backup")));
                PlayerFile file = (PlayerFile) in.readObject();
                in.close();
                return file.players;
            } catch (ClassNotFoundException | IOException e2) {
            	Bukkit.getServer().getLogger().log(Level.WARNING, "No se pudo cargar el backup de jugadores");
                return new HashMap<UUID, ParkourPlayer>();
            }
        }
	}
	
}
