package com.video.wimp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class HomeActivity extends SherlockFragmentActivity 
	implements ActionBar.OnNavigationListener, VideoListFragment.OnVideoSelectedListener{

	// create object of ActionBar and VideoListFragment
	VideoListFragment videoListFrag;
    private static final int menuItemIdRefresh = 10;
    //private static final int menuItemIdShare = 20;
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
        /*MenuItem shareItem = menu.add(0, menuItemIdShare, 0,
                getString(R.string.share_it)).setShowAsActionFlags(
                MenuItem.SHOW_AS_ACTION_ALWAYS);
        shareItem.setIcon(R.drawable.ic_action_share);*/
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
            case menuItemIdFav:
                // open About app page
                Intent iMyFavorite = new Intent(this, MyFavoriteActivity.class);
                startActivity(iMyFavorite);
                return true;
            case menuItemIdRefresh:
                //Refresh the list
                videoListFrag.btnRefresh.performClick();
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

}
