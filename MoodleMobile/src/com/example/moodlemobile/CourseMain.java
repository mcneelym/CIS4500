package com.example.moodlemobile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ExpandableListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.SparseArray;

public class CourseMain extends ExpandableListActivity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_course_main);
		new GetCoursesTask(this).execute();
	}
	
	private class GetCoursesTask extends AsyncTask<String, Void,SparseArray<Group>> {
		
		private ExpandableListActivity mContext;
	    public GetCoursesTask (ExpandableListActivity context){
	         mContext = context;
	    }
		
		@Override
		protected SparseArray<Group> doInBackground(String... info) {
			MoodleRestService service = MoodleRestService.getService();
			if (service != null) {
				String json;
				SparseArray<Group> array = new SparseArray<Group>();
				try {
					 json = service.getCourseInformation(service.getCurrentCourseId());
				} catch (IOException e) {
					return null;
				}
				try {
					JSONArray jArray = new JSONArray(json);
					JSONObject json_data = null;
					for (int i = 0; i < jArray.length(); i++) {
						json_data = jArray.getJSONObject(i);
						if (json_data.getInt("visible") == 1) {
							Group g = new Group(json_data.getString("name"), json_data.getLong("id"));
							JSONArray childArray = json_data.getJSONArray("modules");
							for (int j = 0; j < childArray.length(); j++) {
								JSONObject childData = childArray.getJSONObject(j);
								if (childData.getInt("visible") == 1) {
									GroupChild c = new GroupChild(childData.getString("name"), childData.getString("url"), childData.getString("modicon"), childData.getLong("id"));
									g.children.add(c);
								}
							}
							array.append(i, g);
						}
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
		protected void onPostExecute(SparseArray<Group> result) {
			if (result != null) {
				CoursesContentListAdapter adapter = new CoursesContentListAdapter(mContext, result);
				((ExpandableListActivity)mContext).setListAdapter(adapter);
			}
			else
			{
			   	
			}
		}
	}
	
	public class GroupChild {
		public String name;
		public String url;
		public String modIcon;
		public long id;	
		
		public GroupChild(String name, String url, String modIcon, long id) {
			this.name = name;
			this.id = id;
			this.url = url;
			this.modIcon = modIcon;
		}
	}
	
	public class Group {
		
		public String name;
		public long id;
		public final List<GroupChild> children = new ArrayList<GroupChild>();
		
		public Group(String name, long id) {
			this.name = name;
			this.id = id;
		}
	
	} 
	

}
