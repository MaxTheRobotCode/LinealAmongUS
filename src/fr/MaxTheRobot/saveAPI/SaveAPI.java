package fr.MaxTheRobot.saveAPI;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class SaveAPI {

	private File srcFolder;
	
	public SaveAPI(File srcFolder) {
		this.srcFolder = srcFolder;
	}
	
	public boolean createEmptyFile(String name) {
		try {
			File file = new File(srcFolder + "/" + name + ".txt");
			if(!file.exists()) {
				file.createNewFile();
				return true;
			} else return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean save(SaveFile file) {
		File f = new File(srcFolder + "/" + file.getName() + ".txt");
		try {
			if(!f.exists()) f.createNewFile();
			List<String> fileContent = file.getContent();
			FileWriter fw =new FileWriter(f);
			BufferedWriter w = new BufferedWriter(fw);
			for(String s : fileContent) {
				w.write(s);
				w.newLine();
			}
			w.flush();
			fw.close();
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	@SuppressWarnings("resource")
	public SaveFile get(String name) {
		File f = new File(srcFolder  + "/" + name + ".txt");
		if(!f.exists()) return null;
		try {
			BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));
			List<String> fileContent = new ArrayList<String>();
			String line = r.readLine();
			while (line != null) {
				fileContent.add(line);
				line = r.readLine();
			}
			return new SaveFile(fileContent, name);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public boolean deleteAll() { for(File f : srcFolder.listFiles()) { f.delete(); } return true; }
}
