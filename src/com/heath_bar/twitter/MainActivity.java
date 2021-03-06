package com.heath_bar.twitter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONObject;
import org.json.JSONArray;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.heath_bar.twitter.tweetsAdapter;

public class MainActivity extends Activity {

  // final String URL = "https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=twitterapi&include_rts=1";
  final String URL = "https://api.twitter.com/1.1/search/tweets.json?q=mobiledev&count=15";
  final String APIKEY = "64BfTDfttvcFO5TnbxE1Q";
  final String APISECRET = "rsxPcW3uFAnGNgiZvY5QnfiId6GY0VMXeyPofSlXtg";
  
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  //  Main
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    
    // "Get Bearer Token" Button
    Button btn_bearer_token = (Button)findViewById(R.id.btn_bearer_token);
    btn_bearer_token.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        new GetBearerTokenTask().execute();
      }
    });
    
    // "Get Feed" Token
    Button btn_get_feed = (Button)findViewById(R.id.btn_get_feed);
    btn_get_feed.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        TextView txt_bearer_token = (TextView)findViewById(R.id.txt_bearer_token);
        String bearer_token = txt_bearer_token.getText().toString();
        new GetFeedTask().execute(bearer_token, URL);
      }

    });
  }

  

  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  //  GetBearerTokenTask
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  
  protected class GetBearerTokenTask extends AsyncTask<Void, Void, String> {
    
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~  
    @Override // GetBearerTokenTask
    protected String doInBackground(Void... params) {
      
      try {

        DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
        HttpPost httppost = new HttpPost("https://api.twitter.com/oauth2/token");
        
        String apiString = APIKEY + ":" + APISECRET;
        String authorization = "Basic " + Base64.encodeToString(apiString.getBytes(), Base64.NO_WRAP);
    
        httppost.setHeader("Authorization", authorization);
        httppost.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
        httppost.setEntity(new StringEntity("grant_type=client_credentials"));
    
        InputStream inputStream = null;
        HttpResponse response = httpclient.execute(httppost);
        HttpEntity entity = response.getEntity();
    
        inputStream = entity.getContent();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
        StringBuilder sb = new StringBuilder();
    
        String line = null;
        while ((line = reader.readLine()) != null)
        {
            sb.append(line);
        }
        
        return sb.toString(); // pass string to onPostExectute

      }catch (Exception e){

        Log.e("GetBearerTokenTask", "Error:" + e.getMessage());
        return null;

      }
    }
    
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    @Override // GetBearerTokenTask
    protected void onPostExecute(String jsonText){

      try {

        JSONObject root = new JSONObject(jsonText);
        String bearer_token = root.getString("access_token");     
        
        TextView txt = (TextView)findViewById(R.id.txt_bearer_token);
        txt.setText(bearer_token);
        
      }catch (Exception e){

        Log.e("GetBearerTokenTask", "Error:" + e.getMessage());

      }
    }
  }


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  //  GetFeedTask
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  protected class GetFeedTask extends AsyncTask<String, Void, String> {

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    @Override //GetFeedTask
    protected String doInBackground(String... params) {
      
      try {

        DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
        HttpGet httpget = new HttpGet(params[1]);
        httpget.setHeader("Authorization", "Bearer " + params[0]);
        httpget.setHeader("Content-type", "application/json");

        InputStream inputStream = null;
        HttpResponse response = httpclient.execute(httpget);
        HttpEntity entity = response.getEntity();

        inputStream = entity.getContent();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
        StringBuilder sb = new StringBuilder();

        String line = null;
        while ((line = reader.readLine()) != null)
        {
          sb.append(line);
        }
        return sb.toString(); // pass string to onPostExecute

      } catch (Exception e){

        Log.e("GetFeedTask", "Error:" + e.getMessage());
        return null;

      }
    }
    
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    @Override //GetFeedTask
    protected void onPostExecute(String jsonText){


      try {

        final ArrayList<Tweet> tweets = new ArrayList<Tweet>();
        final ListView myListView = (ListView) findViewById(R.id.listTweets);
        final tweetsAdapter myTweetsAdapter = new tweetsAdapter(MainActivity.this, R.layout.tweet, tweets);
        myListView.setAdapter(myTweetsAdapter);

        JSONArray statuses = null;
        JSONObject status = null;
        int i;

        JSONObject json_feed = new JSONObject(jsonText);
        statuses = json_feed.getJSONArray("statuses");
        
        for (i=0; i<statuses.length(); i++) {
          status = statuses.getJSONObject(i);
          Tweet new_entry = new Tweet (
        		  status.getString("text"),
        		  status.getString("created_at"),
        		  status.getJSONObject("user").getString("name"),
        		  status.getJSONObject("user").getString("profile_image_url")
        		  );
          tweets.add(0, new_entry);
          myTweetsAdapter.notifyDataSetChanged();
        }


      }catch (Exception e){

        Log.e("GetFeedTask", "Error:" + e.getMessage());

      }

    }
  }
  
  
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  //  Inflater
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;  
    
  }

}
