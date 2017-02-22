package com.csmckelvey.statitician;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class DashboardActivity extends Activity implements GameClockListener, View.OnClickListener {
	
	private String gameId;
	private boolean isGoal;
	private String playerId;
	private int playerScore;
	private int opponentScore;
	
	private Intent intent;
	private Handler handler;
	private Context myContext;

	private Thread picThread;
	private GameClock myClock;
	private Thread clockThread;
	private Thread finishThread;
	private Thread deletionThread;
	private BufferedReader results;
	private StatWebService webService;
	private AlertDialog.Builder alertDialogBuilder;
	
	private ListView listView;
	private Button submitGame;
	private Button clearButton;
	private TextView minuteView;
	private TextView secondView;
	private ImageView playerIcon;
	private TextView playerScoreView;
	private TextView opponentScoreView;
	
	private URL playerPictureUrl;
	private InputStream playerPicResult;
	private Drawable playerPictureDrawable;
	
	private ImageButton start;
	private ImageButton pause; 
	private ImageButton pass_bad;
	private ImageButton shot_bad;
	private ImageButton foul_bad;
	private ImageButton teamGoal;
	private ImageButton steal_bad;
	private ImageButton pass_good;
	private ImageButton foul_good;
	private ImageButton shot_good;
	private ImageButton steal_good;
	private ImageButton playerGoal;
	private ImageButton myScoreAdd;
	private ImageButton playerAssist;
	private ImageButton opponentGoal;
	private ImageButton feedbackButton;
	private ImageButton myScoreSubtract;
	private ImageButton opponentScoreAdd;
	private ImageButton opponentScoreSubtract;
	
	private DialogFactory dialogFactory;
	private FeedbackListAdapter adapter;
	private ArrayList<StatObject> queries;
	private ArrayList<String> shotTypeList;
	private ArrayList<String> foulTypeList;
	private ArrayAdapter<String> shotTypeAdapter;
	private ArrayAdapter<String> foulTypeAdapter;
	private ArrayList<BasicNameValuePair> playerToGet;
	private ArrayList<BasicNameValuePair> statsToUpload;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dashboard);
		init();
		grabViews();
		setupFeedback();
		setViewListeners();
	}

	@Override
	public void onClick(View v) {
		if ((clockThread != null) && (myClock.shouldStop == false)) {
			switch(v.getId()) {
				case R.id.pass_good:
					queries.add(new Pass("Pass\tCompleted", gameId, playerId, true, R.drawable.check40)); break;
				case R.id.pass_bad:
					queries.add(new Pass("Pass\tIntercepted", gameId, playerId, false, R.drawable.redx40)); break;
				case R.id.shot_good:
					showShotDialog(true, false); break;
				case R.id.shot_bad:
					showShotDialog(false, false); break;
				case R.id.steal_good:
					queries.add(new Steal("Possession\tGained", gameId, playerId, true, R.drawable.check40)); break;
				case R.id.steal_bad:
					queries.add(new Steal("Possession\tLost", gameId, playerId, false, R.drawable.check40)); break;
				case R.id.foul_good:
					showFoulDialog(true); break;
				case R.id.foul_bad:
					showFoulDialog(false); break;
				case R.id.myScoreAdd:
					playerScoreView.setText(Integer.toString(++playerScore)); break;
				case R.id.myScoreSubtract:
					if (playerScore > 0) { playerScoreView.setText(Integer.toString(--playerScore)); } break;
				case R.id.opponentScoreAdd:
					opponentScoreView.setText(Integer.toString(++opponentScore)); break;
				case R.id.opponentScoreSubtract:
					if (opponentScore > 0) { opponentScoreView.setText(Integer.toString(--opponentScore)); } break;
				case R.id.player_goal:
					showShotDialog(false, true); break;
				case R.id.player_assist:
					playerScoreView.setText(Integer.toString(++playerScore));
					queries.add(new Assist("Assist\tNice Job", gameId, playerId, R.drawable.check40)); break;
				case R.id.team_goal:
					playerScoreView.setText(Integer.toString(++playerScore)); break;
				case R.id.opponent_goal:
					opponentScoreView.setText(Integer.toString(++opponentScore)); break;
			}
			adapter.notifyDataSetChanged();
		}
	}
	@Override
	public void onBackPressed() {
		dialogFactory.prepareDashboardBack(alertDialogBuilder);
		alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				myClock.shouldStop();
				intent = new Intent(myContext, HomepageActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra("playerId", playerId);
				dialog.cancel();
				deleteUnusedGame();
				startActivity(intent);
				finish();
			}
	    });
		alertDialogBuilder.show();
	}
	@Override
	public void handleClockEvent(final GameClockEvent e) {
		handler.post(new Runnable() {
			public void run() {
				minuteView.setText(e.mins);
				secondView.setText(e.secs);
			}
		});
	}
	
	private void init() {
		myContext = this;
		playerScore = 0;
		opponentScore = 0;
		handler = new Handler();
		myClock = new GameClock();
		queries = new ArrayList<StatObject>();
		dialogFactory = new DialogFactory();
		playerToGet = new ArrayList<BasicNameValuePair>();
		statsToUpload = new ArrayList<BasicNameValuePair>();
		gameId = getIntent().getExtras().getString("gameId");
		playerId = getIntent().getExtras().getString("playerId");
		shotTypeList = new ArrayList<String>(Arrays.asList("Regular","Header","Free Kick","Volley","Penalty"));
		foulTypeList = new ArrayList<String>(Arrays.asList("Misconduct","Slide Tackle", "Hand Ball","Bad Throw-In"));
		init2();
	}
	private void init2() {
		webService = new StatWebService(myContext);
		alertDialogBuilder = new AlertDialog.Builder(myContext);
		adapter = new FeedbackListAdapter(myContext, android.R.layout.simple_dropdown_item_1line, queries);
		shotTypeAdapter = new ArrayAdapter<String>(myContext, android.R.layout.simple_dropdown_item_1line);
		foulTypeAdapter = new ArrayAdapter<String>(myContext, android.R.layout.simple_dropdown_item_1line);
		shotTypeAdapter.addAll(shotTypeList);
		foulTypeAdapter.addAll(foulTypeList);
	}
	private void grabViews() {
		foul_bad = (ImageButton)findViewById(R.id.foul_bad);
		pass_bad = (ImageButton)findViewById(R.id.pass_bad);
		shot_bad = (ImageButton)findViewById(R.id.shot_bad);
		pause = (ImageButton)findViewById(R.id.pause_button);
		start = (ImageButton)findViewById(R.id.start_button);
		teamGoal = (ImageButton)findViewById(R.id.team_goal);
		pass_good = (ImageButton)findViewById(R.id.pass_good);
		shot_good = (ImageButton)findViewById(R.id.shot_good);
		steal_bad = (ImageButton)findViewById(R.id.steal_bad);
		foul_good = (ImageButton)findViewById(R.id.foul_good);
		myScoreAdd = (ImageButton)findViewById(R.id.myScoreAdd);
		steal_good = (ImageButton)findViewById(R.id.steal_good);
		minuteView = (TextView)findViewById(R.id.clock_minutes);
		secondView = (TextView)findViewById(R.id.clock_seconds);
		playerGoal = (ImageButton)findViewById(R.id.player_goal);
		submitGame = (Button)findViewById(R.id.finish_game_button);
		playerScoreView = (TextView)findViewById(R.id.player_score);
		opponentGoal = (ImageButton)findViewById(R.id.opponent_goal);
		playerAssist = (ImageButton)findViewById(R.id.player_assist);
		opponentScoreView = (TextView)findViewById(R.id.opponent_score);
		myScoreSubtract = (ImageButton)findViewById(R.id.myScoreSubtract);
		opponentScoreAdd = (ImageButton)findViewById(R.id.opponentScoreAdd);
		opponentScoreSubtract = (ImageButton)findViewById(R.id.opponentScoreSubtract);
	}
	private void fillPicture() {
		playerToGet.clear();
		playerToGet.add(new BasicNameValuePair("playerId", playerId));
		picThread = new Thread(new Runnable() {
			public void run() {
				try {
					results = webService.deploy("http://www.csmckelvey.com/webServices/getPicture.php", playerToGet);
					if (results.readLine().equals("PASS")) {
						playerPictureUrl = new URL(results.readLine()); 
						playerPicResult = (InputStream)playerPictureUrl.getContent();
						playerPictureDrawable = Drawable.createFromStream(playerPicResult, "src");
						handler.post(new Runnable() { public void run() { playerIcon.setImageDrawable(playerPictureDrawable); } });
					}
				} 
				catch (UnsupportedEncodingException e) {e.printStackTrace();} 
				catch (ClientProtocolException e) {e.printStackTrace();} 
				catch (IOException e) {e.printStackTrace();}
			}
		});
		picThread.start();
	}
	private void setupFeedback() {
		if (findViewById(R.id.feedbackList) != null) {
			playerIcon = (ImageView)findViewById(R.id.playerIcon);
			fillPicture();
			
			clearButton = (Button)findViewById(R.id.clear_button);
			clearButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialogFactory.prepareClearListDialog(alertDialogBuilder);
					alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								queries.clear();
								adapter.notifyDataSetChanged();
								dialog.cancel();
							}
					    });
					alertDialogBuilder.show();
				}
			});
			
			listView = (ListView)findViewById(R.id.feedbackList);
			listView.setAdapter(adapter);
			listView.setOnItemLongClickListener(new OnItemLongClickListener() {
				@Override
				public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					final int position = arg2;
					dialogFactory.prepareListViewDialog(alertDialogBuilder);
					alertDialogBuilder.setMessage("You Really Want To Delete : " + queries.get(position).getDisplayText() + "?");
					alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								isGoal = queries.get(position) instanceof com.csmckelvey.statitician.Goal;
								queries.remove(position);
								if (isGoal && (position != 0) && (queries.get((position-1)) instanceof com.csmckelvey.statitician.Shot)) { queries.remove((position-1)); }
								adapter.notifyDataSetChanged();
								dialog.cancel();
							}
					    });
					alertDialogBuilder.show();
					return false;
				}
			});
		}
		else if (findViewById(R.id.feedbackButton) != null) {
			listView = new ListView(myContext);
			listView.setAdapter(adapter);
			listView.setOnItemLongClickListener(new OnItemLongClickListener() {
				@Override
				public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					isGoal = queries.get(arg2) instanceof com.csmckelvey.statitician.Goal;
					queries.remove(arg2);
					if (isGoal && (arg2 != 0) && (queries.get((arg2-1)) instanceof com.csmckelvey.statitician.Shot)) { queries.remove((arg2-1)); }
					adapter.notifyDataSetChanged();
					return false;
				}
			});
			
			feedbackButton = (ImageButton)findViewById(R.id.feedbackButton);
			feedbackButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (listView.getParent() != null) {
						listView = new ListView(myContext);
						listView.setAdapter(adapter);
						listView.setOnItemLongClickListener(new OnItemLongClickListener() {
							@Override
							public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
								isGoal = queries.get(arg2) instanceof com.csmckelvey.statitician.Goal;
								queries.remove(arg2);
								if (isGoal && (arg2 != 0) && (queries.get((arg2-1)) instanceof com.csmckelvey.statitician.Shot)) { queries.remove((arg2-1)); }
								adapter.notifyDataSetChanged();
								return false;
							}
						});
					}
					dialogFactory.showFeedbackListDialog(alertDialogBuilder, listView);
				}
			});
		}
	}
	private void deleteUnusedGame() {
		playerToGet.clear();
		playerToGet.add(new BasicNameValuePair("gameId", gameId));
		deletionThread = new Thread(new Runnable() { public void run() { results = webService.deploy("http://www.csmckelvey.com/webServices/removeUnusedGame.php", playerToGet); }  });
		deletionThread.start();
	}
	private void verifyCompletion() {
		dialogFactory.prepareVerifyDialog(alertDialogBuilder);
		alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				myClock.shouldStop();
				statsToUpload.clear();
				statsToUpload.add(new BasicNameValuePair("gameId", gameId));
				statsToUpload.add(new BasicNameValuePair("playerId", playerId));
				statsToUpload.add(new BasicNameValuePair("myScore", Integer.toString(playerScore)));
				statsToUpload.add(new BasicNameValuePair("opponentScore", Integer.toString(opponentScore)));
				for (StatObject obj : queries) { statsToUpload.add(new BasicNameValuePair("queryList[]", obj.getQueryText())); }
				finishThread = new Thread(new Runnable() {
					public void run() {
						try {
							results = webService.deploy("http://www.csmckelvey.com/webServices/finishGame.php", statsToUpload);
							if (results.readLine().equals("PASS")) {
								handler.post(webService.successToast());
								results.close();
								intent = new Intent(myContext, HomepageActivity.class);
								intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								intent.putExtra("playerId", playerId);
								startActivity(intent);
								finish();
							}
							else { handler.post(webService.failedToast()); }
						} 
						catch (UnsupportedEncodingException e) {e.printStackTrace();} 
						catch (ClientProtocolException e) {e.printStackTrace();} 
						catch (IOException e) {e.printStackTrace();}
					}
				});
				finishThread.start();
				Toast.makeText(myContext, "Submitting Game ...", Toast.LENGTH_SHORT).show();
				dialog.cancel();
			}
		});
		alertDialogBuilder.show();	
	}
	private void setViewListeners() {
		teamGoal.setOnClickListener(this);
		pass_bad.setOnClickListener(this);
		shot_bad.setOnClickListener(this);
		foul_bad.setOnClickListener(this);
		myClock.addGameClockListener(this);
		pass_good.setOnClickListener(this);
		shot_good.setOnClickListener(this);
		steal_bad.setOnClickListener(this);
		foul_good.setOnClickListener(this);
		steal_good.setOnClickListener(this);
		myScoreAdd.setOnClickListener(this);
		playerGoal.setOnClickListener(this);
		opponentGoal.setOnClickListener(this);
		playerAssist.setOnClickListener(this);
		myScoreSubtract.setOnClickListener(this);
		opponentScoreAdd.setOnClickListener(this);
		opponentScoreSubtract.setOnClickListener(this);
		
		start.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if ((clockThread == null) || (myClock.shouldStop)) {
					clockThread = new Thread(myClock);
					clockThread.start();
				}
			}
		});
		pause.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (!myClock.shouldStop) { myClock.shouldStop(); }
				else {
					clockThread = new Thread(myClock);
					clockThread.start(); 
				}
			}
		});
		submitGame.setOnClickListener(new View.OnClickListener() { public void onClick(View v) { verifyCompletion(); } });
	}
	private void showFoulDialog(final boolean successful) {
		dialogFactory.prepareFoulDialog(alertDialogBuilder);
		alertDialogBuilder.setAdapter(foulTypeAdapter, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				String choice = foulTypeAdapter.getItem(which);
				if (successful) { queries.add(new Foul("Foul Drawn\t"+choice, gameId, playerId, choice, true, R.drawable.check40)); }
				else { queries.add(new Foul("Foul Committed\t"+choice, gameId, playerId, choice, false, R.drawable.redx40)); }
				adapter.notifyDataSetChanged();
				dialog.cancel();
			}
		});
		alertDialogBuilder.show();
	}
	private void showShotDialog(final boolean successful, final boolean isGoal) {
		dialogFactory.prepareShotDialog(alertDialogBuilder);
		alertDialogBuilder.setAdapter(shotTypeAdapter, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				String choice = shotTypeAdapter.getItem(which);
				if (isGoal) {
					queries.add(new Shot("Shot On Goal\t"+choice, gameId, playerId, choice, true, R.drawable.check40));
					queries.add(new Goal("GOOOOL!\t"+choice, gameId, playerId, choice, R.drawable.check40));
					playerScoreView.setText(Integer.toString(++playerScore));
				}
				else {
					if (successful) { queries.add(new Shot("Shot On Goal\t"+choice, gameId, playerId, choice, true, R.drawable.check40)); }
					else { queries.add(new Shot("Shot Off Target\t"+choice, gameId, playerId, choice, false, R.drawable.redx40)); }
				}
				adapter.notifyDataSetChanged();
				dialog.cancel();
			}
		});
		alertDialogBuilder.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.dashboard, menu);
		return true;
	}
}






























