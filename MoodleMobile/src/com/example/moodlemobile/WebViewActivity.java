package com.example.moodlemobile;


import org.apache.http.util.EncodingUtils;

import android.app.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web_view);
		WebView myWebView = (WebView) findViewById(R.id.webview);
		WebSettings webSettings = myWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		
		String url = getIntent().getStringExtra("url");
		myWebView.setWebViewClient(new MyWebViewClient());
		//clearCookies("bucky.socs.uoguelph.ca/moodledev/");
		CookieManager.getInstance().setAcceptCookie(true);
		//Try to login on the moodle site before we go to the page that we want
		String data = "username="+ MoodleRestService.getService().getUsername() +"&password="+ MoodleRestService.getService().getPassword();
		myWebView.postUrl(MoodleRestService.getService().getUrl() + "login/index.php", EncodingUtils.getBytes(data, "application/x-www-form-urlencoded"));
		//Cookies don't seem to be transfering properly when we do another load
		CookieSyncManager.getInstance().startSync();
		myWebView.loadUrl(url);
	}


	public void clearCookies(String domain) {
	    CookieManager cookieManager = CookieManager.getInstance();
	    String cookiestring = cookieManager.getCookie(domain);
	    String[] cookies =  cookiestring.split(";");
	    for (int i=0; i<cookies.length; i++) {
	        String[] cookieparts = cookies[i].split("=");
	        cookieManager.setCookie(domain, cookieparts[0].trim()+"=; Expires=Wed, 31 Dec 2025 23:59:59 GMT");
	    }
	    CookieSyncManager.getInstance().sync();
	    cookieManager = CookieManager.getInstance();
	    cookieManager.removeAllCookie();
	    CookieSyncManager.getInstance().sync();
	}


	private class MyWebViewClient extends WebViewClient {
	    @Override
	    public boolean shouldOverrideUrlLoading(WebView view, String url) {
	        if (Uri.parse(url).getHost().equals(Uri.parse(MoodleRestService.getService().getUrl()).getHost())) {
	            // This is my web site, so do not override; let my WebView load the page
	            return false;
	        }
	        
	        // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
	        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
	        startActivity(intent);
	        return true;
	    }
	}

}
