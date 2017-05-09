package com.thundernoteapp.android;

import com.thundernoteapp.android.supportclasses.VerificaConnessione;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

public class NoInternetActivity extends Activity {

	private String sender;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_no_internet);
		Intent intent = getIntent();
		sender = intent.getStringExtra("sender");

		ImageButton refresh = (ImageButton) findViewById(R.id.refresh);
		refresh.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				if (!VerificaConnessione.isNetworkAvailable(getApplicationContext())){
					Toast noconnection = Toast.makeText(getApplicationContext(),
							"No connection!!",
							Toast.LENGTH_LONG);
					noconnection.show();
				}
				else {
					if (sender.equals("postHome")){
						Intent SceltaServizioActivity = new Intent(getApplicationContext(), SceltaServizioActivity.class);
						startActivity(SceltaServizioActivity);
						finish();
					}
					else{
						Intent LoginActivity = new Intent(getApplicationContext(), LoginActivity.class);
						startActivity(LoginActivity);
						finish();
					}
				}

			}
		});
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			return false;
		}

		return super.onKeyDown(keyCode, event);
	}
}
