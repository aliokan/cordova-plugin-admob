#import <Cordova/CDV.h>
#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

#import "GADBannerViewDelegate.h"
#import "GADInterstitialDelegate.h"

@class GADBannerView;
@class GADInterstitial;

#pragma mark AdMob Plugin

// This version of the AdMob plugin has been tested with Cordova version 3.0.0.
@interface AdMobPlugin : CDVPlugin <GADBannerViewDelegate, GADInterstitialDelegate> {
 @private
  // Value set by the javascript to indicate whether the ad is to be positioned
  // at the top or bottom of the screen.
  BOOL positionAdAtTop_;
}

@property(nonatomic, retain) GADBannerView *bannerView;
@property(nonatomic, retain) GADInterstitial *interstitial;

- (void)createBannerView:(CDVInvokedUrlCommand *)command;
- (void)createInterstitialView:(CDVInvokedUrlCommand *)command;
- (void)requestAd:(CDVInvokedUrlCommand *)command;
- (void)killAd:(CDVInvokedUrlCommand *)command;

@end
