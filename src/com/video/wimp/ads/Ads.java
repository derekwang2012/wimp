package com.video.wimp.ads;

import com.google.ads.AdRequest;
import com.google.ads.AdView;
import com.google.ads.InterstitialAd;

public class Ads {
	public static void loadAds(AdView ads){
		
		/**
		 * beginning of admob testing code
		 * 
		 * this code is for testing admob only, 
		 * when you publish the app remove the following code
		 */
		/*AdRequest adRequest = new AdRequest();
		
		// use this code below to test admob on android device
		adRequest.addTestDevice("DEVICE_ID");
		ads.loadAd(adRequest);*/
		/**
		 * end of admob testing code
		 */
		
		/**
		* code below is used to publish admob when the app launched.
		* remove the comment tag below and delete block of code 
		* that used for testing admob.
		*/

		ads.loadAd(new AdRequest());
	}
	
	public static void loadInterstitialAd(InterstitialAd interstitial){
		
		/**
		 * beginning of admob testing code
		 * 
		 * this code is for testing admob only, 
		 * when you publish the app remove the following code
		 */
		/*AdRequest adRequest = new AdRequest();
		
		// use this code below to test admob on android device
		adRequest.addTestDevice("DEVICE_ID");
		interstitial.loadAd(adRequest);*/
		/**
		 * end of admob testing code
		 */
		
		/**
		* code below is used to publish admob when the app launched.
		* remove the comment tag below and delete block of code 
		* that used for testing admob.
		*/

		interstitial.loadAd(new AdRequest());
	}
}
