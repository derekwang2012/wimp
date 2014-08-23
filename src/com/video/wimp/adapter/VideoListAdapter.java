package com.video.wimp.adapter;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.video.wimp.R;
import com.video.wimp.util.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;


public class VideoListAdapter extends BaseAdapter {
		
		private Activity activity;
		private ArrayList<HashMap<String, String>> data;
		private static LayoutInflater inflater=null;
		public ImageLoader imageLoader;
		
		
		public VideoListAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
	        activity = a;
	        data=d;
			imageLoader = new ImageLoader(a.getApplicationContext());
	        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        
		}
		
		
		public int getCount() {
			// TODO Auto-generated method stub
			return data.size();
		}

		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View vi=convertView;

			if(convertView == null)
				vi = inflater.inflate(R.layout.row_video_list, null);
			
				// connect views object and views id on xml
				ImageView imgThumbnail = (ImageView) vi.findViewById(R.id.imgThumbnail);
				TextView txtTitle = (TextView) vi.findViewById(R.id.txtTitle);
                TextView txtCreateDate = (TextView) vi.findViewById(R.id.txtCreateDate);

				
				HashMap<String, String> item = data.get(position);

				// set data to textview and imageview
		        txtTitle.setText(item.get("title"));
                txtCreateDate.setText(item.get("create_date"));
			
		        imageLoader.DisplayImage(item.get("thumbnail"), imgThumbnail);
			
			return vi;
		}
		
	}