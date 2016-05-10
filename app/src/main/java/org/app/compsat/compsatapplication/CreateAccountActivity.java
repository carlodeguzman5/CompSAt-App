package org.app.compsat.compsatapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;

public class CreateAccountActivity extends Activity {

    EditText idInput, nameInput, passwordInput, yearInput, courseInput, mobileInput, emailInput;
    DatePicker birthdateInput;
    TextView idText, nameText, passText, yearText, courseText, mobileText, emailText;
    Context context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        idInput = (EditText) findViewById(R.id.idNumberCreate);
        nameInput = (EditText) findViewById(R.id.nameCreate);
        passwordInput = (EditText) findViewById(R.id.passwordCreate);
        yearInput = (EditText) findViewById(R.id.yearCreate);
        courseInput = (EditText) findViewById(R.id.courseCreate);
        mobileInput = (EditText) findViewById(R.id.mobileCreate);
        emailInput = (EditText) findViewById(R.id.emailCreate);
        birthdateInput = (DatePicker) findViewById(R.id.datePicker);

        idText = (TextView)findViewById(R.id.textView2);
        nameText = (TextView)findViewById(R.id.textView3);
        passText = (TextView)findViewById(R.id.textView4);
        yearText = (TextView)findViewById(R.id.textView5);
        courseText = (TextView)findViewById(R.id.textView9);
        mobileText = (TextView)findViewById(R.id.textView7);
        emailText = (TextView)findViewById(R.id.textView8);

        Date date = new Date();
        birthdateInput.setMaxDate(date.getTime());


        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Regular.ttf");
        idInput.setTypeface(tf);
        nameInput.setTypeface(tf);
        passwordInput.setTypeface(tf);
        yearInput.setTypeface(tf);
        courseInput.setTypeface(tf);
        mobileInput.setTypeface(tf);
        emailInput.setTypeface(tf);

        idText.setTypeface(tf);
        nameText.setTypeface(tf);
        passText.setTypeface(tf);
        yearText.setTypeface(tf);
        courseText.setTypeface(tf);
        mobileText.setTypeface(tf);
        emailText.setTypeface(tf);

    }

    class CreateAccountTask extends AsyncTask<String, String, String> {
        String url = "http://app.compsat.org/index.php/MobileRegistration_controller/register/format/json/";

        @Override
        protected String doInBackground(String... params) {
            HttpClient client = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            JSONObject json = new JSONObject();
            try {

                json.put("username", params[0]);
                json.put("password", params[1]);
                json.put("name", params[2]);
                json.put("year", params[3]);
                json.put("course", params[4]);
                json.put("mobile", params[5]);
                json.put("email", params[6]);
                json.put("birthdate", params[7]);

                org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(json.toString());
                se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                httpPost.setEntity(se);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            int statusCode = 0;
            try {
                HttpResponse response = client.execute(httpPost);
                //String responseBody = EntityUtils.toString(response.getEntity());
                StatusLine statusLine = response.getStatusLine();
                statusCode = statusLine.getStatusCode();
                Log.d("STATUS", statusCode + "");
            } catch (ClientProtocolException e) {
                Log.d("STATUS", "ERROR");
            } catch (IOException e) {
                Log.d("STATUS", "ERROR");
            }
            return statusCode +"";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s.equals("200")){

                Toast.makeText(context, "Account creation successful", Toast.LENGTH_LONG).show();
                finish();
            }
            else if(s.equals("404")){
                Toast.makeText(context, "ID number is already registered", Toast.LENGTH_LONG).show();
            }
            else if(s.equals("500")){
                Toast.makeText(context, "Internal Error", Toast.LENGTH_LONG).show();
            }

        }
    }
    public void createAccount(View view) {
        if (isFormComplete()) {

            String date = birthdateInput.getYear() + "-" + birthdateInput.getMonth() + "-" + birthdateInput.getDayOfMonth() + "";
            new CreateAccountTask().execute(
                    idInput.getText().toString(),
                    passwordInput.getText().toString(),
                    nameInput.getText().toString(),
                    yearInput.getText().toString(),
                    courseInput.getText().toString(),
                    mobileInput.getText().toString(),
                    emailInput.getText().toString(),
                    date
            );



        } else {
            Toast.makeText(context, "Incomplete credentials", Toast.LENGTH_SHORT).show();
        }

    }

    public boolean isFormComplete() {
        if (idInput.getText().toString().equals("")) {
            return false;
        }
        if (nameInput.getText().toString().equals("")) {
            return false;
        }
        if (passwordInput.getText().toString().equals("")) {
            return false;
        }
        if (yearInput.getText().toString().equals("")) {
            return false;
        }
        if (courseInput.getText().toString().equals("")) {
            return false;
        }
        if (mobileInput.getText().toString().equals("")) {
            return false;
        }
        if (emailInput.getText().toString().equals("") && !emailInput.getText().toString().contains("@")) {
            return false;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void back(View view){
        onBackPressed();
    }

}
