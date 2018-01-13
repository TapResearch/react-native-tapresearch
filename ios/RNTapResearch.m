#import "RNTapResearch.h"

@implementation RNTapResearch
{
  bool hasListeners;
}

+ (UIColor *)colorFromHexString:(NSString *)hexString {
    unsigned rgbValue = 0;
    NSScanner *scanner = [NSScanner scannerWithString:hexString];
    [scanner setScanLocation:1];
    [scanner scanHexInt:&rgbValue];

    return [UIColor colorWithRed:((rgbValue & 0xFF0000) >> 16)/255.0 green:((rgbValue & 0xFF00) >> 8)/255.0 blue:(rgbValue & 0xFF)/255.0 alpha:1.0];
}


RCT_EXPORT_MODULE();

- (dispatch_queue_t)methodQueue
{
  return dispatch_get_main_queue();
}

#pragma mark - Exported Methods

RCT_EXPORT_METHOD(initWithApiToken:(NSString *)apiToken)
{
  [TapResearch initWithApiToken:apiToken delegate:self];
}

RCT_EXPORT_METHOD(isSurveyAvailable:(RCTResponseSenderBlock)callback)
{
  BOOL available = [TapResearch isSurveyAvailable];
  NSNumber *num = [NSNumber numberWithBool:available];
  callback(@[num]);
}

RCT_EXPORT_METHOD(isSurveyAvailableForIdentifier:(NSString *)identifier callback:(RCTResponseSenderBlock)callback)
{
  BOOL available = [TapResearch isSurveyAvailableForIdentifier:identifier];
  NSNumber *num = [NSNumber numberWithBool:available];
  callback(@[num]);
}

RCT_EXPORT_METHOD(showSurvey)
{
  [TapResearch showSurveyWithDelegate:self];
}

RCT_EXPORT_METHOD(showSurveyWithIdentifier:(NSString *)identifier)
{
  [TapResearch showSurveyWithIdentifier:identifier delegate:self];
}

RCT_EXPORT_METHOD(setUniqueUserIdentifier:(NSString *)userIdentifier)
{
  [TapResearch setUniqueUserIdentifier:userIdentifier];
}

RCT_EXPORT_METHOD(setNavigationBarColor:(NSString *)hexColor)
{
  UIColor *color = [RNTapResearch colorFromHexString:hexColor];
  [TapResearch setNavigationBarColor:color];
}

RCT_EXPORT_METHOD(setNavigationBarText:(NSString *)text)
{
  [TapResearch setNavigationBarText:text];
}

RCT_EXPORT_METHOD(setNavigationBarTextColor:(NSString *)hexColor)
{
  UIColor *color = [RNTapResearch colorFromHexString:hexColor];
  [TapResearch setNavigationBarTextColor:color];
}

#pragma mark - TapResearchDelegate

- (void)tapResearchDidReceiveRewardWithQuantity:(NSInteger)quantity transactionIdentifier:(NSString *)transactionIdentifier currencyName:(NSString *)currencyName payoutEvent:(NSInteger)payoutEvent offerIdentifier:(NSString *)offerIdentifier
{
  if (hasListeners) {

      NSDictionary *body = @{
          @"quantity": @(quantity),
          @"transactionIdentifier": transactionIdentifier,
          @"currencyName": currencyName,
          @"payoutEvent": @(payoutEvent),
          @"offerIdentifier": offerIdentifier
      };
      [self sendEventWithName:@"tapResearchDidReceiveReward" body:body];
  }
}

- (void)tapResearchOnSurveyAvailable
{
  if (hasListeners) [self sendEventWithName:@"tapResearchOnSurveyAvailable" body:nil];
}

- (void)tapResearchOnSurveyAvailableWithPlacement:(NSString *)placement
{
  NSDictionary *body = @{@"placementIdentifier":placement};
  if (hasListeners) [self sendEventWithName:@"tapResearchOnSurveyAvailableWithPlacement" body:body];
}

- (void)tapResearchOnSurveyNotAvailable
{
  if (hasListeners) [self sendEventWithName:@"tapResearchOnSurveyNotAvailable" body:nil];
}

- (void)tapResearchOnSurveyNotAvailableWithPlacement:(NSString *)placement
{
  NSDictionary *body = @{@"placementIdentifier":placement};
  if (hasListeners) [self sendEventWithName:@"tapResearchOnSurveyNotAvailableWithPlacement" body:body];
}

#pragma mark - TapResearchSurveyDelegate

- (void)tapResearchSurveyModalOpened
{
  if (hasListeners) [self sendEventWithName:@"tapResearchSurveyModalOpened" body:nil];
}

- (void)tapResearchSurveyModalOpenedWithPlacement:(NSString *)placement
{
  NSDictionary *body = @{@"placementIdentifier":placement};
  if (hasListeners) [self sendEventWithName:@"tapResearchSurveyModalOpenedWithPlacement" body:body];
}

- (void)tapResearchSurveyModalDismissed
{
  if (hasListeners) [self sendEventWithName:@"tapResearchSurveyModalDismissed" body:nil];
}

- (void)tapResearchSurveyModalDismissedWithPlacement:(NSString *)placement
{
  NSDictionary *body = @{@"placementIdentifier":placement};
  if (hasListeners) [self sendEventWithName:@"tapResearchSurveyModalDismissedWithPlacement" body:body];
}

#pragma Lifecycle

-(void)startObserving
{
  hasListeners = YES;
}

-(void)stopObserving
{
  hasListeners = NO;
}

- (NSArray<NSString *> *)supportedEvents
{
  return @[
           @"tapResearchDidReceiveReward",
           @"tapResearchOnSurveyAvailable",
           @"tapResearchOnSurveyAvailableWithPlacement",
           @"tapResearchOnSurveyNotAvailable",
           @"tapResearchOnSurveyNotAvailableWithPlacement",
           @"tapResearchSurveyModalOpened",
           @"tapResearchSurveyModalOpenedWithPlacement",
           @"tapResearchSurveyModalDismissed",
           @"tapResearchSurveyModalDismissedWithPlacement"
           ];
}

@end
