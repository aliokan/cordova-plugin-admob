cordova-plugin-admob
====================

AdMob Phonegap Plugin for Android and iOS
compatible Phonegap 3.0 CLI

What is phonegap CLI ?  
http://docs.phonegap.com/en/edge/guide_cli_index.md.html#The%20Command-line%20Interface  
How to install plugin whith phonegap CLI : 
> phonegap local plugin add https://github.com/aliokan/cordova-plugin-admob

##Implementation:

There are two calls needed to get AdMob Ads:

1. `createBannerView`

   Takes in a object containing a publisherId and adSize, as well as success
   and failure callbacks.  An example call is provided below:

         admob.createBannerView(
             {
               'publisherId': 'INSERT_YOUR_PUBLISHER_ID_HERE',
               'adSize': admob.AD_SIZE.BANNER
             },
             successCallback,
             failureCallback
         );

2. `requestAd`

   Takes in an object containing an optional testing flag, and an optional
   list of extras.  This method should only be invoked once createBannerView
   has invoked successCallback.  An example call is provided below:

         admob.requestAd(
             {
               'isTesting': false,
               'extras': {
                 'color_bg': 'AAAAFF',
                 'color_bg_top': 'FFFFFF',
                 'color_border': 'FFFFFF',
                 'color_link': '000080',
                 'color_text': '808080',
                 'color_url': '008000'
               },
             },
             successCallback,
             failureCallback
         );


This plugin also allows you the option to listen for ad events.  The following
events are supported:

         document.addEventListener('onReceiveAd', callback);
         document.addEventListener('onFailedToReceiveAd', callback);
         document.addEventListener('onDismissScreen', callback);
         document.addEventListener('onPresentScreen', callback);
         document.addEventListener('onLeaveApplication', callback);

If you want to create an Interstitials sur `createInterstitialView`  

         admob.createInterstitialView(
         {
           'publisherId': 'INSERT_YOUR_PUBLISHER_ID_HERE'
         },
         successCallback,
         failureCallback);

If you want Kill banner :  

         admob.killAd(successCallback,failureCallback);
         
Based on:  
https://github.com/rajpara11/phonegap-plugins/tree/master/Android/AdMobPlugin  
https://github.com/rajpara11/phonegap-plugins/tree/master/iOS/AdMobPlugin  
with Interstitials support
