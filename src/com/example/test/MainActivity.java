package com.example.test;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;



public class MainActivity extends Activity {
	
	public String savedName;

	private TextView name, gender, adress, beer, shot, gps, dist, jsonLoc;
	private ListView list;
	public Button btngetdata, getLocationBtn;
	private ArrayList<HashMap<String, String>> clubList = new ArrayList<HashMap<String, String>>();
	private LocationManager lm;
	private LocationListener ll;

	  //URL to get JSON Array
	  public static final String URL = "http://cypo.esy.es/lodz.json"; //dodaæ wyj¹tek jeœli serwer nie odpowiada/nie istnieje

		//JSON Node Names
	  public static final String TAG_CLUBS = "kluby";
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

//VOLLEY/////////////////////////////////////////
	  	private static String TAG = MainActivity.class.getSimpleName();
	  	private Button btnMakeObjectRequest, btnMakeArrayRequest;
	    // Progress dialog
	    private ProgressDialog pDialog;
	    private TextView txtResponse;
	    // temporary string to show the parsed response
	    private String jsonResponse;
//////////////////////////////////////////////////	    
	    
	 // JSONArray jArray = null;
	  JSONArray jArrayLoc = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getActionBar().setTitle(R.string.app_name);
				

// buttons, textViews
		btngetdata = (Button)findViewById(R.id.btngetdata);
	    btngetdata.setOnClickListener(new View.OnClickListener() {
	      @Override
	      public void onClick(View view) {
	    	  clubList.clear();
	    	  new JSONParse().execute();
	      }
           });
	  
	    
	    txtResponse = (TextView)findViewById(R.id.jsonLoc);
	    
	    
	    gps = (TextView)findViewById(R.id.gps);
	    dist = (TextView)findViewById(R.id.distance);
	    dist.setText("Odleg³oœæ od punktu:");
	    getLocationBtn = (Button)findViewById(R.id.getLocationBtn);
	    getLocationBtn.setOnClickListener(new OnClickListener() {           
	    	            @Override
	    	            public void onClick(View v) {
	    	                saveProximityAlertPoint();
	    	            }
	    	        });
	    
// LocationManager initialization
	    lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
	    ll = new MyLocationListener();
	    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);  
	  
	    
		new JSONParse().execute();
	   	makeJsonArrayRequest();
}
	
	
//VOLLEY//////////////////////////
	private void makeJsonArrayRequest() {
		 
	 
	    JsonArrayRequest req = new JsonArrayRequest(URL,
	            new Response.Listener<JSONArray>() {
	                @Override
	                public void onResponse(JSONArray response) {
	                    Log.d(TAG, response.toString());
	 
	                    try {
	                        // Parsing json array response
	                        // loop through each json object
	                        jsonResponse = "";
	                        //for (int i = 0; i < response.length(); i++) {
	 
	                            JSONObject loc = (JSONObject) response.get(1);
	 
	                            String latitude = loc.getString(TAG_LATITUDE);
	                            String longitude = loc.getString(TAG_LONGITUDE);
	                           	 
	                            jsonResponse = latitude + "\n" + longitude + "\n\n";
	                      //  }
	 
	                        txtResponse.setText(jsonResponse);
	 
	                    } catch (JSONException e) {
	                        e.printStackTrace();
	                        Toast.makeText(getApplicationContext(),
	                                "Error: " + e.getMessage(),
	                                Toast.LENGTH_LONG).show();
	                    }
	 	                  
	                }
	            }, new Response.ErrorListener() {
	                @Override
	                public void onErrorResponse(VolleyError error) {
	                    VolleyLog.d(TAG, "Error: " + error.getMessage());
	                    Toast.makeText(getApplicationContext(),
	                            error.getMessage(), Toast.LENGTH_SHORT).show();
	                    }
	            });
	 
	    // Adding request to request queue
	    Controller.getInstance().addToRequestQueue(req);
	}
//////////////////////////////////////////////////////////////////////
	
		
    	private void saveProximityAlertPoint() {
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location==null) {
            Toast.makeText(this, "No last known location. Aborting...", Toast.LENGTH_LONG).show();
            return;
        	}
            addProximityAlert(location.getLatitude(), location.getLongitude());
            saveCoordinatesInPreferences((float)location.getLatitude(), (float)location.getLongitude());
            Log.d("GPSStatus", String.valueOf(location.getLatitude())+" "+String.valueOf(location.getLongitude()));
    	}

		private void saveCoordinatesInPreferences(float latitude, float longitude) {
	         SharedPreferences prefs = this.getSharedPreferences(getClass().getSimpleName(), Context.MODE_PRIVATE);
	         SharedPreferences.Editor prefsEditor = prefs.edit();
	         prefsEditor.putFloat(POINT_LATITUDE_KEY, latitude);
	         prefsEditor.putFloat(POINT_LONGITUDE_KEY, longitude);
	         prefsEditor.commit();
	     }
    	
		private Location retrievelocationFromPreferences() {
	         SharedPreferences prefs = this.getSharedPreferences(getClass().getSimpleName(), Context.MODE_PRIVATE);
	         Location location = new Location("POINT_LOCATION");
	         location.setLatitude(prefs.getFloat(POINT_LATITUDE_KEY, 0));
	         location.setLongitude(prefs.getFloat(POINT_LONGITUDE_KEY, 0));
	         return location;
	     }
		
		private void addProximityAlert(double latitude, double longitude) {
			Intent intent = new Intent(PROX_ALERT_INTENT);
	        PendingIntent proximityIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		    Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		    lm.addProximityAlert(location.getLatitude(), location.getLongitude(), 50, -1, proximityIntent);
		    IntentFilter filter = new IntentFilter(PROX_ALERT_INTENT); 
		   	registerReceiver(new ProximityIntentReceiver(), filter);	        
	    	}

				
	public class MyLocationListener implements LocationListener {
			  
	    public void onLocationChanged(Location location) {
	        			Location pointLocation = retrievelocationFromPreferences();
			            float distance = location.distanceTo(pointLocation);
			        	dist.setText("Odleg³oœæ od punktu: "+distFormat.format(distance)+"m");
			            //Toast.makeText(MainActivity.this, "Distance from Point:"+distFormat.format(distance)+"m", Toast.LENGTH_SHORT).show();
			            gps.setText("Szerokoœæ: "+ location.getLatitude() + " D³ugoœæ: " + location.getLongitude());
			        }
	    public void onStatusChanged(String s, int i, Bundle b) {           
			        }
	    public void onProviderDisabled(String s) {
			        	
			        }
	    public void onProviderEnabled(String s) {           
			        }
	}
		
		
		/*		 private void populateCoordinatesFromLastKnownLocation() {
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (location!=null) {
            latitudeEditText.setText(nf.format(location.getLatitude()));
            longitudeEditText.setText(nf.format(location.getLongitude()));
        }
    }
*/
	
	
	//Tworzenie procesu AsyncTask
   public class JSONParse extends AsyncTask<String, String, JSONArray> {
	     private ProgressDialog pDialog;
       @Override
         protected void onPreExecute() {
             super.onPreExecute();
             //identyfikowanie pól tekstowych w xml
             name = (TextView)findViewById(R.id.name);
             gender = (TextView)findViewById(R.id.gender);
             adress = (TextView)findViewById(R.id.adress);
             beer = (TextView)findViewById(R.id.beer);
             shot = (TextView)findViewById(R.id.shot);
              
             //definiowanie okna dialogowego ³adowania
             pDialog = new ProgressDialog(MainActivity.this);
             pDialog.setMessage(GETTING_DATA);
             pDialog.setIndeterminate(false);
             pDialog.setCancelable(true);
             pDialog.show();
       }
       
       //zwraca jsona przez klasê JSONParser
       @Override
       protected JSONArray doInBackground(String... args) {
           // Getting JSON from URL
    	   JSONParser jParser = new JSONParser();
    	   JSONArray json = jParser.getJSONFromUrl(URL);
    	   return json;
     }
       @TargetApi(Build.VERSION_CODES.KITKAT)
      @Override
        protected void onPostExecute(JSONArray json) {
        pDialog.dismiss();
        
        try {
           JSONArray jArray = json;
           for(int i=0; i<jArray.length(); i++){
           JSONObject c = jArray.getJSONObject(i);
           // Storing  JSON item in a Variable
           String id = c.getString("Id");
           String name = c.getString(TAG_NAME);
           String gender = c.getString(TAG_GENDER);
           String adress = c.getString(TAG_ADRESS);
           String beer = c.getString(TAG_BEER);
           String shot = c.getString(TAG_SHOT);
           // Adding value HashMap key => value
           HashMap<String, String> map = new HashMap<String, String>();
           map.put(TAG_NAME, name);
           map.put(TAG_GENDER, gender);
           map.put(TAG_ADRESS, adress);
           map.put(TAG_BEER, beer);
           map.put(TAG_SHOT, shot);
           map.put("Id", id);
           //map.put("latitude", latitude);
           //map.put("longitude", longitude);
           clubList.add(map);
           
           
           list=(ListView)findViewById(R.id.listView1);
           ListAdapter adapter = new SimpleAdapter(
        		   				MainActivity.this, 
        		   				clubList,
        		   				R.layout.activity_list,
        		   				new String[] { TAG_NAME, TAG_GENDER, TAG_ADRESS, TAG_BEER, TAG_SHOT }, 
        		   				new int[] {	R.id.name,R.id.gender, R.id.adress, R.id.beer, R.id.shot });
           
           list.setAdapter(adapter);
           
           list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                   @Override
                   public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                	  Intent intent = new Intent(getApplicationContext(), Details.class);
                	  intent.putExtra("id", clubList.get(+position).get("Id"));
                	  startActivity(intent);
                   }
               });
           }
       } catch (JSONException e) {
         e.printStackTrace();
       }
      }
   }   
   
   @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public void onPause(){
		super.onPause();
		//lm.removeUpdates(ll);
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
