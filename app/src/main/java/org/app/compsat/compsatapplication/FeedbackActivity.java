package org.app.compsat.compsatapplication;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.json.JSONException;

public class FeedbackActivity extends Activity {

    private Context context = this;
    private EditText feedbackText, subjectText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/FuturaLT.ttf");
        feedbackText = (EditText) findViewById(R.id.feedbackText);
        feedbackText.setTypeface(tf);

        subjectText = (EditText) findViewById(R.id.subjectText);
        subjectText.setTypeface(tf);

        Button button = (Button) findViewById(R.id.feedbackBtn);
        button.setTypeface(tf);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_feedback, menu);
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

    public void submitFeedback(View view) throws JSONException {
        if(String.valueOf(subjectText.getText()).trim().equals("") ||
                String.valueOf(feedbackText.getText()).trim().equals("") ){
            Toast.makeText(this, "Oops, you forgot something", Toast.LENGTH_SHORT).show();
        }
        else{
            new ExecutePost("http://app.compsat.org/index.php/Feedback_controller/feedback/format/json", String.valueOf(subjectText.getText()), String.valueOf(feedbackText.getText()) ).execute();
        }

    }

    public class ExecutePost extends AsyncTask<String, String, String> {

        String url, feedback, subject;
        public ExecutePost(String url, String subject, String feedback){
            this.url = url;
            this.feedback = feedback;
            this.subject = subject;
        }

        @Override
        protected String doInBackground(String... params) {
            SharedPreferences prefs = getSharedPreferences(
                    "org.app.compsat.compsatapplication", Context.MODE_PRIVATE);

            RestClient feedbackRestClient = new RestClient(url);
            feedbackRestClient.addParam("subject", subject);
            feedbackRestClient.addParam("feedback", feedback);
            feedbackRestClient.addParam("name", prefs.getString("name", ""));
            feedbackRestClient.execute();

            return String.valueOf(feedbackRestClient.getStatusCode());
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            switch (s){
                case "200":
                    Toast.makeText(context, "Feedback has been submitted!", Toast.LENGTH_LONG).show();

                    feedbackText.setText("");
                    subjectText.setText("");
                    break;

                case "500":
                    Toast.makeText(context, "Internal Error", Toast.LENGTH_LONG).show();
                    break;

                default:
                    Log.d("STATUS", s);
                    break;

            }


        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }
}
