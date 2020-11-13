package fr.MaxTheRobot.UtilsAPI;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SQL {
	
	private String host, name, user, pass;
	private int port;
	
	private Connection connection;
	private Statement statement;
	
	public SQL(String host, int port, String name, String user, String pass) {
		this.host = host;
		this.name = name;
		this.pass = pass;
		this.port = port;
		this.user = user;
		
		connect();
	}
	
	public void connect() {
		try {
			if(this.connection != null && !this.connection.isClosed()) return;
			Class.forName("com.mysql.jdbc.Driver");
			this.connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.name + "?autoReconnect=true", this.user, this.pass);
			this.statement = this.connection.createStatement();
			System.out.println("[LinealMC] SQL connection enabled !");
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isConnected() {
		try {
			if(this.connection != null && !this.connection.isClosed()) return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public void disconnect() {
		try {
			if(isConnected()) this.connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public Object get(String request, String column, Object object) {
		try {
			PreparedStatement ps = this.connection.prepareStatement(request);
			ResultSet r = ps.executeQuery();
			if(r.next()) {
				if(object == String.class) {
					return r.getString(column);
				} else if(object == Integer.class) {
					return r.getInt(column);
				} else if(object == Boolean.class) {
					return r.getBoolean(column);
				}
			}
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public List<Line> getLines(String request, List<String> columns){
		try {
			PreparedStatement ps = this.connection.prepareStatement(request);
			ResultSet r = ps.executeQuery();
			List<Line> lines = new ArrayList<Line>();
			while (r.next()) {
				List<FullColumn> fcs = new ArrayList<FullColumn>();
				for(String c : columns) {
					fcs.add(new FullColumn(c, r.getString(c)));
				}
				lines.add(new Line(fcs));
			}
			return lines;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Line getLine(String request, String column) {
		Line l = new Line(new ArrayList<FullColumn>());
		l.getColumns().add(new FullColumn(column, (String) get(request, column, String.class)));
		return l;
	}
	
	public void execute(String request) {
		try {
			this.statement.execute(request);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
