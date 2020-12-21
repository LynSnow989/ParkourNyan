package me.LynSnow.ParkourNyan.Main;

import java.io.Serializable;

public class ParkourTime implements Serializable {
	private static transient final long serialVersionUID = -1681012206529286338L;
	private String name;
	private int time;
	
	public ParkourTime(String n, int t) {
		this.name = n;
		this.time = Math.min(t, 3599999);
	}
	
	public ParkourTime() {
		this.name = "&oNO_NAME";
		this.time = 0;
	}
	
	@Override
	public String toString() {
		if(time != 0) {
			int m = 0;
			int s = 0;
			int ms = time;
			if(ms >= 6000) {
				m = new Double(Math.floor(ms/6000)).intValue();
				ms = new Double(Math.floorMod(ms, 6000)).intValue();
			}
			if(ms >= 100) {
				s = new Double(Math.floor(ms/100)).intValue();
				ms = new Double(Math.floorMod(ms, 100)).intValue();
			}
			return name + " > " + String.format("%02d:%02d:%02d", m, s, ms);
		} else {
			return name + " >  --:--:--";
		}
	}
	
	public int getTime() {
		return this.time;
	}
	
	public String getName() {
		return this.name;
	}
	
}
