#import "RNTapResearch.h"

@implementation RNTapResearch
{
  bool hasListeners;
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

//RCT_EXPORT_METHOD(isSurveyAvailableForIdentifier:(NSString *)identifier callback:(RCTResponseSenderBlock)callback)
//{
//  BOOL available = [TapResearch isSurveyAvailableForIdentifier:identifier];
//  NSNumber *num = [NSNumber numberWithBool:available];
//  callback(@[num]);
//}

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

#pragma mark - TapResearchDelegate

- (void)tapResearchDidReceiveRewardWithQuantity:(NSInteger)quantity transactionIdentifier:(NSString *)transactionIdentifier currencyName:(NSString *)currencyName payoutEvent:(NSInteger)payoutEvent offerIdentifier:(NSString *)offerIdentifier
{
  if (hasListeners) {
    
      NSDictionary *body = @{
          @"quantity": @(quantity),
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

- (void)tapResearchOnSurveyNotAvailable
{
  if (hasListeners) [self sendEventWithName:@"tapResearchOnSurveyNotAvailable" body:nil];
}

#pragma mark - TapResearchSurveyDelegate

- (void)tapResearchSurveyModalOpened
{
  if (hasListeners) [self sendEventWithName:@"tapResearchSurveyModalOpened" body:nil];
}

- (void)tapResearchSurveyModalDismissed
{
  if (hasListeners) [self sendEventWithName:@"tapResearchSurveyModalDismissed" body:nil];
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
           @"tapResearchOnSurveyNotAvailable",
           @"tapResearchSurveyModalOpened",
           @"tapResearchSurveyModalDismissed"
           ];
}

@end
