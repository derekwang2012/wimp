package com.video.wimp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;

public class HomeActivity extends SherlockFragmentActivity
        implements ActionBar.OnNavigationListener, VideoListFragment.OnVideoSelectedListener {

    // create object of ActionBar and VideoListFragment
    VideoListFragment videoListFrag;
    ProgressDialog pDialog;
    private static final int menuItemIdRefresh = 10;
    private static final int menuItemIdRandom = 20;
    private static final int menuItemIdFav = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        videoListFrag = new VideoListFragment();
        Bundle bundle = new Bundle();
        videoListFrag.setArguments(bundle);

        // call video list fragment with new data
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, videoListFrag, "VIDEO_LIST_FRAGMENT")
                .commit();
    }

    // create option menu
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem refreshItem = menu.add(0, menuItemIdRefresh, 0,
                getString(R.string.refresh)).setShowAsActionFlags(
                MenuItem.SHOW_AS_ACTION_ALWAYS);
        refreshItem.setIcon(R.drawable.ic_action_refresh);
        MenuItem favItem = menu.add(0, menuItemIdFav, 0,
                getString(R.string.my_favorite)).setShowAsActionFlags(
                MenuItem.SHOW_AS_ACTION_ALWAYS);
        favItem.setIcon(R.drawable.ic_action_favorite);
        MenuItem randomItem = menu.add(0, menuItemIdRandom, 0,
                getString(R.string.random)).setShowAsActionFlags(
                MenuItem.SHOW_AS_ACTION_ALWAYS);
        randomItem.setIcon(R.drawable.ic_action_shuffle);

        getSupportMenuInflater().inflate(R.menu.favorite, menu);
        return true;
    }

    // listener for option menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuShare:
                // share google play link of this app to other app such as email, facebook, etc
                Intent iShare = new Intent(Intent.ACTION_SEND);
                iShare.setType("text/plain");
                iShare.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.subject));
                iShare.putExtra(Intent.EXTRA_TEXT, getString(R.string.message) + " " + getString(R.string.gplay_web_url));
                startActivity(Intent.createChooser(iShare, getString(R.string.share_via)));
                return true;
            case R.id.menuRate:
                // open google play app to ask user to rate & review this app
                Intent iRate = new Intent(Intent.ACTION_VIEW);
                iRate.setData(Uri.parse(getString(R.string.gplay_url)));
                startActivity(iRate);
                return true;
            case menuItemIdFav:
                // open favorite app page
                Intent iMyFavorite = new Intent(this, MyFavoriteActivity.class);
                startActivity(iMyFavorite);
                return true;
            case menuItemIdRefresh:
                //Refresh the list
                videoListFrag.btnRefresh.performClick();
                return true;
            case menuItemIdRandom:
                new loadRandomVideoView().execute();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        return true;
    }

    @Override
    public void onVideoSelected(String ID) {
        // TODO Auto-generated method stub

        // call player page to play selected video
        Intent i = new Intent(this, PlayerActivity.class);
        i.putExtra("id", ID);
        startActivity(i);

    }

    // load random videos
    private class loadRandomVideoView extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
        }

        protected Void doInBackground(Void... unused) {
            try {

                HttpClient client = new DefaultHttpClient();
                HttpConnectionParams.setConnectionTimeout(client.getParams(), 15000);
                HttpConnectionParams.setSoTimeout(client.getParams(), 15000);

                // Perform a GET request to YouTube for a JSON list of all the videos by a specific user
                HttpUriRequest request = new HttpGet("http://trucn.com/thisisfive.com/showWimpRandom.php?format=json");
                // Get the response that YouTube sends back
                HttpResponse response = client.execute(request);
                // Convert this response into an inputstream for the parser to use
                InputStream atomInputStream = response.getEntity().getContent();

                BufferedReader in = new BufferedReader(new InputStreamReader(atomInputStream));

                String line;
                String str = "";
                while ((line = in.readLine()) != null) {
                    str += line;
                }

                Intent i = new Intent(getApplicationContext(), PlayerActivity.class);
                i.putExtra("id", str);
                startActivity(i);

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return (null);
        }

        protected void onPostExecute(Void unused) {
            // closing progress dialog
            try {
                pDialog.dismiss();
                pDialog = null;
            } catch (Exception e) {
                // nothing
            }
        }
    }

}
