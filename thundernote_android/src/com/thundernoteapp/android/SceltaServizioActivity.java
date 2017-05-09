package com.thundernoteapp.android;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.thundernoteapp.android.supportclasses.PlacesAutoCompleteAdapter;
import com.thundernoteapp.android.supportclasses.VerificaConnessione;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

public class SceltaServizioActivity extends Activity implements OnClickListener {

	Intent FoursquareActivityIntent;
	Intent returnToLoginActivity;
	Intent FavoritePlacesIntent;
	private LocationManager lManager;
	private LocationListener lListener;
	private LocationListener lListenerGPS;
	private Location location;
	private Location locationGPS;
	double longitude;
	double latitude;
	private ImageButton logout;
	String selected;
	private Spinner spinner;
	private String response;
	private EditText keywords;
	private String key;
	private AutoCompleteTextView autoCompView;

	private IntentFilter mNetworkStateChangedFilter;
	private BroadcastReceiver mNetworkStateIntentReceiver;
	
	 private LinearLayout ll;
	 private RelativeLayout home;
	 private float startX;
	 private Animation animUp;
	 private Animation animDown;

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

		setContentView(R.layout.activity_scelta);
		
		ll = (LinearLayout) findViewById(R.id.slider);
		home = (RelativeLayout) findViewById(R.id.home);
		animUp = AnimationUtils.loadAnimation(this, R.anim.anim_up);
	    animDown = AnimationUtils.loadAnimation(this, R.anim.anim_down);
        ll.setVisibility(View.GONE);

		ImageButton getNearby = (ImageButton) findViewById(R.id.getnearby);
		ImageButton favoritePlaces = (ImageButton) findViewById(R.id.favoriteplaces);
		keywords = (EditText) findViewById(R.id.keyword);
		logout = (ImageButton) findViewById(R.id.logout);
		logout.setOnClickListener(this);
		getNearby.setOnClickListener(this);
		favoritePlaces.setOnClickListener(this);

		spinner = (Spinner) findViewById(R.id.spinner1);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				this,
				android.R.layout.simple_spinner_item,
				new String[] { "No filters", "Food", "Drinks",
						"Coffee", "Shops",
						"Arts", "Outdoors"});
		spinner.setAdapter(adapter);
		autoCompView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView1);
		autoCompView.setAdapter(new PlacesAutoCompleteAdapter(this,R.layout.item_list));
		autoCompView.setOnClickListener(this);
		FoursquareActivityIntent = new Intent(this, FoursquareActivity.class);
		FavoritePlacesIntent = new Intent(this, FavoritePlacesActivity.class);

		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> adapter, View view,
					int pos, long id) {

				selected = (String) adapter.getItemAtPosition(pos);
				FoursquareActivityIntent.putExtra("Filter", selected);

			}

			public void onNothingSelected(AdapterView<?> arg0) {

				selected = "No filters";
				FoursquareActivityIntent.putExtra("Filter", selected);

			}
		});



	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(mNetworkStateIntentReceiver);
	}

	@Override
	protected void onResume(){
		super.onResume();
		registerReceiver(mNetworkStateIntentReceiver, mNetworkStateChangedFilter);

		this.getCurrentLocation();
		autoCompView.setOnItemClickListener(new OnItemClickListener() {


			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {

				String str = (String) adapterView.getItemAtPosition(position);


				String[] st = new String [1];
				st [0] = str;
				new MapsCall().execute(st);

			}
		});	
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
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			startX = event.getX();
			break;
		case MotionEvent.ACTION_UP: {
			float endX = event.getX();
			
			if (startX < endX) {
				ll.setVisibility(View.VISIBLE);
				home.startAnimation(animUp);
			}
			else {
				home.startAnimation(animDown);
				ll.setVisibility(View.GONE);
			}
	    }
		
		
	
	}
		return true;
	}

	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.getnearby:

			if (locationGPS != null) {
				longitude = locationGPS.getLongitude();
				latitude = locationGPS.getLatitude();
			} else if (location != null) {

				longitude = location.getLongitude();
				latitude = location.getLatitude();

			}
			else {

				longitude = 0.0;
				latitude = 0.0;
			}

			if (longitude != 0 && latitude != 0) {

				lManager.removeUpdates(lListener);
				lManager.removeUpdates(lListenerGPS);

			} 

			FoursquareActivityIntent.putExtra("longitude", longitude);
			FoursquareActivityIntent.putExtra("latitude", latitude);
			try {
				key = URLEncoder.encode(keywords.getText().toString(), "utf8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			FoursquareActivityIntent.putExtra("keywords", key);
			spinner.setSelection(0);
			keywords.setText("");
			startActivity(FoursquareActivityIntent);
			finish();
			break;

		case R.id.favoriteplaces:
			spinner.setSelection(0);
			keywords.setText("");
			autoCompView.setText("");
			startActivity(FavoritePlacesIntent);
			finish();

			break;

		case R.id.logout:

			new logOut().execute();
			Toast saluti = Toast.makeText(this,
					"Grazie di aver usato il nostro servizio. Torna presto!!",
					Toast.LENGTH_LONG);
			saluti.show();
			returnToLoginActivity = new Intent(this, LoginActivity.class);
			startActivity(returnToLoginActivity);
			finish();

			break;

		}

	}

	private void getCurrentLocation() {

		/*LocationManager provides access to the system location services.
		 *  These services allow applications to obtain periodic updates of the device's geographical location.
		 */
		lManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);  

		Criteria crta = new Criteria();
		crta.setAccuracy(Criteria.ACCURACY_FINE);
		crta.setAltitudeRequired(false);
		crta.setBearingRequired(false);
		crta.setCostAllowed(true);
		crta.setPowerRequirement(Criteria.POWER_LOW);

		//getLastKnownLocation(String provider) returns a Location indicating the data from the last known location fix obtained from the given provider.

		location = lManager
				.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		locationGPS = lManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);

		//LocationListener() is used for receiving notifications from the LocationManager when the location has changed.
		//

		lListenerGPS = new LocationListener() {

			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				// TODO Auto-generated method stub
			}

			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub
			}

			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub
			}

			public void onLocationChanged(Location loc) {
				// TODO Auto-generated method stub
				locationGPS = loc;
			}
		};

		lListener = new LocationListener() {

			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				// TODO Auto-generated method stub
			}

			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub
			}

			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub
			}

			public void onLocationChanged(Location loc) {
				// TODO Auto-generated method stub
				location = loc;
			}
		};

		//requestLocationUpdates (String provider, long minTime, float minDistance, LocationListener listener)
		/*
		 * 	provider	the name of the provider with which to register
		 *	minTime	minimum time interval between location updates, in milliseconds
		 *	minDistance	minimum distance between location updates, in meters
		 *	listener a LocationListener whose onLocationChanged(Location) method will be called for each location update

		 */

		lManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 300000, 0,
				lListener);
		lManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 300000, 0,
				lListenerGPS);
	}


	public class logOut extends AsyncTask<String, Void, String> {

		String str;
		HttpResponse rp;
		boolean bool = false;

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub

			HttpClient hc = new DefaultHttpClient();

			HttpGet get = new HttpGet(
					"http://thunder-note.appspot.com/loginmobile?method=logout");

			try {
				rp = hc.execute(get, LoginActivity.httpContext);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


			return str;
		}

		protected void onPostExecute(String str){

			if (rp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				try {
					str = EntityUtils.toString(rp.getEntity());
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else{
				Toast toast = Toast.makeText(getApplicationContext(), "Errore durante logout!",
						Toast.LENGTH_LONG);
				toast.show();
			}	
		}
	}

	public class MapsCall extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String resp = "";

			HttpURLConnection conn = null;
			StringBuilder jsonResults = new StringBuilder();

			StringBuilder sb = new StringBuilder(
					"http://maps.googleapis.com/maps/api/geocode/json?address=");
			try {
				sb.append(URLEncoder.encode(params[0], "utf8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sb.append("&sensor=false");

			URL url = null;
			try {
				url = new URL(sb.toString());
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				conn = (HttpURLConnection) url.openConnection();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			InputStreamReader in = null;
			try {
				in = new InputStreamReader(conn.getInputStream());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// Load the results into a StringBuilder
			int read;
			char[] buff = new char[1024];
			try {
				while ((read = in.read(buff)) != -1) {
					jsonResults.append(buff, 0, read);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			resp = jsonResults.toString();
			return resp;   	


		}

		protected void onPostExecute(String resp)
		{    	
			response = resp; 
			double[] latLong = new double[2];

			JSONObject jsonObj = null;
			try {
				jsonObj = (JSONObject) new JSONTokener(response).nextValue();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			JSONArray results = null;
			try {
				results = (JSONArray) jsonObj.getJSONArray("results");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			JSONObject location = null;
			try {
				location = (JSONObject) results.getJSONObject(0)
						.getJSONObject("geometry").getJSONObject("location");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			double lat = 0;
			try {
				lat = location.getDouble("lat");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			double lng = 0;
			try {
				lng = location.getDouble("lng");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			latLong[0] = lat;
			latLong[1] = lng;
			try {
				key = URLEncoder.encode(keywords.getText().toString(), "utf8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			FoursquareActivityIntent.putExtra("longitude", latLong[1]);
			FoursquareActivityIntent.putExtra("latitude", latLong[0]);
			FoursquareActivityIntent.putExtra("Filter", selected);
			FoursquareActivityIntent.putExtra("keywords", key);

			spinner.setSelection(0);
			autoCompView.setText("");
			keywords.setText("");
			startActivity(FoursquareActivityIntent);
			finish();

		}	
	}
}
