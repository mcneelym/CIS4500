package com.example.moodlemobile;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;
import com.example.moodlemobile.CourseMain.Group;
import com.example.moodlemobile.CourseMain.GroupChild;


public class CoursesContentListAdapter extends BaseExpandableListAdapter {
	
	private final SparseArray<Group> groups;
	public LayoutInflater inflater;
	public Context activity;
	
	public CoursesContentListAdapter(Activity act, SparseArray<Group> groups) {
		activity = act;
		this.groups = groups;
	    inflater = act.getLayoutInflater();
	}
	
	@Override
	public int getGroupCount() {
		return groups.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return groups.get(groupPosition).children.size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return groups.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return groups.get(groupPosition).children.get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return 0;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.listrow_group, null);
		}
		Group group = (Group) getGroup(groupPosition);
		((CheckedTextView) convertView).setText(group.name);
		((CheckedTextView) convertView).setChecked(isExpanded);
		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		final GroupChild child = ((GroupChild)getChild(groupPosition, childPosition));
		final String children = child.name;
		TextView text = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.listrow_details, null);
		}
		
		text = (TextView) convertView.findViewById(R.id.textView1);
		if (text.getCompoundDrawables()[0] == null) {
			new getDrawableFromUrl(text).execute(child.modIcon);
		}
		text.setText(children);
		convertView.setOnClickListener(new OnClickListener() {
		      @Override
		      public void onClick(View v) {
		    	  Intent i = new Intent(activity, WebViewActivity.class);
		    	  i.putExtra("url", child.url);
		    	  //i.setData(Uri.parse(child.url));
		    	  activity.startActivity(i);
		      }
		    });
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
	
	private class getDrawableFromUrl extends AsyncTask<String, Void, Drawable> {
		
		TextView text;
		
		public getDrawableFromUrl(TextView text) {
			this.text = text;
		}

		@Override
		protected Drawable doInBackground(String... params) {
			Bitmap x;

		    HttpURLConnection connection;
		    InputStream input;
			try {
				connection = (HttpURLConnection) new URL(params[0]).openConnection();
				connection.addRequestProperty("Cache-Control", "no-cache");
				connection.connect();
				input = connection.getInputStream();
				x = BitmapFactory.decodeStream(input);
				input.close();
			} catch (MalformedURLException e) {
				return null;
			} catch (IOException e) {
				return null;
			}
		    
		    return new BitmapDrawable(activity.getResources(), x);
		}
		
		@Override
		protected void onPostExecute(Drawable result) {
			super.onPostExecute(result);
			result.setBounds(new Rect(0, 0, 64, 64));
			text.setCompoundDrawables(result, null, null, null);
		}
		
	}

}
