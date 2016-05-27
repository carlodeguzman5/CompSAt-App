package org.app.compsat.compsatapplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by carlo on 4/20/2016.
 */
public class RestClient {
    private JSONObject paramList;
    private HttpURLConnection httpURLConnection;
    private URL address;
    private JSONArray output;
    private int statusCode;
    public RestClient(String url){

        try {
            address = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        paramList = new JSONObject();
    }

    public void addParam(String key, String value){
        try {
            paramList.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void execute(){

        try {
            httpURLConnection = (HttpURLConnection) address.openConnection();
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setConnectTimeout(15000);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            OutputStream os = httpURLConnection.getOutputStream();
            os.write(paramList.toString().getBytes("UTF-8"));
            os.flush();

            statusCode = httpURLConnection.getResponseCode();

            /*if(httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_CREATED){
                throw new RuntimeException("Failed : HTTP error code : "
                        + httpURLConnection.getResponseCode());
            }*/

            BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));

            String tempLine;
            String tempOutput = "";
            while((tempLine = br.readLine()) != null){
                tempOutput += tempLine + "\n";
            }

            output = new JSONArray(tempOutput);

            httpURLConnection.disconnect();

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONArray getResponse(){
        return output;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
