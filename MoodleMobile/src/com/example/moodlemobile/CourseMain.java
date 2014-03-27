package com.example.moodlemobile;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import net.beaconhillcott.moodlerest.MoodleCourse;
import net.beaconhillcott.moodlerest.MoodleRestEnrol;
import net.beaconhillcott.moodlerest.MoodleRestException;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class CourseMain extends Activity {
	
	ListView lv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new GetCoursesTask(this).execute();
		setContentView(R.layout.activity_course_main);
		lv = (ListView) findViewById(R.id.listView1);
		
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		      @Override
		      public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
		    	  final MoodleCourse item = (MoodleCourse) parent.getItemAtPosition(position);
		        
		      }
		    });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.course_main, menu);
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
	
	private class GetCoursesTask extends AsyncTask<String, Void, MoodleCourse[]> {
		
		private Context mContext;
	    public GetCoursesTask (Context context){
	         mContext = context;
	    }
		
		@Override
		protected MoodleCourse[] doInBackground(String... info) {
			MoodleRestService service = MoodleRestService.getService();
			MoodleCourse[] courses = null;
			if (service != null) {
				try {
					courses = MoodleRestEnrol.getUsersCourses(service.getUserId());
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (MoodleRestException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return courses;
			}
			else
			{
				return null;
			}
		}
		
		@Override
		protected void onPostExecute(MoodleCourse[] result) {
			if (result != null) {
				List<MoodleCourse> courses = Arrays.asList(result); 
				final CoursesArrayAdapter adapter = new CoursesArrayAdapter(mContext, android.R.layout.simple_list_item_1, courses);
				lv.setAdapter(adapter);

			}
			else
			{
			   	
			}
		}
	}
	
	private class CoursesArrayAdapter extends ArrayAdapter<MoodleCourse>
	{
		HashMap<MoodleCourse, Integer> mIdMap = new HashMap<MoodleCourse, Integer>();
		
		public CoursesArrayAdapter(Context context, int textViewResourceId, List<MoodleCourse> objects) {
			super(context, textViewResourceId);
			for (int i = 0; i < objects.size(); ++i) {
				mIdMap.put(objects.get(i), i);
			}
		}
		
		@Override
		public long getItemId(int position) {
			MoodleCourse item = getItem(position);
			return mIdMap.get(item);
		}
		
		@Override
		public boolean hasStableIds() {
			return true;
		}
	}

}
