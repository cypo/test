package com.example.test;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.view.Menu;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;

import android.widget.*;


public class MainActivity extends Activity {

	public TextView name, gender, adress, beer, shot;
	private ListView list;
	private ListView listView1;
	public Button btngetdata;
	ArrayList<HashMap<String, String>> clubList = new ArrayList<HashMap<String, String>>();
	
	  //URL to get JSON Array
	  private static String url = "http://cypo.esy.es/lodz.json";

		//JSON Node Names
	  private static final String TAG_CLUBS = "kluby";
	  private static final String TAG_NAME = "name";
	  private static final String TAG_GENDER = "gender";
	  private static final String TAG_ADRESS = "adress";
	  private static final String TAG_BEER = "beer";
	  private static final String TAG_SHOT = "shot";
	  
	  JSONArray android = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		new JSONParse().execute();
		clubList = new ArrayList<HashMap<String, String>>();
             
		btngetdata = (Button)findViewById(R.id.btngetdata);
	    btngetdata.setOnClickListener(new View.OnClickListener() {
	      @Override
	      public void onClick(View view) {
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
	
   class JSONParse extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
       @Override
         protected void onPreExecute() {
             super.onPreExecute();
             name = (TextView)findViewById(R.id.name);
             gender = (TextView)findViewById(R.id.gender);
             adress = (TextView)findViewById(R.id.adress);
             beer = (TextView)findViewById(R.id.beer);
             shot = (TextView)findViewById(R.id.shot);
              
             pDialog = new ProgressDialog(MainActivity.this);
             pDialog.setMessage("Getting Data ...");
             pDialog.setIndeterminate(false);
             pDialog.setCancelable(true);
             pDialog.show();
       }
       @Override
       protected JSONObject doInBackground(String... args) {
       JSONParser jParser = new JSONParser();
       // Getting JSON from URL
       JSONObject json = jParser.getJSONFromUrl(url);
       return json;
     }
      @Override
        protected void onPostExecute(JSONObject json) {
        pDialog.dismiss();
        try {
           // Getting JSON Array from URL
           android = json.getJSONArray(TAG_CLUBS);
           for(int i = 0; i < android.length(); i++){
           JSONObject c = android.getJSONObject(i);
           // Storing  JSON item in a Variable
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
           clubList.add(map);
           
           list=(ListView)findViewById(R.id.listView1);
           ListAdapter adapter = new SimpleAdapter(MainActivity.this, clubList,
               R.layout.activity_list,
               new String[] { TAG_NAME,TAG_GENDER, TAG_ADRESS, TAG_BEER, TAG_SHOT }, new int[] {
                   R.id.name,R.id.gender, R.id.adress, R.id.beer, R.id.shot});
           list.setAdapter(adapter);
           
           list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                   @Override
                   public void onItemClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                       Toast.makeText(MainActivity.this, clubList.get(+position).get("name"), Toast.LENGTH_SHORT).show();
                   }
               });
           }
       } catch (JSONException e) {
         e.printStackTrace();
       }
      }
   }
	
	

      
}
