package me.LynSnow.ParkourNyan.Main;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ParkourLevel implements Serializable {
	private static transient final long serialVersionUID = -1681012206529286338L;
	
	public enum TipoPos{
		START,
		END,
		EXIT,
		CHECK
	};
	
	public enum Medal{
		GENERAL,
		BRONZE,
		SILVER,
		GOLD
	};
	
	private String nombre;
	private String displayName;
	private Location inicio = null;
	private Location meta = null;
	private List<Location> checkPoints;
	private Location exit = null;
	private ItemStack[] prize = new ItemStack[9];
	private ItemStack[][] medalPrize = new ItemStack[3][4];
	private int[] prizeM = new int[4];
	private int cooldown;
	private int alturaMin;
	private boolean enabled;
	private int[] medal = new int[3];
	
	private ParkourTime[] best = new ParkourTime[5];
	private ParkourDisplay timeTable = null;
	
	public ParkourLevel(String nombre, String display) {
		this.nombre = nombre;
		this.displayName = display;
		enabled = false;
		alturaMin = 0;
		
		for(int i = 0; i < 4; i++) {
			prizeM[i] = 0;
			if(i != 3) {
				medal[i] = -1;
			}
		}
		
		checkPoints = new ArrayList<Location>();
		
		for (int i = 0; i < best.length; i++) {
			best[i] = new ParkourTime();
		}
	}
	
	public void setDisplay(String disp) {
		this.displayName = disp;
	}
	public void setInicio(Location inicio){
		this.inicio = inicio;
	}
	public void setMeta(Location meta){
		if(meta != null) {
			this.meta = meta;
			this.meta.setX(meta.getBlockX());
			this.meta.setY(meta.getBlockY());
			this.meta.setZ(meta.getBlockZ());
			this.meta.setPitch(0);
			this.meta.setYaw(0);
		}
	}
	public boolean addCheck(Location check){
		if(this.checkPoints.size() > 5) {
			return false;
		}
		Location cp = check;
		cp.setX(check.getBlockX());
		cp.setY(check.getBlockY());
		cp.setZ(check.getBlockZ());
		cp.setPitch(0);
		this.checkPoints.add(cp);
		return true;
	}
	public void setLow(int y) {
		this.alturaMin = y;
	}
	public void setExit(Location exit) {
		this.exit = exit;
		this.exit.setX(exit.getBlockX() + 0.5);
		this.exit.setY(exit.getBlockY());
		this.exit.setZ(exit.getBlockZ() + 0.5);
		this.exit.setPitch(0);
	}
	public void setCooldown(int cd) {
		this.cooldown = cd;
	}
	
	public void setPrize(ItemStack[] prize, Medal tipo) {
		switch(tipo) {
		case GENERAL:
			this.prize = prize;
			break;
		case BRONZE:
			this.medalPrize[0] = prize;
			break;
		case SILVER:
			this.medalPrize[1] = prize;
			break;
		case GOLD:
			this.medalPrize[2] = prize;
			break;
		}
	}
	
	public void setPrizeM(int prizeM, Medal tipo) {
		switch(tipo) {
			case GENERAL:
				this.prizeM[0] = prizeM;
				break;
			case BRONZE:
				this.prizeM[1] = prizeM;
				break;
			case SILVER:
				this.prizeM[2] = prizeM;
				break;
			case GOLD:
				this.prizeM[3] = prizeM;
				break;
		}
	}
	
	public void setBest(ParkourTime time, int i) {
		this.best[i] = time;
	}
	
	public void setBest(ParkourTime[] times) {
		this.best = times;
	}
	
	public boolean newTimeTable(Player p) {
		if(timeTable != null) {
			if(!delTimeTable()) {
				return false;
			}
		}
		timeTable = new ParkourDisplay(p, this);
		return true;
	}
	
	public boolean delTimeTable() {
		if(timeTable == null) return false;
		if(!timeTable.isLoaded()) return false;
		timeTable.delete();
		timeTable = null;
		return true;
	}
	
	public int addTime(ParkourTime time) {
		int k = -1;
		for(int i = 0; i < best.length; i++) {
			if(best[i].getName().equals(time.getName())) {
				k = i;
			}
		}
		if(k != -1) {
			if(best[k].getTime() > time.getTime()) {
				this.removeFromTimeTable(time.getName().toLowerCase());
			} else {
				return 0;
			}
		}
		for(int i = 0; i < best.length; i++) {
			ParkourTime b = best[i];
			if(time.getTime() < b.getTime() || b.getTime() == 0) {
				for(int j = best.length-1; j > i; j--) {
					best[j] = best[j-1];
				}
				best[i] = time;
				return i+1;
			}
		}
		return 0;
	}
	
	public boolean updateTimeTable() {
		if(timeTable == null) return false;
		if(!timeTable.isLoaded()) return false;
		timeTable.update(this);
		return true;
	}
	
	public void resetBest() {
		for (int i = 0; i < best.length; i++) {
			best[i] = new ParkourTime();
		}
	}
	
	public void setMedal(Medal tipo, int t) {
		switch(tipo) {
		case BRONZE:
			this.medal[0] = t;
			break;
		case SILVER:
			this.medal[1] = t;
			break;
		case GOLD:
			this.medal[2] = t;
			break;
		default:
		}
	}
	
	public void removeFromTimeTable(String nick) {
		for(int i = 0; i < this.best.length; i++) {
			if(this.best[i].getName().toLowerCase().equals(nick)) {
				for(int j = i; j < this.best.length - 1; j++) {
					this.best[j] = this.best[j + 1];
				}
				this.best[best.length-1] = new ParkourTime();
			}
		}
	}
	
	
	
	public String getNombre() {
		return this.nombre;
	}
	public String getDisplay() {
		return this.displayName;
	}
	public Location getInicioB() {
		if(this.inicio == null) return null;
		Location ini = this.inicio.clone();
		ini.setX(this.inicio.getBlockX());
		ini.setY(this.inicio.getBlockY());
		ini.setZ(this.inicio.getBlockZ());
		ini.setYaw(0);
		ini.setPitch(0);
		return ini;
	}
	public Location getInicio() {
		return this.inicio;
	}
	public Location getMeta() {
		return this.meta;
	}
	public Location getCheck(int id) {
		if(id <= this.checkPoints.size() ) {
			Location cp = this.checkPoints.get(id).clone();
			cp.setX(cp.getBlockX() + 0.5);
			cp.setZ(cp.getBlockZ() + 0.5);
			return cp;
		}
		else {
			return null;
		}
	}
	
	public Location getCheckB(int id) {
		if(id < this.checkPoints.size() ) {
			Location cp = this.checkPoints.get(id).clone();
			cp.setX(cp.getBlockX());
			cp.setY(cp.getBlockY());
			cp.setZ(cp.getBlockZ());
			cp.setYaw(0);
			return cp;
		}
		else {
			return null;
		}
	}
	
	public List<Location> getChecks(){
		return this.checkPoints;
	}
	
	public boolean isEnabled() {
		return this.enabled;
	}
	public float getLow() {
		return this.alturaMin;
	}
	public Location getExit() {
		return this.exit;
	}
	public int getCooldown() {
		return this.cooldown;
	}
	
	public ItemStack[] getPrize(Medal tipo) {
		switch(tipo) {
			case BRONZE:
				return this.medalPrize[0];
			case SILVER:
				return this.medalPrize[1];
			case GOLD:
				return this.medalPrize[2];
			default:
				return this.prize;
		}
	}
	
	public ItemStack getPrize(Medal tipo, int i) {
		switch(tipo) {
			case BRONZE:
				return this.medalPrize[0][i];
			case SILVER:
				return this.medalPrize[1][i];
			case GOLD:
				return this.medalPrize[2][i];
			default:
				return this.prize[i];
		}
	}
	
	public int getPrizeM(Medal tipo) {
		switch(tipo) {
			case BRONZE:
				return this.prizeM[1];
			case SILVER:
				return this.prizeM[2];
			case GOLD:
				return this.prizeM[3];
			default:
				return this.prizeM[0];
		}
	}
	
	public boolean hasMedal(Medal tipo) {
		switch(tipo) {
		case BRONZE:
			return this.medal[0] != -1;
		case SILVER:
			return this.medal[1] != -1;
		case GOLD:
			return this.medal[2] != -1;
		default:
			return true;
		}
	}
	
	public boolean hasMedal(int index) {
		if(index == 0) return true;
		return this.medal[index] != -1;
	}
	
	public int getMedal(Medal tipo) {
		switch (tipo) {
		case BRONZE:
			return this.medal[0];
		case SILVER:
			return this.medal[1];
		case GOLD:
			return this.medal[2];
		default:
			return -1;
		}
	}
	
	public Medal compareMedal(int t) {
		Medal m = Medal.GENERAL;
		if(this.hasMedal(Medal.BRONZE)) {
			if(t <= this.medal[0]*100) {
				m = Medal.BRONZE;
			}
		}
		if(this.hasMedal(Medal.SILVER)) {
			if(t <= this.medal[1]*100) {
				m = Medal.SILVER;
			}
		}
		if(this.hasMedal(Medal.GOLD)) {
			if(t <= this.medal[2]*100) {
				m = Medal.GOLD;
			}
		}
		return m;
	}
	
	public boolean enableIfComplete() {
		if(inicio != null && meta != null && exit != null) {
			enabled = true;
		}
		return enabled;
	}
	
	public void disable() {
		this.enabled = false;
	}
	
	public ParkourTime getBest(int i) {
		if(this.best[i] == null) {
			this.best[i] = new ParkourTime();
		}
		return this.best[i];
	}
	
	public ParkourTime[] getBest() {
		return this.best;
	}
	
	public ParkourDisplay getTimeTable() {
		return this.timeTable;
	}
	
	public boolean hasBest(String nick) {
		for(ParkourTime time : this.best) {
			if(time.getName().toLowerCase().equals(nick)) {
				return true;
			}
		}
		return false;
	}
	
	
}
