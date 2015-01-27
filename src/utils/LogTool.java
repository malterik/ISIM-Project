package utils;

import sebastian.PlannerGUI;

public class LogTool {
	private static PlannerGUI gui = null;
	
	public static void print(String str, String level) {
		if (gui == null) {
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
		else {
			if(level.equals("error") && Config.errors) {
				gui.appendText(str + "\n");
			} else if(level.equals("warning") && Config.warnings) {
				gui.appendText(str + "\n");
			} else if (level.equals("debug") && Config.debug) {
				gui.appendText(str + "\n");
			} else if (level.equals("notification") && Config.notification) {
				gui.appendText(str + "\n");
			}
		}
		
	}

	public static void addGUI(PlannerGUI plannerGui) {
		gui = plannerGui;
	}
}
