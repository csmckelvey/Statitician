package com.csmckelvey.statitician;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.widget.Toast;

public class StatWebService {

	private Context myContext;
	private HttpPost httpPost;
	private HttpClient httpClient;
	private HttpResponse httpResponse;
	
	public StatWebService(Context context) { 
		this.httpClient = new DefaultHttpClient();
		this.myContext = context;
	}

	public BufferedReader deploy(String post) { return deploy(post, null); }
	public BufferedReader deploy(String post, ArrayList<BasicNameValuePair> params) {
		this.httpPost = new HttpPost(post);
		if (params != null) { 
			try { this.httpPost.setEntity(new UrlEncodedFormEntity(params)); }
			catch (UnsupportedEncodingException e) { e.printStackTrace(); }
		}
		
		try { 
			this.httpResponse = httpClient.execute(httpPost); 
			return new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
		}
		catch (IllegalStateException e) { e.printStackTrace(); }
		catch (IOException e) { e.printStackTrace(); }
		return null;
	}
	public Runnable loginFailedToast() {
		return new Runnable() {
			public void run() {
				Toast.makeText(myContext, "Login Failed ...", Toast.LENGTH_SHORT).show();
			}
		};
	}
	public Runnable successToast() {
		return new Runnable() {
			public void run() {
				Toast.makeText(myContext, "SUCCESS!", Toast.LENGTH_SHORT).show();
			}
		};
	}
	public Runnable failedToast() {
		return new Runnable() {
			public void run() {
				Toast.makeText(myContext, "Failed ...", Toast.LENGTH_SHORT).show();
			}
		};
	}
}
