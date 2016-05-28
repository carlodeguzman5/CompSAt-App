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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


public class LinksActivity extends ListActivity implements SwipeRefreshLayout.OnRefreshListener{

    private Context context = this;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressDialog pDialog;

    private JSONArray linksJsonArray = new JSONArray();


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
            pDialog = new ProgressDialog(LinksActivity.this);
            pDialog.setIcon(R.drawable.gear);
            pDialog.setMessage("Loading linksJsonArray. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting All products from url
         */
        protected String doInBackground(String... args) {
            final ListView listView = getListView();
            runOnUiThread(new Runnable() {
                public void run() {
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            JSONObject item = (JSONObject) parent.getItemAtPosition(position);
                            try {
                                Intent intent = new Intent(context, WebsiteActivity.class);
                                intent.putExtra("url", item.getString("url"));

                                startActivity(intent);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                }
            });

            String url = "http://app.compsat.org/index.php/Link_controller/links/format/json";

            RestClient linksRestClient = new RestClient(url);

            linksRestClient.execute();

            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();


            if(activeNetworkInfo != null && activeNetworkInfo.isConnected()){
               if(linksRestClient.getStatusCode() == 200){
                   linksJsonArray = linksRestClient.getResponse();
                   Log.d("LINKS", linksJsonArray.toString());
                   writeToFile("linksJsonArray.txt", linksJsonArray.toString());
               }
               else{
                   try {
                       linksJsonArray = new JSONArray(readFromFile("linksJsonArray.txt"));
                   } catch (JSONException e) {
                       e.printStackTrace();
                   }
               }
            }
            else{
                try {
                    linksJsonArray = new JSONArray(readFromFile("linksJsonArray.txt"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        protected void onPostExecute(String file_url) {
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    LinksListAdapter adapter = new LinksListAdapter(LinksActivity.this, linksJsonArray);

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
            Log.e("Links", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("Links", "Can not read file: " + e.toString());
        }

        return ret;
    }
}
