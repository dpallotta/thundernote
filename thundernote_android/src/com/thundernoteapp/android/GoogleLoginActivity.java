package com.thundernoteapp.android;

import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.webkit.*;
import android.net.http.*;

import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.api.client.auth.oauth2.draft10.AccessTokenResponse;
import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAuthorizationRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAccessTokenRequest.GoogleAuthorizationCodeGrant;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import com.thundernoteapp.android.supportclasses.VerificaConnessione;
import com.thundernoteapp.android.supportclasses.VerificaRegistra;

import android.graphics.Bitmap;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class GoogleLoginActivity extends Activity {

	final static String CLIENT_ID = "YOUR_CLIENT_ID";
	private static final String CLIENT_SECRET = "YOUR_CLIENT_SECRET";
	private static final String SCOPE = "https://www.googleapis.com/auth/userinfo.email";
	private static final String REDIRECT_URI = "http://localhost";
	Intent SceltaServizioIntent;
	String res;
	AccessTokenResponse accessTokenResponse = null;
	String email = null;
	String TokenResponse = "";

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
		SceltaServizioIntent = new Intent(this, SceltaServizioActivity.class);
		final WebView webview = (WebView) findViewById(R.id.webview);
		webview.getSettings().setJavaScriptEnabled(true);
		final String authorizationUrl = new GoogleAuthorizationRequestUrl(
				CLIENT_ID, REDIRECT_URI, SCOPE).build();
		webview.setWebViewClient(new WebViewClient() {
			
			public void onReceivedSslError (WebView view, SslErrorHandler handler, SslError error) {

				 handler.proceed() ;

				 }

			@Override
			public void onPageStarted(WebView view, String url, Bitmap bitmap) {
				view.getSettings().setJavaScriptEnabled(true);

				if (url.startsWith(REDIRECT_URI)) {
					view.setVisibility(View.GONE);
					String [] param = new String [1];
					param[0] = url;
					new loginGoogle().execute(param);

				}
				System.out.println("onPageFinished : " + url);
			}	
		});

		webview.loadUrl(authorizationUrl);

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

	private String extractCodeFromUrl(String url) {
		return url.substring(REDIRECT_URI.length() + 7, url.length());
	}

	public class loginGoogle extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {

			String url = params[0];
			String code = extractCodeFromUrl(url);

			try {
				accessTokenResponse = new GoogleAuthorizationCodeGrant(
						new NetHttpTransport(), new JacksonFactory(),
						CLIENT_ID, CLIENT_SECRET, code, REDIRECT_URI)
				.execute();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			return null;    

		}
		protected void onPostExecute(String page) { 

			JSONObject ob = new JSONObject(accessTokenResponse);

			try {
				TokenResponse = (String) ob.get("access_token");
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			new googleApi().execute();

		}
	}

	public class googleApi extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			HttpClient hc = new DefaultHttpClient();
			HttpGet get = new HttpGet(
					"https://www.googleapis.com/oauth2/v1/userinfo?access_token="
							+ TokenResponse);

			HttpResponse rp = null;
			try {
				rp = hc.execute(get);
			} catch (ClientProtocolException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			String response = null;
			try {
				response = EntityUtils.toString(rp.getEntity());
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			JSONObject JSONObj = null;
			try {
				JSONObj = new JSONObject(response);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				email = (String) JSONObj.get("email");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String provider = "google";
			try {
				res = VerificaRegistra.VerificaoRegistraUtente(email, email , provider);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		protected void onPostExecute(String page) { 

			if (res.equals(email)){
				startActivity(SceltaServizioIntent);
			}
			else{
				Toast errore = Toast.makeText(GoogleLoginActivity.this,
						"Errore verifica o registra utente",
						Toast.LENGTH_LONG);
				errore.show();
			}
		}
	}
}
