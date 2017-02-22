package com.csmckelvey.statitician;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.app.Activity;
import android.widget.Button;
import android.content.Intent;
import android.content.Context;

public class HomepageActivity extends Activity {
	
	private Button logout;
	private Intent intent;
	private Button newGame;
	private String playerId;
	private Context context;
	private Button viewStats;
	private Button editAccount;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_homepage);
		
		init();
		getViews();
		
		newGame.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				intent =  new Intent(context, StartNewGameActivity.class);
				intent.putExtra("playerId", playerId);
				startActivity(intent);
			}
		});
		viewStats.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
			}
		});
		editAccount.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
		});
		logout.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		
	}

	@Override
	public void onBackPressed() {
		intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.test, menu);
		return true;
	}
	
	private void init() {
		context = this;
		playerId = getIntent().getExtras().getString("playerId");
	}
	private void getViews() {
		logout = (Button)findViewById(R.id.logout);
		newGame = (Button)findViewById(R.id.new_game);
		viewStats = (Button)findViewById(R.id.view_stats);
		editAccount = (Button)findViewById(R.id.edit_account);
	}
	
}
