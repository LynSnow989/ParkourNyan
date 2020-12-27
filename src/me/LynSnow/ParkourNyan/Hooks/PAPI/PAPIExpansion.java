package me.LynSnow.ParkourNyan.Hooks.PAPI;

import org.bukkit.entity.Player;

import me.LynSnow.ParkourNyan.Main.ParkourLevel;
import me.LynSnow.ParkourNyan.Main.ParkourNyan;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class PAPIExpansion extends PlaceholderExpansion{

	private ParkourNyan pl;
	
	public PAPIExpansion(ParkourNyan pl) {
		this.pl = pl;
	}
	
    @Override
    public boolean persist(){
        return true;
    }
    
    @Override
    public boolean canRegister(){
        return true;
    }
	
	@Override
	public String getAuthor() {
		return pl.getDescription().getAuthors().toString();
	}

	@Override
	public String getIdentifier() {
		return "pnyan";
	}

	@Override
	public String getVersion() {
		return pl.getDescription().getVersion();
	}
	
    @Override
    public String onPlaceholderRequest(Player player, String identifier){

        if(player == null){
            return "";
        }
        
        String[] args = identifier.split("_");
        
        if(!pl.getNiveles().containsKey(args[0])) return null;
        final ParkourLevel lvl = pl.getNiveles().get(args[0]);
        
        if(args.length < 2) return null;
        
      //%pnyan_nivel_time_x%
        if(args[1].equals("time")){
        	if(args.length < 3) return null;
        	final String time;
        	try{
        		final int timeID = Integer.valueOf(args[2]);
        		time = lvl.getBest(timeID).toString();
        	} catch (NumberFormatException e) {
        		return null;
        	}
        	return time;
        }
      //%pnyan_nivel_disp%
        if(args[1].equals("disp")) {
        	return lvl.getDisplay();
        }

        
        return null;
    }

}
