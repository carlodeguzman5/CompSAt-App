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
import android.widget.TextView;
import android.widget.Toast;
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
            RestClient accountRestClient = new RestClient(url);

            accountRestClient.addParam("username", params[0]);
            accountRestClient.addParam("password", params[1]);
            accountRestClient.addParam("name", params[2]);
            accountRestClient.addParam("year", params[3]);
            accountRestClient.addParam("course", params[4]);
            accountRestClient.addParam("mobile", params[5]);
            accountRestClient.addParam("email", params[6]);
            accountRestClient.addParam("birthdate", params[7]);

            accountRestClient.execute();

            return String.valueOf(accountRestClient.getStatusCode());
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            switch (s) {
                case "200":
                    Toast.makeText(context, "Account creation successful", Toast.LENGTH_LONG).show();
                    finish();
                    break;
                case "404":
                    Toast.makeText(context, "ID number is already registered", Toast.LENGTH_LONG).show();
                    break;
                case "500":
                    Toast.makeText(context, "Internal Error", Toast.LENGTH_LONG).show();
                    break;
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
