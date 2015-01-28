package utils;


public class LogTool {
	
	public static void print(String str, String level) {	
		if(level.equals("error") && Config.errors) {
			System.out.println(str);
		} else if(level.equals("warning") && Config.warnings) {
			System.out.println(str);
		} else if (level.equals("debug") && Config.debug) {
			System.out.println(str);
		} else if (level.equals("notification") && Config.notification) {
			System.out.println(str);
		}
	}
}
