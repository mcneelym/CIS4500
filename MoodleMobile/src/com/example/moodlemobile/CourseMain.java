package com.example.moodlemobile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.app.ListActivity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class CourseMain extends ListActivity {
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_course_main);
		new GetCoursesTask(this).execute();
	}

	@Override 
    public void onListItemClick(ListView l, View v, int position, long id) {

    }
	
	private class GetCoursesTask extends AsyncTask<String, Void, HashMap<String,Long>> {
		
		private Context mContext;
	    public GetCoursesTask (Context context){
	         mContext = context;
	    }
		
		@Override
		protected HashMap<String,Long> doInBackground(String... info) {
			MoodleRestService service = MoodleRestService.getService();
			if (service != null) {
				String json;
				HashMap<String,Long> array = new HashMap<String,Long>();
				try {
					 json = service.getCourses();
				} catch (IOException e) {
					return null;
				}
				try {
					JSONArray jArray = new JSONArray(json);
					JSONObject json_data = null;
					for (int i = 0; i < jArray.length(); i++) {
						json_data = jArray.getJSONObject(i);
						array.put(json_data.getString("fullname"),json_data.getLong("id"));
					}
				} catch (JSONException e) {
					return null;
				}
				return array;
			}
			else
			{
				return null;
			}
		}
		
		@Override
		protected void onPostExecute(HashMap<String,Long> result) {
			if (result != null) {
				CourseArrayAdapter adapter = new CourseArrayAdapter(mContext, R.layout.row_layout, result);
				((ListActivity)mContext).setListAdapter(adapter);
			}
			else
			{
			   	
			}
		}
	}
	
	private class CourseArrayAdapter extends ArrayAdapter<String> {

		HashMap<String, Long> mIdMap;
		
		public CourseArrayAdapter(Context context, int textViewResourceId, HashMap<String,Long> objects) {
			super(context, textViewResourceId, new ArrayList<String>(objects.keySet()));
			mIdMap = objects;
		}
		
		@Override
		public long getItemId(int position) {
			String item = getItem(position);
			return mIdMap.get(item);
		}
		
		@Override
		public boolean hasStableIds() {
			return true;
		}
	}

}
