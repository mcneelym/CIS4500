package com.example.moodlemobile;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

import org.json.JSONException;
import org.json.JSONObject;


public class MoodleRestService {

	private String url;
	private String token;
	private String username;
	private String fname;
	private String lname;
	private long userid;
	private long currentCourseId = -1;
	
	private static MoodleRestService service = null;
	
	public String getToken() {
		return token;
	}
	
	public long getUserId() {
		return userid;
	}
	
	public void setCurrentCourseId(long id) {
		currentCourseId = id;
	}
	
	public long getCurrentCourseId() {
		return currentCourseId;
	}
	
	public void logout() {
		url = "";
		token = "";
		username = "";
		fname = "";
		lname = "";
		userid = 0;
		currentCourseId = -1;
		service = null;
	}
	
	private MoodleRestService(String url, String username, String password) throws IOException, JSONException {
		this.url = url;
		setToken(username, password);
		this.url = url + "webservice/rest/server.php";
		getUserInfo();
	}
	
	public static MoodleRestService init(String url, String username, String password) {
		try {
			service = new MoodleRestService(url, username, password);
		} catch (IOException e) {
			return null;
		} catch (JSONException e) {
			return null;
		}
		return service;
	}
	
	public static MoodleRestService getService() {
		return service;
	}
	
	private void setToken(String username, String password) throws IOException {
		url = url + "login/token.php";
		String params = "username=" + username + "&password=" + password + "&service=moodle_mobile_app";

		
		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
		con.addRequestProperty("Cache-Control", "max-age=0");
		con.setRequestMethod("POST");
 
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(params);
		wr.flush();
		wr.close();
		int responseCode = con.getResponseCode();

		if (responseCode == 200) {
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
	 
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			String t = response.toString().split(":")[1];
			token = t.substring(1, t.length()-2);
		} else {
			//TODO: throw error
		}
	}

	public String callMoodleFunction(String function, String options) throws IOException {
		String params = "moodlewsrestformat=json&wsfunction=" + function + "&wstoken=" + token + "&" + options;
		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
		con.setRequestMethod("POST");
 
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(params);
		wr.flush();
		wr.close();
		int responseCode = con.getResponseCode();

		if (responseCode == 200) {
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
	 
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			return response.toString();
		} else {
			//TODO: throw error
			return null;
		}
	}
	
	private void getUserInfo() throws IOException, JSONException {
		String json = callMoodleFunction("moodle_webservice_get_siteinfo", "");

		JSONObject jObject = new JSONObject(json);
		userid = jObject.getLong("userid");
		fname = jObject.getString("firstname");
		lname = jObject.getString("lastname");
		username = jObject.getString("username");
	}
	
	public String getCourses() throws IOException {
		String json = callMoodleFunction("moodle_enrol_get_users_courses", "userid="+this.userid);
		return json;
	}
	
	public String getCourseInformation(long courseid) throws IOException {
		String json = callMoodleFunction("core_course_get_contents", "courseid="+courseid);
		return json;
	}
	
}
