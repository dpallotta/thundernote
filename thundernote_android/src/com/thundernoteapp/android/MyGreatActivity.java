package com.thundernoteapp.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import com.thundernoteapp.android.R;

public class MyGreatActivity extends Activity {

	private final int SPLASH_DISPLAY_LENGHT = 2000;// millisecondi di durata
	String user;
	String notLog;
	Intent secondaActivityIntent;
	Intent SceltaServizioIntent;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

	}

	@Override
	protected void onStart(){
		super.onStart();

		secondaActivityIntent = new Intent(this, LoginActivity.class);

		new Handler().postDelayed(new Runnable() {

			public void run() {


				startActivity(secondaActivityIntent);


			}

		}, SPLASH_DISPLAY_LENGHT);
	}

}