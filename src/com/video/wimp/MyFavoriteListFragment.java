package com.video.wimp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.google.ads.AdView;
import com.video.wimp.adapter.VideoListAdapter;
import com.video.wimp.ads.Ads;
import com.video.wimp.util.DatabaseHandler;
import com.video.wimp.util.Youtube;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyFavoriteListFragment extends SherlockFragment {

    OnVideoSelectedListener mCallback;


    // create object of views
    ListView list;
    ProgressBar prgLoading;
    Button btnRefresh;
    AdView ads;
    TextView emptyFavTextView;

    // create variable to get position, connection status, resources, and channel username
    int position;
    boolean isConnect = true;

    // create object of custom adapter
    VideoListAdapter vla;

    // create arraylist variables
    ArrayList<HashMap<String, String>> videoItems;
    ProgressDialog pDialog;

    // flag for current page
    static final String KEY_ID = "yid";
    static final String KEY_TITLE = "title";
    static final String KEY_THUMBNAIL = "thumbnail";

    // create interface listener
    public interface OnVideoSelectedListener{
        public void onVideoSelected(String ID);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.list) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;

            String[] menuItems = getResources().getStringArray(R.array.remove_favorite);
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        DatabaseHandler db = new DatabaseHandler(getActivity());
        HashMap<String, String> selectedVideoInfo = videoItems.get(info.position);
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
        }else if(item.getTitle().equals("Remove Favorite")) {
            // Add to sqlite
            //db.deleteAll();


            // Deleting Contacts
            //Log.d("Insert: ", "Inserting ..");
            db.deleteYoutube(new Youtube(yid, title, null));
            Toast.makeText(getActivity(), getString(R.string.favorite_removed), Toast.LENGTH_LONG).show();
            videoItems = new ArrayList<HashMap<String, String>>();
            new loadFavoriteListView().execute();
        }
        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View v = inflater.inflate(R.layout.fav_fragment_list, container, false);

        // connect view objects and view id on xml
        list = (ListView) v.findViewById(R.id.list);
        prgLoading = (ProgressBar) v.findViewById(R.id.prgLoading);
        btnRefresh = (Button) v.findViewById(R.id.btnRefresh);
        emptyFavTextView = (TextView) v.findViewById(R.id.emptyFavList);
        ads = (AdView) v.findViewById(R.id.ads);

        videoItems = new ArrayList<HashMap<String, String>>();

        // get value that passed from previous page
        Bundle bundle = this.getArguments();
        position = bundle.getInt("position", 0);

        Ads.loadAds(ads);

        new loadFavoriteListView().execute();


        // listener to handle list when clicked
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
                // TODO Auto-generated method stub
                HashMap<String, String> item = new HashMap<String, String>();
                item = videoItems.get(position);

                mCallback.onVideoSelected(item.get("yid"));

                list.setItemChecked(position, true);
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


    // load favorite videos
    private class loadFavoriteListView extends AsyncTask<Void, Void, Void> {

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
            DatabaseHandler db = new DatabaseHandler(getActivity());

            List<Youtube> youtubeList = db.getAllYoutubesOrderByCreateDate();
            for(Youtube youtube : youtubeList) {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put(KEY_ID, youtube.getYid());
                map.put(KEY_TITLE, youtube.getTitle());
                map.put(KEY_THUMBNAIL, "https://i1.ytimg.com/vi/"+youtube.getYid()+"/mqdefault.jpg");
                videoItems.add(map);
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
                    vla = new VideoListAdapter(getActivity(), videoItems);
                    list.setAdapter(vla);

                    //Here is the textView to show if the list is empty
                    if(videoItems.size() > 0) {
                        emptyFavTextView.setVisibility(View.GONE);
                    } else {
                        emptyFavTextView.setVisibility(View.VISIBLE);
                    }

                }else{
                    btnRefresh.setVisibility(View.VISIBLE);
                    Toast.makeText(getActivity(), getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
                }
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