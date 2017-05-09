package com.thundernoteapp.android;

import java.io.IOException;
import org.apache.http.ParseException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;
import android.view.KeyEvent;
import android.view.View;

import com.thundernoteapp.android.supportclasses.VerificaConnessione;
import com.thundernoteapp.android.supportclasses.VerificaRegistra;

import com.thundernoteapp.android.login.utility.*;
import com.thundernoteapp.android.login.utility.Facebook.DialogListener;

public class LoginActivity extends Activity implements OnClickListener {

	Facebook facebook = new Facebook("217048318417667");
	AsyncFacebookRunner mAsyncRunner = new AsyncFacebookRunner(facebook);
	String id;
	String name;

	CookieStore cookieStore;
	public static HttpContext httpContext;

	private IntentFilter mNetworkStateChangedFilter;
	private BroadcastReceiver mNetworkStateIntentReceiver;

	Intent TwitterLoginActivityIntent;
	Intent GoogleLoginActivityIntent;
	Intent SceltaServizioIntent;
	String resp;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		mNetworkStateChangedFilter = new IntentFilter();
		mNetworkStateChangedFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

		mNetworkStateIntentReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
					boolean noConnectivity = VerificaConnessione.isNetworkAvailable(context);
					if (noConnectivity == false){
						Intent noInternet = new Intent (context, NoInternetActivity.class);
						noInternet.putExtra("sender", "preHome");
						startActivity(noInternet);
						finish();

					}
				}
			}
		};

		setContentView(R.layout.activity_login);

		cookieStore = new BasicCookieStore();
		httpContext = new BasicHttpContext();
		httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

		final ImageView buttonFacebook = (ImageView) findViewById(R.id.facebook_login);
		final ImageView buttonGoogle = (ImageView) findViewById(R.id.google_login);
		final ImageView buttonTwitter = (ImageView) findViewById(R.id.twitter_login);
		buttonFacebook.setOnClickListener(this);
		buttonGoogle.setOnClickListener(this);
		buttonTwitter.setOnClickListener(this);
		SceltaServizioIntent = new Intent(this, SceltaServizioActivity.class);

	}

	@Override
	public void onResume() {

		super.onResume();
		registerReceiver(mNetworkStateIntentReceiver, mNetworkStateChangedFilter);

	}


	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(mNetworkStateIntentReceiver);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {	

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent (Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {

		case R.id.facebook_login:
			facebook.authorize(this, new DialogListener() {
				public void onComplete(Bundle values) {

					new facebookLogin().execute();

				}

				public void onFacebookError(FacebookError error) {
				}

				public void onError(DialogError e) {
				}

				public void onCancel() {
				}
			});

			break;

		case R.id.google_login:
			GoogleLoginActivityIntent = new Intent(this,
					GoogleLoginActivity.class);
			startActivity(GoogleLoginActivityIntent);
			break;

		case R.id.twitter_login:
			TwitterLoginActivityIntent = new Intent(this,
					TwitterLoginActivity.class);
			startActivity(TwitterLoginActivityIntent);
			break;

		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		facebook.authorizeCallback(requestCode, resultCode, data);
	}


	public class facebookLogin extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub

			try {
				JSONObject respObj = new JSONObject(facebook
						.request("me"));
				id = respObj.getString("id");
				name = respObj.getString("name");
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			String provider = "facebook";
			try {
				String res = VerificaRegistra.VerificaoRegistraUtente(
						id, name, provider);
				if (res.equals(name)) {

					startActivity(SceltaServizioIntent);
				} else {

					publishProgress(1);
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		protected void onProgressUpdate(Integer... progress) {
			//this runs in UI thread so its safe to modify the UI
			if(progress[0] == 1){
				Toast errore = Toast.makeText(LoginActivity.this,
						"Errore verifica o registra utente",
						Toast.LENGTH_LONG);
				errore.show();
			}
		}
	}
}