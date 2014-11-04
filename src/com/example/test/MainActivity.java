package com.example.test;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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


public class MainActivity extends Activity {
	
	public String savedName;

	private TextView name, gender, adress, beer, shot;
	private ListView list;
	public Button btngetdata;
	private ArrayList<HashMap<String, String>> clubList = new ArrayList<HashMap<String, String>>();

	  //URL to get JSON Array
	  public static final String URL = "http://cypo.esy.es/lodz.json"; //dodaæ wyj¹tek jeœli serwer nie odpowiada/nie istnieje

		//JSON Node Names
	  public static final String TAG_CLUBS = "kluby";
	  public static final String TAG_NAME = "name";
	  public static final String TAG_GENDER = "gender";
	  public static final String TAG_ADRESS = "adress";
	  public static final String TAG_BEER = "beer";
	  public static final String TAG_SHOT = "shot";
	  
	  private static final String GETTING_DATA = "Pobieranie danych...";
	  
	  JSONArray jArray = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
				
		new JSONParse().execute();
		             
		btngetdata = (Button)findViewById(R.id.btngetdata);
	    btngetdata.setOnClickListener(new View.OnClickListener() {
	      @Override
	      public void onClick(View view) {
	    	  clubList.clear();
	    	  new JSONParse().execute();
	      }
           });
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
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
                      //Toast.makeText(MainActivity.this, clubList.get(+position).get("name"), Toast.LENGTH_SHORT).show();
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
}
