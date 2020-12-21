package me.LynSnow.ParkourNyan.Main;

public class HelpCommands {
	
	public final static String[] general = {
		"&9&lSecciones del comando &f/parkournyan help", "",
		"&b&lcreation &f&oCreación de los diferentes puntos de un parkour.",
		"&b&lconfig &f&oManejo de la personalización/premios del parkour.",
		"&b&ltable &f&oManejo de las tablas de tiempo.",
		"&b&lmisc &f&oOtros comandos útiles.",
	};
	
	public final static String[] creation = {
		"&9&lComandos de creación:", "",
		"&b/parkournyan create [parkour] (display) &f&oCrea un nuevo parkour.",
		"&b/parkournyan start [parkour] &f&oSelecciona el punto de inicio.",
		"&b/parkournyan end [parkour] &f&oSelecciona el punto de llegada.",
		"&b/parkournyan exit [parkour] &f&oSelecciona el punto al que se irá al salir o ser kickeado del parkour.",
		"&b/parkournyan checkpoint/cp [parkour] &f&oCrea un CheckPoint."
	};
	
	public final static String[] config = {
		"&9&lComandos de configuración:", "",
		"&b/parkournyan setlow [parkour] (altura) &f&oSelecciona la altura desde la que los usuarios pierden al caer.", 
		"&b/parkournyan display [parkour] [display] &f&oCambia el parkour que se mostrará en el chat.",
		"&b/parkournyan prize [parkour] [add/remove] &f&oAñade/quita premios a un parkour.",
		"&b/parkournyan enable [parkour/all] &f&oActiva un parkour.",
		"&b/parkournyan disable [parkour/all] &f&oDesactiva un parkour.", 
		"&b/parkournyan cooldown/cd [parkour] (segundos) &f&oAñade o quita el tiempo de espera para conseguir los premios del parkour.",
		"&b/parkournyan medal [tipo] [parkour] (segundos) &f&oAñade o quita el tiempo minimo para conseguir una medalla.",
		"&b/parkournyan delete [parkour/all] &f&oElimina un parkour."
	};
	
	public final static String[] misc = {
		"&9&lComandos misceláneos:", "",
		"&b/parkournyan &f&oMuestra información básica del plugin.",
		"&b/parkournyan help (tipo) &f&oMuestra los comandos de ayuda.",
		"&b/parkournyan list &f&oObten la lista de niveles.",
		"&b/parkournyan info [parkour] &f&oMuestra información de un parkour.",
		"&b/parkournyan tp [parkour] &f&oTeletransportate al punto de salida de un parkour.",
		"&b/parkournyan kick [nick] &f&oExpulsa a un jugador del parkour que esté jugando."
	};
	
	public final static String[] table = {
		"&9&lComandos de tablas de tiempos:", "",
		"&b/parkournyan table create [parkour] &f&oCrea (o mueve si ya existe) una tabla de tiempos de parkour.",
		"&b/parkournyan table delete [parkour] &f&oElimina una tabla de tiempos de parkour.",
		"&b/parkournyan table update [parkour] &f&oActualiza una tabla de tiempos de parkour. (Usualmente no es necesario)",
		"&b/parkournyan table reset [parkour] &f&oElimina todos los tiempos guardados del parkour.",
		"&b/parkournyan table remove [parkour] [nick] &f&oElimina a un jugador de una tabla de tiempos.",
	};
	
	
}
