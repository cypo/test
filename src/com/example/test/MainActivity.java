package com.example.test;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import java.io.*;
import android.widget.*;
import android.widget.ArrayAdapter;


public class MainActivity extends Activity {

	public TextView nazwa1, gatunek1, adres1, piwo1, shot1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		list();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
	
		return true;

	}
    private String readTextFromRawResource(int resourceId) {

        InputStream inputStream = getResources().openRawResource(resourceId);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        int i;
        try {
            i = inputStream.read();
            while (i != -1) {
                byteArrayOutputStream.write(i);
                i = inputStream.read();
            }
            inputStream.close();
        } catch (IOException e) {
           //Log.e(TAG, "Error while opening resource", e);
        }

        return byteArrayOutputStream.toString();
    }
    
    public void list(){
		try {
			JSONObject json = new JSONObject(readTextFromRawResource(R.raw.lodz));
			//json.getJSONArray("kluby").getJSONObject(0).toString();
			HomeResponse response = HomeResponse.fromJsonObject(json);
			nazwa1 = (TextView) findViewById(R.id.nazwa1);
			gatunek1 = (TextView) findViewById(R.id.gatunek1);
			adres1 = (TextView) findViewById(R.id.adres1);
			piwo1 = (TextView) findViewById(R.id.piwo1);
			shot1 = (TextView) findViewById(R.id.shot1);
			

			nazwa1.setText(response.kluby[0].toString());
			gatunek1.setText("Muzyka: "+response.kluby[0].gatunek);
			adres1.setText("Adres: "+response.kluby[0].adres);
			piwo1.setText("Piwo: "+Double.toString(response.kluby[0].piwo)+"z³.");
			shot1.setText("Shot: "+Double.toString(response.kluby[0].shot)+"z³.");
		
			
			} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }
}
