package fr.MaxTheRobot.saveAPI;

import java.util.List;

public class SaveFile {

	private List<String> FileContent;
	private String name;
		
	public SaveFile(List<String> fileContent, String name) {
		FileContent = fileContent;
		this.name = name;
	}

	public boolean addContent(List<String> content) {
		if(content.size() == 0) return false;
		content.forEach(s -> FileContent.add(s));
		return true;
	}
	
	public boolean addContent(String content) {
		FileContent.add(content);
		return true;
	}
	
	public List<String> getContent() {
		return FileContent;
	}
	
	public String getName() {
		return name;
	}
}
