package com.csmckelvey.statitician;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends Activity {
	
	private String emailText;
	private String passwordText;
	private String firstnameText;
	private String lastnameText;
	private String nicknameText;
	private String bdaydayText;
	private String bdaymonthText;
	private String bdayyearText;
	private String teamText;
	private String positionText;
	private String numberText;
	private String pictureText;
	
	private EditText first_name;
	private EditText last_name;
	private EditText nick_name;
	private EditText bday_day;
	private EditText bday_month;
	private EditText bday_year;
	private EditText team;
	private EditText position;
	private EditText number;
	private EditText email;
	private EditText password;
	private EditText picture;
	private Button registerButton;

	private Intent intent;
	private Handler handler;
	private Context myContext;
	private boolean valid = true;
	private Thread registerThread;
	private BufferedReader results;
	private String validationResult;
	private StatWebService webService;
	private ArrayList<BasicNameValuePair> credentials = new ArrayList<BasicNameValuePair>();
	
	private final String bdayCriteria2 = "^[0-9]{2}";
	private final String bdayCriteria4 = "^[0-9]{4}";
	private final String nameCriteria = "^[a-zA-Z]{2,}";
	private final String numberCriteria = "^[0-9]{1,2}";
	private final String positionCriteria = "^[a-zA-Z]{2,}(\\s)?[a-zA-Z]*";
	private final String emailCriteria = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.account_form);
		
		myContext = this;
		handler = new Handler();
		webService = new StatWebService(myContext);
		
		emailText = getIntent().getExtras().getString("email");
		passwordText = getIntent().getExtras().getString("password");

		grabViews();
		email.setText(emailText);
		password.setText(passwordText);
		
		registerButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				teamText = team.getText().toString();
				emailText = email.getText().toString();
				numberText = number.getText().toString();
				pictureText = picture.getText().toString();
				bdaydayText = bday_day.getText().toString();
				positionText = position.getText().toString();
				passwordText = password.getText().toString();
				bdayyearText = bday_year.getText().toString();
				lastnameText = last_name.getText().toString();
				nicknameText = nick_name.getText().toString();
				firstnameText = first_name.getText().toString();
				bdaymonthText = bday_month.getText().toString();

				valid = true;
				validationResult = "";
				if (!firstnameText.matches(nameCriteria)) { validationResult += "First Name Invalid\n"; valid = false; }
				if (!lastnameText.matches(nameCriteria)) { validationResult += "Last Name Invalid\n"; valid = false; }
				if (!bdaydayText.matches(bdayCriteria2) && Integer.valueOf(bdaydayText) < 32 && Integer.valueOf(bdaydayText) > 0) { validationResult += "Birthday Date Invalid\n"; valid = false; }
				if (!bdaymonthText.matches(bdayCriteria2) && Integer.valueOf(bdaymonthText) < 13 && Integer.valueOf(bdaymonthText) > 0) { validationResult += "Birthday Month Invalid\n"; valid = false; }
				if (!bdayyearText.matches(bdayCriteria4) && Integer.valueOf(bdayyearText) < 2015 && Integer.valueOf(bdayyearText) > 1900) { validationResult += "Birthday Year Invalid\n"; valid = false; }
				if (!teamText.matches(nameCriteria)) { validationResult += "Team Name Invalid\n"; valid = false; }
				if (!emailText.matches(emailCriteria)) { validationResult += "Email Invalid\n"; valid = false; }
				if (passwordText.length() < 6) { validationResult += "Password Too Short\n"; valid = false; }
				
				if (!nicknameText.equals("") && !nicknameText.matches(nameCriteria)) { validationResult += "Nick Name Invalid\n"; valid = false; }
				if (!positionText.equals("") && !positionText.matches(positionCriteria)) { validationResult += "Position Invalid\n"; valid = false; }
				if (!numberText.equals("") && !numberText.matches(numberCriteria)) { validationResult += "Number Invalid\n"; valid = false; }
				
				if (!valid) { Toast.makeText(myContext, validationResult, Toast.LENGTH_LONG).show(); }
				else {
					credentials.clear();
					credentials.add(new BasicNameValuePair("firstName", firstnameText));
					credentials.add(new BasicNameValuePair("lastName", lastnameText));
					credentials.add(new BasicNameValuePair("nickName", nicknameText));
					credentials.add(new BasicNameValuePair("bdayDay", bdaydayText));
					credentials.add(new BasicNameValuePair("bdayMonth", bdaymonthText));
					credentials.add(new BasicNameValuePair("bdayYear", bdayyearText));
					credentials.add(new BasicNameValuePair("team", teamText));
					credentials.add(new BasicNameValuePair("email", emailText));
					credentials.add(new BasicNameValuePair("password", passwordText));
					credentials.add(new BasicNameValuePair("position", positionText));
					credentials.add(new BasicNameValuePair("number", numberText));
					
					registerThread = new Thread(new Runnable() {
						public void run() {
							results = webService.deploy("http://www.csmckelvey.com/webServices/register.php", credentials);
							try {
								if (results.readLine().equals("PASS")) {
									handler.post(new Runnable() {
										public void run() { Toast.makeText(myContext, "SUCCESS!", Toast.LENGTH_LONG).show(); }
									});
									intent = new Intent(myContext, HomepageActivity.class);
									intent.putExtra("playerId", results.readLine());
									startActivity(intent);
								}
								else { 
									handler.post(new Runnable() {
										public void run() { Toast.makeText(myContext, "WARNING:\nAn account with that email already exists ...", Toast.LENGTH_LONG).show(); }
									});
								}
							} 
							catch (IOException e) {e.printStackTrace();}
						}
					});
					registerThread.start();
				}
			}
		});	
	}

	private void grabViews() {
		first_name = (EditText)findViewById(R.id.first_name);
		last_name = (EditText)findViewById(R.id.last_name);
		nick_name = (EditText)findViewById(R.id.nick_name);
		bday_day = (EditText)findViewById(R.id.bday_day);
		bday_month = (EditText)findViewById(R.id.bday_month);
		bday_year = (EditText)findViewById(R.id.bday_year);
		team = (EditText)findViewById(R.id.team);
		position = (EditText)findViewById(R.id.position);
		number = (EditText)findViewById(R.id.number);
		email = (EditText)findViewById(R.id.email);
		password = (EditText)findViewById(R.id.password);
		picture = (EditText)findViewById(R.id.picture);
		registerButton = (Button)findViewById(R.id.registerButton);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.register, menu);
		return true;
	}

}
