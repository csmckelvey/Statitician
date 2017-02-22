package com.csmckelvey.statitician;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class SizeTest extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_size_test);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.size_test, menu);
		return true;
	}

}
