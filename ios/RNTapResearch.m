#import "RNTapResearch.h"
#import <objc/runtime.h>


@implementation RNTapResearch
{
  BOOL hasListeners;
  BOOL receiveRewardCollection;
  NSMutableDictionary *placementsCache;
}

RCT_EXPORT_MODULE();
- (dispatch_queue_t)methodQueue
{
  return dispatch_get_main_queue();
}

#pragma mark - Exported Methods

RCT_EXPORT_METHOD(initWithApiToken:(NSString *)apiToken)
{
  [TapResearch initWithApiToken:apiToken developmentPlatform:PLATFORM developmentPlatformVersion:PLATFORM_VERSION rewardDelegate:self placementDelegate:self];
}

RCT_EXPORT_METHOD(setUniqueUserIdentifier:(NSString *)userIdentifier)
{
  [TapResearch setUniqueUserIdentifier:userIdentifier];
}

RCT_EXPORT_METHOD(setReceiveRewardCollection:(BOOL)enabled)
{
  [self _setReceiveRewardCollection:enabled];
}

RCT_EXPORT_METHOD(showSurveyWallWithParams:(NSDictionary *)placementDict :(NSDictionary *)params)
{
  [self showSurveyWallParams:placementDict :params];
}

RCT_EXPORT_METHOD(showSurveyWallParams:(NSDictionary *)placementDict :(NSDictionary *)params)
{
  if (!placementsCache) {
    NSLog(@"Init placement wasn't called there is no available placement");
    return;
  }
  TRPlacementCustomParameterList* placementCustomParameterList = [[TRPlacementCustomParameterList alloc] init];
  for (NSString* key in [params allKeys]) {
    NSString* value=[params valueForKey:key];
    TRPlacementCustomParameter *placementCustomParam = [TRPlacementCustomParameter new];
    [[[[placementCustomParam builder] key: key] value: value] build];

    [placementCustomParameterList addParameter:placementCustomParam];
  }
  NSString *placementIdentifier = [placementDict objectForKey:@"placementIdentifier"];
  TRPlacement *placement = [placementsCache objectForKey:placementIdentifier];
  if (!placement) {
    NSLog(@"Placement is missing make sure you are passing the right placement");
    return;
  }

  [placement showSurveyWallWithDelegate:self customParameters:placementCustomParameterList];
}

RCT_EXPORT_METHOD(showSurveyWall:(NSDictionary *)placementDict)
{
  if (!placementsCache) {
    NSLog(@"Init placement wasn't called there is no available placement");
    return;
  }
  NSString *placementIdentifier = [placementDict objectForKey:@"placementIdentifier"];
  TRPlacement *placement = [placementsCache objectForKey:placementIdentifier];
  if (!placement) {
    NSLog(@"Placement is missing make sure you are passing the right placement");
    return;
  }
  [placement showSurveyWallWithDelegate:self];
}

RCT_EXPORT_METHOD(displayEvent:(NSDictionary *)placementDict)
{
  if (!placementsCache) {
    NSLog(@"Init placement wasn't called there is no available placement");
    return;
  }
  NSString *placementIdentifier = [placementDict objectForKey:@"placementIdentifier"];
  TRPlacement *placement = [placementsCache objectForKey:placementIdentifier];
  if (!placement) {
    NSLog(@"Placement is missing make sure you are passing the right placement");
    return;
  }
  [placement displayEvent:placementIdentifier withDisplayDelegate:self surveyDelegate:self];
}


#pragma mark - TapResearchSurveyDelegate
- (void)tapResearchSurveyWallOpenedWithPlacement:(TRPlacement *)placement
{
  [self emitPlacement:placement eventName:@"tapResearchOnSurveyWallOpened"];
}

- (void)tapResearchSurveyWallDismissedWithPlacement:(TRPlacement *)placement
{
  [self emitPlacement:placement eventName:@"tapResearchOnSurveyWallDismissed"];
}

- (void)emitPlacement:(TRPlacement *)placement eventName:(NSString *)eventName
{
  NSMutableArray *events = [[NSMutableArray alloc] init];
  if (placement.events.count > 0) {
    for (TREvent *event in placement.events) {
      NSDictionary *eventDict = [TRSerializationHelper dictionaryWithPropertiesOfObject:event];
      [events addObject:eventDict];
    }
  }

  NSMutableDictionary *placementDict = [NSMutableDictionary dictionaryWithDictionary:[TRSerializationHelper dictionaryWithPropertiesOfObject:placement]];
  placementDict[@"events"] = events;

  placementDict[@"isEventAvailable"] = @(placement.events.count > 0);

  NSLog(@"Sending event %@", eventName);
  [self sendEventWithName:eventName body:placementDict];
}

#pragma mark - TapResearchPlacementDelegate


- (void)placementReady:(nonnull TRPlacement *)placement
{
  if (!placementsCache) {
    placementsCache = [[NSMutableDictionary alloc] init];
  }
  [placementsCache setObject:placement forKey:placement.placementIdentifier];
  [self emitPlacement:placement eventName:@"tapResearchOnPlacementReady"];
}

- (void)placementUnavailable:(nonnull NSString *)placementId
{
  if (!placementsCache) {
    placementsCache = [[NSMutableDictionary alloc] init];
  }
  [placementsCache removeObjectForKey:placementId];
  [self sendEventWithName:@"tapResearchOnPlacementUnavailable" body:placementId];
}

#pragma mark - TapResearchRewardsDelegate

- (void)tapResearchDidReceiveRewards:(NSArray<TRReward *> *)rewards {
  if(!hasListeners) return;

  NSArray *rewardArray = [self dictionaryWithPropertiesOfObject:rewards];

  if(receiveRewardCollection){
    [self sendEventWithName:@"tapResearchOnReceivedRewardCollection" body:rewardArray];
    return;
  }

  for (TRReward *reward in rewardArray) {
    [self sendEventWithName:@"tapResearchOnReceivedReward" body:reward];
  }
}

- (NSArray *) dictionaryWithPropertiesOfObject:(id)obj
{
  NSInteger arrayCount = [obj count];
  NSMutableArray *array = [NSMutableArray arrayWithCapacity:arrayCount];

  for (int i = 0; i < arrayCount;i++){
    NSDictionary *rewardDict = [TRSerializationHelper dictionaryWithPropertiesOfObject:obj[i]];
    [array addObject:rewardDict];
  }

  return array;
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

-(void)_setReceiveRewardCollection:(BOOL)enabled{
  receiveRewardCollection = enabled;
}

- (NSArray<NSString *> *)supportedEvents
{
  return @[
    @"tapResearchOnSurveyWallOpened",
    @"tapResearchOnSurveyWallDismissed",
    @"tapResearchOnReceivedReward",
    @"tapResearchOnReceivedRewardCollection",
    @"tapResearchOnPlacementUnavailable",
    @"tapResearchOnPlacementReady",
    // These are not implemented
    @"tapResearchOnEventDismissed",
    @"tapResearchOnEventOpened"
  ];
}

#pragma helpers

+ (UIColor *)colorFromHexString:(NSString *)hexString {
  unsigned rgbValue = 0;
  NSScanner *scanner = [NSScanner scannerWithString:hexString];
  [scanner setScanLocation:1];
  [scanner scanHexInt:&rgbValue];

  return [UIColor colorWithRed:((rgbValue & 0xFF0000) >> 16)/255.0 green:((rgbValue & 0xFF00) >> 8)/255.0 blue:(rgbValue & 0xFF)/255.0 alpha:1.0];
}

@end
