package com.example.moodlemobile;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class LoginScreen extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		hideActionBar();
		setContentView(R.layout.activity_login_screen);
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void hideActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.hide();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.login_screen, menu);
		return true;
	}
	
	/** Called when the user clicks the Login button */
	public void login(View view) {
		String url = ((TextView)findViewById(R.id.editUrl)).getText().toString();
		String username = ((TextView)findViewById(R.id.editUsername)).getText().toString();
		String password = ((TextView)findViewById(R.id.editPassword)).getText().toString();
		
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			 new LoginTask().execute(url, username, password);
		} else {
			//TODO: Error
		}
	}
	
	@Override
	public void finish() {
	  // Prepare data intent 
	  Intent data = new Intent();
	  setResult(RESULT_OK, data);
	  super.finish();
	} 
	
	 private class LoginTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... info) {
			MoodleRestService service = MoodleRestService.init(info[0], info[1], info[2]);
			if (service != null) {
				return service.getToken();
			}
			else
			{
				return null;
			}
		}
		
		@Override
        protected void onPostExecute(String result) {
            if (result != null) {
            	finish();
            }
            else
            {
            	
            }
       }
	 }

}
