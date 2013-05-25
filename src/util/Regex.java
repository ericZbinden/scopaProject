package util;

import java.util.regex.Pattern;

/**
 * @author Eric Zbinden
 * 
 * Util
 *
 */
public class Regex {
 
	/**
	 * @param email the string that needed to be tested as valid email address
	 * @return  If the provided email is a valid one*/
	public static boolean matchEmailPattern(String email){
		return Pattern.matches("^[_a-z0-9-]+(\\.[_a-z0-9-]+)*@[a-z0-9-]+(\\.[a-z0-9-]+)+$", email);
	}
	
	/**
	 * @param ip the string that neededto be tested as a valid ip address
	 * @return If the provided ip address is a valid one
	 */
	public static boolean matchIP4Pattern(String ip){
		
		if(ip.equals("localhost")) return true;
		
		String[] ips = ip.split("\\u002E");
		if(ips.length > 4 || ips.length < 4) return false;
		for(String i : ips){
			int number;
			try{
				number = Integer.valueOf(i);
				if(number < 0 || number > 255) return false;
			}catch(NumberFormatException e){
				return false;
			}
		}
		return true;
	}
}
