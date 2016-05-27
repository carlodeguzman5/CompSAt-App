package org.app.compsat.compsatapplication;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NewsfeedActivity extends ListActivity implements SwipeRefreshLayout.OnRefreshListener{


    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressDialog pDialog;

    // events JSONArray
    JSONArray newsfeedJsonArray = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);


        new LoadAllEvents().execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        new LoadAllEvents().execute();
    }

    class LoadAllEvents extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(NewsfeedActivity.this);
            pDialog.setIcon(R.drawable.gear);
            pDialog.setMessage("Loading Newsfeed. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting All news from url
         */
        protected String doInBackground(String... args) {

            String url = "http://app.compsat.org/index.php/News_controller/news/format/json";

            RestClient newsRestClient = new RestClient(url);

            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();

            if(activeNetworkInfo != null && activeNetworkInfo.isConnected()){
                // getting JSON string from URL
                newsRestClient.execute();

                if(newsRestClient.getStatusCode() == 200){

                    newsfeedJsonArray = newsRestClient.getResponse();
                    writeToFile(newsfeedJsonArray.toString());
                }
                else{
                    try {
                        newsfeedJsonArray = new JSONArray(readFromFile());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            else{
                try {
                    newsfeedJsonArray = new JSONArray(readFromFile());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
        // updating UI from Background Thread

        /**
         * After completing background task Dismiss the progress dialog
         * **/

        protected void onPostExecute(String file_url) {
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    NewsfeedListAdapter adapter = new NewsfeedListAdapter(NewsfeedActivity.this, newsfeedJsonArray);
                    setListAdapter(adapter);
                    pDialog.dismiss();
                    swipeRefreshLayout.setRefreshing(false);
                }
            });

        }
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    private void writeToFile(String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("newsfeed.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private String readFromFile() {

        String ret = "";

        try {
            InputStream inputStream = openFileInput("newsfeed.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Log.e("News Feed", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("News Feed", "Can not read file: " + e.toString());
        }

        return ret;
    }

}
