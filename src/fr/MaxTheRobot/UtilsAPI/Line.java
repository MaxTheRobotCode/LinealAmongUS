package fr.MaxTheRobot.UtilsAPI;

import java.util.List;

public class Line {

	List<FullColumn> columns;
	public Line(List<FullColumn> columns) {
		this.columns = columns;
	}
	
	public List<FullColumn> getColumns() {
		return columns;
	}
}
