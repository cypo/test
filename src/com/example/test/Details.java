package com.example.test;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.test.MainActivity.JSONParse;
import com.example.test.MainActivity.MyLocationListener;
import com.squareup.picasso.Picasso;


public class Details extends Activity {
	public String jsonName, jsonGender, jsonAdress, jsonBeer, jsonShot, jsonLogo, jsonDescription, jsonLongitude, jsonLatitude;
	private TextView name, gender, adress, beer, shot, description, savedIdTV, jsonLong, jsonLati, aaa, bbb;
	private Button PAButton;
	public String savedId;
	JSONArray jArray = null;
	ImageView logo;
	  public static final String TAG_NAME = "name";
	  public static final String TAG_GENDER = "gender";
	  public static final String TAG_ADRESS = "adress";
	  public static final String TAG_BEER = "beer";
	  public static final String TAG_SHOT = "shot";
	  public static final String TAG_DESCRIPTION = "opis";
	  public static final String TAG_LOGO = "logo";
	  public static final String TAG_LATITUDE = "latitude";
	  public static final String TAG_LONGITUDE = "longitude";
	  
	  private static final String GETTING_DATA = "Pobieranie danych...";
	  private static final String PROX_ALERT_INTENT = "com.example.test.ProximityIntentReceiver";
	  
	  private static final NumberFormat nf = new DecimalFormat("##.########");
	  private static final NumberFormat distFormat = new DecimalFormat("##.##");
	  private static final String POINT_LATITUDE_KEY = "POINT_LATITUDE_KEY";
	  private static final String POINT_LONGITUDE_KEY = "POINT_LONGITUDE_KEY";
	  public static final String URL = "http://cypo.esy.es/lodz.json";
	  
	  double doubleLatitude;
	  double doubleLongitude;
	  
	  
	  private LocationManager lm;
	  private LocationListener ll;
		

	  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);
		getActionBar().setTitle(R.string.details);
		getActionBar().setDisplayHomeAsUpEnabled(true);
				
			Intent intent = getIntent();
			savedId = intent.getStringExtra("id");
			
			

			
			PAButton = (Button)findViewById(R.id.PAButton);
			PAButton.setOnClickListener(new View.OnClickListener() {
			      @Override
			      public void onClick(View view) {
			    
					
			    	addProximityAlert(doubleLatitude, doubleLongitude);
			      }
		           });
					
			new JSONParse().execute();
/*			lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
			ll = x.new MyLocationListener();
		    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);*/

			   lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
			   ll = new MyLocationListener();
			   lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);  
			//niepotrzebne
/*			savedIdTV = (TextView)findViewById(R.id.savedId);
			savedIdTV.setText(savedId);*/
			
	}
	public class MyLocationListener implements LocationListener {
		  
	    public void onLocationChanged(Location location) {
	        			//Location pointLocation = retrievelocationFromPreferences();
			            //float distance = location.distanceTo(pointLocation);
			        	//dist.setText("Odleg³oœæ od punktu: "+distFormat.format(distance)+"m");
			            //Toast.makeText(MainActivity.this, "Distance from Point:"+distFormat.format(distance)+"m", Toast.LENGTH_SHORT).show();
			            //gps.setText("Szerokoœæ: "+ location.getLatitude() + " D³ugoœæ: " + location.getLongitude());
			        }
	    public void onStatusChanged(String s, int i, Bundle b) {           
			        }
	    public void onProviderDisabled(String s) {
			        	
			        }
	    public void onProviderEnabled(String s) {           
			        }
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.details, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	

	//Tworzenie procesu asynctask
	   public class JSONParse extends AsyncTask<String, String, JSONArray> {
		   
		     private ProgressDialog pDialog;
	       @Override
	         protected void onPreExecute() {
	             super.onPreExecute();
	             //definiowanie okna dialogowego ³adowania
	             pDialog = new ProgressDialog(Details.this);
	             pDialog.setMessage("Pobieranie danych");
	             pDialog.setIndeterminate(false);
	             pDialog.setCancelable(true);
	             pDialog.show();
	       }
	       
	       //zwraca jsona przez klasê JSONParser
	       @Override
	       protected JSONArray doInBackground(String... args) {
	       JSONParser jParser = new JSONParser();
	       // Getting JSON from URL
	       JSONArray json = jParser.getJSONFromUrl(MainActivity.URL);
	       return json;
	     }
	      @TargetApi(Build.VERSION_CODES.KITKAT)
		@Override
	        protected void onPostExecute(JSONArray json) {
	        pDialog.dismiss();
	        try {
	           // Getting JSON Array from URL
	
	           jArray = json;
	           int savedIdInt = Integer.parseInt(savedId);
	           JSONObject c = jArray.getJSONObject(savedIdInt-1);
	           // Storing  JSON item in a Variable
	           jsonName = c.getString(MainActivity.TAG_NAME);
	           jsonGender = c.getString(MainActivity.TAG_GENDER);
	           jsonAdress = c.getString(MainActivity.TAG_ADRESS);
	           jsonBeer = c.getString(MainActivity.TAG_BEER);
	           jsonShot = c.getString(MainActivity.TAG_SHOT);
	           jsonDescription = c.getString(MainActivity.TAG_DESCRIPTION);
	           jsonLogo = c.getString(MainActivity.TAG_LOGO);
	           jsonLongitude = c.getString(MainActivity.TAG_LONGITUDE);
	           jsonLatitude = c.getString(MainActivity.TAG_LATITUDE);
	                          	         	           
	           name = (TextView)findViewById(R.id.detailsName);
	           gender = (TextView)findViewById(R.id.detailsGender);
	           adress = (TextView)findViewById(R.id.detailsAdress);
	           beer = (TextView)findViewById(R.id.detailsBeer);
	           shot = (TextView)findViewById(R.id.detailsShot);
	           description = (TextView)findViewById(R.id.detailsDescription);
	           jsonLati = (TextView)findViewById(R.id.jsonLati);
	           jsonLong = (TextView)findViewById(R.id.jsonLong);
	           
	        		   
	           name.setText(jsonName);
	           gender.setText(jsonGender);
	           adress.setText(jsonAdress);
	           beer.setText(jsonBeer);
	           shot.setText(jsonShot);
	           description.setText(jsonDescription);
	           jsonLati.setText(jsonLatitude);
	           jsonLong.setText(jsonLongitude);
	   	           
				logo = (ImageView)findViewById(R.id.logo);
				Picasso.with(getApplicationContext()).load(jsonLogo).into(logo);
				
				doubleLatitude = Double.parseDouble(jsonLatitude);
				doubleLongitude = Double.parseDouble(jsonLongitude);
	           
	        	} 
	        catch (JSONException e) {
	         e.printStackTrace();
	       }

	        
	      }
	    }   
		//DO zautomatyzowania nadawania lokalizacji
   	/*private void saveProximityAlertPoint() {
   		
       Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
       if (location==null) {
           Toast.makeText(this, "No last known location. Aborting...", Toast.LENGTH_LONG).show();
           return;
       	}
           addProximityAlert(location.getLatitude(), location.getLongitude());
           saveCoordinatesInPreferences((float)location.getLatitude(), (float)location.getLongitude());
           Log.d("GPSStatus", String.valueOf(location.getLatitude())+" "+String.valueOf(location.getLongitude()));
   	}*/

	   private void saveCoordinatesInPreferences(float latitude, float longitude) {
	         SharedPreferences prefs = this.getSharedPreferences("com.example.test", Context.MODE_PRIVATE);
	         SharedPreferences.Editor prefsEditor = prefs.edit();
	         prefsEditor.putFloat(POINT_LATITUDE_KEY, latitude);
	         prefsEditor.putFloat(POINT_LONGITUDE_KEY, longitude);
	         prefsEditor.commit();
	     }
   	

		
		private void addProximityAlert(double latitude, double longitude) {
			Intent intent = new Intent(PROX_ALERT_INTENT);
	        PendingIntent proximityIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		    //Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		    
		    lm.addProximityAlert(latitude, longitude, 50, -1, proximityIntent);
		    float longFloat = (float)longitude;
		    float latiFloat = (float)latitude;
		    saveCoordinatesInPreferences(latiFloat, longFloat);
		    
		    //do wyrzucenia
		    aaa = (TextView)findViewById(R.id.aaa);
		    bbb = (TextView)findViewById(R.id.bbb);
		    String aaaString = Double.toString(latitude);
		    String bbbString = Double.toString(longitude);
		    aaa.setText(aaaString);
		    bbb.setText(bbbString);
		    
		    IntentFilter filter = new IntentFilter(PROX_ALERT_INTENT); 
		   	registerReceiver(new ProximityIntentReceiver(), filter);	        
	    	}
		@Override
		public void onPause(){
			super.onPause();
			lm.removeUpdates(ll);
		}
		
		@Override
		public void onStop(){
			super.onStop();
			lm.removeUpdates(ll);
		}
		
		@Override
		public void onResume(){
			super.onResume();
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll); 
		}
	}
