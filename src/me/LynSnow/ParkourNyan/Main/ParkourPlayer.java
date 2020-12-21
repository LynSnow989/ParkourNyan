package me.LynSnow.ParkourNyan.Main;

import java.io.Serializable;
import java.util.Collection;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class ParkourPlayer implements Serializable{
	private static transient final long serialVersionUID = -1681012206529286338L;

	private ItemStack[] inventory;
	private int food;
	private double hp;
	private int timer;
	private float exhaust;
	private Collection<PotionEffect> effects;
	private String level;
	private Location exit;
	private Location lastCP;
	private boolean onCD;
	
	public ParkourPlayer(ItemStack[] inv,int food, float exhaust, double hp, Collection<PotionEffect> effects, ParkourLevel level, boolean onCD) {
		this.inventory = inv;
		this.food = food;
		this.exhaust = exhaust;
		this.hp = hp;
		this.effects = effects;
		this.level = level.getNombre();
		this.exit = level.getExit();
		this.lastCP = level.getInicio();
		this.onCD = onCD;
		this.timer = 0;
	}
	
	public ItemStack[] getInventory() {
		return this.inventory;
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
	
	public int getTimer() {
		return this.timer;
	}
	
	public String getTimeString(ParkourLevel level) {
		
		String color = "";
		switch(level.compareMedal(this.getTimer())) {
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
		
		if(timer != 0) {
			int m = 0;
			int s = 0;
			int ms = timer;
			if(ms >= 6000) {
				m = new Double(Math.floor(ms/6000)).intValue();
				ms = new Double(Math.floorMod(ms, 6000)).intValue();
			}
			if(ms >= 100) {
				s = new Double(Math.floor(ms/100)).intValue();
				ms = new Double(Math.floorMod(ms, 100)).intValue();
			}
			return String.format(color + "&l%02d:%02d:%02d", m, s, ms);
		} else {
			return "&e--:--:--";
		}
	}
	
	
	public void setLastCP(Location cp) {
		this.lastCP = cp;
	}
	
	public void updateTimer() {
		this.timer += 10;
	}
	
	public void resetTimer() {
		this.timer = 0;
	}
	
}
