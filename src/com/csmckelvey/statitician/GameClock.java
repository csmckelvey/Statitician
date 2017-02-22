package com.csmckelvey.statitician;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import android.os.Handler;
import android.widget.TextView;

public class GameClock implements Runnable {
	int seconds, minutes;
	Handler handler;
	String secondString;
	String minuteString;
	boolean shouldStop = false;
	
	GameClockEvent event;
	ArrayList<GameClockListener> listeners = new ArrayList<GameClockListener>();
	
	
	public GameClock() {
		this.seconds = 0;
		this.minutes = 0;
		handler = new Handler();
	}
	
	public void run() {
		shouldStop = false;
		while (!shouldStop) {
			if (seconds == 59) { 
				seconds = -1; 
				minutes++;
			}
			secondString = (++seconds < 10) ? "0"+ Integer.toString(seconds) : Integer.toString(seconds);
			minuteString = (minutes < 10) ? "0"+ Integer.toString(minutes) : Integer.toString(minutes);
			for (GameClockListener l : listeners) { l.handleClockEvent(new GameClockEvent(this, minuteString, secondString)); }
			try { TimeUnit.SECONDS.sleep(1); }
			catch (InterruptedException e) { e.printStackTrace(); }
		}
	}
	
	public void shouldStop() { shouldStop = true; }
	public void addGameClockListener(GameClockListener l) { listeners.add(l); }
}
