package com.thundernoteapp.android;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.thundernoteapp.android.supportclasses.FsqVenue;
import com.thundernoteapp.android.supportclasses.NearbyAdapter;
import com.thundernoteapp.android.supportclasses.VerificaConnessione;

import android.location.Location;
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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class FavoritePlacesActivity extends Activity {

	private ListView mListView;
	private NearbyAdapter mAdapter;
	ArrayList<FsqVenue> venuesList;
	Intent onClickActivity;
	boolean bool = false;

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

		setContentView(R.layout.activity_foursquare);
		mListView = (ListView) findViewById(R.id.lv_places);
		// Get singletone instance of ImageLoader
		ImageLoader imageLoader = ImageLoader.getInstance();
		// Initialize ImageLoader with configuration. Do it once.
		imageLoader.init(ImageLoaderConfiguration.createDefault(this.getApplicationContext()));
		mAdapter = new NearbyAdapter(this , imageLoader);
		onClickActivity = new Intent(this, InfoPlaceActivity.class);
		new favoritePlaces().execute();	
	}


	@Override
	public void onPause() {
		super.onPause();

		this.unregisterReceiver(mNetworkStateIntentReceiver);
	}

	@Override
	public void onResume() {

		super.onResume();

		registerReceiver(mNetworkStateIntentReceiver, mNetworkStateChangedFilter);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent SceltaServizioActivity = new Intent(this, SceltaServizioActivity.class);
			startActivity(SceltaServizioActivity);
			finish();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	public class favoritePlaces extends AsyncTask<String, Integer, String> {
		
		@Override
		protected void onPreExecute () {
			new loggedUser().execute();
		}


		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			if (bool == false){
	
			try {
				venuesList = favoritePlace();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			}

			return null;
		}

		protected void onPostExecute(String page) {    	

		if (bool == false){
			mAdapter.setData(venuesList);
			if (venuesList.size() == 0) {
				Toast toast = Toast.makeText(getApplicationContext(),
						"Nessun luogo salvato nei preferiti!!",
						Toast.LENGTH_LONG);
				toast.show();
				Intent SceltaServizioActivity = new Intent(getApplicationContext(), SceltaServizioActivity.class);
				startActivity(SceltaServizioActivity);
				finish();
			}

			mListView.setAdapter(mAdapter);
			mListView.setOnItemClickListener(new OnItemClickListener() {

				public void onItemClick(AdapterView<?> arg0, View view,
						int position, long id) {
					// TODO Auto-generated method stub
					Object o = mListView.getItemAtPosition(position);
					FsqVenue fsqv = (FsqVenue) o;

					onClickActivity.putExtra("name", fsqv.name);
					onClickActivity.putExtra("address", fsqv.address);
					onClickActivity.putExtra("type", fsqv.type);
					onClickActivity.putExtra("distance", fsqv.distance);
					onClickActivity.putExtra("categoria", fsqv.categoria);
					onClickActivity.putExtra("url", fsqv.url);
					onClickActivity.putExtra("telephone", fsqv.telephone);
					onClickActivity.putExtra("checkins", fsqv.checkins);
					onClickActivity.putExtra("id", fsqv.id);
					onClickActivity.putExtra("city", fsqv.city);
					onClickActivity.putExtra("state", fsqv.state);
					onClickActivity.putExtra("activity", "favoritePlacesActivity");

					startActivity(onClickActivity);

					finish();

				}
			});
		}
		
		}

		protected void onProgressUpdate(Integer... progress) {
			//this runs in UI thread so its safe to modify the UI
			if (progress[0] == 1) {
				Toast errore = Toast.makeText(FavoritePlacesActivity.this,
						"Errore connessione al server!", Toast.LENGTH_LONG);
				errore.show();
				Intent SceltaServizioActivity = new Intent(getApplicationContext(), SceltaServizioActivity.class);
				startActivity(SceltaServizioActivity);
				finish();
			}
			if(progress[0] == 2){
				Toast errore = Toast.makeText(getApplicationContext(),
						"Errore durante l'invio della richiesta a Foursquare!",
						Toast.LENGTH_LONG);
				errore.show();
				Intent SceltaServizioActivity = new Intent(getApplicationContext(), SceltaServizioActivity.class);
				startActivity(SceltaServizioActivity);
				finish();
			}
		}

		private ArrayList<FsqVenue> favoritePlace() throws JSONException,
		ClientProtocolException, IOException {

			ArrayList<FsqVenue> venueList = new ArrayList<FsqVenue>();
			ArrayList<String> vId = new ArrayList<String>();
			HttpClient hc = new DefaultHttpClient();
			String venueFromId = "";

			String response = getPlaces();

			JSONObject jsonObject = (JSONObject) new JSONTokener(response)
			.nextValue();
			JSONArray venuesId = (JSONArray) jsonObject.getJSONArray("venuesId");
			int length = venuesId.length();

			if (length > 0) {
				for (int i = 0; i < length; i++) {
					String singlevenueId = (String) venuesId.get(i);
					vId.add(singlevenueId);
				}
			}

			int legthVenueId = vId.size();

			if (legthVenueId > 0) {
				for (int j = 0; j < legthVenueId; j++) {
					String venueID = vId.get(j);

					HttpGet get = new HttpGet(
							"https://api.foursquare.com/v2/venues/"
									+ venueID
									+ "?&client_id=YOUR_ID&client_secret=YOUR_SECRET&v=20110908");

					HttpResponse rp = hc.execute(get);

					if (rp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
						venueFromId = EntityUtils.toString(rp.getEntity());
					}
					else{
						publishProgress(2);
					}


					JSONObject jsonObject2 = (JSONObject) new JSONTokener(
							venueFromId).nextValue();
					JSONObject venue = (JSONObject) (jsonObject2
							.getJSONObject("response")).getJSONObject("venue");
					FsqVenue newvenue = new FsqVenue();

					newvenue.id = venue.getString("id");
					newvenue.name = venue.getString("name");

					JSONObject contact = (JSONObject) venue
							.getJSONObject("contact");
					JSONObject stats = (JSONObject) venue.getJSONObject("stats");
					JSONObject location = (JSONObject) venue
							.getJSONObject("location");
					JSONArray categories = (JSONArray) venue
							.getJSONArray("categories");

					if (categories.length() > 0) {
						JSONObject category = (JSONObject) categories
								.getJSONObject(0);

						if (category.has("name")){

							newvenue.categoria = category.getString("name");

						}
						else{
							newvenue.categoria = "Non definita";
						}
						if (category.has("icon")){

							newvenue.url = category.getString("icon");
						}

						JSONArray parents = (JSONArray) category
								.getJSONArray("parents");
						if (parents.length() > 0) {
							newvenue.type = parents.getString(0);
						} else {
							newvenue.type = "Non definito";
						}

					} else {
						newvenue.type = "Non definito";
					}

					if (!location.has("address")) {
						newvenue.address = "Non definita";
					} else {
						newvenue.address = location.getString("address");
					}

					if (!location.has("city")) {
						newvenue.city = "Non definita";
					} else {
						newvenue.city = location.getString("city");
					}

					if (!location.has("state")) {
						newvenue.state = "Non definita";
					} else {
						newvenue.state = location.getString("state");
					}

					if (!location.has("distance")) {
						newvenue.distance = 0;
					} else {
						newvenue.distance = location.getInt("distance");
					}

					if (!contact.has("phone")) {
						newvenue.telephone = "Non definito";
					} else {
						newvenue.telephone = contact.getString("phone");
					}
					if (!stats.has("checkinsCount")) {
						newvenue.checkins = 0;
					} else {
						newvenue.checkins = stats.getInt("checkinsCount");
					}

					Location loc = null;

					newvenue.location = loc;


					venueList.add(newvenue);

				}
			}

			return venueList;
		}

		private String getPlaces() {

			String strResp = "";

			try {
				HttpClient hc = new DefaultHttpClient();
				HttpGet get = new HttpGet(
						"http://thunder-note.appspot.com/getPlacesServlet");

				HttpResponse rp = hc.execute(get, LoginActivity.httpContext);

				if (rp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					strResp = EntityUtils.toString(rp.getEntity());
				} else {
					publishProgress(1);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			return strResp;

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
				bool = true;
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
