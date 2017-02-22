package com.csmckelvey.statitician;

public class Assist implements StatObject {

	private String displayText;
	private String gameId;
	private String playerId;
	private int iconId;	
	
	public Assist(String display, String s, String s2, int i) {
		this.displayText = display;
		this.gameId = s;
		this.playerId = s2;
		this.iconId = i;
	}
	
	public String getDisplayText() {
		return this.displayText;
	}
	public String getGameIdText() {
		return this.gameId;
	}
	public String getQueryText() {
		return "INSERT INTO Assists VALUES (null, " + this.gameId + ", " + this.playerId + ")";
	}
	public int getIconId() {
		return this.iconId;
	}

}
