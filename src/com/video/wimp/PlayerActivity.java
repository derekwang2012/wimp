package com.video.wimp;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;

public class PlayerActivity extends YouTubeBaseActivity implements
	YouTubePlayer.OnInitializedListener{

	// create string variables
	String YOUTUBE_APIKEY;
	String ID;

	// create object of view
	YouTubePlayerView youTubePlayerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        // connect view object and view id on xml
        youTubePlayerView = (YouTubePlayerView)findViewById(R.id.youtubeplayerview);


        // get YOUTUBE APIKEY
        YOUTUBE_APIKEY = getString(R.string.youtube_apikey);

        // get video id from previous page
        Intent i = getIntent();
        ID = i.getStringExtra("id");
        youTubePlayerView.initialize(YOUTUBE_APIKEY, this);

    }

    @Override
	 public void onInitializationFailure(Provider provider,
	   YouTubeInitializationResult result) {

    	if(result == YouTubeInitializationResult.DEVELOPER_KEY_INVALID)
    	     Toast.makeText(this, "Initialization Fail- key invalid", Toast.LENGTH_SHORT).show();
    	else if(result == YouTubeInitializationResult.NETWORK_ERROR)
    		Toast.makeText(this, getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
    	else if(result == YouTubeInitializationResult.SERVICE_INVALID)
    	     updateYoutubeDialog(
    	    		 getString(R.string.update_youtube_app),
    	    		 getString(R.string.update));
    	else if(result == YouTubeInitializationResult.SERVICE_MISSING)
    	     updateYoutubeDialog(
    	    		 getString(R.string.no_youtube_app),
    	    		 getString(R.string.install));

    }

    void updateYoutubeDialog(String message, String button){

    	//if Youtube app is not available show alert dialog
        Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setCancelable(true);
        builder.setPositiveButton(button, new DialogInterface.OnClickListener() {

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

	 @Override
	 public void onInitializationSuccess(Provider provider, YouTubePlayer player,
	   boolean wasRestored) {
		 if (!wasRestored) {
			 player.loadVideo(ID);
	     }
	 }

}
