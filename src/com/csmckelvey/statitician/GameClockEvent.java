package com.csmckelvey.statitician;

import java.util.EventObject;

public class GameClockEvent extends EventObject {
private static final long serialVersionUID = 1482616111073487511L;

	String mins;
	String secs;

	public GameClockEvent(Object source, String minutes, String seconds) {
		super(source);
		this.mins = minutes;
		this.secs = seconds;
	}

}
