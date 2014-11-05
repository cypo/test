package com.example.test;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {
	
	public String savedName;

	private TextView name, gender, adress, beer, shot, gps;
	private ListView list;
	public Button btngetdata;
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
	  
	  private static final String GETTING_DATA = "Pobieranie danych...";
	  private static final String PROX_ALERT_INTENT = "com.example.test.Details";
	  
	  JSONArray jArray = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getActionBar().setTitle(R.string.app_name);
				
		new JSONParse().execute();
		             
		btngetdata = (Button)findViewById(R.id.btngetdata);
	    btngetdata.setOnClickListener(new View.OnClickListener() {
	      @Override
	      public void onClick(View view) {
	    	  clubList.clear();
	    	  new JSONParse().execute();
	      }
           });
	    
	    lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
	    ll = new MyLocationListener();
	        
	    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);  
	    gps = (TextView)findViewById(R.id.gps);
	    
	    Intent intent = new Intent(PROX_ALERT_INTENT);
        PendingIntent proximityIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

	    
	    lm.addProximityAlert(51.85583378, 19.42924514, 1, -1, proximityIntent);
	
	    
	}
	
	 
	   /* private void addProximityAlert(double latitude, double longitude) {
	    	
	    			Intent intent = new Intent(PROX_ALERT_INTENT);
	    	        PendingIntent proximityIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
	    		         
	    	        locationManager.addProximityAlert(
	    	            latitude, // the latitude of the central point of the alert region
	    	            longitude, // the longitude of the central point of the alert region
	    	            POINT_RADIUS, // the radius of the central point of the alert region, in meters
	    	            PROX_ALERT_EXPIRATION, // time for this proximity alert, in milliseconds, or -1 to indicate no expiration
	    	            proximityIntent // will be used to generate an Intent to fire when entry to or exit from the alert region is detected
	    	          );
	    		         
	    	        	IntentFilter filter = new IntentFilter(PROX_ALERT_INTENT); 
	    		       registerReceiver(new ProximityIntentReceiver(), filter);
	    		        
	    		    }*/

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public void onPause(){
		super.onPause();
		lm.removeUpdates(ll);
	}
	
	@Override
	public void onResume(){
		super.onResume();
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll); 
	}
	
	//Tworzenie procesu asynctask
   public class JSONParse extends AsyncTask<String, String, JSONObject> {
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
       protected JSONObject doInBackground(String... args) {
       JSONParser jParser = new JSONParser();
       // Getting JSON from URL
       JSONObject json = jParser.getJSONFromUrl(URL);
       return json;
     }
      @Override
        protected void onPostExecute(JSONObject json) {
        pDialog.dismiss();
        try {
           // Getting JSON Array from URL
           jArray = json.getJSONArray(TAG_CLUBS);
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
  private class MyLocationListener implements LocationListener 
   {           
       @Override
       public void onLocationChanged(Location loc) {
           if (loc != null) {

               gps.setText("Szerokoœæ: "+ loc.getLatitude() + " D³ugoœæ: " + loc.getLongitude());
           }
       }       @Override
               public void onProviderDisabled(String provider) {
                       // TODO Auto-generated method stub
                       
               }

       			@Override
               public void onProviderEnabled(String provider) {
                       // TODO Auto-generated method stub
                       
               }

       			@Override
               public void onStatusChanged(String provider, int status, Bundle extras) {
                       // TODO Auto-generated method stub
                       
               }
   }

}
