package dev.green.commands.impl;

import java.awt.Desktop;

import org.lwjgl.input.Keyboard;

import dev.green.Client;
import dev.green.commands.Command;
import dev.green.extensions.FileManager;
import dev.green.modules.Module;

public class Config extends Command{

	public Config() {
		super("Config", "Allows you to save/load configs","config save | load <name>", "c", "cfg");
	}

	public void onCommand(String[] args, String command) {
		if(args.length == 2) {
			if(args[0].equalsIgnoreCase("save")){
				FileManager.saveMods(args[1]);
				Client.addChatMessage("Saved " + args[1] + " to " + FileManager.FILE_DIR_CONFIG.getAbsolutePath());
			}else if(args[0].equalsIgnoreCase("load")){
				FileManager.loadMods(args[1]);
				Client.addChatMessage("Loaded " + args[1] + " from " + FileManager.FILE_DIR_CONFIG.getAbsolutePath());
			}
		}else if(args.length == 1){
			if(args[0].equalsIgnoreCase("folder")){
				if (Desktop.isDesktopSupported()) {
					
					Desktop desktop = Desktop.getDesktop();
					
					try{
						desktop.open(FileManager.FILE_DIR_CONFIG);
					}catch(Exception ex){
						ex.printStackTrace();
					}

				}
			}
		}else{
			Client.addChatMessage("Usage: .config load/save <name>");
		}
	}

}
