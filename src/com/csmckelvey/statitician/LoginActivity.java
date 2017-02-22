package com.csmckelvey.statitician;

import java.io.IOException;
import java.util.ArrayList;
import java.io.BufferedReader;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.os.Handler;
import android.widget.Toast;
import android.app.Activity;
import android.widget.Button;
import android.content.Intent;
import android.widget.CheckBox;
import android.widget.EditText;
import android.content.Context;
import android.content.SharedPreferences;

import org.apache.http.message.BasicNameValuePair;

public class LoginActivity extends Activity {
	
	private Button exit;
	private String playerId;
	private CheckBox saveBox;
	private String resultText;
	private EditText emailView;
	private Button submitButton;
	private CheckBox registerBox;
	private EditText passwordView;
	
	private Intent intent;
	private Handler handler;
	private Context myContext;
	private Thread loginThread;
	private BufferedReader results;
	private StatWebService webService;
	private SharedPreferences settings;
	private SharedPreferences.Editor settingsEdit;
	private ArrayList<BasicNameValuePair> credentials = new ArrayList<BasicNameValuePair>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		init();
		grabViews();
		checkPreferences();
		submitButton.setOnClickListener(getSubmitButtonListener());
		exit.setOnClickListener(new View.OnClickListener() { public void onClick(View v) { finish(); } });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}	
	
	private void init() {
		myContext = this;
		handler = new Handler();
		webService = new StatWebService(myContext);
		settings = myContext.getSharedPreferences("credentials", Context.MODE_PRIVATE);
		settingsEdit = settings.edit();
	}
	private void grabViews() {
		exit = (Button)findViewById(R.id.exit);
		emailView = (EditText)findViewById(R.id.email);
		saveBox = (CheckBox)findViewById(R.id.saveBox);
		passwordView = (EditText)findViewById(R.id.password);
		registerBox = (CheckBox)findViewById(R.id.registerBox);
		submitButton = (Button)findViewById(R.id.submit_button);
	}
	private void checkPreferences() {
		if (settings.getBoolean("save", false)) {
			saveBox.setChecked(true);
			emailView.setText(settings.getString("email", ""));
			passwordView.setText(settings.getString("password", ""));
		}
	}
	private Thread getLoginThread() {
		return new Thread(new Runnable() {
			public void run() {
				results = webService.deploy("http://www.csmckelvey.com/webServices/login.php", credentials);
				try {
					resultText = results.readLine();
					if ((resultText != null) && (resultText.equals("PASS"))) {
						playerId = results.readLine();
						handler.post(webService.successToast());
						handler.post(new Runnable() {
							public void run() {
								intent = new Intent(myContext, HomepageActivity.class);
								intent.putExtra("playerId", playerId);
								startActivity(intent);
							}
						});
					}
					else { handler.post(webService.loginFailedToast()); }
					results.close();
				} 
				catch (IOException e) { e.printStackTrace(); }
			}
		});
	}
	private View.OnClickListener getSubmitButtonListener() {
		return new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (saveBox.isChecked()) {
					settingsEdit.putBoolean("save", true);
					settingsEdit.putString("email", emailView.getText().toString());
					settingsEdit.putString("password", passwordView.getText().toString());
				}
				else {
					settingsEdit.putString("email", "");
					settingsEdit.putString("password", "");
					settingsEdit.putBoolean("save", false);
				}
				settingsEdit.commit();
				
				credentials.clear();
				credentials.add(new BasicNameValuePair("email", emailView.getText().toString()));
				credentials.add(new BasicNameValuePair("password", passwordView.getText().toString()));
				
				if (registerBox.isChecked()) {
					intent = new Intent(myContext, RegisterActivity.class);
					intent.putExtra("email", settings.getString("email", ""));
					intent.putExtra("password", settings.getString("password", ""));
					startActivity(intent);
				}
				else {
					loginThread = getLoginThread();
					loginThread.start();
					Toast.makeText(myContext, "Logging In ...", Toast.LENGTH_SHORT).show();
				}
			}
		};
	}
}