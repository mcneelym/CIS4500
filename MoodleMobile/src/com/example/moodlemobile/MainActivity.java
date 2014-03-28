package com.example.moodlemobile;

import java.io.File;
import java.io.IOException;

import android.net.http.HttpResponseCache;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;

public class MainActivity extends Activity {

	private static final int REQUEST_CODE_LOGIN = 23;
	private static final int REQUEST_CODE_COURSE = 24;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// Show the Up button in the action bar.
		setupActionBar();
		
		//Setup caching at the http request level
		//This actually works fairly poorly since the moodle responses tell it to not cache
		try {
			File httpCacheDir = new File(this.getCacheDir(), "http");
			long httpCacheSize = 10 * 1024 * 1024; // 10 MiB
			HttpResponseCache.install(httpCacheDir, httpCacheSize);
		}
		catch (IOException e) {
		
		}
		
		//If we do not have a logged in user or course selected then go to the appropriate page
		if (MoodleRestService.getService() == null || MoodleRestService.getService().getToken().isEmpty()){
			Intent intent = new Intent(this, LoginScreen.class);
			this.startActivityForResult(intent, REQUEST_CODE_LOGIN);
		} else if (MoodleRestService.getService() == null || MoodleRestService.getService().getCurrentCourseId() < 0) {
			Intent intent = new Intent(this, SelectCoursesActivity.class);
			this.startActivityForResult(intent, REQUEST_CODE_COURSE);
		}
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(false);
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	//Handle feedback from pages we have opened and expect a response from
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	  if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_LOGIN) {
		  if (MoodleRestService.getService().getCurrentCourseId() < 0) {
				Intent intent = new Intent(this, SelectCoursesActivity.class);
				this.startActivityForResult(intent, REQUEST_CODE_COURSE);
		  }
	  } else if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_COURSE) {
		  MoodleRestService.getService().setCurrentCourseId(data.getLongExtra("course", -1));
	  }
	} 
	
	
	//Handle button clicks for working pages
	/** Called when the user clicks the course contents button */
	public void courseContents(View view) {
		Intent intent = new Intent(this, CourseMain.class);
		this.startActivity(intent);
	}
	
	/** Called when the user clicks the logout button */
	public void logout(View view) {
		MoodleRestService.getService().logout();
		Intent intent = new Intent(this, LoginScreen.class);
		this.startActivity(intent);
	}

}
