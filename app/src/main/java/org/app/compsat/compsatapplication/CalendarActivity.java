package org.app.compsat.compsatapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Scanner;


public class CalendarActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener{
    private Activity context = this;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressDialog pDialog;

    private JSONArray eventsJson;
    private JSONArray monthsJson;
    private RecyclerView mRecyclerView;
    private MyRecyclerAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout2);
        swipeRefreshLayout.setOnRefreshListener(this);

        monthsJson = new JSONArray();
        eventsJson = new JSONArray();

        mAdapter = new MyRecyclerAdapter(context ,monthsJson, eventsJson);
        mRecyclerView.setAdapter(mAdapter);

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
            pDialog = new ProgressDialog(CalendarActivity.this);
            pDialog.setIcon(R.drawable.gear);
            pDialog.setMessage("Loading events. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting All products from url
         */
        protected String doInBackground(String... args) {

            /*InputStream is = null;
            try {
                is = getBaseContext().getAssets().open("db/db.properties");
            } catch (IOException e) {
                e.printStackTrace();
            }
            Scanner scanner = new Scanner(is);*/

            String eventsUrl = "http://app.compsat.org/index.php/Calendar_controller/calendar/format/json";
            String monthsUrl = "http://app.compsat.org/index.php/Calendar_controller/months/format/json";


            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();

            if(activeNetworkInfo != null && activeNetworkInfo.isConnected()){

                RestClient eventsClient = new RestClient(eventsUrl);
                RestClient monthsClient = new RestClient(monthsUrl);

                eventsClient.execute();
                monthsClient.execute();

                if(eventsClient.getStatusCode() == 200 && monthsClient.getStatusCode() == 200){
                    eventsJson = eventsClient.getResponse();
                    monthsJson = monthsClient.getResponse();
                    writeToFile("calendar.txt", eventsJson.toString());
                    writeToFile("months.txt", monthsJson.toString());
                }
                else{
                    try {
                        eventsJson = new JSONArray(readFromFile("calendar.txt"));
                        monthsJson = new JSONArray(readFromFile("months.txt"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            else{
                try {
                    eventsJson = new JSONArray(readFromFile("calendar.txt"));
                    monthsJson = new JSONArray(readFromFile("months.txt"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        protected void onPostExecute(String file_url) {
            runOnUiThread(new Runnable() {
                public void run() {
                    mAdapter.updateData(eventsJson,monthsJson);
                    mAdapter.notifyDataSetChanged();
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

    private void writeToFile(String filename, String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput(filename, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private String readFromFile(String filename) {

        String ret = "";

        try {
            InputStream inputStream = openFileInput(filename);

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
            Log.e("Calendar", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("Calendar", "Can not read file: " + e.toString());
        }

        return ret;
    }

}
