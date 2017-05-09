package com.thundernoteapp.android;

import java.io.IOException;

import com.thundernoteapp.android.R;
import com.thundernoteapp.android.supportclasses.VerificaConnessione;
import com.thundernoteapp.android.supportclasses.VerificaRegistra;

import org.apache.http.ParseException;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class TwitterLoginActivity extends Activity {

	final static String APIKEY = "ML48VSZPF8nOnDzYW5tAg";
	final static String APISECRET = "A03cIY7L5yqT3u9ITQydtBXiUDMIF9EW6sspwHmzzVs";
	final static String CALLBACK = "oauth:///";
	Intent SceltaServizioIntent;
	Token requestToken;
	String authURL;
	OAuthService s;
	String name = "";
	String res;
	WebView webview;

	private IntentFilter mNetworkStateChangedFilter;
	private BroadcastReceiver mNetworkStateIntentReceiver;

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

		setContentView(R.layout.activity_authorization_login);

		s = new ServiceBuilder().provider(TwitterApi.class)
				.apiKey(APIKEY).apiSecret(APISECRET).callback(CALLBACK).build();

		webview = (WebView) findViewById(R.id.webview);
		SceltaServizioIntent = new Intent(this, SceltaServizioActivity.class);
		new requestToken().execute();

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

	public class requestToken extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... URL) {
			requestToken = s.getRequestToken();
			authURL = s.getAuthorizationUrl(requestToken);
			return name;	
		}
		protected void onPostExecute(String res){
			// attach WebViewClient to intercept the callback url
			webview.setWebViewClient(new WebViewClient() {
				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {

					// check for our custom callback protocol otherwise use default
					// behavior
					if (url.startsWith("oauth")) {
						// authorization complete hide webview for now.
						webview.setVisibility(View.GONE);
						String [] URL = new String [1];
						URL[0] = url;
						new loginTwitter().execute(URL);
						return true;
					}

					return super.shouldOverrideUrlLoading(view, url);
				}
			});

			// send user to authorization page
			webview.loadUrl(authURL);
		}

	}

	public class loginTwitter extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... URL) {
			// TODO Auto-generated method stub
			Uri uri = Uri.parse(URL[0]);
			String verifier = uri.getQueryParameter("oauth_verifier");
			Verifier v = new Verifier(verifier);

			// save this token for practical use.
			Token accessToken = s.getAccessToken(requestToken, v);

			// host twitter detected from callback oauth://twitter
			// if (uri.getHost().equals("twitter")) {
			// requesting xml because its easier for human to read
			// as it comes back
			OAuthRequest req = new OAuthRequest(Verb.GET,
					"http://api.twitter.com/1/account/verify_credentials.json");
			s.signRequest(accessToken, req);
			Response response = req.send();
			String str = response.getBody();
			JSONObject respObj = null;
			try {
				respObj = new JSONObject(str);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			int id = 0;
			try {
				id = (Integer) respObj.get("id");
				name = (String) respObj.get("screen_name");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String ID = String.valueOf(id);

			String provider = "twitter";
			try {
				res = VerificaRegistra.VerificaoRegistraUtente(
						ID, name, provider);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return res;
		}

		protected void onPostExecute(String res){    	

			if (res.equals(name)) {
				startActivity(SceltaServizioIntent);
			} else {
				Toast errore = Toast.makeText(
						TwitterLoginActivity.this,
						"Errore verifica o registra utente!",
						Toast.LENGTH_LONG);
				errore.show();
			}
		}		
	}
}
