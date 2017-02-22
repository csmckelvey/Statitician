package com.csmckelvey.statitician;

public class Pass implements StatObject {

	private String displayText;
	private String gameId;
	private String playerId;
	private boolean outcome;
	private int iconId;	
	
	public Pass(String display, String s, String s2, boolean b, int i) {
		this.displayText = display;
		this.gameId = s;
		this.playerId = s2;
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
		return "INSERT INTO Passes VALUES (null, " + this.gameId + ", " + this.playerId + ", " + this.outcome + ")";
	}
	public int getIconId() {
		return this.iconId;
	}

}
