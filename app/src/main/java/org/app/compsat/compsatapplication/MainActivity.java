package org.app.compsat.compsatapplication;

import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class MainActivity extends TabActivity {

    private final String PROJECT_NUMBER = "1058631919049";
    private SharedPreferences prefs;
    private Typeface tf_opensans_regular, tf_opensans_bold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabhost);

        Resources resources = getResources();
        TabHost tabHost = getTabHost();

        /*ImageView menu = (ImageView)findViewById(R.id.menu_button);
        menu.setImageDrawable(resources.getDrawable(R.drawable.ic_menu));*/



        // Android tab
        Intent intentCalendar = new Intent().setClass(this, CalendarActivity.class);
        TabSpec tabSpecCalendar = tabHost
                .newTabSpec("Calendar")
                .setIndicator("", resources.getDrawable(R.drawable.ic_event))
                .setContent(intentCalendar);


        Intent intentWebsite = new Intent().setClass(this, LinksActivity.class);
        TabSpec tabSpecWebsite = tabHost
                .newTabSpec("Links")
                .setIndicator("", resources.getDrawable(R.drawable.ic_website))
                .setContent(intentWebsite);

        Intent intentNews = new Intent().setClass(this, NewsfeedActivity.class);
        TabSpec tabSpecNews = tabHost
                .newTabSpec("Newsfeed")
                .setIndicator("", resources.getDrawable(R.drawable.ic_stream))
                .setContent(intentNews);

        Intent intentFeedback = new Intent().setClass(this, FeedbackActivity.class);
        TabSpec tabSpecFeedback = tabHost
                .newTabSpec("Feedback")
                .setIndicator("", resources.getDrawable(R.drawable.ic_feedback))
                .setContent(intentFeedback);

        tabHost.addTab(tabSpecNews);
        tabHost.addTab(tabSpecCalendar);
        tabHost.addTab(tabSpecWebsite);
        tabHost.addTab(tabSpecFeedback);



        final TextView toolbar;
        toolbar = (TextView) findViewById(R.id.toolbar_title);
        tf_opensans_regular = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Regular.ttf");
        tf_opensans_bold = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Bold.ttf");
        toolbar.setTypeface(tf_opensans_bold);


        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
            if ("Newsfeed".equals(tabId)) {
                toolbar.setText("Newsfeed");
            }
            else if ("Calendar".equals(tabId)) {
                toolbar.setText("Calendar");
            }
            else if ("Links".equals(tabId)) {
                toolbar.setText("Websites");
            }
            else if ("Feedback".equals(tabId)) {
                toolbar.setText("Feedback");
            }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo contextMenuInfo = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        switch(item.getItemId()) {
            case R.id.logout:
                logout();
                break;
        }
        return super.onContextItemSelected(item);
    }

    public void logout(){
        Intent loginScreen=new Intent(this, LoginActivity.class);
        loginScreen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        prefs = this.getSharedPreferences("org.app.compsat.compsatapplication", MODE_PRIVATE);
        prefs.edit().clear().apply();
        startActivity(loginScreen);
        this.finish();
    }

    public void inflateMenu(View view){
        openOptionsMenu();
    }

}
