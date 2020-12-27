package me.LynSnow.ParkourNyan.Main;

import java.io.Serializable;
import java.util.Collection;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.potion.PotionEffect;

public class ParkourPlayer implements Serializable{
	private static transient final long serialVersionUID = -1681012206529286338L;

	//private ItemStack[] inventory;
	private int food;
	private double hp;
	private long startTime;
	private float exhaust;
	private Collection<PotionEffect> effects;
	private String level;
	private Location exit;
	private Location lastCP;
	private boolean onCD;
	
	public ParkourPlayer(int food, float exhaust, double hp, Collection<PotionEffect> effects, ParkourLevel level, boolean onCD) {
		//this.inventory = inv;
		this.food = food;
		this.exhaust = exhaust;
		this.hp = hp;
		this.effects = effects;
		this.level = level.getNombre();
		this.exit = level.getExit();
		this.lastCP = level.getInicio();
		this.onCD = onCD;
		this.startTime = System.currentTimeMillis();
	}
	

	public String getLevel() {
		return this.level;
	}
	public Location getExit() {
		return this.exit;
	}
	
	public int getFood() {
		return this.food;
	}
	
	public float getExhaust() {
		return this.exhaust;
	}
	
	public double getHP() {
		return this.hp;
	}
	
	public Collection<PotionEffect> getEffects(){
		return this.effects;
	}
	
	public Location getLastCP() {
		return this.lastCP;
	}
	
	public boolean isOnCD() {
		return this.onCD;
	}
	
	public long getStartTime() {
		return this.startTime;
	}
	
	public String getTimeString(ParkourLevel level) {
		
		int time = (int) (System.currentTimeMillis() - startTime)/1000;
		
		String color = "";
		switch(level.compareMedal(this.startTime)) {
		case GOLD:
			color = "&e";
			break;
		case SILVER:
			color = "&f";
			break;
		case BRONZE:
			color = "&6";
			break;
		case GENERAL:
			color = "&8";
			break;
		}
		
		if(time != 0) {
			int m = 0;
			int s = time;
			if(s >= 60) {
				m = new Double(Math.floor(s/60)).intValue();
				s = new Double(Math.floorMod(s, 60)).intValue();
			}
			return String.format(color + "&l%02d:%02d", m, s);
		} else {
			return color + ChatColor.BOLD + "00:00";
		}
	}
	
	
	public void setLastCP(Location cp) {
		this.lastCP = cp;
	}

	
	public void resetTimer() {
		this.startTime = System.currentTimeMillis();
	}
	
}
