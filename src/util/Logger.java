package util;

public class Logger {

	static boolean DEBUG = true;
	
	public static void log(String msg){
		System.out.println(msg);
	}
	
	public static void debug(String msg){
		if(DEBUG)
			log("debug>" +msg);
	}
	
	public static void error(String msg){
		log("error> "+msg);
	}
	
	public static void dot(){
		if(DEBUG)
			log("----------------------------------------");
	}
}
