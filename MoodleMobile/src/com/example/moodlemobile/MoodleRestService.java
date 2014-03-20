package com.example.moodlemobile;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

public class MoodleRestService {

	private String url;
	private String token;
	private static MoodleRestService service = null;
	
	public String getToken() {
		return token;
	}
	
	private MoodleRestService(String url, String username, String password) throws IOException {
		this.url = url;
		setToken(username, password);
	}
	
	public static MoodleRestService init(String url, String username, String password) {
		try {
			service = new MoodleRestService(url, username, password);
		} catch (IOException e) {
			return null;
		}
		return service;
	}
	
	private void setToken(String username, String password) throws IOException {
		url = url + "login/token.php";
		String params = "username=" + username + "&password=" + password + "&service=moodle_mobile_app";

		
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
			String t = response.toString().split(":")[1];
			token = t.substring(1, t.length()-2);
		} else {
			//TODO: throw error
		}
	}

	
}
