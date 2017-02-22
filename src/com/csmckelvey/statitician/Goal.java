package com.csmckelvey.statitician;

public class Goal implements StatObject {

	private String displayText;
	private String gameId;
	private String playerId;
	private String type;
	private int iconId;	
	
	public Goal(String display, String s, String s2, String s3, int i) {
		this.displayText = display;
		this.gameId = s;
		this.playerId = s2;
		this.type = s3;
		this.iconId = i;
	}
	
	public String getDisplayText() {
		return this.displayText;
	}
	public String getGameIdText() {
		return this.gameId;
	}
	public String getQueryText() {
		return "INSERT INTO Goals VALUES (null, " + this.gameId + ", " + this.playerId + ", \"" + this.type + "\")";
	}
	public int getIconId() {
		return this.iconId;
	}

}
