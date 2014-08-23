package com.video.wimp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class MyFavoriteActivity extends SherlockFragmentActivity
        implements ActionBar.OnNavigationListener, MyFavoriteListFragment.OnVideoSelectedListener{

    // create object of ActionBar and VideoListFragment
    MyFavoriteListFragment videoListFrag;
    ActionBar actionbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        videoListFrag = new MyFavoriteListFragment();
        Bundle bundle = new Bundle();
        videoListFrag.setArguments(bundle);

        // call video list fragment with new data
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, videoListFrag, "MY_FAVORITE_LIST_FRAGMENT")
                .commit();
    }

    // create option menu
    public boolean onCreateOptionsMenu(Menu menu) {
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
                iShare.putExtra(Intent.EXTRA_TEXT, getString(R.string.message)+" "+getString(R.string.gplay_web_url));
                startActivity(Intent.createChooser(iShare, getString(R.string.share_via)));
                return true;
            case R.id.menuRate:
                // open google play app to ask user to rate & review this app
                Intent iRate = new Intent(Intent.ACTION_VIEW);
                iRate.setData(Uri.parse(getString(R.string.gplay_url)));
                startActivity(iRate);
                return true;
            /*case R.id.menuSetting:
                // open setting page
                Intent iSetting = new Intent(this, UserSettingActivity.class);
                startActivity(iSetting);
                return true;*/
            case android.R.id.home:
                // back to previous page
                finish();
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
        SharedPreferences setting = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean nativeYoutubeFlag = setting.getBoolean("pref_play_youtube", false);

        if(isAppInstalled("com.google.android.youtube")) {
            /*Intent intent = YouTubeStandalonePlayer.createVideoIntent(this, getString(R.string.youtube_apikey), ID, 0, true, true);
            startActivity(intent);*/
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:"+ID));
            intent.putExtra("VIDEO_ID", ID);
            intent.putExtra("force_fullscreen",false);
            startActivity(intent);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.no_youtube_app));
            builder.setCancelable(true);
            builder.setPositiveButton("Install", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.youtube"));
                    startActivity(intent);

                    //Finish the activity so they can't circumvent the check
                    finish();
                }

            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    protected boolean isAppInstalled(String packageName) {
        Intent mIntent = getPackageManager().getLaunchIntentForPackage(packageName);
        if (mIntent != null) {
            return true;
        }
        else {
            return false;
        }
    }

}
