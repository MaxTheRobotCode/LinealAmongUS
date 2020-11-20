package fr.MaxTheRobot.LinealAmongUS;

import java.util.ArrayList;
import java.util.List;

import fr.MaxTheRobot.LinealAmongUS.Object.Map;
import fr.MaxTheRobot.LinealAmongUS.Object.Vent;
import fr.MaxTheRobot.saveAPI.SaveFile;

public class Config {

	public static List<Map> getMap(SaveFile mapSaveFile) {
		List<Map> maps = new ArrayList<Map>();
		for(String l : mapSaveFile.getContent()) {
			Map m = new Map(null, l.split(",")[0], Boolean.valueOf(l.split(",")[1]), new ArrayList<>());
			for(String vl : Main.getSaveFile(m.getName()).getContent()) {
				if(vl.startsWith("V:")) {
					String[] e = vl.substring(2).split("/");
					Vent v = new Vent(m, e[0], Main.locfromstring(e[1], m.getName()), new ArrayList<String>());
					String[] ventsName = e[2].split(":");
					for(String ventName : ventsName) {
						v.getVents().add(ventName);
					}
					m.getVents().add(v);
				} else if(vl.startsWith("S:")) {
					m.setSpawn(Main.locfromstring(vl.substring(2), m.getName()));
				} else if(vl.startsWith("D:")) {
					m.getDoors().add(Main.locfromstring(vl.substring(2), m.getName()));
				}
			}
			maps.add(m);
		}
		return maps;
	}
}
