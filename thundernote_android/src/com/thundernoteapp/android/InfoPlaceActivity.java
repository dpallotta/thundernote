package com.thundernoteapp.android;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.thundernoteapp.android.supportclasses.VerificaConnessione;

import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class InfoPlaceActivity extends Activity implements OnClickListener {

	private String strname;
	private String straddress;
	private String address;
	private String id;
	private String activity;

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
						noInternet.putExtra("sender", "postHome");
						startActivity(noInternet);
						finish();
					}
				}
			}
		};

		Intent intent = getIntent();
		activity = intent.getStringExtra("activity");
		if (activity.equals("foursquareActivity")) {
			setContentView(R.layout.click_on_item);
			final ImageButton savePlace = (ImageButton) findViewById(R.id.saveplace);
			savePlace.setOnClickListener(this);
		} else if (activity.equals("favoritePlacesActivity")) {
			setContentView(R.layout.activity_delete_places);
			final ImageButton deletePlace = (ImageButton) findViewById(R.id.deleteplace);
			deletePlace.setOnClickListener(this);
		}

		strname = intent.getStringExtra("name");
		straddress = intent.getStringExtra("address");
		id = intent.getStringExtra("id");
		String strtype = intent.getStringExtra("type");
		String strphone = intent.getStringExtra("telephone");
		String strcity = intent.getStringExtra("city");
		String strstate = intent.getStringExtra("state");
		String strcategoria = intent.getStringExtra("categoria");
		int idistance = intent.getIntExtra("distance", 0);
		int icheckins = intent.getIntExtra("checkins", 0);

		TextView txtname = (TextView) findViewById(R.id.name);
		TextView txtaddress = (TextView) findViewById(R.id.address);
		TextView txttype = (TextView) findViewById(R.id.type);
		TextView txtphone = (TextView) findViewById(R.id.telephone);
		TextView txtdistance = (TextView) findViewById(R.id.distance);
		TextView txtcategoria = (TextView) findViewById(R.id.categoria);
		TextView txtcheckins = (TextView) findViewById(R.id.checkins);

		txtname.setText(strname);
		txtaddress.setText(straddress);
		txttype.setText(strtype);
		txtphone.setText(strphone);
		txtdistance.setText(String.valueOf(idistance) + "m");
		txtcategoria.setText(strcategoria);
		txtcheckins.setText(String.valueOf(icheckins));

		address = ((straddress.concat(", ")).concat(strcity + ", "))
				.concat(strstate);

	}

	@Override
	public void onResume() {
		super.onResume();
		registerReceiver(mNetworkStateIntentReceiver, mNetworkStateChangedFilter);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		this.unregisterReceiver(mNetworkStateIntentReceiver);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (activity.equals("foursquareActivity")) {
				finish();
			}
			else{
				//Intent SceltaServizioActivity = new Intent(getApplicationContext(), SceltaServizioActivity.class);
				//startActivity(SceltaServizioActivity);
				Intent FavoritePlacesActivity = new Intent(getApplicationContext(), FavoritePlacesActivity.class);
				startActivity(FavoritePlacesActivity);
				finish();
			}

			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		String [] servlet = new String [1];

		switch (v.getId()) {

		case R.id.saveplace:


			servlet[0] = "placeServlet";
			new connectionServletPlaces().execute(servlet);

			break;

		case R.id.deleteplace:

			servlet[0] = "deletePlaceServlet";
			new connectionServletPlaces().execute(servlet);

			break;

		}

	}

	public class connectionServletPlaces extends AsyncTask<String, Integer, String> {

		String servlet;
		
		@Override
		protected void onPreExecute () {
			new loggedUser().execute();
		}


		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub

			String resp = "";
			servlet = params[0];
			List<NameValuePair> param = new LinkedList<NameValuePair>();
			if (servlet.equals("placeServlet")) {
				param.add(new BasicNameValuePair("placeName", strname));
				param.add(new BasicNameValuePair("placeAddress", address));
				param.add(new BasicNameValuePair("placeVenueId", id));
			} else { //If servlet.equals("deletePlaceServlet")
				param.add(new BasicNameValuePair("placeVenueId", id));
			}
			UrlEncodedFormEntity entity = null;
			try {
				entity = new UrlEncodedFormEntity(param);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			HttpClient hc = new DefaultHttpClient();

			HttpPost post = new HttpPost("http://thunder-note.appspot.com/"
					+ servlet);
			post.setEntity(entity);

			HttpResponse rp = null;
			try {
				rp = hc.execute(post, LoginActivity.httpContext);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (rp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				try {
					resp = EntityUtils.toString(rp.getEntity());
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				resp = resp.replaceAll("\r\n|\r|\n", " ");

			} else {
				publishProgress(1);
			}

			return resp;
		}

		protected void onPostExecute(String resp){    

			if (servlet.equals("placeServlet")){
				if (resp.equals("Place Saved! ")
						|| resp.equals("Place already saved... ")) {

					Toast toast = Toast.makeText(getApplicationContext(), "Luogo salvato!",
							Toast.LENGTH_LONG);
					toast.show();

				} else {

					Toast toast = Toast.makeText(getApplicationContext(),
							"Impossibile salvare luogo!", Toast.LENGTH_LONG);
					toast.show();

				}
			}
			else {

				if (resp.equals("Place Deleted! ")
						|| resp.equals("Place already deleted... ")) {

					Toast toast = Toast.makeText(getApplicationContext(), "Luogo eliminato!",
							Toast.LENGTH_LONG);
					toast.show();

				} else {

					Toast toast = Toast.makeText(getApplicationContext(),
							"Impossibile eliminare luogo!", Toast.LENGTH_LONG);
					toast.show();

				}
			}
		}

		protected void onProgressUpdate(Integer... progress) {
			//this runs in UI thread so its safe to modify the UI
			if(progress[0] == 1){
				Toast toast = Toast.makeText(getApplicationContext(),
						"Errore durante connessione al server!", Toast.LENGTH_LONG);
				toast.show();
				Intent SceltaServizioActivity = new Intent(getApplicationContext(), SceltaServizioActivity.class);
				startActivity(SceltaServizioActivity);
				finish();
			}
		}
	}
	
	public class loggedUser extends AsyncTask<String, Integer, String>  {

		@Override
		protected String doInBackground(String... params) {
			String str = null;
			try {
				HttpClient hc = new DefaultHttpClient();

				HttpGet get = new HttpGet(
						"http://thunder-note.appspot.com/loginmobile?method=getLoggedInUser");

				HttpResponse rp = hc.execute(get, LoginActivity.httpContext);

				if (rp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					str = EntityUtils.toString(rp.getEntity());

				} 
			} catch (IOException e) {
				e.printStackTrace();
			}

			return str;
			
		}
		
		protected void onPostExecute(String str){
			
			String notLog = "Utente non loggato ";
			str = str.replaceAll("\r\n|\r|\n", " ");
			if (notLog.equals(str)) {
				Intent loginActivity = new Intent(getApplicationContext(), LoginActivity.class);
				Toast toast = Toast.makeText(getApplicationContext(), "Sessione scaduta!",
						Toast.LENGTH_LONG);
				toast.show();
				startActivity(loginActivity);	
				finish();
			}	
		}
	}
}
