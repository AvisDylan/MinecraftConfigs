package dev.green.extensions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import dev.green.Client;
import dev.green.modules.Module;
import dev.green.modules.render.ClickGui;
import dev.green.settings.BooleanSetting;
import dev.green.settings.ModeSetting;
import dev.green.settings.NumberSetting;
import dev.green.settings.Setting;
import dev.green.util.FileUtil;

public class FileManager {
	
	public static File FILE_DIR_CONFIG = new File("Green\\Config");
	public static File file;
	
	public static void init(String name){		
		file = new File(FILE_DIR_CONFIG, name + ".json");
		
		if(!FILE_DIR_CONFIG.exists()){
			FILE_DIR_CONFIG.mkdirs();
		}
		
		if(!file.exists()) {
	        saveMods(name);
	    } else {
	        loadMods(name);
	    }
		
	}
	
	public static void saveMods(String name){
		file = new File(FILE_DIR_CONFIG, name + ".json");
		
		try{
			JsonObject json = new JsonObject();
			for(Module m : Client.modules){
				JsonObject jsonState = new JsonObject();
				jsonState.addProperty("enabled", m.toggled);
				jsonState.addProperty("key", m.getKey());
				for(Setting s : m.settings){
					if(s instanceof BooleanSetting){
						jsonState.addProperty(s.name, ((BooleanSetting) s).enabled);
					}else if(s instanceof NumberSetting){
						jsonState.addProperty(s.name, ((NumberSetting) s).getValue());
					}else if(s instanceof ModeSetting){
						jsonState.addProperty(s.name, ((ModeSetting) s).getMode());
					}
				}
				json.add(m.name, jsonState);
			}
			PrintWriter save = new PrintWriter(new FileWriter(file));
			save.println(FileUtil.prettyGson.toJson(json));
			save.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void loadMods(String name) {
	    file = new File(FILE_DIR_CONFIG, name + ".json");
	    
	    try {
	        BufferedReader load = new BufferedReader(new FileReader(file));
	        JsonObject json = (JsonObject) FileUtil.jsonParser.parse(load);
	        load.close();
	        Iterator<Entry<String, JsonElement>> itr = json.entrySet().iterator();
	        while (itr.hasNext()) {
	            Entry<String, JsonElement> entry = itr.next();
	            String moduleName = entry.getKey();
	            Module m = Client.getModuleByName(moduleName);
	            if (m == null) {
	                continue;
	            }
	            JsonObject jsonState = (JsonObject) entry.getValue();
	            if (jsonState.has("enabled")) {
	                boolean enabled = jsonState.get("enabled").getAsBoolean();
	                if (enabled) {
	                    m.toggle();
	                }
	            }
	            if (jsonState.has("key")) {
	                int keyCode = jsonState.get("key").getAsInt();
	                m.keyCode = keyCode;
	            }
	            for (Setting s : m.settings) {
	                String settingName = s.name;
	                if (jsonState.has(settingName)) {
	                    if (s instanceof BooleanSetting) {
	                        ((BooleanSetting) s).enabled = jsonState.get(settingName).getAsBoolean();
	                    } else if (s instanceof NumberSetting) {
	                        ((NumberSetting) s).setValue(jsonState.get(settingName).getAsDouble());
	                    } else if (s instanceof ModeSetting) {
	                        ((ModeSetting) s).setMode(jsonState.get(settingName).getAsString());
	                    }
	                }
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

}
