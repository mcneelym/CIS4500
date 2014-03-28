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
import android.widget.Toast;

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
		
		//Make sure we have network connectivity
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			//Call the login task
			Toast toast = Toast.makeText(this, "logging in...", Toast.LENGTH_SHORT);
			toast.show();
			 new LoginTask(this).execute(url, username, password);
		} else {
			Toast toast = Toast.makeText(this, "Network error", Toast.LENGTH_LONG);
			toast.show();
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
		 
		 Context mContext;
		 
		 public LoginTask(Context m) {
			 mContext = m;
		 }
		 
		@Override
		protected String doInBackground(String... info) {
			//Try to create a moodle service object
			MoodleRestService service = MoodleRestService.init(info[0], info[1], info[2]);
			//If we made an object it means we are logged in, else some sort of error
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
			//If we were successful go back to the main page
            if (result != null) {
            	finish();
            }
            else
            {
            	Toast toast = Toast.makeText(mContext, "Failed to login", Toast.LENGTH_LONG);
    			toast.show();
            }
       }
	 }

}
