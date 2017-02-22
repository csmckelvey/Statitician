package com.csmckelvey.statitician;

import java.util.Set;
import java.util.TreeSet;
import java.util.Calendar;
import java.util.ArrayList;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.UnsupportedEncodingException;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.client.ClientProtocolException;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.os.Handler;
import android.widget.Toast;
import android.app.Activity;
import android.widget.Button;
import android.content.Intent;
import android.widget.EditText;
import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

public class StartNewGameActivity extends Activity {

	private Thread newgameThread;
	private Thread autocompleteThread;
	private StatWebService webService;
	
	private String dayText;
	private String yearText;
	private String monthText;
	private String locationText;
	private String opponentText;
	private String tournamentText;
	
	private EditText date_day;
	private EditText date_year;
	private EditText date_month;
	private Button submitButton;
	private AutoCompleteTextView opponent;
	private AutoCompleteTextView location;
	private AutoCompleteTextView tournament;
	
	private Intent intent;
	private Handler handler;
	private Context myContext;
	
	private String playerId;
	private Calendar rightNow;	
	private BufferedReader results;
	private String feedbackString = "";
	private ArrayAdapter<String> adapter;
	private Set<String> teamList = new TreeSet<String>();
	private Set<String> locationList = new TreeSet<String>();
	private Set<String> tournamentList = new TreeSet<String>();
	private ArrayList<BasicNameValuePair> vars = new ArrayList<BasicNameValuePair>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start_new_game);
		
		init();
		getViews();
		setAutocompleteList();
		setDate();
		submitButton.setOnClickListener(getSubmitButtonListener());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.start_new_game, menu);
		return true;
	}
	
	private void setAutocompleteList() {
		autocompleteThread = new Thread(new Runnable() {
			public void run() {
				vars.clear();
				teamList.clear();
				locationList.clear();
				tournamentList.clear();
				vars.add(new BasicNameValuePair("playerId", playerId));
				try {
					results = webService.deploy("http://www.csmckelvey.com/webServices/teamList.php", vars);
					while ((feedbackString = results.readLine()) != null) { teamList.add(feedbackString); }
					results = webService.deploy("http://www.csmckelvey.com/webServices/locationList.php", vars);
					while ((feedbackString = results.readLine()) != null) { locationList.add(feedbackString); }
					results = webService.deploy("http://www.csmckelvey.com/webServices/tournamentList.php", vars);
					while ((feedbackString = results.readLine()) != null) { tournamentList.add(feedbackString); }
					
					handler.post(new Runnable() {
						public void run() {
							adapter = new ArrayAdapter<String>(myContext, android.R.layout.simple_dropdown_item_1line, new ArrayList<String>(teamList));
							opponent.setAdapter(adapter);
						}});
					handler.post(new Runnable() {
						public void run() {
							adapter = new ArrayAdapter<String>(myContext, android.R.layout.simple_dropdown_item_1line, new ArrayList<String>(tournamentList));
							tournament.setAdapter(adapter);
						}});
					handler.post(new Runnable() {
						public void run() {
							adapter = new ArrayAdapter<String>(myContext, android.R.layout.simple_dropdown_item_1line, new ArrayList<String>(locationList));
							location.setAdapter(adapter);
						}});
				} 
				catch (ClientProtocolException e) {e.printStackTrace();}
				catch (IOException e) {e.printStackTrace();}	
			}
		});
		autocompleteThread.start();
	}
	private void init() {
		myContext = this;
		handler = new Handler();
		rightNow = Calendar.getInstance();
		webService = new StatWebService(myContext);
		playerId = getIntent().getExtras().get("playerId").toString();
	}
	private void getViews() {
		date_day = (EditText)findViewById(R.id.dateDay);
		date_year = (EditText)findViewById(R.id.dateYear);
		date_month = (EditText)findViewById(R.id.dateMonth);
		submitButton = (Button)findViewById(R.id.submitButton);
		opponent = (AutoCompleteTextView)findViewById(R.id.opponent);
		location = (AutoCompleteTextView)findViewById(R.id.location);
		tournament = (AutoCompleteTextView)findViewById(R.id.tournament);
		opponent.setThreshold(1);
		location.setThreshold(1);
		tournament.setThreshold(1);
	}	
	private void setDate() {
		date_month.setText(Integer.toString(rightNow.get(Calendar.MONTH) + 1)); //MONTH + 1 Because January == 0
		date_day.setText(Integer.toString(rightNow.get(Calendar.DAY_OF_MONTH)));
		date_year.setText(Integer.toString(rightNow.get(Calendar.YEAR)));
	}
	private void startDashboard(String id, String playerid) {
		intent = new Intent(myContext, DashboardActivity.class);
		intent.putExtra("gameId", id);
		intent.putExtra("playerId", playerid);
		handler.post(new Runnable() { public void run() { startActivity(intent); } });
	}

	private View.OnClickListener getSubmitButtonListener() {
		return new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				dayText = date_day.getText().toString();
				yearText = date_year.getText().toString();
				monthText = date_month.getText().toString();
				locationText = location.getText().toString();
				opponentText = opponent.getText().toString();
				tournamentText = tournament.getText().toString();
				
				if (!locationText.equals("") && !opponentText.equals("") && !dayText.equals("")&& !monthText.equals("")&& !yearText.equals("")) {
					vars.clear();
					vars.add(new BasicNameValuePair("date_month", monthText));
					vars.add(new BasicNameValuePair("date_day", dayText));
					vars.add(new BasicNameValuePair("date_year", yearText));
					vars.add(new BasicNameValuePair("location", locationText));
					vars.add(new BasicNameValuePair("tournament", tournamentText));
					vars.add(new BasicNameValuePair("opponent", opponentText));
					vars.add(new BasicNameValuePair("playerId", playerId));
					
					newgameThread = new Thread(new Runnable() {
						public void run() {
							try {
								results = webService.deploy("http://www.csmckelvey.com/webServices/addNewGame.php", vars);
								if (results.readLine().equals("PASS")) { 
									handler.post(webService.successToast());
									startDashboard(results.readLine(), playerId);
								}
								else { handler.post(webService.failedToast()); }
							} 
							catch (UnsupportedEncodingException e) {e.printStackTrace();} 
							catch (ClientProtocolException e) {e.printStackTrace();} 
							catch (IOException e) {e.printStackTrace();}
						}
					});
					newgameThread.start();
					Toast.makeText(myContext, "Starting New Game ...", Toast.LENGTH_SHORT).show();
				}
				else { Toast.makeText(myContext, "Missing date, location, or opponent ...", Toast.LENGTH_LONG).show(); }
			}
		};
	}
}




















































