/**
 * This class defines an AdMob object that is used to show ads natively in a
 * native iOS application.
 * @constructor
 */
var admob =  {
    /**
     * This enum represents AdMob's supported ad sizes.  Use one of these
     * constants as the adSize when calling createBannerView.
     * BANNER			320x50		Standard Banner			Phones and Tablets
     * IAB_MRECT 		300x250		IAB Medium Rectangle	Tablets
     * IAB_BANNER		468x60		IAB Full-Size Banner	Tablets
     * IAB_LEADERBOARD	728x90		IAB Leaderboard			Tablets
     * SMART_BANNER		url			Smart Banner			Phones and Tablets
     * https://developers.google.com/mobile-ads-sdk/docs/admob/smart-banners
     * 
     * @const
     */
    AD_SIZE : {
        BANNER: "BANNER",
        IAB_MRECT: "IAB_MRECT",
        IAB_BANNER: "IAB_BANNER",
        IAB_LEADERBOARD: "IAB_LEADERBOARD",
        SMART_BANNER: "SMART_BANNER"
    },
    
     /**
     * Creates a new AdMob banner view.
     *
     * @param {!Object} options The options used to create a banner.  They should
     *        be specified similar to the following.
     *
     *        {
     *          'publisherId': 'MY_PUBLISHER_ID',
     *          'adSize': AdMob.AD_SIZE.AD_SIZE_CONSTANT,
     *          'positionAtTop': false,
     *          'positionFromTop': 20,
     *        }
     *
     *        publisherId is the publisher ID from your AdMob site, adSize
     *        is one of the AdSize constants, and positionAtTop is a boolean to
     *        determine whether to create the banner above or below the app content.
     *        A publisher ID and AdSize are required.  The default for postionAtTop
     *        is false, meaning the banner would be shown below the app content.
     *        positionFromTop signifies the y coordinate of the banners top left corner. 
     *        positionFromTop takes precedence over positionAtTop.
     * @param {function()} successCallback The function to call if the banner was
     *         created successfully.
     * @param {function()} failureCallback The function to call if create banner
     *         was unsuccessful.
     */
    createBannerView : function (options, successCallback, failureCallback) {
        var defaults = {
            'publisherId': undefined,
            'adSize': undefined,
            'positionAtTop': false,
            'positionFromTop': -1
        };
        var requiredOptions = ['publisherId', 'adSize'];
        
        // Merge optional settings into defaults.
        for (var key in defaults) {
            if (typeof options[key] !== 'undefined') {
                defaults[key] = options[key];
            }
        }
        
        // Check for and merge required settings into defaults.
        requiredOptions.forEach(function(key) {
            if (typeof options[key] === 'undefined') {
                failureCallback('Failed to specify key: ' + key + '.');
                return;
            }
            defaults[key] = options[key];
        });
        
        cordova.exec(
            successCallback,
            failureCallback,
            'AdMobPlugin',
            'createBannerView',
            [{publisherId:defaults['publisherId'], adSize:defaults['adSize'], positionAtTop:defaults['positionAtTop'],
             positionFromTop:defaults['positionFromTop']}]
        );
    }, 
    
    /**
     * Creates a new AdMob interstitial view.
     * https://developers.google.com/mobile-ads-sdk/docs/admob/advanced
     * 
     * @param {!Object} options The options used to create a banner.  They should
     *        be specified similar to the following.
     *
     *        {
     *          'publisherId': 'MY_PUBLISHER_ID'
     *        }
     *
     *        publisherId is the publisher ID from your AdMob site, adSize
     *        is one of the AdSize constants, and positionAtTop is a boolean to
     *        determine whether to create the banner above or below the app content.
     *        A publisher ID and AdSize are required.  The default for postionAtTop
     *        is false, meaning the banner would be shown below the app content.
     * @param {function()} successCallback The function to call if the banner was
     *         created successfully.
     * @param {function()} failureCallback The function to call if create banner
     *         was unsuccessful.
     */
    createInterstitialView : function (options, successCallback, failureCallback) {
        var defaults = {
            'publisherId': undefined
        };
        var requiredOptions = ['publisherId'];
        
        // Merge optional settings into defaults.
        for (var key in defaults) {
            if (typeof options[key] !== 'undefined') {
                defaults[key] = options[key];
            }
        }
        
        // Check for and merge required settings into defaults.
        requiredOptions.forEach(function(key) {
            if (typeof options[key] === 'undefined') {
                failureCallback('Failed to specify key: ' + key + '.');
                return;
            }
            defaults[key] = options[key];
        });
        
        cordova.exec(
            successCallback,
            failureCallback,
            'AdMobPlugin',
            'createInterstitialView',
            [{publisherId:defaults['publisherId']}]
        );
    }, 
    
    
    /**
     * Request an AdMob ad.  This call should not be made until after the banner
     * view has been successfully created.
     *
     * @param {!Object} options The options used to request an ad.  They should
     *        be specified similar to the following.
     *
     *        {
     *          'isTesting': true|false,
     *          'extras': {
     *            'key': 'value'
     *          }
     *        }
     *
     *        isTesting is a boolean determining whether or not to request a
     *        test ad on an emulator, and extras represents the extras to pass
     *        into the request. If no options are passed, the request will have
     *        testing set to false and an empty extras.
     * @param {function()} successCallback The function to call if an ad was
     *        requested successfully.
     * @param {function()} failureCallback The function to call if an ad failed
     *        to be requested.
     */
    requestAd : function(options, successCallback, failureCallback) {
        var defaults = {
            'isTesting': false,
            'extras': {}
        };
        
        for (var key in defaults) {
            if (typeof options[key] !== 'undefined') {
                defaults[key] = options[key];
            }
        }
        
        cordova.exec(
            successCallback,
            failureCallback,
            'AdMobPlugin',
            'requestAd',
            [{isTesting:defaults['isTesting'], extras:defaults['extras']}]
        );
    }, 
    
    
    /**
     * kill banner.
     * @param {function()} successCallback The function to call if an ad was
     *        requested successfully.
     * @param {function()} failureCallback The function to call if an ad failed
     *        to be requested.
     */
    killAd : function(successCallback, failureCallback) {
        cordova.exec(
            successCallback,
            failureCallback,
            'AdMobPlugin',
                     'killAd',[{}]);
    }
    
};

module.exports = admob;
