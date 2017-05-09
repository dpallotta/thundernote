package com.thundernoteapp.android;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
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
import com.thundernoteapp.android.supportclasses.Filtro;
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

public class FoursquareActivity extends Activity {

	private ListView mListView;
	private NearbyAdapter mAdapter;
	private ArrayList<FsqVenue> venuesList;
	Intent onClickActivity;
	double longitude;
	double latitude;
	String filter;
	String keywords;
	String radius;
	String section;
	boolean filtra;

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
						Intent SceltaServizioActivity = new Intent(context, SceltaServizioActivity.class);
						SceltaServizioActivity.putExtra("sender", "postHome");
						startActivity(SceltaServizioActivity);
						finish();
					}
				}
			}
		};

		setContentView(R.layout.activity_foursquare);
		mListView = (ListView) findViewById(R.id.lv_places);

		Intent intent = getIntent();
		longitude = intent.getDoubleExtra("longitude", 0.0);
		latitude = intent.getDoubleExtra("latitude", 0.0);
		filter = intent.getStringExtra("Filter");
		keywords = intent.getStringExtra("keywords");
		// Get singletone instance of ImageLoader
		ImageLoader imageLoader = ImageLoader.getInstance();
		// Initialize ImageLoader with configuration. Do it once.
		imageLoader.init(ImageLoaderConfiguration.createDefault(this.getApplicationContext()));
		mAdapter = new NearbyAdapter(this , imageLoader);
		onClickActivity = new Intent(this, InfoPlaceActivity.class);
		radius = "1500";
		section = filter;
		filtra = false;

		if (filter.equals("No filters")) {
			filter = "";
		}

		if (filter.length()>0 && keywords.length()>0){
			filtra = true;
		}

		new foursquareActivity().execute();

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
			Intent SceltaServizioActivity = new Intent(getApplicationContext(), SceltaServizioActivity.class);
			startActivity(SceltaServizioActivity);
			finish();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}




	public class foursquareActivity extends AsyncTask<String, Integer, String>  {

		@Override
		protected String doInBackground(String... params) {

			try {

				venuesList = parseHttp();
				if (venuesList.size() < 5){
					radius = "5000";
					venuesList = parseHttp();	
					if (venuesList.size() < 5){
						radius = "10000";
						venuesList = parseHttp();
					}
				}
				if (filtra == true){

					venuesList = Filtro.gestisciFiltro(venuesList , section);

				}


				mAdapter.setData(venuesList);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;

		}

		protected void onPostExecute(String page){    	

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
					onClickActivity.putExtra("telephone", fsqv.telephone);
					onClickActivity.putExtra("checkins", fsqv.checkins);
					onClickActivity.putExtra("url", fsqv.url);
					onClickActivity.putExtra("id", fsqv.id);
					onClickActivity.putExtra("city", fsqv.city);
					onClickActivity.putExtra("state", fsqv.state);
					onClickActivity.putExtra("activity", "foursquareActivity");

					startActivity(onClickActivity);

				}
			});	


		}

		protected void onProgressUpdate(Integer... progress) {
			//this runs in UI thread so its safe to modify the UI
			if (progress[0] == 1){

				Toast errore = Toast.makeText(getApplicationContext(),
						"Errore durante l'invio della richiesta a Foursquare!",
						Toast.LENGTH_LONG);
				errore.show();
				Intent SceltaServizioActivity = new Intent(getApplicationContext(), SceltaServizioActivity.class);
				startActivity(SceltaServizioActivity);
				finish();
			}

			if (progress[0] == 2){

				Toast toast = Toast.makeText(getApplicationContext(),
						"Nessun luogo di interesse!",
						Toast.LENGTH_LONG);
				toast.show();
				Intent SceltaServizioActivity = new Intent(getApplicationContext(), SceltaServizioActivity.class);
				startActivity(SceltaServizioActivity);
				finish();		
			}

			if(progress[0] == 3){
				Toast errore = Toast.makeText(getApplicationContext(),
						"Nessun luogo di interesse nelle vicinanze!",
						Toast.LENGTH_LONG);
				errore.show();
				Intent SceltaServizioActivity = new Intent(getApplicationContext(), SceltaServizioActivity.class);
				startActivity(SceltaServizioActivity);
				finish();
			}
		}

		private String foursquareCall() {

			HttpGet get;

			String str = "***";
			if (keywords == null){
				keywords = "";
			}

			try {
				HttpClient hc = new DefaultHttpClient();
				if (filter.length()>0 && keywords.length()>0){
					get = new HttpGet(
							"https://api.foursquare.com/v2/venues/explore?ll="
									+ latitude
									+ ","
									+ longitude
									+ "&client_id=FJ1H32ZWDSANM5SVAX0SPC3YFSTGCGNSCKRGNU52LSZ5B1KY&client_secret=BPAQULHN3KWQOBCBFERMZ421W5Q1RXJ1KS32SV3QOP4VYHSP"
									+ "&query="
									+ keywords
									+"&radius="
									+ radius 
									+"&limit=30&v=20110908");
				}
				else{
					get = new HttpGet(
							"https://api.foursquare.com/v2/venues/explore?ll="
									+ latitude
									+ ","
									+ longitude
									+ "&client_id=FJ1H32ZWDSANM5SVAX0SPC3YFSTGCGNSCKRGNU52LSZ5B1KY&client_secret=BPAQULHN3KWQOBCBFERMZ421W5Q1RXJ1KS32SV3QOP4VYHSP"
									+ "&section="
									+ filter
									+ "&query="
									+ keywords
									+"&radius="
									+ radius 
									+"&limit=30&v=20110908");

				}

				HttpResponse rp = hc.execute(get);

				if (rp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					str = EntityUtils.toString(rp.getEntity());
				}
				else{
					publishProgress(1);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			return str;

		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		private ArrayList<FsqVenue> parseHttp() throws JSONException {

			ArrayList<FsqVenue> venueList = new ArrayList<FsqVenue>();
			String response = foursquareCall();

			try {

				JSONObject jsonObj = (JSONObject) new JSONTokener(response).nextValue();

				JSONObject meta = (JSONObject) jsonObj.get("meta");
				int code = meta.getInt("code");
				if (code == 400){
					publishProgress(2);
				}

				else if (code == 200){

					JSONArray groups = (JSONArray) jsonObj.getJSONObject("response")
							.getJSONArray("groups");
					JSONObject obj = (JSONObject) groups.get(0);
					JSONArray items = (JSONArray) obj.getJSONArray("items");
					int length = items.length();
					if (length > 0){

						for (int i = 0; i < length; i++) {

							JSONObject obj1 = (JSONObject) items.get(i);
							JSONObject venue = (JSONObject) obj1.get("venue");
							FsqVenue Venue = new FsqVenue();
							Venue.id = venue.getString("id");
							Venue.name = venue.getString("name");

							JSONObject contact = (JSONObject) venue
									.getJSONObject("contact");
							JSONObject stats = (JSONObject) venue
									.getJSONObject("stats");
							JSONObject location = (JSONObject) venue
									.getJSONObject("location");
							JSONArray categories = (JSONArray) venue
									.getJSONArray("categories");

							if (categories.length() > 0) {
								JSONObject category = (JSONObject) categories
										.getJSONObject(0);
								if (category.has("name")){

									Venue.categoria = category.getString("name");
								}

								else{

									Venue.categoria = "Non definita";

								}

								if (category.has("icon")){

									Venue.url = category.getString("icon");
								}

								JSONArray parents = (JSONArray) category
										.getJSONArray("parents");
								if (parents.length() > 0) {
									Venue.type = parents.getString(0);
								} else {
									Venue.type = "Non definito";
								}

							} else {
								Venue.type = "Non definito";
							}

							if (!location.has("address")) {
								Venue.address = "Non definito";
							} else {
								Venue.address = location.getString("address");
							}

							if (!location.has("city")) {
								Venue.city = "Non definita";
							} else {
								Venue.city = location.getString("city");
							}

							if (!location.has("state")) {
								Venue.state = "Non definita";
							} else {
								Venue.state = location.getString("state");
							}

							if (!location.has("distance")) {
								Venue.distance = 0;
							} else {
								Venue.distance = location.getInt("distance");
							}
							if (!contact.has("phone")) {
								Venue.telephone = "Non definito";
							} else {
								Venue.telephone = contact.getString("phone");
							}
							if (!stats.has("checkinsCount")) {
								Venue.checkins = 0;
							} else {
								Venue.checkins = stats.getInt("checkinsCount");
							}

							Location loc = null;

							Venue.location = loc;



							venueList.add(Venue);
						}
					}

					else {

						publishProgress(3);

					}	
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	


			Collections.sort(venueList, new Comparator() {
				public int compare(Object o1, Object o2) {
					int result = 0;
					FsqVenue venue1 = (FsqVenue) o1;
					FsqVenue venue2 = (FsqVenue) o2;
					if (venue1.distance < venue2.distance) {
						result = -1;
					} else if (venue1.distance > venue2.distance) {
						result = 1;
					} else {
						result = 0;
					}

					return result;
				}
			});
			return venueList;

		}
	}
}
