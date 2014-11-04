package com.example.test;

import java.util.ArrayList;
import java.util.HashMap;

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
import android.widget.TextView;
import android.widget.Toast;

public class Details extends Activity {
	public String jsonName, jsonGender, jsonAdress, jsonBeer, jsonShot;
	private TextView name, gender, adress, beer, shot, savedIdTV;
	public String savedId;
	private ArrayList<HashMap<String, String>> clubList = new ArrayList<HashMap<String, String>>();
    HashMap<String, String> map = new HashMap<String, String>();
	JSONArray jArray = null;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);
		
		try{
			Intent intent = getIntent();
			savedId = intent.getStringExtra("id");
			}
		catch(NullPointerException e){
			name.setText("NULL!!");
		}

		
			new JSONParse2().execute();
			
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
	   public class JSONParse2 extends AsyncTask<String, String, JSONObject> {
		   
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
	                          	         	           
	           name = (TextView)findViewById(R.id.detailsName);
	           gender = (TextView)findViewById(R.id.detailsGender);
	           adress = (TextView)findViewById(R.id.detailsAdress);
	           beer = (TextView)findViewById(R.id.detailsBeer);
	           shot = (TextView)findViewById(R.id.detailsShot);
	        		   
	           name.setText(jsonName);
	           gender.setText(jsonGender);
	           adress.setText(jsonAdress);
	           beer.setText(jsonBeer);
	           shot.setText(jsonShot);
	           
	           Toast.makeText(Details.this, jsonName, Toast.LENGTH_SHORT).show();

	           
	       } catch (JSONException e) {
	         e.printStackTrace();
	       }
	      }
	   

	   
	   }   
	
	
}
