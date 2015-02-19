package com.example.test;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


public class Details extends Activity {
	public String jsonName, jsonGender, jsonAdress, jsonBeer, jsonShot, jsonLogo, jsonDescription;
	private TextView name, gender, adress, beer, shot, description, savedIdTV;
	public String savedId;
	JSONArray jArray = null;
	ImageView logo;
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
	  public static final String URL = "http://cypo.esy.es/lodz.json";
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);
		getActionBar().setTitle(R.string.details);
		getActionBar().setDisplayHomeAsUpEnabled(true);
				
			Intent intent = getIntent();
			savedId = intent.getStringExtra("id");
					
			new JSONParse().execute();
			
			//niepotrzebne
			savedIdTV = (TextView)findViewById(R.id.savedId);
			savedIdTV.setText(savedId);
			
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
	   public class JSONParse extends AsyncTask<String, String, JSONObject> {
		   
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
	       protected JSONObject doInBackground(String... args) {
	       JSONParser jParser = new JSONParser();
	       // Getting JSON from URL
	       JSONObject json = jParser.getJSONFromUrl(MainActivity.URL);
	       return json;
	     }
	      @Override
	        protected void onPostExecute(JSONObject json) {
	        pDialog.dismiss();
	        try {
	           // Getting JSON Array from URL
	
	           jArray = json.getJSONArray(MainActivity.TAG_CLUBS);
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
	                          	         	           
	           name = (TextView)findViewById(R.id.detailsName);
	           gender = (TextView)findViewById(R.id.detailsGender);
	           adress = (TextView)findViewById(R.id.detailsAdress);
	           beer = (TextView)findViewById(R.id.detailsBeer);
	           shot = (TextView)findViewById(R.id.detailsShot);
	           description = (TextView)findViewById(R.id.detailsDescription);
	           
	        		   
	           name.setText(jsonName);
	           gender.setText(jsonGender);
	           adress.setText(jsonAdress);
	           beer.setText(jsonBeer);
	           shot.setText(jsonShot);
	           description.setText(jsonDescription);
	   	           
				logo = (ImageView)findViewById(R.id.logo);
				Picasso.with(getApplicationContext()).load(jsonLogo).into(logo);
	           
	        	} 
	        catch (JSONException e) {
	         e.printStackTrace();
	       }

	        
	      }
	    }   
	}
