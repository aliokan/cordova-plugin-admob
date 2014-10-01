package com.google.cordova.plugin;

import java.util.Iterator;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.LinearLayoutSoftKeyboardDetect;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.ads.*;

/**
 * This class represents the native implementation for the AdMob Cordova plugin.
 * This plugin can be used to request AdMob ads natively via the Google AdMob
 * SDK. The Google AdMob SDK is a dependency for this plugin.
 */
public class AdMobPlugin extends CordovaPlugin {
	/** The adView to display to the user. */
	private AdView adView;
//	private DfpInterstitialAd intertitial;

	/** Whether or not the ad should be positioned at top or bottom of screen. */
	private boolean positionAtTop;

	/** Common tag used for logging statements. */
	private static final String LOGTAG = "AdMobPlugin";

	/** Cordova Actions. */
	public static final String ACTION_CREATE_BANNER_VIEW = "createBannerView";
	public static final String ACTION_CREATE_INTERSTITIAL_VIEW = "createInterstitialView";
	public static final String ACTION_REQUEST_AD = "requestAd";
	public static final String KILL_AD = "killAd";
	public static final String HIDE_AD = "hideAd";
	public static final String SHOW_AD = "showAd";


	/**
	 * This is the main method for the AdMob plugin. All API calls go through
	 * here. This method determines the action, and executes the appropriate
	 * call.
	 * 
	 * @param action
	 *            The action that the plugin should execute.
	 * @param inputs
	 *            The input parameters for the action.
	 * @param callbackId
	 *            The callback ID. This is currently unused.
	 * @return A PluginResult representing the result of the provided action. A
	 *         status of INVALID_ACTION is returned if the action is not
	 *         recognized.
	 */
	@Override
	public boolean execute(String action, JSONArray inputs,
			CallbackContext callbackContext) throws JSONException {
		
		
		
		if (ACTION_CREATE_BANNER_VIEW.equals(action)) {
			executeCreateBannerView(inputs, callbackContext);
			return true;
		} else if (ACTION_CREATE_INTERSTITIAL_VIEW.equals(action)) {
		//	executeCreateInterstitialView(inputs, callbackContext);
			return true;
		} else if (ACTION_REQUEST_AD.equals(action)) {
			executeRequestAd(inputs, callbackContext);
			return true;
		} else if (KILL_AD.equals(action)) {
			executeKillAd(callbackContext);
			return true;
		} else if (HIDE_AD.equals(action)) {
			hideAd(callbackContext);
			return true;
		} else if (SHOW_AD.equals(action)) {
			showAd(callbackContext);
			return true;
		} else {
			Log.d(LOGTAG, String.format("Invalid action passed: %s", action));
			callbackContext.error("Invalid Action");
		}
		return false;
	}

	/**
	 * Parses the create banner view input parameters and runs the create banner
	 * view action on the UI thread. If this request is successful, the
	 * developer should make the requestAd call to request an ad for the banner.
	 * 
	 * @param inputs
	 *            The JSONArray representing input parameters. This function
	 *            expects the first object in the array to be a JSONObject with
	 *            the input parameters.
	 * @return A PluginResult representing whether or not the banner was created
	 *         successfully.
	 */
	private void executeCreateBannerView(JSONArray inputs, CallbackContext callbackContext) {
		String publisherId = "";
		String size = "";

		// Get the input data.
		try {
			JSONObject data = inputs.getJSONObject(0);
			publisherId = data.getString("publisherId");
			size = data.getString("adSize");
			this.positionAtTop = data.getBoolean("positionAtTop");
			Log.w(LOGTAG, "executeCreateBannerView OK");
			Log.w(LOGTAG, "size: " + size);
			Log.w(LOGTAG, "publisherId: " + publisherId);
			Log.w(LOGTAG, "positionAtTop: " + (this.positionAtTop ? "true" : "false"));
		} catch (JSONException exception) {
			Log.w(LOGTAG,
					String.format("Got JSON Exception: %s",
							exception.getMessage()));
			callbackContext.error(exception.getMessage());
		}
		AdSize adSize = adSizeFromSize(size);
		createBannerView(publisherId, adSize, callbackContext);
	}

	private synchronized void createBannerView(final String publisherId,
			final com.google.android.gms.ads.AdSize adSize, final CallbackContext callbackContext) {
		final CordovaInterface cordova = this.cordova;

		// Create the AdView on the UI thread.
		Log.w(LOGTAG, "createBannerView");
		Runnable runnable = new Runnable() {
			public void run() {
				Log.w(LOGTAG, "run");
				Log.w(LOGTAG, String.valueOf(webView));
				// Log.w(LOGTAG, "adSize::" + adSize); calling adSize.toString() with SmartBanner == crash
				if (adSize == null) {
					callbackContext
							.error("AdSize is null. Did you use an AdSize constant?");
					return;
				} else {
					adView = new AdView(cordova.getActivity());
					adView.setAdUnitId(publisherId);
					adView.setAdSize(adSize);
							
					adView.setAdListener(new AdListener() {						  
						public void onAdLoaded() {								
							webView.loadUrl("javascript:cordova.fireDocumentEvent('onReceiveAd');");
						}
						public void onAdFailedToLoad(int errorCode) {
							webView.loadUrl(String
									.format("javascript:cordova.fireDocumentEvent('onFailedToReceiveAd', { 'error': '%s' });",
											errorCode));
						}							
						public void onAdOpened() {
							webView.loadUrl("javascript:cordova.fireDocumentEvent('onPresentScreen');");
						}
						public void onAdClosed() {
							webView.loadUrl("javascript:cordova.fireDocumentEvent('onDismissScreen');");
						}
						public void onAdLeftApplication() {
							webView.loadUrl("javascript:cordova.fireDocumentEvent('onLeaveApplication');");
						}
					});
					
					
					LinearLayoutSoftKeyboardDetect parentView = (LinearLayoutSoftKeyboardDetect) webView
							.getParent();
					if (positionAtTop) {
						parentView.addView(adView, 0);
					} else {
						parentView.addView(adView);
					}
					// Notify the plugin.
					callbackContext.success();
				}
			}
		};
		this.cordova.getActivity().runOnUiThread(runnable);
	}
	
	/**
	 * Parses the create banner view input parameters and runs the create banner
	 * view action on the UI thread. If this request is successful, the
	 * developer should make the requestAd call to request an ad for the banner.
	 * 
	 * @param inputs
	 *            The JSONArray representing input parameters. This function
	 *            expects the first object in the array to be a JSONObject with
	 *            the input parameters.
	 * @return A PluginResult representing whether or not the banner was created
	 *         successfully.
	 */
//	private void executeCreateInterstitialView(JSONArray inputs, CallbackContext callbackContext) {
//		String publisherId = "";
//
//		// Get the input data.
//		try {
//			JSONObject data = inputs.getJSONObject(0);
//			publisherId = data.getString("publisherId");
//			Log.w(LOGTAG, "executeCreateInterstitialView OK");
//		} catch (JSONException exception) {
//			Log.w(LOGTAG, String.format("Got JSON Exception: %s", exception.getMessage()));
//			callbackContext.error(exception.getMessage());
//		}
//		createInterstitialView(publisherId, callbackContext);
//	}
//
//	private synchronized void createInterstitialView(final String publisherId, final CallbackContext callbackContext) {
//		final CordovaInterface cordova = this.cordova;
//
//		// Create the AdView on the UI thread.
//		Log.w(LOGTAG, "createInterstitialView");
//		Runnable runnable = new Runnable() {
//			public void run() {
//				intertitial = new DfpInterstitialAd(cordova.getActivity(), publisherId);
//				intertitial.setAdListener(new BannerListener());
//				// Notify the plugin.
//				callbackContext.success();
//			}
//		};
//		this.cordova.getActivity().runOnUiThread(runnable);
//	}

	/**
	 * Parses the request ad input parameters and runs the request ad action on
	 * the UI thread.
	 * 
	 * @param inputs
	 *            The JSONArray representing input parameters. This function
	 *            expects the first object in the array to be a JSONObject with
	 *            the input parameters.
	 * @return A PluginResult representing whether or not an ad was requested
	 *         succcessfully. Listen for onReceiveAd() and onFailedToReceiveAd()
	 *         callbacks to see if an ad was successfully retrieved.
	 */
	private void executeRequestAd(JSONArray inputs,
			CallbackContext callbackContext) {
		boolean isTesting = false;
		JSONObject inputExtras = null;
				
		// Get the input data.
		try {
			JSONObject data = inputs.getJSONObject(0);
			isTesting = data.getBoolean("isTesting");
			inputExtras = data.getJSONObject("extras");
			Log.w(LOGTAG, "executeRequestAd OK");
			// callbackContext.success();
			// return true;
		} catch (JSONException exception) {
			Log.w(LOGTAG,
					String.format("Got JSON Exception: %s",
							exception.getMessage()));
			callbackContext.error(exception.getMessage());
			return;
		}

		// Request an ad on the UI thread.
		if (adView != null) {
			if(!adView.isEnabled()) this.showAd(callbackContext);
			requestAd(isTesting, inputExtras, callbackContext);
		
//		} else if (intertitial != null) {
//			requestIntertitial(isTesting, inputExtras, callbackContext);
		} else {
			callbackContext
					.error("adView && intertitial are null. Did you call createBannerView?");
			return;
		}
	}
	
	private void hideAd(CallbackContext callbackContext) {	    
	    Runnable runnable = new Runnable() {			
	        @Override
	        public void run() {
	            adView.setEnabled(false);
	            adView.setVisibility(View.GONE);
	        }
	    };
	    this.cordova.getActivity().runOnUiThread(runnable);
	}
	
	private void showAd(CallbackContext callbackContext) {	    
	    Runnable runnable = new Runnable() {
	        @Override
	        public void run() {
	        	if(!adView.isEnabled()) {
	        		adView.setEnabled(true);
	        		adView.setVisibility(View.VISIBLE);
	        	}
	        }
	    };
	    this.cordova.getActivity().runOnUiThread(runnable);
	}

//	private synchronized void requestIntertitial(final boolean isTesting,
//			final JSONObject inputExtras, final CallbackContext callbackContext) {
//		Log.w(LOGTAG, "requestIntertitial");
//		// Create the AdView on the UI thread.
//		Runnable runnable = new Runnable() {
//			@SuppressWarnings("unchecked")
//			public void run() {
//				if (intertitial == null) {
//					callbackContext
//							.error("intertitial is null. Did you call createBannerView?");
//					return;
//				} else {
//					AdRequest request = new AdRequest.Builder().build();
//					if (isTesting) {
//						// This will request test ads on the emulator only. You
//						// can
//						// get your
//						// hashed device ID from LogCat when making a live
//						// request.
//						// Pass
//						// this hashed device ID to addTestDevice request test
//						// ads
//						// on your
//						// device.
//						//request.addTestDevice(AdRequest.TEST_EMULATOR);
//					}
//					AdMobAdapterExtras extras = new AdMobAdapterExtras();
//					Iterator<String> extrasIterator = inputExtras.keys();
//					boolean inputValid = true;
//					while (extrasIterator.hasNext()) {
//						String key = extrasIterator.next();
//						try {
//							extras.addExtra(key, inputExtras.get(key));
//						} catch (JSONException exception) {
//							Log.w(LOGTAG, String.format(
//									"Caught JSON Exception: %s",
//									exception.getMessage()));
//							callbackContext.error("Error grabbing extras");
//							inputValid = false;
//						}
//					}
//					if (inputValid) {
//						// extras.addExtra("cordova", 1);
//						// request.setNetworkExtras(extras);
//						intertitial.loadAd(request);
//						// Notify the plugin.
//						callbackContext.success();
//					}
//				}
//			}
//		};
//		this.cordova.getActivity().runOnUiThread(runnable);
//	}

	private synchronized void requestAd(final boolean isTesting,
			final JSONObject inputExtras, final CallbackContext callbackContext) {
		Log.w(LOGTAG, "requestAd");
		// Create the AdView on the UI thread.
		Runnable runnable = new Runnable() {
			@SuppressWarnings("unchecked")
			public void run() {
				if (adView == null) {
					callbackContext
							.error("AdView is null.  Did you call createBannerView?");
					return;
				} else {
					AdRequest request = new AdRequest.Builder().build();
					
					Bundle bundle = new Bundle();
					
					Iterator<String> extrasIterator = inputExtras.keys();
					boolean inputValid = true;
					while (extrasIterator.hasNext()) {
						String key = extrasIterator.next();
						try {
							bundle.putString(key,inputExtras.get(key).toString());							
						} catch (JSONException exception) {
							Log.w(LOGTAG, String.format(
									"Caught JSON Exception: %s",
									exception.getMessage()));
							callbackContext.error("Error grabbing extras");
							inputValid = false;
						}
					}
					if (inputValid) {
						// extras.addExtra("cordova", 1);
						// request.setNetworkExtras(extras);
						adView.loadAd(request);
						// Notify the plugin.
						callbackContext.success();
					}
				}
			}
		};
		this.cordova.getActivity().runOnUiThread(runnable);
	}
	
	private void executeKillAd(final CallbackContext callbackContext) {
	        final Runnable runnable = new Runnable() {
            		public void run() {
                		if (adView == null) {
                    		// Notify the plugin.
                    			callbackContext.error("AdView is null.  Did you call createBannerView or already destroy it?");
                		} else {
                    			LinearLayoutSoftKeyboardDetect parentView = (LinearLayoutSoftKeyboardDetect) webView
							.getParent();
		                    parentView.removeView(adView);
		                    adView.removeAllViews();
		                    adView.destroy();
		                    adView = null;
		                    callbackContext.success();
		                }
	            	}
	        };

        	this.cordova.getActivity().runOnUiThread(runnable);
	}

	

	@Override
	public void onDestroy() {
		if (adView != null) {
			adView.destroy();
			adView = null;
		}
		super.onDestroy();
	}

	/**
	 * Gets an AdSize object from the string size passed in from JavaScript.
	 * Returns null if an improper string is provided.
	 * 
	 * @param size
	 *            The string size representing an ad format constant.
	 * @return An AdSize object used to create a banner.
	 */
	public static AdSize adSizeFromSize(String size) {
		if ("BANNER".equals(size)) {
			return AdSize.BANNER;
		} else if ("IAB_MRECT".equals(size)) {
			return AdSize.MEDIUM_RECTANGLE;
		} else if ("IAB_BANNER".equals(size)) {
			return AdSize.FULL_BANNER;
		} else if ("IAB_LEADERBOARD".equals(size)) {
			return AdSize.LEADERBOARD;
		} else if ("SMART_BANNER".equals(size)) {
			return AdSize.SMART_BANNER;
		} else {
			return null;
		}
	}
}