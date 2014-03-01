package com.video.wimp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.google.ads.AdView;
import com.video.wimp.adapter.VideoListAdapter;
import com.video.wimp.ads.Ads;
import com.video.wimp.util.DatabaseHandler;
import com.video.wimp.util.Youtube;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;

public class VideoListFragment extends SherlockFragment {
	
	OnVideoSelectedListener mCallback;
	
	// create string variables
    private String YOUTUBE_API = "http://trucn.com/thisisfive.com/showWimp.php?format=json";
	
	// create object of views
	ListView list;
	ProgressBar prgLoading;
	Button btnRefresh;
	Button btnLoadMore;
	AdView ads;
	
	// create variable to get position, connection status, resources, and channel username
	int position;
	boolean isConnect = true;

	// create object of custom adapter
	VideoListAdapter vla;
	
	// create arraylist variables
    ArrayList<HashMap<String, String>> menuItems;
    ProgressDialog pDialog;
    
    // flag for current page
    int current_page = 0;
    int previous_page;

	static final String KEY_ID = "yid";
	static final String KEY_TITLE = "title";
	static final String KEY_THUMBNAIL = "thumbnail";
	//static final String KEY_DURATION = "duration";
    //static final String KEY_DATE_CATEGORY = "date_category";
	
	// create interface listener
	public interface OnVideoSelectedListener{
		public void onVideoSelected(String ID);
	}

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.list) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;

            String[] menuItems = getResources().getStringArray(R.array.add_to_favorite);
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        DatabaseHandler db = new DatabaseHandler(getActivity());

        HashMap<String, String> selectedVideoInfo = menuItems.get(info.position);
        String yid = selectedVideoInfo.get("yid");
        String title = selectedVideoInfo.get("title");
        String youtubeLink = "http://www.youtube.com/watch?v=" + yid;
        if(item.getTitle().equals("Share")) {
            // Share single video
            Intent iShare = new Intent(Intent.ACTION_SEND);
            iShare.setType("text/plain");
            iShare.putExtra(Intent.EXTRA_SUBJECT, title);
            iShare.putExtra(Intent.EXTRA_TEXT, youtubeLink);
            startActivity(Intent.createChooser(iShare, getString(R.string.share_via)));
        }else if(item.getTitle().equals("Add to Favorite")) {
            // Add to sqlite
            //db.deleteAll();


            // Inserting Contacts
            //Log.d("Insert: ", "Inserting ..");
            db.addYoutube(new Youtube(yid, title, null));
            Toast.makeText(getActivity(), getString(R.string.favorite_added), Toast.LENGTH_LONG).show();
        }


        return true;
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.fragment_list, container, false);

		// connect view objects and view id on xml
		list = (ListView) v.findViewById(R.id.list);
		prgLoading = (ProgressBar) v.findViewById(R.id.prgLoading);
		btnRefresh = (Button) v.findViewById(R.id.btnRefresh);
		ads = (AdView) v.findViewById(R.id.ads);
		
		menuItems = new ArrayList<HashMap<String, String>>();
		
		// get value that passed from previous page
		Bundle bundle = this.getArguments();
		position = bundle.getInt("position", 0);

		Ads.loadAds(ads);

        YOUTUBE_API = "http://trucn.com/thisisfive.com/showWimp.php?format=json";
		// create LoadMore button
        btnLoadMore = new Button(getActivity());
        btnLoadMore.setBackgroundResource(R.drawable.btn_default_holo_light);
        btnLoadMore.setText(getString(R.string.load_more));
 
        // adding load more button to lisview at bottom
        list.addFooterView(btnLoadMore);
        
		new loadFirstListView().execute();
 
        // listener to handle load more buttton when clicked
        btnLoadMore.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View arg0) {
                // Starting a new async task
				isConnect = true;
                new loadMoreListView().execute();
            }
        });
 
		
		
		// listener to handle list when clicked
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				HashMap<String, String> item = new HashMap<String, String>();
		        item = menuItems.get(position);

				mCallback.onVideoSelected(item.get("yid"));
				
				list.setItemChecked(position, true);
			}
		});
		
		// listener to handle refresh button when clicked
		btnRefresh.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isConnect = true;
                menuItems = new ArrayList<HashMap<String, String>>();
                current_page = 0;
				new loadFirstListView().execute();
			}
		});
        registerForContextMenu(list);
		        
		return v;
	}
	
	
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        try {
            mCallback = (OnVideoSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }
	
	
	// load first 10 videos
	private class loadFirstListView extends AsyncTask<Void, Void, Void> {
		 
        @Override
        protected void onPreExecute() {
            // Showing progress dialog before sending http request
            pDialog = new ProgressDialog(
                    getActivity());
            pDialog.setMessage("Please wait..");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();
        }
 
        protected Void doInBackground(Void... unused) {
        	try {

    	        HttpClient client = new DefaultHttpClient();
    	        HttpConnectionParams.setConnectionTimeout(client.getParams(), 15000);
    			HttpConnectionParams.setSoTimeout(client.getParams(), 15000);
    			
    	        // Perform a GET request to YouTube for a JSON list of all the videos by a specific user
    			HttpUriRequest request = new HttpGet("http://trucn.com/thisisfive.com/showWimp.php?format=json");
    			// Get the response that YouTube sends back
    			HttpResponse response = client.execute(request);
    			// Convert this response into an inputstream for the parser to use
    			InputStream atomInputStream = response.getEntity().getContent();

    			
    			 BufferedReader in = new BufferedReader(new InputStreamReader(atomInputStream));
    		        
    	        //BufferedReader in = new BufferedReader(new InputStreamReader(tc.getInputStream()));
    	        String line;
    	        String str = "";
    	        while ((line = in.readLine()) != null){
    	        	str += line;
    	        }
    			
                JSONObject json = new JSONObject(str);
                JSONArray items = json.getJSONArray("wimps");


                for (int i = 0; i < items.length(); i++) {

                    HashMap<String, String> map = new HashMap<String, String>();
                    JSONObject youtubeObject = items.getJSONObject(i);

                    map.put(KEY_ID, youtubeObject.getString("yid"));
                    map.put(KEY_TITLE, youtubeObject.getString("title").replace("&amp;", "&").replace("&quot;", "\""));
                    map.put(KEY_THUMBNAIL, "https://i1.ytimg.com/vi/"+youtubeObject.getString("yid")+"/mqdefault.jpg");

                    /*String duration = secondToTime(youtubeObject.getInt("duration"));
                    map.put(KEY_DURATION, duration);
                    map.put(KEY_DATE_CATEGORY, youtubeObject.getString("date_category"));*/

                    // adding HashList to ArrayList
                    menuItems.add(map);
                }
    				
    		} catch (MalformedURLException e) {
    		    // TODO Auto-generated catch block
    		    e.printStackTrace();
    		} catch (IOException e) {
    		    // TODO Auto-generated catch block
    			isConnect = false;
    		    e.printStackTrace();
    		} catch (JSONException e) {
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


        	if(isAdded()){
	            if(isConnect){
	            	btnRefresh.setVisibility(View.GONE);
	            	// Getting adapter
	            	vla = new VideoListAdapter(getActivity(), menuItems);
	            	list.setAdapter(vla);
	            	
	            }else{
	            	btnRefresh.setVisibility(View.VISIBLE);
	            	Toast.makeText(getActivity(), getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
	            }
        	}
        }
    }
	
	// load more videos
    private class loadMoreListView extends AsyncTask<Void, Void, Void> {
 
        @Override
        protected void onPreExecute() {
            // Showing progress dialog before sending http request
            pDialog = new ProgressDialog(
                    getActivity());
            pDialog.setMessage("Please wait..");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();
        }


        protected Void doInBackground(Void... unused) {

			// store previous value of current page
			previous_page = current_page;
            // increment current page
            current_page += 15;

            //YOUTUBE_API = "http://trucn.com/thisisfive.com/showWimp.php?format=json&offset=" + current_page + "&search=" + params[0];
            YOUTUBE_API = "http://trucn.com/thisisfive.com/showWimp.php?format=json&offset=" + current_page;

            try {
    	        
    	        HttpClient client = new DefaultHttpClient();
    	        HttpConnectionParams.setConnectionTimeout(client.getParams(), 15000);
    			HttpConnectionParams.setSoTimeout(client.getParams(), 15000);
    			
    	        // Perform a GET request to YouTube for a JSON list of all the videos by a specific user
    			HttpUriRequest request = new HttpGet(YOUTUBE_API);
    			// Get the response that YouTube sends back
    			HttpResponse response = client.execute(request);
    			// Convert this response into an inputstream for the parser to use
    			InputStream atomInputStream = response.getEntity().getContent();

    			BufferedReader in = new BufferedReader(new InputStreamReader(atomInputStream));

    	        String line;
    	        String str = "";
    	        while ((line = in.readLine()) != null){
    	        	str += line;
    	        }
    			
                JSONObject json = new JSONObject(str);
                JSONArray items = json.getJSONArray("wimps");

                for (int i = 0; i < items.length(); i++) {

                    HashMap<String, String> map = new HashMap<String, String>();
                    JSONObject youtubeObject = items.getJSONObject(i);

                    map.put(KEY_ID, youtubeObject.getString("yid"));
                    map.put(KEY_TITLE, youtubeObject.getString("title").replace("&amp;", "&").replace("&quot;", "\""));
                    map.put(KEY_THUMBNAIL, "https://i1.ytimg.com/vi/"+youtubeObject.getString("yid")+"/mqdefault.jpg");


                    // adding HashList to ArrayList
                    menuItems.add(map);
                }
    		} catch (MalformedURLException e) {
    		    // TODO Auto-generated catch block
    		    e.printStackTrace();
    		} catch (IOException e) {
    		    // TODO Auto-generated catch block
    			isConnect = false;
    		    e.printStackTrace();
    		} catch (JSONException e) {
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
            
            if(isConnect){
            	// get listview current position - used to maintain scroll position
	            int currentPosition = list.getFirstVisiblePosition();
	

            	btnRefresh.setVisibility(View.GONE);
	            // Appending new data to menuItems ArrayList
	            vla = new VideoListAdapter(
	                    getActivity(),
	                    menuItems);
	            list.setAdapter(vla);
	            // Setting new scroll position
	            list.setSelectionFromTop(currentPosition + 1, 0);

            }else{
            	if(menuItems != null){
            		current_page = previous_page;
                	btnRefresh.setVisibility(View.GONE);
            	}else{
            		btnRefresh.setVisibility(View.VISIBLE);
            	}
            	Toast.makeText(getActivity(), getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
            }
        }
    }

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
        if(vla != null) {
		    vla.imageLoader.clearCache();
        }

		Log.d("clear cache", "clear cache");
	}
}
