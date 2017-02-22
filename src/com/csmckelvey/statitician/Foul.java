package com.csmckelvey.statitician;

public class Foul implements StatObject {

	private String displayText;
	private String gameId;
	private String type;
	private String playerId;
	private boolean outcome;
	private int iconId;	
	
	public Foul(String display, String s, String s2, String s3, boolean b, int i) {
		this.displayText = display;
		this.gameId = s;
		this.playerId = s2;
		this.type = s3;
		this.outcome = b;
		this.iconId = i;
	}
	
	public String getDisplayText() {
		return this.displayText;
	}
	public String getGameIdText() {
		return this.gameId;
	}
	public String getQueryText() {
		return "INSERT INTO Fouls VALUES (null, " + this.gameId + ", " + this.playerId + ", \"" + this.type + "\", " + this.outcome + ")";
	}
	public int getIconId() {
		return this.iconId;
	}

}
